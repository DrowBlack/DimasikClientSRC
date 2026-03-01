package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.nbt.ByteArrayNBT;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.INBTType;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongArrayNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.nbt.NumberNBT;
import net.minecraft.nbt.ShortNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.TranslationTextComponent;

public class JsonToNBT {
    public static final SimpleCommandExceptionType ERROR_TRAILING_DATA = new SimpleCommandExceptionType(new TranslationTextComponent("argument.nbt.trailing"));
    public static final SimpleCommandExceptionType ERROR_EXPECTED_KEY = new SimpleCommandExceptionType(new TranslationTextComponent("argument.nbt.expected.key"));
    public static final SimpleCommandExceptionType ERROR_EXPECTED_VALUE = new SimpleCommandExceptionType(new TranslationTextComponent("argument.nbt.expected.value"));
    public static final Dynamic2CommandExceptionType ERROR_INSERT_MIXED_LIST = new Dynamic2CommandExceptionType((error1, error2) -> new TranslationTextComponent("argument.nbt.list.mixed", error1, error2));
    public static final Dynamic2CommandExceptionType ERROR_INSERT_MIXED_ARRAY = new Dynamic2CommandExceptionType((error, error2) -> new TranslationTextComponent("argument.nbt.array.mixed", error, error2));
    public static final DynamicCommandExceptionType ERROR_INVALID_ARRAY = new DynamicCommandExceptionType(error -> new TranslationTextComponent("argument.nbt.array.invalid", error));
    private static final Pattern DOUBLE_PATTERN_NOSUFFIX = Pattern.compile("[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?", 2);
    private static final Pattern DOUBLE_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", 2);
    private static final Pattern FLOAT_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f", 2);
    private static final Pattern BYTE_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)b", 2);
    private static final Pattern LONG_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)l", 2);
    private static final Pattern SHORT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)s", 2);
    private static final Pattern INT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)");
    private final StringReader reader;

    public static CompoundNBT getTagFromJson(String jsonString) throws CommandSyntaxException {
        return new JsonToNBT(new StringReader(jsonString)).readSingleStruct();
    }

    @VisibleForTesting
    CompoundNBT readSingleStruct() throws CommandSyntaxException {
        CompoundNBT compoundnbt = this.readStruct();
        this.reader.skipWhitespace();
        if (this.reader.canRead()) {
            throw ERROR_TRAILING_DATA.createWithContext(this.reader);
        }
        return compoundnbt;
    }

    public JsonToNBT(StringReader readerIn) {
        this.reader = readerIn;
    }

    protected String readKey() throws CommandSyntaxException {
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw ERROR_EXPECTED_KEY.createWithContext(this.reader);
        }
        return this.reader.readString();
    }

    protected INBT readTypedValue() throws CommandSyntaxException {
        this.reader.skipWhitespace();
        int i = this.reader.getCursor();
        if (StringReader.isQuotedStringStart(this.reader.peek())) {
            return StringNBT.valueOf(this.reader.readQuotedString());
        }
        String s = this.reader.readUnquotedString();
        if (s.isEmpty()) {
            this.reader.setCursor(i);
            throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
        }
        return this.type(s);
    }

    private INBT type(String stringIn) {
        try {
            if (FLOAT_PATTERN.matcher(stringIn).matches()) {
                return FloatNBT.valueOf(Float.parseFloat(stringIn.substring(0, stringIn.length() - 1)));
            }
            if (BYTE_PATTERN.matcher(stringIn).matches()) {
                return ByteNBT.valueOf(Byte.parseByte(stringIn.substring(0, stringIn.length() - 1)));
            }
            if (LONG_PATTERN.matcher(stringIn).matches()) {
                return LongNBT.valueOf(Long.parseLong(stringIn.substring(0, stringIn.length() - 1)));
            }
            if (SHORT_PATTERN.matcher(stringIn).matches()) {
                return ShortNBT.valueOf(Short.parseShort(stringIn.substring(0, stringIn.length() - 1)));
            }
            if (INT_PATTERN.matcher(stringIn).matches()) {
                return IntNBT.valueOf(Integer.parseInt(stringIn));
            }
            if (DOUBLE_PATTERN.matcher(stringIn).matches()) {
                return DoubleNBT.valueOf(Double.parseDouble(stringIn.substring(0, stringIn.length() - 1)));
            }
            if (DOUBLE_PATTERN_NOSUFFIX.matcher(stringIn).matches()) {
                return DoubleNBT.valueOf(Double.parseDouble(stringIn));
            }
            if ("true".equalsIgnoreCase(stringIn)) {
                return ByteNBT.ONE;
            }
            if ("false".equalsIgnoreCase(stringIn)) {
                return ByteNBT.ZERO;
            }
        }
        catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
        return StringNBT.valueOf(stringIn);
    }

    public INBT readValue() throws CommandSyntaxException {
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
        }
        char c0 = this.reader.peek();
        if (c0 == '{') {
            return this.readStruct();
        }
        return c0 == '[' ? this.readList() : this.readTypedValue();
    }

    protected INBT readList() throws CommandSyntaxException {
        return this.reader.canRead(3) && !StringReader.isQuotedStringStart(this.reader.peek(1)) && this.reader.peek(2) == ';' ? this.readArrayTag() : this.readListTag();
    }

    public CompoundNBT readStruct() throws CommandSyntaxException {
        this.expect('{');
        CompoundNBT compoundnbt = new CompoundNBT();
        this.reader.skipWhitespace();
        while (this.reader.canRead() && this.reader.peek() != '}') {
            int i = this.reader.getCursor();
            String s = this.readKey();
            if (s.isEmpty()) {
                this.reader.setCursor(i);
                throw ERROR_EXPECTED_KEY.createWithContext(this.reader);
            }
            this.expect(':');
            compoundnbt.put(s, this.readValue());
            if (!this.hasElementSeparator()) break;
            if (this.reader.canRead()) continue;
            throw ERROR_EXPECTED_KEY.createWithContext(this.reader);
        }
        this.expect('}');
        return compoundnbt;
    }

    private INBT readListTag() throws CommandSyntaxException {
        this.expect('[');
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
        }
        ListNBT listnbt = new ListNBT();
        INBTType<?> inbttype = null;
        while (this.reader.peek() != ']') {
            int i = this.reader.getCursor();
            INBT inbt = this.readValue();
            INBTType<?> inbttype1 = inbt.getType();
            if (inbttype == null) {
                inbttype = inbttype1;
            } else if (inbttype1 != inbttype) {
                this.reader.setCursor(i);
                throw ERROR_INSERT_MIXED_LIST.createWithContext(this.reader, inbttype1.getTagName(), inbttype.getTagName());
            }
            listnbt.add(inbt);
            if (!this.hasElementSeparator()) break;
            if (this.reader.canRead()) continue;
            throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
        }
        this.expect(']');
        return listnbt;
    }

    private INBT readArrayTag() throws CommandSyntaxException {
        this.expect('[');
        int i = this.reader.getCursor();
        char c0 = this.reader.read();
        this.reader.read();
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
        }
        if (c0 == 'B') {
            return new ByteArrayNBT(this.getNumberList(ByteArrayNBT.TYPE, ByteNBT.TYPE));
        }
        if (c0 == 'L') {
            return new LongArrayNBT(this.getNumberList(LongArrayNBT.TYPE, LongNBT.TYPE));
        }
        if (c0 == 'I') {
            return new IntArrayNBT(this.getNumberList(IntArrayNBT.TYPE, IntNBT.TYPE));
        }
        this.reader.setCursor(i);
        throw ERROR_INVALID_ARRAY.createWithContext(this.reader, String.valueOf(c0));
    }

    private <T extends Number> List<T> getNumberList(INBTType<?> arrayType, INBTType<?> numberType) throws CommandSyntaxException {
        ArrayList<Number> list = Lists.newArrayList();
        while (this.reader.peek() != ']') {
            int i = this.reader.getCursor();
            INBT inbt = this.readValue();
            INBTType<?> inbttype = inbt.getType();
            if (inbttype != numberType) {
                this.reader.setCursor(i);
                throw ERROR_INSERT_MIXED_ARRAY.createWithContext(this.reader, inbttype.getTagName(), arrayType.getTagName());
            }
            if (numberType == ByteNBT.TYPE) {
                list.add(((NumberNBT)inbt).getByte());
            } else if (numberType == LongNBT.TYPE) {
                list.add(((NumberNBT)inbt).getLong());
            } else {
                list.add(((NumberNBT)inbt).getInt());
            }
            if (!this.hasElementSeparator()) break;
            if (this.reader.canRead()) continue;
            throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
        }
        this.expect(']');
        return list;
    }

    private boolean hasElementSeparator() {
        this.reader.skipWhitespace();
        if (this.reader.canRead() && this.reader.peek() == ',') {
            this.reader.skip();
            this.reader.skipWhitespace();
            return true;
        }
        return false;
    }

    private void expect(char expected) throws CommandSyntaxException {
        this.reader.skipWhitespace();
        this.reader.expect(expected);
    }
}
