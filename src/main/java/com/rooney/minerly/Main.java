package com.rooney.minerly;

import com.rooney.minerly.managers.MinerManager;
import com.rooney.minerly.models.Word;

import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        File inputFile = new File("input.txt");
        List<String> wordFileList = MinerManager.readInputFile(inputFile);
        List<Word> wordList = MinerManager.requestWords(wordFileList);
        // printWords(wordList);
        MinerManager.exportToAnki(wordList);
    }

    public static void printWords(List<Word> wordList) {
        System.out.println("==================================================");
        System.out.println("               DICTIONARY OF WORDS                ");
        System.out.println("==================================================");

        for (Word w : wordList) {
            System.out.printf("📖 Word: %s [%s]%n", w.word().toUpperCase(), w.partOfSpeech());
            System.out.printf("🌍 Translation: %s%n", w.translation());
            System.out.printf("💡 Definition: %s%n", w.definition());

            System.out.println("📝 Examples:");
            if (w.examples() == null || w.examples().isEmpty()) {
                System.out.println("   - (No examples available)");
            } else {
                for (String example : w.examples()) {
                    System.out.printf("   • \"%s\"%n", example);
                }
            }

            System.out.println("--------------------------------------------------");
        }
    }
}
