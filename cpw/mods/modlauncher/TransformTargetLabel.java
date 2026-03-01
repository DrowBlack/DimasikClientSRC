package cpw.mods.modlauncher;

import cpw.mods.modlauncher.TransformList;
import cpw.mods.modlauncher.api.ITransformer;
import java.util.EnumMap;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public final class TransformTargetLabel {
    private final Type className;
    private final String elementName;
    private final Type elementDescriptor;
    private final LabelType labelType;

    TransformTargetLabel(ITransformer.Target target) {
        this(target.getClassName(), target.getElementName(), target.getElementDescriptor(), LabelType.valueOf(target.getTargetType().name()));
    }

    private TransformTargetLabel(String className, String elementName, String elementDescriptor, LabelType labelType) {
        this.className = Type.getObjectType(className.replace('.', '/'));
        this.elementName = elementName;
        this.elementDescriptor = elementDescriptor.length() > 0 ? Type.getMethodType(elementDescriptor) : Type.VOID_TYPE;
        this.labelType = labelType;
    }

    public TransformTargetLabel(String className, String fieldName) {
        this(className, fieldName, "", LabelType.FIELD);
    }

    TransformTargetLabel(String className, String methodName, String methodDesc) {
        this(className, methodName, methodDesc, LabelType.METHOD);
    }

    public TransformTargetLabel(String className) {
        this(className, "", "", LabelType.CLASS);
    }

    final Type getClassName() {
        return this.className;
    }

    public final String getElementName() {
        return this.elementName;
    }

    public final Type getElementDescriptor() {
        return this.elementDescriptor;
    }

    final LabelType getLabelType() {
        return this.labelType;
    }

    public int hashCode() {
        return Objects.hash(this.className, this.elementName, this.elementDescriptor);
    }

    public boolean equals(Object obj) {
        try {
            TransformTargetLabel tl = (TransformTargetLabel)obj;
            return Objects.equals(this.className, tl.className) && Objects.equals(this.elementName, tl.elementName) && Objects.equals(this.elementDescriptor, tl.elementDescriptor);
        }
        catch (ClassCastException cce) {
            return false;
        }
    }

    public String toString() {
        return "Target : " + Objects.toString((Object)this.labelType) + " {" + Objects.toString(this.className) + "} {" + Objects.toString(this.elementName) + "} {" + Objects.toString(this.elementDescriptor) + "}";
    }

    public static enum LabelType {
        FIELD(FieldNode.class),
        METHOD(MethodNode.class),
        CLASS(ClassNode.class);

        private final Class<?> nodeType;

        private LabelType(Class<?> nodeType) {
            this.nodeType = nodeType;
        }

        public static Optional<LabelType> getTypeFor(java.lang.reflect.Type type) {
            for (LabelType t : LabelType.values()) {
                if (!t.nodeType.getName().equals(type.getTypeName())) continue;
                return Optional.of(t);
            }
            return Optional.empty();
        }

        public Class<?> getNodeType() {
            return this.nodeType;
        }

        public <V> TransformList<V> getFromMap(EnumMap<LabelType, TransformList<?>> transformers) {
            return this.get(transformers, this.nodeType);
        }

        private <V> TransformList<V> get(EnumMap<LabelType, TransformList<?>> transformers, Class<V> type) {
            return transformers.get((Object)this);
        }

        public <T> Supplier<TransformList<T>> mapSupplier(EnumMap<LabelType, TransformList<?>> transformers) {
            return () -> (TransformList)transformers.get((Object)this);
        }
    }
}
