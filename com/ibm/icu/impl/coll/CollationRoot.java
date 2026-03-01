package com.ibm.icu.impl.coll;

import com.ibm.icu.impl.ICUBinary;
import com.ibm.icu.impl.coll.CollationData;
import com.ibm.icu.impl.coll.CollationDataReader;
import com.ibm.icu.impl.coll.CollationSettings;
import com.ibm.icu.impl.coll.CollationTailoring;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.MissingResourceException;

public final class CollationRoot {
    private static final CollationTailoring rootSingleton;
    private static final RuntimeException exception;

    public static final CollationTailoring getRoot() {
        if (exception != null) {
            throw exception;
        }
        return rootSingleton;
    }

    public static final CollationData getData() {
        CollationTailoring root = CollationRoot.getRoot();
        return root.data;
    }

    static final CollationSettings getSettings() {
        CollationTailoring root = CollationRoot.getRoot();
        return root.settings.readOnly();
    }

    static {
        CollationTailoring t = null;
        RuntimeException e2 = null;
        try {
            ByteBuffer bytes = ICUBinary.getRequiredData("coll/ucadata.icu");
            CollationTailoring t2 = new CollationTailoring(null);
            CollationDataReader.read(null, bytes, t2);
            t = t2;
        }
        catch (IOException e) {
            e2 = new MissingResourceException("IOException while reading CLDR root data", "CollationRoot", "data/icudt66b/coll/ucadata.icu");
        }
        catch (RuntimeException e) {
            e2 = e;
        }
        rootSingleton = t;
        exception = e2;
    }
}
