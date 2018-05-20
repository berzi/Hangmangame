import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Parse the designated file containing the list of phrases and allows to pick random entries.
 * The file must have one entry per line.
 */
class PhrasesFile {
    private File listFile;
    private List<String> filmsList = new ArrayList<>();


    /**
     * Initialise the PhrasesFile with the default list file.
     */
    PhrasesFile() throws FileNotFoundException {
        /* Pick the default file name for the listFile, if unspecified. */
        this.listFile = new File("films.txt");

        parseList();
    }

    /**
     * Initialise the PhrasesFile with a specified list file.
     * @param file_path the path to the custom list file.
     */
    PhrasesFile(String file_path) throws FileNotFoundException {
        this.listFile = new File(file_path);

        parseList();
    }


    /**
     * Parse the file containing the list of phrases to build a list containing each of them.
     * @throws FileNotFoundException if the file is not found.
     */
    private void parseList() throws FileNotFoundException {
        Scanner scanner = new Scanner(this.listFile);

        while (scanner.hasNextLine()) {
            this.filmsList.add(scanner.nextLine());
        }
    }


    /**
     * Pick a phrase from the list of phrases.
     * @return a phrase picked from the list.
     */
    String pickPhrase() {
        Collections.shuffle(this.filmsList);
        return this.filmsList.get(0);
    }
}
