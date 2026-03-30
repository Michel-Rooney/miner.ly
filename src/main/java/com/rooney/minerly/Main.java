package com.rooney.minerly;

import com.rooney.minerly.enums.LanguageCode;
import com.rooney.minerly.managers.MinerManager;
import com.rooney.minerly.models.Entry;
import com.rooney.minerly.models.Sense;
import com.rooney.minerly.models.Translation;
import com.rooney.minerly.models.Word;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, World!");

        File inputFile = new File("input.txt");
        List<String> words = MinerManager.readInputFile(inputFile);
        System.out.println(words);

        List<Word> wordsModel = MinerManager.requestWords(LanguageCode.EN, words.subList(0, 3));

        for (Word word : wordsModel) {
            // Supondo que 'entries' seja sua List<Entry> vinda do Jackson
            for (Entry entry : word.entries()) {
                System.out.println("========== ENTRADA ==========");
                System.out.println(word.word());

                for (Sense sense : entry.senses()) {
                    System.out.println("\n[DEFINIÇÃO]");
                    System.out.println("-> " + sense.definition());

                    // Listar Exemplos, se houver
                    if (sense.examples() != null && !sense.examples().isEmpty()) {
                        System.out.println("  Exemplos:");
                        for (String example : sense.examples()) {
                            System.out.println("    * " + example);
                        }
                    }

                    // Listar Traduções, se houver
                    if (sense.translations() != null && !sense.translations().isEmpty()) {
                        System.out.print("  Traduções: ");
                        for (int i = 0; i < sense.translations().size(); i++) {
                            Translation t = sense.translations().get(i);
                            // Ex: "Português (perdido)"
                            System.out.print(t.language().name() + " (" + t.word() + ")");

                            // Apenas para colocar uma vírgula entre as traduções
                            if (i < sense.translations().size() - 1) System.out.print(", ");
                        }
                        System.out.println();
                    }
                }
                System.out.println("\n");
            }
        }
    }
}
