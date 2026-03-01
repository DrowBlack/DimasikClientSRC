package lombok.javac.java8;

import com.sun.tools.javac.parser.Scanner;
import com.sun.tools.javac.parser.ScannerFactory;
import com.sun.tools.javac.util.Context;
import java.nio.Buffer;
import java.nio.CharBuffer;
import lombok.javac.java8.CommentCollectingScanner;
import lombok.javac.java8.CommentCollectingTokenizer;

public class CommentCollectingScannerFactory
extends ScannerFactory {
    public static boolean findTextBlocks;

    public static void preRegister(Context context) {
        if (context.get(scannerFactoryKey) == null) {
            class MyFactory
            implements Context.Factory {
                private final /* synthetic */ Context val$context;

                MyFactory(Context context) {
                    this.val$context = context;
                }

                public Object make() {
                    return new CommentCollectingScannerFactory(this.val$context);
                }

                public Object make(Context c) {
                    return new CommentCollectingScannerFactory(c);
                }
            }
            MyFactory factory = new MyFactory(context);
            context.put(scannerFactoryKey, factory);
        }
    }

    protected CommentCollectingScannerFactory(Context context) {
        super(context);
    }

    @Override
    public Scanner newScanner(CharSequence input, boolean keepDocComments) {
        int limit;
        char[] array;
        if (input instanceof CharBuffer && ((CharBuffer)input).hasArray()) {
            CharBuffer cb = (CharBuffer)input;
            ((Buffer)cb.compact()).flip();
            array = cb.array();
            limit = cb.limit();
        } else {
            array = input.toString().toCharArray();
            limit = array.length;
        }
        if (array.length == limit) {
            char[] d = new char[limit + 1];
            System.arraycopy(array, 0, d, 0, limit);
            array = d;
        }
        return this.newScanner(array, limit, keepDocComments);
    }

    @Override
    public Scanner newScanner(char[] input, int inputLength, boolean keepDocComments) {
        return new CommentCollectingScanner((ScannerFactory)this, CommentCollectingTokenizer.create(this, input, inputLength, findTextBlocks));
    }
}
