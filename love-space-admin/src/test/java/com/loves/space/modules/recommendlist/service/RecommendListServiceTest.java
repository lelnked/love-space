package com.loves.space.modules.recommendlist.service;

import com.loves.space.infrastructure.storage.ImageUrlSigner;
import com.loves.space.modules.city.entity.City;
import com.loves.space.modules.city.repository.CityRepository;
import com.loves.space.modules.merchant.entity.Merchant;
import com.loves.space.modules.merchant.repository.MerchantRepository;
import com.loves.space.modules.recommendlist.dto.RecommendListCreateRequest;
import com.loves.space.modules.recommendlist.dto.RecommendListDetailResponse;
import com.loves.space.modules.recommendlist.dto.RecommendListMerchantItemRequest;
import com.loves.space.modules.recommendlist.dto.RecommendListMerchantResponse;
import com.loves.space.modules.recommendlist.repository.RecommendListMerchantRepository;
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
 * {@link RecommendListService} 集成测试：清单 CRUD、商户全量替换的同城/去重校验、删除级联。
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
        merchant.setOnline(true);
        return merchantRepository.save(merchant).getId();
    }

    // @scenario: recommend-list/推荐清单管理#创建清单
    @Test
    void createReturnsAllFields() {
        UUID cityId = cityId();
        RecommendListDetailResponse detail = recommendListService.create(
                new RecommendListCreateRequest("周末探店", "介绍", cityId, 3));
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
                new RecommendListCreateRequest("标题", null, UUID.randomUUID(), null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("所属城市不存在");
    }

    // @scenario: recommend-list/清单内商户维护#添加本城市商户
    @Test
    void replaceMerchantsOrdersBySortOrder() {
        UUID cityId = cityId();
        UUID m1 = merchantIn(cityId);
        UUID m2 = merchantIn(cityId);
        UUID listId = recommendListService.create(
                new RecommendListCreateRequest("排序清单", null, cityId, 0)).id();

        RecommendListDetailResponse detail = recommendListService.replaceMerchants(listId, List.of(
                new RecommendListMerchantItemRequest(m1, 5),
                new RecommendListMerchantItemRequest(m2, 1)));

        assertThat(detail.merchants()).extracting(RecommendListMerchantResponse::merchantId)
                .containsExactly(m2, m1);
    }

    // @scenario: recommend-list/清单内商户维护#拒绝跨城市商户
    @Test
    void replaceMerchantsRejectsCrossCityMerchant() {
        UUID cityId = cityId();
        UUID otherCityMerchant = merchantIn(cityId());
        UUID listId = recommendListService.create(
                new RecommendListCreateRequest("同城清单", null, cityId, 0)).id();

        assertThatThrownBy(() -> recommendListService.replaceMerchants(listId,
                List.of(new RecommendListMerchantItemRequest(otherCityMerchant, 0))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("不属于清单所属城市");
    }

    // @scenario: recommend-list/清单内商户维护#重复添加同一商户被拒绝
    @Test
    void replaceMerchantsRejectsDuplicateMerchant() {
        UUID cityId = cityId();
        UUID merchantId = merchantIn(cityId);
        UUID listId = recommendListService.create(
                new RecommendListCreateRequest("去重清单", null, cityId, 0)).id();

        assertThatThrownBy(() -> recommendListService.replaceMerchants(listId, List.of(
                new RecommendListMerchantItemRequest(merchantId, 0),
                new RecommendListMerchantItemRequest(merchantId, 1))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("重复");
    }

    // @scenario: recommend-list/推荐清单管理#清单列表按排序号升序
    @Test
    void pageOrdersBySortOrderAscending() {
        UUID cityId = cityId();
        recommendListService.create(new RecommendListCreateRequest("清单五", null, cityId, 5));
        recommendListService.create(new RecommendListCreateRequest("清单一", null, cityId, 1));
        recommendListService.create(new RecommendListCreateRequest("清单三", null, cityId, 3));

        assertThat(recommendListService.page(cityId, null, org.springframework.data.domain.PageRequest.of(0, 10))
                .content())
                .extracting(item -> item.sortOrder())
                .containsExactly(1, 3, 5);
    }

    // @scenario: recommend-list/清单内商户维护#从清单移除商户
    @Test
    void replaceMerchantsRemovesUnlistedWithoutDeletingMerchant() {
        UUID cityId = cityId();
        UUID m1 = merchantIn(cityId);
        UUID m2 = merchantIn(cityId);
        UUID listId = recommendListService.create(
                new RecommendListCreateRequest("移除清单", null, cityId, 0)).id();
        recommendListService.replaceMerchants(listId, List.of(
                new RecommendListMerchantItemRequest(m1, 0),
                new RecommendListMerchantItemRequest(m2, 1)));

        RecommendListDetailResponse detail = recommendListService.replaceMerchants(listId,
                List.of(new RecommendListMerchantItemRequest(m2, 0)));

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
                new RecommendListCreateRequest("待删清单", null, cityId, 0)).id();
        recommendListService.replaceMerchants(listId,
                List.of(new RecommendListMerchantItemRequest(merchantId, 0)));

        recommendListService.delete(listId);

        assertThatThrownBy(() -> recommendListService.detail(listId))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(recommendListMerchantRepository.findAllByRecommendListIdOrderBySortOrderAsc(listId)).isEmpty();
        assertThat(merchantRepository.existsById(merchantId)).isTrue();
    }
}
