package org.openjdk.nashorn.internal.runtime;

import java.io.Serializable;
import org.openjdk.nashorn.internal.objects.NativeSymbol;

public final class Symbol
implements Serializable {
    private final String name;
    private static final long serialVersionUID = -2988436597549486913L;

    public Symbol(String name) {
        this.name = name;
    }

    public String toString() {
        return "Symbol(" + this.name + ")";
    }

    public final String getName() {
        return this.name;
    }

    private Object writeReplace() {
        return NativeSymbol.keyFor(null, this) == this.name ? new GlobalSymbol(this.name) : this;
    }

    private static class GlobalSymbol
    implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String name;

        GlobalSymbol(String name) {
            this.name = name;
        }

        private Object readResolve() {
            return NativeSymbol._for(null, this.name);
        }
    }
}
