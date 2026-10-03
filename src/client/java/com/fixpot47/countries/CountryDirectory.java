package com.fixpot47.countries;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public final class CountryDirectory {
    private static final URI REMOTE_DIRECTORY = URI.create(
            "https://raw.githubusercontent.com/fixpot47/countries/main/players.json"
    );

    private static final Path LOCAL_FILE = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("countries.json");

    private static final Path CACHE_FILE = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("countries-cache.json");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private static volatile Directory remote = Directory.EMPTY;
    private static volatile Directory local = Directory.EMPTY;

    private static final Map<UUID, String> learnedUuids = new ConcurrentHashMap<>();

    private static CompletableFuture<Directory> remoteRequest;
    private static int ticks;
    private static boolean initialized;
    private static long localModified = Long.MIN_VALUE;

    private CountryDirectory() {
    }

    public static void tick() {
        if (!initialized) {
            initialized = true;
            loadCache();
            loadLocal(true);
            refreshRemote();
        }

        ticks++;

        if (ticks % 100 == 0) {
            loadLocal(false);
        }

        if (ticks % 1200 == 0 && remoteRequest == null) {
            refreshRemote();
        }

        if (remoteRequest != null && remoteRequest.isDone()) {
            Directory result = remoteRequest.getNow(null);
            remoteRequest = null;

            if (result != null) {
                remote = result;
            }
        }
    }

    public static String resolve(UUID uuid, String playerName) {
        String country = lookup(local, uuid, playerName);
        if (country != null) {
            return country;
        }

        country = lookup(remote, uuid, playerName);
        if (country != null) {
            return country;
        }

        if (uuid != null) {
            country = learnedUuids.get(uuid);
            if (country != null) {
                return country;
            }
        }

        return CountryState.get(uuid);
    }

    public static void rememberUuid(UUID uuid, String countryCode) {
        if (uuid == null) {
            return;
        }

        String normalized = normalizeCountry(countryCode);
        if (normalized == null) {
            return;
        }

        String previous = learnedUuids.put(uuid, normalized);
        if (!normalized.equals(previous)) {
            saveCache();
        }
    }

    private static String lookup(Directory directory, UUID uuid, String playerName) {
        if (uuid != null) {
            String byUuid = directory.byUuid().get(uuid);
            if (byUuid != null) {
                return byUuid;
            }
        }

        if (playerName != null) {
            return directory.byName().get(playerName.toLowerCase(Locale.ROOT));
        }

        return null;
    }

    private static void refreshRemote() {
        remoteRequest = CompletableFuture.supplyAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(REMOTE_DIRECTORY)
                        .timeout(Duration.ofSeconds(8))
                        .header("User-Agent", "Countries-Minecraft-Mod/0.2")
                        .header("Cache-Control", "no-cache")
                        .GET()
                        .build();

                HttpResponse<String> response = HTTP.send(
                        request,
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );

                if (response.statusCode() >= 200 && response.statusCode() < 300) {
                    return parseDirectory(response.body());
                }
            } catch (Exception ignored) {
            }

            return null;
        });
    }

    private static void loadLocal(boolean force) {
        try {
            Files.createDirectories(LOCAL_FILE.getParent());

            if (!Files.exists(LOCAL_FILE)) {
                Files.writeString(LOCAL_FILE, "{}\n", StandardCharsets.UTF_8);
            }

            long modified = Files.getLastModifiedTime(LOCAL_FILE).toMillis();
            if (!force && modified == localModified) {
                return;
            }

            localModified = modified;
            local = parseDirectory(Files.readString(LOCAL_FILE, StandardCharsets.UTF_8));
        } catch (Exception ignored) {
        }
    }

    private static Directory parseDirectory(String json) {
        try {
            JsonElement root = GSON.fromJson(json, JsonElement.class);
            if (root == null || !root.isJsonObject()) {
                return Directory.EMPTY;
            }

            Map<String, String> names = new HashMap<>();
            Map<UUID, String> uuids = new HashMap<>();

            JsonObject object = root.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                if (!entry.getValue().isJsonPrimitive()
                        || !entry.getValue().getAsJsonPrimitive().isString()) {
                    continue;
                }

                String country = normalizeCountry(entry.getValue().getAsString());
                if (country == null) {
                    continue;
                }

                String key = entry.getKey().trim();
                try {
                    uuids.put(UUID.fromString(key), country);
                } catch (IllegalArgumentException ignored) {
                    if (!key.isEmpty()) {
                        names.put(key.toLowerCase(Locale.ROOT), country);
                    }
                }
            }

            return new Directory(Map.copyOf(names), Map.copyOf(uuids));
        } catch (Exception ignored) {
            return Directory.EMPTY;
        }
    }

    private static String normalizeCountry(String raw) {
        if (raw == null) {
            return null;
        }

        String code = raw.trim().toLowerCase(Locale.ROOT);
        if (code.length() != 2
                || !Character.isLetter(code.charAt(0))
                || !Character.isLetter(code.charAt(1))
                || !CountryGlyphs.supports(code)) {
            return null;
        }

        return code;
    }

    private static void loadCache() {
        try {
            Files.createDirectories(CACHE_FILE.getParent());

            if (!Files.exists(CACHE_FILE)) {
                return;
            }

            Directory cache = parseDirectory(Files.readString(CACHE_FILE, StandardCharsets.UTF_8));
            learnedUuids.putAll(cache.byUuid());
        } catch (Exception ignored) {
        }
    }

    private static synchronized void saveCache() {
        try {
            Files.createDirectories(CACHE_FILE.getParent());

            JsonObject object = new JsonObject();
            learnedUuids.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> object.addProperty(
                            entry.getKey().toString(),
                            entry.getValue().toUpperCase(Locale.ROOT)
                    ));

            Files.writeString(
                    CACHE_FILE,
                    GSON.toJson(object) + "\n",
                    StandardCharsets.UTF_8
            );
        } catch (Exception ignored) {
        }
    }

    private record Directory(
            Map<String, String> byName,
            Map<UUID, String> byUuid
    ) {
        private static final Directory EMPTY = new Directory(Map.of(), Map.of());
    }
}
