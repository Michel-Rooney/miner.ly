package com.rooney.minerly;

import com.rooney.minerly.enums.LanguageCode;
import com.rooney.minerly.managers.MinerManager;
import com.rooney.minerly.models.Word;
import com.rooney.minerly.models.response.WordResponse;

import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, World!");

        File inputFile = new File("input.txt");
        List<String> wordFileList = MinerManager.readInputFile(inputFile);
        System.out.println(wordFileList);

        List<WordResponse> wordResponseList = MinerManager.requestWords(LanguageCode.EN, wordFileList.subList(0, 3));
        List<Word> wordList = MinerManager.mapperWords(wordResponseList);

        System.out.println("==================================================");
        System.out.println("             DICIONÁRIO DE PALAVRAS               ");
        System.out.println("==================================================");

        for (Word w : wordList) {
            System.out.printf("📖 Palavra: %s [%s]%n", w.word().toUpperCase(), w.partOfSpeech());
            System.out.printf("🌍 Tradução: %s (%s - %s)%n", w.translationWord(), w.translationName(), w.translationCode());
            System.out.printf("💡 Definição: %s%n", w.definition());

            System.out.println("📝 Exemplos:");
            if (w.examples() == null || w.examples().isEmpty()) {
                System.out.println("   - (Nenhum exemplo disponível)");
            } else {
                for (String exemplo : w.examples()) {
                    System.out.printf("   • \"%s\"%n", exemplo);
                }
            }

            System.out.println("--------------------------------------------------");
        }
    }
}
