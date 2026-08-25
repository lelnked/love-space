package com.loves.space.modules.activity.service;

import com.loves.space.infrastructure.storage.ImageUrlSigner;
import com.loves.space.infrastructure.storage.ObjectKeyValidator;
import com.loves.space.modules.activity.dto.ActivityDetailResponse;
import com.loves.space.modules.activity.dto.ActivityItemResponse;
import com.loves.space.modules.activity.dto.ActivityItineraryItemRequest;
import com.loves.space.modules.activity.dto.ActivityUpsertRequest;
import com.loves.space.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * {@link ActivityService} 集成测试：创建（含行程顺序与富文本图片绑定）、列表、上下线。
 */
class ActivityServiceTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private ActivityService activityService;

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

    private ActivityUpsertRequest request(String title) {
        return new ActivityUpsertRequest(List.of("images/a.png"), title,
                List.of("徒步"), List.of("FOLLICULAR", "OVULATION"), "L2",
                "简介", "编辑说", "集合地", "解散地", "交通", "签证", "海岸线景观",
                List.of(new ActivityItineraryItemRequest("第一天", "内容一"),
                        new ActivityItineraryItemRequest("第二天", "内容二")),
                "<p>说明</p><img src=\"images/rich.png\">", null);
    }

    // @scenario: activity/活动管理#创建活动
    @Test
    void createReturnsFullDetailWithBoundRichTextImages() {
        ActivityDetailResponse detail = activityService.create(request("山间徒步"));
        assertThat(detail.title()).isEqualTo("山间徒步");
        assertThat(detail.periods()).containsExactly("FOLLICULAR", "OVULATION");
        assertThat(detail.landscape()).isEqualTo("海岸线景观");
        assertThat(detail.itinerary()).extracting(i -> i.title())
                .containsExactly("第一天", "第二天");
        // 富文本 img src 落库为 bound key，读出时替换为签名 URL
        assertThat(detail.detailHtml()).contains("https://signed.example.com/bound/images/rich.png");
    }

    // @scenario: activity/活动管理#活动列表不按城市过滤
    @Test
    void pageReturnsAllActivitiesRegardlessOfCity() {
        String tag = UUID.randomUUID().toString().substring(0, 8);
        activityService.create(request("列表甲-" + tag));
        activityService.create(request("列表乙-" + tag));

        assertThat(activityService.page(tag, PageRequest.of(0, 20)).content())
                .extracting(ActivityItemResponse::title)
                .containsExactlyInAnyOrder("列表甲-" + tag, "列表乙-" + tag);
    }

    // @scenario: activity/活动管理#景观字段可写可改可空
    @Test
    void landscapeIsWritableUpdatableAndNullable() {
        ActivityDetailResponse created = activityService.create(request("景观活动"));
        assertThat(created.landscape()).isEqualTo("海岸线景观");

        ActivityUpsertRequest base = request("景观活动");
        ActivityUpsertRequest volcano = new ActivityUpsertRequest(base.images(), base.title(),
                base.tags(), base.periods(), base.level(), base.introduction(), base.editorNote(),
                base.gatheringPlace(), base.dismissalPlace(), base.transportation(), base.visa(),
                "火山地貌", base.itinerary(), base.detailHtml(), base.online());
        assertThat(activityService.update(created.id(), volcano).landscape()).isEqualTo("火山地貌");

        ActivityUpsertRequest blank = new ActivityUpsertRequest(base.images(), base.title(),
                base.tags(), base.periods(), base.level(), base.introduction(), base.editorNote(),
                base.gatheringPlace(), base.dismissalPlace(), base.transportation(), base.visa(),
                null, base.itinerary(), base.detailHtml(), base.online());
        assertThat(activityService.update(created.id(), blank).landscape()).isNull();
    }

    // @scenario: activity/活动管理#活动上下线切换
    @Test
    void setOnlineToggles() {
        UUID id = activityService.create(request("开关活动")).id();
        assertThat(activityService.setOnline(id, true).online()).isTrue();
        assertThat(activityService.setOnline(id, false).online()).isFalse();
    }
}
