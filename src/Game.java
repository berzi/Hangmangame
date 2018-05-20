import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Control the flow of the game and manage the the game's solution and progress.
 */
class Game {
    /**
     * Whether the game is still on or not. If it isn't, no more guesses can be made and the solution can be revealed.
     */
    private boolean isOn;

    private String solution;
    private String currentPhrase;

    private String wrongLetters;

    /**
     * The maximum number of errors allowed to the user. Defaults to 10.
     */
    private int errorLimit;

    private int guessesMade;
    private int errors;

    Game(String solution) {
        this.solution = solution.toLowerCase().trim();
        currentPhrase = mask(solution);

        wrongLetters = "";
        errorLimit = 10;
        guessesMade = 0;
        errors = 0;
        isOn = true;
    }

    Game(String solution, int errorLimit) {
        this(solution);
        this.errorLimit = errorLimit;
    }

    /**
     * @return the solution if the game has ended, otherwise return null.
     */
    String getSolution() { return isOn()? null:solution; }

    int getErrorLimit() { return errorLimit; }

    int getErrors() { return errors; }

    int getGuessesMade() { return guessesMade; }

    /**
     * @return all the letters that have been attempted but are not present in the solution, separated by commas.
     */
    String getWrongLetters() {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < wrongLetters.length(); i++) {
            output.append(Character.toUpperCase(wrongLetters.charAt(i)));

            if (i == wrongLetters.length()) {
                output.append("."); // TODO: debug why output always ends with , and not .
            } else {
                output.append(", ");
            }
        }

        return output.toString();
    }

    /**
     * @return the current progress of the phrase, made more readable and sanitised to hide telltale punctuation.
     */
    String getCurrentPhrase() {
        // Replace every space between words with three spaces for clarity (see below).
        // Then replace every non-space, non-alphanumeric, non-underscore character with a single space,
        // which obfuscates punctuation without removing its traces completely.
        String sanitisedOutput = currentPhrase.
                replace(" ", "   ").
                replaceAll("[\\W\\S]", " ");

        // Then add a space after each underscore and revealed letter that isn't followed by space or end-of-line.
        Matcher matcher = Pattern.compile("[_\\w](?!\\b)").matcher(sanitisedOutput);

        List<Character> outputChars = new ArrayList<>(); // TODO: convert this mess into a StringBuilder and debug.
        for (char character: sanitisedOutput.toCharArray()) outputChars.add(character);

        while (matcher.find()) outputChars.add(matcher.start()+1, ' ');
        sanitisedOutput = String.valueOf(outputChars);

        return sanitisedOutput;
    }

    /**
     * Check if a letter is present in the solution.
     * @param letter: the letter to be checked.
     * @return -1: if the letter has been attempted before.
     *          0: if the letter is not present in the solution and an error has been added.
     *          #: the number of matches inside the solution, if the letter has been found.
     * @throws GameHasEnded if the game has already ended.
     * @throws InvalidInput if letter is not alphanumeric.
     */
    int check(char letter) throws GameHasEnded, InvalidInput {
        if (!isOn()) { throw new GameHasEnded(); }

        char guess = Character.toLowerCase(letter);
        if (!Character.isLetterOrDigit(guess)) throw new InvalidInput();

        int result = unmask(guess);

        // Only count the guess if it wasn't a duplicate.
        if (result != -1) guessesMade += 1;

        return result;
    }

    /**
     * Check if the solution has been found and if so, stop the game.
     * @return true if the solution has been found, false otherwise.
     */
    boolean isSolutionFound() {
        if (currentPhrase.contentEquals(solution)) {
            endGame();
            return true;
        }

        return false;
    }

    boolean isOn() {
        return isOn;
    }


    /**
     * Add one to the count of errors made by the player so far. End the game if the errorLimit has been reached.
     * @throws GameHasEnded if the game has ended, preventing changes.
     */
    private void addError() throws GameHasEnded {
        if (!isOn()) { throw new GameHasEnded(); }

        errors +=1;

        if (errors >= errorLimit) endGame();
    }

    /**
     * Set the game as finished.
     */
    private void endGame() { isOn = false; }

    /**
     * @param string to mask.
     * @return a string with all word characters converted to _ and all special characters removed.
     */
    private String mask(String string) {
        // NOTE: this retains information on ' and other symbols, which are hidden when displaying the current progress.
        return string.replaceAll("\\w", "_");
    }

    /**
     * Unmask the entered letter if present in the solution.
     * @param letter the letter to be revealed.
     * @return the amount of matches found in the solution or -1 if the letter has been attempted before.
     */
    private int unmask(char letter) throws GameHasEnded {
        if (!isOn()) { throw new GameHasEnded(); }

        // If the letter is already present in the current phrase or the wrong letters, it's been guessed before.
        if (currentPhrase.indexOf(letter) != -1 || wrongLetters.indexOf(letter) != -1) return -1;

        int index = solution.indexOf(letter);
        // If the letter isn't found in the solution, return 0 and update wrongLetters.
        if (index == -1) {
            wrongLetters = (wrongLetters + letter).toLowerCase();
            addError();
            return 0;
        }

        int found = 0;

        // Convert to array so that we can modify the contents of the string.
        char[] phraseChars = currentPhrase.toCharArray();
        while (index != -1) {
            found += 1;

            // Replace the _ at the index where the letter was found in the solution.
            phraseChars[index] = letter;

            // Look for the next instance of letter in the solution.
            // TODO: check if letter in last index gives index bounds exception.
            index = solution.indexOf(letter, index+1);
        }
        // Convert back to String and update the current phrase.
        currentPhrase = String.valueOf(phraseChars);

        // Stop the game if the solution has been found.
        isSolutionFound();

        return found;
    }


    class GameHasEnded extends Exception {}
    class InvalidInput extends Exception {}
}
