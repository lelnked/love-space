import { apiClient } from "./client";

export interface FileUploadResponse {
  url: string;
}

/** 上传单文件（multipart 字段名 file），返回可访问 URL。 */
export async function uploadFile(file: File): Promise<FileUploadResponse> {
  const form = new FormData();
  form.append("file", file);
  const { data } = await apiClient.post<FileUploadResponse>(
    "/api/admin/files/upload",
    form,
    { headers: { "Content-Type": "multipart/form-data" } },
  );
  return data;
}
