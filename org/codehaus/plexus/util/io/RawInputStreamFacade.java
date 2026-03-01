package org.codehaus.plexus.util.io;

import java.io.IOException;
import java.io.InputStream;
import org.codehaus.plexus.util.io.InputStreamFacade;

public class RawInputStreamFacade
implements InputStreamFacade {
    final InputStream stream;

    public RawInputStreamFacade(InputStream stream) {
        this.stream = stream;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.stream;
    }
}
