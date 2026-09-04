package com.space.app.common.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RichTextImagesTest {

    // @scenario: file/objectKey 两段式生命周期与绑定校验#富文本内联小图放行
    @Test
    void dataUrlPassThroughAndObjectKeySigned() {
        String html = "<img src=\"data:image/gif;base64,R0lGODlh\"><img src=\"bound/a.png\">";
        String out = RichTextImages.rewriteSrc(html, src -> "https://cdn/" + src);
        assertThat(out).isEqualTo("<img src=\"data:image/gif;base64,R0lGODlh\"><img src=\"https://cdn/bound/a.png\">");
        assertThat(RichTextImages.rewriteSrc(null, src -> "X")).isNull();
    }
}
