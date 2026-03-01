package lombok.patcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Hook {
    private final String className;
    private final String methodName;
    private final String returnType;
    private final List<String> parameterTypes;
    private static final Map<String, String> PRIMITIVES;

    static {
        HashMap<String, String> m = new HashMap<String, String>();
        m.put("int", "I");
        m.put("long", "J");
        m.put("short", "S");
        m.put("byte", "B");
        m.put("char", "C");
        m.put("double", "D");
        m.put("float", "F");
        m.put("void", "V");
        m.put("boolean", "Z");
        PRIMITIVES = Collections.unmodifiableMap(m);
    }

    public Hook(String className, String methodName, String returnType, String ... parameterTypes) {
        if (className == null) {
            throw new NullPointerException("classSpec");
        }
        if (methodName == null) {
            throw new NullPointerException("methodName");
        }
        if (returnType == null) {
            throw new NullPointerException("returnType");
        }
        if (parameterTypes == null) {
            throw new NullPointerException("parameterTypes");
        }
        this.className = className;
        this.methodName = methodName;
        this.returnType = returnType;
        ArrayList<String> params = new ArrayList<String>();
        String[] stringArray = parameterTypes;
        int n = parameterTypes.length;
        int n2 = 0;
        while (n2 < n) {
            String param = stringArray[n2];
            params.add(param);
            ++n2;
        }
        this.parameterTypes = Collections.unmodifiableList(params);
    }

    public boolean isConstructor() {
        return "<init>".equals(this.methodName);
    }

    public String getClassName() {
        return this.className;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public String getReturnType() {
        return this.returnType;
    }

    public List<String> getParameterTypes() {
        return this.parameterTypes;
    }

    public String getClassSpec() {
        return Hook.convertType(this.className);
    }

    public String getMethodDescriptor() {
        StringBuilder out = new StringBuilder();
        out.append("(");
        for (String p : this.parameterTypes) {
            out.append(Hook.toSpec(p));
        }
        out.append(")");
        out.append(Hook.toSpec(this.returnType));
        return out.toString();
    }

    public static String toSpec(String type) {
        StringBuilder out = new StringBuilder();
        while (type.endsWith("[]")) {
            type = type.substring(0, type.length() - 2);
            out.append("[");
        }
        String p = PRIMITIVES.get(type);
        if (p != null) {
            out.append(p);
            return out.toString();
        }
        out.append("L");
        out.append(Hook.convertType(type));
        out.append(';');
        return out.toString();
    }

    public static String convertType(String type) {
        StringBuilder out = new StringBuilder();
        String[] stringArray = type.split("\\.");
        int n = stringArray.length;
        int n2 = 0;
        while (n2 < n) {
            String part = stringArray[n2];
            if (out.length() > 0) {
                out.append('/');
            }
            out.append(part);
            ++n2;
        }
        return out.toString();
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + (this.className == null ? 0 : this.className.hashCode());
        result = 31 * result + (this.methodName == null ? 0 : this.methodName.hashCode());
        result = 31 * result + (this.parameterTypes == null ? 0 : this.parameterTypes.hashCode());
        result = 31 * result + (this.returnType == null ? 0 : this.returnType.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Hook other = (Hook)obj;
        if (this.className == null ? other.className != null : !this.className.equals(other.className)) {
            return false;
        }
        if (this.methodName == null ? other.methodName != null : !this.methodName.equals(other.methodName)) {
            return false;
        }
        if (this.parameterTypes == null ? other.parameterTypes != null : !this.parameterTypes.equals(other.parameterTypes)) {
            return false;
        }
        return !(this.returnType == null ? other.returnType != null : !this.returnType.equals(other.returnType));
    }

    public String toString() {
        return "Hook [className=" + this.className + ", methodName=" + this.methodName + ", returnType=" + this.returnType + ", parameterTypes=" + this.parameterTypes + "]";
    }
}
