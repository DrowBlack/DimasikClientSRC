package org.openjdk.nashorn.api.tree;

import java.util.List;
import org.openjdk.nashorn.api.tree.CompilationUnitTree;
import org.openjdk.nashorn.api.tree.LineMap;
import org.openjdk.nashorn.api.tree.LineMapImpl;
import org.openjdk.nashorn.api.tree.ModuleTree;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeImpl;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.FunctionNode;

final class CompilationUnitTreeImpl
extends TreeImpl
implements CompilationUnitTree {
    private final FunctionNode funcNode;
    private final List<? extends Tree> elements;
    private final ModuleTree module;

    CompilationUnitTreeImpl(FunctionNode node, List<? extends Tree> elements, ModuleTree module) {
        super(node);
        this.funcNode = node;
        assert (this.funcNode.getKind() == FunctionNode.Kind.SCRIPT || this.funcNode.getKind() == FunctionNode.Kind.MODULE) : "script or module function expected";
        this.elements = elements;
        this.module = module;
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.COMPILATION_UNIT;
    }

    @Override
    public List<? extends Tree> getSourceElements() {
        return this.elements;
    }

    @Override
    public String getSourceName() {
        return this.funcNode.getSourceName();
    }

    @Override
    public boolean isStrict() {
        return this.funcNode.isStrict();
    }

    @Override
    public LineMap getLineMap() {
        return new LineMapImpl(this.funcNode.getSource());
    }

    @Override
    public ModuleTree getModule() {
        return this.module;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitCompilationUnit(this, data);
    }
}
