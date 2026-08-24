import { useRef, useMemo, forwardRef, useImperativeHandle } from "react";
import ReactQuill from "react-quill-new";
import type { Quill } from "react-quill-new";
import "react-quill-new/dist/quill.snow.css";
import { AxiosError } from "axios";

import { uploadToOss } from "../../lib/ossUpload";
import { useToast } from "../../context/ToastContext";

interface RichTextEditorProps {
  /**
   * 初始 HTML（编辑回显，img src 为后端下发的签名 URL）。
   * 仅首次挂载时生效——父级需在数据加载完成后再渲染本组件（沿既有 Form 的 loading 门控即可）。
   */
  initialValue?: string;
  /**
   * 内容变化回调（非受控模式下可选，编辑器内部状态独立维护）。
   * blobUrl -> objectKey 的替换在提交时由 getHtmlForSubmit() 完成。
   */
  onChange?: (html: string) => void;
  disabled?: boolean;
}

const ACCEPT = "image/png,image/jpeg,image/webp";

/**
 * 富文本编辑公用组件（Quill）。
 *
 * 工具栏：段落样式 / 字体 / 加粗 / 斜体 / 下划线 / 无序列表 / 有序列表 / 居中对齐
 *        / 链接 / 图片 / 视频 / 公式 / 代码块 / 下标 / 上标。
 * 插图复用既有 OSS 直传链路：上传得 objectKey，编辑器内用 data URL 即时预览，
 * 提交时由 getHtmlForSubmit() 把 data URL 替换为 objectKey。
 */
export interface RichTextEditorRef {
  /** 获取用于提交的 HTML（blobUrl 已替换为 objectKey）。 */
  getHtmlForSubmit: () => string;
}

export default forwardRef<RichTextEditorRef, RichTextEditorProps>(function RichTextEditor(
  { initialValue, onChange: _onChange, disabled },
  ref,
) {
  const toast = useToast();
  const quillRef = useRef<ReactQuill>(null);
  // 预览 URL（data URL）-> objectKey 映射，提交时由 getHtmlForSubmit() 替换。
  const keyMapRef = useRef(new Map<string, string>());

  useImperativeHandle(
    ref,
    () => ({
      getHtmlForSubmit: () => {
        if (!quillRef.current) return "";
        const editor: Quill = quillRef.current.getEditor();
        let html = editor.root.innerHTML;
        for (const [blobUrl, objectKey] of keyMapRef.current) {
          html = html.split(blobUrl).join(objectKey);
        }
        return html;
      },
    }),
    [],
  );

  const insertImage = async (file?: File) => {
    if (!file || !quillRef.current) return;
    const editor: Quill = quillRef.current.getEditor();
    const range = editor.getSelection(true);

    // 1. 用 FileReader 生成 data URL，立即插入编辑器做预览
    const dataUrl: string = await new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => resolve(reader.result as string);
      reader.onerror = () => reject(new Error("文件读取失败"));
      reader.readAsDataURL(file);
    });
    editor.insertEmbed(range.index, "image", dataUrl);
    editor.setSelection(range.index + 1);

    // 2. 后台上传到 OSS，成功后注册 dataUrl -> objectKey 映射
    try {
      const objectKey = await uploadToOss(file);
      keyMapRef.current.set(dataUrl, objectKey);
    } catch (err) {
      console.error("[RichTextEditor] OSS 上传失败，图片将保留 data URL:", err);
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(
        ax.response?.data?.detail ?? (err instanceof Error ? err.message : "上传失败"),
      );
      // 上传失败也注册映射（dataUrl -> dataUrl），提交时保留原样
      keyMapRef.current.set(dataUrl, dataUrl);
    }
  };

  const insertVideo = () => {
    if (!quillRef.current) return;
    const url = window.prompt("请输入视频 URL（YouTube/Vimeo 等）:");
    if (url) {
      const editor: Quill = quillRef.current.getEditor();
      const range = editor.getSelection(true);
      editor.insertEmbed(range.index, "video", url);
    }
  };

  const modules = useMemo(
    () => ({
      toolbar: {
        container: [
          [{ header: [1, 2, 3, false] }],
          [{ font: [] }],
          ["bold", "italic", "underline"],
          [{ list: "bullet" }, { list: "ordered" }, { align: [] }],
          ["link", "image", "video"],
          ["formula", "code-block"],
          [{ script: "sub" }, { script: "super" }],
        ],
        handlers: {
          image: () => {
            const input = document.createElement("input");
            input.setAttribute("type", "file");
            input.setAttribute("accept", ACCEPT);
            input.click();
            input.onchange = () => {
              const file = input.files?.[0];
              if (file) void insertImage(file);
            };
          },
          video: insertVideo,
        },
      },
    }),
    // refs 身份稳定，imageHandler/videoHandler 通过 ref.current 访问最新实例
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [],
  );

  return (
    <div className="rounded-lg border border-gray-300 dark:border-gray-700 rich-text-editor">
      <style>{`
        .rich-text-editor .ql-editor { min-height: 200px; }
      `}</style>
      <ReactQuill
        ref={quillRef}
        theme="snow"
        defaultValue={initialValue ?? ""}
        readOnly={disabled}
        modules={modules}
      />
    </div>
  );
});
