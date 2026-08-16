package com.space.app.modules.featured.service;

import com.space.app.infrastructure.storage.ImageUrlSigner;
import com.space.app.modules.city.entity.City;
import com.space.app.modules.city.repository.CityRepository;
import com.space.app.modules.featured.entity.FeaturedItem;
import com.space.app.modules.featured.repository.FeaturedItemRepository;
import com.space.app.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * {@link FeaturedItemQueryService} 集成测试：上线∧城市上架可见性、城市信息映射。
 */
class FeaturedItemQueryServiceTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private FeaturedItemQueryService featuredItemQueryService;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private FeaturedItemRepository featuredItemRepository;

    @MockitoBean
    private ImageUrlSigner imageUrlSigner;

    @BeforeEach
    void stubSigner() {
        when(imageUrlSigner.sign(anyString()))
                .thenAnswer(inv -> "https://signed.example.com/" + inv.getArgument(0));
    }

    private City city(String name, boolean online) {
        City city = new City();
        city.setChineseName(name);
        city.setEnglishName("featured-city");
        city.setChineseProvince("省");
        city.setEnglishProvince("Province");
        city.setOnline(online);
        return cityRepository.save(city);
    }

    private UUID item(UUID cityId, boolean online, String description) {
        FeaturedItem item = new FeaturedItem();
        item.setCityId(cityId);
        item.setBanner("bound/banner.png");
        item.setDescription(description);
        item.setOnline(online);
        return featuredItemRepository.save(item).getId();
    }

    // @scenario: featured/App 端精选推荐查询#查询精选推荐信息流
    @Test
    void listReturnsOnlineItemsWithCityInfo() {
        City city = city("信息流城-" + UUID.randomUUID(), true);
        UUID visible = item(city.getId(), true, "上新推荐");
        item(city.getId(), false, "下线条目");

        var feed = featuredItemQueryService.list().stream()
                .filter(f -> f.city().id().equals(city.getId()))
                .toList();
        assertThat(feed).extracting(f -> f.id()).containsExactly(visible);
        assertThat(feed.getFirst().city().name()).isEqualTo(city.getChineseName());
        assertThat(feed.getFirst().banner().url())
                .isEqualTo("https://signed.example.com/bound/banner.png");
        assertThat(feed.getFirst().description()).isEqualTo("上新推荐");
    }

    // @scenario: city/地图下架对精选推荐级联生效#下架城市后 app 端精选推荐不可见
    @Test
    void offlineCityItemsInvisible() {
        City offlineCity = city("下架城-" + UUID.randomUUID(), false);
        item(offlineCity.getId(), true, "被级联隐藏");

        assertThat(featuredItemQueryService.list())
                .noneMatch(f -> f.city().id().equals(offlineCity.getId()));
    }
}
