package lombok.javac.java6;

import com.sun.tools.javac.parser.EndPosParser;
import com.sun.tools.javac.parser.Lexer;
import com.sun.tools.javac.parser.Parser;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import lombok.javac.CommentCatcher;
import lombok.javac.CommentInfo;
import lombok.javac.java6.CommentCollectingScanner;

class CommentCollectingParser
extends EndPosParser {
    private final Lexer lexer;

    protected CommentCollectingParser(Parser.Factory fac, Lexer S, boolean keepDocComments) {
        super(fac, S, keepDocComments);
        this.lexer = S;
    }

    public JCTree.JCCompilationUnit compilationUnit() {
        JCTree.JCCompilationUnit result = super.compilationUnit();
        if (this.lexer instanceof CommentCollectingScanner) {
            List<CommentInfo> comments = ((CommentCollectingScanner)this.lexer).getComments();
            CommentCatcher.JCCompilationUnit_comments.set(result, comments);
        }
        return result;
    }
}
