package com.fixpot47.countries.mixin.client;

import com.fixpot47.countries.CountryGlyphs;
import com.fixpot47.countries.CountryState;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerTabOverlay.class)
public abstract class PlayerTabOverlayMixin {
    @Inject(method = "getNameForDisplay", at = @At("RETURN"), cancellable = true)
    private void countries$appendFlag(
            PlayerInfo playerInfo,
            CallbackInfoReturnable<Component> cir
    ) {
        String country = CountryState.get(playerInfo.getProfile().id());
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
