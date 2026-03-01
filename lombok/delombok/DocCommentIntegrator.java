package lombok.delombok;

import com.sun.tools.javac.parser.Tokens;
import com.sun.tools.javac.tree.DocCommentTable;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeScanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.regex.Pattern;
import lombok.javac.CommentInfo;
import lombok.javac.Javac;
import lombok.javac.handlers.JavacHandlerUtil;

public class DocCommentIntegrator {
    private static final Pattern CONTENT_STRIPPER = Pattern.compile("^(?:\\s*\\*)?(.*?)$", 8);

    public List<CommentInfo> integrate(List<CommentInfo> comments, JCTree.JCCompilationUnit unit) {
        ArrayList<CommentInfo> out = new ArrayList<CommentInfo>();
        CommentInfo lastExcisedComment = null;
        JCTree lastNode = null;
        NavigableMap<Integer, JCTree> positionMap = this.buildNodePositionMap(unit);
        for (CommentInfo cmt : comments) {
            if (!cmt.isJavadoc()) {
                out.add(cmt);
                continue;
            }
            Map.Entry<Integer, JCTree> entry = positionMap.ceilingEntry(cmt.endPos);
            if (entry == null) {
                out.add(cmt);
                continue;
            }
            JCTree node = entry.getValue();
            if (node == lastNode) {
                out.add(lastExcisedComment);
            }
            if (!this.attach(unit, node, cmt)) {
                out.add(cmt);
                continue;
            }
            lastNode = node;
            lastExcisedComment = cmt;
        }
        return out;
    }

    private NavigableMap<Integer, JCTree> buildNodePositionMap(JCTree.JCCompilationUnit unit) {
        final TreeMap<Integer, JCTree> positionMap = new TreeMap<Integer, JCTree>();
        unit.accept(new TreeScanner(){

            @Override
            public void visitClassDef(JCTree.JCClassDecl tree) {
                positionMap.put(tree.pos, tree);
                super.visitClassDef(tree);
            }

            @Override
            public void visitMethodDef(JCTree.JCMethodDecl tree) {
                positionMap.put(tree.pos, tree);
                super.visitMethodDef(tree);
            }

            @Override
            public void visitVarDef(JCTree.JCVariableDecl tree) {
                positionMap.put(tree.pos, tree);
                super.visitVarDef(tree);
            }
        });
        return positionMap;
    }

    private boolean attach(JCTree.JCCompilationUnit top, JCTree node, CommentInfo cmt) {
        Object map_;
        String docCommentContent = cmt.content;
        if (docCommentContent.startsWith("/**")) {
            docCommentContent = docCommentContent.substring(3);
        }
        if (docCommentContent.endsWith("*/")) {
            docCommentContent = docCommentContent.substring(0, docCommentContent.length() - 2);
        }
        docCommentContent = CONTENT_STRIPPER.matcher(docCommentContent).replaceAll("$1");
        docCommentContent = docCommentContent.trim();
        if (Javac.getDocComments(top) == null) {
            Javac.initDocComments(top);
        }
        if ((map_ = Javac.getDocComments(top)) instanceof Map) {
            ((Map)map_).put(node, docCommentContent);
            return true;
        }
        if (Javac.instanceOfDocCommentTable(map_)) {
            CommentAttacher_8.attach(node, docCommentContent, cmt.pos, map_);
            return true;
        }
        return false;
    }

    private static class CommentAttacher_8 {
        private CommentAttacher_8() {
        }

        static void attach(final JCTree node, String docCommentContent, final int pos, Object map_) {
            final String docCommentContent_ = docCommentContent;
            ((DocCommentTable)map_).putComment(node, new Tokens.Comment(){

                @Override
                public String getText() {
                    return docCommentContent_;
                }

                @Override
                public int getSourcePos(int index) {
                    return pos + index;
                }

                @Override
                public Tokens.Comment.CommentStyle getStyle() {
                    return Tokens.Comment.CommentStyle.JAVADOC;
                }

                @Override
                public boolean isDeprecated() {
                    return JavacHandlerUtil.nodeHasDeprecatedFlag(node);
                }
            });
        }
    }
}
