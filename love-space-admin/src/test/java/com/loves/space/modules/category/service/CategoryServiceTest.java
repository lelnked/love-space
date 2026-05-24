package com.loves.space.modules.category.service;

import com.loves.space.infrastructure.storage.ImageUrlSigner;
import com.loves.space.infrastructure.storage.ObjectKeyValidator;
import com.loves.space.modules.category.dto.CategoryItemResponse;
import com.loves.space.modules.category.dto.CategoryUpsertRequest;
import com.loves.space.modules.city.dto.CityCreateRequest;
import com.loves.space.modules.city.service.CityService;
import com.loves.space.modules.merchant.dto.MerchantDetailResponse;
import com.loves.space.modules.merchant.dto.MerchantUpsertRequest;
import com.loves.space.modules.merchant.entity.Merchant;
import com.loves.space.modules.merchant.repository.MerchantRepository;
import com.loves.space.modules.merchant.service.MerchantService;
import com.loves.space.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * {@link CategoryService} 集成测试：删除分类前会触发同分类商户批量下架。
 */
class CategoryServiceTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private MerchantRepository merchantRepository;
    @Autowired
    private CityService cityService;

    @MockitoBean
    private ObjectKeyValidator objectKeyValidator;

    @MockitoBean
    private ImageUrlSigner imageUrlSigner;

    @BeforeEach
    void stubStorage() {
        when(objectKeyValidator.validateAndBind(anyString()))
                .thenAnswer(inv -> {
                    String key = inv.getArgument(0);
                    return key.startsWith("images/") ? "bound/" + key.substring("images/".length()) : key;
                });
        when(imageUrlSigner.sign(anyString()))
                .thenAnswer(inv -> "https://signed.example.com/" + inv.getArgument(0));
    }

    @Test
    void deleteCategoryOfflinesAndDetachesItsMerchants() {
        CategoryItemResponse category = categoryService.create(
                new CategoryUpsertRequest("测试分类-" + UUID.randomUUID()));

        UUID cityId = cityService.create(new CityCreateRequest(
                "城-" + UUID.randomUUID(), "EN", "省", "Province", null, true)).id();
        MerchantUpsertRequest request = new MerchantUpsertRequest(
                "分类下商户",
                "https://example.com/logo.png",
                "地址",
                null, null,
                cityId,
                category.id(),
                (short) 20, (short) 15, (short) 15, (short) 10,
                null, 0, true,
                List.of(), List.of(),
                List.of("https://example.com/1.png")
        );
        MerchantDetailResponse merchant = merchantService.upsert(null, request);
        assertThat(merchantRepository.findById(merchant.id()).orElseThrow().isOnline()).isTrue();

        categoryService.delete(category.id());

        Merchant reloaded = merchantRepository.findById(merchant.id()).orElseThrow();
        assertThat(reloaded.isOnline()).isFalse();
        assertThat(reloaded.getCategoryId()).isNull();
    }
}
