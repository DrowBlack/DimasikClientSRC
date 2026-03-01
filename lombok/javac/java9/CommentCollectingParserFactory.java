package lombok.javac.java9;

import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.parser.JavacParser;
import com.sun.tools.javac.parser.ParserFactory;
import com.sun.tools.javac.parser.Scanner;
import com.sun.tools.javac.parser.ScannerFactory;
import com.sun.tools.javac.util.Context;
import java.lang.reflect.Field;
import lombok.javac.java9.CommentCollectingParser;
import lombok.permit.Permit;

public class CommentCollectingParserFactory
extends ParserFactory {
    private final Context context;

    static Context.Key<ParserFactory> key() {
        return parserFactoryKey;
    }

    protected CommentCollectingParserFactory(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public JavacParser newParser(CharSequence input, boolean keepDocComments, boolean keepEndPos, boolean keepLineMap) {
        return this.newParser(input, keepDocComments, keepEndPos, keepLineMap, false);
    }

    @Override
    public JavacParser newParser(CharSequence input, boolean keepDocComments, boolean keepEndPos, boolean keepLineMap, boolean parseModuleInfo) {
        ScannerFactory scannerFactory = ScannerFactory.instance(this.context);
        Scanner lexer = scannerFactory.newScanner(input, true);
        CommentCollectingParser x = new CommentCollectingParser(this, lexer, true, keepLineMap, keepEndPos, parseModuleInfo);
        return x;
    }

    public static void setInCompiler(JavaCompiler compiler, Context context) {
        context.put(CommentCollectingParserFactory.key(), null);
        try {
            Field field = Permit.getField(JavaCompiler.class, "parserFactory");
            field.set(compiler, new CommentCollectingParserFactory(context));
        }
        catch (Exception e) {
            throw new IllegalStateException("Could not set comment sensitive parser in the compiler", e);
        }
    }
}
