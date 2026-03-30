package com.rooney.minerly.enums;

public enum LanguageCode {
    EN("en"), // English
    PT("pt"), // Portuguese
    ES("es"), // Spanish
    FR("fr"), // French
    DE("de"), // German
    IT("it"), // Italian
    JA("ja"); // Japanese

    private final String code;

    LanguageCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static LanguageCode fromString(String text) {
        for (LanguageCode lang : LanguageCode.values()) {
            if (lang.code.equalsIgnoreCase(text)) {
                return lang;
            }
        }
        return null;
    }
}