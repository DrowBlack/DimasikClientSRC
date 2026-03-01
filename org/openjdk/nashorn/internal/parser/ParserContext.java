package org.openjdk.nashorn.internal.parser;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.openjdk.nashorn.internal.ir.Statement;
import org.openjdk.nashorn.internal.parser.ParserContextBlockNode;
import org.openjdk.nashorn.internal.parser.ParserContextBreakableNode;
import org.openjdk.nashorn.internal.parser.ParserContextFunctionNode;
import org.openjdk.nashorn.internal.parser.ParserContextLabelNode;
import org.openjdk.nashorn.internal.parser.ParserContextLoopNode;
import org.openjdk.nashorn.internal.parser.ParserContextModuleNode;
import org.openjdk.nashorn.internal.parser.ParserContextNode;

class ParserContext {
    private ParserContextNode[] stack = new ParserContextNode[16];
    private int sp = 0;
    private static final int INITIAL_DEPTH = 16;

    public <T extends ParserContextNode> T push(T node) {
        assert (!this.contains(node));
        if (this.sp == this.stack.length) {
            ParserContextNode[] newStack = new ParserContextNode[this.sp * 2];
            System.arraycopy(this.stack, 0, newStack, 0, this.sp);
            this.stack = newStack;
        }
        this.stack[this.sp] = node;
        ++this.sp;
        return node;
    }

    public ParserContextNode peek() {
        return this.stack[this.sp - 1];
    }

    public <T extends ParserContextNode> T pop(T node) {
        --this.sp;
        ParserContextNode popped = this.stack[this.sp];
        this.stack[this.sp] = null;
        assert (node == popped);
        return (T)popped;
    }

    public boolean contains(ParserContextNode node) {
        for (int i = 0; i < this.sp; ++i) {
            if (this.stack[i] != node) continue;
            return true;
        }
        return false;
    }

    private ParserContextBreakableNode getBreakable() {
        NodeIterator<ParserContextBreakableNode> iter = new NodeIterator<ParserContextBreakableNode>(ParserContextBreakableNode.class, this.getCurrentFunction());
        while (iter.hasNext()) {
            ParserContextBreakableNode next = (ParserContextBreakableNode)iter.next();
            if (!next.isBreakableWithoutLabel()) continue;
            return next;
        }
        return null;
    }

    public ParserContextBreakableNode getBreakable(String labelName) {
        if (labelName != null) {
            ParserContextLabelNode foundLabel = this.findLabel(labelName);
            if (foundLabel != null) {
                ParserContextBreakableNode breakable = null;
                NodeIterator<ParserContextBreakableNode> iter = new NodeIterator<ParserContextBreakableNode>(ParserContextBreakableNode.class, foundLabel);
                while (iter.hasNext()) {
                    breakable = (ParserContextBreakableNode)iter.next();
                }
                return breakable;
            }
            return null;
        }
        return this.getBreakable();
    }

    public ParserContextLoopNode getCurrentLoop() {
        NodeIterator<ParserContextLoopNode> iter = new NodeIterator<ParserContextLoopNode>(ParserContextLoopNode.class, this.getCurrentFunction());
        return iter.hasNext() ? (ParserContextLoopNode)iter.next() : null;
    }

    private ParserContextLoopNode getContinueTo() {
        return this.getCurrentLoop();
    }

    public ParserContextLoopNode getContinueTo(String labelName) {
        if (labelName != null) {
            ParserContextLabelNode foundLabel = this.findLabel(labelName);
            if (foundLabel != null) {
                ParserContextLoopNode loop = null;
                NodeIterator<ParserContextLoopNode> iter = new NodeIterator<ParserContextLoopNode>(ParserContextLoopNode.class, foundLabel);
                while (iter.hasNext()) {
                    loop = (ParserContextLoopNode)iter.next();
                }
                return loop;
            }
            return null;
        }
        return this.getContinueTo();
    }

    public ParserContextBlockNode getFunctionBody(ParserContextFunctionNode functionNode) {
        for (int i = this.sp - 1; i >= 0; --i) {
            if (this.stack[i] != functionNode) continue;
            return (ParserContextBlockNode)this.stack[i + 1];
        }
        throw new AssertionError((Object)(functionNode.getName() + " not on context stack"));
    }

    public ParserContextLabelNode findLabel(String name) {
        NodeIterator<ParserContextLabelNode> iter = new NodeIterator<ParserContextLabelNode>(ParserContextLabelNode.class, this.getCurrentFunction());
        while (iter.hasNext()) {
            ParserContextLabelNode next = (ParserContextLabelNode)iter.next();
            if (!next.getLabelName().equals(name)) continue;
            return next;
        }
        return null;
    }

    public void prependStatementToCurrentNode(Statement statement) {
        assert (statement != null);
        this.stack[this.sp - 1].prependStatement(statement);
    }

    public void appendStatementToCurrentNode(Statement statement) {
        assert (statement != null);
        this.stack[this.sp - 1].appendStatement(statement);
    }

    public ParserContextFunctionNode getCurrentFunction() {
        for (int i = this.sp - 1; i >= 0; --i) {
            if (!(this.stack[i] instanceof ParserContextFunctionNode)) continue;
            return (ParserContextFunctionNode)this.stack[i];
        }
        return null;
    }

    public Iterator<ParserContextBlockNode> getBlocks() {
        return new NodeIterator<ParserContextBlockNode>(ParserContextBlockNode.class);
    }

    public ParserContextBlockNode getCurrentBlock() {
        return this.getBlocks().next();
    }

    public Statement getLastStatement() {
        if (this.sp == 0) {
            return null;
        }
        ParserContextNode top = this.stack[this.sp - 1];
        int s = top.getStatements().size();
        return s == 0 ? null : top.getStatements().get(s - 1);
    }

    public Iterator<ParserContextFunctionNode> getFunctions() {
        return new NodeIterator<ParserContextFunctionNode>(ParserContextFunctionNode.class);
    }

    public ParserContextModuleNode getCurrentModule() {
        NodeIterator<ParserContextModuleNode> iter = new NodeIterator<ParserContextModuleNode>(ParserContextModuleNode.class, this.getCurrentFunction());
        return iter.hasNext() ? (ParserContextModuleNode)iter.next() : null;
    }

    private class NodeIterator<T extends ParserContextNode>
    implements Iterator<T> {
        private int index;
        private T next;
        private final Class<T> clazz;
        private final ParserContextNode until;

        NodeIterator(Class<T> clazz) {
            this(clazz, null);
        }

        NodeIterator(Class<T> clazz, ParserContextNode until) {
            this.index = ParserContext.this.sp - 1;
            this.clazz = clazz;
            this.until = until;
            this.next = this.findNext();
        }

        @Override
        public boolean hasNext() {
            return this.next != null;
        }

        @Override
        public T next() {
            if (this.next == null) {
                throw new NoSuchElementException();
            }
            T lnext = this.next;
            this.next = this.findNext();
            return lnext;
        }

        private T findNext() {
            for (int i = this.index; i >= 0; --i) {
                ParserContextNode node = ParserContext.this.stack[i];
                if (node == this.until) {
                    return null;
                }
                if (!this.clazz.isAssignableFrom(node.getClass())) continue;
                this.index = i - 1;
                return (T)node;
            }
            return null;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
