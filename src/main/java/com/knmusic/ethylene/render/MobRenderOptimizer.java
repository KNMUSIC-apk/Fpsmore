package com.knmusic.ethylene.render;

import com.knmusic.ethylene.EthyleneClient;
import com.knmusic.ethylene.config.EthyleneConfig;
import com.knmusic.ethylene.mob.MobDensityPolicy;

public final class MobRenderOptimizer {
    private MobRenderOptimizer() {
    }

    public static void updateRuntimeState() {
        // Hook point for runtime mob blur and motion reduction.
        // Runtime decisions can be switched to per-entity logic later without changing the public API.
    }

    public static float mobAlphaFor(int nearbyMobs) {
        EthyleneConfig config = EthyleneClient.config();
        if (!config.mobBlurEnabled) {
            return 1.0f;
        }
        double blur = MobDensityPolicy.blurAmount(nearbyMobs, config.mobDensityThreshold, config.mobBlurPercent);
        return (float) Math.max(0.0, 1.0 - blur);
    }

    public static boolean shouldReduceMotionFor(int nearbyMobs) {
        EthyleneConfig config = EthyleneClient.config();
        return MobDensityPolicy.shouldReduceMotion(nearbyMobs, config.mobDensityThreshold, config.reduceMobMotion);
    }
}
