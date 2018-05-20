import exceptions.InvalidInputException;
import exceptions.GameHasEndedException;

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
        var output = new StringBuilder();

        for (int i = 0; i < wrongLetters.length(); i++) {
            output.append(Character.toUpperCase(wrongLetters.charAt(i)));

            if (i == wrongLetters.length()-1) {
                output.append(".");
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
        var sanitisedOutput = currentPhrase.
                replaceAll(" ", "   ").
                replaceAll("[^ _\\p{Alnum}]", " ");

        // Then add a space after each underscore and revealed letter that isn't followed by space or end-of-line.
        var builder = new StringBuilder();
        for (int i = 0; i < sanitisedOutput.length(); i++) {
            var currentChar = sanitisedOutput.charAt(i);

            if (currentChar == '_' || Character.isLetterOrDigit(currentChar)) {
                if (i + 1 == sanitisedOutput.length() || sanitisedOutput.charAt(i + 1) == ' ') {
                    builder.append(currentChar);
                } else {
                    builder.append(currentChar).append(" ");
                }
            } else {
                builder.append(currentChar);
            }
        }

        return builder.toString();
    }

    /**
     * Check if a letter is present in the solution.
     * @param letter the letter to be checked.
     * @return -1: if the letter has been attempted before.
     *          0: if the letter is not present in the solution and an error has been added.
     *          #: the number of matches inside the solution, if the letter has been found.
     * @throws GameHasEndedException if the game has already ended.
     * @throws InvalidInputException if letter is not alphanumeric.
     */
    int check(char letter) throws GameHasEndedException, InvalidInputException {
        if (!isOn()) { throw new GameHasEndedException(); }

        var guess = Character.toLowerCase(letter);
        if (!Character.isLetterOrDigit(guess)) throw new InvalidInputException();

        var result = unmask(guess);

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

    boolean isOn() { return isOn; }


    /**
     * Add one to the count of errors made by the player so far. End the game if the errorLimit has been reached.
     * @throws GameHasEndedException if the game has ended, preventing changes.
     */
    private void addError() throws GameHasEndedException {
        if (!isOn()) { throw new GameHasEndedException(); }

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
     * @throws GameHasEndedException if the game has ended, preventing changes.
     */
    private int unmask(char letter) throws GameHasEndedException {
        if (!isOn()) { throw new GameHasEndedException(); }

        // If the letter is already present in the current phrase or the wrong letters, it's been guessed before.
        if (currentPhrase.indexOf(letter) != -1 || wrongLetters.indexOf(letter) != -1) return -1;

        int index = solution.indexOf(letter);
        // If the letter isn't found in the solution, return 0 and update wrongLetters.
        if (index == -1) {
            wrongLetters = (wrongLetters + letter).toLowerCase();
            addError();
            return 0;
        }

        var found = 0;

        // Convert to array so that we can modify the contents of the string.
        var phraseChars = currentPhrase.toCharArray();
        while (index != -1) {
            found += 1;

            // Replace the _ at the index where the letter was found in the solution.
            phraseChars[index] = letter;

            // Look for the next instance of letter in the solution.
            index = solution.indexOf(letter, index+1);
        }
        // Convert back to String and update the current phrase.
        currentPhrase = String.valueOf(phraseChars);

        // Stop the game if the solution has been found.
        isSolutionFound();

        return found;
    }
}
