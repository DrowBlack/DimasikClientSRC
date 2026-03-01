package com.ibm.icu.text;

import com.ibm.icu.lang.CharSequences;
import com.ibm.icu.text.Normalizer2;
import com.ibm.icu.text.Transform;
import com.ibm.icu.text.Transliterator;
import com.ibm.icu.text.UTF16;
import com.ibm.icu.text.UnicodeSet;
import java.util.HashSet;
import java.util.Set;

class SourceTargetUtility {
    final Transform<String, String> transform;
    final UnicodeSet sourceCache;
    final Set<String> sourceStrings;
    static final UnicodeSet NON_STARTERS = new UnicodeSet("[:^ccc=0:]").freeze();
    static Normalizer2 NFC = Normalizer2.getNFCInstance();

    public SourceTargetUtility(Transform<String, String> transform) {
        this(transform, null);
    }

    public SourceTargetUtility(Transform<String, String> transform, Normalizer2 normalizer) {
        this.transform = transform;
        this.sourceCache = normalizer != null ? new UnicodeSet("[:^ccc=0:]") : new UnicodeSet();
        this.sourceStrings = new HashSet<String>();
        for (int i = 0; i <= 0x10FFFF; ++i) {
            String d;
            String s = transform.transform(UTF16.valueOf(i));
            boolean added = false;
            if (!CharSequences.equals(i, s)) {
                this.sourceCache.add(i);
                added = true;
            }
            if (normalizer == null || (d = NFC.getDecomposition(i)) == null) continue;
            s = transform.transform(d);
            if (!d.equals(s)) {
                this.sourceStrings.add(d);
            }
            if (added || normalizer.isInert(i)) continue;
            this.sourceCache.add(i);
        }
        this.sourceCache.freeze();
    }

    public void addSourceTargetSet(Transliterator transliterator, UnicodeSet inputFilter, UnicodeSet sourceSet, UnicodeSet targetSet) {
        UnicodeSet myFilter = transliterator.getFilterAsUnicodeSet(inputFilter);
        UnicodeSet affectedCharacters = new UnicodeSet(this.sourceCache).retainAll(myFilter);
        sourceSet.addAll(affectedCharacters);
        for (String s : affectedCharacters) {
            targetSet.addAll(this.transform.transform(s));
        }
        for (String s : this.sourceStrings) {
            String t;
            if (!myFilter.containsAll(s) || s.equals(t = this.transform.transform(s))) continue;
            targetSet.addAll(t);
            sourceSet.addAll(s);
        }
    }
}
