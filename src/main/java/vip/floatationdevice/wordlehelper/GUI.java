package vip.floatationdevice.wordlehelper;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import static vip.floatationdevice.wordlehelper.Main.*;

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
    *
    * Help:
    *   Each line accepts first 5 letters and then 5 numbers from 0 to 2.
    *   After the 5th number is typed the possible words will be calculated.
    *   Redo by pressing the backspace key.
    */

    //acceptable chars: 0-2, a-z, backspace
    char[] acceptableChars = "abcdefghijklmnopqrstuvwxyz012\b".toCharArray();
    //possible words field
    private final JTextArea possibleWords=new JTextArea("Possible words will be shown here\nEnter your first 5 letters and then 5 numbers to see them\nPress the '?' button on the bottom left for help");
    //result board, 6 lines, 5 letters each line
    private final JLabel[][] board=new JLabel[6][5];
    //tries status
    private final JLabel tries=new JLabel("Try {}/6");
    //'words left' status
    private final JLabel wordsLeft=new JLabel("{} words left");
    //[?] button that shows the help message
    private final JButton help=new JButton("?");
    //[R] button that resets the board, the tries and the possible words
    private final JButton reset=new JButton("R");

    //wordle result letter background colors
    Color
            unset = new Color(0x121213),
            offTargeted = new Color(0x3a3a3c),
            displaced = new Color(0xb59f3b),
            hit = new Color(0x538d4e);

    //set every JLabel in the board with '_'
    private void initBoard()
    {
        for(int i=0;i<6;i++)
            for(int j=0;j<5;j++)
            {
                board[i][j]=new JLabel("_");
            }
    }

    //global key event dispatcher
    KeyEventDispatcher keyEventDispatcher = new KeyEventDispatcher()
    {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e)
        {
            if(e.getID()==KeyEvent.KEY_PRESSED)
                for(char c : acceptableChars)
                    if(e.getKeyChar()==c)
                        System.out.println(e.getKeyChar()+" key code: "+e.getKeyCode());
            return false;
        }
    };

    static boolean startupComplete = false;

    //constructor
    public GUI()
    {
        try{UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}catch(Throwable ex){System.out.println("Error setting native LAF: "+ex);}
        JFrame startup=new JFrame();
        if(!startupComplete)
        {
            //show a startup window
            startup.setSize(200,50);
            startup.setLocationRelativeTo(null);
            startup.setUndecorated(true);
            startup.setAlwaysOnTop(true);
            startup.setResizable(false);
            JLabel startupText = new JLabel("Starting WordleHelper...");
            startupText.setHorizontalAlignment(SwingConstants.CENTER);
            startup.add(startupText);
            startup.setVisible(true);
            /*try {
                Thread.sleep(1000); //magic, don't touch
            }catch (InterruptedException e){}*/
        }

        //set main window
        setTitle("WordleHelper 1.1");
        setSize(640,480);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        JPanel panel=new JPanel(null);
        panel.setBounds(0,0,640,480);
        panel.setBackground(Color.DARK_GRAY);
        add(panel);
        //set result board
        initBoard();
        //set tries status
        tries.setBounds(10,330,100,20);
        tries.setForeground(Color.WHITE);
        //set words left status
        wordsLeft.setBounds(110,330,100,20);
        wordsLeft.setForeground(Color.WHITE);
        //set possible words field
        possibleWords.setBounds(270,10,360,420);
        possibleWords.setEditable(false);
        possibleWords.setLineWrap(true);
        //set color
        possibleWords.setBackground(Color.GRAY);
        possibleWords.setForeground(Color.WHITE);
        //set help and reset button
        help.setBounds(10,400,50,30);
        reset.setBounds(70,400,50,30);
        //set global key event listener
        //help.addKeyListener(boardKeyListener);
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(keyEventDispatcher);
        //set help button's action listener
        help.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                //debug
                System.out.println("help button pressed");
                //show help dialog
                JOptionPane.showMessageDialog(GUI.this,
                        "Each line accepts first 5 letters and then 5 numbers from 0 to 2.\n" +
                        "After the 5th number is typed the possible words will be calculated.\n" +
                        "Redo by pressing the backspace key.\n\n" +
                        "· 0 means the letter is not in the word,\n" +
                        "· 1 means the letter is at the right position,\n" +
                        "· 2 means the letter is in the wrong position.",
                        "Help",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        //set reset button's action listener
        reset.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.out.println("resetting");
                dispose();
                new GUI();
            }
        });
        //add components
        panel.add(tries);
        panel.add(wordsLeft);
        panel.add(possibleWords);
        panel.add(help);
        panel.add(reset);
        for(int i=0;i<6;i++)
            for(int j=0;j<5;j++)
            {
                panel.add(board[i][j]);
                board[i][j].setBounds(j*50+10,i*50+10,50,50);
                board[i][j].setHorizontalAlignment(JLabel.CENTER);
                board[i][j].setBackground(unset);
                board[i][j].setForeground(Color.WHITE);
                board[i][j].setOpaque(true);
            }
        //show magic
        startup.dispose();
        setVisible(true);
        startupComplete=true;
    }


    public static void main(String[] args)
    {
        new GUI().setVisible(true);
    }
}
