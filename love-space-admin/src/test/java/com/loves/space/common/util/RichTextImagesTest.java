package com.loves.space.common.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    void noImageHtmlAndNullPassThrough() {
        assertThat(RichTextImages.rewriteSrc("<p>纯文字</p>", src -> "X")).isEqualTo("<p>纯文字</p>");
        assertThat(RichTextImages.rewriteSrc(null, src -> "X")).isNull();
        assertThat(RichTextImages.rewriteSrc("  ", src -> "X")).isEqualTo("  ");
    }

    @Test
    void normalizeToObjectKeyStripsSignedUrlButKeepsPlainKey() {
        assertThat(RichTextImages.normalizeToObjectKey("https://bucket.oss.example.com/bound/a.png?Expires=1&Signature=x"))
                .isEqualTo("bound/a.png");
        assertThat(RichTextImages.normalizeToObjectKey("images/b.png")).isEqualTo("images/b.png");
        assertThat(RichTextImages.normalizeToObjectKey(null)).isNull();
    }
}
