package com.github.t1.make.cli;

public class HelpCommand implements Runnable {
    static final String HELP = "help for make";

    @Override
    public void run() {
        System.out.println(HELP);
    }
}
