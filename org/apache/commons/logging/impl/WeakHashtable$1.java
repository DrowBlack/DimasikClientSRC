package org.apache.commons.logging.impl;

import java.util.Enumeration;
import org.apache.commons.logging.impl.WeakHashtable;

class WeakHashtable.1
implements Enumeration {
    private final /* synthetic */ Enumeration val$enumer;

    WeakHashtable.1(Enumeration enumeration) {
        this.val$enumer = enumeration;
    }

    public boolean hasMoreElements() {
        return this.val$enumer.hasMoreElements();
    }

    public Object nextElement() {
        WeakHashtable.Referenced nextReference = (WeakHashtable.Referenced)this.val$enumer.nextElement();
        return nextReference.getValue();
    }
}
