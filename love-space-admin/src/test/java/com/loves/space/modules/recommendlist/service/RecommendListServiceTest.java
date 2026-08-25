package com.loves.space.modules.recommendlist.service;

import com.loves.space.infrastructure.storage.ImageUrlSigner;
import com.loves.space.modules.city.entity.City;
import com.loves.space.modules.city.repository.CityRepository;
import com.loves.space.modules.merchant.entity.Merchant;
import com.loves.space.modules.merchant.repository.MerchantRepository;
import com.loves.space.modules.recommendlist.dto.RecommendListCreateRequest;
import com.loves.space.modules.recommendlist.dto.RecommendListDetailResponse;
import com.loves.space.modules.recommendlist.dto.RecommendListMerchantResponse;
import com.loves.space.modules.recommendlist.dto.RecommendListUpdateRequest;
import com.loves.space.modules.recommendlist.repository.RecommendListMerchantRepository;
import com.loves.space.modules.recommendlist.repository.RecommendListRepository;
import com.loves.space.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * {@link RecommendListService} 集成测试：清单 CRUD、merchantIds 整体替换的同城/去重/未下架校验、城市变更、人工恢复、删除级联。
 */
class RecommendListServiceTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private RecommendListService recommendListService;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private RecommendListMerchantRepository recommendListMerchantRepository;

    @MockitoBean
    private ImageUrlSigner imageUrlSigner;

    @BeforeEach
    void stubSigner() {
        when(imageUrlSigner.sign(anyString()))
                .thenAnswer(inv -> "https://signed.example.com/" + inv.getArgument(0));
    }

    private UUID cityId() {
        City city = new City();
        city.setChineseName("清单城-" + UUID.randomUUID());
        city.setEnglishName("list-city");
        city.setChineseProvince("省");
        city.setEnglishProvince("Province");
        city.setOnline(true);
        return cityRepository.save(city).getId();
    }

    private UUID merchantIn(UUID cityId) {
        return merchantIn(cityId, true);
    }

    private UUID merchantIn(UUID cityId, boolean online) {
        Merchant merchant = new Merchant();
        merchant.setName("清单商户");
        merchant.setLogo("bound/logo.png");
        merchant.setAddress("地址");
        merchant.setCityId(cityId);
        merchant.setSafetyEnvironmentScore((short) 20);
        merchant.setBusinessRightsScore((short) 15);
        merchant.setExperienceFriendlyScore((short) 15);
        merchant.setSocialContributionScore((short) 10);
        merchant.setImages(new java.util.ArrayList<>(List.of("bound/a.png")));
        merchant.setOnline(online);
        return merchantRepository.save(merchant).getId();
    }

    /** 仅替换 merchantIds 的更新请求（title 必填沿用原标题）。 */
    private static RecommendListUpdateRequest merchants(String title, List<UUID> merchantIds) {
        return new RecommendListUpdateRequest(title, null, null, 0, null, merchantIds);
    }

    // @scenario: recommend-list/推荐清单管理#创建清单
    @Test
    void createReturnsAllFields() {
        UUID cityId = cityId();
        RecommendListDetailResponse detail = recommendListService.create(
                new RecommendListCreateRequest("周末探店", "介绍", cityId, 3, null, null));
        assertThat(detail.id()).isNotNull();
        assertThat(detail.title()).isEqualTo("周末探店");
        assertThat(detail.introduction()).isEqualTo("介绍");
        assertThat(detail.cityId()).isEqualTo(cityId);
        assertThat(detail.sortOrder()).isEqualTo(3);
        assertThat(detail.merchants()).isEmpty();
    }

    // @scenario: recommend-list/推荐清单管理#缺少必填项被拒绝
    @Test
    void createRejectsMissingCity() {
        assertThatThrownBy(() -> recommendListService.create(
                new RecommendListCreateRequest("标题", null, UUID.randomUUID(), null, null, null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("所属城市不存在");
    }

    // @scenario: recommend-list/清单内商户维护#添加本城市商户
    @Test
    void updateMerchantIdsKeepsArrayOrder() {
        UUID cityId = cityId();
        UUID m1 = merchantIn(cityId);
        UUID m2 = merchantIn(cityId);
        UUID listId = recommendListService.create(
                new RecommendListCreateRequest("排序清单", null, cityId, 0, null, null)).id();

        RecommendListDetailResponse detail = recommendListService.update(listId,
                merchants("排序清单", List.of(m2, m1)));

        assertThat(detail.merchants()).extracting(RecommendListMerchantResponse::merchantId)
                .containsExactly(m2, m1);
    }

    // @scenario: recommend-list/清单内商户维护#拒绝跨城市商户
    @Test
    void updateRejectsCrossCityMerchant() {
        UUID cityId = cityId();
        UUID otherCityMerchant = merchantIn(cityId());
        UUID listId = recommendListService.create(
                new RecommendListCreateRequest("同城清单", null, cityId, 0, null, null)).id();

        assertThatThrownBy(() -> recommendListService.update(listId,
                merchants("同城清单", List.of(otherCityMerchant))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("不属于清单所属城市");
        assertThat(recommendListService.detail(listId).merchants()).isEmpty();
    }

    // @scenario: recommend-list/清单内商户维护#重复添加同一商户被拒绝
    @Test
    void updateRejectsDuplicateMerchant() {
        UUID cityId = cityId();
        UUID merchantId = merchantIn(cityId);
        UUID listId = recommendListService.create(
                new RecommendListCreateRequest("去重清单", null, cityId, 0, null, null)).id();

        assertThatThrownBy(() -> recommendListService.update(listId,
                merchants("去重清单", List.of(merchantId, merchantId))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("重复");
        assertThat(recommendListService.detail(listId).merchants()).isEmpty();
    }

    // @scenario: recommend-list/清单内商户维护#拒绝已下架商户
    @Test
    void updateRejectsOfflineMerchant() {
        UUID cityId = cityId();
        UUID offline = merchantIn(cityId, false);
        UUID listId = recommendListService.create(
                new RecommendListCreateRequest("下架清单", null, cityId, 0, null, null)).id();

        assertThatThrownBy(() -> recommendListService.update(listId,
                merchants("下架清单", List.of(offline))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("已下架");
        assertThat(recommendListService.detail(listId).merchants()).isEmpty();
    }

    // @scenario: recommend-list/推荐清单管理#修改所属城市需清单内商户同属新城市
    @Test
    void updateCityRequiresMerchantsInNewCity() {
        UUID cityA = cityId();
        UUID cityB = cityId();
        UUID m1 = merchantIn(cityA);
        UUID withMerchant = recommendListService.create(
                new RecommendListCreateRequest("含商户", null, cityA, 0, null, List.of(m1))).id();
        UUID empty = recommendListService.create(
                new RecommendListCreateRequest("空清单", null, cityA, 0, null, null)).id();

        assertThatThrownBy(() -> recommendListService.update(withMerchant,
                new RecommendListUpdateRequest("含商户", null, cityB, 0, null, null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("不属于新城市");
        assertThat(recommendListService.detail(withMerchant).cityId()).isEqualTo(cityA);

        RecommendListDetailResponse moved = recommendListService.update(empty,
                new RecommendListUpdateRequest("空清单", null, cityB, 0, null, null));
        assertThat(moved.cityId()).isEqualTo(cityB);
    }

    // @scenario: recommend-list/推荐清单管理#人工恢复清单
    @Test
    void onlineRequiresNoOfflineMerchant() {
        UUID cityId = cityId();
        UUID merchantId = merchantIn(cityId);
        UUID listId = recommendListService.create(
                new RecommendListCreateRequest("待恢复", null, cityId, 0, "OFFLINE", List.of(merchantId))).id();

        Merchant merchant = merchantRepository.findById(merchantId).orElseThrow();
        merchant.setOnline(false);
        merchantRepository.save(merchant);
        assertThatThrownBy(() -> recommendListService.online(listId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("未上架商户");
        assertThat(recommendListService.detail(listId).status()).isEqualTo("OFFLINE");

        merchant.setOnline(true);
        merchantRepository.save(merchant);
        assertThat(recommendListService.online(listId).status()).isEqualTo("ONLINE");
        assertThat(recommendListService.online(listId).status()).isEqualTo("ONLINE");
    }

    // @scenario: recommend-list/推荐清单管理#清单列表按排序号升序
    @Test
    void pageOrdersBySortOrderAscending() {
        UUID cityId = cityId();
        recommendListService.create(new RecommendListCreateRequest("清单五", null, cityId, 5, null, null));
        recommendListService.create(new RecommendListCreateRequest("清单一", null, cityId, 1, null, null));
        recommendListService.create(new RecommendListCreateRequest("清单三", null, cityId, 3, null, null));

        assertThat(recommendListService.page(cityId, null, org.springframework.data.domain.PageRequest.of(0, 10))
                .content())
                .extracting(item -> item.sortOrder())
                .containsExactly(1, 3, 5);
    }

    // @scenario: recommend-list/清单内商户维护#从清单移除商户
    @Test
    void updateRemovesUnlistedWithoutDeletingMerchant() {
        UUID cityId = cityId();
        UUID m1 = merchantIn(cityId);
        UUID m2 = merchantIn(cityId);
        UUID listId = recommendListService.create(
                new RecommendListCreateRequest("移除清单", null, cityId, 0, null, List.of(m1, m2))).id();

        RecommendListDetailResponse detail = recommendListService.update(listId,
                merchants("移除清单", List.of(m2)));

        assertThat(detail.merchants()).extracting(RecommendListMerchantResponse::merchantId)
                .containsExactly(m2);
        assertThat(merchantRepository.existsById(m1)).isTrue();
    }

    // @scenario: recommend-list/推荐清单管理#删除清单
    @Test
    void deleteRemovesListAndRelationsButKeepsMerchant() {
        UUID cityId = cityId();
        UUID merchantId = merchantIn(cityId);
        UUID listId = recommendListService.create(
                new RecommendListCreateRequest("待删清单", null, cityId, 0, null, List.of(merchantId))).id();

        recommendListService.delete(listId);

        assertThatThrownBy(() -> recommendListService.detail(listId))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(recommendListMerchantRepository.findAllByRecommendListIdOrderBySortOrderAsc(listId)).isEmpty();
        assertThat(merchantRepository.existsById(merchantId)).isTrue();
    }
}
