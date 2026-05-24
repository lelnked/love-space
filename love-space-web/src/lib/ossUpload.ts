import OSS from "ali-oss";

import { apiClient } from "../api/client";
import type {
  UploadCredentialRequest,
  UploadCredentialResponse,
} from "../types/image";

/** 申请上传凭证 + 预生成 objectKey。 */
export async function fetchUploadCredential(
  contentType: string,
): Promise<UploadCredentialResponse> {
  const body: UploadCredentialRequest = { contentType };
  const { data } = await apiClient.post<UploadCredentialResponse>(
    "/api/admin/files/upload-credentials",
    body,
  );
  return data;
}

/** 上传进度回调；progress ∈ [0, 1]。 */
export type UploadProgressHandler = (progress: number) => void;

/**
 * 用 STS 直传到 OSS，返回服务端预生成的 objectKey（即 ImageResponse.id 的源头）。
 *
 * 通过 `multipartUpload` 上传以获得真实字节进度；onProgress 形参 progress ∈ [0, 1]。
 */
export async function uploadToOss(
  file: File,
  onProgress?: UploadProgressHandler,
): Promise<string> {
  if (!file.type || !["image/png", "image/jpeg", "image/webp"].includes(file.type)) {
    throw new Error("仅支持 png/jpeg/webp 图片");
  }
  const credential = await fetchUploadCredential(file.type);
  const client = new OSS({
    region: credential.region,
    accessKeyId: credential.accessKeyId,
    accessKeySecret: credential.accessKeySecret,
    stsToken: credential.securityToken,
    bucket: credential.bucket,
    secure: true,
  });
  await client.multipartUpload(credential.objectKey, file, {
    headers: { "Content-Type": file.type },
    progress: (percent: number) => onProgress?.(percent),
  });
  return credential.objectKey;
}
