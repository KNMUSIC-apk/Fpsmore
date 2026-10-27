package com.knmusic.ethylene;

import com.knmusic.ethylene.lod.LodPolicy;
import com.knmusic.ethylene.mob.MobDensityPolicy;

/**
 * Kiểm tra logic thuần, chạy không cần Minecraft:
 *   javac -d out src/main/java/com/knmusic/ethylene/lod/LodPolicy.java \
 *         src/main/java/com/knmusic/ethylene/mob/MobDensityPolicy.java \
 *         src/test/java/com/knmusic/ethylene/EthyleneLogicTest.java
 *   java -cp out com.knmusic.ethylene.EthyleneLogicTest
 */
public final class EthyleneLogicTest {

    private static int passed = 0;

    public static void main(String[] args) {
        lodChunkCountMatchesSpec();
        lodBandsFollowSpec();
        mobBlurAndMotion();
        System.out.println("OK: " + passed + " nhóm kiểm tra đều đạt");
    }

    private static void lodChunkCountMatchesSpec() {
        // Yêu cầu: render 4 -> 2 vòng giảm chi tiết, render 5 -> 3 vòng.
        check(LodPolicy.lodChunkCount(4, 2, 1.0) == 2, "render 4 phải có 2 vòng LOD");
        check(LodPolicy.lodChunkCount(5, 2, 1.0) == 3, "render 5 phải có 3 vòng LOD");
        check(LodPolicy.lodChunkCount(2, 2, 1.0) == 0, "không có vòng ngoài thì không LOD");
        // Chỉnh bước: 0.5, 0.3, 0.2 mỗi chunk tăng thêm.
        check(LodPolicy.lodChunkCount(4, 2, 0.5) == 1, "step 0.5 render 4 -> 1 vòng");
        check(LodPolicy.lodChunkCount(5, 2, 0.5) == 1, "step 0.5 render 5 -> 1 vòng");
        check(LodPolicy.lodChunkCount(6, 2, 0.5) == 2, "step 0.5 render 6 -> 2 vòng");
        check(LodPolicy.lodChunkCount(5, 2, 0.3) == 0, "step 0.3 render 5 -> 0 vòng");
        check(LodPolicy.lodChunkCount(12, 2, 0.2) == 2, "step 0.2 render 12 -> 2 vòng");
        check(LodPolicy.lodChunkCount(10, 2, 0.1 * 3) == 2, "không lỗi làm tròn số thực");
        passed++;
    }

    private static void lodBandsFollowSpec() {
        // render 4, 2 vòng LOD: chunk cách 1-2 giữ 32 px, vòng 3 là 16 px, vòng 4 còn 1 px.
        check(LodPolicy.texelsFor(2, 4, 2, 1.0) == 32, "vòng gần vẫn 32 px");
        check(LodPolicy.texelsFor(3, 4, 2, 1.0) == 16, "vòng LOD đầu 16 px");
        check(LodPolicy.texelsFor(4, 4, 2, 1.0) == 1, "vòng LOD xa nhất 1 px");
        // Không giảm: mọi vòng vẫn 32 px.
        check(LodPolicy.texelsFor(4, 4, 2, 0.0) == 32, "reduction 0 không giảm pixel");
        // render 5, 3 vòng LOD: 16, 8, 1.
        check(LodPolicy.texelsFor(3, 5, 3, 1.0) == 16, "render 5 vòng 1 -> 16 px");
        check(LodPolicy.texelsFor(4, 5, 3, 1.0) == 8, "render 5 vòng 2 -> 8 px");
        check(LodPolicy.texelsFor(5, 5, 3, 1.0) == 1, "render 5 vòng 3 -> 1 px");
        // Giá trị luôn là luỹ thừa của 2 trong 1..32.
        for (int d = 1; d <= 16; d++) {
            for (int n = 1; n <= d; n++) {
                for (double r = 0.0; r <= 1.0; r += 0.25) {
                    int px = LodPolicy.texelsFor(d, d, n, r);
                    check(px >= 1 && px <= 32 && Integer.bitCount(px) == 1,
                            "px không hợp lệ: " + px + " với d=" + d + " n=" + n + " r=" + r);
                }
            }
        }
        passed++;
    }

    private static void mobBlurAndMotion() {
        check(MobDensityPolicy.blurAmount(40, 40, 50) == 0.0, "đúng ngưỡng thì chưa làm mờ");
        check(Math.abs(MobDensityPolicy.blurAmount(60, 40, 50) - 0.25) < 1e-9, "1.5x ngưỡng -> 25%");
        check(Math.abs(MobDensityPolicy.blurAmount(80, 40, 50) - 0.5) < 1e-9, "2x ngưỡng -> mức tối đa");
        check(Math.abs(MobDensityPolicy.blurAmount(500, 40, 50) - 0.5) < 1e-9, "vượt xa vẫn không quá mức tối đa");
        check(MobDensityPolicy.blurAmount(60, 40, 0) == 0.0, "tắt làm mờ bằng 0%");

        check(MobDensityPolicy.shouldReduceMotion(41, 40, true), "vượt ngưỡng thì giảm chuyển động");
        check(!MobDensityPolicy.shouldReduceMotion(41, 40, false), "tắt tuỳ chọn thì không giảm");
        check(!MobDensityPolicy.shouldReduceMotion(40, 40, true), "đúng ngưỡng thì không giảm");
        passed++;
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
