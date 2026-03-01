package lombok.javac;

import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import lombok.core.FieldAugment;
import lombok.javac.CommentInfo;
import lombok.javac.Javac;
import lombok.permit.Permit;

public class CommentCatcher {
    private final JavaCompiler compiler;
    public static final FieldAugment<JCTree.JCCompilationUnit, List<CommentInfo>> JCCompilationUnit_comments = FieldAugment.augment(JCTree.JCCompilationUnit.class, List.class, "lombok$comments");
    public static final FieldAugment<JCTree.JCCompilationUnit, List<Integer>> JCCompilationUnit_textBlockStarts = FieldAugment.augment(JCTree.JCCompilationUnit.class, List.class, "lombok$textBlockStarts");

    public static CommentCatcher create(Context context, boolean findTextBlocks) {
        CommentCatcher.registerCommentsCollectingScannerFactory(context, findTextBlocks);
        JavaCompiler compiler = new JavaCompiler(context);
        CommentCatcher.setInCompiler(compiler, context);
        compiler.keepComments = true;
        compiler.genEndPos = true;
        return new CommentCatcher(compiler);
    }

    private CommentCatcher(JavaCompiler compiler) {
        this.compiler = compiler;
    }

    public JavaCompiler getCompiler() {
        return this.compiler;
    }

    public void setComments(JCTree.JCCompilationUnit ast, List<CommentInfo> comments) {
        if (comments != null) {
            JCCompilationUnit_comments.set(ast, comments);
        } else {
            JCCompilationUnit_comments.clear(ast);
        }
    }

    public List<CommentInfo> getComments(JCTree.JCCompilationUnit ast) {
        List<CommentInfo> list = JCCompilationUnit_comments.get(ast);
        return list == null ? Collections.emptyList() : list;
    }

    public List<Integer> getTextBlockStarts(JCTree.JCCompilationUnit ast) {
        List<Integer> list = JCCompilationUnit_textBlockStarts.get(ast);
        return list == null ? Collections.emptyList() : list;
    }

    private static void registerCommentsCollectingScannerFactory(Context context, boolean findTextBlocks) {
        try {
            Class<?> scannerFactory;
            int javaCompilerVersion = Javac.getJavaCompilerVersion();
            if (javaCompilerVersion <= 6) {
                scannerFactory = Class.forName("lombok.javac.java6.CommentCollectingScannerFactory");
            } else if (javaCompilerVersion == 7) {
                scannerFactory = Class.forName("lombok.javac.java7.CommentCollectingScannerFactory");
            } else {
                scannerFactory = Class.forName("lombok.javac.java8.CommentCollectingScannerFactory");
                if (findTextBlocks) {
                    Permit.getField(scannerFactory, "findTextBlocks").set(null, true);
                }
            }
            Permit.getMethod(scannerFactory, "preRegister", Context.class).invoke(null, context);
        }
        catch (InvocationTargetException e) {
            throw Javac.sneakyThrow(e.getCause());
        }
        catch (Exception e) {
            throw Javac.sneakyThrow(e);
        }
    }

    private static void setInCompiler(JavaCompiler compiler, Context context) {
        try {
            int javaCompilerVersion = Javac.getJavaCompilerVersion();
            Class<?> parserFactory = javaCompilerVersion <= 6 ? Class.forName("lombok.javac.java6.CommentCollectingParserFactory") : (javaCompilerVersion == 7 ? Class.forName("lombok.javac.java7.CommentCollectingParserFactory") : (javaCompilerVersion == 8 ? Class.forName("lombok.javac.java8.CommentCollectingParserFactory") : Class.forName("lombok.javac.java9.CommentCollectingParserFactory")));
            Permit.getMethod(parserFactory, "setInCompiler", JavaCompiler.class, Context.class).invoke(null, compiler, context);
        }
        catch (InvocationTargetException e) {
            throw Javac.sneakyThrow(e.getCause());
        }
        catch (Exception e) {
            throw Javac.sneakyThrow(e);
        }
    }
}
