package com.loves.space.common.util;

import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RichTextImagesTest {

    // @scenario: activity/App 端活动查询#活动详情返回富文本
    @Test
    void rewritesEverySrcAndKeepsRestOfHtml() {
        String html = "<p>开头</p><img src=\"bound/a.png\"><div><img class=\"x\" src=\"bound/b.jpg\" alt=\"图\"></div>";
        String out = RichTextImages.rewriteSrc(html, src -> "https://cdn/" + src + "?sig=1");
        assertThat(out).isEqualTo(
                "<p>开头</p><img src=\"https://cdn/bound/a.png?sig=1\">"
                        + "<div><img class=\"x\" src=\"https://cdn/bound/b.jpg?sig=1\" alt=\"图\"></div>");
    }

    // @scenario: file/objectKey 两段式生命周期与绑定校验#已绑定图片重复提交不再复制
    @Test
    void noImageHtmlAndNullPassThrough() {
        assertThat(RichTextImages.rewriteSrc("<p>纯文字</p>", src -> "X")).isEqualTo("<p>纯文字</p>");
        assertThat(RichTextImages.rewriteSrc(null, src -> "X")).isNull();
        assertThat(RichTextImages.rewriteSrc("  ", src -> "X")).isEqualTo("  ");
    }

    // @scenario: file/objectKey 两段式生命周期与绑定校验#未绑定图片在业务保存时被绑定
    @Test
    void normalizeToObjectKeyStripsSignedUrlButKeepsPlainKey() {
        assertThat(RichTextImages.normalizeToObjectKey("https://bucket.oss.example.com/bound/a.png?Expires=1&Signature=x"))
                .isEqualTo("bound/a.png");
        assertThat(RichTextImages.normalizeToObjectKey("images/b.png")).isEqualTo("images/b.png");
        assertThat(RichTextImages.normalizeToObjectKey(null)).isNull();
    }

    private static String dataUrl(String mime, int bytes) {
        return "data:" + mime + ";base64," + Base64.getEncoder().encodeToString(new byte[bytes]);
    }

    // @scenario: file/objectKey 两段式生命周期与绑定校验#富文本内联小图放行
    @Test
    void inlineSmallImageKeptAsIsAndFnNotCalled() {
        String html = "<p>x</p><img src=\"" + dataUrl("image/gif", 2048) + "\"><img src=\"bound/a.png\">";
        String out = RichTextImages.rewriteSrc(html, src -> {
            assertThat(src).isEqualTo("bound/a.png");
            return "signed";
        });
        assertThat(out).isEqualTo("<p>x</p><img src=\"" + dataUrl("image/gif", 2048) + "\"><img src=\"signed\">");
        assertThat(RichTextImages.isInlineImage(dataUrl("image/png", RichTextImages.INLINE_MAX_BYTES))).isTrue();
    }

    // @scenario: file/objectKey 两段式生命周期与绑定校验#富文本内联图超限被拒绝
    @Test
    void inlineImageOverLimitRejected() {
        assertThat(RichTextImages.isInlineImage(dataUrl("image/png", RichTextImages.INLINE_MAX_BYTES + 1))).isFalse();
        assertThatThrownBy(() -> RichTextImages.rewriteSrc("<img src=\"" + dataUrl("image/png", 4096) + "\">", src -> src))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("图片对象不可用");
    }

    // @scenario: file/objectKey 两段式生命周期与绑定校验#富文本内联图类型不符被拒绝
    @Test
    void inlineImageWrongTypeRejected() {
        assertThat(RichTextImages.isInlineImage(dataUrl("image/svg+xml", 1024))).isFalse();
        assertThat(RichTextImages.isInlineImage("data:text/plain;base64,QUJD")).isFalse();
        assertThat(RichTextImages.isInlineImage("data:image/png;base64,@@@")).isFalse();
        assertThatThrownBy(() -> RichTextImages.rewriteSrc("<img src=\"" + dataUrl("image/svg+xml", 1024) + "\">", src -> src))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
