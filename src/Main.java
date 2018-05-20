import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Given a list of film titles (or other words), provided in a file, one item per line,
 * generate a Hangman game with a random item and allow the player to guess a letter at a time.
 */
public class Main {

    /**
     * @param args [0]: the file from which to generate a phrase to guess. "-" or no args for default.
     *             [1]: the number of errors allowed to the player before losing. Empty for default.
     */
    public static void main(String[] args){
        PhrasesFile phrasesFile = null;
        Game game = null;

        /* Generate the file */
        if (args.length == 0 || args[0].equals("-")) {
            // No arguments or default file: generate a phrasesFile from the default file.
            try {
                phrasesFile = new PhrasesFile();
            } catch (FileNotFoundException e) {
                System.out.println("The default file films.txt was not found.");
                System.out.println("Please make a new one or specify a different file.");
                System.exit(1);
            }
        } else {
            // First argument is custom: attempt to generate a phrasesFile from the custom file.
            try {
                phrasesFile = new PhrasesFile(args[0]);
            } catch (FileNotFoundException e) {
                System.out.println("The specified file was not found.");
                System.out.println("Please double-check the path and name of the file.");
                System.out.println("Alternatively, specify a different file or use the default one.");
                System.exit(1);
            }
        }

        /* Set the error limit and set up the game. */
        if (args.length == 0) {
            // No arguments: use the default error limit.
            game = new Game(phrasesFile.pickPhrase());
        } else {
            // Try to set the error limit to the given input.
            try {
                game = new Game(phrasesFile.pickPhrase(), Integer.parseInt(args[1]));
            } catch (NumberFormatException e) {
                System.out.println("You entered an invalid value for the error limit!");
                System.out.println("Please enter a valid number.");
                System.out.println("Alternatively, leave the second parameter empty to use the default.");
                System.exit(1);
            }
        }


        /* Start the game. */
        System.out.println("======================= Welcome to HangmanGame by brzrkr =======================");
        System.out.println("Try to guess all the letters in the hidden phrase; if you get them all, you win!");
        System.out.println("================================================================================");
        System.out.println("You are allowed " + game.getErrorLimit() + " errors.");
        System.out.println();

        Scanner inputScanner = new Scanner(System.in);

        while (game.isOn()) {
            if (game.getGuessesMade() == 0) {
                System.out.println("Make your first guess!");
            } else {
                System.out.println();
                System.out.println();

                int errors = game.getErrors();
                if (errors > 0) {
                    System.out.println("You have made " + errors + " out of " +
                            game.getErrorLimit() + " allowed errors.");

                    String plural = errors==1?"This letter has been tried and is":
                            "These letters have been tried and are";

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
            char guessedLetter = input.charAt(0);

            int guessResult = -1;
            try { guessResult = game.check(guessedLetter); }
            catch (Game.GameHasEnded gameHasEnded) {
                System.out.println("ERROR: the game has already ended! Bye!");
                System.exit(0);
            } catch (Game.InvalidInput invalidInput) {
                System.out.println("You can only enter a letter or number! Try again.");
                continue;
            }

            if (guessResult == -1) {
                System.out.println("You have tried the letter " + guessedLetter + " already, try again!");
            } else if (guessResult == 0) {
                System.out.println("The letter " + guessedLetter + " is not present, sorry!");
            } else {
                String plural = guessResult>1?"s":"";
                System.out.println("Yay! The letter " + guessedLetter + " is present " +
                        guessResult + " time" + plural + "!");
            }
        }

        // At this point, the game is finished.
        System.out.println();
        if (game.isSolutionFound()) {
            int errors = game.getErrors();
            String plural = errors>1?"s":"";

            System.out.println(" *** Hurray! You won! *** ");
            System.out.println("It took you " + game.getGuessesMade() + " guesses and you made " +
                    errors + " error" + plural + " in total.");
            System.out.println("Here's the solution:");
        } else {
            System.out.println(" )': Oh no! You ran out of tries, you lost! :'( ");
            System.out.println("Better luck next time! This was the solution:");
        }
        System.out.println(game.getSolution());
        System.out.println("Thanks for playing, goodbye! :*");
    }
}
