package com.loves.space.modules.tag.service;

import com.loves.space.infrastructure.storage.ImageUrlSigner;
import com.loves.space.infrastructure.storage.ObjectKeyValidator;
import com.loves.space.modules.city.dto.CityCreateRequest;
import com.loves.space.modules.city.service.CityService;
import com.loves.space.modules.merchant.dto.MerchantUpsertRequest;
import com.loves.space.modules.merchant.repository.MerchantRepository;
import com.loves.space.modules.merchant.repository.MerchantTagRepository;
import com.loves.space.modules.merchant.service.MerchantService;
import com.loves.space.modules.tag.dto.TagItemResponse;
import com.loves.space.modules.tag.dto.TagUpsertRequest;
import com.loves.space.modules.tag.repository.TagRepository;
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
 * {@link TagService} 集成测试。
 */
class TagServiceTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private TagService tagService;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private MerchantRepository merchantRepository;
    @Autowired
    private MerchantTagRepository merchantTagRepository;
    @Autowired
    private CityService cityService;

    @MockitoBean
    private ObjectKeyValidator objectKeyValidator;
    @MockitoBean
    private ImageUrlSigner imageUrlSigner;

    @BeforeEach
    void stubStorage() {
        when(objectKeyValidator.validateAndBind(anyString()))
                .thenAnswer(inv -> inv.getArgument(0));
        when(imageUrlSigner.sign(anyString()))
                .thenAnswer(inv -> "https://signed.example.com/" + inv.getArgument(0));
    }

    private String uniqueName() {
        return "T" + Integer.toHexString((int) (Math.random() * 0xFFFF));
    }

    /** 创建一个绑定指定标签且上架的商户，返回其 ID。 */
    private UUID merchantWithTag(UUID tagId) {
        UUID cityId = cityService.create(new CityCreateRequest(
                "城-" + UUID.randomUUID(), "EN", "省", "Province", null, true)).id();
        MerchantUpsertRequest request = new MerchantUpsertRequest(
                "带标签商户", "https://example.com/logo.png", "地址", null, null,
                cityId, null,
                (short) 20, (short) 15, (short) 15, (short) 10,
                null, 0, true,
                List.of(), List.of(tagId),
                List.of("https://example.com/1.png"));
        return merchantService.upsert(null, request).id();
    }

    @Test
    void setOnlineFalsePreservesTagInDb() {
        TagItemResponse tag = tagService.create(new TagUpsertRequest(uniqueName()));
        assertThat(tag.online()).isTrue();

        tagService.setOnline(tag.id(), false);

        assertThat(tagRepository.findById(tag.id()).orElseThrow().isOnline()).isFalse();
    }

    @Test
    void deleteRemovesTag() {
        TagItemResponse tag = tagService.create(new TagUpsertRequest(uniqueName()));
        tagService.delete(tag.id());
        assertThat(tagRepository.findById(tag.id())).isEmpty();
    }

    @Test
    void setOnlineFalseClearsMerchantTagAssociationsButKeepsMerchantOnline() {
        TagItemResponse tag = tagService.create(new TagUpsertRequest(uniqueName()));
        UUID merchantId = merchantWithTag(tag.id());
        assertThat(merchantTagRepository.findAllByMerchantId(merchantId)).hasSize(1);

        tagService.setOnline(tag.id(), false);

        assertThat(merchantTagRepository.findAllByMerchantId(merchantId)).isEmpty();
        assertThat(merchantRepository.findById(merchantId).orElseThrow().isOnline()).isTrue();
    }

    @Test
    void deleteClearsMerchantTagAssociationsButKeepsMerchantOnline() {
        TagItemResponse tag = tagService.create(new TagUpsertRequest(uniqueName()));
        UUID merchantId = merchantWithTag(tag.id());
        assertThat(merchantTagRepository.findAllByMerchantId(merchantId)).hasSize(1);

        tagService.delete(tag.id());

        assertThat(merchantTagRepository.findAllByMerchantId(merchantId)).isEmpty();
        assertThat(merchantRepository.findById(merchantId).orElseThrow().isOnline()).isTrue();
    }

    @Test
    void nameLongerThanSixCodePointsIsRejected() {
        String tooLong = "字".repeat(7);
        assertThatThrownBy(() -> tagService.create(new TagUpsertRequest(tooLong)))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void duplicateNameIsRejected() {
        String name = uniqueName();
        tagService.create(new TagUpsertRequest(name));
        assertThatThrownBy(() -> tagService.create(new TagUpsertRequest(name)))
                .isInstanceOf(ValidationException.class);
    }
}
