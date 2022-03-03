package vip.floatationdevice.wordlehelper;

import static vip.floatationdevice.wordlehelper.Common.PROGRAM_NAME;

public class Launcher
{
    static long startTime = 0;
    public static void main(String[] args)
    {
        startTime = System.currentTimeMillis();
        System.out.println(PROGRAM_NAME);
        if(args.length > 0 && args[0].equals("-c"))
            CLI.main(args);
        else GUI.main(args);
    }
}
