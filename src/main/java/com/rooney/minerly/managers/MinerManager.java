package com.rooney.minerly.managers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rooney.minerly.enums.LanguageCode;
import com.rooney.minerly.models.Word;

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

    public static List<Word> requestWords(LanguageCode language, List<String> words) {
        // https://freedictionaryapi.com/api/v1/entries/en/hello?translations=true
        boolean translations = true;
        List<Word> wordsModel = new ArrayList<>();

        for (String word : words) {
            String path = String.format("%s/%s?translations=%s", language.getCode(), word, Boolean.toString(translations));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_API_URL + path))
                    .GET()
                    .build();

            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println(response.body());
                Word wordModel = mapper.readValue(response.body(), Word.class);
                wordsModel.add(wordModel);
            } catch (Exception e) {
                throw new RuntimeException("Request error: " + e.getMessage());
            }

        }

        return wordsModel;
    }
}
