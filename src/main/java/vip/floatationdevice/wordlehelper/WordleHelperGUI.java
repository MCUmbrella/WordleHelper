package vip.floatationdevice.wordlehelper;

import vip.floatationdevice.wordlehelper.gui.LetterBlock;
import vip.floatationdevice.wordlehelper.gui.StartupWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import static vip.floatationdevice.wordlehelper.WordleHelper.*;

/**
 * Main GUI class for WordleHelper.
 */
public class WordleHelperGUI extends JFrame
{
    /** help message */
    private final static String HELP_TEXT =
            "Each line accepts first 5 letters and then 5 numbers from 0 to 2.\n" +
                    "After the 5th number is typed the possible words will be calculated.\n" +
                    "Clear a line by pressing the backspace key.\n\n" +
                    "· 0 means the letter is not in the word,\n" +
                    "· 1 means the letter is at the right position,\n" +
                    "· 2 means the letter is in the wrong position.";

    /** about message */
    private static final String ABOUT_TEXT =
            PROGRAM_NAME + '\n' +
                    "https://github.com/MCUmbrella/WordleHelper\n" +
                    "This software is licensed under the MIT license and provided with absolutely no warranty.\n" +
                    "You can go to https://github.com/MCUmbrella/WordleHelper to check out the source code,\n" +
                    "submit code changes or initiate any issues.";

    /** default text for possible words field */
    private final static String INIT_TEXT =
            "Possible words will be shown here\n" +
                    "Enter 5 letters and then 5 numbers to update them\n" +
                    "Press the [?] button to see help message\n" +
                    "Press the [R] button or Ctrl+R to reset the program\n\n";

