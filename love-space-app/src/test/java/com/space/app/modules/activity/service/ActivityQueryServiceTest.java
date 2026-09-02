package com.space.app.modules.activity.service;

import com.space.app.common.exception.ResourceNotFoundException;
import com.space.app.infrastructure.storage.ImageUrlSigner;
import com.space.app.modules.activity.dto.ActivityItemResponse;
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
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * {@link ActivityQueryService} 集成测试：全局列表、下线不可见、富文本签名替换、与城市上架状态无关。
 */
class ActivityQueryServiceTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private ActivityQueryService activityQueryService;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private CityRepository cityRepository;

    @MockitoBean
    private ImageUrlSigner imageUrlSigner;

    @BeforeEach
    void stubSigner() {
        when(imageUrlSigner.sign(anyString()))
                .thenAnswer(inv -> "https://signed.example.com/" + inv.getArgument(0));
    }

    private UUID activity(boolean online, String title) {
        Activity activity = new Activity();
        activity.setImages(new ArrayList<>(List.of("bound/a.png")));
        activity.setTitle(title);
        activity.setTags(new ArrayList<>(List.of("徒步")));
        activity.setPeriods(new ArrayList<>(List.of("FOLLICULAR")));
        activity.setLevel("L1");
        activity.setItinerary(new ArrayList<>(List.of(new ActivityItineraryItem("第一天", "内容"))));
        activity.setDetailHtml("<p>说明</p><img src=\"bound/rich.png\">");
        activity.setLandscape("火山地貌");
        activity.setOnline(online);
        return activityRepository.save(activity).getId();
    }

    /** 造一个下架城市，用于证明活动可见性与城市状态无关。 */
    private void offlineCity() {
        City city = new City();
        city.setChineseName("下架城-" + UUID.randomUUID());
        city.setEnglishName("offline-city");
        city.setChineseProvince("省");
        city.setEnglishProvince("Province");
        city.setOnline(false);
        cityRepository.save(city);
    }

    // @scenario: activity/App 端活动查询#活动副标题下发且未填时为 null
    @Test
    void listAndDetailCarrySubtitleAndNullWhenAbsent() {
        String tag = UUID.randomUUID().toString().substring(0, 8);
        UUID withSubtitle = activity(true, "有副标题-" + tag);
        Activity a = activityRepository.findById(withSubtitle).orElseThrow();
        a.setSubtitle("山野轻装");
        activityRepository.save(a);
        UUID withoutSubtitle = activity(true, "无副标题-" + tag);

        assertThat(activityQueryService.listAll())
                .filteredOn(i -> i.title().endsWith(tag))
                .extracting(ActivityItemResponse::title, ActivityItemResponse::subtitle)
                .contains(tuple("有副标题-" + tag, "山野轻装"), tuple("无副标题-" + tag, null));
        assertThat(activityQueryService.detail(withSubtitle).subtitle()).isEqualTo("山野轻装");
        // 未填写时为 null，不回落为标题
        assertThat(activityQueryService.detail(withoutSubtitle).subtitle()).isNull();
    }

    // @scenario: activity/App 端活动查询#活动详情返回景观
    @Test
    void detailReturnsLandscape() {
        UUID id = activity(true, "景观活动");
        assertThat(activityQueryService.detail(id).landscape()).isEqualTo("火山地貌");
    }

    // @scenario: activity/App 端活动查询#查询上架城市的活动
    @Test
    void listReturnsAllOnlineActivities() {
        offlineCity();
        UUID first = activity(true, "可见活动甲");
        UUID second = activity(true, "可见活动乙");

        assertThat(activityQueryService.listAll())
                .extracting(ActivityItemResponse::id)
                .contains(first, second);
    }

    // @scenario: activity/App 端活动查询#下线活动不可见
    @Test
    void offlineActivityInvisible() {
        UUID offline = activity(false, "下线活动");

        assertThat(activityQueryService.listAll())
                .extracting(ActivityItemResponse::id)
                .doesNotContain(offline);
        assertThatThrownBy(() -> activityQueryService.detail(offline))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // @scenario: activity/App 端活动查询#城市上架状态不影响活动详情可见性
    @Test
    void visibilityIndependentOfCityOnlineState() {
        offlineCity();
        UUID id = activity(true, "与城市无关的活动");

        assertThat(activityQueryService.detail(id).id()).isEqualTo(id);
        assertThat(activityQueryService.listAll())
                .extracting(ActivityItemResponse::id)
                .contains(id);
    }

    // @scenario: activity/App 端活动查询#活动详情返回富文本
    @Test
    void detailReturnsSignedRichTextHtml() {
        UUID id = activity(true, "富文本活动");

        assertThat(activityQueryService.detail(id).detailHtml())
                .contains("<p>说明</p>")
                .contains("https://signed.example.com/bound/rich.png");
    }
}
