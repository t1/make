package com.github.t1.make.cli;

import static java.util.Arrays.*;
import static java.util.stream.Collectors.*;

public class Main implements Runnable {
    private final String[] DEFAULT_ARGS = { "help" };

    public static void main(String... args) {
        new Main(args).run();
    }

    private final Cli cli;

    @CliArgument
    private boolean verbose;

    public Main(String... args) {
        if (args.length == 0)
            args = DEFAULT_ARGS;
        this.cli = new Cli(asList(args));
        this.cli.on(this);
    }

    @Override
    public void run() {
        try {
            cli.commands().forEach(arg -> command(arg).run());
        } catch (RuntimeException e) {
            System.err.println("failed to run [" + cli.args().collect(joining(" ")) + "]: " + e.getMessage());
            if (verbose) {
                e.printStackTrace();
            }
        }
    }

    private Runnable command(String commandName) {
        Runnable command = buildCommand(commandName);
        return cli.on(command);
    }

    private Runnable buildCommand(String commandName) {
        String className = Main.class.getPackage().getName() + "." + initCap(commandName) + "Command";
        try {
            return (Runnable) Class.forName(className).newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private String initCap(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }
}
