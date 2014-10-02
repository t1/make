package com.github.t1.somemake.cli;

import static java.util.Arrays.*;
import static java.util.stream.Collectors.*;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;
import java.util.function.*;

import org.joda.convert.StringConvert;

import com.github.t1.somemake.model.PathConverter;

public class Main implements Runnable {
    private final String[] DEFAULT_ARGS = { "help" };

    public static void main(String... args) {
        new Main(args).run();
    }

    private final List<String> args;
    private final StringConvert converter;

    public Main(String... args) {
        if (args.length == 0)
            args = DEFAULT_ARGS;
        this.args = asList(args);
        this.converter = StringConvert.create();
        this.converter.register(Path.class, new PathConverter());
    }

    @Override
    public void run() {
        try {
            args.stream().filter(arg -> isCommand(arg)).forEach(arg -> command(arg).run());
        } catch (RuntimeException e) {
            System.err.println("failed to run [" + args.stream().collect(joining(" ")) + "]: " + e.getMessage());
            // e.printStackTrace();
        }
    }

    private boolean isCommand(String arg) {
        return !arg.startsWith("-");
    }

    private Runnable command(String commandName) {
        Runnable command = build(commandName);
        for (Field field : command.getClass().getDeclaredFields()) {
            CliArgument cliArgument = field.getAnnotation(CliArgument.class);
            if (cliArgument == null)
                continue;
            args.stream().filter(isOptionArgFor(field)).forEach(setOptionFor(command, field));
        }
        return command;
    }

    private Runnable build(String commandName) {
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

    private Predicate<? super String> isOptionArgFor(Field field) {
        return arg -> //
        (arg.startsWith("--" + field.getName() + "=") //
        || arg.startsWith("-" + field.getName().substring(0, 1) + "="));
    }

    private Consumer<String> setOptionFor(Runnable command, Field field) {
        return arg -> {
            try {
                field.setAccessible(true);
                String stringValue = optionValue(arg);
                Object objectValue = converter.convertFromString(field.getType(), stringValue);
                field.set(command, objectValue);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private String optionValue(String arg) {
        int equalsIndex = arg.indexOf('=');
        return arg.substring(equalsIndex + 1);
    }
}
