package org.apache.commons.logging;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.PrivilegedAction;
import java.util.Properties;
import org.apache.commons.logging.LogFactory;

static final class LogFactory.5
implements PrivilegedAction {
    private final /* synthetic */ URL val$url;

    LogFactory.5(URL uRL) {
        this.val$url = uRL;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object run() {
        InputStream stream = null;
        try {
            URLConnection connection = this.val$url.openConnection();
            connection.setUseCaches(false);
            stream = connection.getInputStream();
            if (stream != null) {
                Properties props = new Properties();
                props.load(stream);
                stream.close();
                stream = null;
                Properties properties = props;
                return properties;
            }
        }
        catch (IOException e) {
            if (LogFactory.isDiagnosticsEnabled()) {
                LogFactory.logDiagnostic("Unable to read URL " + this.val$url);
            }
        }
        finally {
            block17: {
                if (stream != null) {
                    try {
                        stream.close();
                    }
                    catch (IOException e) {
                        if (!LogFactory.isDiagnosticsEnabled()) break block17;
                        LogFactory.logDiagnostic("Unable to close stream for URL " + this.val$url);
                    }
                }
            }
        }
        return null;
    }
}
