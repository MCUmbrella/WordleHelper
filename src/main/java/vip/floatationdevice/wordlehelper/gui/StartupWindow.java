package vip.floatationdevice.wordlehelper.gui;

import javax.swing.*;

public class StartupWindow extends JFrame
{
    final static JLabel startupText = new JLabel("Starting WordleHelper...");
    public StartupWindow()
    {
        setTitle("Starting WordleHelper");
        startupText.setHorizontalAlignment(SwingConstants.CENTER);
        add(startupText);
        setSize(200,50);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
