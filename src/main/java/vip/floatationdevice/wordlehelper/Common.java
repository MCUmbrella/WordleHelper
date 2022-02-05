package vip.floatationdevice.wordlehelper;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Common
{
    public final static String PROGRAM_NAME = "WordleHelper 2.1";

    //regex: 5 letters
    public final static Pattern validWord = Pattern.compile("^[a-zA-Z]{5}$");
    //regex: 5 letters, a space, 5 numbers
    public final static Pattern checkResult = Pattern.compile("^[a-zA-Z]{5} [0-2]{5}$");
    //file: ./words.txt
    public final static File file = new File("words.txt");
    //words remaining to be found
    public static ArrayList<String> possibleWordsList = new ArrayList<String>();
    //how many times you want the program to try to find the word. Can be overridden by command line argument
    public static int maxTries = 6;
    //wordle letter block background colors
    public final static Color
            COLOR_UNSET = new Color(0x121213),
            COLOR_OFF_TARGETED = new Color(0x3a3a3c),
            COLOR_DISPLACED = new Color(0xb59f3b),
            COLOR_HIT = new Color(0x538d4e);

    //read words from './words.txt' and store them in ArrayList<String> words
    public static void readDictionary() throws Exception
    {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        for (String line = reader.readLine(); line != null; line = reader.readLine()) //put matched words into the ArrayList
            if (validWord.matcher(line).find()) possibleWordsList.add(line.toLowerCase());
        reader.close();
        System.out.println("Dictionary size: " + possibleWordsList.size());
    }

    //calculate the possible words
    public static void calculatePossibleWords(String inputWord, int[] result)
    {
        String inputWordLower = inputWord.toLowerCase();
        //remove this word if result isn't [1, 1, 1, 1, 1]
        //to prevent a possible bug
        if (!(result[0] == 1 && result[1] == 1 && result[2] == 1 && result[3] == 1 && result[4] == 1))
            possibleWordsList.remove(inputWordLower);
        for (int loc = 0; loc != 5; loc++)
        {
            switch (result[loc])
            {
                case 2://the char is in another location
                {
                    //keep the words that have the char
                    ArrayList<String> temp = new ArrayList<String>();
                    for (String word : possibleWordsList) if (word.contains(inputWordLower.charAt(loc)+"")) temp.add(word);
                    possibleWordsList = temp;
                    break;
                }
                case 1://the char is in the right location
                {
                    //keep the words that have the same char at the same location
                    ArrayList<String> temp = new ArrayList<String>();
                    for (String word : possibleWordsList) if (word.charAt(loc) == inputWordLower.charAt(loc)) temp.add(word);
                    possibleWordsList = temp;
                    break;
                }
                case 0://the char is not in the answer word
                {
                    //remove the words that have the same char that is not matched
                    ArrayList<String> temp = new ArrayList<String>();
                    for (String word : possibleWordsList) if (!word.contains(inputWordLower.charAt(loc)+"")) temp.add(word);
                    possibleWordsList = temp;
                    break;
                }
            }
        }
    }
}
