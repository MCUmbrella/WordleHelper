package vip.floatationdevice.wordlehelper;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * This class sorts the answer words based on their frequency by comparing them with
 * a pre-sorted list of words ("sorted.txt" under the workdir), and provides an
 * option to save the sorted answer words to a file ("answer.txt" under the workdir).
 *
 * You can grab a copy of pre-sorted word list from various websites. For example:
 * <a href="https://github.com/first20hours/google-10000-english">first20hours/google-10000-english</a>
 */
public class WordleHelperAnswerWordsSorter
{
    final static Pattern validWord = Pattern.compile("^[a-zA-Z]{5}$"); // regex for words with 5 letters
    final static File sortedWordsFile = new File("sorted.txt"); // the sorted words file at "sorted.txt" under the workdir
    static LinkedList<String> sortedWords = new LinkedList<>(); // the list of sorted words read from sorted.txt
    static LinkedList<String> answerWords = null; // the list of unsorted answer words read by WordleHelper

    public static void main(String[] args) throws Exception
    {
        System.out.println("Initializing WordleHelper");
        WordleHelper.init();
        answerWords = new LinkedList<>(Arrays.asList(WordleHelper.getRemainingWords()));
        System.out.println("OK: Unsorted answer words: " + answerWords.size() + " words, " + answerWords);

        System.out.println("Reading sorted words from './sorted.txt'");
        InputStream is = new FileInputStream(sortedWordsFile);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(isr);
        for(String line = reader.readLine(); line != null; line = reader.readLine())
            if(validWord.matcher(line).find()) sortedWords.add(line.toLowerCase());
        reader.close();
        isr.close();
        is.close();
        System.out.println("OK: Words sorted by frequency: " + sortedWords.size() + " words");

        System.out.println("Sorting answer words");
        LinkedList<String> sortedAnswerWords = new LinkedList<>();
        for(String w : sortedWords) // for every word in the sorted words file
            for(Iterator<String> aw = answerWords.iterator(); aw.hasNext(); ) // for every word in the unsorted answer words list
            {
                String s = aw.next();
                if(s.equals(w)) // if the word is in the unsorted answer words list
                {
                    // move the word from the unsorted answer words list to the new sorted words list
                    sortedAnswerWords.add(s);
                    aw.remove();
                }
            }
        System.out.println("OK: Answer words sorted by frequency: " + sortedAnswerWords.size() + " words");

        // ensures that all the answer words are included in the final sorted list,
        // even if they were not found in the pre-sorted words list
        System.out.println("Putting " + answerWords.size() + " remaining unsorted answer words to the sorted words list");
        sortedAnswerWords.addAll(answerWords);
        System.out.println("OK: Final answer words sorted by frequency: " + sortedAnswerWords.size() + " words, " + sortedAnswerWords);

        // check if the size of sorted answer words list equals to the unsorted one
        if(sortedAnswerWords.size() != WordleHelper.getRemainingWords().length)
            throw new AssertionError(); // logic error

        System.out.print("Save the sorted answer words list to './answer.txt'? [y/N]\n? ");
        try
        {
            if(new Scanner(System.in).nextLine().equalsIgnoreCase("y"))
            {
                System.out.println("Please wait");
                BufferedWriter writer = new BufferedWriter(new FileWriter("answer.txt"));
                for(String s : sortedAnswerWords)
                {
                    writer.write(s);
                    writer.write('\n');
                }
                writer.flush();
                writer.close();
                System.out.println("OK: the sorted answer words list has been saved to './answer.txt'");
            }
        }
        catch(NoSuchElementException ignored) {}
    }
}
