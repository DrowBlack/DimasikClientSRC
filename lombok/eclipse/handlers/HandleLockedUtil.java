package lombok.eclipse.handlers;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.ConfigurationKeys;
import lombok.core.AST;
import lombok.core.handlers.HandlerUtil;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.TryStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;

public final class HandleLockedUtil {
    private static final char[] INSTANCE_LOCK_NAME = "$lock".toCharArray();
    private static final char[] STATIC_LOCK_NAME = "$LOCK".toCharArray();
    private static final char[] LOCK_METHOD = "lock".toCharArray();
    private static final char[] UNLOCK_METHOD = "unlock".toCharArray();

    private HandleLockedUtil() {
    }

    public static void preHandle(String annotationValue, char[][] lockTypeClass, char[][] lockImplClass, EclipseNode annotationNode) {
        EclipseNode methodNode = (EclipseNode)annotationNode.up();
        if (methodNode == null || methodNode.getKind() != AST.Kind.METHOD || !(methodNode.get() instanceof MethodDeclaration)) {
            return;
        }
        MethodDeclaration method = (MethodDeclaration)methodNode.get();
        if (method.isAbstract()) {
            return;
        }
        HandleLockedUtil.createLockField(annotationValue, annotationNode, lockTypeClass, lockImplClass, new AtomicBoolean(method.isStatic()), false);
    }

    private static char[] createLockField(String name, EclipseNode annotationNode, char[][] lockTypeClass, char[][] lockImplClass, AtomicBoolean isStatic, boolean reportErrors) {
        char[] lockName = name.toCharArray();
        Annotation source = (Annotation)annotationNode.get();
        if (lockName.length == 0) {
            lockName = isStatic.get() ? STATIC_LOCK_NAME : INSTANCE_LOCK_NAME;
        }
        EclipseNode typeNode = EclipseHandlerUtil.upToTypeNode(annotationNode);
        EclipseHandlerUtil.MemberExistsResult exists = EclipseHandlerUtil.MemberExistsResult.NOT_EXISTS;
        QualifiedTypeReference lockType = new QualifiedTypeReference(lockTypeClass, new long[5]);
        if (typeNode != null && typeNode.get() instanceof TypeDeclaration) {
            TypeDeclaration typeDecl = (TypeDeclaration)typeNode.get();
            if (typeDecl.fields != null) {
                FieldDeclaration[] fieldDeclarationArray = typeDecl.fields;
                int n = typeDecl.fields.length;
                int n2 = 0;
                while (n2 < n) {
                    FieldDeclaration def = fieldDeclarationArray[n2];
                    char[] fName = def.name;
                    if (fName != null && Arrays.equals(fName, lockName)) {
                        exists = EclipseHandlerUtil.getGeneratedBy((ASTNode)def) == null ? EclipseHandlerUtil.MemberExistsResult.EXISTS_BY_USER : EclipseHandlerUtil.MemberExistsResult.EXISTS_BY_LOMBOK;
                        boolean st = def.isStatic();
                        if (st != isStatic.get() && exists == EclipseHandlerUtil.MemberExistsResult.EXISTS_BY_LOMBOK) {
                            if (reportErrors) {
                                annotationNode.addError(String.format("The generated field %s does not match the static status of this method", new String(lockName)));
                            }
                            return null;
                        }
                        isStatic.set(st);
                        if (exists != EclipseHandlerUtil.MemberExistsResult.EXISTS_BY_LOMBOK || Arrays.deepEquals((Object[])lockType.getTypeName(), (Object[])def.type.getTypeName())) break;
                        annotationNode.addError("Expected field " + new String(lockName) + " to be of type " + lockType + " but got type " + def.type + ". Did you mix @Locked with @Locked.Read/Write on the same generated field?");
                        return null;
                    }
                    ++n2;
                }
            }
        }
        if (exists == EclipseHandlerUtil.MemberExistsResult.NOT_EXISTS) {
            FieldDeclaration fieldDecl = EclipseHandlerUtil.setGeneratedBy(new FieldDeclaration(lockName, 0, -1), (ASTNode)source);
            fieldDecl.declarationSourceEnd = -1;
            fieldDecl.modifiers = (isStatic.get() ? 8 : 0) | 0x10 | 2;
            AllocationExpression lockAlloc = EclipseHandlerUtil.setGeneratedBy(new AllocationExpression(), (ASTNode)source);
            lockAlloc.type = (TypeReference)EclipseHandlerUtil.setGeneratedBy(new QualifiedTypeReference(lockImplClass, new long[5]), (ASTNode)source);
            fieldDecl.type = (TypeReference)EclipseHandlerUtil.setGeneratedBy(new QualifiedTypeReference(lockTypeClass, new long[5]), (ASTNode)source);
            fieldDecl.initialization = lockAlloc;
            EclipseHandlerUtil.injectFieldAndMarkGenerated((EclipseNode)((EclipseNode)annotationNode.up()).up(), fieldDecl);
        }
        return lockName;
    }

