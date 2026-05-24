/** 图片对外表示：id = OSS 对象 key（典型 `bound/<uuid>.<ext>`），url = 当次签名访问地址。 */
export interface ImageResponse {
  id: string;
  url: string;
}

/** 申请上传凭证请求体。 */
export interface UploadCredentialRequest {
  /** 图片 MIME，仅支持 image/png | image/jpeg | image/webp。 */
  contentType: string;
}

/** 服务端下发的 OSS 表单直传（PostObject）签名 + 预生成 objectKey。 */
export interface UploadCredentialResponse {
  /** 表单提交地址（带 bucket 的虚拟主机域名）。 */
  host: string;
  /** 服务端预生成的目标 key，表单 `key` 字段须与之相等，例如 `images/<uuidv7>.<ext>`。 */
  objectKey: string;
  /** Base64 编码的 Policy。 */
  policy: string;
  /** V4 签名。 */
  signature: string;
  /** 签名版本，固定 `OSS4-HMAC-SHA256`。 */
  signatureVersion: string;
  /** `x-oss-credential` 表单字段。 */
  xOssCredential: string;
  /** `x-oss-date` 表单字段。 */
  xOssDate: string;
  /** `x-oss-security-token` 表单字段。 */
  securityToken: string;
  /** ISO-8601 UTC 过期时间。 */
  expiration: string;
}
