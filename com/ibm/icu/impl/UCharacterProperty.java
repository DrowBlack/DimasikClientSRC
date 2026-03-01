package com.ibm.icu.impl;

import com.ibm.icu.impl.ICUBinary;
import com.ibm.icu.impl.Norm2AllModes;
import com.ibm.icu.impl.Normalizer2Impl;
import com.ibm.icu.impl.Trie2;
import com.ibm.icu.impl.Trie2_16;
import com.ibm.icu.impl.UBiDiProps;
import com.ibm.icu.impl.UCaseProps;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.lang.UScript;
import com.ibm.icu.text.Normalizer2;
import com.ibm.icu.text.UTF16;
import com.ibm.icu.text.UnicodeSet;
import com.ibm.icu.util.CodePointMap;
import com.ibm.icu.util.CodePointTrie;
import com.ibm.icu.util.ICUException;
import com.ibm.icu.util.ICUUncheckedIOException;
import com.ibm.icu.util.VersionInfo;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.MissingResourceException;

public final class UCharacterProperty {
    public static final UCharacterProperty INSTANCE;
    public Trie2_16 m_trie_;
    public VersionInfo m_unicodeVersion_;
    public static final char LATIN_CAPITAL_LETTER_I_WITH_DOT_ABOVE_ = '\u0130';
    public static final char LATIN_SMALL_LETTER_DOTLESS_I_ = '\u0131';
    public static final char LATIN_SMALL_LETTER_I_ = 'i';
    public static final int TYPE_MASK = 31;
    public static final int SRC_NONE = 0;
    public static final int SRC_CHAR = 1;
    public static final int SRC_PROPSVEC = 2;
    public static final int SRC_NAMES = 3;
    public static final int SRC_CASE = 4;
    public static final int SRC_BIDI = 5;
    public static final int SRC_CHAR_AND_PROPSVEC = 6;
    public static final int SRC_CASE_AND_NORM = 7;
    public static final int SRC_NFC = 8;
    public static final int SRC_NFKC = 9;
    public static final int SRC_NFKC_CF = 10;
    public static final int SRC_NFC_CANON_ITER = 11;
    public static final int SRC_INPC = 12;
    public static final int SRC_INSC = 13;
    public static final int SRC_VO = 14;
    public static final int SRC_COUNT = 15;
    static final int MY_MASK = 30;
    private static final int GC_CN_MASK;
    private static final int GC_CC_MASK;
    private static final int GC_CS_MASK;
    private static final int GC_ZS_MASK;
    private static final int GC_ZL_MASK;
    private static final int GC_ZP_MASK;
    private static final int GC_Z_MASK;
    BinaryProperty[] binProps = new BinaryProperty[]{new BinaryProperty(1, 256), new BinaryProperty(1, 128), new BinaryProperty(5){

        @Override
        boolean contains(int c) {
            return UBiDiProps.INSTANCE.isBidiControl(c);
        }
    }, new BinaryProperty(5){

        @Override
        boolean contains(int c) {
            return UBiDiProps.INSTANCE.isMirrored(c);
        }
    }, new BinaryProperty(1, 2), new BinaryProperty(1, 524288), new BinaryProperty(1, 0x100000), new BinaryProperty(1, 1024), new BinaryProperty(1, 2048), new BinaryProperty(8){

        @Override
        boolean contains(int c) {
            Normalizer2Impl impl = Norm2AllModes.getNFCInstance().impl;
            return impl.isCompNo(impl.getNorm16(c));
        }
    }, new BinaryProperty(1, 0x4000000), new BinaryProperty(1, 8192), new BinaryProperty(1, 16384), new BinaryProperty(1, 64), new BinaryProperty(1, 4), new BinaryProperty(1, 0x2000000), new BinaryProperty(1, 0x1000000), new BinaryProperty(1, 512), new BinaryProperty(1, 32768), new BinaryProperty(1, 65536), new BinaryProperty(5){

        @Override
        boolean contains(int c) {
            return UBiDiProps.INSTANCE.isJoinControl(c);
        }
    }, new BinaryProperty(1, 0x200000), new CaseBinaryProperty(22), new BinaryProperty(1, 32), new BinaryProperty(1, 4096), new BinaryProperty(1, 8), new BinaryProperty(1, 131072), new CaseBinaryProperty(27), new BinaryProperty(1, 16), new BinaryProperty(1, 262144), new CaseBinaryProperty(30), new BinaryProperty(1, 1), new BinaryProperty(1, 0x800000), new BinaryProperty(1, 0x400000), new CaseBinaryProperty(34), new BinaryProperty(1, 0x8000000), new BinaryProperty(1, 0x10000000), new NormInertBinaryProperty(8, 37), new NormInertBinaryProperty(9, 38), new NormInertBinaryProperty(8, 39), new NormInertBinaryProperty(9, 40), new BinaryProperty(11){

        @Override
        boolean contains(int c) {
            return Norm2AllModes.getNFCInstance().impl.ensureCanonIterData().isCanonSegmentStarter(c);
        }
    }, new BinaryProperty(1, 0x20000000), new BinaryProperty(1, 0x40000000), new BinaryProperty(6){

        @Override
        boolean contains(int c) {
            return UCharacter.isUAlphabetic(c) || UCharacter.isDigit(c);
        }
    }, new BinaryProperty(1){

        @Override
        boolean contains(int c) {
            if (c <= 159) {
                return c == 9 || c == 32;
            }
            return UCharacter.getType(c) == 12;
        }
    }, new BinaryProperty(1){

        @Override
        boolean contains(int c) {
            return UCharacterProperty.isgraphPOSIX(c);
        }
    }, new BinaryProperty(1){

        @Override
        boolean contains(int c) {
            return UCharacter.getType(c) == 12 || UCharacterProperty.isgraphPOSIX(c);
        }
    }, new BinaryProperty(1){

        @Override
        boolean contains(int c) {
            if (c <= 102 && c >= 65 && (c <= 70 || c >= 97) || c >= 65313 && c <= 65350 && (c <= 65318 || c >= 65345)) {
                return true;
            }
            return UCharacter.getType(c) == 9;
        }
    }, new CaseBinaryProperty(49), new CaseBinaryProperty(50), new CaseBinaryProperty(51), new CaseBinaryProperty(52), new CaseBinaryProperty(53), new BinaryProperty(7){

        @Override
        boolean contains(int c) {
            String nfd = Norm2AllModes.getNFCInstance().impl.getDecomposition(c);
            if (nfd != null) {
                c = nfd.codePointAt(0);
                if (Character.charCount(c) != nfd.length()) {
                    c = -1;
                }
            } else if (c < 0) {
                return false;
            }
            if (c >= 0) {
                UCaseProps csp = UCaseProps.INSTANCE;
                UCaseProps.dummyStringBuilder.setLength(0);
                return csp.toFullFolding(c, UCaseProps.dummyStringBuilder, 0) >= 0;
            }
            String folded = UCharacter.foldCase(nfd, true);
            return !folded.equals(nfd);
        }
    }, new CaseBinaryProperty(55), new BinaryProperty(10){

        @Override
        boolean contains(int c) {
            Normalizer2Impl kcf = Norm2AllModes.getNFKC_CFInstance().impl;
            String src = UTF16.valueOf(c);
            StringBuilder dest = new StringBuilder();
            Normalizer2Impl.ReorderingBuffer buffer = new Normalizer2Impl.ReorderingBuffer(kcf, dest, 5);
            kcf.compose(src, 0, src.length(), false, true, buffer);
            return !Normalizer2Impl.UTF16Plus.equal(dest, src);
        }
    }, new BinaryProperty(2, 0x10000000), new BinaryProperty(2, 0x20000000), new BinaryProperty(2, 0x40000000), new BinaryProperty(2, Integer.MIN_VALUE), new BinaryProperty(2, 0x8000000), new BinaryProperty(2){

        @Override
        boolean contains(int c) {
            return 127462 <= c && c <= 127487;
        }
    }, new BinaryProperty(1, Integer.MIN_VALUE), new BinaryProperty(2, 0x4000000)};
    private static final int[] gcbToHst;
    IntProperty[] intProps = new IntProperty[]{new BiDiIntProperty(){

        @Override
        int getValue(int c) {
            return UBiDiProps.INSTANCE.getClass(c);
        }
    }, new IntProperty(0, 130816, 8), new CombiningClassIntProperty(8){

        @Override
        int getValue(int c) {
            return Normalizer2.getNFDInstance().getCombiningClass(c);
        }
    }, new IntProperty(2, 31, 0), new IntProperty(0, 917504, 17), new IntProperty(1){

        @Override
        int getValue(int c) {
            return UCharacterProperty.this.getType(c);
        }

        @Override
        int getMaxValue(int which) {
            return 29;
        }
    }, new BiDiIntProperty(){

        @Override
        int getValue(int c) {
            return UBiDiProps.INSTANCE.getJoiningGroup(c);
        }
    }, new BiDiIntProperty(){

        @Override
        int getValue(int c) {
            return UBiDiProps.INSTANCE.getJoiningType(c);
        }
    }, new IntProperty(2, 0x3F00000, 20), new IntProperty(1){

        @Override
        int getValue(int c) {
            return UCharacterProperty.ntvGetType(UCharacterProperty.getNumericTypeValue(UCharacterProperty.this.getProperty(c)));
        }

        @Override
        int getMaxValue(int which) {
            return 3;
        }
    }, new IntProperty(2){

        @Override
        int getValue(int c) {
            return UScript.getScript(c);
        }

        @Override
        int getMaxValue(int which) {
            int scriptX = UCharacterProperty.this.getMaxValues(0) & 0xF000FF;
            return UCharacterProperty.mergeScriptCodeOrIndex(scriptX);
        }
    }, new IntProperty(2){

        @Override
        int getValue(int c) {
            int gcb = (UCharacterProperty.this.getAdditional(c, 2) & 0x3E0) >>> 5;
            if (gcb < gcbToHst.length) {
                return gcbToHst[gcb];
            }
            return 0;
        }

        @Override
        int getMaxValue(int which) {
            return 5;
        }
    }, new NormQuickCheckIntProperty(8, 4108, 1), new NormQuickCheckIntProperty(9, 4109, 1), new NormQuickCheckIntProperty(8, 4110, 2), new NormQuickCheckIntProperty(9, 4111, 2), new CombiningClassIntProperty(8){

        @Override
        int getValue(int c) {
            return Norm2AllModes.getNFCInstance().impl.getFCD16(c) >> 8;
        }
    }, new CombiningClassIntProperty(8){

        @Override
        int getValue(int c) {
            return Norm2AllModes.getNFCInstance().impl.getFCD16(c) & 0xFF;
        }
    }, new IntProperty(2, 992, 5), new IntProperty(2, 1015808, 15), new IntProperty(2, 31744, 10), new BiDiIntProperty(){

        @Override
        int getValue(int c) {
            return UBiDiProps.INSTANCE.getPairedBracketType(c);
        }
    }, new IntProperty(12){

        @Override
        int getValue(int c) {
            CodePointTrie trie = LayoutProps.INSTANCE.inpcTrie;
            return trie != null ? trie.get(c) : 0;
        }

        @Override
        int getMaxValue(int which) {
            return LayoutProps.INSTANCE.maxInpcValue;
        }
    }, new IntProperty(13){

        @Override
        int getValue(int c) {
            CodePointTrie trie = LayoutProps.INSTANCE.inscTrie;
            return trie != null ? trie.get(c) : 0;
        }

        @Override
        int getMaxValue(int which) {
            return LayoutProps.INSTANCE.maxInscValue;
        }
    }, new IntProperty(14){

        @Override
        int getValue(int c) {
            CodePointTrie trie = LayoutProps.INSTANCE.voTrie;
            return trie != null ? trie.get(c) : 0;
        }

        @Override
        int getMaxValue(int which) {
            return LayoutProps.INSTANCE.maxVoValue;
        }
    }};
    Trie2_16 m_additionalTrie_;
    int[] m_additionalVectors_;
    int m_additionalColumnsCount_;
    int m_maxBlockScriptValue_;
    int m_maxJTGValue_;
    public char[] m_scriptExtensions_;
    private static final String DATA_FILE_NAME_ = "uprops.icu";
    private static final int NUMERIC_TYPE_VALUE_SHIFT_ = 6;
    private static final int NTV_NONE_ = 0;
    private static final int NTV_DECIMAL_START_ = 1;
    private static final int NTV_DIGIT_START_ = 11;
    private static final int NTV_NUMERIC_START_ = 21;
    private static final int NTV_FRACTION_START_ = 176;
    private static final int NTV_LARGE_START_ = 480;
    private static final int NTV_BASE60_START_ = 768;
    private static final int NTV_FRACTION20_START_ = 804;
    private static final int NTV_FRACTION32_START_ = 828;
    private static final int NTV_RESERVED_START_ = 844;
    public static final int SCRIPT_X_MASK = 0xF000FF;
    public static final int SCRIPT_HIGH_MASK = 0x300000;
    public static final int SCRIPT_HIGH_SHIFT = 12;
    public static final int MAX_SCRIPT = 1023;
    private static final int EAST_ASIAN_MASK_ = 917504;
    private static final int EAST_ASIAN_SHIFT_ = 17;
    private static final int BLOCK_MASK_ = 130816;
    private static final int BLOCK_SHIFT_ = 8;
    public static final int SCRIPT_LOW_MASK = 255;
    public static final int SCRIPT_X_WITH_COMMON = 0x400000;
    public static final int SCRIPT_X_WITH_INHERITED = 0x800000;
    public static final int SCRIPT_X_WITH_OTHER = 0xC00000;
    private static final int WHITE_SPACE_PROPERTY_ = 0;
    private static final int DASH_PROPERTY_ = 1;
    private static final int HYPHEN_PROPERTY_ = 2;
    private static final int QUOTATION_MARK_PROPERTY_ = 3;
    private static final int TERMINAL_PUNCTUATION_PROPERTY_ = 4;
    private static final int MATH_PROPERTY_ = 5;
    private static final int HEX_DIGIT_PROPERTY_ = 6;
    private static final int ASCII_HEX_DIGIT_PROPERTY_ = 7;
    private static final int ALPHABETIC_PROPERTY_ = 8;
    private static final int IDEOGRAPHIC_PROPERTY_ = 9;
    private static final int DIACRITIC_PROPERTY_ = 10;
    private static final int EXTENDER_PROPERTY_ = 11;
    private static final int NONCHARACTER_CODE_POINT_PROPERTY_ = 12;
    private static final int GRAPHEME_EXTEND_PROPERTY_ = 13;
    private static final int GRAPHEME_LINK_PROPERTY_ = 14;
    private static final int IDS_BINARY_OPERATOR_PROPERTY_ = 15;
    private static final int IDS_TRINARY_OPERATOR_PROPERTY_ = 16;
    private static final int RADICAL_PROPERTY_ = 17;
    private static final int UNIFIED_IDEOGRAPH_PROPERTY_ = 18;
    private static final int DEFAULT_IGNORABLE_CODE_POINT_PROPERTY_ = 19;
    private static final int DEPRECATED_PROPERTY_ = 20;
    private static final int LOGICAL_ORDER_EXCEPTION_PROPERTY_ = 21;
    private static final int XID_START_PROPERTY_ = 22;
    private static final int XID_CONTINUE_PROPERTY_ = 23;
    private static final int ID_START_PROPERTY_ = 24;
    private static final int ID_CONTINUE_PROPERTY_ = 25;
    private static final int GRAPHEME_BASE_PROPERTY_ = 26;
    private static final int S_TERM_PROPERTY_ = 27;
    private static final int VARIATION_SELECTOR_PROPERTY_ = 28;
    private static final int PATTERN_SYNTAX = 29;
    private static final int PATTERN_WHITE_SPACE = 30;
    private static final int PREPENDED_CONCATENATION_MARK = 31;
    private static final int PROPS_2_EXTENDED_PICTOGRAPHIC = 26;
    private static final int PROPS_2_EMOJI_COMPONENT = 27;
    private static final int PROPS_2_EMOJI = 28;
    private static final int PROPS_2_EMOJI_PRESENTATION = 29;
    private static final int PROPS_2_EMOJI_MODIFIER = 30;
    private static final int PROPS_2_EMOJI_MODIFIER_BASE = 31;
    private static final int LB_MASK = 0x3F00000;
    private static final int LB_SHIFT = 20;
    private static final int SB_MASK = 1015808;
    private static final int SB_SHIFT = 15;
    private static final int WB_MASK = 31744;
    private static final int WB_SHIFT = 10;
    private static final int GCB_MASK = 992;
    private static final int GCB_SHIFT = 5;
    private static final int DECOMPOSITION_TYPE_MASK_ = 31;
    private static final int FIRST_NIBBLE_SHIFT_ = 4;
    private static final int LAST_NIBBLE_MASK_ = 15;
    private static final int AGE_SHIFT_ = 24;
    private static final int DATA_FORMAT = 1431335535;
    private static final int TAB = 9;
    private static final int CR = 13;
    private static final int U_A = 65;
    private static final int U_F = 70;
    private static final int U_Z = 90;
    private static final int U_a = 97;
    private static final int U_f = 102;
    private static final int U_z = 122;
    private static final int DEL = 127;
    private static final int NL = 133;
    private static final int NBSP = 160;
    private static final int CGJ = 847;
    private static final int FIGURESP = 8199;
    private static final int HAIRSP = 8202;
    private static final int RLM = 8207;
    private static final int NNBSP = 8239;
    private static final int WJ = 8288;
    private static final int INHSWAP = 8298;
    private static final int NOMDIG = 8303;
    private static final int U_FW_A = 65313;
    private static final int U_FW_F = 65318;
    private static final int U_FW_Z = 65338;
    private static final int U_FW_a = 65345;
    private static final int U_FW_f = 65350;
    private static final int U_FW_z = 65370;
    private static final int ZWNBSP = 65279;

