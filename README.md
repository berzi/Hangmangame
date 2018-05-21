# Hangman
Hangman is the console-based Java version of the popular word game Hangman, where the player(s) have to guess one letter
 at a time to discover a secret phrase.

Every guessed letter that is not present in the solution counts as a mistake; upon reaching a certain amount of mistakes
, the game is lost.

If the player(s) can guess the solution before that happens, they win!

In this version of the game, the solution cannot be guessed mid-game, but must rather be uncovered by guessing every 
letter it contains.

## How to run
**Java Runtime Environment is required.**

Using your preferred console, navigate to the folder containing `Hangman.jar` and run:

`java -jar Hangman.jar`

**NOTE:** the program works by picking a random phrase from a file. If you don't want to use a custom file, download the
 default `films.txt`, which contains some film titles, and place it in the same directory as the game.

### Options
You can pick a custom phrases file and/or a custom limit for player mistakes either after finishing a game or when first
 running the program.
 
#### Custom file
To pick a custom file, give its path to the first argument of the program:

`java -jar Hangman.jar my_file.txt`

##### Custom file format
The custom file must be plain text and contain one phrase per line.

Note that case is ignored internally.

#### Custom mistakes limit
To set a custom limit to the mistakes the player(s) are allowed to make (default: 10), give it as the second argument to
 the program; for example, to set the limit to 5:
 
`java -jar Hangman.jar my_file.txt 5`

If you want to set the limit but not use a custom file, provide `-` as the first argument:

`java -jar Hangman.jar - 5`