    /** acceptable chars: 0-2, a-z, backspace */
    private final static char[] ACCEPTABLE_CHARS = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '\b'
    };

    /** used to ensure that the startup window is only shown once */
    private static boolean startupComplete = false;

    /** possible words field */
    private final JTextArea possibleWordsField = new JTextArea(INIT_TEXT);

    /** all letter blocks for the result board, 6 lines, 5 letters each line */
    private final LetterBlock[][] board = new LetterBlock[6][5];

    /** tries status: 'Try X / 6' */
    private final JLabel triesLabel = new JLabel("Try 0 / 6");

    /** 'words left' status */
    private final JLabel wordsLeftLabel = new JLabel();

    /** global key event dispatcher */
    private final KeyEventDispatcher keyEventDispatcher;

    /** current location of the letter input */
    private int letterIndexLine = 0, letterIndexColumn = 0;

    /** current location of the number input */
    private int numberIndexLine = 0, numberIndexColumn = 0;

    /** last window location, used in resetting */
    private static int lastWindowX, lastWindowY;

    /** tries counter */
    private int tries = 0;

    /** KeyboardFocusManager used by the WordleHelperGUI instance */
    private final KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();

    public WordleHelperGUI()
    {
        StartupWindow startupWindow = null;
        if(!startupComplete)
        {
            startupWindow = new StartupWindow();
            startupWindow.setVisible(true);
        }

        // load answer dictionary and all words dictionary
        try
        {
            String[] remaining = init();
            wordsLeftLabel.setText("Answer words left: " + remaining.length);
            // show all words at first
            possibleWordsField.append(Arrays.toString(remaining));
        }
        catch(Exception e)
        {
            System.err.println("Error loading dictionary:");
            e.printStackTrace();
            // show error window
            JOptionPane.showMessageDialog(
                    WordleHelperGUI.this,
                    e +
                            "\n\nPlease check the dictionary file path.\nMake sure 'answer.txt' and 'all.txt' are\n in 'resources' folder or in the jar file.",
                    "Error loading dictionary",
                    JOptionPane.ERROR_MESSAGE
            );
            System.exit(-1);
        }

        // setup main window
        setTitle(PROGRAM_NAME);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        JPanel mainPanel = new JPanel(null);
        mainPanel.setBounds(0, 0, 640, 440);
        mainPanel.setBackground(Color.DARK_GRAY);
        add(mainPanel);

        // setup menu bar
        setJMenuBar(new JMenuBar()
        {{
            add(new JMenu("Game")
            {{
                setMnemonic('g');
                add(new JMenuItem("Reset")
                {{
                    setMnemonic('r');
                    addActionListener(new ActionListener()
                    {
                        @Override
                        public void actionPerformed(ActionEvent e)
                        {
                            resetGUI();
                        }
                    });
                }});
                add(new JMenuItem("Exit")
                {{
                    setMnemonic('e');
                    addActionListener(new ActionListener()
                    {
                        @Override
                        public void actionPerformed(ActionEvent e)
                        {
                            if(JOptionPane.showConfirmDialog(
                                    WordleHelperGUI.this,
                                    "Really quit WordleHelper?",
                                    "Confirm",
                                    JOptionPane.YES_NO_OPTION
                            ) == JOptionPane.YES_OPTION)
                                dispose();
                        }
                    });
                }});
            }});
            add(new JMenu("Help")
            {{
                setMnemonic('h');
                add(new JMenuItem("Help")
                {{
                    setMnemonic('h');
                    addActionListener(new ActionListener()
                    {
                        @Override
                        public void actionPerformed(ActionEvent e)
                        {
                            showHelpMessage();
                        }
                    });
                }});
                add(new JMenuItem("About")
                {{
                    setMnemonic('a');
                    addActionListener(new ActionListener()
                    {
                        @Override
                        public void actionPerformed(ActionEvent e)
                        {
                            showAboutMessage();
                        }
                    });
                }});
            }});
        }});

        // setup result board
        initBoard();

        // setup tries status
        triesLabel.setBounds(10, 320, 250, 20);
        triesLabel.setForeground(Color.WHITE);

        // setup words left status
        wordsLeftLabel.setBounds(10, 350, 250, 20);
        wordsLeftLabel.setForeground(Color.WHITE);

        // setup possible words field
        possibleWordsField.setBounds(270, 10, 360, 420);
        possibleWordsField.setEditable(false);
        possibleWordsField.setLineWrap(true);
        possibleWordsField.setWrapStyleWord(true);
        possibleWordsField.setBackground(new Color(96, 96, 96));
        possibleWordsField.setForeground(Color.WHITE);

        // setup help button
        JButton helpButton = new JButton("?")
        {{
            setBounds(10, 400, 50, 30);
            setToolTipText("Show help message");
            addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    showHelpMessage();
                }
            });
        }};

        // setup reset button
        JButton resetButton = new JButton("R")
        {{
            setBounds(70, 400, 50, 30);
            setToolTipText("Reset the program");
            addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    resetGUI();
                }
            });
        }};

        // add components
        mainPanel.add(triesLabel);
        mainPanel.add(wordsLeftLabel);
        mainPanel.add(possibleWordsField);
        mainPanel.add(helpButton);
        mainPanel.add(resetButton);
        for(int i = 0; i < 6; i++)
            for(int j = 0; j < 5; j++)
            {
                mainPanel.add(board[i][j]);
                board[i][j].setLocation(j * 50 + 10, i * 50 + 10);
            }

        // setup global key event listener
        keyEventDispatcher = new KeyEventDispatcher()
        {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e)
            {
                if(e.getID() == KeyEvent.KEY_PRESSED)
                {
                    if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_R) // ^R: reset
                    {
                        resetGUI();
                        return true;
                    }
                    for(char c : ACCEPTABLE_CHARS)
                    {
                        if(e.getKeyChar() == c)
                        {
                            switch(e.getKeyChar())
                            {
                                // numbers
                                case '0':
                                case '1':
                                case '2':
                                {
                                    // check if the current line is filled with letters and the current block's number is not set
                                    if(getWord(letterIndexLine) != null && board[numberIndexLine][numberIndexColumn].getNumber() == -1)
                                    {
                                        // set the number
                                        board[numberIndexLine][numberIndexColumn].setNumber(Integer.parseInt(String.valueOf(e.getKeyChar())));
                                        //move to the next block or to the next line
                                        if(numberIndexColumn == 4)
                                        {
                                            // this line has reached the end, update possible words
                                            System.out.println("update possible words: " + getWord(letterIndexLine) + " " + Arrays.toString(getResultNumbers(numberIndexLine)));
                                            String[] remaining = calculatePossibleWords(getWord(letterIndexLine), getResultNumbers(numberIndexLine));
                                            possibleWordsField.setText("Possible words:\n" + Arrays.toString(remaining));
                                            wordsLeftLabel.setText("Answer words left: " + remaining.length);
                                            // if remaining words list is empty, the game is over
                                            if(remaining.length == 0)
                                            {
                                                kfm.removeKeyEventDispatcher(keyEventDispatcher);
                                                triesLabel.setText("Try " + ++tries + " / 6");
                                                wordsLeftLabel.setText("No words left!");
                                                wordsLeftLabel.setForeground(Color.RED);
                                                System.err.println("no words left");
                                                JOptionPane.showMessageDialog(
                                                        WordleHelperGUI.this,
                                                        "No words left!\n\n" +
                                                                "Is there a:\n  · problem with your input?\n  · word that is not in the dictionary?\n  · bug in the program?" +
                                                                "\n\nThe program will reset",
                                                        "No words left!",
                                                        JOptionPane.ERROR_MESSAGE
                                                );
                                                resetGUI();
                                            }
                                            // if remaining words has only one word left, that word is the answer
                                            else if(remaining.length == 1)
                                            {
                                                kfm.removeKeyEventDispatcher(keyEventDispatcher);
                                                triesLabel.setText("Try " + ++tries + " / 6");
                                                wordsLeftLabel.setText("Answer word: " + remaining[0]);
                                                wordsLeftLabel.setForeground(Color.GREEN);
                                                System.out.println("only one word left: " + remaining[0]);
                                                JOptionPane.showMessageDialog(
                                                        WordleHelperGUI.this,
                                                        "The word we are finding is:\n\n  · " + remaining[0] + "\n\nThe program will reset",
                                                        "Congratulations!",
                                                        JOptionPane.INFORMATION_MESSAGE
                                                );
                                                resetGUI();
                                            }
                                            else // move to the next line
                                            {
                                                numberIndexColumn = 0;
                                                letterIndexColumn = 0;
                                                numberIndexLine++;
                                                letterIndexLine++;
                                                triesLabel.setText("Try " + ++tries + " / 6");
                                            }
                                            // if the last line is reached, the game is over
                                            if(numberIndexLine == 6)
                                            {
                                                kfm.removeKeyEventDispatcher(keyEventDispatcher);
                                                triesLabel.setForeground(Color.RED);
                                                System.err.println("last line reached");
                                                JOptionPane.showMessageDialog(
                                                        WordleHelperGUI.this,
                                                        "Last line reached!\nThe program will reset",
                                                        "Last line reached!",
                                                        JOptionPane.ERROR_MESSAGE
                                                );
                                                resetGUI();
                                            }
                                        }
                                        else numberIndexColumn++;
                                    }
                                    else
                                        System.err.println("number typed before setting the word");
                                    break;
                                }
                                // backspace
                                case '\b':
                                {
                                    // reset the current line
                                    resetLine(letterIndexLine);
                                    System.out.println("backspace pressed. reset line " + (letterIndexLine + 1));
                                    break;
                                }
                                // letters
                                default:
                                {
                                    // word is not fully typed, allow letters
                                    if(getWord(letterIndexLine) == null)
                                    {
                                        // set the current letter
                                        board[letterIndexLine][letterIndexColumn].setText(e.getKeyChar() + "");
                                        // move to the next letter
                                        letterIndexColumn++;
                                        // check if the word is in the dictionary
                                        if(letterIndexColumn == 5)
                                        {
                                            if(isValidWord(getWord(letterIndexLine)))
                                            {
                                                // move to the first letter and start setting the numbers
                                                letterIndexColumn = 0;
                                                System.out.println(getWord(letterIndexLine) + " is in the dictionary. start setting numbers");
                                            }
                                            else
                                            {
                                                // invalid word. reset the current line
                                                resetLine(letterIndexLine);
                                                // move back to the first letter
                                                letterIndexColumn = 0;
                                                System.err.println("word not in dictionary. reset line " + (letterIndexLine + 1));
                                            }
                                        }
                                    }
                                    // the word has been set
                                    else
                                        System.err.println("letter typed after setting the word");
                                    break;
                                }
                            }
                        }
                    }
                }
                return false;
            }
        };
        kfm.addKeyEventDispatcher(keyEventDispatcher);

        // show the window
        getContentPane().setPreferredSize(mainPanel.getSize());
        pack();
        setLocationRelativeTo(null);
        if(startupComplete)
            setLocation(lastWindowX, lastWindowY);
        else
        {
            startupComplete = true;
            startupWindow.dispose();
        }
        setVisible(true);
    }

    public static void main(String[] args)
    {
        if(Launcher.startTime == 0)
        {
            System.err.println("You are not running WordleHelper through the Launcher class. The startup time may be inaccurate.");
            Launcher.startTime = System.currentTimeMillis();
        }
        new WordleHelperGUI().setVisible(true);
        System.out.println("Startup time: " + (System.currentTimeMillis() - Launcher.startTime) + "ms");
    }

    private void initBoard()
    {
        for(int i = 0; i < 6; i++)
            for(int j = 0; j < 5; j++)
                board[i][j] = new LetterBlock();
    }

    /**
     * Get the word on the specified line of the result board.
     * @param line The line number (0-5).
     * @return The word on the specified line, or null if the line is incomplete.
     */
    private String getWord(int line)
    {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i != 5; i++)
        {
            if(!board[line][i].getText().equals("_"))
                sb.append(board[line][i].getText());
            else
                return null;
        }
        return sb.toString().toLowerCase();
    }

    /**
     * Get an array containing the check result on the specified line of the result board.
     * @param line The line number (0-5).
     * @return An array with the size of 5, the element can be 0, 1 and 2.
     */
    private int[] getResultNumbers(int line)
    {
        int[] result = new int[5];
        for(int i = 0; i != 5; i++)
            result[i] = board[line][i].getNumber();
        return result;
    }

    /**
     * Reset the specified line of the result board.
     * @param line The line number (0-5).
     */
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

    /**
     * Reset the GUI by destroying the current instance and creating a new one.
     */
    void resetGUI()
    {
        System.out.println("resetting");
        kfm.removeKeyEventDispatcher(keyEventDispatcher);
        lastWindowX = getX();
        lastWindowY = getY();
        dispose();
        System.gc();
        new WordleHelperGUI();
    }

    /**
     * Display a dialog containing the help message.
     */
    private void showHelpMessage()
    {
        kfm.removeKeyEventDispatcher(keyEventDispatcher);
        JOptionPane.showMessageDialog(
                WordleHelperGUI.this,
                HELP_TEXT,
                "Help",
                JOptionPane.INFORMATION_MESSAGE
        );
        kfm.addKeyEventDispatcher(keyEventDispatcher);
    }

    /**
     * Display a dialog containing the about message.
     */
    private void showAboutMessage()
    {
        kfm.removeKeyEventDispatcher(keyEventDispatcher);
        JOptionPane.showMessageDialog(
                WordleHelperGUI.this,
                ABOUT_TEXT,
                "About",
                JOptionPane.INFORMATION_MESSAGE
        );
        kfm.addKeyEventDispatcher(keyEventDispatcher);
    }
}
