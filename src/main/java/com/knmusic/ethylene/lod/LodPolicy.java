package com.knmusic.ethylene.lod;

/**
 * Logic thuần (không phụ thuộc Minecraft) cho chunk LOD.
 *
 * Quy ước:
 * - renderDistance: số chunk render, tính từ chunk của người chơi.
 * - chunkDistance: khoảng cách Chebyshev max(|dx|, |dz|) từ chunk người chơi tới chunk đó.
 * - lodChunks: số vòng chunk ngoài cùng được giảm chi tiết.
 *
 * Ví dụ với mặc định (fullDetailChunks = 2, step = 1.0):
 * - renderDistance 4 -> 2 vòng ngoài giảm chi tiết.
 * - renderDistance 5 -> 3 vòng ngoài giảm chi tiết.
 */
public final class LodPolicy {

    /** Số px của một mặt block ở chi tiết gốc. */
    public static final int FULL_TEXELS = 32;

    private static final double EPS = 1e-9;

    private LodPolicy() {
    }

    public static int chebyshev(int dx, int dz) {
        return Math.max(Math.abs(dx), Math.abs(dz));
    }

    /**
     * Số vòng chunk LOD.
     *
     * @param renderDistance      khoảng cách render (chunk)
     * @param fullDetailChunks    số vòng gần người chơi luôn giữ chi tiết đầy đủ
     * @param step                số vòng LOD thêm cho mỗi chunk render tăng thêm (mặc định 1.0)
     */
    public static int lodChunkCount(int renderDistance, int fullDetailChunks, double step) {
        int available = renderDistance - fullDetailChunks;
        if (available <= 0 || step <= 0.0) {
            return 0;
        }
        int count = (int) Math.floor(available * step + EPS);
        return Math.min(available, count);
    }

    /** Chunk ở khoảng cách này có nằm trong vùng LOD không. */
    public static boolean isLod(int chunkDistance, int renderDistance, int lodChunks) {
        return lodChunks > 0 && chunkDistance > renderDistance - lodChunks;
    }

    /**
     * Số px mỗi mặt block cho chunk ở khoảng cách này.
     * Kết quả luôn là luỹ thừa của 2 trong khoảng 1..32.
     *
     * @param reduction mức giảm, 0.0 = không giảm, 1.0 = vòng xa nhất còn 1 px
     */
    public static int texelsFor(int chunkDistance, int renderDistance, int lodChunks, double reduction) {
        if (!isLod(chunkDistance, renderDistance, lodChunks)) {
            return FULL_TEXELS;
        }
        int ring = chunkDistance - (renderDistance - lodChunks); // 1..lodChunks
        double fraction = ring / (double) lodChunks;
        double r = Math.max(0.0, Math.min(1.0, reduction));
        double raw = FULL_TEXELS * (1.0 - r * fraction);
        int px = (int) Math.floor(raw + EPS);
        if (px < 1) {
            return 1;
        }
        return Integer.highestOneBit(Math.min(px, FULL_TEXELS));
    }
}
