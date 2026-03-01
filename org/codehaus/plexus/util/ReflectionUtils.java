package org.codehaus.plexus.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.plexus.util.StringUtils;

public final class ReflectionUtils {
    public static Field getFieldByNameIncludingSuperclasses(String fieldName, Class<?> clazz) {
        Field retValue;
        block2: {
            retValue = null;
            try {
                retValue = clazz.getDeclaredField(fieldName);
            }
            catch (NoSuchFieldException e) {
                Class<?> superclass = clazz.getSuperclass();
                if (superclass == null) break block2;
                retValue = ReflectionUtils.getFieldByNameIncludingSuperclasses(fieldName, superclass);
            }
        }
        return retValue;
    }

    public static List<Field> getFieldsIncludingSuperclasses(Class<?> clazz) {
        ArrayList<Field> fields = new ArrayList<Field>(Arrays.asList(clazz.getDeclaredFields()));
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) {
            fields.addAll(ReflectionUtils.getFieldsIncludingSuperclasses(superclass));
        }
        return fields;
    }

    public static Method getSetter(String fieldName, Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        fieldName = "set" + StringUtils.capitalizeFirstLetter(fieldName);
        for (Method method : methods) {
            if (!method.getName().equals(fieldName) || !ReflectionUtils.isSetter(method)) continue;
            return method;
        }
        return null;
    }

    public static List<Method> getSetters(Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        ArrayList<Method> list = new ArrayList<Method>();
        for (Method method : methods) {
            if (!ReflectionUtils.isSetter(method)) continue;
            list.add(method);
        }
        return list;
    }

    public static Class<?> getSetterType(Method method) {
        if (!ReflectionUtils.isSetter(method)) {
            throw new RuntimeException("The method " + method.getDeclaringClass().getName() + "." + method.getName() + " is not a setter.");
        }
        return method.getParameterTypes()[0];
    }

    public static void setVariableValueInObject(Object object, String variable, Object value) throws IllegalAccessException {
        Field field = ReflectionUtils.getFieldByNameIncludingSuperclasses(variable, object.getClass());
        field.setAccessible(true);
        field.set(object, value);
    }

    public static Object getValueIncludingSuperclasses(String variable, Object object) throws IllegalAccessException {
        Field field = ReflectionUtils.getFieldByNameIncludingSuperclasses(variable, object.getClass());
        field.setAccessible(true);
        return field.get(object);
    }

    public static Map<String, Object> getVariablesAndValuesIncludingSuperclasses(Object object) throws IllegalAccessException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        ReflectionUtils.gatherVariablesAndValuesIncludingSuperclasses(object, map);
        return map;
    }

    public static boolean isSetter(Method method) {
        return method.getReturnType().equals(Void.TYPE) && !Modifier.isStatic(method.getModifiers()) && method.getParameterTypes().length == 1;
    }

    private static void gatherVariablesAndValuesIncludingSuperclasses(Object object, Map<String, Object> map) throws IllegalAccessException {
        Class<?> clazz = object.getClass();
        AccessibleObject[] fields = clazz.getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);
        for (AccessibleObject field : fields) {
            map.put(((Field)field).getName(), ((Field)field).get(object));
        }
        Class<?> superclass = clazz.getSuperclass();
        if (!Object.class.equals(superclass)) {
            ReflectionUtils.gatherVariablesAndValuesIncludingSuperclasses(superclass, map);
        }
    }
}
