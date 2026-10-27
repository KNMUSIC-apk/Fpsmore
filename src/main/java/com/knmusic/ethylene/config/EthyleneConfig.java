package com.knmusic.ethylene.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cấu hình Ethylene, lưu tại config/ethylene.json.
 * Mọi trường có giá trị mặc định; file thiếu trường nào thì dùng mặc định cho trường đó.
 */
public final class EthyleneConfig {

    private static final Logger LOG = LoggerFactory.getLogger("ethylene");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // ---- Chunk LOD ----
    /** Bật/tắt giảm chi tiết chunk ở xa. */
    public boolean lodEnabled = true;
    /** Số vòng chunk gần người chơi luôn giữ chi tiết đầy đủ. */
    public int fullDetailChunks = 2;
    /** Số vòng LOD thêm cho mỗi chunk render tăng thêm. Mặc định 1.0; có thể đặt 0.5, 0.3, 0.2... */
    public double lodChunkStep = 1.0;
    /** Mức giảm pixel: 0.0 = không giảm, 1.0 = vòng xa nhất còn 1 px mỗi block. */
    public double lodReduction = 1.0;

    // ---- Mob render ----
    /** Bật/tắt làm mờ mob khi quá đông. */
    public boolean mobBlurEnabled = true;
    /** Mức làm mờ tối đa, theo phần trăm (0..100). */
    public int mobBlurPercent = 50;
    /** Số mob trong bán kính trên mức này thì bắt đầu giảm tải. */
    public int mobDensityThreshold = 40;
    /** Bán kính (block) để đếm mật độ mob. */
    public int mobDensityRadius = 32;
    /** Bật/tắt giảm chuyển động của mob khi quá đông. */
    public boolean reduceMobMotion = true;

    /** Đọc cấu hình từ file. Không có file thì tạo file mặc định. Lỗi thì dùng mặc định. */
    public static EthyleneConfig load(Path file) {
        EthyleneConfig config;
        try {
            if (Files.exists(file)) {
                try (Reader reader = Files.newBufferedReader(file)) {
                    EthyleneConfig read = GSON.fromJson(reader, EthyleneConfig.class);
                    config = read != null ? read : new EthyleneConfig();
                }
            } else {
                config = new EthyleneConfig();
            }
            config.clampValues();
            config.save(file);
            return config;
        } catch (IOException | RuntimeException e) {
            LOG.error("Không đọc được {}, dùng cấu hình mặc định", file, e);
            return new EthyleneConfig();
        }
    }

    public void save(Path file) throws IOException {
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (Writer writer = Files.newBufferedWriter(file)) {
            GSON.toJson(this, writer);
        }
    }

    /** Đưa mọi giá trị về khoảng hợp lệ. */
    public void clampValues() {
        fullDetailChunks = clampInt(fullDetailChunks, 0, 32);
        lodChunkStep = Math.max(0.0, Math.min(4.0, lodChunkStep));
        lodReduction = Math.max(0.0, Math.min(1.0, lodReduction));
        mobBlurPercent = clampInt(mobBlurPercent, 0, 100);
        mobDensityThreshold = clampInt(mobDensityThreshold, 1, 1000);
        mobDensityRadius = clampInt(mobDensityRadius, 8, 128);
    }

    private static int clampInt(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
