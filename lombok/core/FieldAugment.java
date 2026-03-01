package lombok.core;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import lombok.permit.Permit;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class FieldAugment<T, F> {
    private static Object getDefaultValue(Class<?> type) {
        if (type == Boolean.TYPE) {
            return false;
        }
        if (type == Integer.TYPE) {
            return 0;
        }
        if (!type.isPrimitive()) {
            return null;
        }
        if (type == Long.TYPE) {
            return 0L;
        }
        if (type == Short.TYPE) {
            return (short)0;
        }
        if (type == Byte.TYPE) {
            return (byte)0;
        }
        if (type == Character.TYPE) {
            return Character.valueOf('\u0000');
        }
        if (type == Float.TYPE) {
            return Float.valueOf(0.0f);
        }
        if (type == Double.TYPE) {
            return 0.0;
        }
        return null;
    }

    public static <T, F> FieldAugment<T, F> augment(Class<T> type, Class<? super F> fieldType, String name) {
        FieldAugment.checkNotNull(type, "type");
        FieldAugment.checkNotNull(fieldType, "fieldType");
        FieldAugment.checkNotNull(name, "name");
        if (type.isInterface()) {
            return new InterfaceFieldAugment(name, fieldType);
        }
        Object defaultValue = FieldAugment.getDefaultValue(fieldType);
        MapFieldAugment ret = FieldAugment.tryCreateReflectionAugment(type, fieldType, name, defaultValue);
        return ret != null ? ret : new MapFieldAugment(defaultValue);
    }

    public static <T, F> FieldAugment<T, F> circularSafeAugment(Class<T> type, Class<? super F> fieldType, String name) {
        FieldAugment.checkNotNull(type, "type");
        FieldAugment.checkNotNull(fieldType, "fieldType");
        FieldAugment.checkNotNull(name, "name");
        Object defaultValue = FieldAugment.getDefaultValue(fieldType);
        MapWeakFieldAugment ret = FieldAugment.tryCreateReflectionAugment(type, fieldType, name, defaultValue);
        return ret != null ? ret : new MapWeakFieldAugment(defaultValue);
    }

    private static <T, F> FieldAugment<T, F> tryCreateReflectionAugment(Class<T> type, Class<? super F> fieldType, String name, F defaultValue) {
        Field f = FieldAugment.findField(type, fieldType, name);
        if (f != null && FieldAugment.typeIsAssignmentCompatible(f.getType(), fieldType)) {
            return new ReflectionFieldAugment(f, fieldType, defaultValue);
        }
        return null;
    }

    private static Field findField(Class<?> type, Class<?> wantedType, String name) {
        Field f;
        block5: {
            block4: {
                try {
                    f = Permit.getField(type, name);
                    if (!Modifier.isStatic(f.getModifiers()) && !Modifier.isFinal(f.getModifiers())) break block4;
                    return null;
                }
                catch (Exception exception) {
                    return null;
                }
            }
            if (FieldAugment.typeIsAssignmentCompatible(f.getType(), wantedType)) break block5;
            return null;
        }
        return f;
    }

    private static boolean typeIsAssignmentCompatible(Class<?> fieldType, Class<?> wantedType) {
        if (fieldType == Object.class) {
            return true;
        }
        if (fieldType == wantedType) {
            return true;
        }
        if (fieldType.isPrimitive()) {
            return fieldType == wantedType;
        }
        if (wantedType == Integer.TYPE && (fieldType == Number.class || fieldType == Integer.class)) {
            return true;
        }
        if (wantedType == Long.TYPE && (fieldType == Number.class || fieldType == Long.class)) {
            return true;
        }
        if (wantedType == Short.TYPE && (fieldType == Number.class || fieldType == Short.class)) {
            return true;
        }
        if (wantedType == Byte.TYPE && (fieldType == Number.class || fieldType == Byte.class)) {
            return true;
        }
        if (wantedType == Character.TYPE && (fieldType == Number.class || fieldType == Character.class)) {
            return true;
        }
        if (wantedType == Float.TYPE && (fieldType == Number.class || fieldType == Float.class)) {
            return true;
        }
        if (wantedType == Double.TYPE && (fieldType == Number.class || fieldType == Double.class)) {
            return true;
        }
        if (wantedType == Boolean.TYPE && fieldType == Boolean.class) {
            return true;
        }
        return fieldType.isAssignableFrom(wantedType);
    }

    private FieldAugment() {
    }

    public abstract F get(T var1);

    public final void set(T object, F value) {
        this.getAndSet(object, value);
    }

    public abstract F getAndSet(T var1, F var2);

    public abstract F clear(T var1);

    public abstract F compareAndClear(T var1, F var2);

    public abstract F setIfAbsent(T var1, F var2);

    public abstract F compareAndSet(T var1, F var2, F var3);

    private static <T> T checkNotNull(T object, String name) {
        if (object == null) {
            throw new NullPointerException(name);
        }
        return object;
    }

    /* synthetic */ FieldAugment(FieldAugment fieldAugment) {
        this();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class InterfaceFieldAugment<T, F>
    extends FieldAugment<T, F> {
        private final String name;
        private final Class<? super F> fieldType;
        private Map<Class<T>, FieldAugment<T, F>> map = new HashMap<Class<T>, FieldAugment<T, F>>();

        private InterfaceFieldAugment(String name, Class<? super F> fieldType) {
            super(null);
            this.name = name;
            this.fieldType = fieldType;
        }

        private synchronized FieldAugment<T, F> getDelegate(T object) {
            Class<?> c = object.getClass();
            FieldAugment<Object, Object> fieldAugment = this.map.get(c);
            if (fieldAugment == null) {
                fieldAugment = InterfaceFieldAugment.augment(c, this.fieldType, this.name);
                this.map.put(c, fieldAugment);
            }
            return fieldAugment;
        }

        @Override
        public F get(T object) {
            return this.getDelegate(object).get(object);
        }

        @Override
        public F getAndSet(T object, F value) {
            return this.getDelegate(object).getAndSet(object, value);
        }

        @Override
        public F clear(T object) {
            return this.getDelegate(object).clear(object);
        }

        @Override
        public F compareAndClear(T object, F expected) {
            return this.getDelegate(object).compareAndClear(object, expected);
        }

        @Override
        public F setIfAbsent(T object, F value) {
            return this.getDelegate(object).setIfAbsent(object, value);
        }

        @Override
        public F compareAndSet(T object, F expected, F value) {
            return this.getDelegate(object).compareAndSet(object, expected, value);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class MapFieldAugment<T, F>
    extends FieldAugment<T, F> {
        final Map<T, Object> values = new WeakHashMap<T, Object>();
        final F defaultValue;

        MapFieldAugment(F defaultValue) {
            super(null);
            this.defaultValue = defaultValue;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public F get(T object) {
            FieldAugment.checkNotNull(object, "object");
            Map<T, Object> map = this.values;
            synchronized (map) {
                return this.read(object);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public F getAndSet(T object, F value) {
            FieldAugment.checkNotNull(object, "object");
            FieldAugment.checkNotNull(value, "value");
            Map<T, Object> map = this.values;
            synchronized (map) {
                F result = this.read(object);
                this.write(object, value);
                return result;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public F clear(T object) {
            FieldAugment.checkNotNull(object, "object");
            Map<T, Object> map = this.values;
            synchronized (map) {
                F result = this.read(object);
                this.values.remove(object);
                return result;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public F compareAndClear(T object, F expected) {
            FieldAugment.checkNotNull(object, "object");
            FieldAugment.checkNotNull(expected, "expected");
            Map<T, Object> map = this.values;
            synchronized (map) {
                F result;
                block5: {
                    result = this.read(object);
                    if (result != null) break block5;
                    return null;
                }
                if (!expected.equals(result)) {
                    return result;
                }
                this.values.remove(object);
                return null;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public F setIfAbsent(T object, F value) {
            FieldAugment.checkNotNull(object, "object");
            FieldAugment.checkNotNull(value, "value");
            Map<T, Object> map = this.values;
            synchronized (map) {
                F result = this.read(object);
                if (result != null) {
                    return result;
                }
                this.write(object, value);
                return value;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public F compareAndSet(T object, F expected, F value) {
            FieldAugment.checkNotNull(object, "object");
            FieldAugment.checkNotNull(expected, "expected");
            FieldAugment.checkNotNull(value, "value");
            Map<T, Object> map = this.values;
            synchronized (map) {
                F result = this.read(object);
                if (!expected.equals(result)) {
                    return result;
                }
                this.write(object, value);
                return value;
            }
        }

        F read(T object) {
            Object value = this.values.get(object);
            return (F)(value == null ? this.defaultValue : value);
        }

        void write(T object, F value) {
            this.values.put(object, value);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class MapWeakFieldAugment<T, F>
    extends MapFieldAugment<T, F> {
        MapWeakFieldAugment(F defaultValue) {
            super(defaultValue);
        }

        @Override
        F read(T object) {
            WeakReference read = (WeakReference)this.values.get(object);
            if (read == null) {
                return (F)this.defaultValue;
            }
            Object result = read.get();
            if (result == null) {
                this.values.remove(object);
            }
            return (F)(result == null ? this.defaultValue : result);
        }

        @Override
        void write(T object, F value) {
            this.values.put(object, new WeakReference<F>(value));
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class ReflectionFieldAugment<T, F>
    extends FieldAugment<T, F> {
        private final Object lock = new Object();
        private final Field field;
        private final Class<F> targetType;
        private final F defaultValue;

        ReflectionFieldAugment(Field field, Class<? super F> targetType, F defaultValue) {
            super(null);
            this.field = field;
            this.targetType = targetType;
            this.defaultValue = defaultValue;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public F get(T object) {
            FieldAugment.checkNotNull(object, "object");
            try {
                F value;
                Object object2 = this.lock;
                synchronized (object2) {
                    value = this.targetType.cast(this.field.get(object));
                }
                return value == null ? this.defaultValue : value;
            }
            catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public F getAndSet(T object, F value) {
            FieldAugment.checkNotNull(object, "object");
            FieldAugment.checkNotNull(value, "value");
            try {
                F oldValue;
                Object object2 = this.lock;
                synchronized (object2) {
                    oldValue = this.targetType.cast(this.field.get(object));
                    this.field.set(object, value);
                }
                return oldValue == null ? this.defaultValue : oldValue;
            }
            catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public F clear(T object) {
            FieldAugment.checkNotNull(object, "object");
            try {
                F oldValue;
                Object object2 = this.lock;
                synchronized (object2) {
                    oldValue = this.targetType.cast(this.field.get(object));
                    this.field.set(object, this.defaultValue);
                }
                return oldValue == null ? this.defaultValue : oldValue;
            }
            catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public F compareAndClear(T object, F expected) {
            FieldAugment.checkNotNull(object, "object");
            FieldAugment.checkNotNull(expected, "expected");
            try {
                F oldValue;
                Object object2 = this.lock;
                synchronized (object2) {
                    oldValue = this.targetType.cast(this.field.get(object));
                    if (expected.equals(oldValue)) {
                        this.field.set(object, this.defaultValue);
                        return this.defaultValue;
                    }
                }
                return oldValue;
            }
            catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public F setIfAbsent(T object, F value) {
            FieldAugment.checkNotNull(object, "object");
            FieldAugment.checkNotNull(value, "value");
            try {
                Object object2 = this.lock;
                synchronized (object2) {
                    F oldValue = this.targetType.cast(this.field.get(object));
                    if (oldValue != null && !oldValue.equals(this.defaultValue)) {
                        return oldValue;
                    }
                    this.field.set(object, value);
                    return value;
                }
            }
            catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public F compareAndSet(T object, F expected, F value) {
            FieldAugment.checkNotNull(object, "object");
            FieldAugment.checkNotNull(expected, "expected");
            FieldAugment.checkNotNull(value, "value");
            try {
                Object object2 = this.lock;
                synchronized (object2) {
                    F oldValue = this.targetType.cast(this.field.get(object));
                    if (!expected.equals(oldValue)) {
                        return oldValue == null ? this.defaultValue : oldValue;
                    }
                    this.field.set(object, value);
                    return value;
                }
            }
            catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
