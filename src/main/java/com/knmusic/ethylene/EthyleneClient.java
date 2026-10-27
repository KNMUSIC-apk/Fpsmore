package com.knmusic.ethylene;

import com.knmusic.ethylene.config.EthyleneConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EthyleneClient implements ClientModInitializer {

    public static final Logger LOG = LoggerFactory.getLogger("ethylene");

    private static EthyleneConfig config = new EthyleneConfig();

    public static EthyleneConfig config() {
        return config;
    }

    @Override
    public void onInitializeClient() {
        config = EthyleneConfig.load(FabricLoader.getInstance().getConfigDir().resolve("ethylene.json"));
        LOG.info("Ethylene đã tải cấu hình: LOD={}, giữ {} vòng gần, mob blur={}%, giảm chuyển động={}",
                config.lodEnabled,
                config.fullDetailChunks,
                config.mobBlurPercent,
                config.reduceMobMotion);
    }
}
