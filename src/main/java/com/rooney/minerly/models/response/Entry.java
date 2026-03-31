package com.rooney.minerly.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Entry(
        String partOfSpeech,
        List<Sense> senses
) {}
