package com.rooney.minerly.managers;

import com.rooney.minerly.enums.HttpStatus;
import com.rooney.minerly.enums.LanguageCode;
import com.rooney.minerly.models.Word;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MinerManager {

    private static final String OXFORD_URL = "https://www.oxfordlearnersdictionaries.com/us/definition/english/";

    public static List<String> readInputFile(File inputFile) {
        List<String> words = new ArrayList<>();

        try (Scanner scanner = new Scanner(inputFile)) {
            while (scanner.hasNextLine()) {
                words.add(scanner.nextLine().trim().replaceAll("[^a-zA-Z ]", ""));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Read input file: " + e.getMessage());
        }

        return words;

    }

    public static List<Word> requestWord(String wordToSearch) {
        String url = OXFORD_URL + wordToSearch;
        List<Word> words = new ArrayList<>();

        try {
            Connection.Response response = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .ignoreHttpErrors(true)
                    .execute();

            if (response.statusCode() == HttpStatus.NOT_FOUND.getCode()) {
                // TODO: VALIDATE WHEN THE WORD IS WRONG
                throw new RuntimeException(
                        wordToSearch + " - " + HttpStatus.NOT_FOUND.getCode() + " - " + HttpStatus.NOT_FOUND.getText() + " - "
                                + url);
            }

            if (response.statusCode() != HttpStatus.SUCCESS.getCode()) {
                throw new RuntimeException(wordToSearch + " - " + url);
            }

            Document documentRoot = response.parse();
            Element document = documentRoot.select(".entry").getFirst();
            Element header = document.select(".top-container").getFirst();
            Element titleWord = header.select(".headword").getFirst();
            Element partOfSpeech = header.select(".pos").getFirst();

            Element senses;
            if (!document.select(".senses_multiple").isEmpty()) {
                senses = document.select(".senses_multiple").getFirst();
            } else {
                senses = document.select(".sense_single").getFirst();
            }

            for (Element sense : senses.children()) {
                if (sense.select(".def").isEmpty()) {
                    continue;
                }
                if (sense.select(".examples").isEmpty()) {
                    continue;
                }

                Element definition = sense.select(".def").getFirst();
                Element examples = sense.select(".examples").getFirst();
                List<String> examplesText = examples.children().stream()
                        .map(Element::text)
                        .toList();

                String translation = ContextualTranslator.translate(
                        titleWord.text(), definition.text(), examplesText.getFirst(), LanguageCode.PT_BR
                );

                Word word = new Word(
                        titleWord.text(),
                        partOfSpeech.text(),
                        definition.text(),
                        examplesText,
                        translation);

                words.add(word);
            }

            return words;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static List<Word> requestWords(List<String> wordToSearchList) {
        List<Word> words = new ArrayList<>();
        int index = 0;
        int size = wordToSearchList.size();

        for (String wordToSearch : wordToSearchList) {
            System.out.printf("(%02d/%02d) - Loading...\r", ++index, size);
            try {
                List<Word> sameWordList = requestWord(wordToSearch);
                words.addAll(sameWordList);
                Thread.sleep(500);
            } catch (InterruptedException | RuntimeException ignored) {
                System.out.printf("Failure to process word: (%s)!\n", wordToSearch);
            }
        }

        return words;
    }

    public static void exportToAnki(List<Word> words) {
        Path outputPath = Path.of("output.txt");
        int currentProgress = 0;
        int totalWords = words.size();

        try {
            Files.deleteIfExists(outputPath);
        } catch (IOException e) {
            System.err.printf("Error clearing output file: %s%n", e.getMessage());
        }

        try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {

            for (Word word : words) {
                System.out.printf("(%02d/%02d) - Saving: %s...\r", ++currentProgress, totalWords, word.word());

                for (String example : word.examples()) {
                    String entry = formatAnkiLine(word, example);
                    writer.write(entry);
                    writer.newLine();
                }
            }
            System.out.println("\nExport completed successfully!");

        } catch (IOException e) {
            System.err.println("Critical failure processing output file: " + e.getMessage());
        }
    }

    private static String formatAnkiLine(Word word, String example) {
        String term = word.word();
        String translation = word.translation();
        String rawDefinition = word.definition().trim().replace(";", ",");

        String highlightedExample = example.contains(term)
                ? example.replace(term, "<b>" + term + "</b>")
                : example;

        return String.format(
                "<b>CIMV - I+1</b><br>%s;<b>%s</b>: %s<br>%s",
                highlightedExample,
                term,
                translation,
                rawDefinition
        );
    }
}
