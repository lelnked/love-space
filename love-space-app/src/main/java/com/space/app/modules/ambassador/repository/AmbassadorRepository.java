package com.space.app.modules.ambassador.repository;

import com.space.app.modules.ambassador.entity.Ambassador;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 爱女大使仓储（App 端只读）。
 */
public interface AmbassadorRepository extends JpaRepository<Ambassador, UUID> {

    /** 上线大使列表，条数与排序（weight DESC, createdAt DESC）由 {@link Pageable} 提供。 */
    List<Ambassador> findAllByOnlineTrue(Pageable pageable);

    /** 按 ID 查询且仅当上线时返回。 */
    Optional<Ambassador> findByIdAndOnlineTrue(UUID id);
}
