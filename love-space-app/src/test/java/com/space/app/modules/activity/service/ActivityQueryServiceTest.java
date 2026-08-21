package com.space.app.modules.activity.service;

import com.space.app.common.exception.ResourceNotFoundException;
import com.space.app.infrastructure.storage.ImageUrlSigner;
import com.space.app.modules.activity.entity.Activity;
import com.space.app.modules.activity.entity.ActivityItineraryItem;
import com.space.app.modules.activity.repository.ActivityRepository;
import com.space.app.modules.city.entity.City;
import com.space.app.modules.city.repository.CityRepository;
import com.space.app.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * {@link ActivityQueryService} 集成测试：城市下架/活动下线级联可见性、富文本签名替换。
 */
class ActivityQueryServiceTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private ActivityQueryService activityQueryService;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @MockitoBean
    private ImageUrlSigner imageUrlSigner;

    @BeforeEach
    void stubSigner() {
        when(imageUrlSigner.sign(anyString()))
                .thenAnswer(inv -> "https://signed.example.com/" + inv.getArgument(0));
    }

    private UUID city(boolean online) {
        City city = new City();
        city.setChineseName("活动城-" + UUID.randomUUID());
        city.setEnglishName("activity-city");
        city.setChineseProvince("省");
        city.setEnglishProvince("Province");
        city.setOnline(online);
        return cityRepository.save(city).getId();
    }

    private UUID activity(UUID cityId, boolean online, String title) {
        Activity activity = new Activity();
        activity.setCityId(cityId);
        activity.setImages(new ArrayList<>(List.of("bound/a.png")));
        activity.setTitle(title);
        activity.setTags(new ArrayList<>(List.of("徒步")));
        activity.setPeriods(new ArrayList<>(List.of("FOLLICULAR")));
        activity.setLevel("L1");
        activity.setItinerary(new ArrayList<>(List.of(new ActivityItineraryItem("第一天", "内容"))));
        activity.setDetailHtml("<p>说明</p><img src=\"bound/rich.png\">");
        activity.setOnline(online);
        return activityRepository.save(activity).getId();
    }

    // @scenario: activity/App 端活动查询#查询上架城市的活动
    @Test
    void listReturnsOnlineActivitiesOfOnlineCity() {
        UUID cityId = city(true);
        UUID visible = activity(cityId, true, "可见活动");

        assertThat(activityQueryService.listByCity(cityId))
                .extracting(a -> a.id())
                .containsExactly(visible);
    }

    // @scenario: activity/App 端活动查询#下线活动不可见
    @Test
    void offlineActivityInvisible() {
        UUID cityId = city(true);
        UUID offline = activity(cityId, false, "下线活动");

        assertThat(activityQueryService.listByCity(cityId)).isEmpty();
        assertThatThrownBy(() -> activityQueryService.detail(offline))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // @scenario: activity/App 端活动查询#活动详情返回富文本
    @Test
    void detailReturnsSignedRichTextHtml() {
        UUID id = activity(city(true), true, "富文本活动");

        assertThat(activityQueryService.detail(id).detailHtml())
                .contains("<p>说明</p>")
                .contains("https://signed.example.com/bound/rich.png");
    }

    // @scenario: city/地图下架对活动级联生效#下架城市后 app 端活动不可见
    @Test
    void offlineCityActivitiesInvisible() {
        UUID cityId = city(false);
        UUID id = activity(cityId, true, "下架城活动");

        assertThat(activityQueryService.listByCity(cityId)).isEmpty();
        assertThatThrownBy(() -> activityQueryService.detail(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
