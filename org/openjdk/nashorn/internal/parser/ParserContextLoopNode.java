package org.openjdk.nashorn.internal.parser;

import org.openjdk.nashorn.internal.parser.ParserContextBaseNode;
import org.openjdk.nashorn.internal.parser.ParserContextBreakableNode;

class ParserContextLoopNode
extends ParserContextBaseNode
implements ParserContextBreakableNode {
    ParserContextLoopNode() {
    }

    @Override
    public boolean isBreakableWithoutLabel() {
        return true;
    }
}
