package com.loves.space.modules.merchant.service;

import com.loves.space.common.exception.ValidationException;
import com.loves.space.modules.merchant.dto.MerchantDetailResponse;
import com.loves.space.modules.merchant.dto.MerchantUpsertRequest;
import com.loves.space.modules.merchant.repository.MerchantRepository;
import com.loves.space.modules.merchant.repository.MerchantReviewRepository;
import com.loves.space.modules.merchant.repository.MerchantTagRepository;
import com.loves.space.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link MerchantService} 集成测试：covers upsert 校验、子表替换、按分类批量下架。
 */
class MerchantServiceTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private MerchantService merchantService;
    @Autowired
    private MerchantRepository merchantRepository;
    @Autowired
    private MerchantImageRepository merchantImageRepository;
    @Autowired
    private MerchantPeriodRepository merchantPeriodRepository;
    @Autowired
    private MerchantTagRepository merchantTagRepository;
    @Autowired
    private MerchantReviewRepository merchantReviewRepository;

    /** 构造合法 upsert 请求；name/score/story/images 可在调用前替换。 */
    private MerchantUpsertRequest validRequest() {
        return new MerchantUpsertRequest(
                "测试商户",
                "https://example.com/logo.png",
                "测试地址",
                null,
                null,
                UUID.randomUUID(),
                UUID.randomUUID(),
                (short) 20,
                (short) 15,
                (short) 15,
                (short) 10,
                "故事",
                10,
                true,
                List.of(),
                List.of(),
                List.of(new MerchantUpsertRequest.ImageItem("https://example.com/a.png", 0)),
                List.of()
        );
    }

    @Test
    void upsertRejectsNameLongerThan15CodePoints() {
        MerchantUpsertRequest base = validRequest();
        String tooLong = "字".repeat(16);
        MerchantUpsertRequest bad = new MerchantUpsertRequest(
                tooLong, base.logo(), base.address(), null, null, base.cityId(), base.categoryId(),
                base.safetyEnvironmentScore(), base.businessRightsScore(),
                base.experienceFriendlyScore(), base.socialContributionScore(),
                base.story(), base.weight(), base.online(),
                base.recommendedPeriods(), base.tagIds(), base.images(), base.reviews());

        assertThatThrownBy(() -> merchantService.upsert(null, bad))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void upsertRejectsSafetyScoreAboveMax() {
        MerchantUpsertRequest base = validRequest();
        MerchantUpsertRequest bad = new MerchantUpsertRequest(
                base.name(), base.logo(), base.address(), null, null, base.cityId(), base.categoryId(),
                (short) 31, base.businessRightsScore(),
                base.experienceFriendlyScore(), base.socialContributionScore(),
                base.story(), base.weight(), base.online(),
                base.recommendedPeriods(), base.tagIds(), base.images(), base.reviews());

        assertThatThrownBy(() -> merchantService.upsert(null, bad))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void upsertRejectsEmptyImages() {
        MerchantUpsertRequest base = validRequest();
        MerchantUpsertRequest bad = new MerchantUpsertRequest(
                base.name(), base.logo(), base.address(), null, null, base.cityId(), base.categoryId(),
                base.safetyEnvironmentScore(), base.businessRightsScore(),
                base.experienceFriendlyScore(), base.socialContributionScore(),
                base.story(), base.weight(), base.online(),
                base.recommendedPeriods(), base.tagIds(), List.of(), base.reviews());

        assertThatThrownBy(() -> merchantService.upsert(null, bad))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void upsertRejectsStoryLongerThan5000CodePoints() {
        MerchantUpsertRequest base = validRequest();
        String tooLong = "字".repeat(5001);
        MerchantUpsertRequest bad = new MerchantUpsertRequest(
                base.name(), base.logo(), base.address(), null, null, base.cityId(), base.categoryId(),
                base.safetyEnvironmentScore(), base.businessRightsScore(),
                base.experienceFriendlyScore(), base.socialContributionScore(),
                tooLong, base.weight(), base.online(),
                base.recommendedPeriods(), base.tagIds(), base.images(), base.reviews());

        assertThatThrownBy(() -> merchantService.upsert(null, bad))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void upsertHappyPathPersistsChildren() {
        UUID cityId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        UUID tagId = UUID.randomUUID();
        MerchantUpsertRequest request = new MerchantUpsertRequest(
                "正常商户",
                "https://example.com/logo.png",
                "上海市浦东新区",
                null, null, cityId, categoryId,
                (short) 25, (short) 20, (short) 20, (short) 15,
                "故事内容", 100, true,
                List.of(com.loves.space.common.enums.Period.MENSTRUAL,
                        com.loves.space.common.enums.Period.LUTEAL),
                List.of(tagId),
                List.of(
                        new MerchantUpsertRequest.ImageItem("https://example.com/1.png", 0),
                        new MerchantUpsertRequest.ImageItem("https://example.com/2.png", 1)),
                List.of(new com.loves.space.modules.merchant.dto.ReviewUpsertItem(
                        "user", "title", "content", 0))
        );

        MerchantDetailResponse detail = merchantService.upsert(null, request);

        assertThat(detail.id()).isNotNull();
        assertThat(merchantImageRepository.findAllByMerchantIdOrderBySortOrderAsc(detail.id())).hasSize(2);
        assertThat(merchantPeriodRepository.findAllByMerchantId(detail.id())).hasSize(2);
        assertThat(merchantTagRepository.findAllByMerchantId(detail.id())).hasSize(1);
        assertThat(merchantReviewRepository.findAllByMerchantIdOrderBySortOrderAsc(detail.id())).hasSize(1);
    }

    @Test
    void offlineByCategoryIdFlipsAllOnlineMerchants() {
        UUID categoryId = UUID.randomUUID();
        UUID cityId = UUID.randomUUID();
        MerchantUpsertRequest base = validRequest();
        MerchantUpsertRequest a = new MerchantUpsertRequest(
                "甲", base.logo(), base.address(), null, null, cityId, categoryId,
                base.safetyEnvironmentScore(), base.businessRightsScore(),
                base.experienceFriendlyScore(), base.socialContributionScore(),
                null, 0, true, List.of(), List.of(),
                List.of(new MerchantUpsertRequest.ImageItem("https://example.com/a.png", 0)),
                List.of());
        MerchantUpsertRequest b = new MerchantUpsertRequest(
                "乙", base.logo(), base.address(), null, null, cityId, categoryId,
                base.safetyEnvironmentScore(), base.businessRightsScore(),
                base.experienceFriendlyScore(), base.socialContributionScore(),
                null, 0, true, List.of(), List.of(),
                List.of(new MerchantUpsertRequest.ImageItem("https://example.com/b.png", 0)),
                List.of());

        MerchantDetailResponse ma = merchantService.upsert(null, a);
        MerchantDetailResponse mb = merchantService.upsert(null, b);

        merchantService.offlineByCategoryId(categoryId);

        assertThat(merchantRepository.findById(ma.id()).orElseThrow().isOnline()).isFalse();
        assertThat(merchantRepository.findById(mb.id()).orElseThrow().isOnline()).isFalse();
    }
}
