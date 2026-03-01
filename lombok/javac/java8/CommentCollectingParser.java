package lombok.javac.java8;

import com.sun.tools.javac.parser.JavacParser;
import com.sun.tools.javac.parser.Lexer;
import com.sun.tools.javac.parser.ParserFactory;
import com.sun.tools.javac.tree.JCTree;
import lombok.javac.CommentCatcher;
import lombok.javac.java8.CommentCollectingScanner;

class CommentCollectingParser
extends JavacParser {
    private final Lexer lexer;

    protected CommentCollectingParser(ParserFactory fac, Lexer S, boolean keepDocComments, boolean keepLineMap, boolean keepEndPositions) {
        super(fac, S, keepDocComments, keepLineMap, keepEndPositions);
        this.lexer = S;
    }

    @Override
    public JCTree.JCCompilationUnit parseCompilationUnit() {
        JCTree.JCCompilationUnit result = super.parseCompilationUnit();
        if (this.lexer instanceof CommentCollectingScanner) {
            CommentCatcher.JCCompilationUnit_comments.set(result, ((CommentCollectingScanner)this.lexer).getComments());
            CommentCatcher.JCCompilationUnit_textBlockStarts.set(result, ((CommentCollectingScanner)this.lexer).getTextBlockStarts());
        }
        return result;
    }
}
