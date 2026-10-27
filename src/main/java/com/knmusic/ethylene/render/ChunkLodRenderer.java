package com.knmusic.ethylene.render;

import com.knmusic.ethylene.EthyleneClient;
import com.knmusic.ethylene.config.EthyleneConfig;
import com.knmusic.ethylene.lod.LodPolicy;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

public final class ChunkLodRenderer {
    private ChunkLodRenderer() {
    }

    public static void updateRuntimeState() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.world == null || client.player == null) {
            return;
        }

        EthyleneConfig config = EthyleneClient.config();
        if (!config.lodEnabled) {
            return;
        }

        BlockPos playerPos = client.player.getBlockPos();
        int renderDistance = client.options.getViewDistance().getValue();
        int centerChunkX = playerPos.getX() >> 4;
        int centerChunkZ = playerPos.getZ() >> 4;
        int lodChunks = LodPolicy.lodChunkCount(renderDistance, config.fullDetailChunks, config.lodChunkStep);
        int distance = LodPolicy.chebyshev(centerChunkX, centerChunkZ);
        int texels = LodPolicy.texelsFor(distance, renderDistance, lodChunks, config.lodReduction);

        if (texels < LodPolicy.FULL_TEXELS) {
            // Runtime hook point: the chunk LOD system has decided the current render ring is being reduced.
            // The actual chunk render adjustment can be applied here once the final mapping target is confirmed.
        }
    }

    public static int texelScaleForChunk(int chunkX, int chunkZ, int renderDistance) {
        EthyleneConfig config = EthyleneClient.config();
        if (!config.lodEnabled) {
            return LodPolicy.FULL_TEXELS;
        }

        int lodChunks = LodPolicy.lodChunkCount(renderDistance, config.fullDetailChunks, config.lodChunkStep);
        if (lodChunks <= 0) {
            return LodPolicy.FULL_TEXELS;
        }

        int distance = LodPolicy.chebyshev(chunkX, chunkZ);
        return LodPolicy.texelsFor(distance, renderDistance, lodChunks, config.lodReduction);
    }
}
