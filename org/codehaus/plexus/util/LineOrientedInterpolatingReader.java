package org.codehaus.plexus.util;

import java.io.FilterReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.codehaus.plexus.util.reflection.Reflector;
import org.codehaus.plexus.util.reflection.ReflectorException;

public class LineOrientedInterpolatingReader
extends FilterReader {
    public static final String DEFAULT_START_DELIM = "${";
    public static final String DEFAULT_END_DELIM = "}";
    public static final String DEFAULT_ESCAPE_SEQ = "\\";
    private static final char CARRIAGE_RETURN_CHAR = '\r';
    private static final char NEWLINE_CHAR = '\n';
    private final PushbackReader pushbackReader;
    private final Map<String, Object> context;
    private final String startDelim;
    private final String endDelim;
    private final String escapeSeq;
    private final int minExpressionSize;
    private final Reflector reflector;
    private int lineIdx = -1;
    private String line;

    public LineOrientedInterpolatingReader(Reader reader, Map<String, ?> context, String startDelim, String endDelim, String escapeSeq) {
        super(reader);
        this.startDelim = startDelim;
        this.endDelim = endDelim;
        this.escapeSeq = escapeSeq;
        this.minExpressionSize = startDelim.length() + endDelim.length() + 1;
        this.context = Collections.unmodifiableMap(context);
        this.reflector = new Reflector();
        this.pushbackReader = reader instanceof PushbackReader ? (PushbackReader)reader : new PushbackReader(reader, 1);
    }

    public LineOrientedInterpolatingReader(Reader reader, Map<String, ?> context, String startDelim, String endDelim) {
        this(reader, context, startDelim, endDelim, DEFAULT_ESCAPE_SEQ);
    }

    public LineOrientedInterpolatingReader(Reader reader, Map<String, ?> context) {
        this(reader, context, DEFAULT_START_DELIM, DEFAULT_END_DELIM, DEFAULT_ESCAPE_SEQ);
    }

    @Override
    public int read() throws IOException {
        if (this.line == null || this.lineIdx >= this.line.length()) {
            this.readAndInterpolateLine();
        }
        int next = -1;
        if (this.line != null && this.lineIdx < this.line.length()) {
            next = this.line.charAt(this.lineIdx++);
        }
        return next;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int next;
        int fillCount = 0;
        for (int i = off; i < off + len && (next = this.read()) > -1; ++i) {
            cbuf[i] = (char)next;
            ++fillCount;
        }
        if (fillCount == 0) {
            fillCount = -1;
        }
        return fillCount;
    }

    @Override
    public long skip(long n) throws IOException {
        int next;
        long skipCount = 0L;
        for (long i = 0L; i < n && (next = this.read()) >= 0; ++i) {
            ++skipCount;
        }
        return skipCount;
    }

    private void readAndInterpolateLine() throws IOException {
        String rawLine = this.readLine();
        if (rawLine != null) {
            Set<String> expressions = this.parseForExpressions(rawLine);
            Map<String, Object> evaluatedExpressions = this.evaluateExpressions(expressions);
            String interpolated = this.replaceWithInterpolatedValues(rawLine, evaluatedExpressions);
            if (interpolated != null && interpolated.length() > 0) {
                this.line = interpolated;
                this.lineIdx = 0;
            }
        } else {
            this.line = null;
            this.lineIdx = -1;
        }
    }

    private String readLine() throws IOException {
        int next;
        StringBuilder lineBuffer = new StringBuilder(40);
        boolean lastWasCR = false;
        while ((next = this.pushbackReader.read()) > -1) {
            char c = (char)next;
            if (c == '\r') {
                lastWasCR = true;
                lineBuffer.append(c);
                continue;
            }
            if (c == '\n') {
                lineBuffer.append(c);
                break;
            }
            if (lastWasCR) {
                this.pushbackReader.unread(c);
                break;
            }
            lineBuffer.append(c);
        }
        if (lineBuffer.length() < 1) {
            return null;
        }
        return lineBuffer.toString();
    }

    private String replaceWithInterpolatedValues(String rawLine, Map<String, Object> evaluatedExpressions) {
        String result = rawLine;
        Iterator<Map.Entry<String, Object>> iterator = evaluatedExpressions.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> o;
            Map.Entry<String, Object> entry = o = iterator.next();
            String expression = entry.getKey();
            String value = String.valueOf(entry.getValue());
            result = this.findAndReplaceUnlessEscaped(result, expression, value);
        }
        return result;
    }

    private Map<String, Object> evaluateExpressions(Set<String> expressions) {
        TreeMap<String, Object> evaluated = new TreeMap<String, Object>();
        for (String expression : expressions) {
            Object value;
            String rawExpression = expression;
            String realExpression = rawExpression.substring(this.startDelim.length(), rawExpression.length() - this.endDelim.length());
            String[] parts = realExpression.split("\\.");
            if (parts.length <= 0 || (value = this.context.get(parts[0])) == null) continue;
            for (int i = 1; i < parts.length; ++i) {
                try {
                    value = this.reflector.getObjectProperty(value, parts[i]);
                    if (value != null) continue;
                }
                catch (ReflectorException e) {
                    e.printStackTrace();
                }
                break;
            }
            evaluated.put(rawExpression, value);
        }
        return evaluated;
    }

    private Set<String> parseForExpressions(String rawLine) {
        HashSet<String> expressions = new HashSet<String>();
        if (rawLine != null) {
            int end;
            int start;
            int placeholder = -1;
            while ((start = this.findDelimiter(rawLine, this.startDelim, placeholder)) >= 0 && (end = this.findDelimiter(rawLine, this.endDelim, start + 1)) >= 0) {
                expressions.add(rawLine.substring(start, end + this.endDelim.length()));
                placeholder = end + 1;
                if (placeholder < rawLine.length() - this.minExpressionSize) continue;
            }
        }
        return expressions;
    }

    private int findDelimiter(String rawLine, String delimiter, int lastPos) {
        int position;
        int placeholder = lastPos;
        while ((position = rawLine.indexOf(delimiter, placeholder)) >= 0) {
            int escEndIdx = rawLine.indexOf(this.escapeSeq, placeholder) + this.escapeSeq.length();
            if (escEndIdx > this.escapeSeq.length() - 1 && escEndIdx == position) {
                placeholder = position + 1;
                position = -1;
            }
            if (position < 0 && placeholder < rawLine.length() - this.endDelim.length()) continue;
        }
        return position;
    }

    private String findAndReplaceUnlessEscaped(String rawLine, String search, String replace) {
        int nextReplacement;
        StringBuilder lineBuffer = new StringBuilder((int)((double)rawLine.length() * 1.5));
        int lastReplacement = -1;
        while ((nextReplacement = rawLine.indexOf(search, lastReplacement + 1)) > -1) {
            if (lastReplacement < 0) {
                lastReplacement = 0;
            }
            lineBuffer.append(rawLine, lastReplacement, nextReplacement);
            int escIdx = rawLine.indexOf(this.escapeSeq, lastReplacement + 1);
            if (escIdx > -1 && escIdx + this.escapeSeq.length() == nextReplacement) {
                lineBuffer.setLength(lineBuffer.length() - this.escapeSeq.length());
                lineBuffer.append(search);
            } else {
                lineBuffer.append(replace);
            }
            if ((lastReplacement = nextReplacement + search.length()) > -1) continue;
        }
        if (lastReplacement < rawLine.length()) {
            lineBuffer.append(rawLine, lastReplacement, rawLine.length());
        }
        return lineBuffer.toString();
    }
}
