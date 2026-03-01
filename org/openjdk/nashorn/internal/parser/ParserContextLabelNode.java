package org.openjdk.nashorn.internal.parser;

import org.openjdk.nashorn.internal.parser.ParserContextBaseNode;

class ParserContextLabelNode
extends ParserContextBaseNode {
    private final String name;

    public ParserContextLabelNode(String name) {
        this.name = name;
    }

    public String getLabelName() {
        return this.name;
    }
}
