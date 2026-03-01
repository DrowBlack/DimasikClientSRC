package lombok.javac;

import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import java.io.PrintStream;
import java.lang.reflect.Field;
import lombok.javac.JavacNode;

public interface JavacASTVisitor {
    public void setTrees(Trees var1);

    public void visitCompilationUnit(JavacNode var1, JCTree.JCCompilationUnit var2);

    public void endVisitCompilationUnit(JavacNode var1, JCTree.JCCompilationUnit var2);

    public void visitType(JavacNode var1, JCTree.JCClassDecl var2);

    public void visitAnnotationOnType(JCTree.JCClassDecl var1, JavacNode var2, JCTree.JCAnnotation var3);

    public void endVisitType(JavacNode var1, JCTree.JCClassDecl var2);

    public void visitField(JavacNode var1, JCTree.JCVariableDecl var2);

    public void visitAnnotationOnField(JCTree.JCVariableDecl var1, JavacNode var2, JCTree.JCAnnotation var3);

    public void endVisitField(JavacNode var1, JCTree.JCVariableDecl var2);

    public void visitInitializer(JavacNode var1, JCTree.JCBlock var2);

    public void endVisitInitializer(JavacNode var1, JCTree.JCBlock var2);

    public void visitMethod(JavacNode var1, JCTree.JCMethodDecl var2);

    public void visitAnnotationOnMethod(JCTree.JCMethodDecl var1, JavacNode var2, JCTree.JCAnnotation var3);

    public void endVisitMethod(JavacNode var1, JCTree.JCMethodDecl var2);

    public void visitMethodArgument(JavacNode var1, JCTree.JCVariableDecl var2, JCTree.JCMethodDecl var3);

    public void visitAnnotationOnMethodArgument(JCTree.JCVariableDecl var1, JCTree.JCMethodDecl var2, JavacNode var3, JCTree.JCAnnotation var4);

    public void endVisitMethodArgument(JavacNode var1, JCTree.JCVariableDecl var2, JCTree.JCMethodDecl var3);

    public void visitLocal(JavacNode var1, JCTree.JCVariableDecl var2);

    public void visitAnnotationOnLocal(JCTree.JCVariableDecl var1, JavacNode var2, JCTree.JCAnnotation var3);

    public void endVisitLocal(JavacNode var1, JCTree.JCVariableDecl var2);

    public void visitTypeUse(JavacNode var1, JCTree var2);

    public void visitAnnotationOnTypeUse(JCTree var1, JavacNode var2, JCTree.JCAnnotation var3);

    public void endVisitTypeUse(JavacNode var1, JCTree var2);

    public void visitStatement(JavacNode var1, JCTree var2);

    public void endVisitStatement(JavacNode var1, JCTree var2);

