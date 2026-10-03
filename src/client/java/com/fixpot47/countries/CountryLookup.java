package com.fixpot47.countries;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public final class CountryLookup {
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private CountryLookup() {
    }

    public static CompletableFuture<String> lookup() {
        return CompletableFuture.supplyAsync(() -> {
            String country = lookupIpApi();

            if (country == null) {
                country = lookupCloudflare();
            }

            if (country == null) {
                String localeCountry = Locale.getDefault().getCountry();
                country = normalize(localeCountry);
            }

            return country;
        });
    }

    private static String lookupIpApi() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://ipapi.co/country/"))
                    .timeout(Duration.ofSeconds(6))
                    .header("User-Agent", "Countries-Minecraft-Mod/0.1")
                    .GET()
                    .build();

            HttpResponse<String> response = HTTP.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return normalize(response.body());
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    private static String lookupCloudflare() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://www.cloudflare.com/cdn-cgi/trace"))
                    .timeout(Duration.ofSeconds(6))
                    .header("User-Agent", "Countries-Minecraft-Mod/0.1")
                    .GET()
                    .build();

            HttpResponse<String> response = HTTP.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                for (String line : response.body().split("\\R")) {
                    if (line.startsWith("loc=")) {
                        return normalize(line.substring(4));
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    private static String normalize(String raw) {
        if (raw == null) {
            return null;
        }

        String code = raw.trim().toLowerCase(Locale.ROOT);
        if (code.length() != 2 || !Character.isLetter(code.charAt(0)) || !Character.isLetter(code.charAt(1))) {
            return null;
        }

        return CountryGlyphs.supports(code) ? code : null;
    }
}
