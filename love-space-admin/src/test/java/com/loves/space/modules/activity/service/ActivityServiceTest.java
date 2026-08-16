package com.loves.space.modules.activity.service;

import com.loves.space.infrastructure.storage.ImageUrlSigner;
import com.loves.space.infrastructure.storage.ObjectKeyValidator;
import com.loves.space.modules.activity.dto.ActivityDetailResponse;
import com.loves.space.modules.activity.dto.ActivityItineraryItemRequest;
import com.loves.space.modules.activity.dto.ActivityUpsertRequest;
import com.loves.space.modules.city.entity.City;
import com.loves.space.modules.city.repository.CityRepository;
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
 * {@link ActivityService} 集成测试：创建（含行程顺序与富文本图片绑定）、必填校验、上下线。
 */
class ActivityServiceTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private CityRepository cityRepository;

    @MockitoBean
    private ObjectKeyValidator objectKeyValidator;

    @MockitoBean
    private ImageUrlSigner imageUrlSigner;

    @BeforeEach
    void stubStorage() {
        when(objectKeyValidator.validateAndBind(anyString()))
                .thenAnswer(inv -> "bound/" + inv.getArgument(0));
        when(imageUrlSigner.sign(anyString()))
                .thenAnswer(inv -> "https://signed.example.com/" + inv.getArgument(0));
    }

    private UUID cityId() {
        City city = new City();
        city.setChineseName("活动城-" + UUID.randomUUID());
        city.setEnglishName("activity-city");
        city.setChineseProvince("省");
        city.setEnglishProvince("Province");
        city.setOnline(true);
        return cityRepository.save(city).getId();
    }

    private ActivityUpsertRequest request(UUID cityId, String title) {
        return new ActivityUpsertRequest(cityId, List.of("images/a.png"), title,
                List.of("徒步"), List.of("FOLLICULAR", "OVULATION"), "L2",
                "简介", "编辑说", "集合地", "解散地", "交通", "签证",
                List.of(new ActivityItineraryItemRequest("第一天", "内容一"),
                        new ActivityItineraryItemRequest("第二天", "内容二")),
                "<p>说明</p><img src=\"images/rich.png\">", null);
    }

    // @scenario: activity/活动管理#创建活动
    @Test
    void createReturnsFullDetailWithBoundRichTextImages() {
        UUID cityId = cityId();
        ActivityDetailResponse detail = activityService.create(request(cityId, "山间徒步"));
        assertThat(detail.cityId()).isEqualTo(cityId);
        assertThat(detail.title()).isEqualTo("山间徒步");
        assertThat(detail.periods()).containsExactly("FOLLICULAR", "OVULATION");
        assertThat(detail.itinerary()).extracting(i -> i.title())
                .containsExactly("第一天", "第二天");
        // 富文本 img src 落库为 bound key，读出时替换为签名 URL
        assertThat(detail.detailHtml()).contains("https://signed.example.com/bound/images/rich.png");
    }

    // @scenario: activity/活动管理#缺少必填项被拒绝
    @Test
    void createRejectsMissingCity() {
        assertThatThrownBy(() -> activityService.create(request(UUID.randomUUID(), "无城活动")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("所属城市不存在");
    }

    // @scenario: activity/活动管理#活动上下线切换
    @Test
    void setOnlineToggles() {
        UUID id = activityService.create(request(cityId(), "开关活动")).id();
        assertThat(activityService.setOnline(id, true).online()).isTrue();
        assertThat(activityService.setOnline(id, false).online()).isFalse();
    }
}
