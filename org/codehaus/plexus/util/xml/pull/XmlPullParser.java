package org.codehaus.plexus.util.xml.pull;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public interface XmlPullParser {
    public static final String NO_NAMESPACE = "";
    public static final int START_DOCUMENT = 0;
    public static final int END_DOCUMENT = 1;
    public static final int START_TAG = 2;
    public static final int END_TAG = 3;
    public static final int TEXT = 4;
    public static final int CDSECT = 5;
    public static final int ENTITY_REF = 6;
    public static final int IGNORABLE_WHITESPACE = 7;
    public static final int PROCESSING_INSTRUCTION = 8;
    public static final int COMMENT = 9;
    public static final int DOCDECL = 10;
    public static final String[] TYPES = new String[]{"START_DOCUMENT", "END_DOCUMENT", "START_TAG", "END_TAG", "TEXT", "CDSECT", "ENTITY_REF", "IGNORABLE_WHITESPACE", "PROCESSING_INSTRUCTION", "COMMENT", "DOCDECL"};
    public static final String FEATURE_PROCESS_NAMESPACES = "http://xmlpull.org/v1/doc/features.html#process-namespaces";
    public static final String FEATURE_REPORT_NAMESPACE_ATTRIBUTES = "http://xmlpull.org/v1/doc/features.html#report-namespace-prefixes";
    public static final String FEATURE_PROCESS_DOCDECL = "http://xmlpull.org/v1/doc/features.html#process-docdecl";
    public static final String FEATURE_VALIDATION = "http://xmlpull.org/v1/doc/features.html#validation";

    public void setFeature(String var1, boolean var2) throws XmlPullParserException;

    public boolean getFeature(String var1);

    public void setProperty(String var1, Object var2) throws XmlPullParserException;

    public Object getProperty(String var1);

    public void setInput(Reader var1) throws XmlPullParserException;

    public void setInput(InputStream var1, String var2) throws XmlPullParserException;

    public String getInputEncoding();

    public void defineEntityReplacementText(String var1, String var2) throws XmlPullParserException;

    public int getNamespaceCount(int var1) throws XmlPullParserException;

    public String getNamespacePrefix(int var1) throws XmlPullParserException;

    public String getNamespaceUri(int var1) throws XmlPullParserException;

    public String getNamespace(String var1);

    public int getDepth();

    public String getPositionDescription();

    public int getLineNumber();

    public int getColumnNumber();

    public boolean isWhitespace() throws XmlPullParserException;

    public String getText();

    public char[] getTextCharacters(int[] var1);

    public String getNamespace();

    public String getName();

    public String getPrefix();

    public boolean isEmptyElementTag() throws XmlPullParserException;

    public int getAttributeCount();

    public String getAttributeNamespace(int var1);

    public String getAttributeName(int var1);

    public String getAttributePrefix(int var1);

    public String getAttributeType(int var1);

    public boolean isAttributeDefault(int var1);

    public String getAttributeValue(int var1);

    public String getAttributeValue(String var1, String var2);

    public int getEventType() throws XmlPullParserException;

    public int next() throws XmlPullParserException, IOException;

    public int nextToken() throws XmlPullParserException, IOException;

    public void require(int var1, String var2, String var3) throws XmlPullParserException, IOException;

    public String nextText() throws XmlPullParserException, IOException;

    public int nextTag() throws XmlPullParserException, IOException;
}
