package com.fixpot47.countries;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;

import java.util.UUID;

public final class CountryCommand {
    private CountryCommand() {
    }

    public static boolean handle(ClientPacketListener connection, String command) {
        if (command == null) {
            return false;
        }

        String trimmed = command.trim();
        if (!trimmed.equalsIgnoreCase("country")
                && !trimmed.toLowerCase(java.util.Locale.ROOT).startsWith("country ")) {
            return false;
        }

        String args = trimmed.length() <= 7 ? "" : trimmed.substring(7).trim();
        Minecraft minecraft = Minecraft.getInstance();

        if (args.isEmpty()) {
            feedback(minecraft, "Usage: /country <player> <country>");
            return true;
        }

        int space = args.indexOf(' ');
        if (space <= 0 || space >= args.length() - 1) {
            feedback(minecraft, "Usage: /country <player> <country>");
            return true;
        }

        String playerName = args.substring(0, space).trim();
        String countryInput = args.substring(space + 1).trim();
        String countryCode = CountryNames.resolve(countryInput);

        if (countryCode == null) {
            feedback(minecraft, "Unknown country: " + countryInput);
            return true;
        }

        PlayerInfo info = connection.getPlayerInfoIgnoreCase(playerName);
        UUID uuid = info == null ? null : info.getProfile().id();
        String realName = info == null ? playerName : info.getProfile().name();

        String saved = CountryDirectory.assign(realName, uuid, countryInput);
        if (saved == null) {
            feedback(minecraft, "Could not save country for " + realName);
            return true;
        }

        String status = uuid == null ? "saved by name" : "saved by name + UUID";
        feedback(
                minecraft,
                realName + " -> " + CountryNames.canonicalName(saved) + " (" + status + ")"
        );

        return true;
    }

    private static void feedback(Minecraft minecraft, String text) {
        if (minecraft.gui != null) {
            minecraft.gui.getChat().addClientSystemMessage(
                    Component.literal("[Countries] " + text)
            );
        }
    }
}
