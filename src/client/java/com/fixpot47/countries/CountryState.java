package com.fixpot47.countries;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CountryState {
    private static final Map<UUID, String> COUNTRIES = new ConcurrentHashMap<>();

    private CountryState() {
    }

    public static void clear() {
        COUNTRIES.clear();
    }

    public static void put(UUID uuid, String countryCode) {
        if (uuid == null || countryCode == null) {
            return;
        }

        String normalized = countryCode.toLowerCase(Locale.ROOT);
        if (CountryGlyphs.supports(normalized)) {
            COUNTRIES.put(uuid, normalized);
            CountryDirectory.rememberUuid(uuid, normalized);
        }
    }

    public static String get(UUID uuid) {
        return COUNTRIES.get(uuid);
    }

    public static boolean consumeMarker(String text) {
        if (text == null) {
            return false;
        }

        int start = text.indexOf("[CF:");
        if (start < 0) {
            return false;
        }

        int end = text.indexOf(']', start);
        if (end < 0) {
            return false;
        }

        String payload = text.substring(start + 4, end);
        String[] parts = payload.split(":", 2);
        if (parts.length != 2 || parts[1].length() != 2) {
            return false;
        }

        try {
            put(UUID.fromString(parts[0]), parts[1]);
            return true;
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }
}
