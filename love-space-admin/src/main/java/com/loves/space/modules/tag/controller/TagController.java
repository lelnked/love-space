package com.loves.space.modules.tag.controller;

import com.loves.space.common.annotation.OperationLog;
import com.loves.space.common.dto.OnlineStatusRequest;
import com.loves.space.modules.tag.dto.TagItemResponse;
import com.loves.space.modules.tag.dto.TagQuery;
import com.loves.space.modules.tag.dto.TagUpsertRequest;
import com.loves.space.modules.tag.service.TagService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * 标签管理 Controller（运营后台）。
 */
@RestController
@RequestMapping("/api/admin/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    /** 标签列表（可按上下架状态、名称模糊过滤）。 */
    @GetMapping
    public List<TagItemResponse> list(@RequestParam(required = false) Boolean online,
                                      @RequestParam(required = false) String name) {
        return tagService.list(new TagQuery(online, name));
    }

    /** 创建标签。 */
    @PostMapping
    @OperationLog("tag:create")
    public TagItemResponse create(@Valid @RequestBody TagUpsertRequest request) {
        return tagService.create(request);
    }

    /** 更新标签名。 */
    @PutMapping("/{id}")
    @OperationLog("tag:update")
    public TagItemResponse update(@PathVariable UUID id,
                                  @Valid @RequestBody TagUpsertRequest request) {
        return tagService.update(id, request);
    }

    /** 切换标签上下架。 */
    @PutMapping("/{id}/online")
    @OperationLog("tag:set-online")
    public TagItemResponse setOnline(@PathVariable UUID id,
                                     @Valid @RequestBody OnlineStatusRequest request) {
        return tagService.setOnline(id, request.online());
    }

    /** 删除标签。 */
    @DeleteMapping("/{id}")
    @OperationLog("tag:delete")
    public void delete(@PathVariable UUID id) {
        tagService.delete(id);
    }
}