    public static class Printer
    implements JavacASTVisitor {
        private final PrintStream out;
        private final boolean printContent;
        private int disablePrinting = 0;
        private int indent = 0;

        public Printer(boolean printContent) {
            this(printContent, System.out);
        }

        public Printer(boolean printContent, PrintStream out) {
            this.printContent = printContent;
            this.out = out;
        }

        @Override
        public void setTrees(Trees trees) {
        }

        private void forcePrint(String text, Object ... params) {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while (i < this.indent) {
                sb.append("  ");
                ++i;
            }
            this.out.printf(sb.append(text).append('\n').toString(), params);
            this.out.flush();
        }

        private void print(String text, Object ... params) {
            if (this.disablePrinting == 0) {
                this.forcePrint(text, params);
            }
        }

        @Override
        public void visitCompilationUnit(JavacNode LombokNode2, JCTree.JCCompilationUnit unit) {
            this.out.println("---------------------------------------------------------");
            this.print("<CU %s>", LombokNode2.getFileName());
            ++this.indent;
        }

        @Override
        public void endVisitCompilationUnit(JavacNode node, JCTree.JCCompilationUnit unit) {
            --this.indent;
            this.print("</CUD>", new Object[0]);
        }

        private String printFlags(long f) {
            return Flags.toString(f);
        }

        @Override
        public void visitType(JavacNode node, JCTree.JCClassDecl type) {
            this.print("<TYPE %s> %s", type.name, this.printFlags(type.mods.flags));
            ++this.indent;
            if (this.printContent) {
                this.print("%s", type);
                ++this.disablePrinting;
            }
        }

        @Override
        public void visitAnnotationOnType(JCTree.JCClassDecl type, JavacNode node, JCTree.JCAnnotation annotation) {
            this.forcePrint("<ANNOTATION: %s />", annotation);
        }

        @Override
        public void endVisitType(JavacNode node, JCTree.JCClassDecl type) {
            if (this.printContent) {
                --this.disablePrinting;
            }
            --this.indent;
            this.print("</TYPE %s>", type.name);
        }

        @Override
        public void visitInitializer(JavacNode node, JCTree.JCBlock initializer) {
            this.print("<%s INITIALIZER>", initializer.isStatic() ? "static" : "instance");
            ++this.indent;
            if (this.printContent) {
                this.print("%s", initializer);
                ++this.disablePrinting;
            }
        }

        @Override
        public void endVisitInitializer(JavacNode node, JCTree.JCBlock initializer) {
            if (this.printContent) {
                --this.disablePrinting;
            }
            --this.indent;
            this.print("</%s INITIALIZER>", initializer.isStatic() ? "static" : "instance");
        }

        @Override
        public void visitField(JavacNode node, JCTree.JCVariableDecl field) {
            this.print("<FIELD %s %s> %s", field.vartype, field.name, this.printFlags(field.mods.flags));
            ++this.indent;
            if (this.printContent) {
                if (field.init != null) {
                    this.print("%s", field.init);
                }
                ++this.disablePrinting;
            }
        }

        @Override
        public void visitAnnotationOnField(JCTree.JCVariableDecl field, JavacNode node, JCTree.JCAnnotation annotation) {
            this.forcePrint("<ANNOTATION: %s />", annotation);
        }

        @Override
        public void endVisitField(JavacNode node, JCTree.JCVariableDecl field) {
            if (this.printContent) {
                --this.disablePrinting;
            }
            --this.indent;
            this.print("</FIELD %s %s>", field.vartype, field.name);
        }

        @Override
        public void visitMethod(JavacNode node, JCTree.JCMethodDecl method) {
            JCTree.JCVariableDecl recv;
            String type = method.name.contentEquals("<init>") ? ((method.mods.flags & 0x1000000000L) != 0L ? "DEFAULTCONSTRUCTOR" : "CONSTRUCTOR") : "METHOD";
            this.print("<%s %s> %s returns: %s", type, method.name, this.printFlags(method.mods.flags), method.restype);
            ++this.indent;
            try {
                Field f = JCTree.JCMethodDecl.class.getField("recvparam");
                recv = (JCTree.JCVariableDecl)f.get(method);
            }
            catch (Exception exception) {
                recv = null;
            }
            if (recv != null) {
                List<JCTree.JCAnnotation> annotations = recv.mods.annotations;
                if (recv.mods != null) {
                    annotations = recv.mods.annotations;
                }
                boolean innerContent = annotations != null && annotations.isEmpty();
                this.print("<RECEIVER-PARAM (%s) %s %s%s> %s", recv.vartype == null ? "null" : recv.vartype.getClass().toString(), recv.vartype, recv.name, innerContent ? "" : " /", this.printFlags(recv.mods.flags));
                if (innerContent) {
                    ++this.indent;
                    for (JCTree.JCAnnotation ann : annotations) {
                        this.print("<ANNOTATION: %s />", ann);
                    }
                    --this.indent;
                    this.print("</RECEIVER-PARAM>", new Object[0]);
                }
            }
            if (this.printContent) {
                if (method.body == null) {
                    this.print("(ABSTRACT)", new Object[0]);
                } else {
                    this.print("%s", method.body);
                }
                ++this.disablePrinting;
            }
        }

        @Override
        public void visitAnnotationOnMethod(JCTree.JCMethodDecl method, JavacNode node, JCTree.JCAnnotation annotation) {
            this.forcePrint("<ANNOTATION: %s />", annotation);
        }

        @Override
        public void endVisitMethod(JavacNode node, JCTree.JCMethodDecl method) {
            if (this.printContent) {
                --this.disablePrinting;
            }
            --this.indent;
            this.print("</%s %s>", "METHOD", method.name);
        }

        @Override
        public void visitMethodArgument(JavacNode node, JCTree.JCVariableDecl arg, JCTree.JCMethodDecl method) {
            this.print("<METHODARG (%s) %s %s> %s", arg.vartype.getClass().toString(), arg.vartype, arg.name, this.printFlags(arg.mods.flags));
            ++this.indent;
        }

        @Override
        public void visitAnnotationOnMethodArgument(JCTree.JCVariableDecl arg, JCTree.JCMethodDecl method, JavacNode nodeAnnotation, JCTree.JCAnnotation annotation) {
            this.forcePrint("<ANNOTATION: %s />", annotation);
        }

        @Override
        public void endVisitMethodArgument(JavacNode node, JCTree.JCVariableDecl arg, JCTree.JCMethodDecl method) {
            --this.indent;
            this.print("</METHODARG %s %s>", arg.vartype, arg.name);
        }

        @Override
        public void visitLocal(JavacNode node, JCTree.JCVariableDecl local) {
            this.print("<LOCAL %s %s> %s", local.vartype, local.name, this.printFlags(local.mods.flags));
            ++this.indent;
        }

        @Override
        public void visitAnnotationOnLocal(JCTree.JCVariableDecl local, JavacNode node, JCTree.JCAnnotation annotation) {
            this.print("<ANNOTATION: %s />", annotation);
        }

        @Override
        public void endVisitLocal(JavacNode node, JCTree.JCVariableDecl local) {
            --this.indent;
            this.print("</LOCAL %s %s>", local.vartype, local.name);
        }

        @Override
        public void visitTypeUse(JavacNode node, JCTree typeUse) {
            this.print("<TYPE %s>", typeUse.getClass());
            ++this.indent;
            this.print("%s", typeUse);
        }

        @Override
        public void visitAnnotationOnTypeUse(JCTree typeUse, JavacNode node, JCTree.JCAnnotation annotation) {
            this.print("<ANNOTATION: %s />", annotation);
        }

        @Override
        public void endVisitTypeUse(JavacNode node, JCTree typeUse) {
            --this.indent;
            this.print("</TYPE %s>", typeUse.getClass());
        }

        @Override
        public void visitStatement(JavacNode node, JCTree statement) {
            this.print("<%s>", statement.getClass());
            ++this.indent;
            this.print("%s", statement);
        }

        @Override
        public void endVisitStatement(JavacNode node, JCTree statement) {
            --this.indent;
            this.print("</%s>", statement.getClass());
        }
    }
}
