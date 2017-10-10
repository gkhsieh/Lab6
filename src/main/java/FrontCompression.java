import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

/**
 * Implement front compression.
 * <p>
 * Front compression (also called, strangely, back compression, and, less strangely, front coding)
 * is a compression algorithm used for reducing the size of certain kinds of textual structured
 * data. Instead of storing an entire string value, we use a prefix from the previous value in a
 * list.
 * <p>
 * Front compression is particularly useful when compressing lists of words where each successive
 * element has a great deal of similarity with the previous. One example is a search (or book)
 * index. Another example is a dictionary.
 * <p>
 * This starter code will help walk you through the process of implementing front compression.
 *
 * @see <a href="https://cs125.cs.illinois.edu/lab/6/">Lab 6 Description</a>
 * @see <a href="https://en.wikipedia.org/wiki/Incremental_encoding"> Incremental Encoding on
 *      Wikipedia </a>
 */

public class FrontCompression {

    /**
     * Compress a newline-separated list of words using simple front compression.
     *
     * @param corpus the newline-separated list of words to compress
     * @return the input compressed using front encoding
     */
    public static String compress(final String corpus) {
        /*
         * Defend against bad inputs.
         */
        if (corpus == null) {
            return null;
        } else if (corpus.length() == 0) {
            return "";
        }

        /*
         * Complete this function.
         */
        //split the input into a String array by space
        String[] split = corpus.split("\\r?\\n");

        //first part of compressed string is always 0 + first word
        String compressed = "0 " + split[0] + "\n", addend = "";
        int prefixLength = 0;

        //iterate through pairs to compare and compress
        for (int i = 0; i < split.length - 1; i++) {

            //finds length of common prefix
            prefixLength = longestPrefix(split[i], split[i+1]);

            //concatenates prefixLength and word without prefix
            addend = Integer.toString(prefixLength) + " " + split[i+1].substring(prefixLength) + "\n";
            compressed += addend;
        }

        return compressed;
    }

    /**
     * Decompress a newline-separated list of words using simple front compression.
     *
     * @param corpus the newline-separated list of words to decompress
     * @return the input decompressed using front encoding
     */
    public static String decompress(final String corpus) {
        /*
         * Defend against bad inputs.
         */
        if (corpus == null) {
            return null;
        } else if (corpus.length() == 0) {
            return "";
        }

        /*
         * Complete this function.
         */
        //split the input into a String array by space
        String[] split = corpus.split("\\s+");
        String[] dictionary = new String[split.length / 2];
        int[] prefixLengths = new int[split.length / 2];

        for (int i = 0; i < split.length / 2; i++) {
            prefixLengths[i] = Integer.parseInt(split[i*2]);
            dictionary[i] = split[i*2+1];
        }

        String decompressed = dictionary[0] + "\n", prefix = "";
        String originalPrefix = dictionary[0].substring(0, prefixLengths[1]);

        //iterate through pairs to compare and decompress
        for (int i = 1; i < dictionary.length; i++) {

            //use original prefix if prefixLength is not 0
            if (prefixLengths[i] != 0) {
                prefix = originalPrefix;
                }

            else {
                prefix = dictionary[i-1].substring(0, prefixLengths[i]);
                originalPrefix = prefix;
                }

            decompressed += prefix + dictionary[i] + "\n";
        }

        return decompressed;
    }

    /**
     * Compute the length of the common prefix between two strings.
     *
     * @param firstString the first string
     * @param secondString the second string
     * @return the length of the common prefix between the two strings
     */
    private static int longestPrefix(final String firstString, final String secondString) {
        /*
         * Complete this function.
         */

        //initialize some variables
        int count = 0, currentIndex = 0, maxIndex;
        boolean charIsDifferent = false;

        //finds length of the shorter string
        if (firstString.length() <= secondString.length()) { maxIndex = firstString.length(); }
        else { maxIndex = secondString.length(); }

        //increments count until chars are different or currentIndex reaches maxIndex
        while (currentIndex < maxIndex && !charIsDifferent) {
            if (firstString.charAt(currentIndex) != secondString.charAt(currentIndex)) { charIsDifferent = true; }
            else { count++; currentIndex++; }
            }

        return count;
    }

    /**
     * Test your compression and decompression algorithm.
     *
     * @param unused unused input arguments
     * @throws URISyntaxException thrown if the file URI is invalid
     * @throws FileNotFoundException thrown if the file cannot be found
     */
    public static void main(final String[] unused)
            throws URISyntaxException, FileNotFoundException {
        /*
         * The magic 6 lines that you need in Java to read stuff from a file.
         */
        String words = null;
        String wordsFilePath = FrontCompression.class.getClassLoader().getResource("words.txt")
                .getFile();
        wordsFilePath = new URI(wordsFilePath).getPath();
        File wordsFile = new File(wordsFilePath);
        Scanner wordsScanner = new Scanner(wordsFile, "UTF-8");
        words = wordsScanner.useDelimiter("\\A").next();
        wordsScanner.close();

        String originalWords = words;
        String compressedWords = compress(words);
        String decompressedWords = decompress(compressedWords);

        if (decompressedWords.equals(originalWords)) {
            System.out.println("Original length: " + originalWords.length());
            System.out.println("Compressed length: " + compressedWords.length());
        } else {
            System.out.println("Your compression or decompression is broken!");
            String[] originalWordsArray = originalWords.split("\\R");
            String[] decompressedWordsArray = decompressedWords.split("\\R");
            boolean foundMismatch = false;
            for (int stringIndex = 0; //
                    stringIndex < Math.min(originalWordsArray.length,
                            decompressedWordsArray.length); //
                    stringIndex++) {
                if (!(originalWordsArray[stringIndex]
                        .equals(decompressedWordsArray[stringIndex]))) {
                    System.out.println("Line " + stringIndex + ": " //
                            + originalWordsArray[stringIndex] //
                            + " != " + decompressedWordsArray[stringIndex]);
                    foundMismatch = true;
                    break;
                }
            }
            if (!foundMismatch) {
                if (originalWordsArray.length != decompressedWordsArray.length) {
                    System.out.println("Original and decompressed files have different lengths");
                } else {
                    System.out.println("Original and decompressed files " //
                            + "have different line endings.");
                }
            }
        }
    }
}
