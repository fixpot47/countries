package com.fixpot47.countries.mixin.client;

import com.fixpot47.countries.CountryCommand;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
    @Inject(method = "sendCommand", at = @At("HEAD"), cancellable = true)
    private void countries$localCountryCommand(String command, CallbackInfo ci) {
        if (CountryCommand.handle((ClientPacketListener) (Object) this, command)) {
            ci.cancel();
        }
    }
}
