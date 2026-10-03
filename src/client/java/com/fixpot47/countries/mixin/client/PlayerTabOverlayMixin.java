package com.fixpot47.countries.mixin.client;

import com.fixpot47.countries.CountryDirectory;
import com.fixpot47.countries.CountryGlyphs;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(PlayerTabOverlay.class)
public abstract class PlayerTabOverlayMixin {
    @Inject(method = "getNameForDisplay", at = @At("RETURN"), cancellable = true)
    private void countries$appendFlag(
            PlayerInfo playerInfo,
            CallbackInfoReturnable<Component> cir
    ) {
        UUID uuid = playerInfo.getProfile().id();
        String name = playerInfo.getProfile().name();
        String country = CountryDirectory.resolve(uuid, name);
        String glyph = CountryGlyphs.glyph(country);

        if (glyph == null) {
            return;
        }

        cir.setReturnValue(
                cir.getReturnValue()
                        .copy()
                        .append(Component.literal(" " + glyph))
        );
    }
}
