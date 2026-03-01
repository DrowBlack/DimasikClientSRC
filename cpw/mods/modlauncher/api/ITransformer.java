package cpw.mods.modlauncher.api;

import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;

public interface ITransformer<T> {
    public static final String[] DEFAULT_LABEL = new String[]{"default"};

    @Nonnull
    public T transform(T var1, ITransformerVotingContext var2);

    @Nonnull
    public TransformerVoteResult castVote(ITransformerVotingContext var1);

    @Nonnull
    public Set<Target> targets();

    default public String[] labels() {
        return DEFAULT_LABEL;
    }

    public static final class Target {
        private final String className;
        private final String elementName;
        private final String elementDescriptor;
        private final TargetType targetType;

        Target(String className, String elementName, String elementDescriptor, TargetType targetType) {
            Objects.requireNonNull(className, "Class Name cannot be null");
            Objects.requireNonNull(elementName, "Element Name cannot be null");
            Objects.requireNonNull(elementDescriptor, "Element Descriptor cannot be null");
            Objects.requireNonNull(targetType, "Target Type cannot be null");
            this.className = className;
            this.elementName = elementName;
            this.elementDescriptor = elementDescriptor;
            this.targetType = targetType;
        }

        @Nonnull
        public static Target targetClass(String className) {
            return new Target(className, "", "", TargetType.CLASS);
        }

        @Nonnull
        public static Target targetMethod(String className, String methodName, String methodDescriptor) {
            return new Target(className, methodName, methodDescriptor, TargetType.METHOD);
        }

        @Nonnull
        public static Target targetField(String className, String fieldName) {
            return new Target(className, fieldName, "", TargetType.FIELD);
        }

        public String getClassName() {
            return this.className;
        }

        public String getElementName() {
            return this.elementName;
        }

        public String getElementDescriptor() {
            return this.elementDescriptor;
        }

        public TargetType getTargetType() {
            return this.targetType;
        }
    }

    public static enum TargetType {
        CLASS,
        METHOD,
        FIELD;

    }
}
