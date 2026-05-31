package com.space.app.modules.merchant.service;

import com.space.app.common.enums.Period;
import com.space.app.common.exception.ResourceNotFoundException;
import com.space.app.common.page.PageQuery;
import com.space.app.common.util.ImageResponses;
import com.space.app.infrastructure.storage.ImageUrlSigner;
import com.space.app.modules.merchant.dto.MerchantDetailResponse;
import com.space.app.modules.merchant.dto.MerchantListItemResponse;
import com.space.app.modules.merchant.dto.ReviewItemResponse;
import com.space.app.modules.merchant.dto.TagItemResponse;
import com.space.app.modules.merchant.entity.Merchant;
import com.space.app.modules.merchant.entity.MerchantTag;
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
    private final MerchantTagRepository merchantTagRepository;
    private final MerchantReviewRepository merchantReviewRepository;
    private final TagRepository tagRepository;
    private final ScoreCalculator scoreCalculator;
    private final ImageUrlSigner imageUrlSigner;

    public MerchantService(MerchantRepository merchantRepository,
                           MerchantTagRepository merchantTagRepository,
                           MerchantReviewRepository merchantReviewRepository,
                           TagRepository tagRepository,
                           ScoreCalculator scoreCalculator,
                           ImageUrlSigner imageUrlSigner) {
        this.merchantRepository = merchantRepository;
        this.merchantTagRepository = merchantTagRepository;
        this.merchantReviewRepository = merchantReviewRepository;
        this.tagRepository = tagRepository;
        this.scoreCalculator = scoreCalculator;
        this.imageUrlSigner = imageUrlSigner;
    }

    /** 商户列表分页查询。 */
    public Page<MerchantListItemResponse> page(UUID cityId, Period period, UUID categoryId, PageQuery pageQuery) {
        // 排序在 native SQL 中硬编码（weight DESC, created_at DESC），此处传入未排序 Pageable
        Pageable pageable = pageQuery.toPageable(Sort.unsorted());
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
                ImageResponses.from(m.getLogo(), imageUrlSigner),
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

        List<String> images = List.copyOf(merchant.getImages());

        List<Period> periods = merchant.getPeriods().stream()
                .map(Period::valueOf)
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
                ImageResponses.from(merchant.getLogo(), imageUrlSigner),
                ImageResponses.fromList(images, imageUrlSigner),
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
