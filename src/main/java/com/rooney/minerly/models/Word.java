package com.rooney.minerly.models;

import java.util.List;

public record Word(
        String word,
        String partOfSpeech,
        String definition,
        List<String> examples,
        String translationCode,
        String translationName,
        String translationWord
) {}
