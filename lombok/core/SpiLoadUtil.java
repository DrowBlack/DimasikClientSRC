package lombok.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SpiLoadUtil {
    private SpiLoadUtil() {
    }

    public static <T> List<T> readAllFromIterator(Iterable<T> findServices) {
        ArrayList<T> list = new ArrayList<T>();
        for (T t : findServices) {
            list.add(t);
        }
        return list;
    }

    public static <C> Iterable<C> findServices(Class<C> target) throws IOException {
        return SpiLoadUtil.findServices(target, Thread.currentThread().getContextClassLoader());
    }

    public static <C> Iterable<C> findServices(final Class<C> target, ClassLoader loader) throws IOException {
        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }
        Enumeration<URL> resources = loader.getResources("META-INF/services/" + target.getName());
        LinkedHashSet<String> entries = new LinkedHashSet<String>();
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            SpiLoadUtil.readServicesFromUrl(entries, url);
        }
        final Iterator names = entries.iterator();
        final ClassLoader fLoader = loader;
        return new Iterable<C>(){

            @Override
            public Iterator<C> iterator() {
                return new Iterator<C>(){

                    @Override
                    public boolean hasNext() {
                        return names.hasNext();
                    }

                    @Override
                    public C next() {
                        try {
                            return target.cast(Class.forName((String)names.next(), true, fLoader).getConstructor(new Class[0]).newInstance(new Object[0]));
                        }
                        catch (Exception e) {
                            Throwable t = e;
                            if (t instanceof InvocationTargetException) {
                                t = t.getCause();
                            }
                            if (t instanceof RuntimeException) {
                                throw (RuntimeException)t;
                            }
                            if (t instanceof Error) {
                                throw (Error)t;
                            }
                            throw new RuntimeException(t);
                        }
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    private static void readServicesFromUrl(Collection<String> list, URL url) throws IOException {
        BufferedReader r;
        InputStream in;
        block17: {
            in = url.openStream();
            r = null;
            if (in != null) break block17;
            try {
                if (r != null) {
                    r.close();
                }
                if (in != null) {
                    in.close();
                }
            }
            catch (Throwable throwable) {}
            return;
        }
        try {
            String line;
            r = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            while ((line = r.readLine()) != null) {
                int idx = line.indexOf(35);
                if (idx != -1) {
                    line = line.substring(0, idx);
                }
                if ((line = line.trim()).length() == 0) continue;
                list.add(line);
            }
        }
        catch (Throwable throwable) {
            try {
                if (r != null) {
                    r.close();
                }
                if (in != null) {
                    in.close();
                }
            }
            catch (Throwable throwable2) {}
            throw throwable;
        }
        try {
            if (r != null) {
                r.close();
            }
            if (in != null) {
                in.close();
            }
        }
        catch (Throwable throwable) {}
    }

    public static Class<? extends Annotation> findAnnotationClass(Class<?> c, Class<?> base) {
        if (c == Object.class || c == null) {
            return null;
        }
        Class<? extends Annotation> answer = null;
        answer = SpiLoadUtil.findAnnotationHelper(base, c.getGenericSuperclass());
        if (answer != null) {
            return answer;
        }
        Type[] typeArray = c.getGenericInterfaces();
        int n = typeArray.length;
        int n2 = 0;
        while (n2 < n) {
            Type iface = typeArray[n2];
            answer = SpiLoadUtil.findAnnotationHelper(base, iface);
            if (answer != null) {
                return answer;
            }
            ++n2;
        }
        Class<? extends Annotation> potential = SpiLoadUtil.findAnnotationClass(c.getSuperclass(), base);
        if (potential != null) {
            return potential;
        }
        Class<?>[] classArray = c.getInterfaces();
        int n3 = classArray.length;
        n = 0;
        while (n < n3) {
            Class<?> iface = classArray[n];
            potential = SpiLoadUtil.findAnnotationClass(iface, base);
            if (potential != null) {
                return potential;
            }
            ++n;
        }
        return null;
    }

    private static Class<? extends Annotation> findAnnotationHelper(Class<?> base, Type iface) {
        if (iface instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType)iface;
            if (!base.equals(p.getRawType())) {
                return null;
            }
            Type target = p.getActualTypeArguments()[0];
            if (target instanceof Class && Annotation.class.isAssignableFrom((Class)target)) {
                return (Class)target;
            }
            throw new ClassCastException("Not an annotation type: " + target);
        }
        return null;
    }
}
