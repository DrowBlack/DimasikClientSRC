package com.ibm.icu.impl;

import com.ibm.icu.impl.Norm2AllModes;
import com.ibm.icu.impl.UBiDiProps;
import com.ibm.icu.impl.UCaseProps;
import com.ibm.icu.impl.UCharacterProperty;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.UnicodeSet;

public final class CharacterPropertiesImpl {
    private static final int NUM_INCLUSIONS = 40;
    private static final UnicodeSet[] inclusions = new UnicodeSet[40];

    public static synchronized void clear() {
        for (int i = 0; i < inclusions.length; ++i) {
            CharacterPropertiesImpl.inclusions[i] = null;
        }
    }

    private static UnicodeSet getInclusionsForSource(int src) {
        if (inclusions[src] == null) {
            UnicodeSet incl = new UnicodeSet();
            switch (src) {
                case 1: {
                    UCharacterProperty.INSTANCE.addPropertyStarts(incl);
                    break;
                }
                case 2: {
                    UCharacterProperty.INSTANCE.upropsvec_addPropertyStarts(incl);
                    break;
                }
                case 6: {
                    UCharacterProperty.INSTANCE.addPropertyStarts(incl);
                    UCharacterProperty.INSTANCE.upropsvec_addPropertyStarts(incl);
                    break;
                }
                case 7: {
                    Norm2AllModes.getNFCInstance().impl.addPropertyStarts(incl);
                    UCaseProps.INSTANCE.addPropertyStarts(incl);
                    break;
                }
                case 8: {
                    Norm2AllModes.getNFCInstance().impl.addPropertyStarts(incl);
                    break;
                }
                case 9: {
                    Norm2AllModes.getNFKCInstance().impl.addPropertyStarts(incl);
                    break;
                }
                case 10: {
                    Norm2AllModes.getNFKC_CFInstance().impl.addPropertyStarts(incl);
                    break;
                }
                case 11: {
                    Norm2AllModes.getNFCInstance().impl.addCanonIterPropertyStarts(incl);
                    break;
                }
                case 4: {
                    UCaseProps.INSTANCE.addPropertyStarts(incl);
                    break;
                }
                case 5: {
                    UBiDiProps.INSTANCE.addPropertyStarts(incl);
                    break;
                }
                case 12: 
                case 13: 
                case 14: {
                    UCharacterProperty.ulayout_addPropertyStarts(src, incl);
                    break;
                }
                default: {
                    throw new IllegalStateException("getInclusions(unknown src " + src + ")");
                }
            }
            CharacterPropertiesImpl.inclusions[src] = incl.compact();
        }
        return inclusions[src];
    }

    private static UnicodeSet getIntPropInclusions(int prop) {
        assert (4096 <= prop && prop < 4121);
        int inclIndex = 15 + prop - 4096;
        if (inclusions[inclIndex] != null) {
            return inclusions[inclIndex];
        }
        int src = UCharacterProperty.INSTANCE.getSource(prop);
        UnicodeSet incl = CharacterPropertiesImpl.getInclusionsForSource(src);
        UnicodeSet intPropIncl = new UnicodeSet(0, 0);
        int numRanges = incl.getRangeCount();
        int prevValue = 0;
        for (int i = 0; i < numRanges; ++i) {
            int rangeEnd = incl.getRangeEnd(i);
            for (int c = incl.getRangeStart(i); c <= rangeEnd; ++c) {
                int value = UCharacter.getIntPropertyValue(c, prop);
                if (value == prevValue) continue;
                intPropIncl.add(c);
                prevValue = value;
            }
        }
        CharacterPropertiesImpl.inclusions[inclIndex] = intPropIncl.compact();
        return CharacterPropertiesImpl.inclusions[inclIndex];
    }

    public static synchronized UnicodeSet getInclusionsForProperty(int prop) {
        if (4096 <= prop && prop < 4121) {
            return CharacterPropertiesImpl.getIntPropInclusions(prop);
        }
        int src = UCharacterProperty.INSTANCE.getSource(prop);
        return CharacterPropertiesImpl.getInclusionsForSource(src);
    }
}
