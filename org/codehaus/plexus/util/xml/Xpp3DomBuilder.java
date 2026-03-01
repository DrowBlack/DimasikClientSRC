package org.codehaus.plexus.util.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.pull.MXParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class Xpp3DomBuilder {
    private static final boolean DEFAULT_TRIM = true;

    public static Xpp3Dom build(Reader reader) throws XmlPullParserException, IOException {
        return Xpp3DomBuilder.build(reader, null);
    }

    public static Xpp3Dom build(Reader reader, InputLocationBuilder locationBuilder) throws XmlPullParserException, IOException {
        return Xpp3DomBuilder.build(reader, true, locationBuilder);
    }

    public static Xpp3Dom build(InputStream is, String encoding) throws XmlPullParserException, IOException {
        return Xpp3DomBuilder.build(is, encoding, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Xpp3Dom build(InputStream is, String encoding, boolean trim) throws XmlPullParserException, IOException {
        try {
            MXParser parser = new MXParser();
            parser.setInput(is, encoding);
            Xpp3Dom xpp3Dom = Xpp3DomBuilder.build(parser, trim);
            is.close();
            is = null;
            Xpp3Dom xpp3Dom2 = xpp3Dom;
            return xpp3Dom2;
        }
        finally {
            IOUtil.close(is);
        }
    }

    public static Xpp3Dom build(Reader reader, boolean trim) throws XmlPullParserException, IOException {
        return Xpp3DomBuilder.build(reader, trim, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Xpp3Dom build(Reader reader, boolean trim, InputLocationBuilder locationBuilder) throws XmlPullParserException, IOException {
        try {
            MXParser parser = new MXParser();
            parser.setInput(reader);
            Xpp3Dom xpp3Dom = Xpp3DomBuilder.build(parser, trim, locationBuilder);
            reader.close();
            reader = null;
            Xpp3Dom xpp3Dom2 = xpp3Dom;
            return xpp3Dom2;
        }
        finally {
            IOUtil.close(reader);
        }
    }

    public static Xpp3Dom build(XmlPullParser parser) throws XmlPullParserException, IOException {
        return Xpp3DomBuilder.build(parser, true);
    }

    public static Xpp3Dom build(XmlPullParser parser, boolean trim) throws XmlPullParserException, IOException {
        return Xpp3DomBuilder.build(parser, trim, null);
    }

    public static Xpp3Dom build(XmlPullParser parser, boolean trim, InputLocationBuilder locationBuilder) throws XmlPullParserException, IOException {
        ArrayList<Xpp3Dom> elements = new ArrayList<Xpp3Dom>();
        ArrayList<StringBuilder> values = new ArrayList<StringBuilder>();
        int eventType = parser.getEventType();
        boolean spacePreserve = false;
        while (eventType != 1) {
            if (eventType == 2) {
                int depth;
                spacePreserve = false;
                String rawName = parser.getName();
                Xpp3Dom childConfiguration = new Xpp3Dom(rawName);
                if (locationBuilder != null) {
                    childConfiguration.setInputLocation(locationBuilder.toInputLocation(parser));
                }
                if ((depth = elements.size()) > 0) {
                    Xpp3Dom parent = (Xpp3Dom)elements.get(depth - 1);
                    parent.addChild(childConfiguration);
                }
                elements.add(childConfiguration);
                if (parser.isEmptyElementTag()) {
                    values.add(null);
                } else {
                    values.add(new StringBuilder());
                }
                int attributesSize = parser.getAttributeCount();
                for (int i = 0; i < attributesSize; ++i) {
                    String name = parser.getAttributeName(i);
                    String value = parser.getAttributeValue(i);
                    childConfiguration.setAttribute(name, value);
                    spacePreserve = spacePreserve || "xml:space".equals(name) && "preserve".equals(value);
                }
            } else if (eventType == 4) {
                int depth = values.size() - 1;
                StringBuilder valueBuffer = (StringBuilder)values.get(depth);
                String text = parser.getText();
                if (trim && !spacePreserve) {
                    text = text.trim();
                }
                valueBuffer.append(text);
            } else if (eventType == 3) {
                int depth = elements.size() - 1;
                Xpp3Dom finishedConfiguration = (Xpp3Dom)elements.remove(depth);
                Object accumulatedValue = values.remove(depth);
                if (finishedConfiguration.getChildCount() == 0) {
                    if (accumulatedValue == null) {
                        finishedConfiguration.setValue(null);
                    } else {
                        finishedConfiguration.setValue(accumulatedValue.toString());
                    }
                }
                if (depth == 0) {
                    return finishedConfiguration;
                }
            }
            eventType = parser.next();
        }
        throw new IllegalStateException("End of document found before returning to 0 depth");
    }

    public static interface InputLocationBuilder {
        public Object toInputLocation(XmlPullParser var1);
    }
}
