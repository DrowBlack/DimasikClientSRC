package com.ibm.icu.impl;

import com.ibm.icu.impl.ICUData;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.MissingResourceException;
import java.util.Properties;

public class ICUConfig {
    public static final String CONFIG_PROPS_FILE = "/com/ibm/icu/ICUConfig.properties";
    private static final Properties CONFIG_PROPS;

    public static String get(String name) {
        return ICUConfig.get(name, null);
    }

    public static String get(String name, String def) {
        String val = null;
        final String fname = name;
        if (System.getSecurityManager() != null) {
            try {
                val = AccessController.doPrivileged(new PrivilegedAction<String>(){

                    @Override
                    public String run() {
                        return System.getProperty(fname);
                    }
                });
            }
            catch (AccessControlException accessControlException) {}
        } else {
            val = System.getProperty(name);
        }
        if (val == null) {
            val = CONFIG_PROPS.getProperty(name, def);
        }
        return val;
    }

    static {
        block6: {
            CONFIG_PROPS = new Properties();
            try {
                InputStream is = ICUData.getStream(CONFIG_PROPS_FILE);
                if (is == null) break block6;
                try {
                    CONFIG_PROPS.load(is);
                }
                finally {
                    is.close();
                }
            }
            catch (MissingResourceException missingResourceException) {
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }
}
