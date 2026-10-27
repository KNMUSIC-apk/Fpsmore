package com.knmusic.ethylene.mixin;

import com.knmusic.ethylene.render.MobRenderOptimizer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void ethylene$beforeEntityRender(CallbackInfo ci) {
        MobRenderOptimizer.updateRuntimeState();
    }
}
