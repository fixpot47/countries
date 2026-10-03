package com.fixpot47.countries.mixin.client;

import com.fixpot47.countries.CountryState;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin {
    @Inject(method = "addMessage", at = @At("HEAD"), cancellable = true)
    private void countries$hideHandshake(
            Component message,
            MessageSignature signature,
            GuiMessageSource source,
            GuiMessageTag tag,
            CallbackInfo ci
    ) {
        if (CountryState.consumeMarker(message.getString())) {
            ci.cancel();
        }
    }
}
