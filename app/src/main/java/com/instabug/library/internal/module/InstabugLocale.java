package com.instabug.library.internal.module;

/* loaded from: classes.dex */
public enum InstabugLocale {
    ENGLISH("en"),
    ARABIC("ar"),
    GERMAN("de"),
    SPANISH("es"),
    FRENCH("fr"),
    ITALIAN("it"),
    JAPANESE("ja"),
    KOREAN("ko"),
    POLISH("pl"),
    PORTUGUESE_BRAZIL("pt", "BR"),
    PORTUGUESE_PORTUGAL("pt", "PT"),
    RUSSIAN("ru"),
    SWEDISH("sv"),
    TURKISH("tr"),
    SIMPLIFIED_CHINESE("zh", "CN"),
    TRADITIONAL_CHINESE("zh", "TW"),
    CZECH("cs"),
    PERSIAN("fa"),
    INDONESIAN("in"),
    DANISH("da"),
    SLOVAK("sk"),
    NETHERLANDS("nl");

    private final String code;
    private final String country;

    InstabugLocale(String str) {
        this.code = str;
        this.country = "";
    }

    InstabugLocale(String str, String str2) {
        this.code = str;
        this.country = str2;
    }

    public String getCode() {
        return this.code;
    }

    public String getCountry() {
        return this.country;
    }
}
