package org.codehaus.plexus.util.introspection;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Hashtable;
import java.util.Map;
import org.codehaus.plexus.util.introspection.MethodMap;

public class ClassMap {
    private static final CacheMiss CACHE_MISS = new CacheMiss();
    private static final Object OBJECT = new Object();
    private final Class clazz;
    private Map<String, Object> methodCache = new Hashtable<String, Object>();
    private final MethodMap methodMap = new MethodMap();

    public ClassMap(Class clazz) {
        this.clazz = clazz;
        this.populateMethodCache();
    }

    Class getCachedClass() {
        return this.clazz;
    }

    public Method findMethod(String name, Object[] params) throws MethodMap.AmbiguousException {
        String methodKey = ClassMap.makeMethodKey(name, params);
        Object cacheEntry = this.methodCache.get(methodKey);
        if (cacheEntry == CACHE_MISS) {
            return null;
        }
        if (cacheEntry == null) {
            try {
                cacheEntry = this.methodMap.find(name, params);
            }
            catch (MethodMap.AmbiguousException ae) {
                this.methodCache.put(methodKey, CACHE_MISS);
                throw ae;
            }
            if (cacheEntry == null) {
                this.methodCache.put(methodKey, CACHE_MISS);
            } else {
                this.methodCache.put(methodKey, cacheEntry);
            }
        }
        return (Method)cacheEntry;
    }

    private void populateMethodCache() {
        Method[] methods;
        for (Method method : methods = ClassMap.getAccessibleMethods(this.clazz)) {
            Method publicMethod = ClassMap.getPublicMethod(method);
            if (publicMethod == null) continue;
            this.methodMap.add(publicMethod);
            this.methodCache.put(this.makeMethodKey(publicMethod), publicMethod);
        }
    }

    private String makeMethodKey(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        StringBuilder methodKey = new StringBuilder(method.getName());
        for (Class<?> parameterType : parameterTypes) {
            if (parameterType.isPrimitive()) {
                if (parameterType.equals(Boolean.TYPE)) {
                    methodKey.append("java.lang.Boolean");
                    continue;
                }
                if (parameterType.equals(Byte.TYPE)) {
                    methodKey.append("java.lang.Byte");
                    continue;
                }
                if (parameterType.equals(Character.TYPE)) {
                    methodKey.append("java.lang.Character");
                    continue;
                }
                if (parameterType.equals(Double.TYPE)) {
                    methodKey.append("java.lang.Double");
                    continue;
                }
                if (parameterType.equals(Float.TYPE)) {
                    methodKey.append("java.lang.Float");
                    continue;
                }
                if (parameterType.equals(Integer.TYPE)) {
                    methodKey.append("java.lang.Integer");
                    continue;
                }
                if (parameterType.equals(Long.TYPE)) {
                    methodKey.append("java.lang.Long");
                    continue;
                }
                if (!parameterType.equals(Short.TYPE)) continue;
                methodKey.append("java.lang.Short");
                continue;
            }
            methodKey.append(parameterType.getName());
        }
        return methodKey.toString();
    }

    private static String makeMethodKey(String method, Object[] params) {
        StringBuilder methodKey = new StringBuilder().append(method);
        for (Object param : params) {
            Object arg = param;
            if (arg == null) {
                arg = OBJECT;
            }
            methodKey.append(arg.getClass().getName());
        }
        return methodKey.toString();
    }

    private static Method[] getAccessibleMethods(Class clazz) {
        Method[] methods = clazz.getMethods();
        if (Modifier.isPublic(clazz.getModifiers())) {
            return methods;
        }
        MethodInfo[] methodInfos = new MethodInfo[methods.length];
        int i = methods.length;
        while (i-- > 0) {
            methodInfos[i] = new MethodInfo(methods[i]);
        }
        int upcastCount = ClassMap.getAccessibleMethods(clazz, methodInfos, 0);
        if (upcastCount < methods.length) {
            methods = new Method[upcastCount];
        }
        int j = 0;
        for (MethodInfo methodInfo : methodInfos) {
            if (!methodInfo.upcast) continue;
            methods[j++] = methodInfo.method;
        }
        return methods;
    }

    private static int getAccessibleMethods(Class clazz, MethodInfo[] methodInfos, int upcastCount) {
        Class superclazz;
        int l = methodInfos.length;
        if (Modifier.isPublic(clazz.getModifiers())) {
            for (int i = 0; i < l && upcastCount < l; ++i) {
                try {
                    MethodInfo methodInfo = methodInfos[i];
                    if (methodInfo.upcast) continue;
                    methodInfo.tryUpcasting(clazz);
                    ++upcastCount;
                    continue;
                }
                catch (NoSuchMethodException methodInfo) {
                    // empty catch block
                }
            }
            if (upcastCount == l) {
                return upcastCount;
            }
        }
        if ((superclazz = clazz.getSuperclass()) != null && (upcastCount = ClassMap.getAccessibleMethods(superclazz, methodInfos, upcastCount)) == l) {
            return upcastCount;
        }
        Class<?>[] interfaces = clazz.getInterfaces();
        int i = interfaces.length;
        while (i-- > 0) {
            if ((upcastCount = ClassMap.getAccessibleMethods(interfaces[i], methodInfos, upcastCount)) != l) continue;
            return upcastCount;
        }
        return upcastCount;
    }

    public static Method getPublicMethod(Method method) {
        Class<?> clazz = method.getDeclaringClass();
        if ((clazz.getModifiers() & 1) != 0) {
            return method;
        }
        return ClassMap.getPublicMethod(clazz, method.getName(), method.getParameterTypes());
    }

    private static Method getPublicMethod(Class clazz, String name, Class[] paramTypes) {
        Class<?>[] interfaces;
        Method superclazzMethod;
        if ((clazz.getModifiers() & 1) != 0) {
            try {
                return clazz.getMethod(name, paramTypes);
            }
            catch (NoSuchMethodException e) {
                return null;
            }
        }
        Class superclazz = clazz.getSuperclass();
        if (superclazz != null && (superclazzMethod = ClassMap.getPublicMethod(superclazz, name, paramTypes)) != null) {
            return superclazzMethod;
        }
        for (Class<?> anInterface : interfaces = clazz.getInterfaces()) {
            Method interfaceMethod = ClassMap.getPublicMethod(anInterface, name, paramTypes);
            if (interfaceMethod == null) continue;
            return interfaceMethod;
        }
        return null;
    }

    private static final class MethodInfo {
        Method method = null;
        String name;
        Class[] parameterTypes;
        boolean upcast;

        MethodInfo(Method method) {
            this.name = method.getName();
            this.parameterTypes = method.getParameterTypes();
            this.upcast = false;
        }

        void tryUpcasting(Class clazz) throws NoSuchMethodException {
            this.method = clazz.getMethod(this.name, this.parameterTypes);
            this.name = null;
            this.parameterTypes = null;
            this.upcast = true;
        }
    }

    private static final class CacheMiss {
        private CacheMiss() {
        }
    }
}
