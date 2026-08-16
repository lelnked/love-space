import { useRef } from "react";
import { EditorContent, useEditor } from "@tiptap/react";
import StarterKit from "@tiptap/starter-kit";
import Image from "@tiptap/extension-image";
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
   * 内容变化回调。回传的 HTML 已把本地预览 blob src 归一为上传响应的 objectKey，
   * 可直接提交；回显残留的签名 URL 由后端保存时自动归一回 objectKey。
   */
  onChange: (html: string) => void;
  disabled?: boolean;
}

const ACCEPT = "image/png,image/jpeg,image/webp";

/**
 * 富文本编辑公用组件（TipTap）。
 *
 * 工具栏：加粗 / 斜体 / 无序列表 / 有序列表 / 插入图片。
 * 插图复用既有 OSS 直传链路：上传得 objectKey，编辑器内用本地 blob URL 预览，
 * onChange 回传时把 blob src 替换为 objectKey。
 */
export default function RichTextEditor({ initialValue, onChange, disabled }: RichTextEditorProps) {
  const toast = useToast();
  const inputRef = useRef<HTMLInputElement>(null);
  // 本地预览 blob URL -> objectKey 映射，提交 HTML 前做替换。
  // ponytail: blob URL 不主动 revoke，随页面卸载释放
  const keyMapRef = useRef(new Map<string, string>());

  const editor = useEditor({
    extensions: [StarterKit, Image],
    content: initialValue ?? "",
    editable: !disabled,
    editorProps: {
      attributes: {
        class:
          "min-h-[200px] px-3 py-2 text-sm outline-none " +
          "[&_ul]:list-disc [&_ul]:pl-5 [&_ol]:list-decimal [&_ol]:pl-5 [&_img]:max-w-full",
      },
    },
    onUpdate: ({ editor: e }) => {
      let html = e.getHTML();
      for (const [blobUrl, objectKey] of keyMapRef.current) {
        html = html.split(blobUrl).join(objectKey);
      }
      onChange(html);
    },
  });

  const insertImage = async (file?: File) => {
    if (!file || !editor) return;
    try {
      const objectKey = await uploadToOss(file);
      const blobUrl = URL.createObjectURL(file);
      keyMapRef.current.set(blobUrl, objectKey);
      editor.chain().focus().setImage({ src: blobUrl }).run();
    } catch (err) {
      const ax = err as AxiosError<{ detail?: string }>;
      toast.error(
        ax.response?.data?.detail ?? (err instanceof Error ? err.message : "上传失败"),
      );
    }
  };

  if (!editor) return null;

  const btnClass =
    "px-2.5 py-1 text-sm rounded border border-gray-300 bg-white text-gray-700 hover:bg-gray-50 " +
    "dark:border-gray-700 dark:bg-gray-800 dark:text-gray-300 disabled:opacity-50";

  return (
    <div className="rounded-lg border border-gray-300 dark:border-gray-700">
      <div className="flex flex-wrap gap-2 border-b border-gray-200 p-2 dark:border-gray-800">
        <button
          type="button"
          className={btnClass}
          disabled={disabled}
          onClick={() => editor.chain().focus().toggleBold().run()}
        >
          加粗
        </button>
        <button
          type="button"
          className={btnClass}
          disabled={disabled}
          onClick={() => editor.chain().focus().toggleItalic().run()}
        >
          斜体
        </button>
        <button
          type="button"
          className={btnClass}
          disabled={disabled}
          onClick={() => editor.chain().focus().toggleBulletList().run()}
        >
          无序列表
        </button>
        <button
          type="button"
          className={btnClass}
          disabled={disabled}
          onClick={() => editor.chain().focus().toggleOrderedList().run()}
        >
          有序列表
        </button>
        <button
          type="button"
          className={btnClass}
          disabled={disabled}
          onClick={() => inputRef.current?.click()}
        >
          插入图片
        </button>
        <input
          ref={inputRef}
          type="file"
          accept={ACCEPT}
          className="hidden"
          onChange={(e) => {
            void insertImage(e.target.files?.[0]);
            e.target.value = ""; // 允许再次选择同一文件
          }}
        />
      </div>
      <EditorContent editor={editor} />
    </div>
  );
}
