package com.ibm.icu.impl;

import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.MissingResourceException;
import java.util.logging.Logger;

public final class ICUData {
    static final String ICU_DATA_PATH = "com/ibm/icu/impl/";
    static final String PACKAGE_NAME = "icudt66b";
    public static final String ICU_BUNDLE = "data/icudt66b";
    public static final String ICU_BASE_NAME = "com/ibm/icu/impl/data/icudt66b";
    public static final String ICU_COLLATION_BASE_NAME = "com/ibm/icu/impl/data/icudt66b/coll";
    public static final String ICU_BRKITR_NAME = "brkitr";
    public static final String ICU_BRKITR_BASE_NAME = "com/ibm/icu/impl/data/icudt66b/brkitr";
    public static final String ICU_RBNF_BASE_NAME = "com/ibm/icu/impl/data/icudt66b/rbnf";
    public static final String ICU_TRANSLIT_BASE_NAME = "com/ibm/icu/impl/data/icudt66b/translit";
    public static final String ICU_LANG_BASE_NAME = "com/ibm/icu/impl/data/icudt66b/lang";
    public static final String ICU_CURR_BASE_NAME = "com/ibm/icu/impl/data/icudt66b/curr";
    public static final String ICU_REGION_BASE_NAME = "com/ibm/icu/impl/data/icudt66b/region";
    public static final String ICU_ZONE_BASE_NAME = "com/ibm/icu/impl/data/icudt66b/zone";
    public static final String ICU_UNIT_BASE_NAME = "com/ibm/icu/impl/data/icudt66b/unit";
    private static final boolean logBinaryDataFromInputStream = false;
    private static final Logger logger = null;

    public static boolean exists(final String resourceName) {
        URL i = null;
        i = System.getSecurityManager() != null ? AccessController.doPrivileged(new PrivilegedAction<URL>(){

            @Override
            public URL run() {
                return ICUData.class.getResource(resourceName);
            }
        }) : ICUData.class.getResource(resourceName);
        return i != null;
    }

    private static InputStream getStream(final Class<?> root, final String resourceName, boolean required) {
        InputStream i = null;
        i = System.getSecurityManager() != null ? AccessController.doPrivileged(new PrivilegedAction<InputStream>(){

            @Override
            public InputStream run() {
                return root.getResourceAsStream(resourceName);
            }
        }) : root.getResourceAsStream(resourceName);
        if (i == null && required) {
            throw new MissingResourceException("could not locate data " + resourceName, root.getPackage().getName(), resourceName);
        }
        ICUData.checkStreamForBinaryData(i, resourceName);
        return i;
    }

    static InputStream getStream(final ClassLoader loader, final String resourceName, boolean required) {
        InputStream i = null;
        i = System.getSecurityManager() != null ? AccessController.doPrivileged(new PrivilegedAction<InputStream>(){

            @Override
            public InputStream run() {
                return loader.getResourceAsStream(resourceName);
            }
        }) : loader.getResourceAsStream(resourceName);
        if (i == null && required) {
            throw new MissingResourceException("could not locate data", loader.toString(), resourceName);
        }
        ICUData.checkStreamForBinaryData(i, resourceName);
        return i;
    }

    private static void checkStreamForBinaryData(InputStream is, String resourceName) {
    }

    public static InputStream getStream(ClassLoader loader, String resourceName) {
        return ICUData.getStream(loader, resourceName, false);
    }

    public static InputStream getRequiredStream(ClassLoader loader, String resourceName) {
        return ICUData.getStream(loader, resourceName, true);
    }

    public static InputStream getStream(String resourceName) {
        return ICUData.getStream(ICUData.class, resourceName, false);
    }

    public static InputStream getRequiredStream(String resourceName) {
        return ICUData.getStream(ICUData.class, resourceName, true);
    }

    public static InputStream getStream(Class<?> root, String resourceName) {
        return ICUData.getStream(root, resourceName, false);
    }

    public static InputStream getRequiredStream(Class<?> root, String resourceName) {
        return ICUData.getStream(root, resourceName, true);
    }
}
