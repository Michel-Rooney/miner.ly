package com.rooney.minerly.managers;

import com.rooney.minerly.enums.LanguageCode;
import com.rooney.minerly.models.Word;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public class ContextualTranslator {

    public interface TranslatorService {
        @SystemMessage("""
            You are a professional English (en) to Portuguese (pt) translator.
            Your goal is to return ONLY the translated word, without an explanation.
            Of the original English word to Portuguese grammar, vocabulary, and cultural sensitivities.
            Produce only the Portuguese translation, without any additional explanations or commentary.
            Please translate the following English word into Portuguese:
            
            
            """)
        String translate(@UserMessage String message);
    }

    private static final ChatModel chatModel = OllamaChatModel.builder()
            .baseUrl("http://localhost:11434")
            .modelName("translategemma")
            .build();

    private static final TranslatorService translatorService = AiServices.create(TranslatorService.class, chatModel);


    public static String translate(String word, String definition, String example, LanguageCode language) {
        String message = String.format(
                "word: %s | definition: %s",
                word, definition
        );
        return translatorService.translate(message);
    }


}
