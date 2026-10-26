package com.loves.space.modules.ai.service;

import com.loves.space.common.exception.ResourceNotFoundException;
import com.loves.space.modules.ai.dto.AiLimitResponse;
import com.loves.space.modules.ai.dto.AiLimitUpdateRequest;
import com.loves.space.modules.ai.entity.AiLimit;
import com.loves.space.modules.ai.repository.AiLimitRepository;
import com.loves.space.security.OperatingContext;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;

/** 生成额度配置：查看与逐项修改，每行独立记录修改人。 */
@Service
@Transactional
public class AiLimitService {

    private final AiLimitRepository limitRepository;
    private final OperatingContext operatingContext;
    private final Clock clock;

    public AiLimitService(AiLimitRepository limitRepository, OperatingContext operatingContext, Clock aiClock) {
        this.limitRepository = limitRepository;
        this.operatingContext = operatingContext;
        this.clock = aiClock;
    }

    /** 全部限制，按编码升序。 */
    @Transactional(readOnly = true)
    public List<AiLimitResponse> list() {
        return limitRepository.findAll(Sort.by("key")).stream().map(AiLimitResponse::from).toList();
    }

    /** 修改一项：只改该行的值与修改人/时间。 */
    public AiLimitResponse update(String key, AiLimitUpdateRequest request) {
        AiLimit limit = limitRepository.findById(key)
                .orElseThrow(() -> new ResourceNotFoundException("限制不存在：" + key));
        int value = request.value().intValueExact();
        if (value == 0) {
            throw new IllegalArgumentException("取值须为 -1 或正整数");
        }
        limit.setValue(value);
        limit.setUpdatedBy(operatingContext.currentUsername().orElse(null));
        limit.setUpdatedAt(OffsetDateTime.now(clock));
        return AiLimitResponse.from(limit);
    }
}