    public final int getProperty(int ch) {
        return this.m_trie_.get(ch);
    }

    public int getAdditional(int codepoint, int column) {
        assert (column >= 0);
        if (column >= this.m_additionalColumnsCount_) {
            return 0;
        }
        return this.m_additionalVectors_[this.m_additionalTrie_.get(codepoint) + column];
    }

    public VersionInfo getAge(int codepoint) {
        int version = this.getAdditional(codepoint, 0) >> 24;
        return VersionInfo.getInstance(version >> 4 & 0xF, version & 0xF, 0, 0);
    }

    private static final boolean isgraphPOSIX(int c) {
        return (UCharacterProperty.getMask(UCharacter.getType(c)) & (GC_CC_MASK | GC_CS_MASK | GC_CN_MASK | GC_Z_MASK)) == 0;
    }

    public boolean hasBinaryProperty(int c, int which) {
        if (which < 0 || 65 <= which) {
            return false;
        }
        return this.binProps[which].contains(c);
    }

    public int getType(int c) {
        return this.getProperty(c) & 0x1F;
    }

    public int getIntPropertyValue(int c, int which) {
        if (which < 4096) {
            if (0 <= which && which < 65) {
                return this.binProps[which].contains(c) ? 1 : 0;
            }
        } else {
            if (which < 4121) {
                return this.intProps[which - 4096].getValue(c);
            }
            if (which == 8192) {
                return UCharacterProperty.getMask(this.getType(c));
            }
        }
        return 0;
    }

