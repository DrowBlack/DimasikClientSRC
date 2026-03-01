package org.codehaus.plexus.util.xml;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.XMLWriter;

public class PrettyPrintXMLWriter
implements XMLWriter {
    protected static final String LS = System.getProperty("line.separator");
    private PrintWriter writer;
    private LinkedList<String> elementStack = new LinkedList();
    private boolean tagInProgress;
    private int depth;
    private String lineIndenter;
    private String lineSeparator;
    private String encoding;
    private String docType;
    private boolean readyForNewLine;
    private boolean tagIsEmpty;
    private static final Pattern amp = Pattern.compile("&");
    private static final Pattern lt = Pattern.compile("<");
    private static final Pattern gt = Pattern.compile(">");
    private static final Pattern dqoute = Pattern.compile("\"");
    private static final Pattern sqoute = Pattern.compile("'");
    private static final String crlf_str = "\r\n";
    private static final Pattern crlf = Pattern.compile("\r\n");
    private static final Pattern lowers = Pattern.compile("([\u0000-\u001f])");

    public PrettyPrintXMLWriter(PrintWriter writer, String lineIndenter) {
        this(writer, lineIndenter, null, null);
    }

    public PrettyPrintXMLWriter(Writer writer, String lineIndenter) {
        this(new PrintWriter(writer), lineIndenter);
    }

    public PrettyPrintXMLWriter(PrintWriter writer) {
        this(writer, null, null);
    }

    public PrettyPrintXMLWriter(Writer writer) {
        this(new PrintWriter(writer));
    }

    public PrettyPrintXMLWriter(PrintWriter writer, String lineIndenter, String encoding, String doctype) {
        this(writer, lineIndenter, LS, encoding, doctype);
    }

    public PrettyPrintXMLWriter(Writer writer, String lineIndenter, String encoding, String doctype) {
        this(new PrintWriter(writer), lineIndenter, encoding, doctype);
    }

    public PrettyPrintXMLWriter(PrintWriter writer, String encoding, String doctype) {
        this(writer, "  ", encoding, doctype);
    }

    public PrettyPrintXMLWriter(Writer writer, String encoding, String doctype) {
        this(new PrintWriter(writer), encoding, doctype);
    }

    public PrettyPrintXMLWriter(PrintWriter writer, String lineIndenter, String lineSeparator, String encoding, String doctype) {
        this.setWriter(writer);
        this.setLineIndenter(lineIndenter);
        this.setLineSeparator(lineSeparator);
        this.setEncoding(encoding);
        this.setDocType(doctype);
        if (doctype != null || encoding != null) {
            this.writeDocumentHeaders();
        }
    }

    @Override
    public void startElement(String name) {
        this.tagIsEmpty = false;
        this.finishTag();
        this.write("<");
        this.write(name);
        this.elementStack.addLast(name);
        this.tagInProgress = true;
        this.setDepth(this.getDepth() + 1);
        this.readyForNewLine = true;
        this.tagIsEmpty = true;
    }

    @Override
    public void writeText(String text) {
        this.writeText(text, true);
    }

    @Override
    public void writeMarkup(String text) {
        this.writeText(text, false);
    }

    private void writeText(String text, boolean escapeXml) {
        this.readyForNewLine = false;
        this.tagIsEmpty = false;
        this.finishTag();
        if (escapeXml) {
            text = PrettyPrintXMLWriter.escapeXml(text);
        }
        this.write(StringUtils.unifyLineSeparators(text, this.lineSeparator));
    }

    private static String escapeXml(String text) {
        if (text.indexOf(38) >= 0) {
            text = amp.matcher(text).replaceAll("&amp;");
        }
        if (text.indexOf(60) >= 0) {
            text = lt.matcher(text).replaceAll("&lt;");
        }
        if (text.indexOf(62) >= 0) {
            text = gt.matcher(text).replaceAll("&gt;");
        }
        if (text.indexOf(34) >= 0) {
            text = dqoute.matcher(text).replaceAll("&quot;");
        }
        if (text.indexOf(39) >= 0) {
            text = sqoute.matcher(text).replaceAll("&apos;");
        }
        return text;
    }

    private static String escapeXmlAttribute(String text) {
        text = PrettyPrintXMLWriter.escapeXml(text);
        Matcher crlfmatcher = crlf.matcher(text);
        if (text.contains(crlf_str)) {
            text = crlfmatcher.replaceAll("&#10;");
        }
        Matcher m = lowers.matcher(text);
        StringBuffer b = new StringBuffer();
        while (m.find()) {
            m = m.appendReplacement(b, "&#" + Integer.toString(m.group(1).charAt(0)) + ";");
        }
        m.appendTail(b);
        return b.toString();
    }

    @Override
    public void addAttribute(String key, String value) {
        this.write(" ");
        this.write(key);
        this.write("=\"");
        this.write(PrettyPrintXMLWriter.escapeXmlAttribute(value));
        this.write("\"");
    }

    @Override
    public void endElement() {
        this.setDepth(this.getDepth() - 1);
        if (this.tagIsEmpty) {
            this.write("/");
            this.readyForNewLine = false;
            this.finishTag();
            this.elementStack.removeLast();
        } else {
            this.finishTag();
            this.write("</");
            this.write(this.elementStack.removeLast());
            this.write(">");
        }
        this.readyForNewLine = true;
    }

    private void write(String str) {
        this.getWriter().write(str);
    }

    private void finishTag() {
        if (this.tagInProgress) {
            this.write(">");
        }
        this.tagInProgress = false;
        if (this.readyForNewLine) {
            this.endOfLine();
        }
        this.readyForNewLine = false;
        this.tagIsEmpty = false;
    }

    protected String getLineIndenter() {
        return this.lineIndenter;
    }

    protected void setLineIndenter(String lineIndenter) {
        this.lineIndenter = lineIndenter;
    }

    protected String getLineSeparator() {
        return this.lineSeparator;
    }

    protected void setLineSeparator(String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }

    protected void endOfLine() {
        this.write(this.getLineSeparator());
        for (int i = 0; i < this.getDepth(); ++i) {
            this.write(this.getLineIndenter());
        }
    }

    private void writeDocumentHeaders() {
        this.write("<?xml version=\"1.0\"");
        if (this.getEncoding() != null) {
            this.write(" encoding=\"" + this.getEncoding() + "\"");
        }
        this.write("?>");
        this.endOfLine();
        if (this.getDocType() != null) {
            this.write("<!DOCTYPE ");
            this.write(this.getDocType());
            this.write(">");
            this.endOfLine();
        }
    }

    protected void setWriter(PrintWriter writer) {
        if (writer == null) {
            throw new IllegalArgumentException("writer could not be null");
        }
        this.writer = writer;
    }

    protected PrintWriter getWriter() {
        return this.writer;
    }

    protected void setDepth(int depth) {
        this.depth = depth;
    }

    protected int getDepth() {
        return this.depth;
    }

    protected void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    protected String getEncoding() {
        return this.encoding;
    }

    protected void setDocType(String docType) {
        this.docType = docType;
    }

    protected String getDocType() {
        return this.docType;
    }

    protected LinkedList<String> getElementStack() {
        return this.elementStack;
    }
}
