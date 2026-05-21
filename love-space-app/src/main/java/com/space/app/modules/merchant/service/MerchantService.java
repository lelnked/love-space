package com.space.app.modules.merchant.service;

import com.space.app.common.enums.Period;
import com.space.app.common.exception.ResourceNotFoundException;
import com.space.app.common.page.PageQuery;
import com.space.app.modules.merchant.dto.MerchantDetailResponse;
import com.space.app.modules.merchant.dto.MerchantListItemResponse;
import com.space.app.modules.merchant.dto.ReviewItemResponse;
import com.space.app.modules.merchant.dto.TagItemResponse;
import com.space.app.modules.merchant.entity.Merchant;
import com.space.app.modules.merchant.entity.MerchantPeriod;
import com.space.app.modules.merchant.entity.MerchantTag;
import com.space.app.modules.merchant.repository.MerchantImageRepository;
import com.space.app.modules.merchant.repository.MerchantPeriodRepository;
import com.space.app.modules.merchant.repository.MerchantRepository;
import com.space.app.modules.merchant.repository.MerchantReviewRepository;
import com.space.app.modules.merchant.repository.MerchantTagRepository;
import com.space.app.modules.tag.entity.Tag;
import com.space.app.modules.tag.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 商户服务：App 端只读。
 * <ul>
 *   <li>列表分页：cityId 必填，period / categoryId 可选；排序 weight DESC, createdAt DESC；</li>
 *   <li>详情拼装：图片、上架标签、四维百分制、爱女指数、评价、故事；下架商户 404。</li>
 * </ul>
 */
@Service
@Transactional(readOnly = true)
public class MerchantService {

    private final MerchantRepository merchantRepository;
    private final MerchantImageRepository merchantImageRepository;
    private final MerchantPeriodRepository merchantPeriodRepository;
    private final MerchantTagRepository merchantTagRepository;
    private final MerchantReviewRepository merchantReviewRepository;
    private final TagRepository tagRepository;
    private final ScoreCalculator scoreCalculator;

    public MerchantService(MerchantRepository merchantRepository,
                           MerchantImageRepository merchantImageRepository,
                           MerchantPeriodRepository merchantPeriodRepository,
                           MerchantTagRepository merchantTagRepository,
                           MerchantReviewRepository merchantReviewRepository,
                           TagRepository tagRepository,
                           ScoreCalculator scoreCalculator) {
        this.merchantRepository = merchantRepository;
        this.merchantImageRepository = merchantImageRepository;
        this.merchantPeriodRepository = merchantPeriodRepository;
        this.merchantTagRepository = merchantTagRepository;
        this.merchantReviewRepository = merchantReviewRepository;
        this.tagRepository = tagRepository;
        this.scoreCalculator = scoreCalculator;
    }

    /** 商户列表分页查询。 */
    public Page<MerchantListItemResponse> search(UUID cityId, Period period, UUID categoryId, PageQuery pageQuery) {
        Pageable pageable = pageQuery.toPageable(Sort.by(Sort.Order.desc("weight"), Sort.Order.desc("createdAt")));
        Page<Merchant> page = merchantRepository.searchOnline(cityId, period, categoryId, pageable);

        // 批量取标签关联与上架标签，避免 N+1
        List<UUID> merchantIds = page.getContent().stream().map(Merchant::getId).toList();
        List<MerchantTag> allRelations = merchantIds.isEmpty() ? List.of()
                : merchantTagRepository.findAllByMerchantIdIn(merchantIds);
        List<UUID> tagIds = allRelations.stream().map(MerchantTag::getTagId).distinct().toList();
        Map<UUID, Tag> onlineTagsById = tagIds.isEmpty() ? Map.of()
                : tagRepository.findByIdInAndOnlineTrue(tagIds).stream()
                .collect(Collectors.toMap(Tag::getId, t -> t));
        Map<UUID, List<TagItemResponse>> tagsByMerchant = allRelations.stream()
                .filter(r -> onlineTagsById.containsKey(r.getTagId()))
                .collect(Collectors.groupingBy(
                        MerchantTag::getMerchantId,
                        Collectors.mapping(
                                r -> {
                                    Tag t = onlineTagsById.get(r.getTagId());
                                    return new TagItemResponse(t.getId(), t.getName());
                                },
                                Collectors.toList())));

        return page.map(m -> new MerchantListItemResponse(
                m.getId(),
                m.getName(),
                m.getLogo(),
                m.getAddress(),
                tagsByMerchant.getOrDefault(m.getId(), List.of()),
                scoreCalculator.toScoreView(
                        m.getSafetyEnvironmentScore(),
                        m.getBusinessRightsScore(),
                        m.getExperienceFriendlyScore(),
                        m.getSocialContributionScore()),
                scoreCalculator.toLoveIndex(
                        m.getSafetyEnvironmentScore(),
                        m.getBusinessRightsScore(),
                        m.getExperienceFriendlyScore(),
                        m.getSocialContributionScore())));
    }

    /** 商户详情；下架或不存在抛 404。 */
    public MerchantDetailResponse detail(UUID id) {
        Merchant merchant = merchantRepository.findByIdAndOnlineTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("merchant not found: " + id));

        List<String> images = merchantImageRepository.findAllByMerchantIdOrderBySortOrderAsc(id).stream()
                .map(img -> img.getUrl())
                .toList();

        List<Period> periods = merchantPeriodRepository.findAllByMerchantId(id).stream()
                .map(MerchantPeriod::getPeriod)
                .sorted(Comparator.comparingInt(Enum::ordinal))
                .toList();

        List<UUID> relatedTagIds = merchantTagRepository.findAllByMerchantId(id).stream()
                .map(MerchantTag::getTagId)
                .toList();
        List<TagItemResponse> tags = relatedTagIds.isEmpty() ? List.of()
                : tagRepository.findByIdInAndOnlineTrue(relatedTagIds).stream()
                .map(t -> new TagItemResponse(t.getId(), t.getName()))
                .toList();

        List<ReviewItemResponse> reviews = merchantReviewRepository.findAllByMerchantIdOrderBySortOrderAsc(id).stream()
                .map(r -> new ReviewItemResponse(r.getNickname(), r.getTitle(), r.getContent()))
                .toList();

        return new MerchantDetailResponse(
                merchant.getId(),
                merchant.getName(),
                merchant.getLogo(),
                images,
                merchant.getAddress(),
                merchant.getLongitude(),
                merchant.getLatitude(),
                periods,
                tags,
                scoreCalculator.toScoreView(
                        merchant.getSafetyEnvironmentScore(),
                        merchant.getBusinessRightsScore(),
                        merchant.getExperienceFriendlyScore(),
                        merchant.getSocialContributionScore()),
                scoreCalculator.toLoveIndex(
                        merchant.getSafetyEnvironmentScore(),
                        merchant.getBusinessRightsScore(),
                        merchant.getExperienceFriendlyScore(),
                        merchant.getSocialContributionScore()),
                reviews,
                Objects.toString(merchant.getStory(), null));
    }
}
