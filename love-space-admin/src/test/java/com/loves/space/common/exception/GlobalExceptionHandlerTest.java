package com.loves.space.common.exception;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import static org.assertj.core.api.Assertions.assertThat;

/** 404 / 409 / 乐观锁映射。 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/admin/ai/scenes/x");

    @Test
    void notFoundMapsTo404() {
        var response = handler.handleResourceNotFound(new ResourceNotFoundException("场景不存在"), request);
        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).containsEntry("message", "场景不存在");
    }

    @Test
    void conflictMapsTo409() {
        var response = handler.handleConflict(new ConflictException("请先测试"), request);
        assertThat(response.getStatusCode().value()).isEqualTo(409);
        assertThat(response.getBody()).containsEntry("message", "请先测试");
    }

    @Test
    void optimisticLockMapsTo409() {
        var response = handler.handleOptimisticLock(new ObjectOptimisticLockingFailureException(Object.class, "id"), request);
        assertThat(response.getStatusCode().value()).isEqualTo(409);
        assertThat(response.getBody()).containsEntry("message", "内容已被他人更新，请刷新");
    }
}
