import { useCallback, useEffect, useMemo, useRef } from "react";
import { useEditor, EditorContent } from "@tiptap/react";
import StarterKit from "@tiptap/starter-kit";
import Image from "@tiptap/extension-image";
import Underline from "@tiptap/extension-underline";
import Highlight from "@tiptap/extension-highlight";
import TextAlign from "@tiptap/extension-text-align";
import Placeholder from "@tiptap/extension-placeholder";
import CodeBlockLowlight from "@tiptap/extension-code-block-lowlight";
import Link from "@tiptap/extension-link";
import { common, createLowlight } from "lowlight";
import { uploadToOss } from "../../lib/ossUpload";
import { useToast } from "../../context/ToastContext";

interface ArticleRichTextEditorProps {
  initialValue?: string;
  onChange: (html: string) => void;
  disabled?: boolean;
}

const lowlight = createLowlight(common);

const ArticleRichTextEditor = ({
  initialValue,
  onChange,
  disabled,
}: ArticleRichTextEditorProps) => {
  const toast = useToast();
  const keyMapRef = useRef(new Map<string, string>());
  const inputRef = useRef<HTMLInputElement>(null);

  const extensions = useMemo(
    () => [
      StarterKit.configure({ codeBlock: false }),
      Underline,
      Highlight.configure({ multicolor: true }),
      Link.configure({ openOnClick: false }),
      Placeholder.configure({ placeholder: "开始输入文章内容..." }),
      Image.configure({ inline: false, allowBase64: true }),
      CodeBlockLowlight.configure({ lowlight }),
      TextAlign.configure({ types: ["heading", "paragraph"] }),
    ],
    []
  );

  const editor = useEditor({
    extensions,
    content: initialValue ?? "",
    editable: !disabled,
    editorProps: {
      attributes: {
        class:
          "min-h-[220px] rounded-b-lg border border-gray-300 dark:border-gray-700 " +
          "bg-white dark:bg-gray-900 px-4 py-3 text-sm outline-none " +
          "[&_ul]:list-disc [&_ol]:list-decimal [&_ul]:pl-5 [&_ol]:pl-5 " +
          "[&_pre]:rounded-md [&_pre]:bg-gray-900 [&_pre]:text-gray-100 [&_pre]:p-3 [&_pre]:overflow-x-auto " +
          "[&_blockquote]:border-l-4 [&_blockquote]:border-gray-300 [&_blockquote]:pl-3 [&_blockquote]:italic " +
          "[&_img]:max-w-full [&_a]:text-blue-600 [&_a]:underline",
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

  const isInternalUpdateRef = useRef(false);
  const lastSetInitialValueRef = useRef<string | undefined>(undefined);

  useEffect(() => {
    if (!editor) return;
    if (isInternalUpdateRef.current) {
      isInternalUpdateRef.current = false;
      return;
    }
    if (initialValue !== undefined && lastSetInitialValueRef.current !== initialValue) {
      editor.commands.setContent(initialValue);
      lastSetInitialValueRef.current = initialValue;
    }
  }, [editor, initialValue]);

  const insertImage = useCallback(
    async (file?: File) => {
      if (!file || !editor) return;
      try {
        const objectKey = await uploadToOss(file);
        const blobUrl = URL.createObjectURL(file);
        keyMapRef.current.set(blobUrl, objectKey);
        editor
          .chain()
          .focus()
          .setImage({ src: blobUrl })
          .run();
        console.log("[ArticleRichTextEditor] insertImage done", { objectKey, blobUrl });
        isInternalUpdateRef.current = true;
      } catch (err) {
        console.error("[ArticleRichTextEditor] insertImage failed", err);
        toast.error(
          err instanceof Error ? err.message : "图片插入失败"
        );
      }
    },
    [editor, toast]
  );

  const baseBtn =
    "inline-flex h-8 w-8 items-center justify-center rounded-md border border-gray-300 bg-white text-gray-700 hover:bg-gray-50 disabled:opacity-50 " +
    "dark:border-gray-700 dark:bg-gray-800 dark:text-gray-300 dark:hover:bg-gray-700";

  if (!editor) return null;

  const isActive = (fn: () => boolean) => (disabled ? false : fn());

  return (
    <div className="rounded-lg border border-gray-300 dark:border-gray-700 overflow-hidden">
      <div className="flex flex-wrap items-center gap-1 border-b border-gray-200 p-2 dark:border-gray-800">
        <button
          type="button"
          className={baseBtn}
          disabled={disabled}
          onClick={() => editor.chain().focus().undo().run()}
        >
          <svg viewBox="0 0 24 24" className="h-4 w-4" fill="none" stroke="currentColor" strokeWidth="2">
            <path strokeLinecap="round" strokeLinejoin="round" d="M9 15 3 9m0 0 6-6M3 9h12a6 6 0 0 1 0 12h-3" />
          </svg>
        </button>
        <button
          type="button"
          className={baseBtn}
          disabled={disabled}
          onClick={() => editor.chain().focus().redo().run()}
        >
          <svg viewBox="0 0 24 24" className="h-4 w-4" fill="none" stroke="currentColor" strokeWidth="2">
            <path strokeLinecap="round" strokeLinejoin="round" d="m15 15 6-6m0 0-6-6m6 6H9a6 6 0 0 0 0 12h3" />
          </svg>
        </button>

        <div className="mx-1 h-5 w-px bg-gray-200 dark:bg-gray-800" />

        <select
          disabled={disabled}
          className="h-8 rounded-md border border-gray-300 bg-white px-2 text-xs disabled:opacity-50 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-300"
          value={editor.isActive("heading", { level: 1 }) ? "h1" : editor.isActive("heading", { level: 2 }) ? "h2" : editor.isActive("heading", { level: 3 }) ? "h3" : "p"}
          onChange={(e) => {
            const level = e.target.value;
            if (level === "p") editor.chain().focus().setParagraph().run();
            else editor.chain().focus().toggleHeading({ level: Number(level.slice(1)) as 1 | 2 | 3 }).run();
          }}
        >
          <option value="p">正文</option>
          <option value="h1">标题 1</option>
          <option value="h2">标题 2</option>
          <option value="h3">标题 3</option>
        </select>

        <div className="mx-1 h-5 w-px bg-gray-200 dark:bg-gray-800" />

        <button
          type="button"
          className={baseBtn}
          disabled={disabled}
          onClick={() => editor.chain().focus().toggleBold().run()}
          data-active={isActive(() => editor.isActive("bold"))}
        >
          <svg viewBox="0 0 24 24" className="h-4 w-4" fill="none" stroke="currentColor" strokeWidth="2">
            <path strokeLinecap="round" strokeLinejoin="round" d="M6.5 12h7.5a3.5 3.5 0 0 1 0 7H6.5m0-7v7m11-7h-4a3.5 3.5 0 0 0 0 7h4" />
          </svg>
        </button>
        <button
          type="button"
          className={baseBtn}
          disabled={disabled}
          onClick={() => editor.chain().focus().toggleItalic().run()}
          data-active={isActive(() => editor.isActive("italic"))}
        >
          <svg viewBox="0 0 24 24" className="h-4 w-4" fill="none" stroke="currentColor" strokeWidth="2">
            <path strokeLinecap="round" strokeLinejoin="round" d="m19 4-7 14M9 4 2 20" />
          </svg>
        </button>
        <button
          type="button"
          className={baseBtn}
          disabled={disabled}
          onClick={() => editor.chain().focus().toggleUnderline().run()}
          data-active={isActive(() => editor.isActive("underline"))}
        >
          <svg viewBox="0 0 24 24" className="h-4 w-4" fill="none" stroke="currentColor" strokeWidth="2">
            <path strokeLinecap="round" strokeLinejoin="round" d="M6 4h12M7 21h10M6 12v7a3 3 0 0 0 3 3h6a3 3 0 0 0 3-3v-7" />
          </svg>
        </button>
        <button
          type="button"
          className={baseBtn}
          disabled={disabled}
          onClick={() => editor.chain().focus().toggleStrike().run()}
          data-active={isActive(() => editor.isActive("strike"))}
        >
          <svg viewBox="0 0 24 24" className="h-4 w-4" fill="none" stroke="currentColor" strokeWidth="2">
            <path strokeLinecap="round" strokeLinejoin="round" d="M16 4H9a3 3 0 0 0-2.83 4M14 12a4 4 0 0 1 0 4H6M4 12h16" />
          </svg>
        </button>
        <button
          type="button"
          className={baseBtn}
          disabled={disabled}
          onClick={() => editor.chain().focus().toggleHighlight().run()}
          data-active={isActive(() => editor.isActive("highlight"))}
        >
          <svg viewBox="0 0 24 24" className="h-4 w-4" fill="none" stroke="currentColor" strokeWidth="2">
            <path strokeLinecap="round" strokeLinejoin="round" d="m9 5 3 3-3 3m5 0 3-3-3-3m-6 9h10" />
            <path strokeLinecap="round" strokeLinejoin="round" d="M5 19h14" />
          </svg>
        </button>

        <div className="mx-1 h-5 w-px bg-gray-200 dark:bg-gray-800" />

        <button
          type="button"
          className={baseBtn}
          disabled={disabled}
          onClick={() => editor.chain().focus().toggleBulletList().run()}
          data-active={isActive(() => editor.isActive("bulletList"))}
        >
          <svg viewBox="0 0 24 24" className="h-4 w-4" fill="none" stroke="currentColor" strokeWidth="2">
            <path strokeLinecap="round" strokeLinejoin="round" d="M8 6h13M8 12h13M8 18h13M3 6h.01M3 12h.01M3 18h.01" />
          </svg>
        </button>
        <button
          type="button"
          className={baseBtn}
          disabled={disabled}
          onClick={() => editor.chain().focus().toggleOrderedList().run()}
          data-active={isActive(() => editor.isActive("orderedList"))}
        >
          <svg viewBox="0 0 24 24" className="h-4 w-4" fill="none" stroke="currentColor" strokeWidth="2">
            <path strokeLinecap="round" strokeLinejoin="round" d="M10 6h11M10 12h11M10 18h11M4 6h1v4M4 10h2l-3 3 4 4-3 3h2" />
          </svg>
        </button>
        <button
          type="button"
          className={baseBtn}
          disabled={disabled}
          onClick={() => editor.chain().focus().toggleBlockquote().run()}
          data-active={isActive(() => editor.isActive("blockquote"))}
        >
          <svg viewBox="0 0 24 24" className="h-4 w-4" fill="none" stroke="currentColor" strokeWidth="2">
            <path strokeLinecap="round" strokeLinejoin="round" d="M3 21c3 0 7-1 7-8V5c0-1.25-.756-2.017-2-2H4c-1.25 0-2 .75-2 1.972V11c0 1.25.75 2 2 2 1 0 1 1 1 2v1c0 1-1 2-2 2s-1 .008-1 1.031V21zm9 0c3 0 7-1 7-8V5c0-1.25-.757-2.017-2-2h-4c-1.25 0-2 .75-2 1.972V11c0 1.25.75 2 2 2h.75c0 2.25.25 4-2.75 4v3c0 1 0 1 1 1z" />
          </svg>
        </button>
        <button
          type="button"
          className={baseBtn}
          disabled={disabled}
          onClick={() => editor.chain().focus().toggleCodeBlock().run()}
          data-active={isActive(() => editor.isActive("codeBlock"))}
        >
          <svg viewBox="0 0 24 24" className="h-4 w-4" fill="none" stroke="currentColor" strokeWidth="2">
            <path strokeLinecap="round" strokeLinejoin="round" d="m16 18 6-6-6-6M8 6l-6 6 6 6" />
          </svg>
        </button>
        <button
          type="button"
          className={baseBtn}
          disabled={disabled}
          onClick={() => editor.chain().focus().setHorizontalRule().run()}
        >
          <svg viewBox="0 0 24 24" className="h-4 w-4" fill="none" stroke="currentColor" strokeWidth="2">
            <path strokeLinecap="round" strokeLinejoin="round" d="M3 12h18" />
          </svg>
        </button>

        <div className="mx-1 h-5 w-px bg-gray-200 dark:bg-gray-800" />

        <button
          type="button"
          className={baseBtn}
          disabled={disabled}
          onClick={() => editor.chain().focus().setTextAlign("left").run()}
          data-active={isActive(() => editor.isActive({ textAlign: "left" }))}
        >
          <svg viewBox="0 0 24 24" className="h-4 w-4" fill="none" stroke="currentColor" strokeWidth="2">
            <path strokeLinecap="round" strokeLinejoin="round" d="M3 6h18M3 12h12M3 18h16" />
          </svg>
        </button>
        <button
          type="button"
          className={baseBtn}
          disabled={disabled}
          onClick={() => editor.chain().focus().setTextAlign("center").run()}
          data-active={isActive(() => editor.isActive({ textAlign: "center" }))}
        >
          <svg viewBox="0 0 24 24" className="h-4 w-4" fill="none" stroke="currentColor" strokeWidth="2">
            <path strokeLinecap="round" strokeLinejoin="round" d="M4 6h16M7 12h10M5 18h14" />
          </svg>
        </button>
        <button
          type="button"
          className={baseBtn}
          disabled={disabled}
          onClick={() => editor.chain().focus().setTextAlign("right").run()}
          data-active={isActive(() => editor.isActive({ textAlign: "right" }))}
        >
          <svg viewBox="0 0 24 24" className="h-4 w-4" fill="none" stroke="currentColor" strokeWidth="2">
            <path strokeLinecap="round" strokeLinejoin="round" d="M3 6h18M9 12h12M5 18h16" />
          </svg>
        </button>

        <div className="mx-1 h-5 w-px bg-gray-200 dark:bg-gray-800" />

        <button
          type="button"
          className={baseBtn}
          disabled={disabled}
          onClick={() => inputRef.current?.click()}
        >
          <svg viewBox="0 0 24 24" className="h-4 w-4" fill="none" stroke="currentColor" strokeWidth="2">
            <path strokeLinecap="round" strokeLinejoin="round" d="M4 16l4.586-4.586a2 2 0 0 1 2.828 0L16 16m-2-2 1.586-1.586a2 2 0 0 1 2.828 0L20 14" />
            <rect x="3" y="3" width="18" height="18" rx="2" />
          </svg>
        </button>
        <input
          ref={inputRef}
          type="file"
          accept="image/png,image/jpeg,image/webp"
          className="hidden"
          disabled={disabled}
          onChange={(e) => {
            void insertImage(e.target.files?.[0]);
            e.target.value = "";
          }}
        />
      </div>

      <EditorContent editor={editor} />
    </div>
  );
};

export default ArticleRichTextEditor;
