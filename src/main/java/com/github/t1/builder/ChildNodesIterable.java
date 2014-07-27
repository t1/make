package com.github.t1.builder;

import java.util.Iterator;

import lombok.AllArgsConstructor;

import org.w3c.dom.*;

@AllArgsConstructor
public class ChildNodesIterable implements Iterable<Node> {
    public static Iterable<Node> childNodes(Element element) {
        return new ChildNodesIterable(element);
    }

    private final Element element;

    @Override
    public Iterator<Node> iterator() {
        return new Iterator<Node>() {
            private final NodeList childNodes = element.getChildNodes();
            private int index;

            @Override
            public boolean hasNext() {
                return index < childNodes.getLength();
            }

            @Override
            public Node next() {
                return childNodes.item(index++);
            }
        };
    }
}