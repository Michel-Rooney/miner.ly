package com.rooney.minerly.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Sense(
        String definition,
        List<String> examples,
        List<Translation> translations
) {}
