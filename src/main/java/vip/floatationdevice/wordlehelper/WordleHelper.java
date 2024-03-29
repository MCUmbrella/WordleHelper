package vip.floatationdevice.wordlehelper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Pattern;

public class WordleHelper
{
    private WordleHelper(){throw new UnsupportedOperationException();}

    public final static String PROGRAM_NAME = "WordleHelper 2.4.0";

    // regex: 5 letters
    private final static Pattern VALID_WORD = Pattern.compile("^[a-zA-Z]{5}$");

    // answer words file 'answer.txt' in the jar
    private final static String ANSWER_WORDS_PATH = "/answer.txt";

    // all words file 'all.txt' in the jar
    private final static String ALL_WORDS_PATH = "/all.txt";

    // list of possible answer words (from answer.txt)
    private static LinkedList<String> answerWordsList = new LinkedList<>();

    // list of all acceptable words (from all.txt)
    private final static LinkedList<String> allWordsList = new LinkedList<>();

    // read words from 'answer.txt' and store them in answerWordsList
    private static void readAnswerWords() throws Exception
    {
        InputStream is = WordleHelper.class.getResourceAsStream(ANSWER_WORDS_PATH);
        if(is == null) throw new Exception("Could not read file '" + ANSWER_WORDS_PATH + "'");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(isr);
        for(String line = reader.readLine(); line != null; line = reader.readLine())
            if(VALID_WORD.matcher(line).find()) answerWordsList.add(line.toLowerCase());
        reader.close();
        isr.close();
        is.close();
        System.out.println("Answer words dictionary size: " + answerWordsList.size());
    }

    // read words from 'all.txt' and store them in allWordsList
    private static void readAllWords() throws Exception
    {
        InputStream is = WordleHelper.class.getResourceAsStream(ALL_WORDS_PATH);
        if(is == null) throw new Exception("Could not read file '" + ALL_WORDS_PATH + "'");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(isr);
        for(String line = reader.readLine(); line != null; line = reader.readLine())
            if(VALID_WORD.matcher(line).find()) allWordsList.add(line.toLowerCase());
        reader.close();
        isr.close();
        is.close();
        System.out.println("All words dictionary size: " + allWordsList.size());
    }

    /**
     * get the String array containing the remaining answer words
     */
    public static String[] getRemainingWords()
    {
        String[] remaining = new String[answerWordsList.size()];
        int i = 0;
        for(String s : answerWordsList)
        {
            remaining[i] = s;
            i++;
        }
        return remaining;
    }

    /**
     * initialize or reset WordleHelper
     * @return the initial answer words array
     * @throws Exception when fail to read dictionary or something else happen
     */
    public static String[] init() throws Exception
    {
        if(allWordsList.size() == 0) readAllWords();
        answerWordsList.clear();
        readAnswerWords();
        return getRemainingWords();
    }

    /**
     * check if the provided string is in the acceptable words' dictionary.
     * @param s the string to be checked
     * @return true if the string is a valid word, false otherwise
     */
    public static boolean isValidWord(String s)
    {
        return allWordsList.contains(s);
    }

    /**
     * calculate the possible words
     * @return the String array that contains the remaining words
     * @throws IllegalStateException when the remaining answer words is less than 2
     */
    public static String[] calculatePossibleWords(String inputWord, int[] result)
    {
        if(answerWordsList.size() < 2) throw new IllegalStateException("No more words to be calculated");
        String inputWordLower = inputWord.toLowerCase();
        // check if the word has a letter that appeared more than once
        boolean hasDuplicateLetter = false;
        byte[] appears = new byte[26];
        for(char c : inputWordLower.toCharArray())
            appears[c - 'a']++;
        for(byte b : appears)
            if(b > 1)
            {
                hasDuplicateLetter = true;
                break;
            }
        //remove the word that have been identified as not being the answer
        if(!(result[0] == 1 && result[1] == 1 && result[2] == 1 && result[3] == 1 && result[4] == 1))
            answerWordsList.remove(inputWordLower);
        //calculation begins
        if(hasDuplicateLetter)
        {
            // use a more 'conservative' algorithm. it doesnt have problems when there are duplicate letters in the word
            for(int loc = 0; loc != 5; loc++)
            {
                switch(result[loc])
                {
                    case 2://the char is in another location
                    {
                        //keep the words that have the char
                        for(Iterator<String> it = answerWordsList.iterator(); it.hasNext(); )
                            if(it.next().charAt(loc) == inputWordLower.charAt(loc)) it.remove();
                        for(Iterator<String> it = answerWordsList.iterator(); it.hasNext(); )
                            if(!it.next().contains(inputWordLower.charAt(loc) + "")) it.remove();
                        break;
                    }
                    case 1://the char is in the right location
                    {
                        //keep the words that have the same char at the same location
                        for(Iterator<String> it = answerWordsList.iterator(); it.hasNext(); )
                            if(it.next().charAt(loc) != inputWordLower.charAt(loc)) it.remove();
                        break;
                    }
                    case 0://the char is not in the answer word
                    {
                        //remove the words that have the same char that is not matched
                        for(Iterator<String> it = answerWordsList.iterator(); it.hasNext(); )
                            if(it.next().charAt(loc) == inputWordLower.charAt(loc)) it.remove();
                        break;
                    }
                }
            }
        }
        else
        {
            // use a more 'aggressive' algorithm that was used by default before WH 2.2.0
            for(int loc = 0; loc != 5; loc++)
            {
                switch(result[loc])
                {
                    case 2://the char is in another location
                    {
                        //keep the words that have the char
                        LinkedList<String> temp = new LinkedList<>();
                        for(String word : answerWordsList)
                            if(word.contains(inputWordLower.charAt(loc) + "")) temp.add(word);
                        //and remove the words that have the char in this location
                        for(Iterator<String> it = temp.iterator(); it.hasNext(); )
                            if(it.next().charAt(loc) == inputWordLower.charAt(loc)) it.remove();
                        answerWordsList = temp;
                        break;
                    }
                    case 1://the char is in the right location
                    {
                        //keep the words that have the same char at the same location
                        LinkedList<String> temp = new LinkedList<>();
                        for(String word : answerWordsList)
                            if(word.charAt(loc) == inputWordLower.charAt(loc)) temp.add(word);
                        answerWordsList = temp;
                        break;
                    }
                    case 0://the char is not in the answer word
                    {
                        //remove the words that have the same char that is not matched
                        LinkedList<String> temp = new LinkedList<>();
                        for(String word : answerWordsList)
                            if(!word.contains(inputWordLower.charAt(loc) + "")) temp.add(word);
                        answerWordsList = temp;
                        break;
                    }
                }
            }
        }
        String[] remaining = new String[answerWordsList.size()];
        int i = 0;
        for(String s : answerWordsList)
        {
            remaining[i] = s;
            i++;
        }
        return remaining;
    }
}
