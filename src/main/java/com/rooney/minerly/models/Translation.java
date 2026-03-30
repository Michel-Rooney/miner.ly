package com.rooney.minerly.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Translation(
        LanguageInfo language,
        String word
) {}
