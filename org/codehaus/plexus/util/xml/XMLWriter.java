package org.codehaus.plexus.util.xml;

public interface XMLWriter {
    public void startElement(String var1);

    public void addAttribute(String var1, String var2);

    public void writeText(String var1);

    public void writeMarkup(String var1);

    public void endElement();
}
