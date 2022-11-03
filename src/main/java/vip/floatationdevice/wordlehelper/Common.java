package vip.floatationdevice.wordlehelper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Pattern;

public class Common
{
    public final static String PROGRAM_NAME = "WordleHelper 2.2.1";

    /** regex: 5 letters */
    public final static Pattern validWord = Pattern.compile("^[a-zA-Z]{5}$");

    /** file: answer words file 'common.txt' in the jar */
    public final static String answerWordsFile = "/common.txt";

    /** file: all words file 'all.txt' in the jar */
    public final static String allWordsFile = "/all.txt";

    /** possible answer words (from common.txt) */
    public static LinkedList<String> answerWordsList = new LinkedList<>();

    /** all accepted words (from all.txt) */
    public static LinkedList<String> allWordsList = new LinkedList<String>();

    /** read words from 'common.txt' and store them in answerWordsList */
    public static void readAnswerWords() throws Exception
    {
        InputStream is = Common.class.getResourceAsStream(answerWordsFile);
        if(is == null) throw new Exception("Could not read file '" + answerWordsFile + "'");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(isr);
        //put matched words into possibleWordsList
        for(String line = reader.readLine(); line != null; line = reader.readLine())
            if(validWord.matcher(line).find()) answerWordsList.add(line.toLowerCase());
        reader.close();
        isr.close();
        is.close();
        System.out.println("Answer words dictionary size: " + answerWordsList.size());
    }

    /** read words from 'all.txt' and store them in allWordsList */
    public static void readAllWords() throws Exception
    {
        InputStream is = Common.class.getResourceAsStream(allWordsFile);
        if(is == null) throw new Exception("Could not read file '" + allWordsFile + "'");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(isr);
        //put matched words into allWordsList
        for(String line = reader.readLine(); line != null; line = reader.readLine())
            if(validWord.matcher(line).find()) allWordsList.add(line.toLowerCase());
        reader.close();
        isr.close();
        is.close();
        System.out.println("All words dictionary size: " + allWordsList.size());
    }

    /** calculate the possible words */
    public static void calculatePossibleWords(String inputWord, int[] result)
    {
        String inputWordLower = inputWord.toLowerCase();
        //remove the word that have been identified as not being the answer
        if(!(result[0] == 1 && result[1] == 1 && result[2] == 1 && result[3] == 1 && result[4] == 1))
            answerWordsList.remove(inputWordLower);
        //calculation begins
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
}