    public int getIntPropertyMaxValue(int which) {
        if (which < 4096) {
            if (0 <= which && which < 65) {
                return 1;
            }
        } else if (which < 4121) {
            return this.intProps[which - 4096].getMaxValue(which);
        }
        return -1;
    }

    final int getSource(int which) {
        if (which < 0) {
            return 0;
        }
        if (which < 65) {
            return this.binProps[which].getSource();
        }
        if (which < 4096) {
            return 0;
        }
        if (which < 4121) {
            return this.intProps[which - 4096].getSource();
        }
        if (which < 16384) {
            switch (which) {
                case 8192: 
                case 12288: {
                    return 1;
                }
            }
            return 0;
        }
        if (which < 16398) {
            switch (which) {
                case 16384: {
                    return 2;
                }
                case 16385: {
                    return 5;
                }
                case 16386: 
                case 16388: 
                case 16390: 
                case 16391: 
                case 16392: 
                case 16393: 
                case 16394: 
                case 16396: {
                    return 4;
                }
                case 16387: 
                case 16389: 
                case 16395: {
                    return 3;
                }
            }
            return 0;
        }
        switch (which) {
            case 28672: {
                return 2;
            }
        }
        return 0;
    }

    public int getMaxValues(int column) {
        switch (column) {
            case 0: {
                return this.m_maxBlockScriptValue_;
            }
            case 2: {
                return this.m_maxJTGValue_;
            }
        }
        return 0;
    }

