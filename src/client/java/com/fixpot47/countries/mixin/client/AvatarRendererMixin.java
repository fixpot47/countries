package com.fixpot47.countries.mixin.client;

import com.fixpot47.countries.CountryDirectory;
import com.fixpot47.countries.CountryGlyphs;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin {
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void countries$appendWorldFlag(
            Avatar player,
            AvatarRenderState state,
            float partialTick,
            CallbackInfo ci
    ) {
        if (state.nameTag == null) {
            return;
        }

        String username = player.getName().getString();
        String country = CountryDirectory.resolve(player.getUUID(), username);
        String glyph = CountryGlyphs.glyph(country);

        if (glyph == null) {
            return;
        }

        Component flag = Component.literal(" " + glyph).setStyle(
                Style.EMPTY
                        .withColor(0xFFFFFF)
                        .withBold(false)
                        .withItalic(false)
                        .withUnderlined(false)
                        .withStrikethrough(false)
                        .withObfuscated(false)
        );

        state.nameTag = countries$insertAfterUsername(state.nameTag, username, flag);
    }

    private static Component countries$insertAfterUsername(
            Component displayName,
            String username,
            Component flag
    ) {
        if (username == null || username.isEmpty()) {
            return displayName.copy().append(flag);
        }

        var parts = displayName.toFlatList();
        int cursor = 0;
        int exactPart = -1;

        for (Component part : parts) {
            if (part.getString().equals(username)) {
                exactPart = cursor;
            }
            cursor += part.getString().length();
        }

        int start = exactPart >= 0
                ? exactPart
                : countries$usernameStart(displayName.getString(), username);

        if (start < 0) {
            return displayName.copy().append(flag);
        }

        int insertion = start + username.length();
        MutableComponent rebuilt = Component.empty();
        cursor = 0;
        boolean inserted = false;

        for (Component part : parts) {
            String text = part.getString();
            int end = cursor + text.length();

            if (!inserted && insertion >= cursor && insertion <= end) {
                int split = insertion - cursor;

                if (split > 0) {
                    rebuilt.append(
                            Component.literal(text.substring(0, split))
                                    .setStyle(part.getStyle())
                    );
                }

                rebuilt.append(flag);

                if (split < text.length()) {
                    rebuilt.append(
                            Component.literal(text.substring(split))
                                    .setStyle(part.getStyle())
                    );
                }

                inserted = true;
            } else {
                rebuilt.append(part.copy());
            }

            cursor = end;
        }

        return inserted ? rebuilt : displayName.copy().append(flag);
    }

    private static int countries$usernameStart(String rendered, String username) {
        int completeToken = -1;

        for (int from = 0; ; ) {
            int found = rendered.indexOf(username, from);
            if (found < 0) {
                return completeToken;
            }

            int end = found + username.length();
            boolean leftBoundary = found == 0
                    || !countries$isUsernameCharacter(rendered.charAt(found - 1));
            boolean rightBoundary = end == rendered.length()
                    || !countries$isUsernameCharacter(rendered.charAt(end));

            if (leftBoundary && rightBoundary) {
                completeToken = found;
            }

            from = found + 1;
        }
    }

    private static boolean countries$isUsernameCharacter(char c) {
        return c == '_'
                || (c >= '0' && c <= '9')
                || (c >= 'A' && c <= 'Z')
                || (c >= 'a' && c <= 'z');
    }
}
