package org.openjdk.nashorn.internal.runtime;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openjdk.nashorn.internal.runtime.Property;
import org.openjdk.nashorn.internal.runtime.Symbol;
import org.openjdk.nashorn.internal.runtime.options.Options;

public final class PropertyHashMap
implements Map<Object, Property> {
    private static final int INITIAL_BINS = 32;
    private static final int LIST_THRESHOLD = 8;
    private static final int QUEUE_THRESHOLD = Options.getIntProperty("nashorn.propmap.queue.threshold", 500);
    public static final PropertyHashMap EMPTY_HASHMAP = new PropertyHashMap();
    private final int size;
    private final int threshold;
    private final Element list;
    private final Element[] bins;
    private ElementQueue queue;
    private Property[] properties;

    private PropertyHashMap() {
        this.size = 0;
        this.threshold = 0;
        this.bins = null;
        this.queue = null;
        this.list = null;
    }

    private PropertyHashMap(MapBuilder map) {
        this.size = map.size;
        if (map.qhead == null) {
            this.bins = map.bins;
            this.queue = null;
        } else {
            this.bins = null;
            this.queue = new ElementQueue(map.qhead, map.bins);
        }
        this.list = map.list;
        this.threshold = map.bins != null ? PropertyHashMap.threeQuarters(map.bins.length) : 0;
    }

    public PropertyHashMap immutableReplace(Property property, Property newProperty) {
        assert (property.getKey().equals(newProperty.getKey())) : "replacing properties with different keys: '" + property.getKey() + "' != '" + newProperty.getKey() + "'";
        assert (this.findElement(property.getKey()) != null) : "replacing property that doesn't exist in map: '" + property.getKey() + "'";
        MapBuilder builder = this.newMapBuilder(this.size);
        builder.replaceProperty(property.getKey(), newProperty);
        return new PropertyHashMap(builder);
    }

    public PropertyHashMap immutableAdd(Property property) {
        int newSize = this.size + 1;
        MapBuilder builder = this.newMapBuilder(newSize);
        builder.addProperty(property);
        return new PropertyHashMap(builder);
    }

    public PropertyHashMap immutableAdd(Property ... newProperties) {
        int newSize = this.size + newProperties.length;
        MapBuilder builder = this.newMapBuilder(newSize);
        for (Property property : newProperties) {
            builder.addProperty(property);
        }
        return new PropertyHashMap(builder);
    }

    public PropertyHashMap immutableAdd(Collection<Property> newProperties) {
        if (newProperties != null) {
            int newSize = this.size + newProperties.size();
            MapBuilder builder = this.newMapBuilder(newSize);
            for (Property property : newProperties) {
                builder.addProperty(property);
            }
            return new PropertyHashMap(builder);
        }
        return this;
    }

    public PropertyHashMap immutableRemove(Object key) {
        MapBuilder builder = this.newMapBuilder(this.size);
        builder.removeProperty(key);
        if (builder.size < this.size) {
            return builder.size != 0 ? new PropertyHashMap(builder) : EMPTY_HASHMAP;
        }
        return this;
    }

    public Property find(Object key) {
        Element element = this.findElement(key);
        return element != null ? element.getProperty() : null;
    }

    Property[] getProperties() {
        if (this.properties == null) {
            Property[] array = new Property[this.size];
            int i = this.size;
            for (Element element = this.list; element != null; element = element.getLink()) {
                array[--i] = element.getProperty();
            }
            this.properties = array;
        }
        return this.properties;
    }

    private static int binIndex(Element[] bins, Object key) {
        return key.hashCode() & bins.length - 1;
    }

    private static int binsNeeded(int n) {
        return 1 << 32 - Integer.numberOfLeadingZeros(n + (n >>> 1) | 0x1F);
    }

    private static int threeQuarters(int n) {
        return (n >>> 1) + (n >>> 2);
    }

    private static Element[] rehash(Element list, int binSize) {
        Element[] newBins = new Element[binSize];
        for (Element element = list; element != null; element = element.getLink()) {
            Property property = element.getProperty();
            Object key = property.getKey();
            int binIndex = PropertyHashMap.binIndex(newBins, key);
            newBins[binIndex] = new Element(newBins[binIndex], property);
        }
        return newBins;
    }

    private Element findElement(Object key) {
        if (this.queue != null) {
            return this.queue.find(key);
        }
        if (this.bins != null) {
            int binIndex = PropertyHashMap.binIndex(this.bins, key);
            return PropertyHashMap.findElement(this.bins[binIndex], key);
        }
        return PropertyHashMap.findElement(this.list, key);
    }

    private static Element findElement(Element elementList, Object key) {
        int hashCode = key.hashCode();
        for (Element element = elementList; element != null; element = element.getLink()) {
            if (!element.match(key, hashCode)) continue;
            return element;
        }
        return null;
    }

    private MapBuilder newMapBuilder(int newSize) {
        if (this.bins == null && newSize < 8) {
            return new MapBuilder(this.bins, this.list, this.size, false);
        }
        if (newSize > this.threshold) {
            return new MapBuilder(PropertyHashMap.rehash(this.list, PropertyHashMap.binsNeeded(newSize)), this.list, this.size, true);
        }
        if (this.shouldCloneBins(this.size, newSize)) {
            return new MapBuilder(this.cloneBins(), this.list, this.size, true);
        }
        if (this.queue == null) {
            return new MapBuilder(this.bins, this.list, this.size, false);
        }
        return new MapBuilder(this.queue, this.list, this.size, false);
    }

    private Element[] cloneBins() {
        if (this.queue != null) {
            return this.queue.cloneAndMergeBins();
        }
        return (Element[])this.bins.clone();
    }

    private boolean shouldCloneBins(int oldSize, int newSize) {
        return newSize < QUEUE_THRESHOLD || newSize >>> 9 > oldSize >>> 9;
    }

    private static Element removeFromList(Element list, Object key) {
        Element head;
        if (list == null) {
            return null;
        }
        int hashCode = key.hashCode();
        if (list.match(key, hashCode)) {
            return list.getLink();
        }
        Element previous = head = new Element(null, list.getProperty());
        for (Element element = list.getLink(); element != null; element = element.getLink()) {
            if (element.match(key, hashCode)) {
                previous.setLink(element.getLink());
                return head;
            }
            Element next = new Element(null, element.getProperty());
            previous.setLink(next);
            previous = next;
        }
        return list;
    }

    private static Element replaceInList(Element list, Object key, Property property) {
        Element head;
        assert (list != null);
        int hashCode = key.hashCode();
        if (list.match(key, hashCode)) {
            return new Element(list.getLink(), property);
        }
        Element previous = head = new Element(null, list.getProperty());
        for (Element element = list.getLink(); element != null; element = element.getLink()) {
            if (element.match(key, hashCode)) {
                previous.setLink(new Element(element.getLink(), property));
                return head;
            }
            Element next = new Element(null, element.getProperty());
            previous.setLink(next);
            previous = next;
        }
        return list;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        assert (key instanceof String || key instanceof Symbol);
        return this.findElement(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        if (value instanceof Property) {
            Property property = (Property)value;
            Element element = this.findElement(property.getKey());
            return element != null && element.getProperty().equals(value);
        }
        return false;
    }

    @Override
    public Property get(Object key) {
        assert (key instanceof String || key instanceof Symbol);
        Element element = this.findElement(key);
        return element != null ? element.getProperty() : null;
    }

    @Override
    public Property put(Object key, Property value) {
        throw new UnsupportedOperationException("Immutable map.");
    }

    @Override
    public Property remove(Object key) {
        throw new UnsupportedOperationException("Immutable map.");
    }

    @Override
    public void putAll(Map<?, ? extends Property> m) {
        throw new UnsupportedOperationException("Immutable map.");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Immutable map.");
    }

    @Override
    public Set<Object> keySet() {
        HashSet<Object> set = new HashSet<Object>();
        for (Element element = this.list; element != null; element = element.getLink()) {
            set.add(element.getKey());
        }
        return Collections.unmodifiableSet(set);
    }

    @Override
    public Collection<Property> values() {
        return List.of(this.getProperties());
    }

    @Override
    public Set<Map.Entry<Object, Property>> entrySet() {
        HashSet<Element> set = new HashSet<Element>();
        for (Element element = this.list; element != null; element = element.getLink()) {
            set.add(element);
        }
        return Collections.unmodifiableSet(set);
    }

    private static class MapBuilder {
        private Element[] bins;
        private boolean hasOwnBins;
        private Element qhead;
        private Element list;
        private int size;

        MapBuilder(Element[] bins, Element list, int size, boolean hasOwnBins) {
            this.bins = bins;
            this.hasOwnBins = hasOwnBins;
            this.list = list;
            this.qhead = null;
            this.size = size;
        }

        MapBuilder(ElementQueue queue, Element list, int size, boolean hasOwnBins) {
            this.bins = queue.qbins;
            this.hasOwnBins = hasOwnBins;
            this.list = list;
            this.qhead = queue.qhead;
            this.size = size;
        }

        private void addProperty(Property property) {
            Object key = property.getKey();
            if (this.bins != null) {
                int binIndex = PropertyHashMap.binIndex(this.bins, key);
                if (PropertyHashMap.findElement(this.bins[binIndex], key) != null) {
                    this.ensureOwnBins();
                    this.bins[binIndex] = this.removeExistingElement(this.bins[binIndex], key);
                } else if (PropertyHashMap.findElement(this.qhead, key) != null) {
                    this.qhead = this.removeExistingElement(this.qhead, key);
                }
                if (this.hasOwnBins) {
                    this.bins[binIndex] = new Element(this.bins[binIndex], property);
                } else {
                    this.qhead = new Element(this.qhead, property);
                }
            } else if (PropertyHashMap.findElement(this.list, key) != null) {
                this.list = PropertyHashMap.removeFromList(this.list, key);
                --this.size;
            }
            this.list = new Element(this.list, property);
            ++this.size;
        }

        private void replaceProperty(Object key, Property property) {
            if (this.bins != null) {
                int binIndex = PropertyHashMap.binIndex(this.bins, key);
                Element bin = this.bins[binIndex];
                if (PropertyHashMap.findElement(bin, key) != null) {
                    this.ensureOwnBins();
                    this.bins[binIndex] = PropertyHashMap.replaceInList(bin, key, property);
                } else if (this.qhead != null) {
                    this.qhead = PropertyHashMap.replaceInList(this.qhead, key, property);
                }
            }
            this.list = PropertyHashMap.replaceInList(this.list, key, property);
        }

        void removeProperty(Object key) {
            if (this.bins != null) {
                int binIndex = PropertyHashMap.binIndex(this.bins, key);
                Element bin = this.bins[binIndex];
                if (PropertyHashMap.findElement(bin, key) != null) {
                    if (this.size >= 8) {
                        this.ensureOwnBins();
                        this.bins[binIndex] = PropertyHashMap.removeFromList(bin, key);
                    } else {
                        this.bins = null;
                        this.qhead = null;
                    }
                } else if (PropertyHashMap.findElement(this.qhead, key) != null) {
                    this.qhead = PropertyHashMap.removeFromList(this.qhead, key);
                }
            }
            this.list = PropertyHashMap.removeFromList(this.list, key);
            --this.size;
        }

        private Element removeExistingElement(Element element, Object key) {
            --this.size;
            this.list = PropertyHashMap.removeFromList(this.list, key);
            return PropertyHashMap.removeFromList(element, key);
        }

        private void ensureOwnBins() {
            if (!this.hasOwnBins) {
                this.bins = (Element[])this.bins.clone();
            }
            this.hasOwnBins = true;
        }
    }

    private class ElementQueue {
        private final Element qhead;
        private final Element[] qbins;
        int searchCount = 0;

        ElementQueue(Element qhead, Element[] qbins) {
            this.qhead = qhead;
            this.qbins = qbins;
        }

        Element find(Object key) {
            int binIndex = PropertyHashMap.binIndex(this.qbins, key);
            Element element = PropertyHashMap.findElement(this.qbins[binIndex], key);
            if (element != null) {
                return element;
            }
            if (this.qhead != null) {
                if (++this.searchCount > 2) {
                    Element[] newBins = this.cloneAndMergeBins();
                    assert (newBins != this.qbins);
                    PropertyHashMap.this.queue = new ElementQueue(null, newBins);
                    return PropertyHashMap.this.queue.find(key);
                }
                return PropertyHashMap.findElement(this.qhead, key);
            }
            return null;
        }

        private Element[] cloneAndMergeBins() {
            if (this.qhead == null) {
                return this.qbins;
            }
            Element[] newBins = (Element[])this.qbins.clone();
            for (Element element = this.qhead; element != null; element = element.getLink()) {
                Property property = element.getProperty();
                Object key = property.getKey();
                int binIndex = PropertyHashMap.binIndex(newBins, key);
                newBins[binIndex] = new Element(newBins[binIndex], property);
            }
            return newBins;
        }
    }

    static final class Element
    implements Map.Entry<Object, Property> {
        private Element link;
        private final Property property;
        private final Object key;
        private final int hashCode;

        Element(Element link, Property property) {
            this.link = link;
            this.property = property;
            this.key = property.getKey();
            this.hashCode = this.key.hashCode();
        }

        boolean match(Object otherKey, int otherHashCode) {
            return this.hashCode == otherHashCode && this.key.equals(otherKey);
        }

        @Override
        public boolean equals(Object other) {
            assert (this.property != null && other != null);
            return other instanceof Element && this.property.equals(((Element)other).property);
        }

        @Override
        public Object getKey() {
            return this.key;
        }

        @Override
        public Property getValue() {
            return this.property;
        }

        @Override
        public int hashCode() {
            return this.hashCode;
        }

        @Override
        public Property setValue(Property value) {
            throw new UnsupportedOperationException("Immutable map.");
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            Element elem = this;
            do {
                sb.append(elem.getValue());
                elem = elem.link;
                if (elem == null) continue;
                sb.append(" -> ");
            } while (elem != null);
            sb.append(']');
            return sb.toString();
        }

        Element getLink() {
            return this.link;
        }

        void setLink(Element link) {
            this.link = link;
        }

        Property getProperty() {
            return this.property;
        }
    }
}
