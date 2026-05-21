package com.space.app.modules.merchant.service;

import com.space.app.modules.merchant.dto.LoveIndexView;
import com.space.app.modules.merchant.dto.ScoreView;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link ScoreCalculator} 单元测试：覆盖典型值、边界值与非法负数。
 */
class ScoreCalculatorTest {

    private final ScoreCalculator calculator = new ScoreCalculator();

    @Test
    void typical_24_20_20_16_yields_80_percent_each_total_80_level_8() {
        ScoreView score = calculator.toScoreView((short) 24, (short) 20, (short) 20, (short) 16);
        assertThat(score.safetyEnvironmentPercent()).isEqualTo(80);
        assertThat(score.businessRightsPercent()).isEqualTo(80);
        assertThat(score.experienceFriendlyPercent()).isEqualTo(80);
        assertThat(score.socialContributionPercent()).isEqualTo(80);

        LoveIndexView index = calculator.toLoveIndex((short) 24, (short) 20, (short) 20, (short) 16);
        assertThat(index.total()).isEqualTo(80);
        assertThat(index.level()).isEqualTo(8);
    }

    @Test
    void zero_scores_yield_zero_percent_total_zero_level_one() {
        ScoreView score = calculator.toScoreView((short) 0, (short) 0, (short) 0, (short) 0);
        assertThat(score.safetyEnvironmentPercent()).isZero();

        LoveIndexView index = calculator.toLoveIndex((short) 0, (short) 0, (short) 0, (short) 0);
        assertThat(index.total()).isZero();
        // ceil(0/10)=0 → clamp 到 1
        assertThat(index.level()).isEqualTo(1);
    }

    @Test
    void full_scores_yield_100_percent_each_total_100_level_10() {
        ScoreView score = calculator.toScoreView((short) 30, (short) 25, (short) 25, (short) 20);
        assertThat(score.safetyEnvironmentPercent()).isEqualTo(100);
        assertThat(score.businessRightsPercent()).isEqualTo(100);
        assertThat(score.experienceFriendlyPercent()).isEqualTo(100);
        assertThat(score.socialContributionPercent()).isEqualTo(100);

        LoveIndexView index = calculator.toLoveIndex((short) 30, (short) 25, (short) 25, (short) 20);
        assertThat(index.total()).isEqualTo(100);
        assertThat(index.level()).isEqualTo(10);
    }

    @Test
    void rounding_uses_half_up() {
        // 7 / 30 * 100 = 23.333… → 23
        assertThat(calculator.percent((short) 7, 30)).isEqualTo(23);
        // 8 / 30 * 100 = 26.666… → 27
        assertThat(calculator.percent((short) 8, 30)).isEqualTo(27);
    }

    @Test
    void level_boundaries() {
        // total = 1 → ceil(0.1)=1
        assertThat(calculator.toLoveIndex((short) 1, (short) 0, (short) 0, (short) 0).level()).isEqualTo(1);
        // total = 10 → ceil(1.0)=1
        assertThat(calculator.toLoveIndex((short) 10, (short) 0, (short) 0, (short) 0).level()).isEqualTo(1);
        // total = 11 → ceil(1.1)=2
        assertThat(calculator.toLoveIndex((short) 11, (short) 0, (short) 0, (short) 0).level()).isEqualTo(2);
        // total = 95 → ceil(9.5)=10
        assertThat(calculator.toLoveIndex((short) 30, (short) 25, (short) 25, (short) 15).level()).isEqualTo(10);
    }

    @Test
    void negative_raw_score_throws() {
        assertThatThrownBy(() -> calculator.percent((short) -1, 30))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> calculator.toLoveIndex((short) -1, (short) 0, (short) 0, (short) 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void non_positive_max_throws() {
        assertThatThrownBy(() -> calculator.percent((short) 5, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
