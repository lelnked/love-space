package com.loves.space.infrastructure.storage;

/**
 * 业务实体绑定 OSS 对象 key 时的服务端校验 + 归档接口。
 *
 * <p>调用方：业务 service（Banner / Merchant / City …）在持久化前对每一个图片字段调用。
 * 实现：{@link AliyunOssObjectKeyValidator}。
 */
public interface ObjectKeyValidator {

    /**
     * 校验客户端提交的 objectKey，并在首次绑定时把对象迁移到 bound 前缀。
     *
     * <p>合法输入形态：
     * <ul>
     *     <li>{@code images/<uuidv7>.<ext>}：新上传，触发 copy → delete，返回 {@code bound/<uuidv7>.<ext>}</li>
     *     <li>{@code bound/<uuidv7>.<ext>}：旧图保留，仅做存在性校验，原样返回</li>
     * </ul>
     *
     * @param rawObjectKey 客户端提交值
     * @return 持久化值：始终是 {@code bound/<uuidv7>.<ext>}
     */
    String validateAndBind(String rawObjectKey);
}
