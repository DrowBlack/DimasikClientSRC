package org.openjdk.nashorn.internal.codegen;

import java.util.List;
import org.openjdk.nashorn.internal.codegen.CodeGenerator;
import org.openjdk.nashorn.internal.codegen.CompilerConstants;
import org.openjdk.nashorn.internal.codegen.MapCreator;
import org.openjdk.nashorn.internal.codegen.MapTuple;
import org.openjdk.nashorn.internal.codegen.MethodEmitter;
import org.openjdk.nashorn.internal.codegen.ObjectClassGenerator;
import org.openjdk.nashorn.internal.codegen.ObjectCreator;
import org.openjdk.nashorn.internal.codegen.types.Type;
import org.openjdk.nashorn.internal.ir.Expression;
import org.openjdk.nashorn.internal.ir.LiteralNode;
import org.openjdk.nashorn.internal.runtime.JSType;
import org.openjdk.nashorn.internal.runtime.Property;
import org.openjdk.nashorn.internal.runtime.PropertyMap;
import org.openjdk.nashorn.internal.runtime.ScriptObject;
import org.openjdk.nashorn.internal.runtime.ScriptRuntime;
import org.openjdk.nashorn.internal.runtime.arrays.ArrayData;
import org.openjdk.nashorn.internal.runtime.arrays.ArrayIndex;
import org.openjdk.nashorn.internal.scripts.JD;
import org.openjdk.nashorn.internal.scripts.JO;

public final class SpillObjectCreator
extends ObjectCreator<Expression> {
    SpillObjectCreator(CodeGenerator codegen, List<MapTuple<Expression>> tuples) {
        super(codegen, tuples, false, false);
        this.makeMap();
    }

    @Override
    public void createObject(MethodEmitter method) {
        assert (!this.isScope()) : "spill scope objects are not currently supported";
        int length = this.tuples.size();
        boolean dualFields = this.codegen.useDualFields();
        int spillLength = ScriptObject.spillAllocationLength(length);
        long[] jpresetValues = dualFields ? new long[spillLength] : null;
        Object[] opresetValues = new Object[spillLength];
        Class<? extends ScriptObject> objectClass = this.getAllocatorClass();
        ArrayData arrayData = ArrayData.allocate(ScriptRuntime.EMPTY_ARRAY);
        int pos = 0;
        for (MapTuple tuple : this.tuples) {
            Object constantValue;
            String key = tuple.key;
            Expression value = (Expression)tuple.value;
            method.invalidateSpecialName(tuple.key);
            if (value != null && (constantValue = LiteralNode.objectAsConstant(value)) != LiteralNode.POSTSET_MARKER) {
                Property property = this.propertyMap.findProperty(key);
                if (property != null) {
                    property.setType(dualFields ? JSType.unboxedFieldType(constantValue) : Object.class);
                    int slot = property.getSlot();
                    if (dualFields && constantValue instanceof Number) {
                        jpresetValues[slot] = ObjectClassGenerator.pack((Number)constantValue);
                    } else {
                        opresetValues[slot] = constantValue;
                    }
                } else {
                    long oldLength = arrayData.length();
                    int index = ArrayIndex.getArrayIndex(key);
                    long longIndex = ArrayIndex.toLongIndex(index);
                    assert (ArrayIndex.isValidArrayIndex(index));
                    if (longIndex >= oldLength) {
                        arrayData = arrayData.ensure(longIndex);
                    }
                    arrayData = constantValue instanceof Integer ? arrayData.set(index, (Integer)constantValue, false) : (constantValue instanceof Double ? arrayData.set(index, (Double)constantValue, false) : arrayData.set(index, constantValue, false));
                    if (longIndex > oldLength) {
                        arrayData = arrayData.delete(oldLength, longIndex - 1L);
                    }
                }
            }
            ++pos;
        }
        method._new(objectClass).dup();
        this.codegen.loadConstant(this.propertyMap);
        if (dualFields) {
            this.codegen.loadConstant(jpresetValues);
        } else {
            method.loadNull();
        }
        this.codegen.loadConstant(opresetValues);
        method.invoke(CompilerConstants.constructorNoLookup(objectClass, PropertyMap.class, long[].class, Object[].class));
        if (arrayData.length() > 0L) {
            method.dup();
            this.codegen.loadConstant(arrayData);
            method.invoke(CompilerConstants.virtualCallNoLookup(ScriptObject.class, "setArray", Void.TYPE, ArrayData.class));
        }
    }

    @Override
    public void populateRange(MethodEmitter method, Type objectType, int objectSlot, int start, int end) {
        int callSiteFlags = this.codegen.getCallSiteFlags();
        method.load(objectType, objectSlot);
        for (int i = start; i < end; ++i) {
            MapTuple tuple = (MapTuple)this.tuples.get(i);
            if (LiteralNode.isConstant(tuple.value)) continue;
            Property property = this.propertyMap.findProperty(tuple.key);
            if (property == null) {
                int index = ArrayIndex.getArrayIndex(tuple.key);
                assert (ArrayIndex.isValidArrayIndex(index));
                method.dup();
                this.loadIndex(method, ArrayIndex.toLongIndex(index));
                this.loadTuple(method, tuple, false);
                method.dynamicSetIndex(callSiteFlags);
                continue;
            }
            assert (property.getKey() instanceof String);
            method.dup();
            this.loadTuple(method, tuple, false);
            method.dynamicSet((String)property.getKey(), this.codegen.getCallSiteFlags(), false);
        }
    }

    @Override
    protected PropertyMap makeMap() {
        assert (this.propertyMap == null) : "property map already initialized";
        Class<? extends ScriptObject> clazz = this.getAllocatorClass();
        this.propertyMap = new MapCreator(clazz, this.tuples).makeSpillMap(false, this.codegen.useDualFields());
        return this.propertyMap;
    }

    @Override
    protected void loadValue(Expression expr, Type type) {
        this.codegen.loadExpressionAsType(expr, Type.generic(type));
    }

    @Override
    protected Class<? extends ScriptObject> getAllocatorClass() {
        return this.codegen.useDualFields() ? JD.class : JO.class;
    }
}
