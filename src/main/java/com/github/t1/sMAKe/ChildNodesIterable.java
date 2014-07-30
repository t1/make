package com.github.t1.sMAKe;

import java.util.Iterator;

import lombok.AllArgsConstructor;

import org.w3c.dom.*;

@AllArgsConstructor
public class ChildNodesIterable implements Iterable<Node> {
    public static Iterable<Node> childNodes(Node node) {
        return new ChildNodesIterable(node);
    }

    private final Node node;

    @Override
    public Iterator<Node> iterator() {
        return new Iterator<Node>() {
            private final NodeList childNodes = node.getChildNodes();
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