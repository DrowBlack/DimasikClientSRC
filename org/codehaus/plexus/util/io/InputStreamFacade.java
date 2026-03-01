package org.codehaus.plexus.util.io;

import java.io.IOException;
import java.io.InputStream;

public interface InputStreamFacade {
    public InputStream getInputStream() throws IOException;
}
