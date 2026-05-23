package com.loves.space.modules.category.service;

import com.loves.space.modules.category.dto.CategoryItemResponse;
import com.loves.space.modules.category.dto.CategoryUpsertRequest;
import com.loves.space.modules.merchant.dto.MerchantDetailResponse;
import com.loves.space.modules.merchant.dto.MerchantUpsertRequest;
import com.loves.space.modules.merchant.repository.MerchantRepository;
import com.loves.space.modules.merchant.service.MerchantService;
import com.loves.space.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    void deleteCategoryOfflinesItsMerchants() {
        CategoryItemResponse category = categoryService.create(
                new CategoryUpsertRequest("测试分类-" + UUID.randomUUID()));

        UUID cityId = UUID.randomUUID();
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
                List.of("https://example.com/1.png"),
                List.of()
        );
        MerchantDetailResponse merchant = merchantService.upsert(null, request);
        assertThat(merchantRepository.findById(merchant.id()).orElseThrow().isOnline()).isTrue();

        categoryService.delete(category.id());

        assertThat(merchantRepository.findById(merchant.id()).orElseThrow().isOnline()).isFalse();
    }
}
