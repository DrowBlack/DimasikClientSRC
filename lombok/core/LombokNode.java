package lombok.core;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.DiagnosticsReceiver;
import lombok.core.ImportList;
import lombok.core.LombokImmutableList;
import lombok.core.TypeResolver;

public abstract class LombokNode<A extends AST<A, L, N>, L extends LombokNode<A, L, N>, N>
implements DiagnosticsReceiver {
    protected final AST.Kind kind;
    protected final N node;
    protected LombokImmutableList<L> children;
    protected L parent;
    protected boolean isStructurallySignificant;

    protected LombokNode(N node, List<L> children, AST.Kind kind) {
        this.kind = kind;
        this.node = node;
        this.children = children != null ? LombokImmutableList.copyOf(children) : LombokImmutableList.of();
        for (LombokNode child : this.children) {
            child.parent = this;
            if (child.isStructurallySignificant) continue;
            child.isStructurallySignificant = this.calculateIsStructurallySignificant(node);
        }
        this.isStructurallySignificant = this.calculateIsStructurallySignificant(null);
    }

    public abstract A getAst();

    public String toString() {
        return String.format("NODE %s (%s) %s", new Object[]{this.kind, this.node == null ? "(NULL)" : this.node.getClass(), this.node == null ? "" : this.node});
    }

    public String getPackageDeclaration() {
        return ((AST)this.getAst()).getPackageDeclaration();
    }

    public ImportList getImportList() {
        return ((AST)this.getAst()).getImportList();
    }

    public TypeResolver getImportListAsTypeResolver() {
        return ((AST)this.getAst()).getImportListAsTypeResolver();
    }

    protected abstract boolean calculateIsStructurallySignificant(N var1);

    public L getNodeFor(N obj) {
        return ((AST)this.getAst()).get(obj);
    }

    public N get() {
        return this.node;
    }

    public AST.Kind getKind() {
        return this.kind;
    }

    public abstract String getName();

    public L up() {
        L result = this.parent;
        while (result != null && !((LombokNode)result).isStructurallySignificant) {
            result = ((LombokNode)result).parent;
        }
        return result;
    }

    public Collection<L> upFromAnnotationToFields() {
        if (this.getKind() != AST.Kind.ANNOTATION) {
            return Collections.emptyList();
        }
        L field = this.up();
        if (field == null || ((LombokNode)field).getKind() != AST.Kind.FIELD) {
            return Collections.emptyList();
        }
        L type = ((LombokNode)field).up();
        if (type == null || ((LombokNode)type).getKind() != AST.Kind.TYPE) {
            return Collections.emptyList();
        }
        ArrayList<LombokNode> fields = new ArrayList<LombokNode>();
        for (LombokNode potentialField : ((LombokNode)type).down()) {
            if (potentialField.getKind() != AST.Kind.FIELD) continue;
            for (LombokNode child : potentialField.down()) {
                if (child.getKind() != AST.Kind.ANNOTATION || child.get() != this.get()) continue;
                fields.add(potentialField);
            }
        }
        return fields;
    }

    public L directUp() {
        return this.parent;
    }

    public LombokImmutableList<L> down() {
        return this.children;
    }

    public int getLatestJavaSpecSupported() {
        return ((AST)this.getAst()).getLatestJavaSpecSupported();
    }

    public int getSourceVersion() {
        return ((AST)this.getAst()).getSourceVersion();
    }

    public L top() {
        return ((AST)this.getAst()).top();
    }

    public String getFileName() {
        return ((AST)this.getAst()).getFileName();
    }

    public L add(N newChild, AST.Kind newChildKind) {
        ((AST)this.getAst()).setChanged();
        Object n = ((AST)this.getAst()).buildTree(newChild, newChildKind);
        if (n == null) {
            return null;
        }
        ((LombokNode)n).parent = this;
        this.children = this.children.append(n);
        return n;
    }

    public void rebuild() {
        IdentityHashMap oldNodes = new IdentityHashMap();
        this.gatherAndRemoveChildren(oldNodes);
        Object newNode = ((AST)this.getAst()).buildTree(this.get(), this.kind);
        ((AST)this.getAst()).setChanged();
        ((AST)this.getAst()).replaceNewWithExistingOld(oldNodes, newNode);
    }

    private void gatherAndRemoveChildren(Map<N, L> map) {
        for (LombokNode child : this.children) {
            child.gatherAndRemoveChildren(map);
        }
        ((AST)this.getAst()).identityDetector.remove(this.get());
        map.put(this.get(), this);
        this.children = LombokImmutableList.of();
        ((AST)this.getAst()).getNodeMap().remove(this.get());
    }

    public void removeChild(L child) {
        ((AST)this.getAst()).setChanged();
        this.children = this.children.removeElement(child);
    }

    public boolean isStructurallySignificant() {
        return this.isStructurallySignificant;
    }

    public abstract boolean hasAnnotation(Class<? extends Annotation> var1);

    public abstract <Z extends Annotation> AnnotationValues<Z> findAnnotation(Class<Z> var1);

    public abstract boolean isStatic();

    public abstract boolean isFinal();

    public abstract boolean isTransient();

    public abstract boolean isPrimitive();

    public abstract boolean isEnumMember();

    public abstract boolean isEnumType();

    public abstract String fieldOrMethodBaseType();

    public abstract int countMethodParameters();

    public abstract int getStartPos();
}
