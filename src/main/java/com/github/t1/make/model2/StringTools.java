package com.github.t1.make.model2;

import java.util.List;
import java.util.stream.Collectors;

public class StringTools {
    public static String toLines(List<?> list) {
        return list.stream().map(Object::toString).collect(Collectors.joining("\n    ", "\n    ", "\n"));
    }
}
