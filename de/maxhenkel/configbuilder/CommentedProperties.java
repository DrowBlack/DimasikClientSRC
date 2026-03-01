package de.maxhenkel.configbuilder;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class CommentedProperties
implements Map<String, String> {
    private final boolean strict;
    private final List<String> headerComments;
    private final Map<String, Property> properties;

    public CommentedProperties(boolean strict) {
        this.strict = strict;
        this.headerComments = new ArrayList<String>();
        this.properties = new LinkedHashMap<String, Property>();
    }

    public CommentedProperties() {
        this(true);
    }

    public CommentedProperties addHeaderComment(String comment) {
        this.headerComments.add(comment);
        return this;
    }

    public CommentedProperties setHeaderComments(List<String> headerComments) {
        this.headerComments.clear();
        this.headerComments.addAll(headerComments);
        return this;
    }

    void sort(Comparator<String> comparator) {
        ArrayList<Map.Entry<String, Property>> list = new ArrayList<Map.Entry<String, Property>>(this.properties.entrySet());
        list.sort((o1, o2) -> comparator.compare((String)o1.getKey(), (String)o2.getKey()));
        this.properties.clear();
        for (Map.Entry entry : list) {
            this.properties.put((String)entry.getKey(), (Property)entry.getValue());
        }
    }

    @Nullable
    public String get(String key) {
        Objects.requireNonNull(key);
        Property property = this.properties.get(key);
        if (property == null) {
            return null;
        }
        return property.value;
    }

    @Nullable
    public List<String> getComments(String key) {
        Objects.requireNonNull(key);
        Property property = this.properties.get(key);
        if (property == null) {
            return null;
        }
        return property.comments;
    }

    public CommentedProperties setComments(String key, List<String> comments) {
        Objects.requireNonNull(key);
        Property property = this.properties.get(key);
        if (property == null) {
            this.properties.put(key, new Property(comments, ""));
            return this;
        }
        property.comments = comments;
        return this;
    }

    public CommentedProperties set(String key, String value, String ... comments) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        this.properties.put(key, new Property(Arrays.asList(comments), value));
        return this;
    }

    public CommentedProperties load(InputStream inputStream) throws IOException {
        ArrayList<String> headerComments = new ArrayList<String>();
        LinkedHashMap<String, Property> properties = new LinkedHashMap<String, Property>();
        try (LineReader reader = LineReader.fromInputStream(inputStream);){
            String line;
            boolean header = true;
            ArrayList<String> previousComments = new ArrayList<String>();
            while ((line = reader.nextLine()) != null) {
                if (line.trim().isEmpty()) {
                    if (!header) continue;
                    headerComments.addAll(previousComments);
                    previousComments.clear();
                    header = false;
                    continue;
                }
                Pair pair = CommentedProperties.readLine(line);
                if (pair.key == null) {
                    previousComments.add(pair.value);
                    continue;
                }
                Property property = new Property(pair.value);
                property.comments.addAll(previousComments);
                previousComments.clear();
                properties.put(pair.key, property);
                header = false;
            }
        }
        this.setHeaderComments(headerComments);
        this.properties.clear();
        this.properties.putAll(properties);
        return this;
    }

    protected static Pair readLine(String line) throws IOException {
        int c;
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();
        StringReader reader = new StringReader(line);
        boolean isKey = true;
        boolean isComment = false;
        boolean isPrecedingBackslash = false;
        boolean onlyHadWhitespace = true;
        boolean isStartOfValue = false;
        while ((c = reader.read()) != -1) {
            boolean isWhitespace = CommentedProperties.isWhitespace(c);
            if (isComment) {
                if (onlyHadWhitespace && isWhitespace) continue;
                onlyHadWhitespace = false;
                value.append((char)c);
                continue;
            }
            if (isPrecedingBackslash) {
                if (isKey) {
                    key.append((char)CommentedProperties.readEscapedCharacter(c, reader));
                } else {
                    value.append((char)CommentedProperties.readEscapedCharacter(c, reader));
                }
                isPrecedingBackslash = false;
                continue;
            }
            if (c == 92) {
                isPrecedingBackslash = true;
                continue;
            }
            if ((c == 35 || c == 33) && onlyHadWhitespace) {
                isComment = true;
                continue;
            }
            if (isKey) {
                if (key.length() <= 0 && CommentedProperties.isWhitespace(c)) continue;
                if (CommentedProperties.isSeparator(c)) {
                    isKey = false;
                    isStartOfValue = true;
                    onlyHadWhitespace = false;
                    continue;
                }
                if (CommentedProperties.isWhitespace(c)) continue;
                key.append((char)c);
            } else {
                if (isStartOfValue && (CommentedProperties.isWhitespace(c) || CommentedProperties.isSeparator(c))) continue;
                value.append((char)c);
                isStartOfValue = false;
            }
            if (!onlyHadWhitespace || isWhitespace) continue;
            onlyHadWhitespace = false;
        }
        return new Pair(isComment ? null : key.toString(), value.toString());
    }

    private static int readEscapedCharacter(int c, StringReader reader) throws IOException {
        if (c == 117) {
            int u = 0;
            for (int i = 0; i < 4; ++i) {
                int uc = reader.read();
                if (uc == -1) {
                    throw new IOException("Invalid unicode escape sequence");
                }
                u <<= 4;
                if (uc >= 48 && uc <= 57) {
                    u += uc - 48;
                    continue;
                }
                if (uc >= 97 && uc <= 102) {
                    u += uc - 97 + 10;
                    continue;
                }
                if (uc >= 65 && uc <= 70) {
                    u += uc - 65 + 10;
                    continue;
                }
                throw new IOException("Invalid unicode escape sequence");
            }
            return u;
        }
        if (c == 116) {
            return 9;
        }
        if (c == 114) {
            return 13;
        }
        if (c == 110) {
            return 10;
        }
        if (c == 102) {
            return 12;
        }
        return c;
    }

    private static boolean isWhitespace(int c) {
        return c == 32 || c == 9 || c == 13 || c == 12 || Character.isWhitespace(c);
    }

    private static boolean isSeparator(int c) {
        return c == 61 || c == 58 || c == 32 || c == 9 || c == 12;
    }

    public CommentedProperties save(OutputStream outputStream) {
        try (PrintWriter writer = new PrintWriter(outputStream);){
            for (String string : CommentedProperties.removeNewLines(this.headerComments)) {
                writer.print("# ");
                writer.println(string);
            }
            if (this.headerComments.size() > 0) {
                writer.println();
            }
            for (Map.Entry entry : this.properties.entrySet()) {
                for (String comment : CommentedProperties.removeNewLines(((Property)entry.getValue()).comments)) {
                    writer.print("# ");
                    writer.println(comment);
                }
                writer.print(this.escapeKey((String)entry.getKey()));
                writer.print("=");
                writer.println(this.escapeValue(((Property)entry.getValue()).value));
            }
            writer.flush();
        }
        return this;
    }

    private static List<String> removeNewLines(List<String> comments) {
        ArrayList<String> newComments = new ArrayList<String>();
        for (String comment : comments) {
            newComments.addAll(Arrays.asList(comment.split("\\r?\\n")));
        }
        return newComments;
    }

    private String escapeKey(String str) {
        str = this.escape(str);
        str = str.replace(" ", "\\ ");
        str = str.replace("=", "\\=");
        str = str.replace(":", "\\:");
        return str;
    }

    private String escapeValue(String str) {
        str = this.escape(str);
        if (this.strict) {
            str = str.replace("=", "\\=");
            str = str.replace(":", "\\:");
        }
        if (str.startsWith(" ")) {
            str = String.format("\\%s", str);
        }
        return str;
    }

    private String escape(String str) {
        str = str.replace("\\", "\\\\");
        str = str.replace("\n", "\\n");
        str = str.replace("\r", "\\n");
        str = str.replace("\t", "\\t");
        str = str.replace("#", "\\#");
        str = str.replace("!", "\\!");
        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.BASIC_LATIN) continue;
            str = String.format("%s\\u%04X%s", str.substring(0, i), (int)c, str.substring(i + 1));
        }
        return str;
    }

    @Override
    public int size() {
        return this.properties.size();
    }

    @Override
    public boolean isEmpty() {
        return this.properties.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.properties.containsKey(key);
    }

    @Override
    @Deprecated
    public boolean containsValue(Object value) {
        for (Property property : this.properties.values()) {
            if (!property.value.equals(value)) continue;
            return true;
        }
        return false;
    }

    @Override
    @Nullable
    @Deprecated
    public String get(Object key) {
        if (!(key instanceof String)) {
            return null;
        }
        return this.get((String)key);
    }

    @Override
    @Deprecated
    public String put(String key, String value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        Property put = this.properties.put(key, new Property(value));
        if (put == null) {
            return null;
        }
        return put.value;
    }

    @Override
    public String remove(Object key) {
        Property removed = this.properties.remove(key);
        if (removed == null) {
            return null;
        }
        return removed.value;
    }

    @Override
    @Deprecated
    public void putAll(Map<? extends String, ? extends String> map) {
        for (Map.Entry<? extends String, ? extends String> entry : map.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        this.headerComments.clear();
        this.properties.clear();
    }

    @Override
    public Set<String> keySet() {
        return this.properties.keySet();
    }

    @Override
    @Deprecated
    public Collection<String> values() {
        return this.properties.values().stream().map(property -> ((Property)property).value).collect(Collectors.toList());
    }

    @Override
    @Deprecated
    public Set<Map.Entry<String, String>> entrySet() {
        return this.properties.entrySet().stream().map(entry -> new AbstractMap.SimpleEntry<String, String>((String)entry.getKey(), ((Property)entry.getValue()).value)).collect(Collectors.toSet());
    }

    protected static class Property {
        private List<String> comments;
        private String value;

        public Property(List<String> comments, String value) {
            this.comments = comments;
            this.value = value;
        }

        public Property(String value) {
            this(new ArrayList<String>(), value);
        }
    }

    protected static class LineReader
    implements Closeable {
        private final BufferedReader reader;

        public LineReader(BufferedReader reader) {
            this.reader = reader;
        }

        public static LineReader fromInputStream(InputStream inputStream) {
            return new LineReader(new BufferedReader(new InputStreamReader(inputStream)));
        }

        @Nullable
        public String nextLine() throws IOException {
            String line = this.reader.readLine();
            if (line == null) {
                return null;
            }
            if (line.endsWith("\\")) {
                line = line.substring(0, line.length() - 1);
                String nextLine = this.nextLine();
                if (nextLine == null) {
                    return line;
                }
                return line + nextLine;
            }
            return line;
        }

        @Override
        public void close() throws IOException {
            this.reader.close();
        }
    }

    protected static class Pair {
        private String key;
        private String value;

        public Pair(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
