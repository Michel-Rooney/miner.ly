package com.rooney.minerly.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LanguageInfo(
        String code,
        String name
) {}
