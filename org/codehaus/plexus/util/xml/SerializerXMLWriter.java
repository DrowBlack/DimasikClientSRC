package org.codehaus.plexus.util.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import org.codehaus.plexus.util.xml.XMLWriter;
import org.codehaus.plexus.util.xml.pull.XmlSerializer;

public class SerializerXMLWriter
implements XMLWriter {
    private final XmlSerializer serializer;
    private final String namespace;
    private final Stack<String> elements = new Stack();
    private List<Exception> exceptions;

    public SerializerXMLWriter(String namespace, XmlSerializer serializer) {
        this.serializer = serializer;
        this.namespace = namespace;
    }

    @Override
    public void startElement(String name) {
        try {
            this.serializer.startTag(this.namespace, name);
            this.elements.push(name);
        }
        catch (IOException e) {
            this.storeException(e);
        }
    }

    @Override
    public void addAttribute(String key, String value) {
        try {
            this.serializer.attribute(this.namespace, key, value);
        }
        catch (IOException e) {
            this.storeException(e);
        }
    }

    @Override
    public void writeText(String text) {
        try {
            this.serializer.text(text);
        }
        catch (IOException e) {
            this.storeException(e);
        }
    }

    @Override
    public void writeMarkup(String text) {
        try {
            this.serializer.cdsect(text);
        }
        catch (IOException e) {
            this.storeException(e);
        }
    }

    @Override
    public void endElement() {
        try {
            this.serializer.endTag(this.namespace, this.elements.pop());
        }
        catch (IOException e) {
            this.storeException(e);
        }
    }

    private void storeException(IOException e) {
        if (this.exceptions == null) {
            this.exceptions = new ArrayList<Exception>();
        }
        this.exceptions.add(e);
    }

    public List<Exception> getExceptions() {
        return this.exceptions == null ? Collections.emptyList() : this.exceptions;
    }
}
