package com.github.t1.somemake;

import java.io.*;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class XmlWriter {
    private final Writer target;
    private int indent = 0;

    protected void nl() {
        append("\n");
    }

    protected void comment(String text) {
        if (text == null)
            return;
        indent();
        append("<!-- " + text + " -->\n");
    }

    protected void tag(String tagName, String body) {
        if (body == null)
            return;
        indent();
        append("<" + tagName + ">");
        append(body);
        append("</" + tagName + ">\n");
    }

    protected void tag(String tagName, Optional<String> body) {
        if (!body.isPresent())
            return;
        indent();
        append("<" + tagName + ">");
        append(body.get());
        append("</" + tagName + ">\n");
    }

    protected void tag(String tagName, Runnable body) {
        tag(tagName, "", body);
    }

    protected void tag(String tagName, String attributes, Runnable body) {
        indent();
        append("<" + tagName);
        if (!attributes.isEmpty())
            append(" " + attributes);
        append(">");

        ++indent;
        nl();

        body.run();

        --indent;
        indent();
        append("</" + tagName + ">\n");
    }

    private void indent() {
        for (int i = 0; i < indent; i++) {
            append("    ");
        }
    }

    private void append(String string) {
        try {
            target.append(string);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
