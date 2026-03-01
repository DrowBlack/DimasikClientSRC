package org.openjdk.nashorn.internal.objects;

import java.util.Collections;
import jdk.dynalink.beans.StaticClass;
import org.openjdk.nashorn.internal.objects.Global;
import org.openjdk.nashorn.internal.runtime.Context;
import org.openjdk.nashorn.internal.runtime.FindProperty;
import org.openjdk.nashorn.internal.runtime.NativeJavaPackage;
import org.openjdk.nashorn.internal.runtime.PropertyMap;
import org.openjdk.nashorn.internal.runtime.ScriptObject;

public final class NativeJavaImporter
extends ScriptObject {
    private final Object[] args;
    private static PropertyMap $nasgenmap$;

    private NativeJavaImporter(Object[] args, ScriptObject proto, PropertyMap map) {
        super(proto, map);
        this.args = args;
    }

    private NativeJavaImporter(Object[] args, Global global) {
        this(args, global.getJavaImporterPrototype(), $nasgenmap$);
    }

    private NativeJavaImporter(Object[] args) {
        this(args, Global.instance());
    }

    @Override
    public String getClassName() {
        return "JavaImporter";
    }

    public static NativeJavaImporter constructor(boolean isNew, Object self, Object ... args) {
        return new NativeJavaImporter(args);
    }

    @Override
    protected FindProperty findProperty(Object key, boolean deep, boolean isScope, ScriptObject start) {
        String name;
        Object value;
        FindProperty find = super.findProperty(key, deep, isScope, start);
        if (find == null && key instanceof String && (value = this.createProperty(name = (String)key)) != null) {
            this.setObject(null, 0, key, value);
            return super.findProperty(key, deep, isScope, start);
        }
        return find;
    }

    private Object createProperty(String name) {
        int len = this.args.length;
        for (int i = len - 1; i > -1; --i) {
            Object obj = this.args[i];
            if (obj instanceof StaticClass) {
                if (!((StaticClass)obj).getRepresentedClass().getSimpleName().equals(name)) continue;
                return obj;
            }
            if (!(obj instanceof NativeJavaPackage)) continue;
            String pkgName = ((NativeJavaPackage)obj).getName();
            String fullName = pkgName.isEmpty() ? name : pkgName + "." + name;
            Context context = Global.instance().getContext();
            try {
                return StaticClass.forClass(context.findClass(fullName));
            }
            catch (ClassNotFoundException classNotFoundException) {
                // empty catch block
            }
        }
        return null;
    }

    static {
        NativeJavaImporter.$clinit$();
    }

    public static void $clinit$() {
        $nasgenmap$ = PropertyMap.newMap(Collections.EMPTY_LIST);
    }
}
