package lombok.core;

import lombok.core.FieldAugment;

public final class Augments {
    public static final FieldAugment<ClassLoader, Boolean> ClassLoader_lombokAlreadyAddedTo = FieldAugment.augment(ClassLoader.class, Boolean.TYPE, "lombok$alreadyAddedTo");

    private Augments() {
    }
}
