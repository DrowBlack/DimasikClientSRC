package lombok.javac;

import com.sun.source.tree.LabeledStatementTree;
import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeCopier;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import lombok.javac.Javac;
import lombok.javac.JavacTreeMaker;

public class TreeMirrorMaker
extends TreeCopier<Void> {
    private final IdentityHashMap<JCTree, JCTree> originalToCopy = new IdentityHashMap();

    public TreeMirrorMaker(JavacTreeMaker maker, Context context) {
        super(maker.getUnderlyingTreeMaker());
    }

    @Override
    public <T extends JCTree> T copy(T original) {
        T copy = super.copy(original);
        this.putIfAbsent(this.originalToCopy, original, copy);
        return copy;
    }

    @Override
    public <T extends JCTree> T copy(T original, Void p) {
        T copy = super.copy(original, p);
        this.putIfAbsent(this.originalToCopy, original, copy);
        return copy;
    }

    @Override
    public <T extends JCTree> List<T> copy(List<T> originals) {
        List<T> copies = super.copy(originals);
        if (originals != null) {
            Iterator<T> it1 = originals.iterator();
            Iterator<T> it2 = copies.iterator();
            while (it1.hasNext()) {
                this.putIfAbsent(this.originalToCopy, (JCTree)it1.next(), (JCTree)it2.next());
            }
        }
        return copies;
    }

    @Override
    public <T extends JCTree> List<T> copy(List<T> originals, Void p) {
        List<T> copies = super.copy(originals, p);
        if (originals != null) {
            Iterator<T> it1 = originals.iterator();
            Iterator<T> it2 = copies.iterator();
            while (it1.hasNext()) {
                this.putIfAbsent(this.originalToCopy, (JCTree)it1.next(), (JCTree)it2.next());
            }
        }
        return copies;
    }

    public Map<JCTree, JCTree> getOriginalToCopyMap() {
        return Collections.unmodifiableMap(this.originalToCopy);
    }

    @Override
    public JCTree visitVariable(VariableTree node, Void p) {
        JCTree.JCVariableDecl original = node instanceof JCTree.JCVariableDecl ? (JCTree.JCVariableDecl)node : null;
        JCTree.JCVariableDecl copy = (JCTree.JCVariableDecl)super.visitVariable(node, p);
        if (original == null) {
            return copy;
        }
        copy.sym = original.sym;
        if (copy.sym != null) {
            copy.type = original.type;
        }
        if (copy.type != null) {
            boolean wipeSymAndType = copy.type.isErroneous();
            if (!wipeSymAndType) {
                JavacTreeMaker.TypeTag typeTag = JavacTreeMaker.TypeTag.typeTag(copy.type);
                boolean bl = wipeSymAndType = Javac.CTC_NONE.equals(typeTag) || Javac.CTC_ERROR.equals(typeTag) || Javac.CTC_UNKNOWN.equals(typeTag) || Javac.CTC_UNDETVAR.equals(typeTag);
            }
            if (wipeSymAndType) {
                copy.sym = null;
                copy.type = null;
            } else if (original.vartype != null) {
                copy.vartype.type = original.vartype.type;
                original.vartype.accept(new TreeScanner(){

                    @Override
                    public void scan(JCTree tree) {
                        super.scan(tree);
                        ((JCTree)((TreeMirrorMaker)TreeMirrorMaker.this).originalToCopy.get((Object)tree)).type = tree.type;
                    }

                    @Override
                    public void visitSelect(JCTree.JCFieldAccess tree) {
                        super.visitSelect(tree);
                        ((JCTree.JCFieldAccess)((TreeMirrorMaker)TreeMirrorMaker.this).originalToCopy.get((Object)tree)).sym = tree.sym;
                    }
                });
            }
        }
        return copy;
    }

    @Override
    public JCTree visitLabeledStatement(LabeledStatementTree node, Void p) {
        return node.getStatement().accept(this, p);
    }

    private <K, V> void putIfAbsent(Map<K, V> map, K key, V value) {
        if (!map.containsKey(key)) {
            map.put(key, value);
        }
    }
}
