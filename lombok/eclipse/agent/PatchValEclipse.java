package lombok.eclipse.agent;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import lombok.Lombok;
import lombok.eclipse.agent.PatchVal;
import lombok.permit.Permit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.ForeachStatement;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.parser.Parser;

public class PatchValEclipse {
    private static final Field FIELD_NAME_INDEX;

    static {
        Field f = null;
        try {
            f = Permit.getField(Name.class, "index");
        }
        catch (Throwable throwable) {}
        FIELD_NAME_INDEX = f;
    }

    public static void copyInitializationOfForEachIterable(Parser parser) {
        int astPtr;
        org.eclipse.jdt.internal.compiler.ast.ASTNode[] astStack;
        try {
            astStack = (org.eclipse.jdt.internal.compiler.ast.ASTNode[])Reflection.astStackField.get(parser);
            astPtr = (Integer)Reflection.astPtrField.get(parser);
        }
        catch (Exception exception) {
            return;
        }
        ForeachStatement foreachDecl = (ForeachStatement)astStack[astPtr];
        Expression init = foreachDecl.collection;
        if (init == null) {
            return;
        }
        boolean val = PatchValEclipse.couldBeVal(parser == null ? null : (parser.compilationUnit == null ? null : parser.compilationUnit.imports), foreachDecl.elementVariable.type);
        boolean var = PatchValEclipse.couldBeVar(parser == null ? null : (parser.compilationUnit == null ? null : parser.compilationUnit.imports), foreachDecl.elementVariable.type);
        if (foreachDecl.elementVariable == null || !val && !var) {
            return;
        }
        try {
            if (Reflection.iterableCopyField != null) {
                Reflection.iterableCopyField.set(foreachDecl.elementVariable, init);
            }
        }
        catch (Exception exception) {}
    }

    public static void copyInitializationOfLocalDeclaration(Parser parser) {
        int astPtr;
        org.eclipse.jdt.internal.compiler.ast.ASTNode[] astStack;
        try {
            astStack = (org.eclipse.jdt.internal.compiler.ast.ASTNode[])Reflection.astStackField.get(parser);
            astPtr = (Integer)Reflection.astPtrField.get(parser);
        }
        catch (Exception exception) {
            return;
        }
        AbstractVariableDeclaration variableDecl = (AbstractVariableDeclaration)astStack[astPtr];
        if (!(variableDecl instanceof LocalDeclaration)) {
            return;
        }
        Expression init = variableDecl.initialization;
        if (init == null) {
            return;
        }
        boolean val = PatchValEclipse.couldBeVal(parser == null ? null : (parser.compilationUnit == null ? null : parser.compilationUnit.imports), variableDecl.type);
        boolean var = PatchValEclipse.couldBeVar(parser == null ? null : (parser.compilationUnit == null ? null : parser.compilationUnit.imports), variableDecl.type);
        if (!val && !var) {
            return;
        }
        try {
            if (Reflection.initCopyField != null) {
                Reflection.initCopyField.set(variableDecl, init);
            }
        }
        catch (Exception exception) {}
    }

    private static boolean couldBeVal(ImportReference[] imports, TypeReference type) {
        return PatchVal.couldBe(imports, "lombok.val", type);
    }

    private static boolean couldBeVar(ImportReference[] imports, TypeReference type) {
        return PatchVal.couldBe(imports, "lombok.experimental.var", type) || PatchVal.couldBe(imports, "lombok.var", type);
    }

    public static void addFinalAndValAnnotationToSingleVariableDeclaration(Object converter, SingleVariableDeclaration out, LocalDeclaration in) {
        List modifiers = out.modifiers();
        PatchValEclipse.addFinalAndValAnnotationToModifierList(converter, modifiers, out.getAST(), in);
    }

    public static void addFinalAndValAnnotationToVariableDeclarationStatement(Object converter, VariableDeclarationStatement out, LocalDeclaration in) {
        List modifiers = out.modifiers();
        PatchValEclipse.addFinalAndValAnnotationToModifierList(converter, modifiers, out.getAST(), in);
    }

