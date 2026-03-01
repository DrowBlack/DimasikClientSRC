package com.zwitserloot.cmdreader;

import com.zwitserloot.cmdreader.InvalidCommandLineException;
import com.zwitserloot.cmdreader.ParseItem;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CmdReader<T> {
    private final Class<T> settingsDescriptor;
    private final List<ParseItem> items;
    private final Map<Character, ParseItem> shorthands;
    private final List<ParseItem> seqList;
    private static final int SCREEN_WIDTH = 72;

    private CmdReader(Class<T> settingsDescriptor) {
        this.settingsDescriptor = settingsDescriptor;
        this.items = Collections.unmodifiableList(this.init());
        this.shorthands = ParseItem.makeShortHandMap(this.items);
        this.seqList = CmdReader.makeSeqList(this.items);
    }

    public static <T> CmdReader<T> of(Class<T> settingsDescriptor) {
        return new CmdReader<T>(settingsDescriptor);
    }

    private List<ParseItem> init() {
        ArrayList<ParseItem> out = new ArrayList<ParseItem>();
        for (Class<T> c = this.settingsDescriptor; c != Object.class; c = c.getSuperclass()) {
            Field[] fields;
            for (Field field : fields = this.settingsDescriptor.getDeclaredFields()) {
                field.setAccessible(true);
                if (Modifier.isStatic(field.getModifiers())) continue;
                out.add(new ParseItem(field));
            }
        }
        ParseItem.multiSanityChecks(out);
        return out;
    }

    private static List<ParseItem> makeSeqList(List<ParseItem> items) {
        ArrayList<ParseItem> list = new ArrayList<ParseItem>();
        for (ParseItem item : items) {
            if (!item.isSeq()) continue;
            list.add(item);
        }
        return list;
    }

    public String generateCommandLineHelp(String commandName) {
        StringBuilder out = new StringBuilder();
        int maxFullName = 0;
        int maxShorthand = 0;
        for (ParseItem item : this.items) {
            if (item.isSeq()) continue;
            maxFullName = Math.max(maxFullName, item.getFullName().length() + (item.isParameterized() ? 4 : 0));
            maxShorthand = Math.max(maxShorthand, item.getShorthand().length());
        }
        if (maxShorthand == 0) {
            ++maxShorthand;
        }
        maxShorthand = maxShorthand * 3 - 1;
        this.generateShortSummary(commandName, out);
        this.generateSequentialArgsHelp(out);
        this.generateMandatoryArgsHelp(maxFullName, maxShorthand, out);
        this.generateOptionalArgsHelp(maxFullName, maxShorthand, out);
        return out.toString();
    }

    private void generateShortSummary(String commandName, StringBuilder out) {
        if (commandName != null && commandName.length() > 0) {
            out.append(commandName).append(" ");
        }
        StringBuilder sb = new StringBuilder();
        for (ParseItem item : this.items) {
            if (item.isSeq() || item.isMandatory()) continue;
            sb.append(item.getShorthand());
        }
        if (sb.length() > 0) {
            out.append("[-").append((CharSequence)sb).append("] ");
            sb.setLength(0);
        }
        for (ParseItem item : this.items) {
            if (item.isSeq() || !item.isMandatory()) continue;
            sb.append(item.getShorthand());
        }
        if (sb.length() > 0) {
            out.append("-").append((CharSequence)sb).append(" ");
            sb.setLength(0);
        }
        for (ParseItem item : this.items) {
            if (item.isSeq() || !item.isMandatory() || item.getShorthand().length() != 0) continue;
            out.append("--").append(item.getFullName()).append("=val ");
        }
        for (ParseItem item : this.items) {
            if (!item.isSeq()) continue;
            if (!item.isMandatory()) {
                out.append('[');
            }
            out.append(item.getFullName());
            if (!item.isMandatory()) {
                out.append(']');
            }
            out.append(' ');
        }
        out.append("\n");
    }

    private void generateSequentialArgsHelp(StringBuilder out) {
        ArrayList<ParseItem> items = new ArrayList<ParseItem>();
        for (ParseItem item : this.items) {
            if (!item.isSeq() || item.getFullDescription().length() <= 0) continue;
            items.add(item);
        }
        if (items.size() == 0) {
            return;
        }
        int maxSeqArg = 0;
        for (ParseItem item : items) {
            maxSeqArg = Math.max(maxSeqArg, item.getFullName().length());
        }
        out.append("\n  Sequential arguments:\n");
        for (ParseItem item : items) {
            this.generateSequentialArgHelp(maxSeqArg, item, out);
        }
    }

    private void generateMandatoryArgsHelp(int maxFullName, int maxShorthand, StringBuilder out) {
        ArrayList<ParseItem> items = new ArrayList<ParseItem>();
        for (ParseItem item : this.items) {
            if (!item.isMandatory() || item.isSeq()) continue;
            items.add(item);
        }
        if (items.size() == 0) {
            return;
        }
        out.append("\n  Mandatory arguments:\n");
        for (ParseItem item : items) {
            this.generateArgHelp(maxFullName, maxShorthand, item, out);
        }
    }

    private void generateOptionalArgsHelp(int maxFullName, int maxShorthand, StringBuilder out) {
        ArrayList<ParseItem> items = new ArrayList<ParseItem>();
        for (ParseItem item : this.items) {
            if (item.isMandatory() || item.isSeq()) continue;
            items.add(item);
        }
        if (items.size() == 0) {
            return;
        }
        out.append("\n  Optional arguments:\n");
        for (ParseItem item : items) {
            this.generateArgHelp(maxFullName, maxShorthand, item, out);
        }
    }

    private void generateArgHelp(int maxFullName, int maxShorthand, ParseItem item, StringBuilder out) {
        out.append("    ");
        String fn = item.getFullName() + (item.isParameterized() ? "=val" : "");
        out.append(String.format("--%-" + maxFullName + "s ", fn));
        StringBuilder sh = new StringBuilder();
        for (char c : item.getShorthand().toCharArray()) {
            if (sh.length() > 0) {
                sh.append(" ");
            }
            sh.append("-").append(c);
        }
        out.append(String.format("%-" + maxShorthand + "s ", sh));
        int left = 64 - maxShorthand - maxFullName;
        String description = item.getFullDescription();
        if (description.length() == 0 || description.length() < left) {
            out.append(description).append("\n");
            return;
        }
        for (String line : CmdReader.wordbreak(item.getFullDescription(), 64)) {
            out.append("\n        ").append(line);
        }
        out.append("\n");
    }

    private void generateSequentialArgHelp(int maxSeqArg, ParseItem item, StringBuilder out) {
        out.append("    ");
        out.append(String.format("%-" + maxSeqArg + "s   ", item.getFullName()));
        int left = 65 - maxSeqArg;
        String description = item.getFullDescription();
        if (description.length() == 0 || description.length() < left) {
            out.append(description).append("\n");
            return;
        }
        for (String line : CmdReader.wordbreak(item.getFullDescription(), 64)) {
            out.append("\n        ").append(line);
        }
        out.append("\n");
    }

    private static List<String> wordbreak(String text, int width) {
        StringBuilder line = new StringBuilder();
        ArrayList<String> out = new ArrayList<String>();
        int lastSpace = -1;
        for (char c : text.toCharArray()) {
            if (c == '\t') {
                c = ' ';
            }
            if (c == '\n') {
                out.add(line.toString());
                line.setLength(0);
                lastSpace = -1;
                continue;
            }
            if (c == ' ') {
                lastSpace = line.length();
                line.append(' ');
            } else {
                line.append(c);
            }
            if (line.length() <= width || lastSpace <= 8) continue;
            out.add(line.substring(0, lastSpace));
            String left = line.substring(lastSpace + 1);
            line.setLength(0);
            line.append(left);
            lastSpace = -1;
        }
        if (line.length() > 0) {
            out.add(line.toString());
        }
        return out;
    }

    public T make(String in) throws InvalidCommandLineException, IllegalArgumentException {
        ArrayList<String> out = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        boolean inQuote = false;
        boolean inBack = false;
        for (char c : in.toCharArray()) {
            if (inBack) {
                inBack = false;
                if (c == '\n') continue;
                sb.append(c);
            }
            if (c == '\\') {
                inBack = true;
                continue;
            }
            if (c == '\"') {
                inQuote = !inQuote;
                continue;
            }
            if (c == ' ' && !inQuote) {
                String p = sb.toString();
                sb.setLength(0);
                if (p.equals("")) continue;
                out.add(p);
                continue;
            }
            sb.append(c);
        }
        if (sb.length() > 0) {
            out.add(sb.toString());
        }
        return this.make(out.toArray(new String[out.size()]));
    }

    public T make(String[] in) throws InvalidCommandLineException {
        final T obj = this.construct();
        if (in == null) {
            in = new String[]{};
        }
        int seq = 0;
        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        class State {
            List<ParseItem> used = new ArrayList<ParseItem>();

            State() {
            }

            void handle(ParseItem item, String value) {
                item.set(obj, value);
                this.used.add(item);
            }

            void finish() throws InvalidCommandLineException {
                this.checkForGlobalMandatories();
                this.checkForExcludes();
                this.checkForRequires();
                this.checkForMandatoriesIf();
                this.checkForMandatoriesIfNot();
            }

            private void checkForGlobalMandatories() throws InvalidCommandLineException {
                for (ParseItem item : CmdReader.this.items) {
                    if (!item.isMandatory() || this.used.contains(item)) continue;
                    throw new InvalidCommandLineException("You did not specify mandatory parameter " + item.getFullName());
                }
            }

            private void checkForExcludes() throws InvalidCommandLineException {
                for (ParseItem item : CmdReader.this.items) {
                    if (!this.used.contains(item)) continue;
                    for (String n : item.getExcludes()) {
                        for (ParseItem i : CmdReader.this.items) {
                            if (!i.getFullName().equals(n) || !this.used.contains(i)) continue;
                            throw new InvalidCommandLineException("You specified parameter " + i.getFullName() + " which cannot be used together with " + item.getFullName());
                        }
                    }
                }
            }

            private void checkForRequires() throws InvalidCommandLineException {
                for (ParseItem item : CmdReader.this.items) {
                    if (!this.used.contains(item)) continue;
                    for (String n : item.getRequires()) {
                        for (ParseItem i : CmdReader.this.items) {
                            if (!i.getFullName().equals(n) || this.used.contains(i)) continue;
                            throw new InvalidCommandLineException("You specified parameter " + item.getFullName() + " which requires that you also supply " + i.getFullName());
                        }
                    }
                }
            }

            private void checkForMandatoriesIf() throws InvalidCommandLineException {
                for (ParseItem item : CmdReader.this.items) {
                    if (this.used.contains(item) || item.getMandatoryIf().size() == 0) continue;
                    for (String n : item.getMandatoryIf()) {
                        for (ParseItem i : CmdReader.this.items) {
                            if (!i.getFullName().equals(n) || !this.used.contains(i)) continue;
                            throw new InvalidCommandLineException("You did not specify parameter " + item.getFullName() + " which is mandatory if you use " + i.getFullName());
                        }
                    }
                }
            }

            private void checkForMandatoriesIfNot() throws InvalidCommandLineException {
                block0: for (ParseItem item : CmdReader.this.items) {
                    if (this.used.contains(item) || item.getMandatoryIfNot().size() == 0) continue;
                    for (String n : item.getMandatoryIfNot()) {
                        for (ParseItem i : CmdReader.this.items) {
                            if (!i.getFullName().equals(n) || !this.used.contains(i)) continue;
                            continue block0;
                        }
                    }
                    StringBuilder alternatives = new StringBuilder();
                    if (item.getMandatoryIfNot().size() > 1) {
                        alternatives.append("one of ");
                    }
                    for (String n : item.getMandatoryIfNot()) {
                        alternatives.append(n).append(", ");
                    }
                    alternatives.setLength(alternatives.length() - 2);
                    throw new InvalidCommandLineException("You did not specify parameter " + item.getFullName() + " which is mandatory unless you use " + alternatives);
                }
            }
        }
        State state = new State();
        for (int i = 0; i < in.length; ++i) {
            if (in[i].startsWith("--")) {
                String value;
                int idx = in[i].indexOf(61);
                String key = idx == -1 ? in[i].substring(2) : in[i].substring(2, idx);
                String string = value = idx == -1 ? "" : in[i].substring(idx + 1);
                if (value.length() == 0 && idx != -1) {
                    throw new InvalidCommandLineException("invalid command line argument - you should write something after the '=': " + in[i]);
                }
                boolean handled = false;
                for (ParseItem item : this.items) {
                    if (!item.getFullName().equalsIgnoreCase(key)) continue;
                    if (item.isParameterized() && value.length() == 0) {
                        if (i < in.length - 1 && !in[i + 1].startsWith("-")) {
                            value = in[++i];
                        } else {
                            throw new InvalidCommandLineException(String.format("invalid command line argument - %s requires a parameter but there is none.", key));
                        }
                    }
                    state.handle(item, (String)(!item.isParameterized() && value.length() == 0 ? null : value));
                    handled = true;
                    break;
                }
                if (handled) continue;
                throw new InvalidCommandLineException("invalid command line argument - I don't know about that option: " + in[i]);
            }
            if (in[i].startsWith("-")) {
                for (char c : in[i].substring(1).toCharArray()) {
                    ParseItem item = this.shorthands.get(Character.valueOf(c));
                    if (item == null) {
                        throw new InvalidCommandLineException(String.format("invalid command line argument - %s is not a known option: %s", Character.valueOf(c), in[i]));
                    }
                    if (item.isParameterized()) {
                        if (i >= in.length - 1 || in[i + 1].startsWith("-")) {
                            throw new InvalidCommandLineException(String.format("invalid command line argument - %s requires a parameter but there is none.", Character.valueOf(c)));
                        }
                        String value = in[++i];
                        state.handle(item, value);
                        continue;
                    }
                    state.handle(item, null);
                }
                continue;
            }
            if (this.seqList.size() < ++seq) {
                if (this.seqList.size() > 0 && this.seqList.get(this.seqList.size() - 1).isCollection()) {
                    state.handle(this.seqList.get(this.seqList.size() - 1), in[i]);
                    continue;
                }
                throw new InvalidCommandLineException(String.format("invalid command line argument - you've provided too many free-standing arguments: %s", in[i]));
            }
            ParseItem item = this.seqList.get(seq - 1);
            state.handle(item, in[i]);
        }
        state.finish();
        return obj;
    }

    private T construct() {
        try {
            Constructor<T> constructor = this.settingsDescriptor.getDeclaredConstructor(new Class[0]);
            constructor.setAccessible(true);
            return constructor.newInstance(new Object[0]);
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(String.format("A CmdReader class must have a no-args constructor: %s", this.settingsDescriptor));
        }
        catch (InstantiationException e) {
            throw new IllegalArgumentException(String.format("A CmdReader class must not be an interface or abstract: %s", this.settingsDescriptor));
        }
        catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Huh?");
        }
        catch (InvocationTargetException e) {
            throw new IllegalArgumentException("Exception occurred when constructing CmdReader class " + this.settingsDescriptor, e.getCause());
        }
    }

    public static String squash(Collection<String> collection) {
        Iterator<String> i = collection.iterator();
        StringBuilder out = new StringBuilder();
        while (i.hasNext()) {
            out.append(i.next());
            if (!i.hasNext()) continue;
            out.append(' ');
        }
        return out.toString();
    }
}
