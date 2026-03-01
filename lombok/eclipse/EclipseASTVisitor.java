package lombok.eclipse;

import java.io.PrintStream;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MarkerAnnotation;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.NormalAnnotation;
import org.eclipse.jdt.internal.compiler.ast.SingleMemberAnnotation;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;

public interface EclipseASTVisitor {
    public void visitCompilationUnit(EclipseNode var1, CompilationUnitDeclaration var2);

    public void endVisitCompilationUnit(EclipseNode var1, CompilationUnitDeclaration var2);

    public void visitType(EclipseNode var1, TypeDeclaration var2);

    public void visitAnnotationOnType(TypeDeclaration var1, EclipseNode var2, Annotation var3);

    public void endVisitType(EclipseNode var1, TypeDeclaration var2);

    public void visitField(EclipseNode var1, FieldDeclaration var2);

    public void visitAnnotationOnField(FieldDeclaration var1, EclipseNode var2, Annotation var3);

    public void endVisitField(EclipseNode var1, FieldDeclaration var2);

    public void visitInitializer(EclipseNode var1, Initializer var2);

    public void endVisitInitializer(EclipseNode var1, Initializer var2);

    public void visitMethod(EclipseNode var1, AbstractMethodDeclaration var2);

    public void visitAnnotationOnMethod(AbstractMethodDeclaration var1, EclipseNode var2, Annotation var3);

    public void endVisitMethod(EclipseNode var1, AbstractMethodDeclaration var2);

    public void visitMethodArgument(EclipseNode var1, Argument var2, AbstractMethodDeclaration var3);

    public void visitAnnotationOnMethodArgument(Argument var1, AbstractMethodDeclaration var2, EclipseNode var3, Annotation var4);

    public void endVisitMethodArgument(EclipseNode var1, Argument var2, AbstractMethodDeclaration var3);

    public void visitLocal(EclipseNode var1, LocalDeclaration var2);

    public void visitAnnotationOnLocal(LocalDeclaration var1, EclipseNode var2, Annotation var3);

    public void endVisitLocal(EclipseNode var1, LocalDeclaration var2);

    public void visitTypeUse(EclipseNode var1, TypeReference var2);

    public void visitAnnotationOnTypeUse(TypeReference var1, EclipseNode var2, Annotation var3);

    public void endVisitTypeUse(EclipseNode var1, TypeReference var2);

    public void visitStatement(EclipseNode var1, Statement var2);

    public void endVisitStatement(EclipseNode var1, Statement var2);

    public boolean isDeferUntilPostDiet();

