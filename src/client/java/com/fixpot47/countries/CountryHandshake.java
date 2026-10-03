package com.fixpot47.countries;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class CountryHandshake {
    private static final String HOST_NAME = "fixpot47";

    private static ClientPacketListener connection;
    private static CompletableFuture<String> lookup;
    private static String country;
    private static int ticks;
    private static int sends;

    private CountryHandshake() {
    }

    public static void tick(Minecraft minecraft) {
        ClientPacketListener currentConnection = minecraft.getConnection();

        if (minecraft.player == null || currentConnection == null) {
            if (connection != null) {
                reset();
            }
            return;
        }

        if (currentConnection != connection) {
            reset();
            connection = currentConnection;
            lookup = CountryLookup.lookup();
        }

        ticks++;

        if (country == null && lookup != null && lookup.isDone()) {
            country = lookup.getNow(null);

            if (country != null) {
                CountryState.put(minecraft.player.getUUID(), country);
            }
        }

        if (country == null) {
            return;
        }

        String localName = minecraft.player.getName().getString();
        if (HOST_NAME.equalsIgnoreCase(localName)) {
            return;
        }

        if ((sends == 0 && ticks >= 100) || (sends == 1 && ticks >= 700)) {
            sendCountry(currentConnection, minecraft.player.getUUID(), country);
            sends++;
        }
    }

    private static void sendCountry(ClientPacketListener connection, UUID uuid, String code) {
        connection.sendCommand(
                "msg " + HOST_NAME + " [CF:" + uuid + ":" + code.toUpperCase() + "]"
        );
    }

    private static void reset() {
        connection = null;
        lookup = null;
        country = null;
        ticks = 0;
        sends = 0;
        CountryState.clear();
    }
}
