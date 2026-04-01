package com.rooney.minerly.managers;

import com.rooney.minerly.enums.HttpStatus;
import com.rooney.minerly.models.Word;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MinerManager {

    private static final String OXFORD_URL = "https://www.oxfordlearnersdictionaries.com/us/definition/english/";

    public static List<String> readInputFile(File inputFile) {
        List<String> words = new ArrayList<>();

        try (Scanner scanner = new Scanner(inputFile)) {
            while (scanner.hasNext()) {
                words.add(scanner.next().trim().replaceAll("[^a-zA-Z ]", ""));
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
                        wordToSearch + " - " + HttpStatus.NOT_FOUND.getCode() + " - " + HttpStatus.NOT_FOUND.getText() + " - " + url
                );
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
                if (sense.select(".def").isEmpty()) {continue;}
                if (sense.select(".examples").isEmpty()) {continue;}

                Element definition = sense.select(".def").getFirst();
                Element examples = sense.select(".examples").getFirst();
                List<String> examplesText = examples.children().stream()
                        .map(Element::text)
                        .toList();

                words.add(new Word(
                        titleWord.text(),
                        partOfSpeech.text(),
                        definition.text(),
                        examplesText,
                        "Implement"
                ));
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
            } catch (InterruptedException | RuntimeException ignored) {}
        }

        return words;
    }
}
