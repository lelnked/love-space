package com.space.app.modules.tag.repository;

import com.space.app.modules.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * 标签 Repository：App 端仅按 ID 集合批量取上架标签。
 */
public interface TagRepository extends JpaRepository<Tag, UUID> {

    /** 按 ID 集合返回上架标签；下架标签在 App 中隐藏。 */
    List<Tag> findByIdInAndOnlineTrue(Collection<UUID> ids);
}
