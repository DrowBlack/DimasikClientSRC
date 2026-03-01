package org.codehaus.plexus.util.xml.pull;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.xml.pull.EntityReplacementMap;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class MXParser
implements XmlPullParser {
    protected static final String XML_URI = "http://www.w3.org/XML/1998/namespace";
    protected static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
    protected static final String FEATURE_XML_ROUNDTRIP = "http://xmlpull.org/v1/doc/features.html#xml-roundtrip";
    protected static final String FEATURE_NAMES_INTERNED = "http://xmlpull.org/v1/doc/features.html#names-interned";
    protected static final String PROPERTY_XMLDECL_VERSION = "http://xmlpull.org/v1/doc/properties.html#xmldecl-version";
    protected static final String PROPERTY_XMLDECL_STANDALONE = "http://xmlpull.org/v1/doc/properties.html#xmldecl-standalone";
    protected static final String PROPERTY_XMLDECL_CONTENT = "http://xmlpull.org/v1/doc/properties.html#xmldecl-content";
    protected static final String PROPERTY_LOCATION = "http://xmlpull.org/v1/doc/properties.html#location";
    protected boolean allStringsInterned;
    private static final boolean TRACE_SIZING = false;
    protected boolean processNamespaces;
    protected boolean roundtripSupported;
    protected String location;
    protected int lineNumber;
    protected int columnNumber;
    protected boolean seenRoot;
    protected boolean reachedEnd;
    protected int eventType;
    protected boolean emptyElementTag;
    protected int depth;
    protected char[][] elRawName;
    protected int[] elRawNameEnd;
    protected int[] elRawNameLine;
    protected String[] elName;
    protected String[] elPrefix;
    protected String[] elUri;
    protected int[] elNamespaceCount;
    protected int attributeCount;
    protected String[] attributeName;
    protected int[] attributeNameHash;
    protected String[] attributePrefix;
    protected String[] attributeUri;
    protected String[] attributeValue;
    protected int namespaceEnd;
    protected String[] namespacePrefix;
    protected int[] namespacePrefixHash;
    protected String[] namespaceUri;
    protected int entityEnd;
    protected String[] entityName;
    protected char[][] entityNameBuf;
    protected String[] entityReplacement;
    protected char[][] entityReplacementBuf;
    protected int[] entityNameHash;
    private final EntityReplacementMap replacementMapTemplate;
    protected static final int READ_CHUNK_SIZE = 8192;
    protected Reader reader;
    protected String inputEncoding;
    protected int bufLoadFactor = 95;
    protected char[] buf = new char[Runtime.getRuntime().freeMemory() > 1000000L ? 8192 : 256];
    protected int bufSoftLimit = this.bufLoadFactor * this.buf.length / 100;
    protected boolean preventBufferCompaction;
    protected int bufAbsoluteStart;
    protected int bufStart;
    protected int bufEnd;
    protected int pos;
    protected int posStart;
    protected int posEnd;
    protected char[] pc = new char[Runtime.getRuntime().freeMemory() > 1000000L ? 8192 : 64];
    protected int pcStart;
    protected int pcEnd;
    protected boolean usePC;
    protected boolean seenStartTag;
    protected boolean seenEndTag;
    protected boolean pastEndTag;
    protected boolean seenAmpersand;
    protected boolean seenMarkup;
    protected boolean seenDocdecl;
    protected boolean tokenize;
    protected String text;
    protected String entityRefName;
    protected String xmlDeclVersion;
    protected Boolean xmlDeclStandalone;
    protected String xmlDeclContent;
    protected char[] charRefOneCharBuf = new char[1];
    protected static final char[] VERSION;
    protected static final char[] NCODING;
    protected static final char[] TANDALONE;
    protected static final char[] YES;
    protected static final char[] NO;
    protected static final int LOOKUP_MAX = 1024;
    protected static final char LOOKUP_MAX_CHAR = '\u0400';
    protected static boolean[] lookupNameStartChar;
    protected static boolean[] lookupNameChar;
    private static final char MIN_HIGH_SURROGATE = '\ud800';
    private static final char MAX_HIGH_SURROGATE = '\udbff';
    private static final int MAX_CODE_POINT = 0x10FFFF;
    private static final int MIN_SUPPLEMENTARY_CODE_POINT = 65536;

    protected void resetStringCache() {
    }

    protected String newString(char[] cbuf, int off, int len) {
        return new String(cbuf, off, len);
    }

    protected String newStringIntern(char[] cbuf, int off, int len) {
        return new String(cbuf, off, len).intern();
    }

    protected void ensureElementsCapacity() {
        int elStackSize;
        int n = elStackSize = this.elName != null ? this.elName.length : 0;
        if (this.depth + 1 >= elStackSize) {
            int newSize = (this.depth >= 7 ? 2 * this.depth : 8) + 2;
            boolean needsCopying = elStackSize > 0;
            String[] arr = null;
            arr = new String[newSize];
            if (needsCopying) {
                System.arraycopy(this.elName, 0, arr, 0, elStackSize);
            }
            this.elName = arr;
            arr = new String[newSize];
            if (needsCopying) {
                System.arraycopy(this.elPrefix, 0, arr, 0, elStackSize);
            }
            this.elPrefix = arr;
            arr = new String[newSize];
            if (needsCopying) {
                System.arraycopy(this.elUri, 0, arr, 0, elStackSize);
            }
            this.elUri = arr;
            int[] iarr = new int[newSize];
            if (needsCopying) {
                System.arraycopy(this.elNamespaceCount, 0, iarr, 0, elStackSize);
            } else {
                iarr[0] = 0;
            }
            this.elNamespaceCount = iarr;
            iarr = new int[newSize];
            if (needsCopying) {
                System.arraycopy(this.elRawNameEnd, 0, iarr, 0, elStackSize);
            }
            this.elRawNameEnd = iarr;
            iarr = new int[newSize];
            if (needsCopying) {
                System.arraycopy(this.elRawNameLine, 0, iarr, 0, elStackSize);
            }
            this.elRawNameLine = iarr;
            char[][] carr = new char[newSize][];
            if (needsCopying) {
                System.arraycopy(this.elRawName, 0, carr, 0, elStackSize);
            }
            this.elRawName = carr;
        }
    }

    protected void ensureAttributesCapacity(int size) {
        int attrPosSize;
        int n = attrPosSize = this.attributeName != null ? this.attributeName.length : 0;
        if (size >= attrPosSize) {
            int newSize = size > 7 ? 2 * size : 8;
            boolean needsCopying = attrPosSize > 0;
            String[] arr = null;
            arr = new String[newSize];
            if (needsCopying) {
                System.arraycopy(this.attributeName, 0, arr, 0, attrPosSize);
            }
            this.attributeName = arr;
            arr = new String[newSize];
            if (needsCopying) {
                System.arraycopy(this.attributePrefix, 0, arr, 0, attrPosSize);
            }
            this.attributePrefix = arr;
            arr = new String[newSize];
            if (needsCopying) {
                System.arraycopy(this.attributeUri, 0, arr, 0, attrPosSize);
            }
            this.attributeUri = arr;
            arr = new String[newSize];
            if (needsCopying) {
                System.arraycopy(this.attributeValue, 0, arr, 0, attrPosSize);
            }
            this.attributeValue = arr;
            if (!this.allStringsInterned) {
                int[] iarr = new int[newSize];
                if (needsCopying) {
                    System.arraycopy(this.attributeNameHash, 0, iarr, 0, attrPosSize);
                }
                this.attributeNameHash = iarr;
            }
            arr = null;
        }
    }

    protected void ensureNamespacesCapacity(int size) {
        int namespaceSize;
        int n = namespaceSize = this.namespacePrefix != null ? this.namespacePrefix.length : 0;
        if (size >= namespaceSize) {
            int newSize = size > 7 ? 2 * size : 8;
            String[] newNamespacePrefix = new String[newSize];
            String[] newNamespaceUri = new String[newSize];
            if (this.namespacePrefix != null) {
                System.arraycopy(this.namespacePrefix, 0, newNamespacePrefix, 0, this.namespaceEnd);
                System.arraycopy(this.namespaceUri, 0, newNamespaceUri, 0, this.namespaceEnd);
            }
            this.namespacePrefix = newNamespacePrefix;
            this.namespaceUri = newNamespaceUri;
            if (!this.allStringsInterned) {
                int[] newNamespacePrefixHash = new int[newSize];
                if (this.namespacePrefixHash != null) {
                    System.arraycopy(this.namespacePrefixHash, 0, newNamespacePrefixHash, 0, this.namespaceEnd);
                }
                this.namespacePrefixHash = newNamespacePrefixHash;
            }
        }
    }

    protected static final int fastHash(char[] ch, int off, int len) {
        if (len == 0) {
            return 0;
        }
        int hash = ch[off];
        hash = (hash << 7) + ch[off + len - 1];
        if (len > 16) {
            hash = (hash << 7) + ch[off + len / 4];
        }
        if (len > 8) {
            hash = (hash << 7) + ch[off + len / 2];
        }
        return hash;
    }

    protected void ensureEntityCapacity() {
        int entitySize;
        int n = entitySize = this.entityReplacementBuf != null ? this.entityReplacementBuf.length : 0;
        if (this.entityEnd >= entitySize) {
            int newSize = this.entityEnd > 7 ? 2 * this.entityEnd : 8;
            String[] newEntityName = new String[newSize];
            char[][] newEntityNameBuf = new char[newSize][];
            String[] newEntityReplacement = new String[newSize];
            char[][] newEntityReplacementBuf = new char[newSize][];
            if (this.entityName != null) {
                System.arraycopy(this.entityName, 0, newEntityName, 0, this.entityEnd);
                System.arraycopy(this.entityNameBuf, 0, newEntityNameBuf, 0, this.entityEnd);
                System.arraycopy(this.entityReplacement, 0, newEntityReplacement, 0, this.entityEnd);
                System.arraycopy(this.entityReplacementBuf, 0, newEntityReplacementBuf, 0, this.entityEnd);
            }
            this.entityName = newEntityName;
            this.entityNameBuf = newEntityNameBuf;
            this.entityReplacement = newEntityReplacement;
            this.entityReplacementBuf = newEntityReplacementBuf;
            if (!this.allStringsInterned) {
                int[] newEntityNameHash = new int[newSize];
                if (this.entityNameHash != null) {
                    System.arraycopy(this.entityNameHash, 0, newEntityNameHash, 0, this.entityEnd);
                }
                this.entityNameHash = newEntityNameHash;
            }
        }
    }

    protected void reset() {
        this.location = null;
        this.lineNumber = 1;
        this.columnNumber = 0;
        this.seenRoot = false;
        this.reachedEnd = false;
        this.eventType = 0;
        this.emptyElementTag = false;
        this.depth = 0;
        this.attributeCount = 0;
        this.namespaceEnd = 0;
        this.entityEnd = 0;
        this.setupFromTemplate();
        this.reader = null;
        this.inputEncoding = null;
        this.preventBufferCompaction = false;
        this.bufAbsoluteStart = 0;
        this.bufStart = 0;
        this.bufEnd = 0;
        this.posEnd = 0;
        this.posStart = 0;
        this.pos = 0;
        this.pcStart = 0;
        this.pcEnd = 0;
        this.usePC = false;
        this.seenStartTag = false;
        this.seenEndTag = false;
        this.pastEndTag = false;
        this.seenAmpersand = false;
        this.seenMarkup = false;
        this.seenDocdecl = false;
        this.xmlDeclVersion = null;
        this.xmlDeclStandalone = null;
        this.xmlDeclContent = null;
        this.resetStringCache();
    }

    public MXParser() {
        this.replacementMapTemplate = null;
    }

    public MXParser(EntityReplacementMap entityReplacementMap) {
        this.replacementMapTemplate = entityReplacementMap;
    }

    public void setupFromTemplate() {
        if (this.replacementMapTemplate != null) {
            int length = this.replacementMapTemplate.entityEnd;
            this.entityName = this.replacementMapTemplate.entityName;
            this.entityNameBuf = this.replacementMapTemplate.entityNameBuf;
            this.entityReplacement = this.replacementMapTemplate.entityReplacement;
            this.entityReplacementBuf = this.replacementMapTemplate.entityReplacementBuf;
            this.entityNameHash = this.replacementMapTemplate.entityNameHash;
            this.entityEnd = length;
        }
    }

    @Override
    public void setFeature(String name, boolean state) throws XmlPullParserException {
        if (name == null) {
            throw new IllegalArgumentException("feature name should not be null");
        }
        if ("http://xmlpull.org/v1/doc/features.html#process-namespaces".equals(name)) {
            if (this.eventType != 0) {
                throw new XmlPullParserException("namespace processing feature can only be changed before parsing", this, null);
            }
            this.processNamespaces = state;
        } else if (FEATURE_NAMES_INTERNED.equals(name)) {
            if (state) {
                throw new XmlPullParserException("interning names in this implementation is not supported");
            }
        } else if ("http://xmlpull.org/v1/doc/features.html#process-docdecl".equals(name)) {
            if (state) {
                throw new XmlPullParserException("processing DOCDECL is not supported");
            }
        } else if (FEATURE_XML_ROUNDTRIP.equals(name)) {
            this.roundtripSupported = state;
        } else {
            throw new XmlPullParserException("unsupported feature " + name);
        }
    }

    @Override
    public boolean getFeature(String name) {
        if (name == null) {
            throw new IllegalArgumentException("feature name should not be null");
        }
        if ("http://xmlpull.org/v1/doc/features.html#process-namespaces".equals(name)) {
            return this.processNamespaces;
        }
        if (FEATURE_NAMES_INTERNED.equals(name)) {
            return false;
        }
        if ("http://xmlpull.org/v1/doc/features.html#process-docdecl".equals(name)) {
            return false;
        }
        if (FEATURE_XML_ROUNDTRIP.equals(name)) {
            return this.roundtripSupported;
        }
        return false;
    }

    @Override
    public void setProperty(String name, Object value) throws XmlPullParserException {
        if (!PROPERTY_LOCATION.equals(name)) {
            throw new XmlPullParserException("unsupported property: '" + name + "'");
        }
        this.location = (String)value;
    }

    @Override
    public Object getProperty(String name) {
        if (name == null) {
            throw new IllegalArgumentException("property name should not be null");
        }
        if (PROPERTY_XMLDECL_VERSION.equals(name)) {
            return this.xmlDeclVersion;
        }
        if (PROPERTY_XMLDECL_STANDALONE.equals(name)) {
            return this.xmlDeclStandalone;
        }
        if (PROPERTY_XMLDECL_CONTENT.equals(name)) {
            return this.xmlDeclContent;
        }
        if (PROPERTY_LOCATION.equals(name)) {
            return this.location;
        }
        return null;
    }

    @Override
    public void setInput(Reader in) throws XmlPullParserException {
        this.reset();
        this.reader = in;
    }

    @Override
    public void setInput(InputStream inputStream, String inputEncoding) throws XmlPullParserException {
        Reader reader;
        if (inputStream == null) {
            throw new IllegalArgumentException("input stream can not be null");
        }
        try {
            reader = inputEncoding != null ? ReaderFactory.newReader(inputStream, inputEncoding) : ReaderFactory.newXmlReader(inputStream);
        }
        catch (UnsupportedEncodingException une) {
            throw new XmlPullParserException("could not create reader for encoding " + inputEncoding + " : " + une, this, une);
        }
        catch (IOException e) {
            throw new XmlPullParserException("could not create reader : " + e, this, e);
        }
        this.setInput(reader);
        this.inputEncoding = inputEncoding;
    }

    @Override
    public String getInputEncoding() {
        return this.inputEncoding;
    }

    @Override
    public void defineEntityReplacementText(String entityName, String replacementText) throws XmlPullParserException {
        if (!replacementText.startsWith("&#") && this.entityName != null && replacementText.length() > 1) {
            String tmp = replacementText.substring(1, replacementText.length() - 1);
            for (int i = 0; i < this.entityName.length; ++i) {
                if (this.entityName[i] == null || !this.entityName[i].equals(tmp)) continue;
                replacementText = this.entityReplacement[i];
            }
        }
        this.ensureEntityCapacity();
        char[] entityNameCharData = entityName.toCharArray();
        this.entityName[this.entityEnd] = this.newString(entityNameCharData, 0, entityName.length());
        this.entityNameBuf[this.entityEnd] = entityNameCharData;
        this.entityReplacement[this.entityEnd] = replacementText;
        this.entityReplacementBuf[this.entityEnd] = replacementText.toCharArray();
        if (!this.allStringsInterned) {
            this.entityNameHash[this.entityEnd] = MXParser.fastHash(this.entityNameBuf[this.entityEnd], 0, this.entityNameBuf[this.entityEnd].length);
        }
        ++this.entityEnd;
    }

    @Override
    public int getNamespaceCount(int depth) throws XmlPullParserException {
        if (!this.processNamespaces || depth == 0) {
            return 0;
        }
        if (depth < 0 || depth > this.depth) {
            throw new IllegalArgumentException("namespace count may be for depth 0.." + this.depth + " not " + depth);
        }
        return this.elNamespaceCount[depth];
    }

    @Override
    public String getNamespacePrefix(int pos) throws XmlPullParserException {
        if (pos < this.namespaceEnd) {
            return this.namespacePrefix[pos];
        }
        throw new XmlPullParserException("position " + pos + " exceeded number of available namespaces " + this.namespaceEnd);
    }

    @Override
    public String getNamespaceUri(int pos) throws XmlPullParserException {
        if (pos < this.namespaceEnd) {
            return this.namespaceUri[pos];
        }
        throw new XmlPullParserException("position " + pos + " exceeded number of available namespaces " + this.namespaceEnd);
    }

    @Override
    public String getNamespace(String prefix) {
        if (prefix != null) {
            for (int i = this.namespaceEnd - 1; i >= 0; --i) {
                if (!prefix.equals(this.namespacePrefix[i])) continue;
                return this.namespaceUri[i];
            }
            if ("xml".equals(prefix)) {
                return XML_URI;
            }
            if ("xmlns".equals(prefix)) {
                return XMLNS_URI;
            }
        } else {
            for (int i = this.namespaceEnd - 1; i >= 0; --i) {
                if (this.namespacePrefix[i] != null) continue;
                return this.namespaceUri[i];
            }
        }
        return null;
    }

    @Override
    public int getDepth() {
        return this.depth;
    }

    private static int findFragment(int bufMinPos, char[] b, int start, int end) {
        char c;
        if (start < bufMinPos) {
            start = bufMinPos;
            if (start > end) {
                start = end;
            }
            return start;
        }
        if (end - start > 65) {
            start = end - 10;
        }
        int i = start + 1;
        while (--i > bufMinPos && end - i <= 65 && ((c = b[i]) != '<' || start - i <= 10)) {
        }
        return i;
    }

    @Override
    public String getPositionDescription() {
        String fragment = null;
        if (this.posStart <= this.pos) {
            int start = MXParser.findFragment(0, this.buf, this.posStart, this.pos);
            if (start < this.pos) {
                fragment = new String(this.buf, start, this.pos - start);
            }
            if (this.bufAbsoluteStart > 0 || start > 0) {
                fragment = "..." + fragment;
            }
        }
        return " " + TYPES[this.eventType] + (fragment != null ? " seen " + this.printable(fragment) + "..." : "") + " " + (this.location != null ? this.location : "") + "@" + this.getLineNumber() + ":" + this.getColumnNumber();
    }

    @Override
    public int getLineNumber() {
        return this.lineNumber;
    }

    @Override
    public int getColumnNumber() {
        return this.columnNumber;
    }

    @Override
    public boolean isWhitespace() throws XmlPullParserException {
        if (this.eventType == 4 || this.eventType == 5) {
            if (this.usePC) {
                for (int i = this.pcStart; i < this.pcEnd; ++i) {
                    if (this.isS(this.pc[i])) continue;
                    return false;
                }
                return true;
            }
            for (int i = this.posStart; i < this.posEnd; ++i) {
                if (this.isS(this.buf[i])) continue;
                return false;
            }
            return true;
        }
        if (this.eventType == 7) {
            return true;
        }
        throw new XmlPullParserException("no content available to check for whitespaces");
    }

    @Override
    public String getText() {
        if (this.eventType == 0 || this.eventType == 1) {
            return null;
        }
        if (this.eventType == 6) {
            return this.text;
        }
        if (this.text == null) {
            this.text = !this.usePC || this.eventType == 2 || this.eventType == 3 ? new String(this.buf, this.posStart, this.posEnd - this.posStart) : new String(this.pc, this.pcStart, this.pcEnd - this.pcStart);
        }
        return this.text;
    }

    @Override
    public char[] getTextCharacters(int[] holderForStartAndLength) {
        if (this.eventType == 4) {
            if (this.usePC) {
                holderForStartAndLength[0] = this.pcStart;
                holderForStartAndLength[1] = this.pcEnd - this.pcStart;
                return this.pc;
            }
            holderForStartAndLength[0] = this.posStart;
            holderForStartAndLength[1] = this.posEnd - this.posStart;
            return this.buf;
        }
        if (this.eventType == 2 || this.eventType == 3 || this.eventType == 5 || this.eventType == 9 || this.eventType == 6 || this.eventType == 8 || this.eventType == 7 || this.eventType == 10) {
            holderForStartAndLength[0] = this.posStart;
            holderForStartAndLength[1] = this.posEnd - this.posStart;
            return this.buf;
        }
        if (this.eventType == 0 || this.eventType == 1) {
            holderForStartAndLength[1] = -1;
            holderForStartAndLength[0] = -1;
            return null;
        }
        throw new IllegalArgumentException("unknown text eventType: " + this.eventType);
    }

    @Override
    public String getNamespace() {
        if (this.eventType == 2) {
            return this.processNamespaces ? this.elUri[this.depth] : "";
        }
        if (this.eventType == 3) {
            return this.processNamespaces ? this.elUri[this.depth] : "";
        }
        return null;
    }

    @Override
    public String getName() {
        if (this.eventType == 2) {
            return this.elName[this.depth];
        }
        if (this.eventType == 3) {
            return this.elName[this.depth];
        }
        if (this.eventType == 6) {
            if (this.entityRefName == null) {
                this.entityRefName = this.newString(this.buf, this.posStart, this.posEnd - this.posStart);
            }
            return this.entityRefName;
        }
        return null;
    }

    @Override
    public String getPrefix() {
        if (this.eventType == 2) {
            return this.elPrefix[this.depth];
        }
        if (this.eventType == 3) {
            return this.elPrefix[this.depth];
        }
        return null;
    }

    @Override
    public boolean isEmptyElementTag() throws XmlPullParserException {
        if (this.eventType != 2) {
            throw new XmlPullParserException("parser must be on START_TAG to check for empty element", this, null);
        }
        return this.emptyElementTag;
    }

    @Override
    public int getAttributeCount() {
        if (this.eventType != 2) {
            return -1;
        }
        return this.attributeCount;
    }

    @Override
    public String getAttributeNamespace(int index) {
        if (this.eventType != 2) {
            throw new IndexOutOfBoundsException("only START_TAG can have attributes");
        }
        if (!this.processNamespaces) {
            return "";
        }
        if (index < 0 || index >= this.attributeCount) {
            throw new IndexOutOfBoundsException("attribute position must be 0.." + (this.attributeCount - 1) + " and not " + index);
        }
        return this.attributeUri[index];
    }

    @Override
    public String getAttributeName(int index) {
        if (this.eventType != 2) {
            throw new IndexOutOfBoundsException("only START_TAG can have attributes");
        }
        if (index < 0 || index >= this.attributeCount) {
            throw new IndexOutOfBoundsException("attribute position must be 0.." + (this.attributeCount - 1) + " and not " + index);
        }
        return this.attributeName[index];
    }

    @Override
    public String getAttributePrefix(int index) {
        if (this.eventType != 2) {
            throw new IndexOutOfBoundsException("only START_TAG can have attributes");
        }
        if (!this.processNamespaces) {
            return null;
        }
        if (index < 0 || index >= this.attributeCount) {
            throw new IndexOutOfBoundsException("attribute position must be 0.." + (this.attributeCount - 1) + " and not " + index);
        }
        return this.attributePrefix[index];
    }

    @Override
    public String getAttributeType(int index) {
        if (this.eventType != 2) {
            throw new IndexOutOfBoundsException("only START_TAG can have attributes");
        }
        if (index < 0 || index >= this.attributeCount) {
            throw new IndexOutOfBoundsException("attribute position must be 0.." + (this.attributeCount - 1) + " and not " + index);
        }
        return "CDATA";
    }

    @Override
    public boolean isAttributeDefault(int index) {
        if (this.eventType != 2) {
            throw new IndexOutOfBoundsException("only START_TAG can have attributes");
        }
        if (index < 0 || index >= this.attributeCount) {
            throw new IndexOutOfBoundsException("attribute position must be 0.." + (this.attributeCount - 1) + " and not " + index);
        }
        return false;
    }

    @Override
    public String getAttributeValue(int index) {
        if (this.eventType != 2) {
            throw new IndexOutOfBoundsException("only START_TAG can have attributes");
        }
        if (index < 0 || index >= this.attributeCount) {
            throw new IndexOutOfBoundsException("attribute position must be 0.." + (this.attributeCount - 1) + " and not " + index);
        }
        return this.attributeValue[index];
    }

    @Override
    public String getAttributeValue(String namespace, String name) {
        if (this.eventType != 2) {
            throw new IndexOutOfBoundsException("only START_TAG can have attributes" + this.getPositionDescription());
        }
        if (name == null) {
            throw new IllegalArgumentException("attribute name can not be null");
        }
        if (this.processNamespaces) {
            if (namespace == null) {
                namespace = "";
            }
            for (int i = 0; i < this.attributeCount; ++i) {
                if (namespace != this.attributeUri[i] && !namespace.equals(this.attributeUri[i]) || !name.equals(this.attributeName[i])) continue;
                return this.attributeValue[i];
            }
        } else {
            if (namespace != null && namespace.length() == 0) {
                namespace = null;
            }
            if (namespace != null) {
                throw new IllegalArgumentException("when namespaces processing is disabled attribute namespace must be null");
            }
            for (int i = 0; i < this.attributeCount; ++i) {
                if (!name.equals(this.attributeName[i])) continue;
                return this.attributeValue[i];
            }
        }
        return null;
    }

    @Override
    public int getEventType() throws XmlPullParserException {
        return this.eventType;
    }

    @Override
    public void require(int type, String namespace, String name) throws XmlPullParserException, IOException {
        if (!this.processNamespaces && namespace != null) {
            throw new XmlPullParserException("processing namespaces must be enabled on parser (or factory) to have possible namespaces declared on elements" + " (position:" + this.getPositionDescription() + ")");
        }
        if (type != this.getEventType() || namespace != null && !namespace.equals(this.getNamespace()) || name != null && !name.equals(this.getName())) {
            throw new XmlPullParserException("expected event " + TYPES[type] + (name != null ? " with name '" + name + "'" : "") + (namespace != null && name != null ? " and" : "") + (namespace != null ? " with namespace '" + namespace + "'" : "") + " but got" + (type != this.getEventType() ? " " + TYPES[this.getEventType()] : "") + (name != null && this.getName() != null && !name.equals(this.getName()) ? " name '" + this.getName() + "'" : "") + (namespace != null && name != null && this.getName() != null && !name.equals(this.getName()) && this.getNamespace() != null && !namespace.equals(this.getNamespace()) ? " and" : "") + (namespace != null && this.getNamespace() != null && !namespace.equals(this.getNamespace()) ? " namespace '" + this.getNamespace() + "'" : "") + " (position:" + this.getPositionDescription() + ")");
        }
    }

    public void skipSubTree() throws XmlPullParserException, IOException {
        this.require(2, null, null);
        int level = 1;
        while (level > 0) {
            int eventType = this.next();
            if (eventType == 3) {
                --level;
                continue;
            }
            if (eventType != 2) continue;
            ++level;
        }
    }

    @Override
    public String nextText() throws XmlPullParserException, IOException {
        if (this.getEventType() != 2) {
            throw new XmlPullParserException("parser must be on START_TAG to read next text", this, null);
        }
        int eventType = this.next();
        if (eventType == 4) {
            String result = this.getText();
            eventType = this.next();
            if (eventType != 3) {
                throw new XmlPullParserException("TEXT must be immediately followed by END_TAG and not " + TYPES[this.getEventType()], this, null);
            }
            return result;
        }
        if (eventType == 3) {
            return "";
        }
        throw new XmlPullParserException("parser must be on START_TAG or TEXT to read text", this, null);
    }

    @Override
    public int nextTag() throws XmlPullParserException, IOException {
        this.next();
        if (this.eventType == 4 && this.isWhitespace()) {
            this.next();
        }
        if (this.eventType != 2 && this.eventType != 3) {
            throw new XmlPullParserException("expected START_TAG or END_TAG not " + TYPES[this.getEventType()], this, null);
        }
        return this.eventType;
    }

    @Override
    public int next() throws XmlPullParserException, IOException {
        this.tokenize = false;
        return this.nextImpl();
    }

    @Override
    public int nextToken() throws XmlPullParserException, IOException {
        this.tokenize = true;
        return this.nextImpl();
    }

    /*
     * Unable to fully structure code
     */
    protected int nextImpl() throws XmlPullParserException, IOException {
        block53: {
            this.text = null;
            this.pcStart = 0;
            this.pcEnd = 0;
            this.usePC = false;
            this.bufStart = this.posEnd;
            if (this.pastEndTag) {
                this.pastEndTag = false;
                --this.depth;
                this.namespaceEnd = this.elNamespaceCount[this.depth];
            }
            if (this.emptyElementTag) {
                this.emptyElementTag = false;
                this.pastEndTag = true;
                this.eventType = 3;
                return 3;
            }
            if (this.depth <= 0) break block53;
            if (this.seenStartTag) {
                this.seenStartTag = false;
                this.eventType = this.parseStartTag();
                return this.eventType;
            }
            if (this.seenEndTag) {
                this.seenEndTag = false;
                this.eventType = this.parseEndTag();
                return this.eventType;
            }
            if (this.seenMarkup) {
                this.seenMarkup = false;
                ch = '<';
            } else if (this.seenAmpersand) {
                this.seenAmpersand = false;
                ch = '&';
            } else {
                ch = this.more();
            }
            this.posStart = this.pos - 1;
            hadCharData = false;
            needsMerging = false;
            while (true) {
                block56: {
                    block54: {
                        block57: {
                            block55: {
                                if (ch != 60) ** GOTO lbl98
                                if (hadCharData && this.tokenize) {
                                    this.seenMarkup = true;
                                    this.eventType = 4;
                                    return 4;
                                }
                                ch = this.more();
                                if (ch == '/') {
                                    if (!this.tokenize && hadCharData) {
                                        this.seenEndTag = true;
                                        this.eventType = 4;
                                        return 4;
                                    }
                                    this.eventType = this.parseEndTag();
                                    return this.eventType;
                                }
                                if (ch != 33) break block54;
                                ch = this.more();
                                if (ch != 45) break block55;
                                this.parseComment();
                                if (this.tokenize) {
                                    this.eventType = 9;
                                    return 9;
                                }
                                if (!this.usePC && hadCharData) {
                                    needsMerging = true;
                                } else {
                                    this.posStart = this.pos;
                                }
                                break block56;
                            }
                            if (ch != 91) break block57;
                            this.parseCDSect(hadCharData);
                            if (this.tokenize) {
                                this.eventType = 5;
                                return 5;
                            }
                            cdEnd = this.posEnd;
                            cdStart = this.posStart;
                            cdLen = cdEnd - cdStart;
                            if (cdLen <= 0) break block56;
                            hadCharData = true;
                            if (this.usePC) break block56;
                            needsMerging = true;
                            break block56;
                        }
                        throw new XmlPullParserException("unexpected character in markup " + this.printable(ch), this, null);
                    }
                    if (ch == '?') {
                        this.parsePI();
                        if (this.tokenize) {
                            this.eventType = 8;
                            return 8;
                        }
                        if (!this.usePC && hadCharData) {
                            needsMerging = true;
                        } else {
                            this.posStart = this.pos;
                        }
                    } else {
                        if (this.isNameStartChar(ch)) {
                            if (!this.tokenize && hadCharData) {
                                this.seenStartTag = true;
                                this.eventType = 4;
                                return 4;
                            }
                            this.eventType = this.parseStartTag();
                            return this.eventType;
                        }
                        throw new XmlPullParserException("unexpected character in markup " + this.printable(ch), this, null);
lbl98:
                        // 1 sources

                        if (ch == '&') {
                            if (this.tokenize && hadCharData) {
                                this.seenAmpersand = true;
                                this.eventType = 4;
                                return 4;
                            }
                            oldStart = this.posStart + this.bufAbsoluteStart;
                            oldEnd = this.posEnd + this.bufAbsoluteStart;
                            resolvedEntity = this.parseEntityRef();
                            if (this.tokenize) {
                                this.eventType = 6;
                                return 6;
                            }
                            if (resolvedEntity == null) {
                                if (this.entityRefName == null) {
                                    this.entityRefName = this.newString(this.buf, this.posStart, this.posEnd - this.posStart);
                                }
                                throw new XmlPullParserException("could not resolve entity named '" + this.printable(this.entityRefName) + "'", this, null);
                            }
                            this.posStart = oldStart - this.bufAbsoluteStart;
                            this.posEnd = oldEnd - this.bufAbsoluteStart;
                            if (!this.usePC) {
                                if (hadCharData) {
                                    this.joinPC();
                                    needsMerging = false;
                                } else {
                                    this.usePC = true;
                                    this.pcEnd = 0;
                                    this.pcStart = 0;
                                }
                            }
                            for (char aResolvedEntity : resolvedEntity) {
                                if (this.pcEnd >= this.pc.length) {
                                    this.ensurePC(this.pcEnd);
                                }
                                this.pc[this.pcEnd++] = aResolvedEntity;
                            }
                            hadCharData = true;
                        } else {
                            if (needsMerging) {
                                this.joinPC();
                                needsMerging = false;
                            }
                            hadCharData = true;
                            normalizedCR = false;
                            normalizeInput = this.tokenize == false || this.roundtripSupported == false;
                            seenBracket = false;
                            seenBracketBracket = false;
                            do {
                                if (ch == ']') {
                                    if (seenBracket) {
                                        seenBracketBracket = true;
                                    } else {
                                        seenBracket = true;
                                    }
                                } else {
                                    if (seenBracketBracket && ch == '>') {
                                        throw new XmlPullParserException("characters ]]> are not allowed in content", this, null);
                                    }
                                    if (seenBracket) {
                                        seenBracket = false;
                                        seenBracketBracket = false;
                                    }
                                }
                                if (!normalizeInput) continue;
                                if (ch == '\r') {
                                    normalizedCR = true;
                                    this.posEnd = this.pos - 1;
                                    if (!this.usePC) {
                                        if (this.posEnd > this.posStart) {
                                            this.joinPC();
                                        } else {
                                            this.usePC = true;
                                            this.pcEnd = 0;
                                            this.pcStart = 0;
                                        }
                                    }
                                    if (this.pcEnd >= this.pc.length) {
                                        this.ensurePC(this.pcEnd);
                                    }
                                    this.pc[this.pcEnd++] = 10;
                                    continue;
                                }
                                if (ch == '\n') {
                                    if (!normalizedCR && this.usePC) {
                                        if (this.pcEnd >= this.pc.length) {
                                            this.ensurePC(this.pcEnd);
                                        }
                                        this.pc[this.pcEnd++] = 10;
                                    }
                                    normalizedCR = false;
                                    continue;
                                }
                                if (this.usePC) {
                                    if (this.pcEnd >= this.pc.length) {
                                        this.ensurePC(this.pcEnd);
                                    }
                                    this.pc[this.pcEnd++] = ch;
                                }
                                normalizedCR = false;
                            } while ((ch = this.more()) != '<' && ch != 38);
                            this.posEnd = this.pos - 1;
                            continue;
                        }
                    }
                }
                ch = this.more();
            }
        }
        if (this.seenRoot) {
            return this.parseEpilog();
        }
        return this.parseProlog();
    }

    /*
     * Enabled aggressive block sorting
     */
    protected int parseProlog() throws XmlPullParserException, IOException {
        char ch = this.seenMarkup ? this.buf[this.pos - 1] : this.more();
        if (this.eventType == 0) {
            if (ch == '\ufffe') {
                throw new XmlPullParserException("first character in input was UNICODE noncharacter (0xFFFE)- input requires int swapping", this, null);
            }
            if (ch == '\ufeff') {
                ch = this.more();
            }
        }
        this.seenMarkup = false;
        boolean gotS = false;
        this.posStart = this.pos - 1;
        boolean normalizeIgnorableWS = this.tokenize && !this.roundtripSupported;
        boolean normalizedCR = false;
        while (true) {
            block31: {
                block32: {
                    block33: {
                        if (ch != '<') break block32;
                        if (gotS && this.tokenize) {
                            this.posEnd = this.pos - 1;
                            this.seenMarkup = true;
                            this.eventType = 7;
                            return 7;
                        }
                        ch = this.more();
                        if (ch != '?') break block33;
                        boolean isXMLDecl = this.parsePI();
                        if (this.tokenize) {
                            if (isXMLDecl) {
                                this.eventType = 0;
                                return 0;
                            }
                            this.eventType = 8;
                            return 8;
                        }
                        break block31;
                    }
                    if (ch == '!') {
                        ch = this.more();
                        if (ch == 'D') {
                            if (this.seenDocdecl) {
                                throw new XmlPullParserException("only one docdecl allowed in XML document", this, null);
                            }
                            this.seenDocdecl = true;
                            this.parseDocdecl();
                            if (this.tokenize) {
                                this.eventType = 10;
                                return 10;
                            }
                            break block31;
                        } else {
                            if (ch != '-') {
                                throw new XmlPullParserException("unexpected markup <!" + this.printable(ch), this, null);
                            }
                            this.parseComment();
                            if (this.tokenize) {
                                this.eventType = 9;
                                return 9;
                            }
                        }
                        break block31;
                    } else {
                        if (ch == '/') {
                            throw new XmlPullParserException("expected start tag name and not " + this.printable(ch), this, null);
                        }
                        if (this.isNameStartChar(ch)) {
                            this.seenRoot = true;
                            return this.parseStartTag();
                        }
                        throw new XmlPullParserException("expected start tag name and not " + this.printable(ch), this, null);
                    }
                }
                if (!this.isS(ch)) {
                    throw new XmlPullParserException("only whitespace content allowed before start tag and not " + this.printable(ch), this, null);
                }
                gotS = true;
                if (normalizeIgnorableWS) {
                    if (ch == '\r') {
                        normalizedCR = true;
                        if (!this.usePC) {
                            this.posEnd = this.pos - 1;
                            if (this.posEnd > this.posStart) {
                                this.joinPC();
                            } else {
                                this.usePC = true;
                                this.pcEnd = 0;
                                this.pcStart = 0;
                            }
                        }
                        if (this.pcEnd >= this.pc.length) {
                            this.ensurePC(this.pcEnd);
                        }
                        this.pc[this.pcEnd++] = 10;
                    } else if (ch == '\n') {
                        if (!normalizedCR && this.usePC) {
                            if (this.pcEnd >= this.pc.length) {
                                this.ensurePC(this.pcEnd);
                            }
                            this.pc[this.pcEnd++] = 10;
                        }
                        normalizedCR = false;
                    } else {
                        if (this.usePC) {
                            if (this.pcEnd >= this.pc.length) {
                                this.ensurePC(this.pcEnd);
                            }
                            this.pc[this.pcEnd++] = ch;
                        }
                        normalizedCR = false;
                    }
                }
            }
            ch = this.more();
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected int parseEpilog() throws XmlPullParserException, IOException {
        boolean gotS;
        block32: {
            if (this.eventType == 1) {
                throw new XmlPullParserException("already reached end of XML input", this, null);
            }
            if (this.reachedEnd) {
                this.eventType = 1;
                return 1;
            }
            gotS = false;
            boolean normalizeIgnorableWS = this.tokenize && !this.roundtripSupported;
            boolean normalizedCR = false;
            try {
                char ch = this.seenMarkup ? this.buf[this.pos - 1] : this.more();
                this.seenMarkup = false;
                this.posStart = this.pos - 1;
                if (this.reachedEnd) break block32;
                do {
                    block33: {
                        block34: {
                            block35: {
                                if (ch != '<') break block34;
                                if (gotS && this.tokenize) {
                                    this.posEnd = this.pos - 1;
                                    this.seenMarkup = true;
                                    this.eventType = 7;
                                    return 7;
                                }
                                ch = this.more();
                                if (this.reachedEnd) break block32;
                                if (ch != '?') break block35;
                                this.parsePI();
                                if (this.tokenize) {
                                    this.eventType = 8;
                                    return 8;
                                }
                                break block33;
                            }
                            if (ch == '!') {
                                ch = this.more();
                                if (this.reachedEnd) break block32;
                                if (ch == 'D') {
                                    this.parseDocdecl();
                                    if (this.tokenize) {
                                        this.eventType = 10;
                                        return 10;
                                    }
                                    break block33;
                                } else {
                                    if (ch != '-') {
                                        throw new XmlPullParserException("unexpected markup <!" + this.printable(ch), this, null);
                                    }
                                    this.parseComment();
                                    if (this.tokenize) {
                                        this.eventType = 9;
                                        return 9;
                                    }
                                }
                                break block33;
                            } else {
                                if (ch == '/') {
                                    throw new XmlPullParserException("end tag not allowed in epilog but got " + this.printable(ch), this, null);
                                }
                                if (this.isNameStartChar(ch)) {
                                    throw new XmlPullParserException("start tag not allowed in epilog but got " + this.printable(ch), this, null);
                                }
                                throw new XmlPullParserException("in epilog expected ignorable content and not " + this.printable(ch), this, null);
                            }
                        }
                        if (!this.isS(ch)) {
                            throw new XmlPullParserException("in epilog non whitespace content is not allowed but got " + this.printable(ch), this, null);
                        }
                        gotS = true;
                        if (normalizeIgnorableWS) {
                            if (ch == '\r') {
                                normalizedCR = true;
                                if (!this.usePC) {
                                    this.posEnd = this.pos - 1;
                                    if (this.posEnd > this.posStart) {
                                        this.joinPC();
                                    } else {
                                        this.usePC = true;
                                        this.pcEnd = 0;
                                        this.pcStart = 0;
                                    }
                                }
                                if (this.pcEnd >= this.pc.length) {
                                    this.ensurePC(this.pcEnd);
                                }
                                this.pc[this.pcEnd++] = 10;
                            } else if (ch == '\n') {
                                if (!normalizedCR && this.usePC) {
                                    if (this.pcEnd >= this.pc.length) {
                                        this.ensurePC(this.pcEnd);
                                    }
                                    this.pc[this.pcEnd++] = 10;
                                }
                                normalizedCR = false;
                            } else {
                                if (this.usePC) {
                                    if (this.pcEnd >= this.pc.length) {
                                        this.ensurePC(this.pcEnd);
                                    }
                                    this.pc[this.pcEnd++] = ch;
                                }
                                normalizedCR = false;
                            }
                        }
                    }
                    ch = this.more();
                } while (!this.reachedEnd);
            }
            catch (EOFException ex) {
                this.reachedEnd = true;
            }
        }
        if (!this.reachedEnd) {
            throw new XmlPullParserException("internal error in parseEpilog");
        }
        if (this.tokenize && gotS) {
            this.posEnd = this.pos;
            this.eventType = 7;
            return 7;
        }
        this.eventType = 1;
        return 1;
    }

    public int parseEndTag() throws XmlPullParserException, IOException {
        char ch = this.more();
        if (!this.isNameStartChar(ch)) {
            throw new XmlPullParserException("expected name start and not " + this.printable(ch), this, null);
        }
        this.posStart = this.pos - 3;
        int nameStart = this.pos - 1 + this.bufAbsoluteStart;
        while (this.isNameChar(ch = this.more())) {
        }
        int off = nameStart - this.bufAbsoluteStart;
        int len = this.pos - 1 - off;
        char[] cbuf = this.elRawName[this.depth];
        if (this.elRawNameEnd[this.depth] != len) {
            String startname = new String(cbuf, 0, this.elRawNameEnd[this.depth]);
            String endname = new String(this.buf, off, len);
            throw new XmlPullParserException("end tag name </" + endname + "> must match start tag name <" + startname + "> from line " + this.elRawNameLine[this.depth], this, null);
        }
        for (int i = 0; i < len; ++i) {
            if (this.buf[off++] == cbuf[i]) continue;
            String startname = new String(cbuf, 0, len);
            String endname = new String(this.buf, off - i - 1, len);
            throw new XmlPullParserException("end tag name </" + endname + "> must be the same as start tag <" + startname + "> from line " + this.elRawNameLine[this.depth], this, null);
        }
        while (this.isS(ch)) {
            ch = this.more();
        }
        if (ch != '>') {
            throw new XmlPullParserException("expected > to finsh end tag not " + this.printable(ch) + " from line " + this.elRawNameLine[this.depth], this, null);
        }
        this.posEnd = this.pos;
        this.pastEndTag = true;
        this.eventType = 3;
        return 3;
    }

    public int parseStartTag() throws XmlPullParserException, IOException {
        String prefix;
        block27: {
            ++this.depth;
            this.posStart = this.pos - 2;
            this.emptyElementTag = false;
            this.attributeCount = 0;
            int nameStart = this.pos - 1 + this.bufAbsoluteStart;
            int colonPos = -1;
            char ch = this.buf[this.pos - 1];
            if (ch == ':' && this.processNamespaces) {
                throw new XmlPullParserException("when namespaces processing enabled colon can not be at element name start", this, null);
            }
            while (this.isNameChar(ch = this.more())) {
                if (ch != ':' || !this.processNamespaces) continue;
                if (colonPos != -1) {
                    throw new XmlPullParserException("only one colon is allowed in name of element when namespaces are enabled", this, null);
                }
                colonPos = this.pos - 1 + this.bufAbsoluteStart;
            }
            this.ensureElementsCapacity();
            int elLen = this.pos - 1 - (nameStart - this.bufAbsoluteStart);
            if (this.elRawName[this.depth] == null || this.elRawName[this.depth].length < elLen) {
                this.elRawName[this.depth] = new char[2 * elLen];
            }
            System.arraycopy(this.buf, nameStart - this.bufAbsoluteStart, this.elRawName[this.depth], 0, elLen);
            this.elRawNameEnd[this.depth] = elLen;
            this.elRawNameLine[this.depth] = this.lineNumber;
            String name = null;
            prefix = null;
            if (this.processNamespaces) {
                if (colonPos != -1) {
                    prefix = this.elPrefix[this.depth] = this.newString(this.buf, nameStart - this.bufAbsoluteStart, colonPos - nameStart);
                    name = this.elName[this.depth] = this.newString(this.buf, colonPos + 1 - this.bufAbsoluteStart, this.pos - 2 - (colonPos - this.bufAbsoluteStart));
                } else {
                    this.elPrefix[this.depth] = null;
                    prefix = null;
                    name = this.elName[this.depth] = this.newString(this.buf, nameStart - this.bufAbsoluteStart, elLen);
                }
            } else {
                name = this.elName[this.depth] = this.newString(this.buf, nameStart - this.bufAbsoluteStart, elLen);
            }
            while (true) {
                if (this.isS(ch)) {
                    ch = this.more();
                    continue;
                }
                if (ch == '>') break block27;
                if (ch == '/') {
                    if (this.emptyElementTag) {
                        throw new XmlPullParserException("repeated / in tag declaration", this, null);
                    }
                    this.emptyElementTag = true;
                    ch = this.more();
                    if (ch != '>') {
                        throw new XmlPullParserException("expected > to end empty tag not " + this.printable(ch), this, null);
                    }
                    break block27;
                }
                if (!this.isNameStartChar(ch)) break;
                ch = this.parseAttribute();
                ch = this.more();
            }
            throw new XmlPullParserException("start tag unexpected character " + this.printable(ch), this, null);
        }
        if (this.processNamespaces) {
            int i;
            String uri = this.getNamespace(prefix);
            if (uri == null) {
                if (prefix == null) {
                    uri = "";
                } else {
                    throw new XmlPullParserException("could not determine namespace bound to element prefix " + prefix, this, null);
                }
            }
            this.elUri[this.depth] = uri;
            for (i = 0; i < this.attributeCount; ++i) {
                String attrPrefix = this.attributePrefix[i];
                if (attrPrefix != null) {
                    String attrUri = this.getNamespace(attrPrefix);
                    if (attrUri == null) {
                        throw new XmlPullParserException("could not determine namespace bound to attribute prefix " + attrPrefix, this, null);
                    }
                    this.attributeUri[i] = attrUri;
                    continue;
                }
                this.attributeUri[i] = "";
            }
            for (i = 1; i < this.attributeCount; ++i) {
                for (int j = 0; j < i; ++j) {
                    if (this.attributeUri[j] != this.attributeUri[i] || (!this.allStringsInterned || !this.attributeName[j].equals(this.attributeName[i])) && (this.allStringsInterned || this.attributeNameHash[j] != this.attributeNameHash[i] || !this.attributeName[j].equals(this.attributeName[i]))) continue;
                    String attr1 = this.attributeName[j];
                    if (this.attributeUri[j] != null) {
                        attr1 = this.attributeUri[j] + ":" + attr1;
                    }
                    String attr2 = this.attributeName[i];
                    if (this.attributeUri[i] != null) {
                        attr2 = this.attributeUri[i] + ":" + attr2;
                    }
                    throw new XmlPullParserException("duplicated attributes " + attr1 + " and " + attr2, this, null);
                }
            }
        } else {
            for (int i = 1; i < this.attributeCount; ++i) {
                for (int j = 0; j < i; ++j) {
                    if ((!this.allStringsInterned || !this.attributeName[j].equals(this.attributeName[i])) && (this.allStringsInterned || this.attributeNameHash[j] != this.attributeNameHash[i] || !this.attributeName[j].equals(this.attributeName[i]))) continue;
                    String attr1 = this.attributeName[j];
                    String attr2 = this.attributeName[i];
                    throw new XmlPullParserException("duplicated attributes " + attr1 + " and " + attr2, this, null);
                }
            }
        }
        this.elNamespaceCount[this.depth] = this.namespaceEnd;
        this.posEnd = this.pos;
        this.eventType = 2;
        return 2;
    }

    protected char parseAttribute() throws XmlPullParserException, IOException {
        int prevPosStart = this.posStart + this.bufAbsoluteStart;
        int nameStart = this.pos - 1 + this.bufAbsoluteStart;
        int colonPos = -1;
        char ch = this.buf[this.pos - 1];
        if (ch == ':' && this.processNamespaces) {
            throw new XmlPullParserException("when namespaces processing enabled colon can not be at attribute name start", this, null);
        }
        boolean startsWithXmlns = this.processNamespaces && ch == 'x';
        int xmlnsPos = 0;
        ch = this.more();
        while (this.isNameChar(ch)) {
            if (this.processNamespaces) {
                if (startsWithXmlns && xmlnsPos < 5) {
                    if (++xmlnsPos == 1) {
                        if (ch != 'm') {
                            startsWithXmlns = false;
                        }
                    } else if (xmlnsPos == 2) {
                        if (ch != 'l') {
                            startsWithXmlns = false;
                        }
                    } else if (xmlnsPos == 3) {
                        if (ch != 'n') {
                            startsWithXmlns = false;
                        }
                    } else if (xmlnsPos == 4) {
                        if (ch != 's') {
                            startsWithXmlns = false;
                        }
                    } else if (xmlnsPos == 5 && ch != ':') {
                        throw new XmlPullParserException("after xmlns in attribute name must be colonwhen namespaces are enabled", this, null);
                    }
                }
                if (ch == ':') {
                    if (colonPos != -1) {
                        throw new XmlPullParserException("only one colon is allowed in attribute name when namespaces are enabled", this, null);
                    }
                    colonPos = this.pos - 1 + this.bufAbsoluteStart;
                }
            }
            ch = this.more();
        }
        this.ensureAttributesCapacity(this.attributeCount);
        String name = null;
        String prefix = null;
        if (this.processNamespaces) {
            if (xmlnsPos < 4) {
                startsWithXmlns = false;
            }
            if (startsWithXmlns) {
                if (colonPos != -1) {
                    int nameLen = this.pos - 2 - (colonPos - this.bufAbsoluteStart);
                    if (nameLen == 0) {
                        throw new XmlPullParserException("namespace prefix is required after xmlns:  when namespaces are enabled", this, null);
                    }
                    name = this.newString(this.buf, colonPos - this.bufAbsoluteStart + 1, nameLen);
                }
            } else {
                if (colonPos != -1) {
                    int prefixLen = colonPos - nameStart;
                    prefix = this.attributePrefix[this.attributeCount] = this.newString(this.buf, nameStart - this.bufAbsoluteStart, prefixLen);
                    int nameLen = this.pos - 2 - (colonPos - this.bufAbsoluteStart);
                    name = this.attributeName[this.attributeCount] = this.newString(this.buf, colonPos - this.bufAbsoluteStart + 1, nameLen);
                } else {
                    this.attributePrefix[this.attributeCount] = null;
                    prefix = null;
                    name = this.attributeName[this.attributeCount] = this.newString(this.buf, nameStart - this.bufAbsoluteStart, this.pos - 1 - (nameStart - this.bufAbsoluteStart));
                }
                if (!this.allStringsInterned) {
                    this.attributeNameHash[this.attributeCount] = name.hashCode();
                }
            }
        } else {
            name = this.attributeName[this.attributeCount] = this.newString(this.buf, nameStart - this.bufAbsoluteStart, this.pos - 1 - (nameStart - this.bufAbsoluteStart));
            if (!this.allStringsInterned) {
                this.attributeNameHash[this.attributeCount] = name.hashCode();
            }
        }
        while (this.isS(ch)) {
            ch = this.more();
        }
        if (ch != '=') {
            throw new XmlPullParserException("expected = after attribute name", this, null);
        }
        ch = this.more();
        while (this.isS(ch)) {
            ch = this.more();
        }
        char delimit = ch;
        if (delimit != '\"' && delimit != '\'') {
            throw new XmlPullParserException("attribute value must start with quotation or apostrophe not " + this.printable(delimit), this, null);
        }
        boolean normalizedCR = false;
        this.usePC = false;
        this.pcStart = this.pcEnd;
        this.posStart = this.pos;
        while ((ch = this.more()) != delimit) {
            if (ch == '<') {
                throw new XmlPullParserException("markup not allowed inside attribute value - illegal < ", this, null);
            }
            if (ch == '&') {
                char[] resolvedEntity;
                this.posEnd = this.pos - 1;
                if (!this.usePC) {
                    boolean hadCharData;
                    boolean bl = hadCharData = this.posEnd > this.posStart;
                    if (hadCharData) {
                        this.joinPC();
                    } else {
                        this.usePC = true;
                        this.pcEnd = 0;
                        this.pcStart = 0;
                    }
                }
                if ((resolvedEntity = this.parseEntityRef()) == null) {
                    if (this.entityRefName == null) {
                        this.entityRefName = this.newString(this.buf, this.posStart, this.posEnd - this.posStart);
                    }
                    throw new XmlPullParserException("could not resolve entity named '" + this.printable(this.entityRefName) + "'", this, null);
                }
                for (char aResolvedEntity : resolvedEntity) {
                    if (this.pcEnd >= this.pc.length) {
                        this.ensurePC(this.pcEnd);
                    }
                    this.pc[this.pcEnd++] = aResolvedEntity;
                }
            } else if (ch == '\t' || ch == '\n' || ch == '\r') {
                if (!this.usePC) {
                    this.posEnd = this.pos - 1;
                    if (this.posEnd > this.posStart) {
                        this.joinPC();
                    } else {
                        this.usePC = true;
                        this.pcStart = 0;
                        this.pcEnd = 0;
                    }
                }
                if (this.pcEnd >= this.pc.length) {
                    this.ensurePC(this.pcEnd);
                }
                if (ch != '\n' || !normalizedCR) {
                    this.pc[this.pcEnd++] = 32;
                }
            } else if (this.usePC) {
                if (this.pcEnd >= this.pc.length) {
                    this.ensurePC(this.pcEnd);
                }
                this.pc[this.pcEnd++] = ch;
            }
            normalizedCR = ch == '\r';
        }
        if (this.processNamespaces && startsWithXmlns) {
            String ns = null;
            ns = !this.usePC ? this.newStringIntern(this.buf, this.posStart, this.pos - 1 - this.posStart) : this.newStringIntern(this.pc, this.pcStart, this.pcEnd - this.pcStart);
            this.ensureNamespacesCapacity(this.namespaceEnd);
            int prefixHash = -1;
            if (colonPos != -1) {
                if (ns.length() == 0) {
                    throw new XmlPullParserException("non-default namespace can not be declared to be empty string", this, null);
                }
                this.namespacePrefix[this.namespaceEnd] = name;
                if (!this.allStringsInterned) {
                    prefixHash = this.namespacePrefixHash[this.namespaceEnd] = name.hashCode();
                }
            } else {
                this.namespacePrefix[this.namespaceEnd] = null;
                if (!this.allStringsInterned) {
                    this.namespacePrefixHash[this.namespaceEnd] = -1;
                    prefixHash = -1;
                }
            }
            this.namespaceUri[this.namespaceEnd] = ns;
            int startNs = this.elNamespaceCount[this.depth - 1];
            for (int i = this.namespaceEnd - 1; i >= startNs; --i) {
                if ((!this.allStringsInterned && name != null || this.namespacePrefix[i] != name) && (this.allStringsInterned || name == null || this.namespacePrefixHash[i] != prefixHash || !name.equals(this.namespacePrefix[i]))) continue;
                String s = name == null ? "default" : "'" + name + "'";
                throw new XmlPullParserException("duplicated namespace declaration for " + s + " prefix", this, null);
            }
            ++this.namespaceEnd;
        } else {
            this.attributeValue[this.attributeCount] = !this.usePC ? new String(this.buf, this.posStart, this.pos - 1 - this.posStart) : new String(this.pc, this.pcStart, this.pcEnd - this.pcStart);
            ++this.attributeCount;
        }
        this.posStart = prevPosStart - this.bufAbsoluteStart;
        return ch;
    }

    protected char[] parseEntityRef() throws XmlPullParserException, IOException {
        this.entityRefName = null;
        this.posStart = this.pos;
        char ch = this.more();
        if (ch == '#') {
            boolean isHex;
            int charRef = 0;
            ch = this.more();
            StringBuilder sb = new StringBuilder();
            boolean bl = isHex = ch == 'x';
            if (isHex) {
                while (true) {
                    if ((ch = this.more()) >= '0' && ch <= '9') {
                        charRef = (char)(charRef * 16 + (ch - 48));
                        sb.append(ch);
                        continue;
                    }
                    if (ch >= 'a' && ch <= 'f') {
                        charRef = (char)(charRef * 16 + (ch - 87));
                        sb.append(ch);
                        continue;
                    }
                    if (ch < 'A' || ch > 'F') break;
                    charRef = (char)(charRef * 16 + (ch - 55));
                    sb.append(ch);
                }
                if (ch != ';') {
                    throw new XmlPullParserException("character reference (with hex value) may not contain " + this.printable(ch), this, null);
                }
            } else {
                while (true) {
                    if (ch < '0' || ch > '9') {
                        if (ch == ';') break;
                        throw new XmlPullParserException("character reference (with decimal value) may not contain " + this.printable(ch), this, null);
                    }
                    charRef = (char)(charRef * 10 + (ch - 48));
                    sb.append(ch);
                    ch = this.more();
                }
            }
            this.posEnd = this.pos - 1;
            try {
                this.charRefOneCharBuf = MXParser.toChars(Integer.parseInt(sb.toString(), isHex ? 16 : 10));
            }
            catch (IllegalArgumentException e) {
                throw new XmlPullParserException("character reference (with " + (isHex ? "hex" : "decimal") + " value " + sb.toString() + ") is invalid", this, null);
            }
            if (this.tokenize) {
                this.text = this.newString(this.charRefOneCharBuf, 0, this.charRefOneCharBuf.length);
            }
            return this.charRefOneCharBuf;
        }
        if (!this.isNameStartChar(ch)) {
            throw new XmlPullParserException("entity reference names can not start with character '" + this.printable(ch) + "'", this, null);
        }
        while ((ch = this.more()) != ';') {
            if (this.isNameChar(ch)) continue;
            throw new XmlPullParserException("entity reference name can not contain character " + this.printable(ch) + "'", this, null);
        }
        this.posEnd = this.pos - 1;
        int len = this.posEnd - this.posStart;
        if (len == 2 && this.buf[this.posStart] == 'l' && this.buf[this.posStart + 1] == 't') {
            if (this.tokenize) {
                this.text = "<";
            }
            this.charRefOneCharBuf[0] = 60;
            return this.charRefOneCharBuf;
        }
        if (len == 3 && this.buf[this.posStart] == 'a' && this.buf[this.posStart + 1] == 'm' && this.buf[this.posStart + 2] == 'p') {
            if (this.tokenize) {
                this.text = "&";
            }
            this.charRefOneCharBuf[0] = 38;
            return this.charRefOneCharBuf;
        }
        if (len == 2 && this.buf[this.posStart] == 'g' && this.buf[this.posStart + 1] == 't') {
            if (this.tokenize) {
                this.text = ">";
            }
            this.charRefOneCharBuf[0] = 62;
            return this.charRefOneCharBuf;
        }
        if (len == 4 && this.buf[this.posStart] == 'a' && this.buf[this.posStart + 1] == 'p' && this.buf[this.posStart + 2] == 'o' && this.buf[this.posStart + 3] == 's') {
            if (this.tokenize) {
                this.text = "'";
            }
            this.charRefOneCharBuf[0] = 39;
            return this.charRefOneCharBuf;
        }
        if (len == 4 && this.buf[this.posStart] == 'q' && this.buf[this.posStart + 1] == 'u' && this.buf[this.posStart + 2] == 'o' && this.buf[this.posStart + 3] == 't') {
            if (this.tokenize) {
                this.text = "\"";
            }
            this.charRefOneCharBuf[0] = 34;
            return this.charRefOneCharBuf;
        }
        char[] result = this.lookuEntityReplacement(len);
        if (result != null) {
            return result;
        }
        if (this.tokenize) {
            this.text = null;
        }
        return null;
    }

    protected char[] lookuEntityReplacement(int entityNameLen) throws XmlPullParserException, IOException {
        if (!this.allStringsInterned) {
            int hash = MXParser.fastHash(this.buf, this.posStart, this.posEnd - this.posStart);
            block0: for (int i = this.entityEnd - 1; i >= 0; --i) {
                if (hash != this.entityNameHash[i] || entityNameLen != this.entityNameBuf[i].length) continue;
                char[] entityBuf = this.entityNameBuf[i];
                for (int j = 0; j < entityNameLen; ++j) {
                    if (this.buf[this.posStart + j] != entityBuf[j]) continue block0;
                }
                if (this.tokenize) {
                    this.text = this.entityReplacement[i];
                }
                return this.entityReplacementBuf[i];
            }
        } else {
            this.entityRefName = this.newString(this.buf, this.posStart, this.posEnd - this.posStart);
            for (int i = this.entityEnd - 1; i >= 0; --i) {
                if (this.entityRefName != this.entityName[i]) continue;
                if (this.tokenize) {
                    this.text = this.entityReplacement[i];
                }
                return this.entityReplacementBuf[i];
            }
        }
        return null;
    }

    protected void parseComment() throws XmlPullParserException, IOException {
        char ch = this.more();
        if (ch != '-') {
            throw new XmlPullParserException("expected <!-- for comment start", this, null);
        }
        if (this.tokenize) {
            this.posStart = this.pos;
        }
        int curLine = this.lineNumber;
        int curColumn = this.columnNumber;
        try {
            boolean normalizeIgnorableWS = this.tokenize && !this.roundtripSupported;
            boolean normalizedCR = false;
            boolean seenDash = false;
            boolean seenDashDash = false;
            while (true) {
                ch = this.more();
                if (seenDashDash && ch != '>') {
                    throw new XmlPullParserException("in comment after two dashes (--) next character must be > not " + this.printable(ch), this, null);
                }
                if (ch == '-') {
                    if (!seenDash) {
                        seenDash = true;
                    } else {
                        seenDashDash = true;
                        seenDash = false;
                    }
                } else if (ch == '>') {
                    if (seenDashDash) break;
                    seenDashDash = false;
                    seenDash = false;
                } else {
                    seenDash = false;
                }
                if (!normalizeIgnorableWS) continue;
                if (ch == '\r') {
                    normalizedCR = true;
                    if (!this.usePC) {
                        this.posEnd = this.pos - 1;
                        if (this.posEnd > this.posStart) {
                            this.joinPC();
                        } else {
                            this.usePC = true;
                            this.pcEnd = 0;
                            this.pcStart = 0;
                        }
                    }
                    if (this.pcEnd >= this.pc.length) {
                        this.ensurePC(this.pcEnd);
                    }
                    this.pc[this.pcEnd++] = 10;
                    continue;
                }
                if (ch == '\n') {
                    if (!normalizedCR && this.usePC) {
                        if (this.pcEnd >= this.pc.length) {
                            this.ensurePC(this.pcEnd);
                        }
                        this.pc[this.pcEnd++] = 10;
                    }
                    normalizedCR = false;
                    continue;
                }
                if (this.usePC) {
                    if (this.pcEnd >= this.pc.length) {
                        this.ensurePC(this.pcEnd);
                    }
                    this.pc[this.pcEnd++] = ch;
                }
                normalizedCR = false;
            }
        }
        catch (EOFException ex) {
            throw new XmlPullParserException("comment started on line " + curLine + " and column " + curColumn + " was not closed", this, ex);
        }
        if (this.tokenize) {
            this.posEnd = this.pos - 3;
            if (this.usePC) {
                this.pcEnd -= 2;
            }
        }
    }

    protected boolean parsePI() throws XmlPullParserException, IOException {
        if (this.tokenize) {
            this.posStart = this.pos;
        }
        int curLine = this.lineNumber;
        int curColumn = this.columnNumber;
        int piTargetStart = this.pos;
        int piTargetEnd = -1;
        boolean normalizeIgnorableWS = this.tokenize && !this.roundtripSupported;
        boolean normalizedCR = false;
        try {
            boolean seenPITarget = false;
            boolean seenQ = false;
            char ch = this.more();
            if (this.isS(ch)) {
                throw new XmlPullParserException("processing instruction PITarget must be exactly after <? and not white space character", this, null);
            }
            while (true) {
                if (ch == '?') {
                    if (!seenPITarget) {
                        throw new XmlPullParserException("processing instruction PITarget name not found", this, null);
                    }
                    seenQ = true;
                } else if (ch == '>') {
                    if (seenQ) break;
                    if (!seenPITarget) {
                        throw new XmlPullParserException("processing instruction PITarget name not found", this, null);
                    }
                } else {
                    if (!(piTargetEnd != -1 || !this.isS(ch) || (piTargetEnd = this.pos - 1) - piTargetStart != 3 || this.buf[piTargetStart] != 'x' && this.buf[piTargetStart] != 'X' || this.buf[piTargetStart + 1] != 'm' && this.buf[piTargetStart + 1] != 'M' || this.buf[piTargetStart + 2] != 'l' && this.buf[piTargetStart + 2] != 'L')) {
                        if (piTargetStart > 3) {
                            throw new XmlPullParserException("processing instruction can not have PITarget with reserved xml name", this, null);
                        }
                        if (this.buf[piTargetStart] != 'x' && this.buf[piTargetStart + 1] != 'm' && this.buf[piTargetStart + 2] != 'l') {
                            throw new XmlPullParserException("XMLDecl must have xml name in lowercase", this, null);
                        }
                        this.parseXmlDecl(ch);
                        if (this.tokenize) {
                            this.posEnd = this.pos - 2;
                        }
                        int off = piTargetStart + 3;
                        int len = this.pos - 2 - off;
                        this.xmlDeclContent = this.newString(this.buf, off, len);
                        return false;
                    }
                    seenQ = false;
                }
                if (normalizeIgnorableWS) {
                    if (ch == '\r') {
                        normalizedCR = true;
                        if (!this.usePC) {
                            this.posEnd = this.pos - 1;
                            if (this.posEnd > this.posStart) {
                                this.joinPC();
                            } else {
                                this.usePC = true;
                                this.pcEnd = 0;
                                this.pcStart = 0;
                            }
                        }
                        if (this.pcEnd >= this.pc.length) {
                            this.ensurePC(this.pcEnd);
                        }
                        this.pc[this.pcEnd++] = 10;
                    } else if (ch == '\n') {
                        if (!normalizedCR && this.usePC) {
                            if (this.pcEnd >= this.pc.length) {
                                this.ensurePC(this.pcEnd);
                            }
                            this.pc[this.pcEnd++] = 10;
                        }
                        normalizedCR = false;
                    } else {
                        if (this.usePC) {
                            if (this.pcEnd >= this.pc.length) {
                                this.ensurePC(this.pcEnd);
                            }
                            this.pc[this.pcEnd++] = ch;
                        }
                        normalizedCR = false;
                    }
                }
                seenPITarget = true;
                ch = this.more();
            }
        }
        catch (EOFException ex) {
            throw new XmlPullParserException("processing instruction started on line " + curLine + " and column " + curColumn + " was not closed", this, ex);
        }
        if (piTargetEnd == -1) {
            piTargetEnd = this.pos - 2 + this.bufAbsoluteStart;
        }
        if (this.tokenize) {
            this.posEnd = this.pos - 2;
            if (normalizeIgnorableWS) {
                --this.pcEnd;
            }
        }
        return true;
    }

    protected void parseXmlDecl(char ch) throws XmlPullParserException, IOException {
        this.preventBufferCompaction = true;
        this.bufStart = 0;
        ch = this.skipS(ch);
        ch = this.requireInput(ch, VERSION);
        if ((ch = this.skipS(ch)) != '=') {
            throw new XmlPullParserException("expected equals sign (=) after version and not " + this.printable(ch), this, null);
        }
        ch = this.more();
        if ((ch = this.skipS(ch)) != '\'' && ch != '\"') {
            throw new XmlPullParserException("expected apostrophe (') or quotation mark (\") after version and not " + this.printable(ch), this, null);
        }
        char quotChar = ch;
        int versionStart = this.pos;
        ch = this.more();
        while (ch != quotChar) {
            if (!(ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch >= '0' && ch <= '9' || ch == '_' || ch == '.' || ch == ':' || ch == '-')) {
                throw new XmlPullParserException("<?xml version value expected to be in ([a-zA-Z0-9_.:] | '-') not " + this.printable(ch), this, null);
            }
            ch = this.more();
        }
        int versionEnd = this.pos - 1;
        this.parseXmlDeclWithVersion(versionStart, versionEnd);
        this.preventBufferCompaction = false;
    }

    protected void parseXmlDeclWithVersion(int versionStart, int versionEnd) throws XmlPullParserException, IOException {
        char quotChar;
        if (versionEnd - versionStart != 3 || this.buf[versionStart] != '1' || this.buf[versionStart + 1] != '.' || this.buf[versionStart + 2] != '0') {
            throw new XmlPullParserException("only 1.0 is supported as <?xml version not '" + this.printable(new String(this.buf, versionStart, versionEnd - versionStart)) + "'", this, null);
        }
        this.xmlDeclVersion = this.newString(this.buf, versionStart, versionEnd - versionStart);
        char ch = this.more();
        if ((ch = this.skipS(ch)) == 'e') {
            ch = this.more();
            ch = this.requireInput(ch, NCODING);
            if ((ch = this.skipS(ch)) != '=') {
                throw new XmlPullParserException("expected equals sign (=) after encoding and not " + this.printable(ch), this, null);
            }
            ch = this.more();
            if ((ch = this.skipS(ch)) != '\'' && ch != '\"') {
                throw new XmlPullParserException("expected apostrophe (') or quotation mark (\") after encoding and not " + this.printable(ch), this, null);
            }
            quotChar = ch;
            int encodingStart = this.pos;
            ch = this.more();
            if (!(ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z')) {
                throw new XmlPullParserException("<?xml encoding name expected to start with [A-Za-z] not " + this.printable(ch), this, null);
            }
            ch = this.more();
            while (ch != quotChar) {
                if (!(ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch >= '0' && ch <= '9' || ch == '.' || ch == '_' || ch == '-')) {
                    throw new XmlPullParserException("<?xml encoding value expected to be in ([A-Za-z0-9._] | '-') not " + this.printable(ch), this, null);
                }
                ch = this.more();
            }
            int encodingEnd = this.pos - 1;
            this.inputEncoding = this.newString(this.buf, encodingStart, encodingEnd - encodingStart);
            ch = this.more();
        }
        if ((ch = this.skipS(ch)) == 's') {
            ch = this.more();
            ch = this.requireInput(ch, TANDALONE);
            if ((ch = this.skipS(ch)) != '=') {
                throw new XmlPullParserException("expected equals sign (=) after standalone and not " + this.printable(ch), this, null);
            }
            ch = this.more();
            if ((ch = this.skipS(ch)) != '\'' && ch != '\"') {
                throw new XmlPullParserException("expected apostrophe (') or quotation mark (\") after encoding and not " + this.printable(ch), this, null);
            }
            quotChar = ch;
            int standaloneStart = this.pos;
            ch = this.more();
            if (ch == 'y') {
                ch = this.requireInput(ch, YES);
                this.xmlDeclStandalone = true;
            } else if (ch == 'n') {
                ch = this.requireInput(ch, NO);
                this.xmlDeclStandalone = false;
            } else {
                throw new XmlPullParserException("expected 'yes' or 'no' after standalone and not " + this.printable(ch), this, null);
            }
            if (ch != quotChar) {
                throw new XmlPullParserException("expected " + quotChar + " after standalone value not " + this.printable(ch), this, null);
            }
            ch = this.more();
        }
        if ((ch = this.skipS(ch)) != '?') {
            throw new XmlPullParserException("expected ?> as last part of <?xml not " + this.printable(ch), this, null);
        }
        ch = this.more();
        if (ch != '>') {
            throw new XmlPullParserException("expected ?> as last part of <?xml not " + this.printable(ch), this, null);
        }
    }

    protected void parseDocdecl() throws XmlPullParserException, IOException {
        char ch = this.more();
        if (ch != 'O') {
            throw new XmlPullParserException("expected <!DOCTYPE", this, null);
        }
        ch = this.more();
        if (ch != 'C') {
            throw new XmlPullParserException("expected <!DOCTYPE", this, null);
        }
        ch = this.more();
        if (ch != 'T') {
            throw new XmlPullParserException("expected <!DOCTYPE", this, null);
        }
        ch = this.more();
        if (ch != 'Y') {
            throw new XmlPullParserException("expected <!DOCTYPE", this, null);
        }
        ch = this.more();
        if (ch != 'P') {
            throw new XmlPullParserException("expected <!DOCTYPE", this, null);
        }
        ch = this.more();
        if (ch != 'E') {
            throw new XmlPullParserException("expected <!DOCTYPE", this, null);
        }
        this.posStart = this.pos;
        int bracketLevel = 0;
        boolean normalizeIgnorableWS = this.tokenize && !this.roundtripSupported;
        boolean normalizedCR = false;
        while (true) {
            if ((ch = this.more()) == '[') {
                ++bracketLevel;
            }
            if (ch == ']') {
                --bracketLevel;
            }
            if (ch == '>' && bracketLevel == 0) break;
            if (!normalizeIgnorableWS) continue;
            if (ch == '\r') {
                normalizedCR = true;
                if (!this.usePC) {
                    this.posEnd = this.pos - 1;
                    if (this.posEnd > this.posStart) {
                        this.joinPC();
                    } else {
                        this.usePC = true;
                        this.pcEnd = 0;
                        this.pcStart = 0;
                    }
                }
                if (this.pcEnd >= this.pc.length) {
                    this.ensurePC(this.pcEnd);
                }
                this.pc[this.pcEnd++] = 10;
                continue;
            }
            if (ch == '\n') {
                if (!normalizedCR && this.usePC) {
                    if (this.pcEnd >= this.pc.length) {
                        this.ensurePC(this.pcEnd);
                    }
                    this.pc[this.pcEnd++] = 10;
                }
                normalizedCR = false;
                continue;
            }
            if (this.usePC) {
                if (this.pcEnd >= this.pc.length) {
                    this.ensurePC(this.pcEnd);
                }
                this.pc[this.pcEnd++] = ch;
            }
            normalizedCR = false;
        }
        this.posEnd = this.pos - 1;
    }

    protected void parseCDSect(boolean hadCharData) throws XmlPullParserException, IOException {
        char ch = this.more();
        if (ch != 'C') {
            throw new XmlPullParserException("expected <[CDATA[ for comment start", this, null);
        }
        ch = this.more();
        if (ch != 'D') {
            throw new XmlPullParserException("expected <[CDATA[ for comment start", this, null);
        }
        ch = this.more();
        if (ch != 'A') {
            throw new XmlPullParserException("expected <[CDATA[ for comment start", this, null);
        }
        ch = this.more();
        if (ch != 'T') {
            throw new XmlPullParserException("expected <[CDATA[ for comment start", this, null);
        }
        ch = this.more();
        if (ch != 'A') {
            throw new XmlPullParserException("expected <[CDATA[ for comment start", this, null);
        }
        ch = this.more();
        if (ch != '[') {
            throw new XmlPullParserException("expected <![CDATA[ for comment start", this, null);
        }
        int cdStart = this.pos + this.bufAbsoluteStart;
        int curLine = this.lineNumber;
        int curColumn = this.columnNumber;
        boolean normalizeInput = !this.tokenize || !this.roundtripSupported;
        try {
            if (normalizeInput && hadCharData && !this.usePC) {
                if (this.posEnd > this.posStart) {
                    this.joinPC();
                } else {
                    this.usePC = true;
                    this.pcEnd = 0;
                    this.pcStart = 0;
                }
            }
            boolean seenBracket = false;
            boolean seenBracketBracket = false;
            boolean normalizedCR = false;
            while (true) {
                if ((ch = this.more()) == ']') {
                    if (!seenBracket) {
                        seenBracket = true;
                    } else {
                        seenBracketBracket = true;
                    }
                } else if (ch == '>') {
                    if (seenBracket && seenBracketBracket) break;
                    seenBracketBracket = false;
                    seenBracket = false;
                } else if (seenBracket) {
                    seenBracket = false;
                }
                if (!normalizeInput) continue;
                if (ch == '\r') {
                    normalizedCR = true;
                    this.posStart = cdStart - this.bufAbsoluteStart;
                    this.posEnd = this.pos - 1;
                    if (!this.usePC) {
                        if (this.posEnd > this.posStart) {
                            this.joinPC();
                        } else {
                            this.usePC = true;
                            this.pcEnd = 0;
                            this.pcStart = 0;
                        }
                    }
                    if (this.pcEnd >= this.pc.length) {
                        this.ensurePC(this.pcEnd);
                    }
                    this.pc[this.pcEnd++] = 10;
                    continue;
                }
                if (ch == '\n') {
                    if (!normalizedCR && this.usePC) {
                        if (this.pcEnd >= this.pc.length) {
                            this.ensurePC(this.pcEnd);
                        }
                        this.pc[this.pcEnd++] = 10;
                    }
                    normalizedCR = false;
                    continue;
                }
                if (this.usePC) {
                    if (this.pcEnd >= this.pc.length) {
                        this.ensurePC(this.pcEnd);
                    }
                    this.pc[this.pcEnd++] = ch;
                }
                normalizedCR = false;
            }
        }
        catch (EOFException ex) {
            throw new XmlPullParserException("CDATA section started on line " + curLine + " and column " + curColumn + " was not closed", this, ex);
        }
        if (normalizeInput && this.usePC) {
            this.pcEnd -= 2;
        }
        this.posStart = cdStart - this.bufAbsoluteStart;
        this.posEnd = this.pos - 3;
    }

    protected void fillBuf() throws IOException, XmlPullParserException {
        int len;
        int ret;
        if (this.reader == null) {
            throw new XmlPullParserException("reader must be set before parsing is started");
        }
        if (this.bufEnd > this.bufSoftLimit) {
            boolean compact = this.bufStart > this.bufSoftLimit;
            boolean expand = false;
            if (this.preventBufferCompaction) {
                compact = false;
                expand = true;
            } else if (!compact) {
                if (this.bufStart < this.buf.length / 2) {
                    expand = true;
                } else {
                    compact = true;
                }
            }
            if (compact) {
                System.arraycopy(this.buf, this.bufStart, this.buf, 0, this.bufEnd - this.bufStart);
            } else if (expand) {
                int newSize = 2 * this.buf.length;
                char[] newBuf = new char[newSize];
                System.arraycopy(this.buf, this.bufStart, newBuf, 0, this.bufEnd - this.bufStart);
                this.buf = newBuf;
                if (this.bufLoadFactor > 0) {
                    this.bufSoftLimit = this.bufLoadFactor * this.buf.length / 100;
                }
            } else {
                throw new XmlPullParserException("internal error in fillBuffer()");
            }
            this.bufEnd -= this.bufStart;
            this.pos -= this.bufStart;
            this.posStart -= this.bufStart;
            this.posEnd -= this.bufStart;
            this.bufAbsoluteStart += this.bufStart;
            this.bufStart = 0;
        }
        if ((ret = this.reader.read(this.buf, this.bufEnd, len = this.buf.length - this.bufEnd > 8192 ? 8192 : this.buf.length - this.bufEnd)) > 0) {
            this.bufEnd += ret;
            return;
        }
        if (ret == -1) {
            if (this.bufAbsoluteStart == 0 && this.pos == 0) {
                throw new EOFException("input contained no data");
            }
            if (this.seenRoot && this.depth == 0) {
                this.reachedEnd = true;
                return;
            }
            StringBuilder expectedTagStack = new StringBuilder();
            if (this.depth > 0) {
                if (this.elRawName == null || this.elRawName[this.depth] == null) {
                    String tagName = new String(this.buf, this.posStart + 1, this.pos - this.posStart - 1);
                    expectedTagStack.append(" - expected the opening tag <").append(tagName).append("...>");
                } else {
                    String tagName;
                    int i;
                    expectedTagStack.append(" - expected end tag");
                    if (this.depth > 1) {
                        expectedTagStack.append("s");
                    }
                    expectedTagStack.append(" ");
                    for (i = this.depth; i > 0; --i) {
                        if (this.elRawName == null || this.elRawName[i] == null) {
                            tagName = new String(this.buf, this.posStart + 1, this.pos - this.posStart - 1);
                            expectedTagStack.append(" - expected the opening tag <").append(tagName).append("...>");
                            continue;
                        }
                        tagName = new String(this.elRawName[i], 0, this.elRawNameEnd[i]);
                        expectedTagStack.append("</").append(tagName).append('>');
                    }
                    expectedTagStack.append(" to close");
                    for (i = this.depth; i > 0; --i) {
                        if (i != this.depth) {
                            expectedTagStack.append(" and");
                        }
                        if (this.elRawName == null || this.elRawName[i] == null) {
                            tagName = new String(this.buf, this.posStart + 1, this.pos - this.posStart - 1);
                            expectedTagStack.append(" start tag <").append(tagName).append(">");
                            expectedTagStack.append(" from line ").append(this.elRawNameLine[i]);
                            continue;
                        }
                        tagName = new String(this.elRawName[i], 0, this.elRawNameEnd[i]);
                        expectedTagStack.append(" start tag <").append(tagName).append(">");
                        expectedTagStack.append(" from line ").append(this.elRawNameLine[i]);
                    }
                    expectedTagStack.append(", parser stopped on");
                }
            }
            throw new EOFException("no more data available" + expectedTagStack.toString() + this.getPositionDescription());
        }
        throw new IOException("error reading input, returned " + ret);
    }

    protected char more() throws IOException, XmlPullParserException {
        char ch;
        if (this.pos >= this.bufEnd) {
            this.fillBuf();
            if (this.reachedEnd) {
                return '\uffff';
            }
        }
        if ((ch = this.buf[this.pos++]) == '\n') {
            ++this.lineNumber;
            this.columnNumber = 1;
        } else {
            ++this.columnNumber;
        }
        return ch;
    }

    protected void ensurePC(int end) {
        int newSize = end > 8192 ? 2 * end : 16384;
        char[] newPC = new char[newSize];
        System.arraycopy(this.pc, 0, newPC, 0, this.pcEnd);
        this.pc = newPC;
    }

    protected void joinPC() {
        int len = this.posEnd - this.posStart;
        int newEnd = this.pcEnd + len + 1;
        if (newEnd >= this.pc.length) {
            this.ensurePC(newEnd);
        }
        System.arraycopy(this.buf, this.posStart, this.pc, this.pcEnd, len);
        this.pcEnd += len;
        this.usePC = true;
    }

    protected char requireInput(char ch, char[] input) throws XmlPullParserException, IOException {
        for (char anInput : input) {
            if (ch != anInput) {
                throw new XmlPullParserException("expected " + this.printable(anInput) + " in " + new String(input) + " and not " + this.printable(ch), this, null);
            }
            ch = this.more();
        }
        return ch;
    }

    protected char requireNextS() throws XmlPullParserException, IOException {
        char ch = this.more();
        if (!this.isS(ch)) {
            throw new XmlPullParserException("white space is required and not " + this.printable(ch), this, null);
        }
        return this.skipS(ch);
    }

    protected char skipS(char ch) throws XmlPullParserException, IOException {
        while (this.isS(ch)) {
            ch = this.more();
        }
        return ch;
    }

    private static final void setName(char ch) {
        MXParser.lookupNameChar[ch] = true;
    }

    private static final void setNameStart(char ch) {
        MXParser.lookupNameStartChar[ch] = true;
        MXParser.setName(ch);
    }

    protected boolean isNameStartChar(char ch) {
        return ch < '\u0400' && lookupNameStartChar[ch] || ch >= '\u0400' && ch <= '\u2027' || ch >= '\u202a' && ch <= '\u218f' || ch >= '\u2800' && ch <= '\uffef';
    }

    protected boolean isNameChar(char ch) {
        return ch < '\u0400' && lookupNameChar[ch] || ch >= '\u0400' && ch <= '\u2027' || ch >= '\u202a' && ch <= '\u218f' || ch >= '\u2800' && ch <= '\uffef';
    }

    protected boolean isS(char ch) {
        return ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t';
    }

    protected String printable(char ch) {
        if (ch == '\n') {
            return "\\n";
        }
        if (ch == '\r') {
            return "\\r";
        }
        if (ch == '\t') {
            return "\\t";
        }
        if (ch == '\'') {
            return "\\'";
        }
        if (ch > '\u007f' || ch < ' ') {
            return "\\u" + Integer.toHexString(ch);
        }
        return "" + ch;
    }

    protected String printable(String s) {
        if (s == null) {
            return null;
        }
        int sLen = s.length();
        StringBuilder buf = new StringBuilder(sLen + 10);
        for (int i = 0; i < sLen; ++i) {
            buf.append(this.printable(s.charAt(i)));
        }
        s = buf.toString();
        return s;
    }

    private static int toCodePoint(char high, char low) {
        int h = (high & 0x3FF) << 10;
        int l = low & 0x3FF;
        return (h | l) + 65536;
    }

    private static boolean isHighSurrogate(char ch) {
        return '\ud800' <= ch && '\udbff' >= ch;
    }

    private static boolean isValidCodePoint(int codePoint) {
        return codePoint == 9 || codePoint == 10 || codePoint == 13 || 32 <= codePoint && codePoint <= 55295 || 57344 <= codePoint && codePoint <= 65533 || 65536 <= codePoint && codePoint <= 0x10FFFF;
    }

    private static boolean isSupplementaryCodePoint(int codePoint) {
        return 65536 <= codePoint && 0x10FFFF >= codePoint;
    }

    public static char[] toChars(int codePoint) {
        if (!MXParser.isValidCodePoint(codePoint)) {
            throw new IllegalArgumentException();
        }
        if (MXParser.isSupplementaryCodePoint(codePoint)) {
            int cpPrime = codePoint - 65536;
            int high = 0xD800 | cpPrime >> 10 & 0x3FF;
            int low = 0xDC00 | cpPrime & 0x3FF;
            return new char[]{(char)high, (char)low};
        }
        return new char[]{(char)codePoint};
    }

    static {
        char ch;
        VERSION = "version".toCharArray();
        NCODING = "ncoding".toCharArray();
        TANDALONE = "tandalone".toCharArray();
        YES = "yes".toCharArray();
        NO = "no".toCharArray();
        lookupNameStartChar = new boolean[1024];
        lookupNameChar = new boolean[1024];
        MXParser.setNameStart(':');
        for (ch = 'A'; ch <= 'Z'; ch = (char)((char)(ch + 1))) {
            MXParser.setNameStart(ch);
        }
        MXParser.setNameStart('_');
        for (ch = 'a'; ch <= 'z'; ch = (char)((char)(ch + 1))) {
            MXParser.setNameStart(ch);
        }
        for (ch = '\u00c0'; ch <= '\u02ff'; ch = (char)((char)(ch + 1))) {
            MXParser.setNameStart(ch);
        }
        for (ch = '\u0370'; ch <= '\u037d'; ch = (char)((char)(ch + 1))) {
            MXParser.setNameStart(ch);
        }
        for (ch = '\u037f'; ch < '\u0400'; ch = (char)((char)(ch + '\u0001'))) {
            MXParser.setNameStart(ch);
        }
        MXParser.setName('-');
        MXParser.setName('.');
        for (ch = '0'; ch <= '9'; ch = (char)((char)(ch + 1))) {
            MXParser.setName(ch);
        }
        MXParser.setName('\u00b7');
        for (ch = '\u0300'; ch <= '\u036f'; ch = (char)(ch + '\u0001')) {
            MXParser.setName(ch);
        }
    }
}
