package student;

import edu.willamette.cs1.spellingbee.SpellingBeeGraphics;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpellingBee {

    private static final String ENGLISH_DICTIONARY = "res/EnglishWords.txt";

    private SpellingBeeGraphics sbg;
    private List<String> dictionaryWords;
    private Set<String> foundWords;
    private int totalScore;

    private boolean isCorrectLength(String input) {
        return input.length() == 7;
    }

    private boolean areAllLetters(String input) {
        return input.chars().allMatch(Character::isLetter);
    }

    private boolean areCharactersUnique(String input) {
        return input.chars().distinct().count() == input.length();
    }


    public void run() {
        sbg = new SpellingBeeGraphics();
        sbg.addField("Puzzle", this::puzzleAction);
        sbg.addField("Word", this::wordAction);
        sbg.addButton("Solve", (s) -> solveAction());

        foundWords = new HashSet<>();
        totalScore = 0;

        try {
            dictionaryWords = readDictionary(ENGLISH_DICTIONARY);
        } catch (IOException e) {
            sbg.showMessage("Error reading the dictionary file.", Color.RED);
        }
    }

    //Word Action Helper functions

    private void showMessage(String message) {
        sbg.showMessage(message, Color.RED);
    }

    private void displayWordWithScore(String word, String puzzle) {
        foundWords.add(word);
        int wordScore = calculateWordScore(word, puzzle);
        totalScore += wordScore;

        if (isPangram(puzzle, word)) {
            sbg.addWord(word + " (" + wordScore + ")", Color.BLUE);
        } else {
            sbg.addWord(word + " (" + wordScore + ")");
        }

        displayTotalScore();
    }

    private void displayTotalScore() {
        // Display the total count and score
        sbg.showMessage(foundWords.size() + " words; " + totalScore + " points");
    }

    private void wordAction(String word) {
        word = word.trim().toLowerCase();
        String puzzle = sbg.getField("Puzzle");
        String centerLetter = String.valueOf(puzzle.charAt(0)).toLowerCase();

        if (!dictionaryWords.contains(word)) {
            showMessage("The word is not in the dictionary.");
        } else if (!usesOnlyPuzzleLetters(word, puzzle)) {
            showMessage("The word includes letters not in the beehive.");
        } else if (!isValidLength(word)) {
            showMessage("The word does not include at least four letters.");
        } else if (!word.contains(centerLetter)) {
            showMessage("The word does not include the center letter.");
        } else if (foundWords.contains(word)) {
            showMessage("You have already found this word.");
        } else {
            displayWordWithScore(word, puzzle);
        }
    }


    // puzzleAction
    public void puzzleAction(String input) {
        if (!isCorrectLength(input)) {
            sbg.showMessage("The puzzle must contain exactly seven characters.");
            return;
        }

        if (!areAllLetters(input)) {
            sbg.showMessage("Every character must be one of the 26 letters.");
            return;
        }

        if (!areCharactersUnique(input)) {
            sbg.showMessage("No letter may appear more than once in the puzzle.");
            return;
        }
        sbg.setBeehiveLetters(input);

        //Restart game
        totalScore = 0;
        sbg.clearWordList();
        foundWords = new HashSet<>();
    }

    //solveAction helper methods
    private boolean isValidLength(String word) {
        return word.length() >= 4;
    }

    private boolean usesOnlyPuzzleLetters(String word, String puzzle) {
        word = word.toUpperCase();
        for (char c : word.toCharArray()) {
            if (!puzzle.contains(String.valueOf(c))) {
                return false;
            }
        }
        return true;
    }

    private boolean containsCenterLetter(String word, String centerLetter) {
        return word.toUpperCase().contains(centerLetter);
    }

    private List<String> readDictionary(String path) throws IOException {
        return Files.readAllLines(Paths.get(path));
    }

    // Method to find and display valid words from the dictionary
    private void displayValidWords(String puzzle) {
        String centerLetter = String.valueOf(puzzle.charAt(0)).toUpperCase();
        puzzle = puzzle.toUpperCase();

        for (String word : dictionaryWords) {
            if (isValidLength(word) && usesOnlyPuzzleLetters(word, puzzle) && containsCenterLetter(word, centerLetter) && !foundWords.contains(word))
                displayWordWithScore(word, puzzle);
            else
                displayTotalScore();
        }
    }

    // Helper method to calculate the score of a word
    private int calculateWordScore(String word, String puzzle) {
        int length = word.length(); // Score based on length
        int score;

        if (length == 4) score = 1;
        else score = length;

        if (isPangram(puzzle, word)) {
            score += 7; // Bonus points for a pangram
        }
        return score;
    }

    // Helper method to check if the word is a pangram
    private boolean isPangram(String baseWord, String checkWord) {
        //System.out.println("baseword: " + baseWord + " | chekword: " + checkWord);
        baseWord = baseWord.toLowerCase();
        checkWord = checkWord.toLowerCase();

        for (int i = 0; i < baseWord.length(); i++) {
            char letter = baseWord.charAt(i);

            if (checkWord.indexOf(letter) == -1) {
                return false;
            }
        }
        return true;
    }

    // solveAction
    private void solveAction() {
        String puzzle = sbg.getField("Puzzle");
        puzzle = puzzle.toUpperCase();
        displayValidWords(puzzle);
    }

    public static void main(String[] args) {
        new SpellingBee().run();
    }
}
