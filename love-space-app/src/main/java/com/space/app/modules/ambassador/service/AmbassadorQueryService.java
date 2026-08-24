package com.space.app.modules.ambassador.service;

import com.space.app.common.exception.ResourceNotFoundException;
import com.space.app.common.util.ImageResponses;
import com.space.app.infrastructure.storage.ImageUrlSigner;
import com.space.app.modules.ambassador.dto.AmbassadorItemResponse;
import com.space.app.modules.ambassador.entity.Ambassador;
import com.space.app.modules.ambassador.repository.AmbassadorRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 爱女大使查询服务：App 端只读，仅暴露上线大使。
 */
@Service
@Transactional(readOnly = true)
public class AmbassadorQueryService {

    /** limit 缺省值。 */
    public static final int DEFAULT_LIMIT = 3;

    /** limit 上限。 */
    public static final int MAX_LIMIT = 20;

    /** 列表排序：权重倒序，同权重按创建时间倒序（与商户列表一致）。 */
    private static final Sort SORT = Sort.by(Sort.Order.desc("weight"), Sort.Order.desc("createdAt"));

    private final AmbassadorRepository ambassadorRepository;
    private final ImageUrlSigner imageUrlSigner;

    public AmbassadorQueryService(AmbassadorRepository ambassadorRepository, ImageUrlSigner imageUrlSigner) {
        this.ambassadorRepository = ambassadorRepository;
        this.imageUrlSigner = imageUrlSigner;
    }

    /**
     * 列表：上线大使，weight DESC, createdAt DESC，最多返回 limit 条。
     *
     * <p>limit 为空/非正数回落到 {@value #DEFAULT_LIMIT}，超过 {@value #MAX_LIMIT} 时收敛到上限
     * （与 {@code PageQuery} 的「非法值回落」口径一致，不抛 400）。
     */
    public List<AmbassadorItemResponse> list(Integer limit) {
        int safeLimit = limit == null || limit < 1 ? DEFAULT_LIMIT : Math.min(limit, MAX_LIMIT);
        return ambassadorRepository.findAllByOnlineTrue(PageRequest.of(0, safeLimit, SORT)).stream()
                .map(this::toItem)
                .toList();
    }

    /** 详情：上线大使；不存在或已下线抛 {@link ResourceNotFoundException}。 */
    public AmbassadorItemResponse detail(UUID id) {
        return ambassadorRepository.findByIdAndOnlineTrue(id)
                .map(this::toItem)
                .orElseThrow(() -> new ResourceNotFoundException("ambassador not found: " + id));
    }

    private AmbassadorItemResponse toItem(Ambassador ambassador) {
        return new AmbassadorItemResponse(
                ambassador.getId(),
                ImageResponses.from(ambassador.getAvatar(), imageUrlSigner),
                ambassador.getName(),
                ambassador.getTags());
    }
}
