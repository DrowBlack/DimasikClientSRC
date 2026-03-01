package ca.weblite.objc;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public interface Runtime
extends Library {
    public static final Runtime INSTANCE = Native.loadLibrary("objc.A", Runtime.class);

    public Pointer objc_lookUpClass(String var1);

    public String class_getName(Pointer var1);

    public Pointer class_getProperty(Pointer var1, String var2);

    public Pointer class_getSuperclass(Pointer var1);

    public int class_getVersion(Pointer var1);

    public String class_getWeakIvarLayout(Pointer var1);

    public boolean class_isMetaClass(Pointer var1);

    public int class_getInstanceSize(Pointer var1);

    public Pointer class_getInstanceVariable(Pointer var1, String var2);

    public Pointer class_getInstanceMethod(Pointer var1, Pointer var2);

    public Pointer class_getClassMethod(Pointer var1, Pointer var2);

    public String class_getIvarLayout(Pointer var1);

    public Pointer class_getMethodImplementation(Pointer var1, Pointer var2);

    public Pointer class_getMethodImplementation_stret(Pointer var1, Pointer var2);

    public Pointer class_replaceMethod(Pointer var1, Pointer var2, Pointer var3, String var4);

    public Pointer class_respondsToSelector(Pointer var1, Pointer var2);

    public void class_setIvarLayout(Pointer var1, String var2);

    public Pointer class_setSuperclass(Pointer var1, Pointer var2);

    public void class_setVersion(Pointer var1, int var2);

    public void class_setWeakIvarLayout(Pointer var1, String var2);

    public String ivar_getName(Pointer var1);

    public long ivar_getOffset(Pointer var1);

    public String ivar_getTypeEncoding(Pointer var1);

    public String method_copyArgumentType(Pointer var1, int var2);

    public String method_copyReturnType(Pointer var1);

    public void method_exchangeImplementations(Pointer var1, Pointer var2);

    public void method_getArgumentType(Pointer var1, int var2, Pointer var3, long var4);

    public Pointer method_getImplementation(Pointer var1);

    public Pointer method_getName(Pointer var1);

    public int method_getNumberOfArguments(Pointer var1);

    public void method_getReturnType(Pointer var1, Pointer var2, long var3);

    public String method_getTypeEncoding(Pointer var1);

    public Pointer method_setImplementation(Pointer var1, Pointer var2);

    public Pointer objc_allocateClassPair(Pointer var1, String var2, long var3);

    public Pointer[] objc_copyProtocolList(Pointer var1);

    public Pointer objc_getAssociatedObject(Pointer var1, String var2);

    public Pointer objc_getClass(String var1);

    public int objc_getClassList(Pointer var1, int var2);

    public Pointer objc_getFutureClass(String var1);

    public Pointer objc_getMetaClass(String var1);

    public Pointer objc_getProtocol(String var1);

    public Pointer objc_getRequiredClass(String var1);

    public long objc_msgSend(Pointer var1, Pointer var2, Object ... var3);

    public long objc_msgSendSuper(Pointer var1, Pointer var2, Object ... var3);

    public long objc_msgSendSuper_stret(Pointer var1, Pointer var2, Object ... var3);

    public double objc_msgSend_fpret(Pointer var1, Pointer var2, Object ... var3);

    public void objc_msgSend_stret(Pointer var1, Pointer var2, Pointer var3, Object ... var4);

    public void objc_registerClassPair(Pointer var1);

    public void objc_removeAssociatedObjects(Pointer var1);

    public void objc_setAssociatedObject(Pointer var1, Pointer var2, Pointer var3, Pointer var4);

    public void objc_setFutureClass(Pointer var1, String var2);

    public Pointer object_copy(Pointer var1, long var2);

    public Pointer object_dispose(Pointer var1);

    public Pointer object_getClass(Pointer var1);

    public String object_getClassName(Pointer var1);

    public Pointer object_getIndexedIvars(Pointer var1);

    public Pointer object_getInstanceVariable(Pointer var1, String var2, Pointer var3);

    public Pointer object_getIvar(Pointer var1, Pointer var2);

    public Pointer object_setClass(Pointer var1, Pointer var2);

    public Pointer object_setInstanceVariable(Pointer var1, String var2, Pointer var3);

    public void object_setIvar(Pointer var1, Pointer var2, Pointer var3);

    public String property_getAttributes(Pointer var1);

    public boolean protocol_conformsToProtocol(Pointer var1, Pointer var2);

    public Structure protocol_copyMethodDescriptionList(Pointer var1, boolean var2, boolean var3, Pointer var4);

    public Pointer protocol_copyPropertyList(Pointer var1, Pointer var2);

    public Pointer protocol_copyProtocolList(Pointer var1, Pointer var2);

    public Pointer protocol_getMethodDescription(Pointer var1, Pointer var2, boolean var3, boolean var4);

    public String protocol_getName(Pointer var1);

    public Pointer protocol_getProperty(Pointer var1, String var2, boolean var3, boolean var4);

    public boolean protocol_isEqual(Pointer var1, Pointer var2);

    public String sel_getName(Pointer var1);

    public Pointer sel_getUid(String var1);

    public boolean sel_isEqual(Pointer var1, Pointer var2);

    public Pointer sel_registerName(String var1);
}
