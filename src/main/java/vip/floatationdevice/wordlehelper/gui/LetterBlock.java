package vip.floatationdevice.wordlehelper.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Represents a letter in the Wordle game.
 * Part of WordleHelperGUI.
 */
public class LetterBlock extends JLabel
{
    /** used to represent the different check results of a letter */
    public final static Color
            COLOR_UNSET = new Color(0x121213),
            COLOR_MISS = new Color(0x3a3a3c),
            COLOR_MISPLACED = new Color(0xb59f3b),
            COLOR_HIT = new Color(0x538d4e);

    /**
     * the check result associated with the letter.
     * -1: unset, 0: miss, 1: hit, 2: misplaced
     */
    int number;

    public LetterBlock()
    {
        setText("_");
        setSize(50, 50);
        setFont(new Font(getFont().getName(), Font.BOLD, getFont().getSize() + 10));
        setNumber(-1);
        setHorizontalAlignment(JLabel.CENTER);
        setBackground(COLOR_UNSET);
        setForeground(Color.WHITE);
        setOpaque(true);
    }

    /**
     * Get the check status of the letter.
     * @return -1, 0, 1 or 2.
     */
    public int getNumber()
    {
        return number;
    }

    /**
     * Set the check result of the letter.
     * @param number -1, 0, 1 or 2.
     * @throws IllegalArgumentException when setting something else.
     */
    public void setNumber(int number)
    {
        this.number = number;
        switch(number)
        {
            case -1:
            {
                setBackground(COLOR_UNSET);
                break;
            }
            case 0:
            {
                setBackground(COLOR_MISS);
                break;
            }
            case 1:
            {
                setBackground(COLOR_HIT);
                break;
            }
            case 2:
            {
                setBackground(COLOR_MISPLACED);
                break;
            }
            default:
            {
                throw new IllegalArgumentException("Invalid number: " + number);
            }
        }
    }

    @Override
    public void setText(String text)
    {
        super.setText(text.toUpperCase());
    }
}
