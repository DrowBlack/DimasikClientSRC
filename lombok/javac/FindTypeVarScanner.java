package lombok.javac;

import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.util.Name;
import java.util.HashSet;
import java.util.Set;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.AbstractTypeVisitor6;

public class FindTypeVarScanner
extends AbstractTypeVisitor6<Void, Void> {
    private Set<String> typeVariables = new HashSet<String>();

    public Set<String> getTypeVariables() {
        return this.typeVariables;
    }

    private Void subVisit(TypeMirror mirror) {
        if (mirror == null) {
            return null;
        }
        return mirror.accept(this, null);
    }

    @Override
    public Void visitPrimitive(PrimitiveType t, Void p) {
        return null;
    }

    @Override
    public Void visitNull(NullType t, Void p) {
        return null;
    }

    @Override
    public Void visitNoType(NoType t, Void p) {
        return null;
    }

    @Override
    public Void visitUnknown(TypeMirror t, Void p) {
        return null;
    }

    @Override
    public Void visitError(ErrorType t, Void p) {
        return null;
    }

    @Override
    public Void visitArray(ArrayType t, Void p) {
        return this.subVisit(t.getComponentType());
    }

    @Override
    public Void visitDeclared(DeclaredType t, Void p) {
        for (TypeMirror typeMirror : t.getTypeArguments()) {
            this.subVisit(typeMirror);
        }
        return null;
    }

    @Override
    public Void visitTypeVariable(TypeVariable t, Void p) {
        Name name = null;
        try {
            name = ((Type)((Object)t)).tsym.name;
        }
        catch (NullPointerException nullPointerException) {}
        if (name != null) {
            this.typeVariables.add(((Object)name).toString());
        }
        this.subVisit(t.getLowerBound());
        this.subVisit(t.getUpperBound());
        return null;
    }

    @Override
    public Void visitWildcard(WildcardType t, Void p) {
        this.subVisit(t.getSuperBound());
        this.subVisit(t.getExtendsBound());
        return null;
    }

    @Override
    public Void visitExecutable(ExecutableType t, Void p) {
        this.subVisit(t.getReturnType());
        for (TypeMirror typeMirror : t.getParameterTypes()) {
            this.subVisit(typeMirror);
        }
        for (TypeMirror typeMirror : t.getThrownTypes()) {
            this.subVisit(typeMirror);
        }
        for (TypeVariable typeVariable : t.getTypeVariables()) {
            this.subVisit(typeVariable);
        }
        return null;
    }
}
