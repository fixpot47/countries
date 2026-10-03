package com.fixpot47.countries;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class CountryNames {
    private static final Map<String, String> ALIASES = new HashMap<>();
    private static final Map<String, String> CANONICAL = new HashMap<>();

    static {
        Locale[] languages = {
                Locale.ENGLISH,
                Locale.forLanguageTag("tr"),
                Locale.GERMAN,
                Locale.forLanguageTag("ru")
        };

        for (String code : Locale.getISOCountries()) {
            String lowerCode = code.toLowerCase(Locale.ROOT);
            if (!CountryGlyphs.supports(lowerCode)) {
                continue;
            }

            register(lowerCode, lowerCode);

            Locale countryLocale = Locale.of("", code);
            for (Locale language : languages) {
                register(countryLocale.getDisplayCountry(language), lowerCode);
            }

            CANONICAL.put(lowerCode, countryLocale.getDisplayCountry(Locale.ENGLISH));
        }

        alias("Turkey", "tr");
        alias("Turkiye", "tr");
        alias("Türkiye", "tr");
        alias("Turkei", "tr");
        alias("Türkei", "tr");

        alias("Germany", "de");
        alias("Deutschland", "de");
        alias("Duetchland", "de");
        alias("Deutchland", "de");

        alias("USA", "us");
        alias("US", "us");
        alias("U.S.A.", "us");
        alias("United States", "us");
        alias("United States of America", "us");
        alias("America", "us");

        alias("Russia", "ru");
        alias("Russian Federation", "ru");
        alias("Россия", "ru");

        alias("Serbia", "rs");
        alias("Srbija", "rs");
        alias("Сербия", "rs");

        alias("UK", "gb");
        alias("United Kingdom", "gb");
        alias("Great Britain", "gb");
        alias("Britain", "gb");

        alias("South Korea", "kr");
        alias("Korea", "kr");
        alias("North Korea", "kp");

        alias("Kosovo", "xk");

        CANONICAL.put("tr", "Turkey");
        CANONICAL.put("de", "Germany");
        CANONICAL.put("us", "USA");
        CANONICAL.put("ru", "Russia");
        CANONICAL.put("rs", "Serbia");
        CANONICAL.put("gb", "United Kingdom");
        CANONICAL.put("kr", "South Korea");
        CANONICAL.put("kp", "North Korea");
        CANONICAL.put("xk", "Kosovo");
    }

    private CountryNames() {
    }

    public static String resolve(String input) {
        if (input == null) {
            return null;
        }

        return ALIASES.get(normalize(input));
    }

    public static String canonicalName(String countryCode) {
        if (countryCode == null) {
            return null;
        }

        String code = countryCode.toLowerCase(Locale.ROOT);
        return CANONICAL.getOrDefault(code, code.toUpperCase(Locale.ROOT));
    }

    private static void alias(String name, String countryCode) {
        register(name, countryCode);
    }

    private static void register(String name, String countryCode) {
        if (name == null || name.isBlank()) {
            return;
        }

        ALIASES.put(normalize(name), countryCode.toLowerCase(Locale.ROOT));
    }

    private static String normalize(String value) {
        String normalized = Normalizer.normalize(value.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT);

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < normalized.length(); i++) {
            char c = normalized.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                result.append(c);
            }
        }

        return result.toString();
    }
}
