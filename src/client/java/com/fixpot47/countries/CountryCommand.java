package com.fixpot47.countries;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;

import java.util.UUID;

public final class CountryCommand {
    private CountryCommand() {
    }

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, buildContext) -> {
            dispatcher.register(
                    ClientCommands.literal("country")
                            .then(ClientCommands.argument("player", StringArgumentType.word())
                                    .then(ClientCommands.argument("country", StringArgumentType.greedyString())
                                            .executes(context -> {
                                                String playerName = StringArgumentType.getString(context, "player");
                                                String countryInput = StringArgumentType.getString(context, "country");
                                                return assign(playerName, countryInput);
                                            })))
                            .executes(context -> {
                                feedback("Usage: /country <player> <country>");
                                return 0;
                            })
            );
        });
    }

    private static int assign(String playerName, String countryInput) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientPacketListener connection = minecraft.getConnection();

        String countryCode = CountryNames.resolve(countryInput);
        if (countryCode == null) {
            feedback("Unknown country: " + countryInput);
            return 0;
        }

        PlayerInfo info = connection == null
                ? null
                : connection.getPlayerInfoIgnoreCase(playerName);

        UUID uuid = info == null ? null : info.getProfile().id();
        String realName = info == null ? playerName : info.getProfile().name();

        String saved = CountryDirectory.assign(realName, uuid, countryInput);
        if (saved == null) {
            feedback("Could not save country for " + realName);
            return 0;
        }

        String status = uuid == null ? "saved by name" : "saved by name + UUID";
        feedback(
                realName + " -> " + CountryNames.canonicalName(saved) + " (" + status + ")"
        );

        return 1;
    }

    private static void feedback(String text) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player != null) {
            minecraft.player.sendSystemMessage(
                    Component.literal("[Countries] " + text)
            );
        }
    }
}
