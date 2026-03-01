package org.codehaus.plexus.util.xml;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.codehaus.plexus.util.xml.XmlStreamReaderException;

public class XmlReader
extends Reader {
    private static final int BUFFER_SIZE = 4096;
    private static final String UTF_8 = "UTF-8";
    private static final String US_ASCII = "US-ASCII";
    private static final String UTF_16BE = "UTF-16BE";
    private static final String UTF_16LE = "UTF-16LE";
    private static final String UTF_16 = "UTF-16";
    private static final String EBCDIC = "CP1047";
    private static String _staticDefaultEncoding = null;
    private Reader _reader;
    private String _encoding;
    private String _defaultEncoding;
    private static final Pattern CHARSET_PATTERN = Pattern.compile("charset=([.[^; ]]*)");
    static final Pattern ENCODING_PATTERN = Pattern.compile("<\\?xml.*encoding[\\s]*=[\\s]*((?:\".[^\"]*\")|(?:'.[^']*'))", 8);
    private static final MessageFormat RAW_EX_1 = new MessageFormat("Invalid encoding, BOM [{0}] XML guess [{1}] XML prolog [{2}] encoding mismatch");
    private static final MessageFormat RAW_EX_2 = new MessageFormat("Invalid encoding, BOM [{0}] XML guess [{1}] XML prolog [{2}] unknown BOM");
    private static final MessageFormat HTTP_EX_1 = new MessageFormat("Invalid encoding, CT-MIME [{0}] CT-Enc [{1}] BOM [{2}] XML guess [{3}] XML prolog [{4}], BOM must be NULL");
    private static final MessageFormat HTTP_EX_2 = new MessageFormat("Invalid encoding, CT-MIME [{0}] CT-Enc [{1}] BOM [{2}] XML guess [{3}] XML prolog [{4}], encoding mismatch");
    private static final MessageFormat HTTP_EX_3 = new MessageFormat("Invalid encoding, CT-MIME [{0}] CT-Enc [{1}] BOM [{2}] XML guess [{3}] XML prolog [{4}], Invalid MIME");

    public static void setDefaultEncoding(String encoding) {
        _staticDefaultEncoding = encoding;
    }

    public static String getDefaultEncoding() {
        return _staticDefaultEncoding;
    }

    public XmlReader(File file) throws IOException {
        this(new FileInputStream(file));
    }

    public XmlReader(InputStream is) throws IOException {
        this(is, true);
    }

    public XmlReader(InputStream is, boolean lenient) throws IOException, XmlStreamReaderException {
        this._defaultEncoding = _staticDefaultEncoding;
        try {
            this.doRawStream(is, lenient);
        }
        catch (XmlStreamReaderException ex) {
            if (!lenient) {
                throw ex;
            }
            this.doLenientDetection(null, ex);
        }
    }

    public XmlReader(URL url) throws IOException {
        this(url.openConnection());
    }

    public XmlReader(URLConnection conn) throws IOException {
        this._defaultEncoding = _staticDefaultEncoding;
        boolean lenient = true;
        if (conn instanceof HttpURLConnection) {
            try {
                this.doHttpStream(conn.getInputStream(), conn.getContentType(), lenient);
            }
            catch (XmlStreamReaderException ex) {
                this.doLenientDetection(conn.getContentType(), ex);
            }
        } else if (conn.getContentType() != null) {
            try {
                this.doHttpStream(conn.getInputStream(), conn.getContentType(), lenient);
            }
            catch (XmlStreamReaderException ex) {
                this.doLenientDetection(conn.getContentType(), ex);
            }
        } else {
            try {
                this.doRawStream(conn.getInputStream(), lenient);
            }
            catch (XmlStreamReaderException ex) {
                this.doLenientDetection(null, ex);
            }
        }
    }

    public XmlReader(InputStream is, String httpContentType) throws IOException {
        this(is, httpContentType, true);
    }

    public XmlReader(InputStream is, String httpContentType, boolean lenient, String defaultEncoding) throws IOException, XmlStreamReaderException {
        this._defaultEncoding = defaultEncoding == null ? _staticDefaultEncoding : defaultEncoding;
        try {
            this.doHttpStream(is, httpContentType, lenient);
        }
        catch (XmlStreamReaderException ex) {
            if (!lenient) {
                throw ex;
            }
            this.doLenientDetection(httpContentType, ex);
        }
    }

    public XmlReader(InputStream is, String httpContentType, boolean lenient) throws IOException, XmlStreamReaderException {
        this(is, httpContentType, lenient, null);
    }

    private void doLenientDetection(String httpContentType, XmlStreamReaderException ex) throws IOException {
        if (httpContentType != null && httpContentType.startsWith("text/html")) {
            httpContentType = httpContentType.substring("text/html".length());
            httpContentType = "text/xml" + httpContentType;
            try {
                this.doHttpStream(ex.getInputStream(), httpContentType, true);
                ex = null;
            }
            catch (XmlStreamReaderException ex2) {
                ex = ex2;
            }
        }
        if (ex != null) {
            String encoding = ex.getXmlEncoding();
            if (encoding == null) {
                encoding = ex.getContentTypeEncoding();
            }
            if (encoding == null) {
                encoding = this._defaultEncoding == null ? UTF_8 : this._defaultEncoding;
            }
            this.prepareReader(ex.getInputStream(), encoding);
        }
    }

    public String getEncoding() {
        return this._encoding;
    }

    @Override
    public int read(char[] buf, int offset, int len) throws IOException {
        return this._reader.read(buf, offset, len);
    }

    @Override
    public void close() throws IOException {
        this._reader.close();
    }

    private void doRawStream(InputStream is, boolean lenient) throws IOException {
        BufferedInputStream pis = new BufferedInputStream(is, 4096);
        String bomEnc = XmlReader.getBOMEncoding(pis);
        String xmlGuessEnc = XmlReader.getXMLGuessEncoding(pis);
        String xmlEnc = XmlReader.getXmlProlog(pis, xmlGuessEnc);
        String encoding = this.calculateRawEncoding(bomEnc, xmlGuessEnc, xmlEnc, pis);
        this.prepareReader(pis, encoding);
    }

    private void doHttpStream(InputStream is, String httpContentType, boolean lenient) throws IOException {
        BufferedInputStream pis = new BufferedInputStream(is, 4096);
        String cTMime = XmlReader.getContentTypeMime(httpContentType);
        String cTEnc = XmlReader.getContentTypeEncoding(httpContentType);
        String bomEnc = XmlReader.getBOMEncoding(pis);
        String xmlGuessEnc = XmlReader.getXMLGuessEncoding(pis);
        String xmlEnc = XmlReader.getXmlProlog(pis, xmlGuessEnc);
        String encoding = this.calculateHttpEncoding(cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc, pis, lenient);
        this.prepareReader(pis, encoding);
    }

    private void prepareReader(InputStream is, String encoding) throws IOException {
        this._reader = new InputStreamReader(is, encoding);
        this._encoding = encoding;
    }

    private String calculateRawEncoding(String bomEnc, String xmlGuessEnc, String xmlEnc, InputStream is) throws IOException {
        String encoding;
        if (bomEnc == null) {
            encoding = xmlGuessEnc == null || xmlEnc == null ? (this._defaultEncoding == null ? UTF_8 : this._defaultEncoding) : (xmlEnc.equals(UTF_16) && (xmlGuessEnc.equals(UTF_16BE) || xmlGuessEnc.equals(UTF_16LE)) ? xmlGuessEnc : xmlEnc);
        } else if (bomEnc.equals(UTF_8)) {
            if (xmlGuessEnc != null && !xmlGuessEnc.equals(UTF_8)) {
                throw new XmlStreamReaderException(RAW_EX_1.format(new Object[]{bomEnc, xmlGuessEnc, xmlEnc}), bomEnc, xmlGuessEnc, xmlEnc, is);
            }
            if (xmlEnc != null && !xmlEnc.equals(UTF_8)) {
                throw new XmlStreamReaderException(RAW_EX_1.format(new Object[]{bomEnc, xmlGuessEnc, xmlEnc}), bomEnc, xmlGuessEnc, xmlEnc, is);
            }
            encoding = UTF_8;
        } else if (bomEnc.equals(UTF_16BE) || bomEnc.equals(UTF_16LE)) {
            if (xmlGuessEnc != null && !xmlGuessEnc.equals(bomEnc)) {
                throw new IOException(RAW_EX_1.format(new Object[]{bomEnc, xmlGuessEnc, xmlEnc}));
            }
            if (xmlEnc != null && !xmlEnc.equals(UTF_16) && !xmlEnc.equals(bomEnc)) {
                throw new XmlStreamReaderException(RAW_EX_1.format(new Object[]{bomEnc, xmlGuessEnc, xmlEnc}), bomEnc, xmlGuessEnc, xmlEnc, is);
            }
            encoding = bomEnc;
        } else {
            throw new XmlStreamReaderException(RAW_EX_2.format(new Object[]{bomEnc, xmlGuessEnc, xmlEnc}), bomEnc, xmlGuessEnc, xmlEnc, is);
        }
        return encoding;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private String calculateHttpEncoding(String cTMime, String cTEnc, String bomEnc, String xmlGuessEnc, String xmlEnc, InputStream is, boolean lenient) throws IOException {
        if (lenient & xmlEnc != null) {
            return xmlEnc;
        }
        boolean appXml = XmlReader.isAppXml(cTMime);
        boolean textXml = XmlReader.isTextXml(cTMime);
        if (!appXml && !textXml) throw new XmlStreamReaderException(HTTP_EX_3.format(new Object[]{cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc}), cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc, is);
        if (cTEnc == null) {
            if (appXml) {
                return this.calculateRawEncoding(bomEnc, xmlGuessEnc, xmlEnc, is);
            }
            if (this._defaultEncoding == null) {
                return US_ASCII;
            }
            String string = this._defaultEncoding;
            return string;
        }
        if (bomEnc != null && (cTEnc.equals(UTF_16BE) || cTEnc.equals(UTF_16LE))) {
            throw new XmlStreamReaderException(HTTP_EX_1.format(new Object[]{cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc}), cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc, is);
        }
        if (!cTEnc.equals(UTF_16)) return cTEnc;
        if (bomEnc == null || !bomEnc.startsWith(UTF_16)) throw new XmlStreamReaderException(HTTP_EX_2.format(new Object[]{cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc}), cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc, is);
        return bomEnc;
    }

    private static String getContentTypeMime(String httpContentType) {
        String mime = null;
        if (httpContentType != null) {
            int i = httpContentType.indexOf(";");
            mime = (i == -1 ? httpContentType : httpContentType.substring(0, i)).trim();
        }
        return mime;
    }

    private static String getContentTypeEncoding(String httpContentType) {
        int i;
        String encoding = null;
        if (httpContentType != null && (i = httpContentType.indexOf(";")) > -1) {
            String postMime = httpContentType.substring(i + 1);
            Matcher m = CHARSET_PATTERN.matcher(postMime);
            encoding = m.find() ? m.group(1) : null;
            encoding = encoding != null ? encoding.toUpperCase(Locale.ENGLISH) : null;
        }
        return encoding;
    }

    private static String getBOMEncoding(BufferedInputStream is) throws IOException {
        String encoding = null;
        int[] bytes = new int[3];
        is.mark(3);
        bytes[0] = is.read();
        bytes[1] = is.read();
        bytes[2] = is.read();
        if (bytes[0] == 254 && bytes[1] == 255) {
            encoding = UTF_16BE;
            is.reset();
            is.read();
            is.read();
        } else if (bytes[0] == 255 && bytes[1] == 254) {
            encoding = UTF_16LE;
            is.reset();
            is.read();
            is.read();
        } else if (bytes[0] == 239 && bytes[1] == 187 && bytes[2] == 191) {
            encoding = UTF_8;
        } else {
            is.reset();
        }
        return encoding;
    }

    private static String getXMLGuessEncoding(BufferedInputStream is) throws IOException {
        String encoding = null;
        int[] bytes = new int[4];
        is.mark(4);
        bytes[0] = is.read();
        bytes[1] = is.read();
        bytes[2] = is.read();
        bytes[3] = is.read();
        is.reset();
        if (bytes[0] == 0 && bytes[1] == 60 && bytes[2] == 0 && bytes[3] == 63) {
            encoding = UTF_16BE;
        } else if (bytes[0] == 60 && bytes[1] == 0 && bytes[2] == 63 && bytes[3] == 0) {
            encoding = UTF_16LE;
        } else if (bytes[0] == 60 && bytes[1] == 63 && bytes[2] == 120 && bytes[3] == 109) {
            encoding = UTF_8;
        } else if (bytes[0] == 76 && bytes[1] == 111 && bytes[2] == 167 && bytes[3] == 148) {
            encoding = EBCDIC;
        }
        return encoding;
    }

    private static String getXmlProlog(BufferedInputStream is, String guessedEnc) throws IOException {
        String encoding = null;
        if (guessedEnc != null) {
            byte[] bytes = new byte[4096];
            is.mark(4096);
            int offset = 0;
            int max = 4096;
            int c = is.read(bytes, offset, max);
            int firstGT = -1;
            String xmlProlog = null;
            while (c != -1 && firstGT == -1 && offset < 4096) {
                c = is.read(bytes, offset += c, max -= c);
                xmlProlog = new String(bytes, 0, offset, guessedEnc);
                firstGT = xmlProlog.indexOf(62);
            }
            if (firstGT == -1) {
                if (c == -1) {
                    throw new IOException("Unexpected end of XML stream");
                }
                throw new IOException("XML prolog or ROOT element not found on first " + offset + " bytes");
            }
            int bytesRead = offset;
            if (bytesRead > 0) {
                is.reset();
                BufferedReader bReader = new BufferedReader(new StringReader(xmlProlog.substring(0, firstGT + 1)));
                StringBuilder prolog = new StringBuilder();
                String line = bReader.readLine();
                while (line != null) {
                    prolog.append(line);
                    line = bReader.readLine();
                }
                Matcher m = ENCODING_PATTERN.matcher(prolog);
                if (m.find()) {
                    encoding = m.group(1).toUpperCase(Locale.ENGLISH);
                    encoding = encoding.substring(1, encoding.length() - 1);
                }
            }
        }
        return encoding;
    }

    private static boolean isAppXml(String mime) {
        return mime != null && (mime.equals("application/xml") || mime.equals("application/xml-dtd") || mime.equals("application/xml-external-parsed-entity") || mime.startsWith("application/") && mime.endsWith("+xml"));
    }

    private static boolean isTextXml(String mime) {
        return mime != null && (mime.equals("text/xml") || mime.equals("text/xml-external-parsed-entity") || mime.startsWith("text/") && mime.endsWith("+xml"));
    }
}
