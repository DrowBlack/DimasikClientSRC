package lombok.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.core.AST;
import lombok.core.ClassLiteral;
import lombok.core.FieldSelect;
import lombok.core.LombokNode;
import lombok.permit.Permit;

public class AnnotationValues<A extends Annotation> {
    private final Class<A> type;
    private final Map<String, AnnotationValue> values;
    private final LombokNode<?, ?, ?> ast;
    private A cachedInstance = null;

    public AnnotationValues(Class<A> type, Map<String, AnnotationValue> values, LombokNode<?, ?, ?> ast) {
        this.type = type;
        this.values = values;
        this.ast = ast;
    }

    public static <A extends Annotation> AnnotationValues<A> of(Class<A> type) {
        return new AnnotationValues<A>(type, Collections.<String, AnnotationValue>emptyMap(), null);
    }

    public static <A extends Annotation> AnnotationValues<A> of(Class<A> type, LombokNode<?, ?, ?> ast) {
        return new AnnotationValues<A>(type, Collections.<String, AnnotationValue>emptyMap(), ast);
    }

    private static AnnotationValueDecodeFail makeNoDefaultFail(AnnotationValue owner, Method method) {
        return new AnnotationValueDecodeFail(owner, "No value supplied but " + method.getName() + " has no default either.", -1);
    }

    public List<String> getAsStringList(String methodName) {
        AnnotationValue v = this.values.get(methodName);
        if (v == null) {
            String[] s = this.getDefaultIf(methodName, new String[0]);
            return Collections.unmodifiableList(Arrays.asList(s));
        }
        ArrayList<String> out = new ArrayList<String>(v.valueGuesses.size());
        int idx = 0;
        for (Object guess : v.valueGuesses) {
            Object result;
            Object object = result = guess == null ? null : this.guessToType(guess, String.class, v, idx);
            if (result == null) {
                if (v.valueGuesses.size() == 1) {
                    String[] s = this.getDefaultIf(methodName, new String[0]);
                    return Collections.unmodifiableList(Arrays.asList(s));
                }
                throw new AnnotationValueDecodeFail(v, "I can't make sense of this annotation value. Try using a fully qualified literal.", idx);
            }
            out.add((String)result);
        }
        return Collections.unmodifiableList(out);
    }

    public String getAsString(String methodName) {
        AnnotationValue v = this.values.get(methodName);
        if (v == null || v.valueGuesses.size() != 1) {
            return this.getDefaultIf(methodName, "");
        }
        Object guess = this.guessToType(v.valueGuesses.get(0), String.class, v, 0);
        if (guess instanceof String) {
            return (String)guess;
        }
        return this.getDefaultIf(methodName, "");
    }

    public boolean getAsBoolean(String methodName) {
        AnnotationValue v = this.values.get(methodName);
        if (v == null || v.valueGuesses.size() != 1) {
            return this.getDefaultIf(methodName, false);
        }
        Object guess = this.guessToType(v.valueGuesses.get(0), Boolean.TYPE, v, 0);
        if (guess instanceof Boolean) {
            return (Boolean)guess;
        }
        return this.getDefaultIf(methodName, false);
    }

    public <T> T getDefaultIf(String methodName, T defaultValue) {
        try {
            return (T)Permit.getMethod(this.type, methodName, new Class[0]).getDefaultValue();
        }
        catch (Exception exception) {
            return defaultValue;
        }
    }

