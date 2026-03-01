package org.codehaus.plexus.util.xml;

import java.io.PrintWriter;
import java.io.Writer;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;
import org.codehaus.plexus.util.xml.XMLWriter;
import org.codehaus.plexus.util.xml.Xpp3Dom;

public class Xpp3DomWriter {
    public static void write(Writer writer, Xpp3Dom dom) {
        Xpp3DomWriter.write(new PrettyPrintXMLWriter(writer), dom);
    }

    public static void write(PrintWriter writer, Xpp3Dom dom) {
        Xpp3DomWriter.write(new PrettyPrintXMLWriter(writer), dom);
    }

    public static void write(XMLWriter xmlWriter, Xpp3Dom dom) {
        Xpp3DomWriter.write(xmlWriter, dom, true);
    }

    public static void write(XMLWriter xmlWriter, Xpp3Dom dom, boolean escape) {
        Xpp3Dom[] children;
        String[] attributeNames;
        xmlWriter.startElement(dom.getName());
        for (String attributeName : attributeNames = dom.getAttributeNames()) {
            xmlWriter.addAttribute(attributeName, dom.getAttribute(attributeName));
        }
        for (Xpp3Dom aChildren : children = dom.getChildren()) {
            Xpp3DomWriter.write(xmlWriter, aChildren, escape);
        }
        String value = dom.getValue();
        if (value != null) {
            if (escape) {
                xmlWriter.writeText(value);
            } else {
                xmlWriter.writeMarkup(value);
            }
        }
        xmlWriter.endElement();
    }
}