    public static final int getMask(int type) {
        return 1 << type;
    }

    public static int getEuropeanDigit(int ch) {
        if (ch > 122 && ch < 65313 || ch < 65 || ch > 90 && ch < 97 || ch > 65370 || ch > 65338 && ch < 65345) {
            return -1;
        }
        if (ch <= 122) {
            return ch + 10 - (ch <= 90 ? 65 : 97);
        }
        if (ch <= 65338) {
            return ch + 10 - 65313;
        }
        return ch + 10 - 65345;
    }

    public int digit(int c) {
        int value = UCharacterProperty.getNumericTypeValue(this.getProperty(c)) - 1;
        if (value <= 9) {
            return value;
        }
        return -1;
    }

    public int getNumericValue(int c) {
        int ntv = UCharacterProperty.getNumericTypeValue(this.getProperty(c));
        if (ntv == 0) {
            return UCharacterProperty.getEuropeanDigit(c);
        }
        if (ntv < 11) {
            return ntv - 1;
        }
        if (ntv < 21) {
            return ntv - 11;
        }
        if (ntv < 176) {
            return ntv - 21;
        }
        if (ntv < 480) {
            return -2;
        }
        if (ntv < 768) {
            int mant = (ntv >> 5) - 14;
            int exp = (ntv & 0x1F) + 2;
            if (exp < 9 || exp == 9 && mant <= 2) {
                int numValue = mant;
                do {
                    numValue *= 10;
                } while (--exp > 0);
                return numValue;
            }
            return -2;
        }
        if (ntv < 804) {
            int numValue = (ntv >> 2) - 191;
            int exp = (ntv & 3) + 1;
            switch (exp) {
                case 4: {
                    numValue *= 12960000;
                    break;
                }
                case 3: {
                    numValue *= 216000;
                    break;
                }
                case 2: {
                    numValue *= 3600;
                    break;
                }
                case 1: {
                    numValue *= 60;
                    break;
                }
            }
            return numValue;
        }
        if (ntv < 844) {
            return -2;
        }
        return -2;
    }

