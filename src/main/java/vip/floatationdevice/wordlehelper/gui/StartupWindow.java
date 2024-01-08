package vip.floatationdevice.wordlehelper.gui;

import javax.swing.*;

/**
 * A window that displays a message while the WordleHelper GUI is starting up.
 * Part of WordleHelperGUI.
 */
public class StartupWindow extends JFrame
{
    private final static JLabel STARTUP_TEXT = new JLabel("Starting WordleHelper...");

    public StartupWindow()
    {
        setTitle("Starting WordleHelper");
        STARTUP_TEXT.setHorizontalAlignment(SwingConstants.CENTER);
        add(STARTUP_TEXT);
        setSize(200, 50);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
