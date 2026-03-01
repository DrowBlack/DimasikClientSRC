package org.codehaus.plexus.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class CollectionUtils {
    public static <K, V> Map<K, V> mergeMaps(Map<K, V> dominantMap, Map<K, V> recessiveMap) {
        if (dominantMap == null && recessiveMap == null) {
            return null;
        }
        if (dominantMap != null && recessiveMap == null) {
            return dominantMap;
        }
        if (dominantMap == null) {
            return recessiveMap;
        }
        HashMap<K, V> result = new HashMap<K, V>();
        Set<K> dominantMapKeys = dominantMap.keySet();
        Set<K> recessiveMapKeys = recessiveMap.keySet();
        Collection<K> contributingRecessiveKeys = CollectionUtils.subtract(recessiveMapKeys, CollectionUtils.intersection(dominantMapKeys, recessiveMapKeys));
        result.putAll(dominantMap);
        for (K key : contributingRecessiveKeys) {
            result.put(key, recessiveMap.get(key));
        }
        return result;
    }

    public static <K, V> Map<K, V> mergeMaps(Map<K, V>[] maps) {
        Map<K, V> result;
        if (maps.length == 0) {
            result = null;
        } else if (maps.length == 1) {
            result = maps[0];
        } else {
            result = CollectionUtils.mergeMaps(maps[0], maps[1]);
            for (int i = 2; i < maps.length; ++i) {
                result = CollectionUtils.mergeMaps(result, maps[i]);
            }
        }
        return result;
    }

    public static <E> Collection<E> intersection(Collection<E> a, Collection<E> b) {
        ArrayList list = new ArrayList();
        Map<E, Integer> mapa = CollectionUtils.getCardinalityMap(a);
        Map<E, Integer> mapb = CollectionUtils.getCardinalityMap(b);
        HashSet<E> elts = new HashSet<E>(a);
        elts.addAll(b);
        for (Object obj : elts) {
            int m = Math.min(CollectionUtils.getFreq(obj, mapa), CollectionUtils.getFreq(obj, mapb));
            for (int i = 0; i < m; ++i) {
                list.add(obj);
            }
        }
        return list;
    }

    public static <T> Collection<T> subtract(Collection<T> a, Collection<T> b) {
        ArrayList<T> list = new ArrayList<T>(a);
        for (T aB : b) {
            list.remove(aB);
        }
        return list;
    }

    public static <E> Map<E, Integer> getCardinalityMap(Collection<E> col) {
        HashMap<E, Integer> count = new HashMap<E, Integer>();
        for (E obj : col) {
            Integer c = (Integer)count.get(obj);
            if (null == c) {
                count.put(obj, 1);
                continue;
            }
            count.put(obj, c + 1);
        }
        return count;
    }

    public static <E> List<E> iteratorToList(Iterator<E> it) {
        if (it == null) {
            throw new NullPointerException("it cannot be null.");
        }
        ArrayList<E> list = new ArrayList<E>();
        while (it.hasNext()) {
            list.add(it.next());
        }
        return list;
    }

    private static <E> int getFreq(E obj, Map<E, Integer> freqMap) {
        try {
            Integer o = freqMap.get(obj);
            if (o != null) {
                return o;
            }
        }
        catch (NullPointerException nullPointerException) {
        }
        catch (NoSuchElementException noSuchElementException) {
            // empty catch block
        }
        return 0;
    }
}