    public double getUnicodeNumericValue(int c) {
        int ntv = UCharacterProperty.getNumericTypeValue(this.getProperty(c));
        if (ntv == 0) {
            return -1.23456789E8;
        }
        if (ntv < 11) {
            return ntv - 1;
        }
        if (ntv < 21) {
            return ntv - 11;
        }
        if (ntv < 176) {
            return ntv - 21;
        }
        if (ntv < 480) {
            int numerator = (ntv >> 4) - 12;
            int denominator = (ntv & 0xF) + 1;
            return (double)numerator / (double)denominator;
        }
        if (ntv < 768) {
            int exp;
            int mant = (ntv >> 5) - 14;
            double numValue = mant;
            for (exp = (ntv & 0x1F) + 2; exp >= 4; exp -= 4) {
                numValue *= 10000.0;
            }
            switch (exp) {
                case 3: {
                    numValue *= 1000.0;
                    break;
                }
                case 2: {
                    numValue *= 100.0;
                    break;
                }
                case 1: {
                    numValue *= 10.0;
                    break;
                }
            }
            return numValue;
        }
        if (ntv < 804) {
            int numValue = (ntv >> 2) - 191;
            int exp = (ntv & 3) + 1;
            switch (exp) {
                case 4: {
                    numValue *= 12960000;
                    break;
                }
                case 3: {
                    numValue *= 216000;
                    break;
                }
                case 2: {
                    numValue *= 3600;
                    break;
                }
                case 1: {
                    numValue *= 60;
                    break;
                }
            }
            return numValue;
        }
        if (ntv < 828) {
            int frac20 = ntv - 804;
            int numerator = 2 * (frac20 & 3) + 1;
            int denominator = 20 << (frac20 >> 2);
            return (double)numerator / (double)denominator;
        }
        if (ntv < 844) {
            int frac32 = ntv - 828;
            int numerator = 2 * (frac32 & 3) + 1;
            int denominator = 32 << (frac32 >> 2);
            return (double)numerator / (double)denominator;
        }
        return -1.23456789E8;
    }

