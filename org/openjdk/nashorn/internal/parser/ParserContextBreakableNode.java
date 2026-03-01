package org.openjdk.nashorn.internal.parser;

import org.openjdk.nashorn.internal.parser.ParserContextNode;

interface ParserContextBreakableNode
extends ParserContextNode {
    public boolean isBreakableWithoutLabel();
}
