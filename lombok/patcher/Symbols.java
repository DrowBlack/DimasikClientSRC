package lombok.patcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Symbols {
    private static final ThreadLocal<LinkedList<String>> stack = new ThreadLocal<LinkedList<String>>(){

        @Override
        protected LinkedList<String> initialValue() {
            return new LinkedList<String>();
        }
    };

    private Symbols() {
    }

    public static void push(String symbol) {
        stack.get().addFirst(symbol);
    }

    public static void pop() {
        stack.get().poll();
    }

    public static boolean isEmpty() {
        return stack.get().isEmpty();
    }

    public static int size() {
        return stack.get().size();
    }

    public static boolean hasSymbol(String symbol) {
        if (symbol == null) {
            throw new NullPointerException("symbol");
        }
        return stack.get().contains(symbol);
    }

    public static boolean hasTail(String symbol) {
        if (symbol == null) {
            throw new NullPointerException("symbol");
        }
        return symbol.equals(stack.get().peek());
    }

    public static List<String> getCopy() {
        return new ArrayList<String>((Collection)stack.get());
    }
}
