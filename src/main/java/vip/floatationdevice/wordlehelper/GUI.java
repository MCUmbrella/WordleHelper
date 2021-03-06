package vip.floatationdevice.wordlehelper;

import vip.floatationdevice.wordlehelper.gui.LetterBlock;
import vip.floatationdevice.wordlehelper.gui.StartupWindow;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import static vip.floatationdevice.wordlehelper.Common.*;

public class GUI extends JFrame
{
    /*
     * The perfect layout of the window
     *           - but I can't get there
     *
     * +-------------------------------+
     * | WordleHelper 1.1    [R][-][X] |
     * +-----------+-------------------+
     * | _ _ _ _ _ |  Possible words:  |
     * | _ _ _ _ _ | xxxxx xxxxx xxxxx |
     * | _ _ _ _ _ | xxxxx xxxxx xxxxx |
     * | _ _ _ _ _ | xxxxx xxxxx xxxxx |
     * | _ _ _ _ _ | xxxxx xxxxx xxxxx |
     * | _ _ _ _ _ | xxxxx xxxxx xxxxx |
     * +-----------+-------------------+
     * | Try {}/6  | {} words left [?] |
     * +-------------------------------+
     */

    //help message
    private final static String helpText =
            "Each line accepts first 5 letters and then 5 numbers from 0 to 2.\n" +
                    "After the 5th number is typed the possible words will be calculated.\n" +
                    "Clear a line by pressing the backspace key.\n\n" +
                    "· 0 means the letter is not in the word,\n" +
                    "· 1 means the letter is at the right position,\n" +
                    "· 2 means the letter is in the wrong position.";
    //default text for possible words field
    private final static String initText =
            "Possible words will be shown here\n" +
                    "Enter 5 letters and then 5 numbers to update them\n" +
                    "Press the '?' button to see help message\n" +
                    "Press the 'R' button to reset the program\n\n";
    //acceptable chars: 0-2, a-z, backspace
    private final static char[] acceptableChars = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '\b'
    };
    //startup status
    static boolean startupComplete = false;
    //possible words field
    private final JTextArea possibleWordsField = new JTextArea(initText);
    //result board
    JPanel resultBoard = new JPanel(null);
    //all letter blocks for the result board, 6 lines, 5 letters each line
    private final LetterBlock[][] board = new LetterBlock[6][5];
    //current location of the letter input (1-25)
    private int letterIndexLine = 0;
    private int letterIndexColumn = 0;
    //current location of the number input (1-25)
    private int numberIndexLine = 0;
    private int numberIndexColumn = 0;
    //tries status
    private final JLabel triesLabel = new JLabel("Try 0 / 6");
    //tries counter
    private int tries = 0;
    //'words left' status
    private final JLabel wordsLeftLabel = new JLabel();
    //[?] button that shows the help message
    private final JButton helpButton = new JButton("?");
    //[R] button that resets the board, the tries and the possible words
    private final JButton resetButton = new JButton("R");

    //set every JLabel in the board with '_'
    private void initBoard()
    {
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 5; j++)
            {
                board[i][j] = new LetterBlock();
            }
    }

    //get the word of the current line
    private String getWord(int line)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i != 5; i++)
        {
            if (!board[line][i].getText().equals("_"))
                sb.append(board[line][i].getText());
            else return null;
        }
        return sb.toString().toLowerCase();
    }

    //get the numbers of the current line
    private int[] getResultNumbers(int line)
    {
        int[] result = new int[5];
        for (int i = 0; i != 5; i++)
            //if(board[line][i].numberSet)
            result[i] = board[line][i].getNumber();
        //else return null;
        return result;
    }

    //reset a line
    private void resetLine(int line)
    {
        for (int i = 0; i != 5; i++)
        {
            board[line][i].setText("_");
            board[line][i].setNumber(0);
            board[line][i].numberSet = false;
            board[line][i].setBackground(LetterBlock.COLOR_UNSET);
            letterIndexColumn = 0;
            numberIndexColumn = 0;
        }
    }

    //this is what the 'R' button does
    void resetGUI()
    {
        System.out.println("resetting");
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(keyEventDispatcher);
        answerWordsList.clear();
        allWordsList.clear();
        dispose();
        new GUI();
    }

    //global key event dispatcher
    KeyEventDispatcher keyEventDispatcher = new KeyEventDispatcher()
    {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e)
        {
            if (e.getID() == KeyEvent.KEY_PRESSED)
                for (char c : acceptableChars)
                    if (e.getKeyChar() == c)
                    {
                        switch (e.getKeyChar())
                        {
                            //numbers
                            case '0':
                            case '1':
                            case '2':
                            {
                                //check if the current line is filled with letters and the current block's number is not set
                                if (getWord(letterIndexLine) != null && !board[numberIndexLine][numberIndexColumn].numberSet)
                                {
                                    //set the number
                                    board[numberIndexLine][numberIndexColumn].setNumber(Integer.parseInt(String.valueOf(e.getKeyChar())));
                                    board[numberIndexLine][numberIndexColumn].numberSet = true;
                                    //move to the next block or to the next line
                                    if (numberIndexColumn == 4)
                                    {
                                        //update the possible words
                                        System.out.println("update possible words: " + getWord(letterIndexLine) + " " + Arrays.toString(getResultNumbers(numberIndexLine)));
                                        calculatePossibleWords(getWord(letterIndexLine), getResultNumbers(numberIndexLine));
                                        possibleWordsField.setText("Possible words:\n" + answerWordsList);
                                        wordsLeftLabel.setText("Words left: " + answerWordsList.size());
                                        //if ArrayList is empty, the game is over
                                        if (answerWordsList.size() == 0)
                                        {
                                            System.out.println("no words left");
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
                                        else if (answerWordsList.size() == 1)
                                        {
                                            triesLabel.setText("Try " + ++tries + " / " + "6");
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
                                            triesLabel.setText("Try " + ++tries + " / " + "6");
                                            //tries++;
                                        }
                                        //if the last line is reached, the game is over
                                        if (numberIndexLine == 6)
                                        {
                                            System.out.println("last line reached");
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
                                else System.out.println("number typed before setting the word");
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
                                //set the current letter
                                board[letterIndexLine][letterIndexColumn].setText(e.getKeyChar() + "");
                                //move to the next letter
                                letterIndexColumn++;
                                if (letterIndexColumn == 5)//check if the word is in the dictionary
                                {
                                    if (allWordsList.contains(getWord(letterIndexLine)))
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
                                        System.out.println("word not in dictionary. reset line " + (letterIndexLine + 1));
                                    }
                                }
                                break;
                            }
                        }
                    }
            return false;
        }
    };

    //constructor
    public GUI()
    {
        StartupWindow startupWindow = new StartupWindow();
        if (!startupComplete)
        {
            //show a startup window
            startupWindow.setVisible(true);
            /*try {Thread.sleep(1000); //magic, don't touch
            }catch (InterruptedException e){e.printStackTrace();System.exit(-1);}*/
        }
        //load answer dictionary and all words dictionary
        try
        {
            readAnswerWords();
            readAllWords();
            wordsLeftLabel.setText(answerWordsList.size() + " words left");
            //show all words at first
            possibleWordsField.append(answerWordsList.toString());
        } catch (Exception e)
        {
            startupWindow.dispose();
            System.out.println("Error loading dictionary:");
            e.printStackTrace();
            //show error window
            JOptionPane.showMessageDialog(null,
                    e +
                            "\n\nPlease check the dictionary file path.\nMake sure 'common.txt' and 'all.txt' are\n in 'resources' folder or in the jar file.",
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
        resultBoard.setBounds(0, 0, 640, 480);
        resultBoard.setBackground(Color.DARK_GRAY);
        add(resultBoard);
        //set result board
        initBoard();
        //set tries status
        triesLabel.setBounds(10, 330, 100, 20);
        triesLabel.setForeground(Color.WHITE);
        //set words left status
        wordsLeftLabel.setBounds(110, 330, 100, 20);
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
        resetButton.setBounds(70, 400, 50, 30);
        //set global key event listener
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyEventDispatcher);
        //set help button's action listener
        helpButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                //debug
                System.out.println("help button pressed");
                //show help dialog
                JOptionPane.showMessageDialog(GUI.this,
                        helpText,
                        "Help",
                        JOptionPane.INFORMATION_MESSAGE);
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
        resultBoard.add(triesLabel);
        resultBoard.add(wordsLeftLabel);
        resultBoard.add(possibleWordsField);
        resultBoard.add(helpButton);
        resultBoard.add(resetButton);
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 5; j++)
            {
                resultBoard.add(board[i][j]);
                board[i][j].setBounds(j * 50 + 10, i * 50 + 10, 50, 50);
            }
        //show magic
        setVisible(true);
        startupWindow.dispose();
        startupComplete = true;
    }


    public static void main(String[] args)
    {
        if (Launcher.startTime == 0)
        {
            System.err.println("You are not running WordleHelper through the Launcher class. The startup time may be inaccurate.");
            Launcher.startTime = System.currentTimeMillis();
        }
        new GUI().setVisible(true);
        System.out.println("Startup time: " + (System.currentTimeMillis() - Launcher.startTime) + "ms");
    }
}