    private static final int getNumericTypeValue(int props) {
        return props >> 6;
    }

    private static final int ntvGetType(int ntv) {
        return ntv == 0 ? 0 : (ntv < 11 ? 1 : (ntv < 21 ? 2 : 3));
    }

    public static final int mergeScriptCodeOrIndex(int scriptX) {
        return (scriptX & 0x300000) >> 12 | scriptX & 0xFF;
    }

    private UCharacterProperty() throws IOException {
        int numChars;
        if (this.binProps.length != 65) {
            throw new ICUException("binProps.length!=UProperty.BINARY_LIMIT");
        }
        if (this.intProps.length != 25) {
            throw new ICUException("intProps.length!=(UProperty.INT_LIMIT-UProperty.INT_START)");
        }
        ByteBuffer bytes = ICUBinary.getRequiredData(DATA_FILE_NAME_);
        this.m_unicodeVersion_ = ICUBinary.readHeaderAndDataVersion(bytes, 1431335535, new IsAcceptable());
        int propertyOffset = bytes.getInt();
        bytes.getInt();
        bytes.getInt();
        int additionalOffset = bytes.getInt();
        int additionalVectorsOffset = bytes.getInt();
        this.m_additionalColumnsCount_ = bytes.getInt();
        int scriptExtensionsOffset = bytes.getInt();
        int reservedOffset7 = bytes.getInt();
        bytes.getInt();
        bytes.getInt();
        this.m_maxBlockScriptValue_ = bytes.getInt();
        this.m_maxJTGValue_ = bytes.getInt();
        ICUBinary.skipBytes(bytes, 16);
        this.m_trie_ = Trie2_16.createFromSerialized(bytes);
        int expectedTrieLength = (propertyOffset - 16) * 4;
        int trieLength = this.m_trie_.getSerializedLength();
        if (trieLength > expectedTrieLength) {
            throw new IOException("uprops.icu: not enough bytes for main trie");
        }
        ICUBinary.skipBytes(bytes, expectedTrieLength - trieLength);
        ICUBinary.skipBytes(bytes, (additionalOffset - propertyOffset) * 4);
        if (this.m_additionalColumnsCount_ > 0) {
            this.m_additionalTrie_ = Trie2_16.createFromSerialized(bytes);
            expectedTrieLength = (additionalVectorsOffset - additionalOffset) * 4;
            trieLength = this.m_additionalTrie_.getSerializedLength();
            if (trieLength > expectedTrieLength) {
                throw new IOException("uprops.icu: not enough bytes for additional-properties trie");
            }
            ICUBinary.skipBytes(bytes, expectedTrieLength - trieLength);
            int size = scriptExtensionsOffset - additionalVectorsOffset;
            this.m_additionalVectors_ = ICUBinary.getInts(bytes, size, 0);
        }
        if ((numChars = (reservedOffset7 - scriptExtensionsOffset) * 2) > 0) {
            this.m_scriptExtensions_ = ICUBinary.getChars(bytes, numChars, 0);
        }
    }