    public static class Printer
    implements EclipseASTVisitor {
        private final PrintStream out;
        private final boolean printContent;
        private int disablePrinting = 0;
        private int indent = 0;
        private boolean printClassNames = false;
        private final boolean printPositions;

        public boolean deferUntilPostDiet() {
            return false;
        }

        public Printer(boolean printContent) {
            this(printContent, System.out, false);
        }

        public Printer(boolean printContent, PrintStream out, boolean printPositions) {
            this.printContent = printContent;
            this.out = out;
            this.printPositions = printPositions;
        }

        private void forcePrint(String text, Object ... params) {
            Object[] t;
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while (i < this.indent) {
                sb.append("  ");
                ++i;
            }
            sb.append(text);
            if (this.printClassNames && params.length > 0) {
                sb.append(" [");
                int i2 = 0;
                while (i2 < params.length) {
                    if (i2 > 0) {
                        sb.append(", ");
                    }
                    sb.append("%s");
                    ++i2;
                }
                sb.append("]");
                t = new Object[params.length + params.length];
                i2 = 0;
                while (i2 < params.length) {
                    t[i2] = params[i2];
                    t[i2 + params.length] = params[i2] == null ? "NULL " : params[i2].getClass();
                    ++i2;
                }
            } else {
                t = params;
            }
            sb.append("\n");
            this.out.printf(sb.toString(), t);
            this.out.flush();
        }

        private void print(String text, Object ... params) {
            if (this.disablePrinting == 0) {
                this.forcePrint(text, params);
            }
        }

        private String str(char[] c) {
            if (c == null) {
                return "(NULL)";
            }
            return new String(c);
        }

        private String str(TypeReference type) {
            if (type == null) {
                return "(NULL)";
            }
            char[][] c = type.getTypeName();
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            char[][] cArray = c;
            int n = c.length;
            int n2 = 0;
            while (n2 < n) {
                char[] d = cArray[n2];
                sb.append(first ? "" : ".").append(new String(d));
                first = false;
                ++n2;
            }
            return sb.toString();
        }

        @Override
        public void visitCompilationUnit(EclipseNode node, CompilationUnitDeclaration unit) {
            this.out.println("---------------------------------------------------------");
            this.out.println(node.isCompleteParse() ? "COMPLETE" : "incomplete");
            this.print("<CUD %s%s%s>", node.getFileName(), EclipseHandlerUtil.isGenerated((ASTNode)unit) ? " (GENERATED)" : "", this.position(node));
            ++this.indent;
        }

        @Override
        public void endVisitCompilationUnit(EclipseNode node, CompilationUnitDeclaration unit) {
            --this.indent;
            this.print("</CUD>", new Object[0]);
        }

        private String printFlags(int flags, ASTNode node) {
            StringBuilder out = new StringBuilder();
            if ((flags & 1) != 0) {
                flags &= 0xFFFFFFFE;
                out.append("public ");
            }
            if ((flags & 2) != 0) {
                flags &= 0xFFFFFFFD;
                out.append("private ");
            }
            if ((flags & 4) != 0) {
                flags &= 0xFFFFFFFB;
                out.append("protected ");
            }
            if ((flags & 8) != 0) {
                flags &= 0xFFFFFFF7;
                out.append("static ");
            }
            if ((flags & 0x10) != 0) {
                flags &= 0xFFFFFFEF;
                out.append("final ");
            }
            if ((flags & 0x20) != 0) {
                flags &= 0xFFFFFFDF;
                out.append("synchronized ");
            }
            if ((flags & 0x100) != 0) {
                flags &= 0xFFFFFEFF;
                out.append("native ");
            }
            if ((flags & 0x200) != 0) {
                flags &= 0xFFFFFDFF;
                out.append("interface ");
            }
            if ((flags & 0x400) != 0) {
                flags &= 0xFFFFFBFF;
                out.append("abstract ");
            }
            if ((flags & 0x800) != 0) {
                flags &= 0xFFFFF7FF;
                out.append("strictfp ");
            }
            if ((flags & 0x1000) != 0) {
                flags &= 0xFFFFEFFF;
                out.append("synthetic ");
            }
            if ((flags & 0x2000) != 0) {
                flags &= 0xFFFFDFFF;
                out.append("annotation ");
            }
            if ((flags & 0x4000) != 0) {
                flags &= 0xFFFFBFFF;
                out.append("enum ");
            }
            if ((flags & 0x40) != 0) {
                flags &= 0xFFFFFFBF;
                if (node instanceof FieldDeclaration) {
                    out.append("volatile ");
                } else {
                    out.append("volatile/bridge ");
                }
            }
            if ((flags & 0x80) != 0) {
                flags &= 0xFFFFFF7F;
                if (node instanceof Argument) {
                    out.append("varargs ");
                } else if (node instanceof FieldDeclaration) {
                    out.append("transient ");
                } else {
                    out.append("transient/varargs ");
                }
            }
            if (flags != 0) {
                out.append(String.format(" 0x%08X ", flags));
            }
            return out.toString().trim();
        }

        @Override
        public void visitType(EclipseNode node, TypeDeclaration type) {
            this.print("<TYPE %s%s%s> %s", this.str(type.name), EclipseHandlerUtil.isGenerated((ASTNode)type) ? " (GENERATED)" : "", this.position(node), this.printFlags(type.modifiers, (ASTNode)type));
            ++this.indent;
            if (this.printContent) {
                this.print("%s", type);
                ++this.disablePrinting;
            }
        }

        @Override
        public void visitAnnotationOnType(TypeDeclaration type, EclipseNode node, Annotation annotation) {
            this.forcePrint("<ANNOTATION%s: %s%s />", EclipseHandlerUtil.isGenerated((ASTNode)annotation) ? " (GENERATED)" : "", annotation, this.position(node));
        }

        @Override
        public void endVisitType(EclipseNode node, TypeDeclaration type) {
            if (this.printContent) {
                --this.disablePrinting;
            }
            --this.indent;
            this.print("</TYPE %s>", this.str(type.name));
        }

        @Override
        public void visitInitializer(EclipseNode node, Initializer initializer) {
            Block block = initializer.block;
            boolean s = block != null && block.statements != null;
            this.print("<%s INITIALIZER: %s%s%s>", (initializer.modifiers & 8) != 0 ? "static" : "instance", s ? "filled" : "blank", EclipseHandlerUtil.isGenerated((ASTNode)initializer) ? " (GENERATED)" : "", this.position(node));
            ++this.indent;
            if (this.printContent) {
                if (initializer.block != null) {
                    this.print("%s", initializer.block);
                }
                ++this.disablePrinting;
            }
        }

        @Override
        public void endVisitInitializer(EclipseNode node, Initializer initializer) {
            if (this.printContent) {
                --this.disablePrinting;
            }
            --this.indent;
            this.print("</%s INITIALIZER>", (initializer.modifiers & 8) != 0 ? "static" : "instance");
        }

        @Override
        public void visitField(EclipseNode node, FieldDeclaration field) {
            this.print("<FIELD%s %s %s = %s%s> %s", EclipseHandlerUtil.isGenerated((ASTNode)field) ? " (GENERATED)" : "", this.str(field.type), this.str(field.name), field.initialization, this.position(node), this.printFlags(field.modifiers, (ASTNode)field));
            ++this.indent;
            if (this.printContent) {
                if (field.initialization != null) {
                    this.print("%s", field.initialization);
                }
                ++this.disablePrinting;
            }
        }

        @Override
        public void visitAnnotationOnField(FieldDeclaration field, EclipseNode node, Annotation annotation) {
            this.forcePrint("<ANNOTATION%s: %s%s />", EclipseHandlerUtil.isGenerated((ASTNode)annotation) ? " (GENERATED)" : "", annotation, this.position(node));
        }

        @Override
        public void endVisitField(EclipseNode node, FieldDeclaration field) {
            if (this.printContent) {
                --this.disablePrinting;
            }
            --this.indent;
            this.print("</FIELD %s %s>", this.str(field.type), this.str(field.name));
        }

        @Override
        public void visitMethod(EclipseNode node, AbstractMethodDeclaration method) {
            String type = method instanceof ConstructorDeclaration ? "CONSTRUCTOR" : "METHOD";
            this.print("<%s %s: %s%s%s> %s", type, this.str(method.selector), method.statements != null ? "filled(" + method.statements.length + ")" : "blank", EclipseHandlerUtil.isGenerated((ASTNode)method) ? " (GENERATED)" : "", this.position(node), this.printFlags(method.modifiers, (ASTNode)method));
            ++this.indent;
            if (method instanceof ConstructorDeclaration) {
                ConstructorDeclaration cd = (ConstructorDeclaration)method;
                this.print("--> constructorCall: %s", cd.constructorCall == null ? "-NONE-" : cd.constructorCall);
            }
            if (this.printContent) {
                if (method.statements != null) {
                    this.print("%s", method);
                }
                ++this.disablePrinting;
            }
        }

        @Override
        public void visitAnnotationOnMethod(AbstractMethodDeclaration method, EclipseNode node, Annotation annotation) {
            this.forcePrint("<ANNOTATION%s: %s%s>", EclipseHandlerUtil.isGenerated((ASTNode)method) ? " (GENERATED)" : "", annotation, this.position(node));
            if (annotation instanceof MarkerAnnotation || this.disablePrinting != 0) {
                this.forcePrint("<ANNOTATION%s: %s%s />", EclipseHandlerUtil.isGenerated((ASTNode)method) ? " (GENERATED)" : "", annotation, this.position(node));
            } else {
                this.forcePrint("<ANNOTATION%s: %s%s>", EclipseHandlerUtil.isGenerated((ASTNode)method) ? " (GENERATED)" : "", annotation, this.position(node));
                ++this.indent;
                if (annotation instanceof SingleMemberAnnotation) {
                    Expression expr = ((SingleMemberAnnotation)annotation).memberValue;
                    this.print("<SINGLE-MEMBER-VALUE %s /> %s", expr.getClass(), expr);
                }
                if (annotation instanceof NormalAnnotation) {
                    MemberValuePair[] memberValuePairArray = ((NormalAnnotation)annotation).memberValuePairs;
                    int n = ((NormalAnnotation)annotation).memberValuePairs.length;
                    int n2 = 0;
                    while (n2 < n) {
                        MemberValuePair mvp = memberValuePairArray[n2];
                        this.print("<Member %s: %s /> %s", new String(mvp.name), mvp.value.getClass(), mvp.value);
                        ++n2;
                    }
                }
                --this.indent;
            }
        }

        @Override
        public void endVisitMethod(EclipseNode node, AbstractMethodDeclaration method) {
            if (this.printContent) {
                --this.disablePrinting;
            }
            String type = method instanceof ConstructorDeclaration ? "CONSTRUCTOR" : "METHOD";
            --this.indent;
            this.print("</%s %s>", type, this.str(method.selector));
        }

        @Override
        public void visitMethodArgument(EclipseNode node, Argument arg, AbstractMethodDeclaration method) {
            this.print("<METHODARG%s %s %s = %s%s> %s", EclipseHandlerUtil.isGenerated((ASTNode)arg) ? " (GENERATED)" : "", this.str(arg.type), this.str(arg.name), arg.initialization, this.position(node), this.printFlags(arg.modifiers, (ASTNode)arg));
            ++this.indent;
        }

        @Override
        public void visitAnnotationOnMethodArgument(Argument arg, AbstractMethodDeclaration method, EclipseNode node, Annotation annotation) {
            this.print("<ANNOTATION%s: %s%s />", EclipseHandlerUtil.isGenerated((ASTNode)annotation) ? " (GENERATED)" : "", annotation, this.position(node));
        }

        @Override
        public void endVisitMethodArgument(EclipseNode node, Argument arg, AbstractMethodDeclaration method) {
            --this.indent;
            this.print("</METHODARG %s %s>", this.str(arg.type), this.str(arg.name));
        }

        @Override
        public void visitLocal(EclipseNode node, LocalDeclaration local) {
            this.print("<LOCAL%s %s %s = %s%s> %s", EclipseHandlerUtil.isGenerated((ASTNode)local) ? " (GENERATED)" : "", this.str(local.type), this.str(local.name), local.initialization, this.position(node), this.printFlags(local.modifiers, (ASTNode)local));
            ++this.indent;
        }

        @Override
        public void visitAnnotationOnLocal(LocalDeclaration local, EclipseNode node, Annotation annotation) {
            this.print("<ANNOTATION%s: %s />", EclipseHandlerUtil.isGenerated((ASTNode)annotation) ? " (GENERATED)" : "", annotation);
        }

        @Override
        public void endVisitLocal(EclipseNode node, LocalDeclaration local) {
            --this.indent;
            this.print("</LOCAL %s %s>", this.str(local.type), this.str(local.name));
        }

        @Override
        public void visitTypeUse(EclipseNode typeUseNode, TypeReference typeUse) {
            this.print("<TYPE %s>", typeUse.getClass());
            ++this.indent;
            this.print("%s", typeUse);
        }

        @Override
        public void visitAnnotationOnTypeUse(TypeReference typeUse, EclipseNode annotationNode, Annotation annotation) {
            this.print("<ANNOTATION%s: %s />", EclipseHandlerUtil.isGenerated((ASTNode)annotation) ? " (GENERATED)" : "", annotation);
        }

        @Override
        public void endVisitTypeUse(EclipseNode typeUseNode, TypeReference typeUse) {
            --this.indent;
            this.print("</TYPE %s>", typeUse.getClass());
        }

        @Override
        public void visitStatement(EclipseNode node, Statement statement) {
            this.print("<%s%s%s>", statement.getClass(), EclipseHandlerUtil.isGenerated((ASTNode)statement) ? " (GENERATED)" : "", this.position(node));
            if (statement instanceof AllocationExpression) {
                AllocationExpression alloc = (AllocationExpression)statement;
                this.print(" --> arguments: %s", alloc.arguments == null ? "NULL" : Integer.valueOf(alloc.arguments.length));
                this.print(" --> genericTypeArguments: %s", alloc.genericTypeArguments == null ? "NULL" : Integer.valueOf(alloc.genericTypeArguments.length));
                this.print(" --> typeArguments: %s", alloc.typeArguments == null ? "NULL" : Integer.valueOf(alloc.typeArguments.length));
                this.print(" --> enumConstant: %s", alloc.enumConstant);
                this.print(" --> inferredReturnType: %s", alloc.inferredReturnType);
            }
            ++this.indent;
            this.print("%s", statement);
        }

        @Override
        public void endVisitStatement(EclipseNode node, Statement statement) {
            --this.indent;
            this.print("</%s>", statement.getClass());
        }

        String position(EclipseNode node) {
            if (!this.printPositions) {
                return "";
            }
            int start = ((ASTNode)node.get()).sourceStart();
            int end = ((ASTNode)node.get()).sourceEnd();
            return String.format(" [%d, %d]", start, end);
        }

        @Override
        public boolean isDeferUntilPostDiet() {
            return false;
        }
    }
}
