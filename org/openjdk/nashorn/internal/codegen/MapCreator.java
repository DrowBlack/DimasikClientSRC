package org.openjdk.nashorn.internal.codegen;

import java.util.ArrayList;
import java.util.List;
import org.openjdk.nashorn.internal.codegen.MapTuple;
import org.openjdk.nashorn.internal.ir.Symbol;
import org.openjdk.nashorn.internal.runtime.AccessorProperty;
import org.openjdk.nashorn.internal.runtime.Property;
import org.openjdk.nashorn.internal.runtime.PropertyMap;
import org.openjdk.nashorn.internal.runtime.ScriptObject;
import org.openjdk.nashorn.internal.runtime.SpillProperty;
import org.openjdk.nashorn.internal.runtime.arrays.ArrayIndex;

public class MapCreator<T> {
    private final Class<?> structure;
    private final List<MapTuple<T>> tuples;

    MapCreator(Class<? extends ScriptObject> structure, List<MapTuple<T>> tuples) {
        this.structure = structure;
        this.tuples = tuples;
    }

    PropertyMap makeFieldMap(boolean hasArguments, boolean dualFields, int fieldCount, int fieldMaximum, boolean evalCode) {
        ArrayList<Property> properties = new ArrayList<Property>();
        assert (this.tuples != null);
        for (MapTuple<T> tuple : this.tuples) {
            Class initialType;
            String key = tuple.key;
            Symbol symbol = tuple.symbol;
            Class clazz = initialType = dualFields ? tuple.getValueType() : Object.class;
            if (symbol == null || ArrayIndex.isValidArrayIndex(ArrayIndex.getArrayIndex(key))) continue;
            int flags = MapCreator.getPropertyFlags(symbol, hasArguments, evalCode, dualFields);
            AccessorProperty property = new AccessorProperty((Object)key, flags, this.structure, symbol.getFieldIndex(), initialType);
            properties.add(property);
        }
        return PropertyMap.newMap(properties, this.structure.getName(), fieldCount, fieldMaximum, 0);
    }

    PropertyMap makeSpillMap(boolean hasArguments, boolean dualFields) {
        ArrayList<Property> properties = new ArrayList<Property>();
        int spillIndex = 0;
        assert (this.tuples != null);
        for (MapTuple<T> tuple : this.tuples) {
            Class initialType;
            String key = tuple.key;
            Symbol symbol = tuple.symbol;
            Class clazz = initialType = dualFields ? tuple.getValueType() : Object.class;
            if (symbol == null || ArrayIndex.isValidArrayIndex(ArrayIndex.getArrayIndex(key))) continue;
            int flags = MapCreator.getPropertyFlags(symbol, hasArguments, false, dualFields);
            properties.add(new SpillProperty(key, flags, spillIndex++, initialType));
        }
        return PropertyMap.newMap(properties, this.structure.getName(), 0, 0, spillIndex);
    }

    static int getPropertyFlags(Symbol symbol, boolean hasArguments, boolean evalCode, boolean dualFields) {
        int flags = 0;
        if (symbol.isParam()) {
            flags |= 8;
        }
        if (hasArguments) {
            flags |= 0x10;
        }
        if (symbol.isScope() && !evalCode) {
            flags |= 4;
        }
        if (symbol.isFunctionDeclaration()) {
            flags |= 0x20;
        }
        if (symbol.isConst()) {
            flags |= 1;
        }
        if (symbol.isBlockScoped()) {
            flags |= 0x400;
        }
        if (symbol.isBlockScoped() && symbol.isScope()) {
            flags |= 0x200;
        }
        if (dualFields) {
            flags |= 0x800;
        }
        return flags;
    }
}