    public static void handle(String annotationValue, Annotation ast, EclipseNode annotationNode, String annotationName, char[][] lockTypeClass, char[][] lockImplClass) {
        HandleLockedUtil.handle(annotationValue, ast, annotationNode, annotationName, lockTypeClass, lockImplClass, null);
    }

    public static void handle(String annotationValue, Annotation source, EclipseNode annotationNode, String annotationName, char[][] lockTypeClass, char[][] lockImplClass, char[] lockableMethodName) {
        HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.LOCKED_FLAG_USAGE, annotationName);
        int p1 = source.sourceStart - 1;
        int p2 = source.sourceStart - 2;
        long pos = (long)p1 << 32 | (long)p2;
        EclipseNode methodNode = (EclipseNode)annotationNode.up();
        if (methodNode == null || methodNode.getKind() != AST.Kind.METHOD || !(methodNode.get() instanceof MethodDeclaration)) {
            annotationNode.addError(String.valueOf(annotationName) + " is legal only on methods.");
            return;
        }
        MethodDeclaration method = (MethodDeclaration)methodNode.get();
        if (method.isAbstract()) {
            annotationNode.addError(String.valueOf(annotationName) + " is legal only on concrete methods.");
            return;
        }
        EclipseNode typeNode = EclipseHandlerUtil.upToTypeNode(annotationNode);
        if (!EclipseHandlerUtil.isClassOrEnum(typeNode)) {
            annotationNode.addError(String.valueOf(annotationName) + " is legal only on methods in classes and enums.");
            return;
        }
        AtomicBoolean isStatic = new AtomicBoolean(method.isStatic());
        char[] lockName = HandleLockedUtil.createLockField(annotationValue, annotationNode, lockTypeClass, lockImplClass, isStatic, true);
        if (lockName == null) {
            return;
        }
        if (method.statements == null) {
            return;
        }
        Block block = new Block(0);
        block.statements = method.statements;
        EclipseHandlerUtil.setGeneratedBy(block, (ASTNode)source);
        block.sourceEnd = method.bodyEnd;
        block.sourceStart = method.bodyStart;
        Statement acquireLock = HandleLockedUtil.getLockingStatement((ASTNode)source, typeNode, LOCK_METHOD, lockName, lockableMethodName, isStatic.get(), p1, p2, pos);
        Statement unLock = HandleLockedUtil.getLockingStatement((ASTNode)source, typeNode, UNLOCK_METHOD, lockName, lockableMethodName, isStatic.get(), p1, p2, pos);
        TryStatement tryStatement = new TryStatement();
        tryStatement.tryBlock = block;
        tryStatement.finallyBlock = new Block(0);
        tryStatement.finallyBlock.statements = new Statement[]{unLock};
        method.statements = new Statement[]{acquireLock, tryStatement};
        method.statements[0].sourceEnd = method.bodyEnd;
        method.statements[0].sourceStart = method.bodyStart;
        methodNode.rebuild();
    }

    private static Statement getLockingStatement(ASTNode source, EclipseNode typeNode, char[] lockMethod, char[] lockableObjectName, char[] lockableMethodName, boolean isStatic, int p1, int p2, long pos) {
        MessageSend lockStat = EclipseHandlerUtil.setGeneratedBy(new MessageSend(), source);
        lockStat.receiver = HandleLockedUtil.getLockable(source, typeNode, lockableObjectName, lockableMethodName, isStatic, p1, p2, pos);
        lockStat.selector = lockMethod;
        lockStat.nameSourcePosition = pos;
        lockStat.sourceStart = p1;
        lockStat.sourceEnd = lockStat.statementEnd = p2;
        return lockStat;
    }

    private static Expression getLockable(ASTNode source, EclipseNode typeNode, char[] lockName, char[] lockableMethodName, boolean isStatic, int p1, int p2, long pos) {
        FieldReference lockable;
        FieldReference lockVariable;
        if (isStatic) {
            char[][] n = EclipseHandlerUtil.getQualifiedInnerName(typeNode, lockName);
            long[] ps = new long[n.length];
            Arrays.fill(ps, pos);
            lockVariable = new QualifiedNameReference(n, ps, p1, p2);
        } else {
            lockVariable = new FieldReference(lockName, pos);
            ThisReference thisReference = new ThisReference(p1, p2);
            EclipseHandlerUtil.setGeneratedBy(thisReference, source);
            lockVariable.receiver = thisReference;
        }
        EclipseHandlerUtil.setGeneratedBy(lockVariable, source);
        if (lockableMethodName == null) {
            lockable = lockVariable;
        } else {
            lockable = new MessageSend();
            ((MessageSend)lockable).receiver = lockVariable;
            ((MessageSend)lockable).selector = lockableMethodName;
            ((MessageSend)lockable).nameSourcePosition = pos;
            lockable.sourceStart = p1;
            lockable.sourceEnd = lockable.statementEnd = p2;
        }
        return (Expression)EclipseHandlerUtil.setGeneratedBy(lockable, source);
    }
}
