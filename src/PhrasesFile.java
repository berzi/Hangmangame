import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Parse the designated file containing the list of phrases and allows to pick random entries.
 * The file must have one entry per line.
 */
class PhrasesFile {
    private List<String> phrasesList;


    /**
     * Initialise the PhrasesFile with a specified list file.
     * @param filePath the path to the custom list file.
     */
    PhrasesFile(String filePath) throws IOException {
        phrasesList = Files.readAllLines(Paths.get(filePath));
    }


    /**
     * @return the phrasesList.
     */
    List<String> getPhrasesList() { return phrasesList; }

    /**
     * Pick a phrase from the list of phrases.
     * @return a phrase picked from the list.
     */
    String pickPhrase() {
        Collections.shuffle(phrasesList);
        return phrasesList.get(0);
    }
}
