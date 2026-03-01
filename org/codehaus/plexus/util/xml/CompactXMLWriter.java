package org.codehaus.plexus.util.xml;

import java.io.PrintWriter;
import java.io.Writer;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;

public class CompactXMLWriter
extends PrettyPrintXMLWriter {
    public CompactXMLWriter(PrintWriter writer) {
        super(writer);
    }

    public CompactXMLWriter(Writer writer) {
        super(writer);
    }

    @Override
    protected void endOfLine() {
    }
}
