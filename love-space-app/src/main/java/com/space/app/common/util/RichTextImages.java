package com.space.app.common.util;

import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 富文本 HTML 内 {@code <img src>} 的统一改写：src 持久化为 objectKey，读取时替换为签名 URL。
 * <p>HTML 由后台编辑器产出、结构可控。
 */
// ponytail: 正则改写 img src，编辑器产出可控；引入 HTML parser 待格式失控再说
public final class RichTextImages {

    private static final Pattern IMG_SRC =
            Pattern.compile("(<img\\b[^>]*?\\bsrc\\s*=\\s*\")([^\"]+)(\")", Pattern.CASE_INSENSITIVE);

    private RichTextImages() {
    }

    /** 对 HTML 内每个 img src 应用转换函数；null/空白原样返回。 */
    public static String rewriteSrc(String html, UnaryOperator<String> fn) {
        if (html == null || html.isBlank()) {
            return html;
        }
        Matcher m = IMG_SRC.matcher(html);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            m.appendReplacement(sb, Matcher.quoteReplacement(m.group(1) + fn.apply(m.group(2)) + m.group(3)));
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