    public UnicodeSet addPropertyStarts(UnicodeSet set) {
        for (Trie2.Range range : this.m_trie_) {
            if (range.leadSurrogate) break;
            set.add(range.startCodePoint);
        }
        set.add(9);
        set.add(10);
        set.add(14);
        set.add(28);
        set.add(32);
        set.add(133);
        set.add(134);
        set.add(127);
        set.add(8202);
        set.add(8208);
        set.add(8298);
        set.add(8304);
        set.add(65279);
        set.add(65280);
        set.add(160);
        set.add(161);
        set.add(8199);
        set.add(8200);
        set.add(8239);
        set.add(8240);
        set.add(12295);
        set.add(12296);
        set.add(19968);
        set.add(19969);
        set.add(20108);
        set.add(20109);
        set.add(19977);
        set.add(19978);
        set.add(22235);
        set.add(22236);
        set.add(20116);
        set.add(20117);
        set.add(20845);
        set.add(20846);
        set.add(19971);
        set.add(19972);
        set.add(20843);
        set.add(20844);
        set.add(20061);
        set.add(20062);
        set.add(97);
        set.add(123);
        set.add(65);
        set.add(91);
        set.add(65345);
        set.add(65371);
        set.add(65313);
        set.add(65339);
        set.add(103);
        set.add(71);
        set.add(65351);
        set.add(65319);
        set.add(8288);
        set.add(65520);
        set.add(65532);
        set.add(917504);
        set.add(921600);
        set.add(847);
        set.add(848);
        return set;
    }

    public void upropsvec_addPropertyStarts(UnicodeSet set) {
        if (this.m_additionalColumnsCount_ > 0) {
            for (Trie2.Range range : this.m_additionalTrie_) {
                if (range.leadSurrogate) break;
                set.add(range.startCodePoint);
            }
        }
    }

    static UnicodeSet ulayout_addPropertyStarts(int src, UnicodeSet set) {
        return LayoutProps.INSTANCE.addPropertyStarts(src, set);
    }

    static {
        GC_CN_MASK = UCharacterProperty.getMask(0);
        GC_CC_MASK = UCharacterProperty.getMask(15);
        GC_CS_MASK = UCharacterProperty.getMask(18);
        GC_ZS_MASK = UCharacterProperty.getMask(12);
        GC_ZL_MASK = UCharacterProperty.getMask(13);
        GC_ZP_MASK = UCharacterProperty.getMask(14);
        GC_Z_MASK = GC_ZS_MASK | GC_ZL_MASK | GC_ZP_MASK;
        gcbToHst = new int[]{0, 0, 0, 0, 1, 0, 4, 5, 3, 2};
        try {
            INSTANCE = new UCharacterProperty();
        }
        catch (IOException e) {
            throw new MissingResourceException(e.getMessage(), "", "");
        }
    }

    private static final class IsAcceptable
    implements ICUBinary.Authenticate {
        private IsAcceptable() {
        }

        @Override
        public boolean isDataVersionAcceptable(byte[] version) {
            return version[0] == 7;
        }
    }

    private class NormQuickCheckIntProperty
    extends IntProperty {
        int which;
        int max;

        NormQuickCheckIntProperty(int source, int which, int max) {
            super(source);
            this.which = which;
            this.max = max;
        }

        @Override
        int getValue(int c) {
            return Norm2AllModes.getN2WithImpl(this.which - 4108).getQuickCheck(c);
        }

        @Override
        int getMaxValue(int which) {
            return this.max;
        }
    }

    private class CombiningClassIntProperty
    extends IntProperty {
        CombiningClassIntProperty(int source) {
            super(source);
        }

        @Override
        int getMaxValue(int which) {
            return 255;
        }
    }

    private class BiDiIntProperty
    extends IntProperty {
        BiDiIntProperty() {
            super(5);
        }

        @Override
        int getMaxValue(int which) {
            return UBiDiProps.INSTANCE.getMaxValue(which);
        }
    }

    private class IntProperty {
        int column;
        int mask;
        int shift;

        IntProperty(int column, int mask, int shift) {
            this.column = column;
            this.mask = mask;
            this.shift = shift;
        }

        IntProperty(int source) {
            this.column = source;
            this.mask = 0;
        }

        final int getSource() {
            return this.mask == 0 ? this.column : 2;
        }

        int getValue(int c) {
            return (UCharacterProperty.this.getAdditional(c, this.column) & this.mask) >>> this.shift;
        }

        int getMaxValue(int which) {
            return (UCharacterProperty.this.getMaxValues(this.column) & this.mask) >>> this.shift;
        }
    }

    private class NormInertBinaryProperty
    extends BinaryProperty {
        int which;

        NormInertBinaryProperty(int source, int which) {
            super(source);
            this.which = which;
        }

        @Override
        boolean contains(int c) {
            return Norm2AllModes.getN2WithImpl(this.which - 37).isInert(c);
        }
    }

