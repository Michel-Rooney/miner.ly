package com.rooney.minerly.managers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rooney.minerly.enums.LanguageCode;
import com.rooney.minerly.models.Word;
import com.rooney.minerly.models.response.Entry;
import com.rooney.minerly.models.response.Sense;
import com.rooney.minerly.models.response.Translation;
import com.rooney.minerly.models.response.WordResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MinerManager {

    private static final String BASE_API_URL = "https://freedictionaryapi.com/api/v1/entries/";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

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

    public static List<WordResponse> requestWords(LanguageCode languageCode, List<String> wordsToSearch) {
        // https://freedictionaryapi.com/api/v1/entries/en/hello?translations=true
        boolean translations = true;
        List<WordResponse> words = new ArrayList<>();

        for (String wordToSearch : wordsToSearch) {
            String path = String.format("%s/%s?translations=%s", languageCode.getCode(), wordToSearch, Boolean.toString(translations));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_API_URL + path))
                    .GET()
                    .build();

            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                WordResponse word = mapper.readValue(response.body(), WordResponse.class);
                words.add(word);
            } catch (Exception e) {
                throw new RuntimeException("Request error: " + e.getMessage());
            }

        }

        return words;
    }

    public static List<Word> mapperWords(List<WordResponse> wordResponseList) {
        List<Word> words = new ArrayList<>();

        for (WordResponse wordResponse : wordResponseList) {
            for (Entry entry : wordResponse.entries()) {
                for (Sense sense : entry.senses()) {
                    String word = wordResponse.word();
                    String partOfSpeech = entry.partOfSpeech();
                    String definition = sense.definition();
                    List<String> examples = sense.examples();

                    String translationCode = "";
                    String translationName = "";
                    String translationWord = "";

                    List<Translation> translationList = sense.translations().stream()
                            .filter(translation1 -> translation1.language().code().equals("pt"))
                            .toList();

                    if (!translationList.isEmpty()) {
                        translationCode = translationList.getFirst().language().code();
                        translationName = translationList.getFirst().language().name();
                        translationWord = translationList.getFirst().word();
                    }

                    if (examples.isEmpty()) {
                        continue;
                    }

                    if (translationWord.isEmpty()) {
                        continue;
                    }

                    words.add(new Word(
                            word,
                            partOfSpeech,
                            definition,
                            examples,
                            translationCode,
                            translationName,
                            translationWord
                    ));
                }
            }
        }

        return words;
    }
}
