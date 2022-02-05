package vip.floatationdevice.wordlehelper;

import java.util.NoSuchElementException;
import java.util.Scanner;

import static vip.floatationdevice.wordlehelper.Common.*;

public class CLI
{
    //scanner for input
    final static Scanner s = new Scanner(System.in);

    public static void main(String[] args)
    {
        try
        {
            Common.readDictionary();
        }
        catch (Exception e)
        {
            System.out.println("Failed to read 'words.txt'. Have you put it under your workdir?\nYou can get the file from 'https://github.com/MCUmbrella/AWordle/tree/main/dictionary'");
            e.printStackTrace();
            System.exit(-1);
        }
        if (args.length > 1)
            try
            {
                maxTries = Integer.parseInt(args[1]);
                if(maxTries < 1) throw new NumberFormatException();
            }
            catch (Exception e)
            {
                System.out.println("Argument must be a positive integer!");
                e.printStackTrace();
                System.exit(-1);
            }
        System.out.println(
                "Enter a Wordle check result with 5 letters, a space and 5 numbers from 0 to 2.\n" +
                "Example: apple 01002\n" +
                "Explaination:\n" +
                "  0 means the letter is not in the word,\n" +
                "  1 means the letter is at the right position,\n" +
                "  2 means the letter is in the wrong position."
        );
        try
        {
            for(int i = 0; i != maxTries;)
            {
                System.out.print("Enter check result (try " + (i + 1) + "/" + maxTries + "): ");
                String input = s.nextLine().toLowerCase();
                if(checkResult.matcher(input).find() && possibleWordsList.contains(input.substring(0, 5))) //if the input is valid
                {
                    String inputWord = input.substring(0, 5);
                    int[] result = new int[5];
                    for(int j = 0; j != 5; j++) result[j] = Integer.parseInt(input.substring(j + 6, j + 7));
                    calculatePossibleWords(inputWord, result);
                    if(possibleWordsList.size() == 0)
                    {
                        System.out.println("No words left!\nIs there a:\n  · problem with your input?\n  · word that is not in the dictionary?\n  · bug in the program?");
                        System.exit(0);
                    }
                    else if (possibleWordsList.size() == 1)
                    {
                        System.out.println("The word is: " + possibleWordsList.get(0));
                        System.exit(0);
                    }
                    else
                    {
                        System.out.println("Possible words: " + possibleWordsList);
                        System.out.println("Words left: " + possibleWordsList.size());
                    }
                    i++;
                }
                else
                {
                    System.out.print("Invalid input. Try again? [y/N]\n? ");
                    if(s.nextLine().equalsIgnoreCase("y")) continue;
                    else
                    {
                        System.out.println("Exiting");
                        System.exit(0);
                    }
                }
            }
        }
        catch (NoSuchElementException e)//someone press ctrl+d !?/!1?!?1?!?1!?!?
        {
            System.out.println("\nExiting");
            System.exit(0);
        }
        System.out.println("Max tries exceeded. The program will exit.\n");
    }
}
