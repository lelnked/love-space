package com.space.app.modules.recommendlist.service;

import com.space.app.common.exception.ResourceNotFoundException;
import com.space.app.infrastructure.storage.ImageUrlSigner;
import com.space.app.modules.city.entity.City;
import com.space.app.modules.city.repository.CityRepository;
import com.space.app.modules.merchant.entity.Merchant;
import com.space.app.modules.merchant.repository.MerchantRepository;
import com.space.app.modules.recommendlist.dto.RecommendListDetailResponse;
import com.space.app.modules.recommendlist.dto.RecommendListMerchantItemResponse;
import com.space.app.modules.recommendlist.entity.RecommendList;
import com.space.app.modules.recommendlist.entity.RecommendListMerchant;
import com.space.app.modules.recommendlist.repository.RecommendListMerchantRepository;
import com.space.app.modules.recommendlist.repository.RecommendListRepository;
import com.space.app.support.AbstractPostgresIntegrationTest;
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
 * {@link RecommendListQueryService} 集成测试：城市下架级联可见性、排序、上架商户过滤。
 */
class RecommendListQueryServiceTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private RecommendListQueryService recommendListQueryService;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private RecommendListRepository recommendListRepository;

    @Autowired
    private RecommendListMerchantRepository recommendListMerchantRepository;

    @MockitoBean
    private ImageUrlSigner imageUrlSigner;

    @BeforeEach
    void stubSigner() {
        when(imageUrlSigner.sign(anyString()))
                .thenAnswer(inv -> "https://signed.example.com/" + inv.getArgument(0));
    }

    private UUID city(boolean online) {
        City city = new City();
        city.setChineseName("清单城-" + UUID.randomUUID());
        city.setEnglishName("list-city");
        city.setChineseProvince("省");
        city.setEnglishProvince("Province");
        city.setOnline(online);
        return cityRepository.save(city).getId();
    }

    private UUID list(UUID cityId, int sortOrder) {
        RecommendList list = new RecommendList();
        list.setTitle("清单-" + sortOrder);
        list.setCityId(cityId);
        list.setSortOrder(sortOrder);
        return recommendListRepository.save(list).getId();
    }

    private UUID merchant(UUID cityId, boolean online, int weight) {
        Merchant merchant = new Merchant();
        merchant.setName("商户-" + weight);
        merchant.setWeight(weight);
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

    private void relate(UUID listId, UUID merchantId, int sortOrder) {
        RecommendListMerchant relation = new RecommendListMerchant();
        relation.setRecommendListId(listId);
        relation.setMerchantId(merchantId);
        relation.setSortOrder(sortOrder);
        recommendListMerchantRepository.save(relation);
    }

    // @scenario: recommend-list/App 端清单与清单内商户查询#查询上架城市的清单
    @Test
    void listReturnsOnlineCityListsInSortOrder() {
        UUID cityId = city(true);
        UUID second = list(cityId, 2);
        UUID first = list(cityId, 1);

        assertThat(recommendListQueryService.listByCity(cityId))
                .extracting(r -> r.id())
                .containsExactly(first, second);
    }

    // @scenario: recommend-list/App 端清单与清单内商户查询#下架城市清单不可见
    // @scenario: city/地图下架对推荐清单级联生效#下架城市后 app 端清单不可见
    @Test
    void offlineCityListsInvisible() {
        UUID cityId = city(false);
        UUID listId = list(cityId, 0);

        assertThat(recommendListQueryService.listByCity(cityId)).isEmpty();
        assertThatThrownBy(() -> recommendListQueryService.detail(listId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // @scenario: recommend-list/App 端清单与清单内商户查询#清单详情返回商户明细
    @Test
    void detailReturnsOnlineMerchantsInSavedOrderWithFourFields() {
        UUID cityId = city(true);
        UUID listId = list(cityId, 0);
        UUID jia = merchant(cityId, true, 1);      // weight 低，保存在前
        UUID yi = merchant(cityId, true, 100);     // weight 高，保存在后
        UUID bing = merchant(cityId, false, 50);   // 已下架
        relate(listId, jia, 1);
        relate(listId, yi, 2);
        relate(listId, bing, 3);

        RecommendListDetailResponse detail = recommendListQueryService.detail(listId);

        // 顺序 = 清单保存顺序，与 weight 无关；下架商户不出现
        assertThat(detail.merchants())
                .extracting(RecommendListMerchantItemResponse::id)
                .containsExactly(jia, yi);
        RecommendListMerchantItemResponse first = detail.merchants().get(0);
        assertThat(first.name()).isEqualTo("商户-1");
        assertThat(first.address()).isEqualTo("地址");
        assertThat(first.logo().id()).isEqualTo("bound/logo.png");
        assertThat(first.logo().url()).isEqualTo("https://signed.example.com/bound/logo.png");
        // 仅四字段：record 组件即响应字段
        assertThat(RecommendListMerchantItemResponse.class.getRecordComponents())
                .extracting(java.lang.reflect.RecordComponent::getName)
                .containsExactlyInAnyOrder("id", "name", "address", "logo");
    }
}
