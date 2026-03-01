package org.codehaus.plexus.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class FastMap<K, V>
implements Map<K, V>,
Cloneable,
Serializable {
    private transient EntryImpl[] _entries;
    private transient int _capacity;
    private transient int _mask;
    private transient EntryImpl _poolFirst;
    private transient EntryImpl _mapFirst;
    private transient EntryImpl _mapLast;
    private transient int _size;
    private transient Values _values;
    private transient EntrySet _entrySet;
    private transient KeySet _keySet;

    public FastMap() {
        this.initialize(256);
    }

    public FastMap(Map map) {
        int capacity = map instanceof FastMap ? ((FastMap)map).capacity() : map.size();
        this.initialize(capacity);
        this.putAll(map);
    }

    public FastMap(int capacity) {
        this.initialize(capacity);
    }

    @Override
    public int size() {
        return this._size;
    }

    public int capacity() {
        return this._capacity;
    }

    @Override
    public boolean isEmpty() {
        return this._size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        EntryImpl entry = this._entries[FastMap.keyHash(key) & this._mask];
        while (entry != null) {
            if (key.equals(entry._key)) {
                return true;
            }
            entry = entry._next;
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        EntryImpl entry = this._mapFirst;
        while (entry != null) {
            if (value.equals(entry._value)) {
                return true;
            }
            entry = entry._after;
        }
        return false;
    }

    @Override
    public V get(Object key) {
        EntryImpl entry = this._entries[FastMap.keyHash(key) & this._mask];
        while (entry != null) {
            if (key.equals(entry._key)) {
                return (V)entry._value;
            }
            entry = entry._next;
        }
        return null;
    }

    public Map.Entry getEntry(Object key) {
        EntryImpl entry = this._entries[FastMap.keyHash(key) & this._mask];
        while (entry != null) {
            if (key.equals(entry._key)) {
                return entry;
            }
            entry = entry._next;
        }
        return null;
    }

    @Override
    public Object put(Object key, Object value) {
        EntryImpl entry = this._entries[FastMap.keyHash(key) & this._mask];
        while (entry != null) {
            if (key.equals(entry._key)) {
                Object prevValue = entry._value;
                entry._value = value;
                return prevValue;
            }
            entry = entry._next;
        }
        this.addEntry(key, value);
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            this.addEntry(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public V remove(Object key) {
        EntryImpl entry = this._entries[FastMap.keyHash(key) & this._mask];
        while (entry != null) {
            if (key.equals(entry._key)) {
                Object prevValue = entry._value;
                this.removeEntry(entry);
                return (V)prevValue;
            }
            entry = entry._next;
        }
        return null;
    }

    @Override
    public void clear() {
        EntryImpl entry = this._mapFirst;
        while (entry != null) {
            entry._key = null;
            entry._value = null;
            entry._before = null;
            entry._next = null;
            if (entry._previous == null) {
                this._entries[((EntryImpl)entry)._index] = null;
            } else {
                entry._previous = null;
            }
            entry = entry._after;
        }
        if (this._mapLast != null) {
            this._mapLast._after = this._poolFirst;
            this._poolFirst = this._mapFirst;
            this._mapFirst = null;
            this._mapLast = null;
            this._size = 0;
            this.sizeChanged();
        }
    }

    public void setCapacity(int newCapacity) {
        int tableLength;
        EntryImpl entry;
        int i;
        if (newCapacity > this._capacity) {
            for (i = this._capacity; i < newCapacity; ++i) {
                entry = new EntryImpl();
                entry._after = this._poolFirst;
                this._poolFirst = entry;
            }
        } else if (newCapacity < this._capacity) {
            for (i = newCapacity; i < this._capacity && this._poolFirst != null; ++i) {
                entry = this._poolFirst;
                this._poolFirst = entry._after;
                entry._after = null;
            }
        }
        for (tableLength = 16; tableLength < newCapacity; tableLength <<= 1) {
        }
        if (this._entries.length != tableLength) {
            this._entries = new EntryImpl[tableLength];
            this._mask = tableLength - 1;
            entry = this._mapFirst;
            while (entry != null) {
                int index = FastMap.keyHash(entry._key) & this._mask;
                entry._index = index;
                entry._previous = null;
                EntryImpl next = this._entries[index];
                entry._next = next;
                if (next != null) {
                    next._previous = entry;
                }
                this._entries[index] = entry;
                entry = entry._after;
            }
        }
        this._capacity = newCapacity;
    }

    public Object clone() {
        try {
            FastMap clone = (FastMap)super.clone();
            clone.initialize(this._capacity);
            clone.putAll(this);
            return clone;
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Map) {
            Map that = (Map)obj;
            if (this.size() == that.size()) {
                EntryImpl entry = this._mapFirst;
                while (entry != null) {
                    if (!that.entrySet().contains(entry)) {
                        return false;
                    }
                    entry = entry._after;
                }
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int code = 0;
        EntryImpl entry = this._mapFirst;
        while (entry != null) {
            code += entry.hashCode();
            entry = entry._after;
        }
        return code;
    }

    public String toString() {
        return this.entrySet().toString();
    }

    @Override
    public Collection values() {
        return this._values;
    }

    @Override
    public Set entrySet() {
        return this._entrySet;
    }

    @Override
    public Set keySet() {
        return this._keySet;
    }

    protected void sizeChanged() {
        if (this.size() > this.capacity()) {
            this.setCapacity(this.capacity() * 2);
        }
    }

    private static int keyHash(Object key) {
        int hashCode = key.hashCode();
        hashCode += ~(hashCode << 9);
        hashCode ^= hashCode >>> 14;
        hashCode += hashCode << 4;
        hashCode ^= hashCode >>> 10;
        return hashCode;
    }

    private void addEntry(Object key, Object value) {
        EntryImpl entry = this._poolFirst;
        if (entry != null) {
            this._poolFirst = entry._after;
            entry._after = null;
        } else {
            entry = new EntryImpl();
        }
        entry._key = key;
        entry._value = value;
        int index = FastMap.keyHash(key) & this._mask;
        entry._index = index;
        EntryImpl next = this._entries[index];
        entry._next = next;
        if (next != null) {
            next._previous = entry;
        }
        this._entries[index] = entry;
        if (this._mapLast != null) {
            entry._before = this._mapLast;
            this._mapLast._after = entry;
        } else {
            this._mapFirst = entry;
        }
        this._mapLast = entry;
        ++this._size;
        this.sizeChanged();
    }

    private void removeEntry(EntryImpl entry) {
        EntryImpl previous = entry._previous;
        EntryImpl next = entry._next;
        if (previous != null) {
            previous._next = next;
            entry._previous = null;
        } else {
            this._entries[((EntryImpl)entry)._index] = next;
        }
        if (next != null) {
            next._previous = previous;
            entry._next = null;
        }
        EntryImpl before = entry._before;
        EntryImpl after = entry._after;
        if (before != null) {
            before._after = after;
            entry._before = null;
        } else {
            this._mapFirst = after;
        }
        if (after != null) {
            after._before = before;
        } else {
            this._mapLast = before;
        }
        entry._key = null;
        entry._value = null;
        entry._after = this._poolFirst;
        this._poolFirst = entry;
        --this._size;
        this.sizeChanged();
    }

    private void initialize(int capacity) {
        int tableLength;
        for (tableLength = 16; tableLength < capacity; tableLength <<= 1) {
        }
        this._entries = new EntryImpl[tableLength];
        this._mask = tableLength - 1;
        this._capacity = capacity;
        this._size = 0;
        this._values = new Values();
        this._entrySet = new EntrySet();
        this._keySet = new KeySet();
        this._poolFirst = null;
        this._mapFirst = null;
        this._mapLast = null;
        for (int i = 0; i < capacity; ++i) {
            EntryImpl entry = new EntryImpl();
            entry._after = this._poolFirst;
            this._poolFirst = entry;
        }
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        int capacity = stream.readInt();
        this.initialize(capacity);
        int size = stream.readInt();
        for (int i = 0; i < size; ++i) {
            Object key = stream.readObject();
            Object value = stream.readObject();
            this.addEntry(key, value);
        }
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.writeInt(this._capacity);
        stream.writeInt(this._size);
        int count = 0;
        EntryImpl entry = this._mapFirst;
        while (entry != null) {
            stream.writeObject(entry._key);
            stream.writeObject(entry._value);
            ++count;
            entry = entry._after;
        }
        if (count != this._size) {
            throw new IOException("FastMap Corrupted");
        }
    }

    private static final class EntryImpl<K, V>
    implements Map.Entry<K, V> {
        private K _key;
        private V _value;
        private int _index;
        private EntryImpl _previous;
        private EntryImpl _next;
        private EntryImpl _before;
        private EntryImpl _after;

        private EntryImpl() {
        }

        @Override
        public K getKey() {
            return this._key;
        }

        @Override
        public V getValue() {
            return this._value;
        }

        @Override
        public V setValue(V value) {
            V old = this._value;
            this._value = value;
            return old;
        }

        @Override
        public boolean equals(Object that) {
            if (that instanceof Map.Entry) {
                Map.Entry entry = (Map.Entry)that;
                return this._key.equals(entry.getKey()) && (this._value != null ? this._value.equals(entry.getValue()) : entry.getValue() == null);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return this._key.hashCode() ^ (this._value != null ? this._value.hashCode() : 0);
        }

        public String toString() {
            return this._key + "=" + this._value;
        }
    }

    private class KeySet
    extends AbstractSet {
        private KeySet() {
        }

        @Override
        public Iterator iterator() {
            return new Iterator(){
                EntryImpl after;
                EntryImpl before;
                {
                    this.after = FastMap.this._mapFirst;
                }

                @Override
                public void remove() {
                    FastMap.this.removeEntry(this.before);
                }

                @Override
                public boolean hasNext() {
                    return this.after != null;
                }

                public Object next() {
                    this.before = this.after;
                    this.after = this.after._after;
                    return this.before._key;
                }
            };
        }

        @Override
        public int size() {
            return FastMap.this._size;
        }

        @Override
        public boolean contains(Object obj) {
            return FastMap.this.containsKey(obj);
        }

        @Override
        public boolean remove(Object obj) {
            return FastMap.this.remove(obj) != null;
        }

        @Override
        public void clear() {
            FastMap.this.clear();
        }
    }

    private class EntrySet
    extends AbstractSet {
        private EntrySet() {
        }

        @Override
        public Iterator iterator() {
            return new Iterator(){
                EntryImpl after;
                EntryImpl before;
                {
                    this.after = FastMap.this._mapFirst;
                }

                @Override
                public void remove() {
                    FastMap.this.removeEntry(this.before);
                }

                @Override
                public boolean hasNext() {
                    return this.after != null;
                }

                public Object next() {
                    this.before = this.after;
                    this.after = this.after._after;
                    return this.before;
                }
            };
        }

        @Override
        public int size() {
            return FastMap.this._size;
        }

        @Override
        public boolean contains(Object obj) {
            if (obj instanceof Map.Entry) {
                Map.Entry entry = (Map.Entry)obj;
                Map.Entry mapEntry = FastMap.this.getEntry(entry.getKey());
                return entry.equals(mapEntry);
            }
            return false;
        }

        @Override
        public boolean remove(Object obj) {
            Map.Entry entry;
            EntryImpl mapEntry;
            if (obj instanceof Map.Entry && (mapEntry = (EntryImpl)FastMap.this.getEntry((entry = (Map.Entry)obj).getKey())) != null && entry.getValue().equals(mapEntry._value)) {
                FastMap.this.removeEntry(mapEntry);
                return true;
            }
            return false;
        }
    }

    private class Values
    extends AbstractCollection {
        private Values() {
        }

        @Override
        public Iterator iterator() {
            return new Iterator(){
                EntryImpl after;
                EntryImpl before;
                {
                    this.after = FastMap.this._mapFirst;
                }

                @Override
                public void remove() {
                    FastMap.this.removeEntry(this.before);
                }

                @Override
                public boolean hasNext() {
                    return this.after != null;
                }

                public Object next() {
                    this.before = this.after;
                    this.after = this.after._after;
                    return this.before._value;
                }
            };
        }

        @Override
        public int size() {
            return FastMap.this._size;
        }

        @Override
        public boolean contains(Object o) {
            return FastMap.this.containsValue(o);
        }

        @Override
        public void clear() {
            FastMap.this.clear();
        }
    }
}
