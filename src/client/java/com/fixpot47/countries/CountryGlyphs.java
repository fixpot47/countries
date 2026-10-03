package com.fixpot47.countries;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class CountryGlyphs {
    private static final String[] CODES = """
ad ae af ag ai al am ao aq ar as at au aw ax az ba bb bd be bf bg bh bi bj bl bm bn bo bq br bs bt bv bw by bz ca cc cd cf cg ch ci ck cl cm cn co cr cu cv cw cx cy cz de dj dk dm do dz ec ee eg eh er es et fi fj fk fm fo fr ga gb-eng gb-nir gb-sct gb-wls gb gd ge gf gg gh gi gl gm gn gp gq gr gs gt gu gw gy hk hm hn hr ht hu id ie il im in io iq ir is it je jm jo jp ke kg kh ki km kn kp kr kw ky kz la lb lc li lk lr ls lt lu lv ly ma mc md me mf mg mh mk ml mm mn mo mp mq mr ms mt mu mv mw mx my mz na nc ne nf ng ni nl no np nr nu nz om pa pe pf pg ph pk pl pm pn pr ps pt pw py qa re ro rs ru rw sa sb sc sd se sg sh si sj sk sl sm sn so sr ss st sv sx sy sz tc td tf tg th tj tk tl tm tn to tr tt tv tw tz ua ug um us uy uz va vc ve vg vi vn vu wf ws xk ye yt za zm zw
""".trim().split("\\s+");

    private static final Map<String, String> GLYPHS = new HashMap<>();

    static {
        for (int i = 0; i < CODES.length; i++) {
            GLYPHS.put(CODES[i], String.valueOf((char) (0xE000 + i)));
        }

        GLYPHS.put("xab", String.valueOf((char) 0xE100));
        GLYPHS.put("xnc", String.valueOf((char) 0xE101));
        GLYPHS.put("xsl", String.valueOf((char) 0xE102));
        GLYPHS.put("xso", String.valueOf((char) 0xE103));
        GLYPHS.put("xtrn", String.valueOf((char) 0xE104));
    }

    private CountryGlyphs() {
    }

    public static String glyph(String countryCode) {
        if (countryCode == null) {
            return null;
        }

        return GLYPHS.get(countryCode.toLowerCase(Locale.ROOT));
    }

    public static boolean supports(String countryCode) {
        return glyph(countryCode) != null;
    }
}
