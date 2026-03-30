package com.rooney.minerly.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Sense(
        String definition,
        List<String> examples,
        List<Translation> translations
) {
    // Método utilitário para pegar apenas a tradução em português
    public String getPortugueseTranslation() {
        if (translations == null) return null;
        return translations.stream()
                .filter(t -> "pt".equalsIgnoreCase(t.language().code()))
                .map(Translation::word)
                .findFirst()
                .orElse(null);
    }
}
