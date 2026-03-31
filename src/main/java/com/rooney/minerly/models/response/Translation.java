package com.rooney.minerly.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Translation(
        String word,
        Language language
) {}
