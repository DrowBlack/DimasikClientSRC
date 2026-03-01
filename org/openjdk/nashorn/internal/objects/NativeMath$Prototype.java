package org.openjdk.nashorn.internal.objects;

import org.openjdk.nashorn.internal.runtime.PrototypeObject;

final class NativeMath$Prototype
extends PrototypeObject {
    NativeMath$Prototype() {
    }

    @Override
    public String getClassName() {
        return "Math";
    }
}
