import exceptions.GameHasEndedException;
import exceptions.InvalidInputException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Given a list of phrases provided in a file, one item per line,
 * generate a Hangman game with a random item and allow the player to guess a letter at a time.
 */
public class Main {
    /**
     * The default file to use to pick phrases for the game if not specified.
     */
    private static String defaultFilePath = "films.txt";

    /**
     * The default error limit allowed to the player if not specified.
     */
    private static int defaultErrorLimit = 10;

    /**
     * The error limit that was last used in this session.
     */
    private static int errorLimit = defaultErrorLimit;

    /**
     * The file path that was last used in this session.
     */
    private static String filePath = defaultFilePath;

    /**
     * Stores the solution that appeared in the session to avoid repeating them.
     */
    private static List<String> usedSolutions = new ArrayList<>();

    private static Game game = null;
    private static PhrasesFile phrasesFile = null;

    private static void setUpGame(String filePath, String errorLimit) {
        /* Generate the file */
        if (filePath.equals(defaultFilePath)) {
            // No arguments or default file: generate a phrasesFile from the default file.
            try { phrasesFile = new PhrasesFile(defaultFilePath); }
            catch (IOException e) {
                System.out.println("The default file " + defaultFilePath + " was not found.");
                System.out.println("Please create it or specify a different file.");
                System.exit(1);
            }
        } else {
            // First argument is custom: attempt to generate a phrasesFile from the custom file.
            try { phrasesFile = new PhrasesFile(filePath); }
            catch (IOException e) {
                System.out.println("The specified file was not found.");
                System.out.println("Please double-check the path and name of the file.");
                System.out.println("Alternatively, specify a different file or use the default one by using - .");
                System.exit(1);
            }
        }

        // Try to pick a phrase that wasn't already used.
        String phrase = phrasesFile.pickPhrase();
        if (usedSolutions.containsAll(phrasesFile.getPhrasesList())){
            System.out.println("Sorry, you've played with all the phrases available in this file.");
            System.out.println("Restart the game to play with the same file regardless, or enter a different file.");
            promptNewGame();
        } else { while (usedSolutions.contains(phrase)) { phrase = phrasesFile.pickPhrase(); } }
        usedSolutions.add(phrase);

        // Set the error limit and set up the game.
        try { game = new Game(phrase, Integer.parseInt(errorLimit)); }
        catch (NumberFormatException e) {
            System.out.println("You entered an invalid value for the error limit!");
            System.out.println("Please enter a valid number.");
            System.out.println("Alternatively, leave the second parameter empty to use the default.");
            System.exit(1);
        }
    }

    private static void promptNewGame() {
        Scanner inputScanner = new Scanner(System.in);

        String input = null;

        // Prompt until player inputs n or y.
        while (input == null ||
                !(input.equalsIgnoreCase("y") || input.equalsIgnoreCase("n"))) {
            System.out.println("Do you want to play again?");
            System.out.print("Y/N: ");
            input = inputScanner.nextLine();
        }

        if (input.equalsIgnoreCase("y")) {
            System.out.print("Select a file to use (or leave blank for lastly used: " +
                    filePath + "): ");
            String replayFile = inputScanner.nextLine();
            System.out.print("Amount of mistakes allowed (or leave blank for lastly used: " +
                    errorLimit + "): ");
            String replayErrors = inputScanner.nextLine();

            if (replayFile.trim().length() == 0) {
                replayFile = "-";
            }

            String[] newArgs = {replayFile, replayErrors};
            main(newArgs);
        } else {
            System.out.println("Thanks for playing then, goodbye! :*");
            System.exit(0);
        }
    }

    /**
     * @param args [0]: the file from which to generate a phrase to guess. "-" or no args for default.
     *             [1]: the number of errors allowed to the player before losing. Empty for default.
     */
    public static void main(String[] args){
        String newErrorLimit = "" + errorLimit;
        if (args.length > 0){
            if (!args[0].equals("-")) { filePath = args[0]; }
            if (args.length > 1 && !args[1].isEmpty()){
                newErrorLimit = args[1];
            }
        }
        setUpGame(filePath, newErrorLimit);

        /* Start the game. */
        System.out.println("======================= Welcome to HangmanGame by brzrkr =======================");
        System.out.println("Try to guess all the letters in the hidden phrase; if you get them all, you win!");
        System.out.println("================================================================================");
        System.out.println("Using file: " + filePath +". You are allowed " + game.getErrorLimit() + " errors.");
        System.out.println();

        Scanner inputScanner = new Scanner(System.in);

        while (game.isOn()) {
            if (game.getGuessesMade() == 0) {
                System.out.println("Make your first guess!");
            } else {
                int errors = game.getErrors();
                if (errors > 0) {
                    System.out.println("You have made " + errors + " out of " +
                            game.getErrorLimit() + " allowed errors.");

                    String plural = errors != 1?"These letters have been tried and are":
                            "This letter has been tried and is";

                    System.out.println(plural + " not present in the solution:");
                    System.out.println(game.getWrongLetters());
                }
            }

            System.out.println();
            System.out.println("The current phrase is:");
            System.out.println(game.getCurrentPhrase());
            System.out.println();
            System.out.print("Guess a letter: ");
            String input = inputScanner.nextLine();
            char guessedLetter;
            try { guessedLetter = input.charAt(0); }
            catch (StringIndexOutOfBoundsException e) { guessedLetter = ' '; }


            int guessResult = -1;
            try { guessResult = game.check(guessedLetter); }
            catch (GameHasEndedException e) {
                System.out.println("ERROR: the game has already ended! Bye!");
                System.exit(0);
            } catch (InvalidInputException e) {
                System.out.println("You must enter a letter or number! Try again.");
                continue;
            }

            guessedLetter = Character.toUpperCase(guessedLetter);

            if (guessResult == -1) {
                System.out.println("You have tried " + guessedLetter + " already, try again!");
            } else if (guessResult == 0) {
                System.out.println(guessedLetter + " is not present, sorry!");
            } else {
                String plural = guessResult != 1?"s":"";
                System.out.println("Yay! " + guessedLetter + " is present " +
                        guessResult + " time" + plural + "!");
            }
        }

        // At this point, the game is finished.
        System.out.println();
        if (game.isSolutionFound()) {
            int errors = game.getErrors();
            String plural = errors != 1?"s":"";

            System.out.println(" *** Hurray! You won! *** ");
            System.out.println("It took you " + game.getGuessesMade() + " guesses and you made " +
                    errors + " error" + plural + " in total.");
            System.out.println("Here's the solution:");
        } else {
            System.out.println(" )': Oh no! You ran out of tries, you lost! :'( ");
            System.out.println();
            System.out.println("Better luck next time! This was the solution:");
        }
        System.out.println(game.getSolution());
        System.out.println();

        promptNewGame();
    }
}