    public A getInstance() {
        if (this.cachedInstance != null) {
            return this.cachedInstance;
        }
        InvocationHandler invocations = new InvocationHandler(){

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                AnnotationValue v = (AnnotationValue)AnnotationValues.this.values.get(method.getName());
                if (v == null) {
                    Object defaultValue = method.getDefaultValue();
                    if (defaultValue != null) {
                        return defaultValue;
                    }
                    throw AnnotationValues.makeNoDefaultFail(v, method);
                }
                boolean isArray = false;
                Class<?> expected = method.getReturnType();
                Object array = null;
                if (expected.isArray()) {
                    isArray = true;
                    expected = expected.getComponentType();
                    array = Array.newInstance(expected, v.valueGuesses.size());
                }
                if (!isArray && v.valueGuesses.size() > 1) {
                    throw new AnnotationValueDecodeFail(v, "Expected a single value, but " + method.getName() + " has an array of values", -1);
                }
                if (v.valueGuesses.size() == 0 && !isArray) {
                    Object defaultValue = method.getDefaultValue();
                    if (defaultValue == null) {
                        throw AnnotationValues.makeNoDefaultFail(v, method);
                    }
                    return defaultValue;
                }
                int idx = 0;
                for (Object guess : v.valueGuesses) {
                    Object result;
                    Object object = result = guess == null ? null : AnnotationValues.this.guessToType(guess, expected, v, idx);
                    if (!isArray) {
                        if (result == null) {
                            Object defaultValue = method.getDefaultValue();
                            if (defaultValue == null) {
                                throw AnnotationValues.makeNoDefaultFail(v, method);
                            }
                            return defaultValue;
                        }
                        return result;
                    }
                    if (result == null) {
                        if (v.valueGuesses.size() == 1) {
                            Object defaultValue = method.getDefaultValue();
                            if (defaultValue == null) {
                                throw AnnotationValues.makeNoDefaultFail(v, method);
                            }
                            return defaultValue;
                        }
                        throw new AnnotationValueDecodeFail(v, "I can't make sense of this annotation value. Try using a fully qualified literal.", idx);
                    }
                    Array.set(array, idx++, result);
                }
                return array;
            }
        };
        this.cachedInstance = (Annotation)Proxy.newProxyInstance(this.type.getClassLoader(), new Class[]{this.type}, invocations);
        return this.cachedInstance;
    }

    private Object guessToType(Object guess, Class<?> expected, AnnotationValue v, int pos) {
        int intVal;
        if ((expected == Integer.TYPE || expected == Integer.class) && (guess instanceof Integer || guess instanceof Short || guess instanceof Byte)) {
            return ((Number)guess).intValue();
        }
        if ((expected == Long.TYPE || expected == Long.class) && (guess instanceof Long || guess instanceof Integer || guess instanceof Short || guess instanceof Byte)) {
            return ((Number)guess).longValue();
        }
        if ((expected == Short.TYPE || expected == Short.class) && (guess instanceof Integer || guess instanceof Short || guess instanceof Byte)) {
            intVal = ((Number)guess).intValue();
            short shortVal = ((Number)guess).shortValue();
            if (shortVal == intVal) {
                return (int)shortVal;
            }
        }
        if ((expected == Byte.TYPE || expected == Byte.class) && (guess instanceof Integer || guess instanceof Short || guess instanceof Byte)) {
            intVal = ((Number)guess).intValue();
            byte byteVal = ((Number)guess).byteValue();
            if (byteVal == intVal) {
                return (int)byteVal;
            }
        }
        if ((expected == Double.TYPE || expected == Double.class) && guess instanceof Number) {
            return ((Number)guess).doubleValue();
        }
        if ((expected == Float.TYPE || expected == Float.class) && guess instanceof Number) {
            return Float.valueOf(((Number)guess).floatValue());
        }
        if ((expected == Boolean.TYPE || expected == Boolean.class) && guess instanceof Boolean) {
            return (boolean)((Boolean)guess);
        }
        if ((expected == Character.TYPE || expected == Character.class) && guess instanceof Character) {
            return Character.valueOf(((Character)guess).charValue());
        }
        if (expected == String.class && guess instanceof String) {
            return guess;
        }
        if (Enum.class.isAssignableFrom(expected) && guess instanceof FieldSelect) {
            String fieldSel = ((FieldSelect)guess).getFinalPart();
            ?[] objArray = expected.getEnumConstants();
            int n = objArray.length;
            int n2 = 0;
            while (n2 < n) {
                Object enumConstant = objArray[n2];
                String target = ((Enum)enumConstant).name();
                if (target.equals(fieldSel)) {
                    return enumConstant;
                }
                ++n2;
            }
            throw new AnnotationValueDecodeFail(v, "Can't translate " + fieldSel + " to an enum of type " + expected, pos);
        }
        if (expected == Class.class && guess instanceof ClassLiteral) {
            try {
                String classLit = ((ClassLiteral)guess).getClassName();
                return Class.forName(this.toFQ(classLit));
            }
            catch (ClassNotFoundException classNotFoundException) {
                throw new AnnotationValueDecodeFail(v, "Can't translate " + guess + " to a class object.", pos);
            }
        }
        if (guess instanceof AnnotationValues) {
            return ((AnnotationValues)guess).getInstance();
        }
        if (guess instanceof FieldSelect) {
            throw new AnnotationValueDecodeFail(v, "You must use constant literals in lombok annotations; they cannot be references to (static) fields.", pos);
        }
        throw new AnnotationValueDecodeFail(v, "Can't translate a " + guess.getClass() + " to the expected " + expected, pos);
    }

    public List<String> getRawExpressions(String annotationMethodName) {
        AnnotationValue v = this.values.get(annotationMethodName);
        return v == null ? Collections.emptyList() : v.raws;
    }

    public List<Object> getActualExpressions(String annotationMethodName) {
        AnnotationValue v = this.values.get(annotationMethodName);
        return v == null ? Collections.emptyList() : v.expressions;
    }

    public boolean isExplicit(String annotationMethodName) {
        AnnotationValue annotationValue = this.values.get(annotationMethodName);
        return annotationValue != null && annotationValue.isExplicit();
    }

    public String getRawExpression(String annotationMethodName) {
        List<String> l = this.getRawExpressions(annotationMethodName);
        return l.isEmpty() ? null : l.get(0);
    }

    public Object getActualExpression(String annotationMethodName) {
        List<Object> l = this.getActualExpressions(annotationMethodName);
        return l.isEmpty() ? null : l.get(0);
    }

    public Object getValueGuess(String annotationMethodName) {
        AnnotationValue v = this.values.get(annotationMethodName);
        return v == null || v.valueGuesses.isEmpty() ? null : v.valueGuesses.get(0);
    }

    public void setError(String annotationMethodName, String message) {
        this.setError(annotationMethodName, message, -1);
    }

    public void setWarning(String annotationMethodName, String message) {
        this.setWarning(annotationMethodName, message, -1);
    }

    public void setError(String annotationMethodName, String message, int index) {
        AnnotationValue v = this.values.get(annotationMethodName);
        if (v == null) {
            return;
        }
        v.setError(message, index);
    }

    public void setWarning(String annotationMethodName, String message, int index) {
        AnnotationValue v = this.values.get(annotationMethodName);
        if (v == null) {
            return;
        }
        v.setWarning(message, index);
    }

    public List<String> getProbableFQTypes(String annotationMethodName) {
        ArrayList<String> result = new ArrayList<String>();
        AnnotationValue v = this.values.get(annotationMethodName);
        if (v == null) {
            return Collections.emptyList();
        }
        for (Object o : v.valueGuesses) {
            result.add(o == null ? null : this.toFQ(o.toString()));
        }
        return result;
    }

    public String getProbableFQType(String annotationMethodName) {
        List<String> l = this.getProbableFQTypes(annotationMethodName);
        return l.isEmpty() ? null : l.get(0);
    }

    private String toFQ(String typeName) {
        String fqn;
        String prefix = typeName.indexOf(46) > -1 ? typeName.substring(0, typeName.indexOf(46)) : typeName;
        Object n = this.ast;
        while (n != null) {
            String simpleName;
            if (((LombokNode)n).getKind() == AST.Kind.TYPE && prefix.equals(simpleName = ((LombokNode)n).getName())) {
                ArrayList<String> outerNames = new ArrayList<String>();
                if ((n = ((LombokNode)n).up()) != null && ((LombokNode)n).getKind() != AST.Kind.COMPILATION_UNIT) {
                    if (((LombokNode)n).getKind() != AST.Kind.TYPE) break;
                    outerNames.add(((LombokNode)n).getName());
                    break;
                }
                StringBuilder result = new StringBuilder();
                if (this.ast.getPackageDeclaration() != null) {
                    result.append(this.ast.getPackageDeclaration());
                }
                if (result.length() > 0) {
                    result.append('.');
                }
                Collections.reverse(outerNames);
                for (String outerName : outerNames) {
                    result.append(outerName).append('.');
                }
                result.append(typeName);
                return result.toString();
            }
            n = ((LombokNode)n).up();
        }
        if (prefix.equals(typeName) && (fqn = this.ast.getImportList().getFullyQualifiedNameForSimpleName(typeName)) != null) {
            return fqn;
        }
        for (String potential : this.ast.getImportList().applyNameToStarImports("java", typeName)) {
            try {
                Class<?> c = Class.forName(potential);
                if (c == null) continue;
                return c.getName();
            }
            catch (Throwable throwable) {}
        }
        if (typeName.indexOf(46) == -1) {
            return AnnotationValues.inLocalPackage(this.ast, typeName);
        }
        char firstChar = typeName.charAt(0);
        if (Character.isTitleCase(firstChar) || Character.isUpperCase(firstChar)) {
            return AnnotationValues.inLocalPackage(this.ast, typeName);
        }
        return typeName;
    }

    private static String inLocalPackage(LombokNode<?, ?, ?> node, String typeName) {
        StringBuilder result = new StringBuilder();
        if (node != null && node.getPackageDeclaration() != null) {
            result.append(node.getPackageDeclaration());
        }
        if (result.length() > 0) {
            result.append('.');
        }
        result.append(typeName);
        return result.toString();
    }

    public AnnotationValues<A> integrate(AnnotationValues<A> defaults) {
        if (this.values.isEmpty()) {
            return defaults;
        }
        for (Map.Entry<String, AnnotationValue> entry : defaults.values.entrySet()) {
            AnnotationValue existingValue;
            if (!entry.getValue().isExplicit || (existingValue = this.values.get(entry.getKey())) != null && existingValue.isExplicit) continue;
            this.values.put(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public boolean isMarking() {
        for (AnnotationValue v : this.values.values()) {
            if (!v.isExplicit) continue;
            return false;
        }
        return true;
    }

    public static class AnnotationValue {
        public final List<String> raws;
        public final List<Object> valueGuesses;
        public final List<Object> expressions;
        private final LombokNode<?, ?, ?> node;
        private final boolean isExplicit;

        public AnnotationValue(LombokNode<?, ?, ?> node, List<String> raws, List<Object> expressions, List<Object> valueGuesses, boolean isExplicit) {
            this.node = node;
            this.raws = raws;
            this.expressions = expressions;
            this.valueGuesses = valueGuesses;
            this.isExplicit = isExplicit;
        }

        public void setError(String message, int valueIdx) {
            this.node.addError(message);
        }

        public void setWarning(String message, int valueIdx) {
            this.node.addError(message);
        }

        public String toString() {
            return "raws: " + this.raws + " valueGuesses: " + this.valueGuesses;
        }

        public boolean isExplicit() {
            return this.isExplicit;
        }
    }

    public static class AnnotationValueDecodeFail
    extends RuntimeException {
        private static final long serialVersionUID = 1L;
        public final int idx;
        public final AnnotationValue owner;

        public AnnotationValueDecodeFail(AnnotationValue owner, String msg, int idx) {
            super(msg);
            this.idx = idx;
            this.owner = owner;
        }
    }
}
