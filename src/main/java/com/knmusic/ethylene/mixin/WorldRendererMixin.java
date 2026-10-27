package com.knmusic.ethylene.render;

import com.knmusic.ethylene.EthyleneClient;
import com.knmusic.ethylene.config.EthyleneConfig;
import com.knmusic.ethylene.mob.MobDensityPolicy;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Box;

public final class MobRenderOptimizer {
    private MobRenderOptimizer() {
    }

    public static void updateRuntimeState() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.world == null || client.player == null) {
            return;
        }

        EthyleneConfig config = EthyleneClient.config();
        if (!config.mobBlurEnabled) {
            return;
        }

        int nearby = countNearbyMobs(client.world, client.player, config.mobDensityRadius);
        float alpha = mobAlphaFor(nearby);
        if (alpha < 1.0F) {
            // Runtime hook point: when the mob density exceeds the threshold, the renderer can apply alpha and
            // animation tuning to the render pass without affecting the surrounding scene.
        }
    }

    public static int countNearbyMobs(ClientWorld world, Entity center, int radius) {
        if (world == null || center == null) {
            return 0;
        }

        double x = center.getX();
        double y = center.getY();
        double z = center.getZ();
        Box bounds = new Box(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius);

        return world.getEntitiesByClass(
                Entity.class,
                bounds,
                entity -> entity != center && entity.isAlive() && entity instanceof MobEntity
        ).size();
    }

    public static float mobAlphaFor(int nearbyMobs) {
        EthyleneConfig config = EthyleneClient.config();
        if (!config.mobBlurEnabled) {
            return 1.0F;
        }

        double blur = MobDensityPolicy.blurAmount(nearbyMobs, config.mobDensityThreshold, config.mobBlurPercent);
        return (float) Math.max(0.0, 1.0 - blur);
    }

    public static boolean shouldReduceMotionFor(int nearbyMobs) {
        EthyleneConfig config = EthyleneClient.config();
        return MobDensityPolicy.shouldReduceMotion(nearbyMobs, config.mobDensityThreshold, config.reduceMobMotion);
    }
}
