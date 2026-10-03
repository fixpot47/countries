package com.fixpot47.countries;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class CountryNames {
    private static final Map<String, String> ALIASES = new HashMap<>();
    private static final Map<String, String> CANONICAL = new HashMap<>();
    private static final List<String> SUGGESTIONS = new ArrayList<>();

    private static final String RAW_SUGGESTIONS = """
Abkhazia|xab
Afghanistan|af
Albania|al
Algeria|dz
Andorra|ad
Angola|ao
Antigua and Barbuda|ag
Argentina|ar
Armenia|am
Australia|au
Austria|at
Azerbaijan|az
Bahamas|bs
Bahrain|bh
Bangladesh|bd
Barbados|bb
Belarus|by
Belgium|be
Belize|bz
Benin|bj
Bhutan|bt
Bolivia|bo
Bosnia and Herzegovina|ba
Botswana|bw
Brazil|br
Brunei|bn
Bulgaria|bg
Burkina Faso|bf
Burundi|bi
Cabo Verde|cv
Cambodia|kh
Cameroon|cm
Canada|ca
Central African Republic|cf
Chad|td
Chile|cl
China|cn
Colombia|co
Comoros|km
Congo|cg
Costa Rica|cr
Côte d'Ivoire|ci
Croatia|hr
Cuba|cu
Cyprus|cy
Czechia|cz
Democratic Republic of the Congo|cd
Denmark|dk
Djibouti|dj
Dominica|dm
Dominican Republic|do
Ecuador|ec
Egypt|eg
El Salvador|sv
Equatorial Guinea|gq
Eritrea|er
Estonia|ee
Eswatini|sz
Ethiopia|et
Fiji|fj
Finland|fi
France|fr
Gabon|ga
Gambia|gm
Georgia|ge
Germany|de
Ghana|gh
Greece|gr
Grenada|gd
Guatemala|gt
Guinea|gn
Guinea-Bissau|gw
Guyana|gy
Haiti|ht
Honduras|hn
Hungary|hu
Iceland|is
India|in
Indonesia|id
Iran|ir
Iraq|iq
Ireland|ie
Israel|il
Italy|it
Jamaica|jm
Japan|jp
Jordan|jo
Kazakhstan|kz
Kenya|ke
Kiribati|ki
Kosovo|xk
Kuwait|kw
Kyrgyzstan|kg
Laos|la
Latvia|lv
Lebanon|lb
Lesotho|ls
Liberia|lr
Libya|ly
Liechtenstein|li
Lithuania|lt
Luxembourg|lu
Madagascar|mg
Malawi|mw
Malaysia|my
Maldives|mv
Mali|ml
Malta|mt
Marshall Islands|mh
Mauritania|mr
Mauritius|mu
Mexico|mx
Micronesia|fm
Moldova|md
Monaco|mc
Mongolia|mn
Montenegro|me
Morocco|ma
Mozambique|mz
Myanmar|mm
Namibia|na
Nauru|nr
Nepal|np
Netherlands|nl
New Zealand|nz
Nicaragua|ni
Niger|ne
Nigeria|ng
North Korea|kp
North Macedonia|mk
Northern Cyprus|xnc
Norway|no
Oman|om
Pakistan|pk
Palau|pw
Palestine|ps
Panama|pa
Papua New Guinea|pg
Paraguay|py
Peru|pe
Philippines|ph
Poland|pl
Portugal|pt
Qatar|qa
Romania|ro
Russia|ru
Rwanda|rw
Sahrawi Arab Democratic Republic|eh
Saint Kitts and Nevis|kn
Saint Lucia|lc
Saint Vincent and the Grenadines|vc
Samoa|ws
San Marino|sm
Sao Tome and Principe|st
Saudi Arabia|sa
Senegal|sn
Serbia|rs
Seychelles|sc
Sierra Leone|sl
Singapore|sg
Slovakia|sk
Slovenia|si
Solomon Islands|sb
Somalia|so
Somaliland|xsl
South Africa|za
South Korea|kr
South Ossetia|xso
South Sudan|ss
Spain|es
Sri Lanka|lk
Sudan|sd
Suriname|sr
Sweden|se
Switzerland|ch
Syria|sy
Taiwan|tw
Tajikistan|tj
Tanzania|tz
Thailand|th
Timor-Leste|tl
Togo|tg
Tonga|to
Transnistria|xtrn
Trinidad and Tobago|tt
Tunisia|tn
Turkey|tr
Turkmenistan|tm
Tuvalu|tv
Uganda|ug
Ukraine|ua
United Arab Emirates|ae
United Kingdom|gb
United States|us
Uruguay|uy
Uzbekistan|uz
Vanuatu|vu
Vatican City|va
Venezuela|ve
Vietnam|vn
Yemen|ye
Zambia|zm
Zimbabwe|zw
""";

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
        }

        for (String line : RAW_SUGGESTIONS.strip().split("\\R")) {
            String[] parts = line.split("\\|", 2);
            if (parts.length != 2) {
                continue;
            }

            addSuggested(parts[0].trim(), parts[1].trim());
        }

        alias("Turkiye", "tr");
        alias("Türkiye", "tr");
        alias("Turkei", "tr");
        alias("Türkei", "tr");

        alias("Deutschland", "de");
        alias("Duetchland", "de");
        alias("Deutchland", "de");

        alias("USA", "us");
        alias("US", "us");
        alias("U.S.A.", "us");
        alias("United States of America", "us");
        alias("America", "us");

        alias("Russian Federation", "ru");
        alias("Россия", "ru");

        alias("Srbija", "rs");
        alias("Сербия", "rs");

        alias("UK", "gb");
        alias("Great Britain", "gb");
        alias("Britain", "gb");

        alias("Korea", "kr");
        alias("Western Sahara", "eh");
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

    public static List<String> suggestions() {
        return List.copyOf(SUGGESTIONS);
    }

    private static void addSuggested(String displayName, String countryCode) {
        String code = countryCode.toLowerCase(Locale.ROOT);
        if (!CountryGlyphs.supports(code)) {
            return;
        }

        String suggestion = slug(displayName);
        SUGGESTIONS.add(suggestion);

        register(displayName, code);
        register(suggestion, code);
        register(code, code);
        CANONICAL.put(code, displayName);
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

    private static String slug(String displayName) {
        return displayName
                .trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", "-");
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
