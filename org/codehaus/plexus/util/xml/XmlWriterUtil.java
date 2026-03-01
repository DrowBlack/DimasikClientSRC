package org.codehaus.plexus.util.xml;

import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.XMLWriter;

public class XmlWriterUtil {
    public static final String LS = System.getProperty("line.separator");
    public static final int DEFAULT_INDENTATION_SIZE = 2;
    public static final int DEFAULT_COLUMN_LINE = 80;

    public static void writeLineBreak(XMLWriter writer) {
        XmlWriterUtil.writeLineBreak(writer, 1);
    }

    public static void writeLineBreak(XMLWriter writer, int repeat) {
        for (int i = 0; i < repeat; ++i) {
            writer.writeMarkup(LS);
        }
    }

    public static void writeLineBreak(XMLWriter writer, int repeat, int indent) {
        XmlWriterUtil.writeLineBreak(writer, repeat, indent, 2);
    }

    public static void writeLineBreak(XMLWriter writer, int repeat, int indent, int indentSize) {
        XmlWriterUtil.writeLineBreak(writer, repeat);
        if (indent < 0) {
            indent = 0;
        }
        if (indentSize < 0) {
            indentSize = 0;
        }
        writer.writeText(StringUtils.repeat(" ", indent * indentSize));
    }

    public static void writeCommentLineBreak(XMLWriter writer) {
        XmlWriterUtil.writeCommentLineBreak(writer, 80);
    }

    public static void writeCommentLineBreak(XMLWriter writer, int columnSize) {
        if (columnSize < 10) {
            columnSize = 80;
        }
        writer.writeMarkup("<!-- " + StringUtils.repeat("=", columnSize - 10) + " -->" + LS);
    }

    public static void writeComment(XMLWriter writer, String comment) {
        XmlWriterUtil.writeComment(writer, comment, 0, 2);
    }

    public static void writeComment(XMLWriter writer, String comment, int indent) {
        XmlWriterUtil.writeComment(writer, comment, indent, 2);
    }

    public static void writeComment(XMLWriter writer, String comment, int indent, int indentSize) {
        XmlWriterUtil.writeComment(writer, comment, indent, indentSize, 80);
    }

    public static void writeComment(XMLWriter writer, String comment, int indent, int indentSize, int columnSize) {
        if (comment == null) {
            comment = "null";
        }
        while (comment.contains("<!--")) {
            comment = comment.replace("<!--", "");
        }
        while (comment.contains("-->")) {
            comment = comment.replace("-->", "");
        }
        if (indent < 0) {
            indent = 0;
        }
        if (indentSize < 0) {
            indentSize = 0;
        }
        if (columnSize < 0) {
            columnSize = 80;
        }
        String indentation = StringUtils.repeat(" ", indent * indentSize);
        int magicNumber = indentation.length() + columnSize - "-->".length() - 1;
        String[] sentences = StringUtils.split(comment, LS);
        StringBuffer line = new StringBuffer(indentation + "<!-- ");
        for (String sentence : sentences) {
            String[] words;
            for (String word : words = StringUtils.split(sentence, " ")) {
                StringBuilder sentenceTmp = new StringBuilder(line.toString());
                sentenceTmp.append(word).append(' ');
                if (sentenceTmp.length() > magicNumber) {
                    if (line.length() != indentation.length() + "<!-- ".length()) {
                        if (magicNumber - line.length() > 0) {
                            line.append(StringUtils.repeat(" ", magicNumber - line.length()));
                        }
                        line.append("-->").append(LS);
                        writer.writeMarkup(line.toString());
                    }
                    line = new StringBuffer(indentation + "<!-- ");
                    line.append(word).append(' ');
                    continue;
                }
                line.append(word).append(' ');
            }
            if (magicNumber - line.length() <= 0) continue;
            line.append(StringUtils.repeat(" ", magicNumber - line.length()));
        }
        if (line.length() <= magicNumber) {
            line.append(StringUtils.repeat(" ", magicNumber - line.length()));
        }
        line.append("-->").append(LS);
        writer.writeMarkup(line.toString());
    }

    public static void writeCommentText(XMLWriter writer, String comment) {
        XmlWriterUtil.writeCommentText(writer, comment, 0, 2);
    }

    public static void writeCommentText(XMLWriter writer, String comment, int indent) {
        XmlWriterUtil.writeCommentText(writer, comment, indent, 2);
    }

    public static void writeCommentText(XMLWriter writer, String comment, int indent, int indentSize) {
        XmlWriterUtil.writeCommentText(writer, comment, indent, indentSize, 80);
    }

    public static void writeCommentText(XMLWriter writer, String comment, int indent, int indentSize, int columnSize) {
        if (indent < 0) {
            indent = 0;
        }
        if (indentSize < 0) {
            indentSize = 0;
        }
        if (columnSize < 0) {
            columnSize = 80;
        }
        XmlWriterUtil.writeLineBreak(writer, 1);
        writer.writeMarkup(StringUtils.repeat(" ", indent * indentSize));
        XmlWriterUtil.writeCommentLineBreak(writer, columnSize);
        XmlWriterUtil.writeComment(writer, comment, indent, indentSize, columnSize);
        writer.writeMarkup(StringUtils.repeat(" ", indent * indentSize));
        XmlWriterUtil.writeCommentLineBreak(writer, columnSize);
        XmlWriterUtil.writeLineBreak(writer, 1, indent, indentSize);
    }
}
