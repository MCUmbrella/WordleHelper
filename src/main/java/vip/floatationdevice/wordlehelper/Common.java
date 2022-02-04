package vip.floatationdevice.wordlehelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Common
{
    //regex: 5 letters
    public final static Pattern validWord = Pattern.compile("^[a-zA-Z]{5}$");
    //regex: 5 letters, a space, 5 numbers
    public final static Pattern checkResult = Pattern.compile("^[a-zA-Z]{5} [0-2]{5}$");
    //file: ./words.txt
    public final static File file = new File("words.txt");
    //words remaining to be found
    public static ArrayList<String> words = new ArrayList<String>();
    //how many times you want the program to try to find the word. Can be overridden by command line argument
    public static int maxTries = 6;

    //read words from './words.txt' and store them in ArrayList<String> words
    public static void readDictionary() throws Exception
    {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        for (String line = reader.readLine(); line != null; line = reader.readLine()) //put matched words into the ArrayList
            if (validWord.matcher(line).find()) words.add(line.toLowerCase());
        reader.close();
        System.out.println("Dictionary size: " + words.size());
    }
}