    public static void addFinalAndValAnnotationToModifierList(Object converter, List<IExtendedModifier> modifiers, AST ast, LocalDeclaration in) {
        MarkerAnnotation newAnnotation;
        if (in.annotations == null) {
            return;
        }
        boolean found = false;
        Annotation valAnnotation = null;
        Annotation varAnnotation = null;
        Annotation[] annotationArray = in.annotations;
        int n = in.annotations.length;
        int n2 = 0;
        while (n2 < n) {
            Annotation ann = annotationArray[n2];
            if (PatchValEclipse.couldBeVal(null, ann.type)) {
                found = true;
                valAnnotation = ann;
            }
            if (PatchValEclipse.couldBeVar(null, ann.type)) {
                found = true;
                varAnnotation = ann;
            }
            ++n2;
        }
        if (!found) {
            return;
        }
        if (modifiers == null) {
            return;
        }
        boolean finalIsPresent = false;
        boolean valIsPresent = false;
        boolean varIsPresent = false;
        for (IExtendedModifier present : modifiers) {
            Name typeName;
            if (present instanceof Modifier) {
                Modifier.ModifierKeyword keyword = ((Modifier)present).getKeyword();
                if (keyword == null) continue;
                if (keyword.toFlagValue() == 16) {
                    finalIsPresent = true;
                }
            }
            if (!(present instanceof org.eclipse.jdt.core.dom.Annotation) || (typeName = ((org.eclipse.jdt.core.dom.Annotation)present).getTypeName()) == null) continue;
            String fullyQualifiedName = typeName.getFullyQualifiedName();
            if ("val".equals(fullyQualifiedName) || "lombok.val".equals(fullyQualifiedName)) {
                valIsPresent = true;
            }
            if (!"var".equals(fullyQualifiedName) && !"lombok.var".equals(fullyQualifiedName) && !"lombok.experimental.var".equals(fullyQualifiedName)) continue;
            varIsPresent = true;
        }
        if (!finalIsPresent && valAnnotation != null) {
            modifiers.add((IExtendedModifier)PatchValEclipse.createModifier(ast, Modifier.ModifierKeyword.FINAL_KEYWORD, valAnnotation.sourceStart, valAnnotation.sourceEnd));
        }
        if (!valIsPresent && valAnnotation != null) {
            newAnnotation = PatchValEclipse.createValVarAnnotation(ast, valAnnotation, valAnnotation.sourceStart, valAnnotation.sourceEnd);
            try {
                Reflection.astConverterRecordNodes.invoke(converter, newAnnotation, valAnnotation);
                Reflection.astConverterRecordNodes.invoke(converter, newAnnotation.getTypeName(), valAnnotation.type);
            }
            catch (IllegalAccessException e) {
                throw Lombok.sneakyThrow(e);
            }
            catch (InvocationTargetException e) {
                throw Lombok.sneakyThrow(e.getCause());
            }
            modifiers.add((IExtendedModifier)newAnnotation);
        }
        if (!varIsPresent && varAnnotation != null) {
            newAnnotation = PatchValEclipse.createValVarAnnotation(ast, varAnnotation, varAnnotation.sourceStart, varAnnotation.sourceEnd);
            try {
                Reflection.astConverterRecordNodes.invoke(converter, newAnnotation, varAnnotation);
                Reflection.astConverterRecordNodes.invoke(converter, newAnnotation.getTypeName(), varAnnotation.type);
            }
            catch (IllegalAccessException e) {
                throw Lombok.sneakyThrow(e);
            }
            catch (InvocationTargetException e) {
                throw Lombok.sneakyThrow(e.getCause());
            }
            modifiers.add((IExtendedModifier)newAnnotation);
        }
    }

    public static Modifier createModifier(AST ast, Modifier.ModifierKeyword keyword, int start, int end) {
        Modifier modifier = null;
        try {
            modifier = (Modifier)Reflection.modifierConstructor.newInstance(ast);
        }
        catch (InstantiationException e) {
            throw Lombok.sneakyThrow(e);
        }
        catch (IllegalAccessException e) {
            throw Lombok.sneakyThrow(e);
        }
        catch (InvocationTargetException e) {
            throw Lombok.sneakyThrow(e);
        }
        if (modifier != null) {
            modifier.setKeyword(keyword);
            modifier.setSourceRange(start, end - start + 1);
        }
        return modifier;
    }

