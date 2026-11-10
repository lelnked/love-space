package com.space.app.modules.systemconfig.service;

import com.space.app.common.exception.ResourceNotFoundException;
import com.space.app.modules.systemconfig.dto.SystemConfigItemResponse;
import com.space.app.modules.systemconfig.entity.SystemConfig;
import com.space.app.modules.systemconfig.repository.SystemConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/** App 端系统配置查询（不缓存，每次读库）。 */
@Service
@Transactional(readOnly = true)
public class SystemConfigQueryService {

    /** 批量查询单次最多 key 数。 */
    static final int MAX_KEYS = 50;

    private final SystemConfigRepository repository;

    public SystemConfigQueryService(SystemConfigRepository repository) {
        this.repository = repository;
    }

    /** 按 key 查询；不存在抛 404。 */
    public SystemConfigItemResponse get(String key) {
        return repository.findByKey(key).map(SystemConfigItemResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("配置不存在"));
    }

    /**
     * 按逗号分隔的 key 批量查询：去空白、去空项、保序去重后须为 1~50 个；
     * 不存在的 key 跳过，结果按请求中 key 的首次出现顺序排列。
     */
    public List<SystemConfigItemResponse> list(String keys) {
        Set<String> wanted = Arrays.stream(keys.split(",")).map(String::strip).filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (wanted.isEmpty() || wanted.size() > MAX_KEYS) {
            throw new IllegalArgumentException("keys 须为 1~" + MAX_KEYS + " 个");
        }
        Map<String, SystemConfig> found = repository.findAllByKeyIn(wanted).stream()
                .collect(Collectors.toMap(SystemConfig::getKey, Function.identity()));
        return wanted.stream().map(found::get).filter(Objects::nonNull).map(SystemConfigItemResponse::from).toList();
    }
}
