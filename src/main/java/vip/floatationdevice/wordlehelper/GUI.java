package vip.floatationdevice.wordlehelper;

import vip.floatationdevice.wordlehelper.gui.LetterBlock;
import vip.floatationdevice.wordlehelper.gui.StartupWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Random;

import static vip.floatationdevice.wordlehelper.Common.*;

public class GUI extends JFrame
{
    /** help message */
    private final static String helpText =
            "Each line accepts first 5 letters and then 5 numbers from 0 to 2.\n" +
                    "After the 5th number is typed the possible words will be calculated.\n" +
                    "Clear a line by pressing the backspace key.\n\n" +
                    "· 0 means the letter is not in the word,\n" +
                    "· 1 means the letter is at the right position,\n" +
                    "· 2 means the letter is in the wrong position.";

    /** default text for possible words field */
    private final static String initText =
            "Possible words will be shown here\n" +
                    "Enter 5 letters and then 5 numbers to update them\n" +
                    "Press the [?] button to see help message\n" +
                    "Press the [R] button or Ctrl+R to reset the program\n\n";

    /** acceptable chars: 0-2, a-z, backspace */
    private final static char[] acceptableChars = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '\b'
    };

    /** startup status */
    static boolean startupComplete = false;

    /** possible words field */
    private final JTextArea possibleWordsField = new JTextArea(initText);

    /** all letter blocks for the result board, 6 lines, 5 letters each line */
    private final LetterBlock[][] board = new LetterBlock[6][5];

    /** tries status: 'Try X / 6' */
    private final JLabel triesLabel = new JLabel("Try 0 / 6");

    /** 'words left' status */
    private final JLabel wordsLeftLabel = new JLabel();

    /** [?] button that shows the help message */
    private final JButton helpButton = new JButton("?");

    /** [R] button that resets the board, the tries and the possible words */
    private final JButton resetButton = new JButton("R");

    JPanel mainPanel = new JPanel(null);

    // current location of the letter input
    private int letterIndexLine = 0;
    private int letterIndexColumn = 0;
    // current location of the number input
    private int numberIndexLine = 0;
    private int numberIndexColumn = 0;
    // window location
    private static int windowX = Integer.MIN_VALUE;
    private static int windowY = Integer.MIN_VALUE;

    /** tries counter */
    private int tries = 0;

    public GUI()
    {
        StartupWindow startupWindow = new StartupWindow();
        if(!startupComplete)
        {
            //show a startup window
            startupWindow.setVisible(true);
        }
        //load answer dictionary and all words dictionary
        try
        {
            readAnswerWords();
            readAllWords();
            wordsLeftLabel.setText("Answer words left: " + answerWordsList.size());
            //show all words at first
            possibleWordsField.append(answerWordsList.toString());
        }
        catch(Exception e)
        {
            startupWindow.dispose(); // close the startup window
            System.out.println("Error loading dictionary:");
            e.printStackTrace();
            //show error window
            JOptionPane.showMessageDialog(null,
                    e +
                            "\n\nPlease check the dictionary file path.\nMake sure 'answer.txt' and 'all.txt' are\n in 'resources' folder or in the jar file.",
                    "Error loading dictionary",
                    JOptionPane.ERROR_MESSAGE
            );
            System.exit(-1);
        }

        //set main window
        setTitle(PROGRAM_NAME);
        setSize(640, 480);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        mainPanel.setBounds(0, 0, 640, 480);
        mainPanel.setBackground(Color.DARK_GRAY);
        add(mainPanel);
        //set result board
        initBoard();
        //set tries status
        triesLabel.setBounds(10, 320, 250, 20);
        triesLabel.setForeground(Color.WHITE);
        //set words left status
        wordsLeftLabel.setBounds(10, 350, 250, 20);
        wordsLeftLabel.setForeground(Color.WHITE);
        //set possible words field
        possibleWordsField.setBounds(270, 10, 360, 420);
        possibleWordsField.setEditable(false);
        possibleWordsField.setLineWrap(true);
        possibleWordsField.setWrapStyleWord(true);
        possibleWordsField.setBackground(new Color(96, 96, 96));
        possibleWordsField.setForeground(Color.WHITE);
        //set help and reset button
        helpButton.setBounds(10, 400, 50, 30);
        helpButton.setToolTipText("Show help message");
        resetButton.setBounds(70, 400, 50, 30);
        resetButton.setToolTipText("Reset the program");
        //set global key event listener
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyEventDispatcher);
        //set help button's action listener
        helpButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(keyEventDispatcher);
                System.out.println("help button pressed");
                //show help dialog
                JOptionPane.showMessageDialog(GUI.this,
                        helpText,
                        "Help",
                        JOptionPane.INFORMATION_MESSAGE
                );
                KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyEventDispatcher);
            }
        });
        //set reset button's action listener
        resetButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                resetGUI();
            }
        });
        //add components
        mainPanel.add(triesLabel);
        mainPanel.add(wordsLeftLabel);
        mainPanel.add(possibleWordsField);
        mainPanel.add(helpButton);
        mainPanel.add(resetButton);
        for(int i = 0; i < 6; i++)
            for(int j = 0; j < 5; j++)
            {
                mainPanel.add(board[i][j]);
                board[i][j].setBounds(j * 50 + 10, i * 50 + 10, 50, 50);
            }
        if(windowX != Integer.MIN_VALUE && windowY != Integer.MAX_VALUE) setLocation(windowX, windowY);
        //show magic
        setVisible(true);
        startupWindow.dispose();
        startupComplete = true;
    }

    public static void main(String[] args)
    {
        if(Launcher.startTime == 0)
        {
            System.err.println("You are not running WordleHelper through the Launcher class. The startup time may be inaccurate.");
            Launcher.startTime = System.currentTimeMillis();
        }
        new GUI().setVisible(true);
        System.out.println("Startup time: " + (System.currentTimeMillis() - Launcher.startTime) + "ms");
    }

    /** initialize every JLabel in the input board */
    private void initBoard()
    {
        for(int i = 0; i < 6; i++)
            for(int j = 0; j < 5; j++)
            {
                board[i][j] = new LetterBlock();
            }
    }

    /** get the word of the current line */
    private String getWord(int line)
    {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i != 5; i++)
        {
            if(!board[line][i].getText().equals("_"))
                sb.append(board[line][i].getText());
            else return null;
        }
        return sb.toString().toLowerCase();
    }

    /** get the numbers of the current line */
    private int[] getResultNumbers(int line)
    {
        int[] result = new int[5];
        for(int i = 0; i != 5; i++)
            result[i] = board[line][i].getNumber();
        return result;
    }

    /** reset a line */
    private void resetLine(int line)
    {
        for(int i = 0; i != 5; i++)
        {
            board[line][i].setText("_");
            board[line][i].setNumber(-1);
        }
        letterIndexColumn = 0;
        numberIndexColumn = 0;
    }

    /** global key event dispatcher */
    KeyEventDispatcher keyEventDispatcher = new KeyEventDispatcher()
    {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e)
        {
            if(e.getID() == KeyEvent.KEY_PRESSED)
            {
                if(e.getKeyCode() == KeyEvent.VK_F3) // F3: turn on debug mode
                {
                    mainPanel.setBorder(BorderFactory.createLineBorder(Color.red));
                    possibleWordsField.setBorder(BorderFactory.createLineBorder(Color.red));
                    triesLabel.setBorder(BorderFactory.createLineBorder(Color.red));
                    wordsLeftLabel.setBorder(BorderFactory.createLineBorder(Color.red));
                    for(LetterBlock[] t : board)
                        for(LetterBlock tt : t)
                            tt.setBorder(BorderFactory.createLineBorder(Color.red));
                    helpButton.setBorder(BorderFactory.createLineBorder(Color.red));
                    resetButton.setBorder(BorderFactory.createLineBorder(Color.red));
                    System.out.println(answerWordsList);
                    JOptionPane.showMessageDialog(GUI.this,
                            "Created by MCUmbrella (https://github.com/MCUmbrella)\n" +
                                    "This software is licensed under the MIT license and provided with absolutely no warranty.\n" +
                                    "You can go to https://github.com/MCUmbrella/WordleHelper to check out the source code,\n" +
                                    "submit code changes or initiate any issues.",
                            DBG_TITLE[new Random().nextInt(DBG_TITLE.length)],
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    return true;
                }
                if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_R) // ^R: reset
                {
                    resetGUI();
                    return true;
                }
                for(char c : acceptableChars)
                {
                    if(e.getKeyChar() == c)
                    {
                        switch(e.getKeyChar())
                        {
                            //numbers
                            case '0':
                            case '1':
                            case '2':
                            {
                                //check if the current line is filled with letters and the current block's number is not set
                                if(getWord(letterIndexLine) != null && board[numberIndexLine][numberIndexColumn].getNumber() == -1)
                                {
                                    //set the number
                                    board[numberIndexLine][numberIndexColumn].setNumber(Integer.parseInt(String.valueOf(e.getKeyChar())));
                                    //move to the next block or to the next line
                                    if(numberIndexColumn == 4)
                                    {
                                        //this line has reached the end, update possible words
                                        System.out.println("update possible words: " + getWord(letterIndexLine) + " " + Arrays.toString(getResultNumbers(numberIndexLine)));
                                        calculatePossibleWords(getWord(letterIndexLine), getResultNumbers(numberIndexLine));
                                        possibleWordsField.setText("Possible words:\n" + answerWordsList);
                                        wordsLeftLabel.setText("Answer words left: " + answerWordsList.size());
                                        //if ArrayList is empty, the game is over
                                        if(answerWordsList.size() == 0)
                                        {
                                            KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(keyEventDispatcher);
                                            triesLabel.setText("Try " + ++tries + " / 6");
                                            wordsLeftLabel.setText("No words left!");
                                            wordsLeftLabel.setForeground(Color.RED);
                                            System.err.println("no words left");
                                            JOptionPane.showMessageDialog(null,
                                                    "No words left!\n\n" +
                                                            "Is there a:\n  · problem with your input?\n  · word that is not in the dictionary?\n  · bug in the program?" +
                                                            "\n\nThe program will reset",
                                                    "No words left!",
                                                    JOptionPane.ERROR_MESSAGE
                                            );
                                            resetGUI();
                                        }
                                        //if ArrayList has only one word, that word is the result
                                        else if(answerWordsList.size() == 1)
                                        {
                                            KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(keyEventDispatcher);
                                            triesLabel.setText("Try " + ++tries + " / 6");
                                            wordsLeftLabel.setText("Answer word: " + answerWordsList.get(0));
                                            wordsLeftLabel.setForeground(Color.GREEN);
                                            System.out.println("only one word left: " + answerWordsList.get(0));
                                            JOptionPane.showMessageDialog(null,
                                                    "The word we are finding is:\n\n  · " + answerWordsList.get(0) + "\n\nThe program will reset",
                                                    "Congratulations!",
                                                    JOptionPane.INFORMATION_MESSAGE
                                            );
                                            resetGUI();
                                        }
                                        else //move to the next line
                                        {
                                            numberIndexColumn = 0;
                                            letterIndexColumn = 0;
                                            numberIndexLine++;
                                            letterIndexLine++;
                                            triesLabel.setText("Try " + ++tries + " / 6");
                                        }
                                        //if the last line is reached, the game is over
                                        if(numberIndexLine == 6)
                                        {
                                            KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(keyEventDispatcher);
                                            triesLabel.setForeground(Color.RED);
                                            System.err.println("last line reached");
                                            JOptionPane.showMessageDialog(null,
                                                    "Last line reached!\nThe program will reset",
                                                    "Last line reached!",
                                                    JOptionPane.ERROR_MESSAGE
                                            );
                                            resetGUI();
                                        }
                                    }
                                    else numberIndexColumn++;
                                }
                                else System.err.println("number typed before setting the word");
                                break;
                            }
                            //backspace
                            case '\b':
                            {
                                //reset the current line
                                resetLine(letterIndexLine);
                                System.out.println("backspace pressed. reset line " + (letterIndexLine + 1));
                                break;
                            }
                            //letters
                            default:
                            {
                                if(getWord(letterIndexLine) == null)//word is not fully typed, allow letters
                                {
                                    //set the current letter
                                    board[letterIndexLine][letterIndexColumn].setText(e.getKeyChar() + "");
                                    //move to the next letter
                                    letterIndexColumn++;
                                    if(letterIndexColumn == 5)//check if the word is in the dictionary
                                    {
                                        if(allWordsList.contains(getWord(letterIndexLine)))
                                        {
                                            //move to the first letter and start setting the numbers
                                            letterIndexColumn = 0;
                                            System.out.println(getWord(letterIndexLine) + " is in the dictionary. start setting numbers");
                                        }
                                        else
                                        {
                                            //reset the current line
                                            resetLine(letterIndexLine);
                                            //move back to the first letter
                                            letterIndexColumn = 0;
                                            System.err.println("word not in dictionary. reset line " + (letterIndexLine + 1));
                                        }
                                    }
                                }
                                else System.err.println("letter typed after setting the word");
                                break;
                            }
                        }
                    }
                }
            }
            return false;
        }
    };

    /** this is what the 'R' button does */
    void resetGUI()
    {
        System.out.println("resetting");
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(keyEventDispatcher);
        answerWordsList.clear();
        allWordsList.clear();
        windowX = getX();
        windowY = getY();
        dispose();
        System.gc();
        new GUI();
    }

    private static final String[] DBG_TITLE = {
            PROGRAM_NAME,
            "[object Object]",
            "Hello world!",
            "8412wg5d",
            "Also try Minecraft!",
            "ok",
            "YEEEEEEEEEEEHAW!",
            "undefined",
            "1145141919810",
            "Kid named debug window:",
            "LIVE DEBUG WINDOW REACTION",
            "Absolutely, 100% Lambda-free!",
            "bruh",
            "waltuh, put your f3 away waltuh",
            "",
            "@Wish-+U-&Have-#A-@Nice-~Day!",
            "*vine boom sound effect*",
            "DBG_TITLE[new Random().nextInt(DBG_TITLE.length)]",
            "null",
            "owo whats this?",
            "F**K NYT!",
            "Also try Guilded4J!",
            "Never gonna give you up",
            "The funny",
            "???? ?? ???? ?? ??? ???",
            "wo ak le",
            "A wild debug window appears!"
    };
}
