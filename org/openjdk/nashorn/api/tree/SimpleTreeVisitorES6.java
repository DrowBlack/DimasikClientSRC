package org.openjdk.nashorn.api.tree;

import java.util.List;
import org.openjdk.nashorn.api.tree.ClassDeclarationTree;
import org.openjdk.nashorn.api.tree.ClassExpressionTree;
import org.openjdk.nashorn.api.tree.CompilationUnitTree;
import org.openjdk.nashorn.api.tree.ExportEntryTree;
import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.ForOfLoopTree;
import org.openjdk.nashorn.api.tree.ImportEntryTree;
import org.openjdk.nashorn.api.tree.ModuleTree;
import org.openjdk.nashorn.api.tree.PropertyTree;
import org.openjdk.nashorn.api.tree.SimpleTreeVisitorES5_1;
import org.openjdk.nashorn.api.tree.SpreadTree;
import org.openjdk.nashorn.api.tree.StatementTree;
import org.openjdk.nashorn.api.tree.TemplateLiteralTree;
import org.openjdk.nashorn.api.tree.VariableTree;
import org.openjdk.nashorn.api.tree.YieldTree;

public class SimpleTreeVisitorES6<R, P>
extends SimpleTreeVisitorES5_1<R, P> {
    @Override
    public R visitCompilationUnit(CompilationUnitTree node, P r) {
        ModuleTree mod = node.getModule();
        if (mod != null) {
            mod.accept(this, r);
        }
        return super.visitCompilationUnit(node, r);
    }

    @Override
    public R visitModule(ModuleTree node, P p) {
        node.getImportEntries().forEach(e -> this.visitImportEntry((ImportEntryTree)e, p));
        node.getLocalExportEntries().forEach(e -> this.visitExportEntry((ExportEntryTree)e, p));
        node.getIndirectExportEntries().forEach(e -> this.visitExportEntry((ExportEntryTree)e, p));
        node.getStarExportEntries().forEach(e -> this.visitExportEntry((ExportEntryTree)e, p));
        return null;
    }

    @Override
    public R visitExportEntry(ExportEntryTree node, P p) {
        return null;
    }

    @Override
    public R visitImportEntry(ImportEntryTree node, P p) {
        return null;
    }

    @Override
    public R visitClassDeclaration(ClassDeclarationTree node, P p) {
        List<? extends PropertyTree> elements;
        PropertyTree constructor;
        node.getName().accept(this, p);
        ExpressionTree heritage = node.getClassHeritage();
        if (heritage != null) {
            heritage.accept(this, p);
        }
        if ((constructor = node.getConstructor()) != null) {
            constructor.accept(this, p);
        }
        if ((elements = node.getClassElements()) != null) {
            for (PropertyTree propertyTree : elements) {
                propertyTree.accept(this, p);
            }
        }
        return null;
    }

    @Override
    public R visitClassExpression(ClassExpressionTree node, P p) {
        List<? extends PropertyTree> elements;
        PropertyTree constructor;
        node.getName().accept(this, p);
        ExpressionTree heritage = node.getClassHeritage();
        if (heritage != null) {
            heritage.accept(this, p);
        }
        if ((constructor = node.getConstructor()) != null) {
            constructor.accept(this, p);
        }
        if ((elements = node.getClassElements()) != null) {
            for (PropertyTree propertyTree : elements) {
                propertyTree.accept(this, p);
            }
        }
        return null;
    }

    @Override
    public R visitForOfLoop(ForOfLoopTree node, P p) {
        node.getVariable().accept(this, p);
        node.getExpression().accept(this, p);
        StatementTree stat = node.getStatement();
        if (stat != null) {
            stat.accept(this, p);
        }
        return null;
    }

    @Override
    public R visitYield(YieldTree node, P p) {
        node.getExpression().accept(this, p);
        return null;
    }

    @Override
    public R visitSpread(SpreadTree node, P p) {
        node.getExpression().accept(this, p);
        return null;
    }

    @Override
    public R visitTemplateLiteral(TemplateLiteralTree node, P p) {
        List<? extends ExpressionTree> expressions = node.getExpressions();
        for (ExpressionTree expressionTree : expressions) {
            expressionTree.accept(this, p);
        }
        return null;
    }

    @Override
    public R visitVariable(VariableTree node, P r) {
        ExpressionTree expr = node.getBinding();
        if (expr != null) {
            expr.accept(this, r);
        }
        super.visitVariable(node, r);
        return null;
    }
}