    public static MarkerAnnotation createValVarAnnotation(AST ast, Annotation original, int start, int end) {
        Object tokens;
        MarkerAnnotation out = null;
        try {
            out = (MarkerAnnotation)Reflection.markerAnnotationConstructor.newInstance(ast);
        }
        catch (InstantiationException e) {
            throw Lombok.sneakyThrow(e);
        }
        catch (IllegalAccessException e) {
            throw Lombok.sneakyThrow(e);
        }
        catch (InvocationTargetException e) {
            throw Lombok.sneakyThrow(e);
        }
        if (original.type instanceof SingleTypeReference) {
            tokens = new char[][]{((SingleTypeReference)original.type).token};
        } else if (original.type instanceof QualifiedTypeReference) {
            tokens = ((QualifiedTypeReference)original.type).tokens;
        } else {
            return null;
        }
        if (out != null) {
            SimpleName valName = ast.newSimpleName(new String(tokens[((char[][])tokens).length - 1]));
            valName.setSourceRange(start, end - start + 1);
            if (((char[][])tokens).length == 1) {
                out.setTypeName((Name)valName);
                PatchValEclipse.setIndex((Name)valName, 1);
            } else if (((char[][])tokens).length == 2) {
                SimpleName lombokName = ast.newSimpleName("lombok");
                lombokName.setSourceRange(start, end - start + 1);
                PatchValEclipse.setIndex((Name)lombokName, 1);
                PatchValEclipse.setIndex((Name)valName, 2);
                QualifiedName fullName = ast.newQualifiedName((Name)lombokName, valName);
                PatchValEclipse.setIndex((Name)fullName, 1);
                fullName.setSourceRange(start, end - start + 1);
                out.setTypeName((Name)fullName);
            } else {
                SimpleName lombokName = ast.newSimpleName("lombok");
                lombokName.setSourceRange(start, end - start + 1);
                SimpleName experimentalName = ast.newSimpleName("experimental");
                lombokName.setSourceRange(start, end - start + 1);
                PatchValEclipse.setIndex((Name)lombokName, 1);
                PatchValEclipse.setIndex((Name)experimentalName, 2);
                PatchValEclipse.setIndex((Name)valName, 3);
                QualifiedName lombokExperimentalName = ast.newQualifiedName((Name)lombokName, experimentalName);
                lombokExperimentalName.setSourceRange(start, end - start + 1);
                PatchValEclipse.setIndex((Name)lombokExperimentalName, 1);
                QualifiedName fullName = ast.newQualifiedName((Name)lombokExperimentalName, valName);
                PatchValEclipse.setIndex((Name)fullName, 1);
                fullName.setSourceRange(start, end - start + 1);
                out.setTypeName((Name)fullName);
            }
            out.setSourceRange(start, end - start + 1);
        }
        return out;
    }

    private static void setIndex(Name name, int index) {
        try {
            if (FIELD_NAME_INDEX != null) {
                FIELD_NAME_INDEX.set(name, index);
            }
        }
        catch (Exception exception) {}
    }

    public static final class Reflection {
        private static final Field initCopyField;
        private static final Field iterableCopyField;
        private static final Field astStackField;
        private static final Field astPtrField;
        private static final Constructor<Modifier> modifierConstructor;
        private static final Constructor<MarkerAnnotation> markerAnnotationConstructor;
        private static final Method astConverterRecordNodes;

        static {
            Field a = null;
            Field b = null;
            Field c = null;
            Field d = null;
            Constructor<Modifier> f = null;
            Constructor<MarkerAnnotation> g = null;
            Method h = null;
            try {
                a = Permit.getField(LocalDeclaration.class, "$initCopy");
                b = Permit.getField(LocalDeclaration.class, "$iterableCopy");
            }
            catch (Throwable throwable) {}
            try {
                c = Permit.getField(Parser.class, "astStack");
                d = Permit.getField(Parser.class, "astPtr");
                f = Permit.getConstructor(Modifier.class, AST.class);
                g = Permit.getConstructor(MarkerAnnotation.class, AST.class);
                Class<?> z = Class.forName("org.eclipse.jdt.core.dom.ASTConverter");
                h = Permit.getMethod(z, "recordNodes", ASTNode.class, org.eclipse.jdt.internal.compiler.ast.ASTNode.class);
            }
            catch (Throwable throwable) {}
            initCopyField = a;
            iterableCopyField = b;
            astStackField = c;
            astPtrField = d;
            modifierConstructor = f;
            markerAnnotationConstructor = g;
            astConverterRecordNodes = h;
        }
    }
}
