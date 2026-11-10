package com.loves.space.modules.systemconfig.service;

import com.loves.space.common.exception.ResourceNotFoundException;
import com.loves.space.common.page.PageQuery;
import com.loves.space.common.page.PageResponseMapper;
import com.loves.space.common.page.PageResponseMapper.PageResponse;
import com.loves.space.modules.systemconfig.dto.SystemConfigItemResponse;
import com.loves.space.modules.systemconfig.dto.SystemConfigUpdateRequest;
import com.loves.space.modules.systemconfig.entity.SystemConfig;
import com.loves.space.modules.systemconfig.entity.SystemConfig_;
import com.loves.space.modules.systemconfig.repository.SystemConfigRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;

import java.util.UUID;

/** 系统配置管理（预置 key，仅提供查询与按 key 结构校验的编辑更新）。 */
@Service
@Transactional
public class SystemConfigService {

    /** value 序列化后的最大长度。 */
    static final int MAX_VALUE_LENGTH = 65535;
    static final String KEY_IOS_LLM_POPUP = "ios_llm_popup";

    private final SystemConfigRepository repository;

    public SystemConfigService(SystemConfigRepository repository) {
        this.repository = repository;
    }

    /**
     * 分页查询，按更新时间、创建时间倒序。
     *
     * @param keyword 模糊匹配 key / 名称，可空
     */
    @Transactional(readOnly = true)
    public PageResponse<SystemConfigItemResponse> page(String keyword, Pageable pageable) {
        Specification<SystemConfig> spec = (root, cq, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }
            String like = "%" + keyword.strip().toLowerCase() + "%";
            return cb.or(cb.like(cb.lower(root.get(SystemConfig_.key)), like),
                    cb.like(cb.lower(root.get(SystemConfig_.name)), like));
        };
        Sort sort = Sort.by(Sort.Order.desc(SystemConfig_.UPDATED_AT), Sort.Order.desc(SystemConfig_.CREATED_AT));
        return PageResponseMapper.map(repository.findAll(spec, PageQuery.normalize(pageable, sort)),
                SystemConfigItemResponse::from);
    }

    @Transactional(readOnly = true)
    public SystemConfigItemResponse detail(UUID id) {
        return SystemConfigItemResponse.from(find(id));
    }

    /** 只改名称、值与说明，key 不可改，并按 key 校验 value 结构。 */
    public SystemConfigItemResponse update(UUID id, SystemConfigUpdateRequest request) {
        SystemConfig config = find(id);
        String value = validateAndNormalizeValue(config.getKey(), request.value());
        config.setName(request.name().strip());
        config.setValue(value);
        config.setDescription(request.description());
        return SystemConfigItemResponse.from(repository.saveAndFlush(config));
    }

    private SystemConfig find(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("配置不存在"));
    }

    /** 缺失与 JSON null 都视为未填；按预置 key 校验字段结构并返回紧凑 JSON 文本。 */
    private static String validateAndNormalizeValue(String key, JsonNode value) {
        if (value == null || value.isNull() || value.isMissingNode()) {
            throw new IllegalArgumentException("value 不能为空");
        }
        String text = value.toString();
        if (text.length() > MAX_VALUE_LENGTH) {
            throw new IllegalArgumentException("value 过长");
        }
        if (KEY_IOS_LLM_POPUP.equals(key)) {
            JsonNode enabledNode = value.isObject() ? value.get("enabled") : null;
            if (enabledNode == null || !enabledNode.isBoolean()) {
                throw new IllegalArgumentException("ios_llm_popup 的 enabled 必须为布尔值");
            }
            return "{\"enabled\":" + enabledNode.booleanValue() + "}";
        }
        return text;
    }
}
