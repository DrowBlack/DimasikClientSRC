package org.codehaus.plexus.util.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.plexus.util.reflection.ReflectorException;

public final class Reflector {
    private static final String CONSTRUCTOR_METHOD_NAME = "$$CONSTRUCTOR$$";
    private static final String GET_INSTANCE_METHOD_NAME = "getInstance";
    private Map<String, Map<String, Map<String, Method>>> classMaps = new HashMap<String, Map<String, Map<String, Method>>>();

    public <T> T newInstance(Class<T> theClass, Object[] params) throws ReflectorException {
        if (params == null) {
            params = new Object[]{};
        }
        Class[] paramTypes = new Class[params.length];
        int len = params.length;
        for (int i = 0; i < len; ++i) {
            paramTypes[i] = params[i].getClass();
        }
        try {
            Constructor<T> con = this.getConstructor(theClass, paramTypes);
            if (con == null) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("Constructor not found for class: ");
                buffer.append(theClass.getName());
                buffer.append(" with specified or ancestor parameter classes: ");
                for (Class paramType : paramTypes) {
                    buffer.append(paramType.getName());
                    buffer.append(',');
                }
                buffer.setLength(buffer.length() - 1);
                throw new ReflectorException(buffer.toString());
            }
            return con.newInstance(params);
        }
        catch (InstantiationException ex) {
            throw new ReflectorException(ex);
        }
        catch (InvocationTargetException ex) {
            throw new ReflectorException(ex);
        }
        catch (IllegalAccessException ex) {
            throw new ReflectorException(ex);
        }
    }

    public <T> T getSingleton(Class<T> theClass, Object[] initParams) throws ReflectorException {
        Class[] paramTypes = new Class[initParams.length];
        int len = initParams.length;
        for (int i = 0; i < len; ++i) {
            paramTypes[i] = initParams[i].getClass();
        }
        try {
            Method method = this.getMethod(theClass, GET_INSTANCE_METHOD_NAME, paramTypes);
            return (T)method.invoke(null, initParams);
        }
        catch (InvocationTargetException ex) {
            throw new ReflectorException(ex);
        }
        catch (IllegalAccessException ex) {
            throw new ReflectorException(ex);
        }
    }

    public Object invoke(Object target, String methodName, Object[] params) throws ReflectorException {
        if (params == null) {
            params = new Object[]{};
        }
        Class[] paramTypes = new Class[params.length];
        int len = params.length;
        for (int i = 0; i < len; ++i) {
            paramTypes[i] = params[i].getClass();
        }
        try {
            Method method = this.getMethod(target.getClass(), methodName, paramTypes);
            if (method == null) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("Singleton-producing method named '").append(methodName).append("' not found with specified parameter classes: ");
                for (Class paramType : paramTypes) {
                    buffer.append(paramType.getName());
                    buffer.append(',');
                }
                buffer.setLength(buffer.length() - 1);
                throw new ReflectorException(buffer.toString());
            }
            return method.invoke(target, params);
        }
        catch (InvocationTargetException ex) {
            throw new ReflectorException(ex);
        }
        catch (IllegalAccessException ex) {
            throw new ReflectorException(ex);
        }
    }

    public Object getStaticField(Class targetClass, String fieldName) throws ReflectorException {
        try {
            Field field = targetClass.getField(fieldName);
            return field.get(null);
        }
        catch (SecurityException e) {
            throw new ReflectorException(e);
        }
        catch (NoSuchFieldException e) {
            throw new ReflectorException(e);
        }
        catch (IllegalArgumentException e) {
            throw new ReflectorException(e);
        }
        catch (IllegalAccessException e) {
            throw new ReflectorException(e);
        }
    }

    public Object getField(Object target, String fieldName) throws ReflectorException {
        return this.getField(target, fieldName, false);
    }

    public Object getField(Object target, String fieldName, boolean breakAccessibility) throws ReflectorException {
        for (Class<?> targetClass = target.getClass(); targetClass != null; targetClass = targetClass.getSuperclass()) {
            try {
                Field field = targetClass.getDeclaredField(fieldName);
                boolean accessibilityBroken = false;
                if (!field.isAccessible() && breakAccessibility) {
                    field.setAccessible(true);
                    accessibilityBroken = true;
                }
                Object result = field.get(target);
                if (accessibilityBroken) {
                    field.setAccessible(false);
                }
                return result;
            }
            catch (SecurityException e) {
                throw new ReflectorException(e);
            }
            catch (NoSuchFieldException e) {
                if (targetClass != Object.class) continue;
                throw new ReflectorException(e);
            }
            catch (IllegalAccessException e) {
                throw new ReflectorException(e);
            }
        }
        return null;
    }

    public Object invokeStatic(Class targetClass, String methodName, Object[] params) throws ReflectorException {
        if (params == null) {
            params = new Object[]{};
        }
        Class[] paramTypes = new Class[params.length];
        int len = params.length;
        for (int i = 0; i < len; ++i) {
            paramTypes[i] = params[i].getClass();
        }
        try {
            Method method = this.getMethod(targetClass, methodName, paramTypes);
            if (method == null) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("Singleton-producing method named '").append(methodName).append("' not found with specified parameter classes: ");
                for (Class paramType : paramTypes) {
                    buffer.append(paramType.getName());
                    buffer.append(',');
                }
                buffer.setLength(buffer.length() - 1);
                throw new ReflectorException(buffer.toString());
            }
            return method.invoke(null, params);
        }
        catch (InvocationTargetException ex) {
            throw new ReflectorException(ex);
        }
        catch (IllegalAccessException ex) {
            throw new ReflectorException(ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> Constructor<T> getConstructor(Class<T> targetClass, Class[] params) throws ReflectorException {
        Constructor<Object> constructor;
        Map<String, Constructor<T>> constructorMap = this.getConstructorMap(targetClass);
        StringBuilder key = new StringBuilder(200);
        key.append("(");
        for (Class param : params) {
            key.append(param.getName());
            key.append(",");
        }
        if (params.length > 0) {
            key.setLength(key.length() - 1);
        }
        key.append(")");
        String paramKey = key.toString();
        String string = paramKey.intern();
        synchronized (string) {
            constructor = constructorMap.get(paramKey);
            if (constructor == null) {
                Constructor<?>[] cands;
                for (Constructor<?> cand : cands = targetClass.getConstructors()) {
                    Class<?>[] types = cand.getParameterTypes();
                    if (params.length != types.length) continue;
                    int len2 = params.length;
                    for (int j = 0; j < len2; ++j) {
                        if (types[j].isAssignableFrom(params[j])) continue;
                    }
                    constructor = cand;
                    constructorMap.put(paramKey, constructor);
                }
            }
        }
        if (constructor == null) {
            throw new ReflectorException("Error retrieving constructor object for: " + targetClass.getName() + paramKey);
        }
        return constructor;
    }

    public Object getObjectProperty(Object target, String propertyName) throws ReflectorException {
        Object returnValue;
        Class[] emptyParams;
        Class<?> targetClass;
        Method method;
        if (propertyName == null || propertyName.trim().length() < 1) {
            throw new ReflectorException("Cannot retrieve value for empty property.");
        }
        String beanAccessor = "get" + Character.toUpperCase(propertyName.charAt(0));
        if (propertyName.trim().length() > 1) {
            beanAccessor = beanAccessor + propertyName.substring(1).trim();
        }
        if ((method = this._getMethod(targetClass = target.getClass(), beanAccessor, emptyParams = new Class[0])) == null) {
            method = this._getMethod(targetClass, propertyName, emptyParams);
        }
        if (method != null) {
            try {
                returnValue = method.invoke(target, new Object[0]);
            }
            catch (IllegalAccessException e) {
                throw new ReflectorException("Error retrieving property '" + propertyName + "' from '" + targetClass + "'", e);
            }
            catch (InvocationTargetException e) {
                throw new ReflectorException("Error retrieving property '" + propertyName + "' from '" + targetClass + "'", e);
            }
        }
        if (method != null) {
            try {
                returnValue = method.invoke(target, new Object[0]);
            }
            catch (IllegalAccessException e) {
                throw new ReflectorException("Error retrieving property '" + propertyName + "' from '" + targetClass + "'", e);
            }
            catch (InvocationTargetException e) {
                throw new ReflectorException("Error retrieving property '" + propertyName + "' from '" + targetClass + "'", e);
            }
        } else {
            returnValue = this.getField(target, propertyName, true);
            if (returnValue == null) {
                throw new ReflectorException("Neither method: '" + propertyName + "' nor bean accessor: '" + beanAccessor + "' can be found for class: '" + targetClass + "', and retrieval of field: '" + propertyName + "' returned null as value.");
            }
        }
        return returnValue;
    }

    public Method getMethod(Class targetClass, String methodName, Class[] params) throws ReflectorException {
        Method method = this._getMethod(targetClass, methodName, params);
        if (method == null) {
            throw new ReflectorException("Method: '" + methodName + "' not found in class: '" + targetClass + "'");
        }
        return method;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Method _getMethod(Class targetClass, String methodName, Class[] params) throws ReflectorException {
        Method method;
        Map<String, ?> methodMap = this.getMethodMap(targetClass, methodName);
        StringBuilder key = new StringBuilder(200);
        key.append("(");
        for (Class param : params) {
            key.append(param.getName());
            key.append(",");
        }
        key.append(")");
        String paramKey = key.toString();
        String string = paramKey.intern();
        synchronized (string) {
            method = (Method)methodMap.get(paramKey);
            if (method == null) {
                Method[] cands;
                for (Method cand : cands = targetClass.getMethods()) {
                    Class<?>[] types;
                    String name = cand.getName();
                    if (!methodName.equals(name) || params.length != (types = cand.getParameterTypes()).length) continue;
                    int len2 = params.length;
                    for (int j = 0; j < len2; ++j) {
                        if (types[j].isAssignableFrom(params[j])) continue;
                    }
                    method = cand;
                    methodMap.put(paramKey, method);
                }
            }
        }
        return method;
    }

    private <T> Map<String, Constructor<T>> getConstructorMap(Class<T> theClass) throws ReflectorException {
        return this.getMethodMap(theClass, CONSTRUCTOR_METHOD_NAME);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Map<String, ?> getMethodMap(Class theClass, String methodName) throws ReflectorException {
        Map<Object, Object> methodMap;
        if (theClass == null) {
            return null;
        }
        String className = theClass.getName();
        String string = className.intern();
        synchronized (string) {
            Map<String, Map<String, Method>> classMethods = this.classMaps.get(className);
            if (classMethods == null) {
                classMethods = new HashMap<String, Map<String, Method>>();
                methodMap = new HashMap();
                classMethods.put(methodName, methodMap);
                this.classMaps.put(className, classMethods);
            } else {
                String key = className + "::" + methodName;
                String string2 = key.intern();
                synchronized (string2) {
                    methodMap = classMethods.get(methodName);
                    if (methodMap == null) {
                        methodMap = new HashMap();
                        classMethods.put(methodName, methodMap);
                    }
                }
            }
        }
        return methodMap;
    }
}
