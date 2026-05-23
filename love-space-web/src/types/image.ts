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

/** 服务端下发的 STS 直传凭证 + 预生成 objectKey。 */
export interface UploadCredentialResponse {
  accessKeyId: string;
  accessKeySecret: string;
  securityToken: string;
  /** ISO-8601 UTC 过期时间。 */
  expiration: string;
  /** 服务端预生成的目标 key，例如 `images/<uuidv7>.<ext>`。 */
  objectKey: string;
  region: string;
  bucket: string;
}
