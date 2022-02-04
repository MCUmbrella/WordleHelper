package vip.floatationdevice.wordlehelper.gui;

import javax.swing.*;
import java.awt.*;

import static vip.floatationdevice.wordlehelper.Common.*;

public class LetterBlock extends JLabel
{
    int number;
    public boolean numberSet = false;

    public LetterBlock()
    {
        setText("_");
        setNumber(-1);
        setHorizontalAlignment(JLabel.CENTER);
        setBackground(COLOR_UNSET);
        setForeground(Color.WHITE);
        setOpaque(true);
    }

    public void setNumber(int number)
    {
        this.number = number;
        switch (number)
        {
            case -1:
            case 0:
            {
                setBackground(COLOR_OFF_TARGETED);
                break;
            }
            case 1:
            {
                setBackground(COLOR_HIT);
                break;
            }
            case 2:
            {
                setBackground(COLOR_DISPLACED);
                break;
            }
            default:
            {
                throw new IllegalArgumentException("Invalid number: " + number);
            }
        }
    }
    public int getNumber() {return number;}
}
