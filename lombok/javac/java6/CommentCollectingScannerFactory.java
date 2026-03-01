package lombok.javac.java6;

import com.sun.tools.javac.parser.Scanner;
import com.sun.tools.javac.util.Context;
import java.nio.CharBuffer;
import lombok.javac.java6.CommentCollectingScanner;

public class CommentCollectingScannerFactory
extends Scanner.Factory {
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

    public Scanner newScanner(CharSequence input) {
        if (input instanceof CharBuffer) {
            return new CommentCollectingScanner(this, (CharBuffer)input);
        }
        char[] array = input.toString().toCharArray();
        return this.newScanner(array, array.length);
    }

    public Scanner newScanner(char[] input, int inputLength) {
        return new CommentCollectingScanner(this, input, inputLength);
    }
}
