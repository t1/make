package com.github.t1.make.cli;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;
import java.util.function.*;
import java.util.stream.Stream;

import org.joda.convert.StringConvert;

public class Cli {
    private final List<String> args;
    private final StringConvert converter;

    public Cli(List<String> args) {
        this.args = args;
        this.converter = StringConvert.create();
        this.converter.register(Path.class, new PathConverter());
    }

    public Stream<String> commands() {
        return args().filter(arg -> isCommand(arg));
    }

    private boolean isCommand(String arg) {
        return !arg.startsWith("-");
    }

    public Stream<String> args() {
        return args.stream();
    }

    public Runnable on(Runnable runnable) {
        for (Field field : runnable.getClass().getDeclaredFields()) {
            CliArgument cliArgument = field.getAnnotation(CliArgument.class);
            if (cliArgument == null)
                continue;
            args.stream().filter(isOptionArgFor(field)).forEach(setOptionFor(runnable, field));
        }
        return runnable;
    }

    private Predicate<? super String> isOptionArgFor(Field field) {
        return arg -> isOptionArgFor(arg, "--" + field.getName())
                || isOptionArgFor(arg, "-" + field.getName().substring(0, 1));
    }

    private boolean isOptionArgFor(String arg, String string) {
        return arg.equals(string) || arg.startsWith(string + "=");
    }

    private Consumer<String> setOptionFor(Runnable command, Field field) {
        return arg -> {
            try {
                field.setAccessible(true);
                String stringValue = optionString(arg);
                Object objectValue = optionValue(field, stringValue);
                field.set(command, objectValue);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private String optionString(String arg) {
        int equalsIndex = arg.indexOf('=');
        if (equalsIndex < 0)
            return null;
        return arg.substring(equalsIndex + 1);
    }

    private Object optionValue(Field field, String stringValue) {
        if (isBoolean(field) && isEmpty(stringValue)) {
            return true;
        } else {
            return converter.convertFromString(field.getType(), stringValue);
        }
    }

    private boolean isBoolean(Field field) {
        return field.getType() == Boolean.class || field.getType() == boolean.class;
    }

    private boolean isEmpty(String stringValue) {
        return stringValue == null || stringValue.isEmpty();
    }
}
