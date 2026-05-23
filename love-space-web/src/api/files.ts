import { uploadToOss } from "../lib/ossUpload";

export interface FileUploadResponse {
  /** OSS objectKey（典型 `images/<uuidv7>.<ext>`），后端创建/更新时作为 imageUrls 入库。 */
  url: string;
}

export async function uploadFile(file: File): Promise<FileUploadResponse> {
  const objectKey = await uploadToOss(file);
  return { url: objectKey };
}
