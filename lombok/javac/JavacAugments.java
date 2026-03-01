package lombok.javac;

import com.sun.tools.javac.tree.JCTree;
import lombok.core.FieldAugment;

public final class JavacAugments {
    public static final FieldAugment<JCTree, Boolean> JCTree_handled = FieldAugment.augment(JCTree.class, Boolean.TYPE, "lombok$handled");
    public static final FieldAugment<JCTree, JCTree> JCTree_generatedNode = FieldAugment.circularSafeAugment(JCTree.class, JCTree.class, "lombok$generatedNode");
    public static final FieldAugment<JCTree.JCImport, Boolean> JCImport_deletable = FieldAugment.circularSafeAugment(JCTree.JCImport.class, Boolean.class, "lombok$deletable");
    public static final FieldAugment<JCTree, Boolean> JCTree_keepPosition = FieldAugment.augment(JCTree.class, Boolean.TYPE, "lombok$keepPosition");

    private JavacAugments() {
    }
}
