package net.optifine.reflect;

import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorClass;
import net.optifine.reflect.ReflectorField;

public class ReflectorFields {
    private ReflectorClass reflectorClass;
    private Class fieldType;
    private int fieldCount;
    private ReflectorField[] reflectorFields;

    public ReflectorFields(ReflectorClass reflectorClass, Class fieldType, int fieldCount) {
        this.reflectorClass = reflectorClass;
        this.fieldType = fieldType;
        this.fieldCount = fieldCount;
        if (reflectorClass.exists() && fieldType != null) {
            this.reflectorFields = new ReflectorField[fieldCount];
            for (int i = 0; i < this.reflectorFields.length; ++i) {
                this.reflectorFields[i] = new ReflectorField(reflectorClass, fieldType, i);
            }
        }
    }

    public ReflectorClass getReflectorClass() {
        return this.reflectorClass;
    }

    public Class getFieldType() {
        return this.fieldType;
    }

    public int getFieldCount() {
        return this.fieldCount;
    }

    public ReflectorField getReflectorField(int index) {
        return index >= 0 && index < this.reflectorFields.length ? this.reflectorFields[index] : null;
    }

    public Object getValue(Object obj, int index) {
        return Reflector.getFieldValue(obj, this, index);
    }

    public void setValue(Object obj, int index, Object val) {
        Reflector.setFieldValue(obj, this, index, val);
    }

    public boolean exists() {
        if (this.reflectorFields == null) {
            return false;
        }
        for (int i = 0; i < this.reflectorFields.length; ++i) {
            ReflectorField reflectorfield = this.reflectorFields[i];
            if (reflectorfield.exists()) continue;
            return false;
        }
        return true;
    }
}
