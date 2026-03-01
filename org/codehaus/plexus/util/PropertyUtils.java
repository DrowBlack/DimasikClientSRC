package org.codehaus.plexus.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import org.codehaus.plexus.util.IOUtil;

public class PropertyUtils {
    public static Properties loadProperties(URL url) throws IOException {
        if (url == null) {
            throw new NullPointerException("url");
        }
        return PropertyUtils.loadProperties(url.openStream());
    }

    public static Properties loadProperties(File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("file");
        }
        return PropertyUtils.loadProperties(new FileInputStream(file));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Properties loadProperties(InputStream is) throws IOException {
        InputStream in = is;
        try {
            Properties properties = new Properties();
            if (in != null) {
                properties.load(in);
                in.close();
                in = null;
            }
            Properties properties2 = properties;
            return properties2;
        }
        finally {
            IOUtil.close(in);
        }
    }
}
