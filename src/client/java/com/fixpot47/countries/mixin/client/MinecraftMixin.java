package com.fixpot47.countries.mixin.client;

import com.fixpot47.countries.CountryHandshake;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void countries$tick(CallbackInfo ci) {
        CountryHandshake.tick((Minecraft) (Object) this);
    }
}
