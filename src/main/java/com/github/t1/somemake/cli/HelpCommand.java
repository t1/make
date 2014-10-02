package com.github.t1.somemake.cli;

public class HelpCommand implements Runnable {
    static final String HELP = "help for somemake";

    @Override
    public void run() {
        System.out.println(HELP);
    }
}
