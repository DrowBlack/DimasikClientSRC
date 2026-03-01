package lombok.patcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.patcher.TargetMatcher;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class MethodTarget
implements TargetMatcher {
    private final String classSpec;
    private final String methodName;
    private final String returnSpec;
    private final List<String> parameterSpec;
    private boolean hasDescription;
    private static final String JVM_TYPE_SPEC = "\\[*(?:[BCDFIJSZ]|L[^;]+;)";
    private static final Pattern PARAM_SPEC = Pattern.compile("\\[*(?:[BCDFIJSZ]|L[^;]+;)");
    private static final Pattern COMPLETE_SPEC = Pattern.compile("^\\(((?:\\[*(?:[BCDFIJSZ]|L[^;]+;))*)\\)(V|\\[*(?:[BCDFIJSZ]|L[^;]+;))$");
    private static final Pattern BRACE_PAIRS = Pattern.compile("^(?:\\[\\])*$");

    @Override
    public String describe() {
        int sci;
        int sci1 = this.classSpec.lastIndexOf(46);
        int sci2 = this.classSpec.lastIndexOf(36);
        int sci3 = this.classSpec.lastIndexOf(47);
        int n = sci = sci1 > sci2 ? sci1 : sci2;
        if (sci < sci3) {
            sci = sci3;
        }
        return String.valueOf(sci == -1 ? this.classSpec : this.classSpec.substring(sci + 1)) + ":" + this.methodName;
    }

    public String getClassSpec() {
        return this.classSpec;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public String getReturnSpec() {
        return this.returnSpec;
    }

    public List<String> getParameterSpec() {
        return this.parameterSpec;
    }

    public boolean isHasDescription() {
        return this.hasDescription;
    }

    public MethodTarget(String classSpec, String methodName) {
        this(classSpec, methodName, false, null, null);
    }

    public MethodTarget(String classSpec, String methodName, String returnSpec, String ... parameterSpecs) {
        this(classSpec, methodName, true, returnSpec, parameterSpecs);
    }

    public Boolean returnTypeIsVoid() {
        if (this.hasDescription) {
            return this.returnSpec.equals("void");
        }
        return null;
    }

    private MethodTarget(String classSpec, String methodName, boolean hasDescription, String returnSpec, String[] parameterSpecs) {
        if (classSpec == null) {
            throw new NullPointerException("classSpec");
        }
        if (methodName == null) {
            throw new NullPointerException("methodName");
        }
        if (hasDescription && returnSpec == null) {
            throw new NullPointerException("returnSpec");
        }
        if (hasDescription && parameterSpecs == null) {
            throw new NullPointerException("parameterSpecs");
        }
        if (methodName.contains("[") || methodName.contains(".")) {
            throw new IllegalArgumentException("Your method name contained dots or braces. Perhaps you switched return type and method name around?");
        }
        this.hasDescription = hasDescription;
        this.classSpec = classSpec;
        this.methodName = methodName;
        this.returnSpec = returnSpec;
        this.parameterSpec = parameterSpecs == null ? null : Collections.unmodifiableList(Arrays.asList(parameterSpecs));
    }

    public static List<String> decomposeFullDesc(String desc) {
        Matcher descMatcher = COMPLETE_SPEC.matcher(desc);
        if (!descMatcher.matches()) {
            throw new IllegalArgumentException("This isn't a valid spec: " + desc);
        }
        ArrayList<String> out = new ArrayList<String>();
        out.add(descMatcher.group(2));
        Matcher paramMatcher = PARAM_SPEC.matcher(descMatcher.group(1));
        while (paramMatcher.find()) {
            out.add(paramMatcher.group(0));
        }
        return out;
    }

    public boolean classMatches(String classSpec) {
        return MethodTarget.typeMatches(classSpec, this.classSpec);
    }

    @Override
    public Collection<String> getAffectedClasses() {
        return Collections.singleton(this.classSpec);
    }

    @Override
    public boolean matches(String classSpec, String methodName, String descriptor) {
        if (!methodName.equals(this.methodName)) {
            return false;
        }
        if (!this.classMatches(classSpec)) {
            return false;
        }
        return this.descriptorMatch(descriptor);
    }

    private boolean descriptorMatch(String descriptor) {
        if (this.returnSpec == null) {
            return true;
        }
        Iterator<String> targetSpecs = MethodTarget.decomposeFullDesc(descriptor).iterator();
        if (!MethodTarget.typeSpecMatch(targetSpecs.next(), this.returnSpec)) {
            return false;
        }
        Iterator<String> patternSpecs = this.parameterSpec.iterator();
        while (targetSpecs.hasNext() && patternSpecs.hasNext()) {
            if (MethodTarget.typeSpecMatch(targetSpecs.next(), patternSpecs.next())) continue;
            return false;
        }
        return !targetSpecs.hasNext() && !patternSpecs.hasNext();
    }

    public static boolean typeSpecMatch(String type, String pattern) {
        if (type.equals("V")) {
            return pattern.equals("void");
        }
        int dimsInType = 0;
        while (dimsInType < type.length()) {
            if (type.charAt(dimsInType) != '[') break;
            ++dimsInType;
        }
        type = type.substring(dimsInType);
        int start = pattern.length() - (dimsInType *= 2);
        if (start < 0) {
            return false;
        }
        String braces = pattern.substring(start);
        if (!BRACE_PAIRS.matcher(braces).matches()) {
            return false;
        }
        pattern = pattern.substring(0, start);
        switch (type.charAt(0)) {
            case 'B': {
                return pattern.equals("byte");
            }
            case 'C': {
                return pattern.equals("char");
            }
            case 'D': {
                return pattern.equals("double");
            }
            case 'F': {
                return pattern.equals("float");
            }
            case 'I': {
                return pattern.equals("int");
            }
            case 'J': {
                return pattern.equals("long");
            }
            case 'S': {
                return pattern.equals("short");
            }
            case 'Z': {
                return pattern.equals("boolean");
            }
            case 'L': {
                return MethodTarget.typeMatches(type.substring(1, type.length() - 1), pattern);
            }
        }
        return false;
    }

    public static boolean typeMatches(String type, String pattern) {
        return type.replace("/", ".").equals(pattern);
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + (this.classSpec == null ? 0 : this.classSpec.hashCode());
        result = 31 * result + (this.hasDescription ? 1231 : 1237);
        result = 31 * result + (this.methodName == null ? 0 : this.methodName.hashCode());
        result = 31 * result + (this.parameterSpec == null ? 0 : this.parameterSpec.hashCode());
        result = 31 * result + (this.returnSpec == null ? 0 : this.returnSpec.hashCode());
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
        MethodTarget other = (MethodTarget)obj;
        if (this.classSpec == null ? other.classSpec != null : !this.classSpec.equals(other.classSpec)) {
            return false;
        }
        if (this.hasDescription != other.hasDescription) {
            return false;
        }
        if (this.methodName == null ? other.methodName != null : !this.methodName.equals(other.methodName)) {
            return false;
        }
        if (this.parameterSpec == null ? other.parameterSpec != null : !this.parameterSpec.equals(other.parameterSpec)) {
            return false;
        }
        return !(this.returnSpec == null ? other.returnSpec != null : !this.returnSpec.equals(other.returnSpec));
    }

    public String toString() {
        return "MethodTarget[classSpec=" + this.classSpec + ", methodName=" + this.methodName + ", returnSpec=" + this.returnSpec + ", parameterSpec=" + this.parameterSpec + ", hasDescription=" + this.hasDescription + "]";
    }
}
