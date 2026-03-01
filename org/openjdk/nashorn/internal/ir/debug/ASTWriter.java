package org.openjdk.nashorn.internal.ir.debug;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.openjdk.nashorn.internal.ir.BinaryNode;
import org.openjdk.nashorn.internal.ir.Block;
import org.openjdk.nashorn.internal.ir.Expression;
import org.openjdk.nashorn.internal.ir.IdentNode;
import org.openjdk.nashorn.internal.ir.Node;
import org.openjdk.nashorn.internal.ir.Statement;
import org.openjdk.nashorn.internal.ir.Symbol;
import org.openjdk.nashorn.internal.ir.Terminal;
import org.openjdk.nashorn.internal.ir.TernaryNode;
import org.openjdk.nashorn.internal.ir.annotations.Ignore;
import org.openjdk.nashorn.internal.ir.annotations.Reference;
import org.openjdk.nashorn.internal.parser.Token;
import org.openjdk.nashorn.internal.runtime.Context;
import org.openjdk.nashorn.internal.runtime.Debug;

public final class ASTWriter {
    private static final ClassValue<Field[]> accessibleFields = new ClassValue<Field[]>(){

        @Override
        protected Field[] computeValue(Class<?> type) {
            Field[] fields;
            for (Field f : fields = type.getDeclaredFields()) {
                f.setAccessible(true);
            }
            return fields;
        }
    };
    private final Node root;
    private static final int TABWIDTH = 4;

    public ASTWriter(Node root) {
        this.root = root;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        this.printAST(sb, null, null, "root", this.root, 0);
        return sb.toString();
    }

    public Node[] toArray() {
        ArrayList<Node> preorder = new ArrayList<Node>();
        this.printAST(new StringBuilder(), preorder, null, "root", this.root, 0);
        return preorder.toArray(new Node[0]);
    }

    private void printAST(StringBuilder sb, List<Node> preorder, Field field, String name, Node node, int indent) {
        Symbol symbol;
        ASTWriter.indent(sb, indent);
        if (node == null) {
            sb.append("[Object ");
            sb.append(name);
            sb.append(" null]\n");
            return;
        }
        if (preorder != null) {
            preorder.add(node);
        }
        boolean isReference = field != null && field.isAnnotationPresent(Reference.class);
        Class<?> clazz = node.getClass();
        Object type = clazz.getName();
        int truncate = ((String)(type = ((String)type).substring(((String)type).lastIndexOf(46) + 1, ((String)type).length()))).indexOf("Node");
        if (truncate == -1) {
            truncate = ((String)type).indexOf("Statement");
        }
        if (truncate != -1) {
            type = ((String)type).substring(0, truncate);
        }
        type = ((String)type).toLowerCase();
        if (isReference) {
            type = "ref: " + (String)type;
        }
        if ((symbol = node instanceof IdentNode ? ((IdentNode)node).getSymbol() : null) != null) {
            type = (String)type + ">" + symbol;
        }
        if (node instanceof Block && ((Block)node).needsScope()) {
            type = (String)type + " <scope>";
        }
        LinkedList<Field> children = new LinkedList<Field>();
        if (!isReference) {
            ASTWriter.enqueueChildren(node, clazz, children);
        }
        Object status = "";
        if (node instanceof Terminal && ((Terminal)((Object)node)).isTerminal()) {
            status = (String)status + " Terminal";
        }
        if (node instanceof Statement && ((Statement)node).hasGoto()) {
            status = (String)status + " Goto ";
        }
        if (symbol != null) {
            status = (String)status + symbol;
        }
        if (!"".equals(status = ((String)status).trim())) {
            status = " [" + (String)status + "]";
        }
        if (symbol != null) {
            String tname = ((Expression)node).getType().toString();
            if (tname.indexOf(46) != -1) {
                tname = tname.substring(tname.lastIndexOf(46) + 1, tname.length());
            }
            status = (String)status + " (" + tname + ")";
        }
        status = (String)status + " @" + Debug.id(node);
        if (children.isEmpty()) {
            sb.append("[").append((String)type).append(' ').append(name).append(" = '").append(node).append("'").append((String)status).append("] ").append('\n');
        } else {
            sb.append("[").append((String)type).append(' ').append(name).append(' ').append(Token.toString(node.getToken())).append((String)status).append("]").append('\n');
            for (Field child : children) {
                Object value;
                if (child.isAnnotationPresent(Ignore.class)) continue;
                try {
                    value = child.get(node);
                }
                catch (IllegalAccessException | IllegalArgumentException e) {
                    Context.printStackTrace(e);
                    return;
                }
                if (value instanceof Node) {
                    this.printAST(sb, preorder, child, child.getName(), (Node)value, indent + 1);
                    continue;
                }
                if (!(value instanceof Collection)) continue;
                int pos = 0;
                ASTWriter.indent(sb, indent + 1);
                sb.append('[').append(child.getName()).append("[0..").append(((Collection)value).size()).append("]]").append('\n');
                for (Node member : (Collection)value) {
                    this.printAST(sb, preorder, child, child.getName() + "[" + pos++ + "]", member, indent + 2);
                }
            }
        }
    }

    private static void enqueueChildren(Node node, Class<?> nodeClass, List<Field> children) {
        Iterator iter;
        ArrayDeque<Class> stack = new ArrayDeque<Class>();
        Class<?> clazz = nodeClass;
        do {
            stack.push(clazz);
        } while ((clazz = clazz.getSuperclass()) != null);
        if (node instanceof TernaryNode) {
            stack.push((Class)stack.removeLast());
        }
        Iterator iterator = iter = node instanceof BinaryNode ? stack.descendingIterator() : stack.iterator();
        while (iter.hasNext()) {
            Class c = (Class)iter.next();
            for (Field f : accessibleFields.get(c)) {
                try {
                    Object child = f.get(node);
                    if (child == null) continue;
                    if (child instanceof Node) {
                        children.add(f);
                        continue;
                    }
                    if (!(child instanceof Collection) || ((Collection)child).isEmpty()) continue;
                    children.add(f);
                }
                catch (IllegalAccessException | IllegalArgumentException e) {
                    return;
                }
            }
        }
    }

    private static void indent(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; ++i) {
            for (int j = 0; j < 4; ++j) {
                sb.append(' ');
            }
        }
    }
}
