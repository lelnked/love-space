package com.space.app.modules.category.controller;

import com.space.app.modules.category.entity.Category;
import com.space.app.modules.category.repository.CategoryRepository;
import com.space.app.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * App 端 {@link CategoryController} 读 IT：
 * <ul>
 *   <li>仅返回上架分类；</li>
 *   <li>排序 sortOrder ASC，相同 sortOrder 时 createdAt ASC。</li>
 * </ul>
 */
@AutoConfigureMockMvc
class CategoryReadIT extends AbstractPostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();
    }

    private Category save(String name, int sortOrder, boolean online) {
        Category c = new Category();
        c.setName(name);
        c.setSortOrder(sortOrder);
        c.setOnline(online);
        return categoryRepository.save(c);
    }

    @Test
    void pageReturnsOnlyOnlineSortedBySortOrderAsc() throws Exception {
        save("下架分类", 0, false);
        save("排序20", 20, true);
        save("排序5", 5, true);

        mockMvc.perform(get("/api/app/categories/page").header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("排序5"))
                .andExpect(jsonPath("$.content[1].name").value("排序20"));
    }

    @Test
    void pageBreaksTieByCreatedAtAsc() throws Exception {
        Category early = save("先创建", 0, true);
        Category late = save("后创建", 0, true);
        // 用 JdbcTemplate 确定性地拉开 created_at，避免依赖审计时间戳的纳秒差
        OffsetDateTime base = OffsetDateTime.now();
        jdbcTemplate.update("update loves_category set created_at = ? where id = ?",
                base.minusMinutes(10), early.getId());
        jdbcTemplate.update("update loves_category set created_at = ? where id = ?",
                base.minusMinutes(1), late.getId());

        mockMvc.perform(get("/api/app/categories/page").header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("先创建"))
                .andExpect(jsonPath("$.content[1].name").value("后创建"));
    }
}
