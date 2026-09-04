package com.loves.space.common.util;

import java.net.URI;
import java.util.Base64;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 富文本 HTML 内 {@code <img src>} 的统一改写：
 * 保存时把 src 归一为 objectKey 并绑定，读取时把 src 换成签名 URL。
 * <p>HTML 由我方编辑器产出、结构可控。
 */
// ponytail: 正则改写 img src，编辑器产出可控；引入 HTML parser 待格式失控再说
public final class RichTextImages {

    private static final Pattern IMG_SRC =
            Pattern.compile("(<img\\b[^>]*?\\bsrc\\s*=\\s*\")([^\"]+)(\")", Pattern.CASE_INSENSITIVE);

    /** 内联小图（表情包）上限：base64 解码后字节数。 */
    public static final int INLINE_MAX_BYTES = 3 * 1024;

    private static final Pattern INLINE_DATA_URL =
            Pattern.compile("^data:image/(png|jpeg|webp|gif);base64,([A-Za-z0-9+/=\\s]+)$", Pattern.CASE_INSENSITIVE);

    private RichTextImages() {
    }

    /**
     * 对 HTML 内每个 img src 应用转换函数；null/空白原样返回。
     * <p>data URL 走「内联小图」规则：白名单 MIME 且解码后 ≤ {@link #INLINE_MAX_BYTES} 则原样保留、不调用 fn；
     * 其余 data URL 一律拒绝（与 objectKey 校验同一文案）。
     */
    public static String rewriteSrc(String html, UnaryOperator<String> fn) {
        if (html == null || html.isBlank()) {
            return html;
        }
        Matcher m = IMG_SRC.matcher(html);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            String src = m.group(2);
            if (src.regionMatches(true, 0, "data:", 0, 5)) {
                if (!isInlineImage(src)) {
                    throw new IllegalArgumentException("图片对象不可用");
                }
                continue;
            }
            m.appendReplacement(sb, Matcher.quoteReplacement(m.group(1) + fn.apply(src) + m.group(3)));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /** 是否为可内联的小图 data URL：白名单 MIME 且 base64 解码后 ≤ 3 KB。 */
    public static boolean isInlineImage(String src) {
        if (src == null) {
            return false;
        }
        Matcher m = INLINE_DATA_URL.matcher(src);
        if (!m.matches()) {
            return false;
        }
        try {
            return Base64.getMimeDecoder().decode(m.group(2)).length <= INLINE_MAX_BYTES;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 把 src 归一为 objectKey：编辑器回传的可能是 objectKey，也可能是编辑预览用的签名 URL——
     * 后者取 URL path（去掉前导 / 与 query）还原成 objectKey。
     */
    public static String normalizeToObjectKey(String src) {
        if (src == null || !src.matches("(?i)^https?://.*")) {
            return src;
        }
        String path = URI.create(src).getPath();
        return path == null ? src : (path.startsWith("/") ? path.substring(1) : path);
    }
}
