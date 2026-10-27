package com.knmusic.ethylene.render;

import com.knmusic.ethylene.EthyleneClient;
import com.knmusic.ethylene.config.EthyleneConfig;
import com.knmusic.ethylene.lod.LodPolicy;

public final class ChunkLodRenderer {
    private ChunkLodRenderer() {
    }

    public static void updateRuntimeState() {
        // Hook point for runtime chunk LOD evaluation.
        // The actual chunk-level render decision is handled by the mixin layer and the
        // LodPolicy logic in the same package.
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
