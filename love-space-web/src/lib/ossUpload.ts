import { apiClient } from "../api/client";
import type {
  UploadCredentialRequest,
  UploadCredentialResponse,
} from "../types/image";

/** 申请上传签名 + 预生成 objectKey。 */
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
 * 用服务端下发的 PostObject 表单签名直传到 OSS，返回服务端预生成的 objectKey
 * （即 ImageResponse.id 的源头）。
 *
 * 浏览器只持有 policy / signature / securityToken，不接触 AccessKeySecret。
 * 用 XMLHttpRequest 发送 multipart 表单以获得真实字节进度；onProgress 形参 progress ∈ [0, 1]。
 */
export async function uploadToOss(
  file: File,
  onProgress?: UploadProgressHandler,
): Promise<string> {
  if (!file.type || !["image/png", "image/jpeg", "image/webp"].includes(file.type)) {
    throw new Error("仅支持 png/jpeg/webp 图片");
  }
  const credential = await fetchUploadCredential(file.type);
  // host 必须是绝对地址，否则会被当成相对路径拼到当前页面 URL 上。
  if (!/^https?:\/\//i.test(credential.host)) {
    throw new Error(`OSS 上传地址非法：${credential.host}`);
  }

  const formData = new FormData();
  formData.append("key", credential.objectKey);
  formData.append("policy", credential.policy);
  formData.append("x-oss-signature", credential.signature);
  formData.append("x-oss-signature-version", credential.signatureVersion);
  formData.append("x-oss-credential", credential.xOssCredential);
  formData.append("x-oss-date", credential.xOssDate);
  formData.append("x-oss-security-token", credential.securityToken);
  formData.append("success_action_status", "200");
  // file 必须为最后一个表单域。
  formData.append("file", file);

  await postFormData(credential.host, formData, onProgress);
  return credential.objectKey;
}

/** 用 XHR 提交表单，暴露上传进度并把 OSS 的非 2xx 响应转成可读错误。 */
function postFormData(
  host: string,
  formData: FormData,
  onProgress?: UploadProgressHandler,
): Promise<void> {
  return new Promise((resolve, reject) => {
    const xhr = new XMLHttpRequest();
    xhr.open("POST", host, true);
    xhr.upload.onprogress = (event) => {
      if (event.lengthComputable) {
        onProgress?.(event.loaded / event.total);
      }
    };
    xhr.onload = () => {
      if (xhr.status >= 200 && xhr.status < 300) {
        onProgress?.(1);
        resolve();
      } else {
        reject(new Error(`上传失败（${xhr.status}）`));
      }
    };
    xhr.onerror = () => reject(new Error("上传失败，请稍后再试"));
    xhr.send(formData);
  });
}