    private class CaseBinaryProperty
    extends BinaryProperty {
        int which;

        CaseBinaryProperty(int which) {
            super(4);
            this.which = which;
        }

        @Override
        boolean contains(int c) {
            return UCaseProps.INSTANCE.hasBinaryProperty(c, this.which);
        }
    }

    private class BinaryProperty {
        int column;
        int mask;

        BinaryProperty(int column, int mask) {
            this.column = column;
            this.mask = mask;
        }

        BinaryProperty(int source) {
            this.column = source;
            this.mask = 0;
        }

        final int getSource() {
            return this.mask == 0 ? this.column : 2;
        }

        boolean contains(int c) {
            return (UCharacterProperty.this.getAdditional(c, this.column) & this.mask) != 0;
        }
    }

    private static final class LayoutProps {
        private static final IsAcceptable IS_ACCEPTABLE = new IsAcceptable();
        private static final int DATA_FORMAT = 1281456495;
        private static final int IX_INPC_TRIE_TOP = 1;
        private static final int IX_INSC_TRIE_TOP = 2;
        private static final int IX_VO_TRIE_TOP = 3;
        private static final int IX_MAX_VALUES = 9;
        private static final int MAX_INPC_SHIFT = 24;
        private static final int MAX_INSC_SHIFT = 16;
        private static final int MAX_VO_SHIFT = 8;
        static final LayoutProps INSTANCE = new LayoutProps();
        CodePointTrie inpcTrie = null;
        CodePointTrie inscTrie = null;
        CodePointTrie voTrie = null;
        int maxInpcValue = 0;
        int maxInscValue = 0;
        int maxVoValue = 0;

        LayoutProps() {
            ByteBuffer bytes = ICUBinary.getRequiredData("ulayout.icu");
            try {
                ICUBinary.readHeaderAndDataVersion(bytes, 1281456495, IS_ACCEPTABLE);
                int startPos = bytes.position();
                int indexesLength = bytes.getInt();
                if (indexesLength < 12) {
                    throw new ICUUncheckedIOException("Text layout properties data: not enough indexes");
                }
                int[] inIndexes = new int[indexesLength];
                inIndexes[0] = indexesLength;
                for (int i = 1; i < indexesLength; ++i) {
                    inIndexes[i] = bytes.getInt();
                }
                int top = inIndexes[1];
                int offset = indexesLength * 4;
                int trieSize = top - offset;
                if (trieSize >= 16) {
                    this.inpcTrie = CodePointTrie.fromBinary(null, null, bytes);
                }
                int pos = bytes.position() - startPos;
                assert (top >= pos);
                ICUBinary.skipBytes(bytes, top - pos);
                offset = top;
                top = inIndexes[2];
                trieSize = top - offset;
                if (trieSize >= 16) {
                    this.inscTrie = CodePointTrie.fromBinary(null, null, bytes);
                }
                pos = bytes.position() - startPos;
                assert (top >= pos);
                ICUBinary.skipBytes(bytes, top - pos);
                offset = top;
                top = inIndexes[3];
                trieSize = top - offset;
                if (trieSize >= 16) {
                    this.voTrie = CodePointTrie.fromBinary(null, null, bytes);
                }
                pos = bytes.position() - startPos;
                assert (top >= pos);
                ICUBinary.skipBytes(bytes, top - pos);
                int maxValues = inIndexes[9];
                this.maxInpcValue = maxValues >>> 24;
                this.maxInscValue = maxValues >> 16 & 0xFF;
                this.maxVoValue = maxValues >> 8 & 0xFF;
            }
            catch (IOException e) {
                throw new ICUUncheckedIOException(e);
            }
        }

        public UnicodeSet addPropertyStarts(int src, UnicodeSet set) {
            CodePointTrie trie;
            switch (src) {
                case 12: {
                    trie = this.inpcTrie;
                    break;
                }
                case 13: {
                    trie = this.inscTrie;
                    break;
                }
                case 14: {
                    trie = this.voTrie;
                    break;
                }
                default: {
                    throw new IllegalStateException();
                }
            }
            if (trie == null) {
                throw new MissingResourceException("no data for one of the text layout properties; src=" + src, "LayoutProps", "");
            }
            CodePointMap.Range range = new CodePointMap.Range();
            int start = 0;
            while (trie.getRange(start, null, range)) {
                set.add(start);
                start = range.getEnd() + 1;
            }
            return set;
        }

        private static final class IsAcceptable
        implements ICUBinary.Authenticate {
            private IsAcceptable() {
            }

            @Override
            public boolean isDataVersionAcceptable(byte[] version) {
                return version[0] == 1;
            }
        }
    }
}
