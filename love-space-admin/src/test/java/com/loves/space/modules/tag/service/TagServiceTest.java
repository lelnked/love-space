package com.loves.space.modules.tag.service;

import com.loves.space.common.exception.ValidationException;
import com.loves.space.modules.tag.dto.TagItemResponse;
import com.loves.space.modules.tag.dto.TagUpsertRequest;
import com.loves.space.modules.tag.repository.TagRepository;
import com.loves.space.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link TagService} 集成测试。
 */
class TagServiceTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private TagService tagService;
    @Autowired
    private TagRepository tagRepository;

    private String uniqueName() {
        return "T" + Integer.toHexString((int) (Math.random() * 0xFFFF));
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
