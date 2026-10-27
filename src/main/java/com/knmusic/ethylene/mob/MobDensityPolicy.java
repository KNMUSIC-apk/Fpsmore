package com.knmusic.ethylene.mob;

/**
 * Logic thuần (không phụ thuộc Minecraft) cho giảm tải mob khi mật độ quá cao.
 *
 * nearbyMobs là số mob trong bán kính cấu hình quanh người chơi.
 */
public final class MobDensityPolicy {

    private MobDensityPolicy() {
    }

    /**
     * Mức làm mờ mob, từ 0.0 (rõ hoàn toàn) tới maxBlurPercent / 100 (mờ nhất).
     * Tăng dần từ ngưỡng và đạt mức tối đa khi số mob gấp đôi ngưỡng.
     */
    public static double blurAmount(int nearbyMobs, int threshold, int maxBlurPercent) {
        if (threshold < 1 || nearbyMobs <= threshold) {
            return 0.0;
        }
        double over = Math.min(1.0, (nearbyMobs - threshold) / (double) threshold);
        int pct = Math.max(0, Math.min(100, maxBlurPercent));
        return over * pct / 100.0;
    }

    /** Có nên giảm chuyển động (animation) của mob hay không. */
    public static boolean shouldReduceMotion(int nearbyMobs, int threshold, boolean enabled) {
        return enabled && threshold >= 1 && nearbyMobs > threshold;
    }
}
