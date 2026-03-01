package com.ibm.icu.util;

import com.ibm.icu.util.ULocale;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocalePriorityList
implements Iterable<ULocale> {
    private static final Double D1 = 1.0;
    private static final Pattern languageSplitter = Pattern.compile("\\s*,\\s*");
    private static final Pattern weightSplitter = Pattern.compile("\\s*(\\S*)\\s*;\\s*q\\s*=\\s*(\\S*)");
    private final Map<ULocale, Double> languagesAndWeights;
    private static Comparator<Double> myDescendingDouble = new Comparator<Double>(){

        @Override
        public int compare(Double o1, Double o2) {
            int result = o1.compareTo(o2);
            return result > 0 ? -1 : (result < 0 ? 1 : 0);
        }
    };

    public static Builder add(ULocale ... locales) {
        return new Builder().add(locales);
    }

    public static Builder add(ULocale locale, double weight) {
        return new Builder().add(locale, weight);
    }

    public static Builder add(LocalePriorityList list) {
        return new Builder(list);
    }

    public static Builder add(String acceptLanguageString) {
        return new Builder().add(acceptLanguageString);
    }

    public Double getWeight(ULocale locale) {
        return this.languagesAndWeights.get(locale);
    }

    public Set<ULocale> getULocales() {
        return this.languagesAndWeights.keySet();
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<ULocale, Double> entry : this.languagesAndWeights.entrySet()) {
            ULocale language = entry.getKey();
            double weight = entry.getValue();
            if (result.length() != 0) {
                result.append(", ");
            }
            result.append(language);
            if (weight == 1.0) continue;
            result.append(";q=").append(weight);
        }
        return result.toString();
    }

    @Override
    public Iterator<ULocale> iterator() {
        return this.languagesAndWeights.keySet().iterator();
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        try {
            LocalePriorityList that = (LocalePriorityList)o;
            return this.languagesAndWeights.equals(that.languagesAndWeights);
        }
        catch (RuntimeException e) {
            return false;
        }
    }

    public int hashCode() {
        return this.languagesAndWeights.hashCode();
    }

    private LocalePriorityList(Map<ULocale, Double> languageToWeight) {
        this.languagesAndWeights = languageToWeight;
    }

    public static class Builder {
        private Map<ULocale, Double> languageToWeight;
        private LocalePriorityList built;
        private boolean hasWeights = false;

        private Builder() {
            this.languageToWeight = new LinkedHashMap<ULocale, Double>();
        }

        private Builder(LocalePriorityList list) {
            this.built = list;
            for (Double value : list.languagesAndWeights.values()) {
                double weight = value;
                assert (0.0 < weight && weight <= 1.0);
                if (weight == 1.0) continue;
                this.hasWeights = true;
                break;
            }
        }

        public LocalePriorityList build() {
            return this.build(false);
        }

        public LocalePriorityList build(boolean preserveWeights) {
            Map<ULocale, Double> temp;
            if (this.built != null) {
                return this.built;
            }
            if (this.hasWeights) {
                TreeMap<Double, LinkedList<ULocale>> weightToLanguages = new TreeMap<Double, LinkedList<ULocale>>(myDescendingDouble);
                for (Map.Entry<ULocale, Double> entry : this.languageToWeight.entrySet()) {
                    ULocale lang = entry.getKey();
                    Double weight = entry.getValue();
                    LinkedList<ULocale> s = (LinkedList<ULocale>)weightToLanguages.get(weight);
                    if (s == null) {
                        s = new LinkedList<ULocale>();
                        weightToLanguages.put(weight, s);
                    }
                    s.add(lang);
                }
                if (weightToLanguages.size() <= 1) {
                    temp = this.languageToWeight;
                    if (weightToLanguages.isEmpty() || (Double)weightToLanguages.firstKey() == 1.0) {
                        this.hasWeights = false;
                    }
                } else {
                    temp = new LinkedHashMap<ULocale, Double>();
                    for (Map.Entry<ULocale, Double> entry : weightToLanguages.entrySet()) {
                        Double weight = preserveWeights ? (Double)((Object)entry.getKey()) : D1;
                        for (ULocale lang : (List)((Object)entry.getValue())) {
                            temp.put(lang, weight);
                        }
                    }
                }
            } else {
                temp = this.languageToWeight;
            }
            this.languageToWeight = null;
            this.built = new LocalePriorityList(Collections.unmodifiableMap(temp));
            return this.built;
        }

        public Builder add(LocalePriorityList list) {
            for (Map.Entry entry : list.languagesAndWeights.entrySet()) {
                this.add((ULocale)entry.getKey(), (Double)entry.getValue());
            }
            return this;
        }

        public Builder add(ULocale locale) {
            return this.add(locale, 1.0);
        }

        public Builder add(ULocale ... locales) {
            for (ULocale languageCode : locales) {
                this.add(languageCode, 1.0);
            }
            return this;
        }

        public Builder add(ULocale locale, double weight) {
            Double value;
            if (this.languageToWeight == null) {
                this.languageToWeight = new LinkedHashMap<ULocale, Double>(this.built.languagesAndWeights);
                this.built = null;
            }
            if (this.languageToWeight.containsKey(locale)) {
                this.languageToWeight.remove(locale);
            }
            if (weight <= 0.0) {
                return this;
            }
            if (weight >= 1.0) {
                value = D1;
            } else {
                value = weight;
                this.hasWeights = true;
            }
            this.languageToWeight.put(locale, value);
            return this;
        }

        public Builder add(String acceptLanguageList) {
            String[] items = languageSplitter.split(acceptLanguageList.trim());
            Matcher itemMatcher = weightSplitter.matcher("");
            for (String item : items) {
                if (itemMatcher.reset(item).matches()) {
                    ULocale language = new ULocale(itemMatcher.group(1));
                    double weight = Double.parseDouble(itemMatcher.group(2));
                    if (!(0.0 <= weight) || !(weight <= 1.0)) {
                        throw new IllegalArgumentException("Illegal weight, must be 0..1: " + weight);
                    }
                    this.add(language, weight);
                    continue;
                }
                if (item.length() == 0) continue;
                this.add(new ULocale(item));
            }
            return this;
        }
    }
}
