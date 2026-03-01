package lombok.core;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.Lombok;
import lombok.core.AnnotationValues;
import lombok.core.ImportList;
import lombok.core.LombokConfiguration;
import lombok.core.LombokImmutableList;
import lombok.core.LombokNode;
import lombok.core.TypeResolver;
import lombok.core.configuration.ConfigurationKey;
import lombok.core.debug.HistogramTracker;
import lombok.permit.Permit;

public abstract class AST<A extends AST<A, L, N>, L extends LombokNode<A, L, N>, N> {
    private L top;
    private final String fileName;
    private final String packageDeclaration;
    private final ImportList imports;
    private TypeResolver importsAsResolver;
    Map<N, N> identityDetector = new IdentityHashMap<N, N>();
    private Map<N, L> nodeMap = new IdentityHashMap<N, L>();
    private boolean changed = false;
    private final Collection<Class<? extends N>> statementTypes;
    private static final HistogramTracker configTracker = System.getProperty("lombok.timeConfig") == null ? null : new HistogramTracker("lombok.config");
    private static final ConcurrentMap<Class<?>, FieldAccess[]> fieldsOfASTClasses = new ConcurrentHashMap();

    protected AST(String fileName, String packageDeclaration, ImportList imports, Collection<Class<? extends N>> statementTypes) {
        this.fileName = fileName == null ? "(unknown).java" : fileName;
        this.packageDeclaration = packageDeclaration;
        this.imports = imports;
        this.statementTypes = statementTypes;
    }

    public abstract URI getAbsoluteFileLocation();

    public void setChanged() {
        this.changed = true;
    }

    protected void clearChanged() {
        this.changed = false;
    }

    public boolean isChanged() {
        return this.changed;
    }

    protected void setTop(L top) {
        this.top = top;
    }

    public final String getPackageDeclaration() {
        return this.packageDeclaration;
    }

    public final ImportList getImportList() {
        return this.imports;
    }

    public final TypeResolver getImportListAsTypeResolver() {
        if (this.importsAsResolver != null) {
            return this.importsAsResolver;
        }
        this.importsAsResolver = new TypeResolver(this.getImportList());
        return this.importsAsResolver;
    }

    protected L putInMap(L node) {
        this.nodeMap.put(((LombokNode)node).get(), node);
        this.identityDetector.put(((LombokNode)node).get(), ((LombokNode)node).get());
        return node;
    }

    protected Map<N, L> getNodeMap() {
        return this.nodeMap;
    }

    protected void clearState() {
        this.identityDetector = new IdentityHashMap<N, N>();
        this.nodeMap = new IdentityHashMap<N, L>();
    }

    protected boolean setAndGetAsHandled(N node) {
        return this.identityDetector.put(node, node) != null;
    }

    public String getFileName() {
        return this.fileName;
    }

    public L top() {
        return this.top;
    }

    public L get(N node) {
        return (L)((LombokNode)this.nodeMap.get(node));
    }

    public int getSourceVersion() {
        return 6;
    }

    public int getLatestJavaSpecSupported() {
        return 6;
    }

    L replaceNewWithExistingOld(Map<N, L> oldNodes, L newNode) {
        LombokNode oldNode = (LombokNode)oldNodes.get(((LombokNode)newNode).get());
        Object targetNode = oldNode == null ? newNode : oldNode;
        ArrayList<LombokNode> children = new ArrayList<LombokNode>();
        for (LombokNode child : ((LombokNode)newNode).children) {
            LombokNode oldChild = this.replaceNewWithExistingOld(oldNodes, child);
            children.add(oldChild);
            oldChild.parent = targetNode;
        }
        ((LombokNode)targetNode).children = LombokImmutableList.copyOf(children);
        return targetNode;
    }

    protected abstract L buildTree(N var1, Kind var2);

    protected FieldAccess[] fieldsOf(Class<?> c) {
        FieldAccess[] fields = (FieldAccess[])fieldsOfASTClasses.get(c);
        if (fields != null) {
            return fields;
        }
        ArrayList<FieldAccess> fieldList = new ArrayList<FieldAccess>();
        this.getFields(c, fieldList);
        fieldsOfASTClasses.putIfAbsent(c, fieldList.toArray(new FieldAccess[0]));
        return (FieldAccess[])fieldsOfASTClasses.get(c);
    }

    /*
     * Unable to fully structure code
     */
    private void getFields(Class<?> c, Collection<FieldAccess> fields) {
        if (c == Object.class || c == null) {
            return;
        }
        var6_3 = c.getDeclaredFields();
        var5_4 = var6_3.length;
        var4_5 = 0;
        while (var4_5 < var5_4) {
            block5: {
                block6: {
                    f = var6_3[var4_5];
                    if (Modifier.isStatic(f.getModifiers())) break block5;
                    t = f.getType();
                    dim = 0;
                    if (!t.isArray()) ** GOTO lbl19
                    while (t.isArray()) {
                        ++dim;
                        t = t.getComponentType();
                    }
                    break block6;
lbl-1000:
                    // 1 sources

                    {
                        ++dim;
                        t = this.getComponentType(f.getGenericType());
lbl19:
                        // 2 sources

                        ** while (Collection.class.isAssignableFrom(t))
                    }
                }
                if (this.shouldDrill(c, t, f.getName())) {
                    Permit.setAccessible(f);
                    fields.add(new FieldAccess(f, dim));
                }
            }
            ++var4_5;
        }
        this.getFields(c.getSuperclass(), fields);
    }

    private Class<?> getComponentType(Type type) {
        if (type instanceof ParameterizedType) {
            Type component = ((ParameterizedType)type).getActualTypeArguments()[0];
            return component instanceof Class ? (Class)component : Object.class;
        }
        return Object.class;
    }

    private boolean shouldDrill(Class<?> parentType, Class<?> childType, String fieldName) {
        for (Class<N> statementType : this.statementTypes) {
            if (!statementType.isAssignableFrom(childType)) continue;
            return true;
        }
        return false;
    }

    protected Collection<L> buildWithField(Class<L> nodeType, N statement, FieldAccess fa) {
        ArrayList list = new ArrayList();
        this.buildWithField0(nodeType, statement, fa, list);
        return list;
    }

    protected boolean replaceStatementInNode(N statement, N oldN, N newN) {
        FieldAccess[] fieldAccessArray = this.fieldsOf(statement.getClass());
        int n = fieldAccessArray.length;
        int n2 = 0;
        while (n2 < n) {
            FieldAccess fa = fieldAccessArray[n2];
            if (this.replaceStatementInField(fa, statement, oldN, newN)) {
                return true;
            }
            ++n2;
        }
        return false;
    }

    private boolean replaceStatementInField(FieldAccess fa, N statement, N oldN, N newN) {
        Object o;
        block8: {
            block7: {
                try {
                    o = fa.field.get(statement);
                    if (o != null) break block7;
                    return false;
                }
                catch (IllegalAccessException e) {
                    throw Lombok.sneakyThrow(e);
                }
            }
            if (o != oldN) break block8;
            fa.field.set(statement, newN);
            return true;
        }
        if (fa.dim > 0) {
            if (o.getClass().isArray()) {
                return this.replaceStatementInArray(o, oldN, newN);
            }
            if (Collection.class.isInstance(o)) {
                return this.replaceStatementInCollection(fa.field, statement, new ArrayList(), (Collection)o, oldN, newN);
            }
        }
        return false;
    }

    private boolean replaceStatementInCollection(Field field, Object fieldRef, List<Collection<?>> chain, Collection<?> collection, N oldN, N newN) throws IllegalAccessException {
        if (collection == null) {
            return false;
        }
        int idx = -1;
        for (Object o : collection) {
            ++idx;
            if (o == null) continue;
            if (Collection.class.isInstance(o)) {
                Collection newC = (Collection)o;
                ArrayList newChain = new ArrayList(chain);
                newChain.add(newC);
                if (this.replaceStatementInCollection(field, fieldRef, newChain, newC, oldN, newN)) {
                    return true;
                }
            }
            if (o != oldN) continue;
            this.setElementInASTCollection(field, fieldRef, chain, collection, idx, newN);
            return true;
        }
        return false;
    }

    protected void setElementInASTCollection(Field field, Object fieldRef, List<Collection<?>> chain, Collection<?> collection, int idx, N newN) throws IllegalAccessException {
        if (collection instanceof List) {
            ((List)collection).set(idx, newN);
        }
    }

    private boolean replaceStatementInArray(Object array, N oldN, N newN) {
        if (array == null) {
            return false;
        }
        int len = Array.getLength(array);
        int i = 0;
        while (i < len) {
            Object o = Array.get(array, i);
            if (o != null) {
                if (o.getClass().isArray()) {
                    if (this.replaceStatementInArray(o, oldN, newN)) {
                        return true;
                    }
                } else if (o == oldN) {
                    Array.set(array, i, newN);
                    return true;
                }
            }
            ++i;
        }
        return false;
    }

    private void buildWithField0(Class<L> nodeType, N child, FieldAccess fa, Collection<L> list) {
        try {
            Object o = fa.field.get(child);
            if (o == null) {
                return;
            }
            if (fa.dim == 0) {
                L node = this.buildTree(o, Kind.STATEMENT);
                if (node != null) {
                    list.add((LombokNode)nodeType.cast(node));
                }
            } else if (o.getClass().isArray()) {
                this.buildWithArray(nodeType, o, list, fa.dim);
            } else if (Collection.class.isInstance(o)) {
                this.buildWithCollection(nodeType, o, list, fa.dim);
            }
        }
        catch (IllegalAccessException e) {
            throw Lombok.sneakyThrow(e);
        }
    }

    private void buildWithArray(Class<L> nodeType, Object array, Collection<L> list, int dim) {
        if (dim == 1) {
            Object[] objectArray = (Object[])array;
            int n = objectArray.length;
            int n2 = 0;
            while (n2 < n) {
                L node;
                Object v = objectArray[n2];
                if (v != null && (node = this.buildTree(v, Kind.STATEMENT)) != null) {
                    list.add((LombokNode)nodeType.cast(node));
                }
                ++n2;
            }
        } else {
            Object[] objectArray = (Object[])array;
            int n = objectArray.length;
            int n3 = 0;
            while (n3 < n) {
                Object v = objectArray[n3];
                if (v == null) {
                    return;
                }
                this.buildWithArray(nodeType, v, list, dim - 1);
                ++n3;
            }
        }
    }

    private void buildWithCollection(Class<L> nodeType, Object collection, Collection<L> list, int dim) {
        if (dim == 1) {
            for (Object v : (Collection)collection) {
                L node;
                if (v == null || (node = this.buildTree(v, Kind.STATEMENT)) == null) continue;
                list.add((LombokNode)nodeType.cast(node));
            }
        } else {
            for (Object v : (Collection)collection) {
                this.buildWithCollection(nodeType, v, list, dim - 1);
            }
        }
    }

    public final <T> T readConfiguration(ConfigurationKey<T> key) {
        long start = configTracker == null ? 0L : configTracker.start();
        try {
            T t = LombokConfiguration.read(key, this);
            return t;
        }
        finally {
            if (configTracker != null) {
                configTracker.end(start);
            }
        }
    }

    public final <T> T readConfigurationOr(ConfigurationKey<T> key, T defaultValue) {
        long start = configTracker == null ? 0L : configTracker.start();
        try {
            T value = LombokConfiguration.read(key, this);
            T t = value != null ? value : defaultValue;
            return t;
        }
        finally {
            if (configTracker != null) {
                configTracker.end(start);
            }
        }
    }

    public boolean getBooleanAnnotationValue(AnnotationValues<?> annotation, String annoMethod, ConfigurationKey<Boolean> confKey) {
        Boolean conf = this.readConfiguration(confKey);
        return annotation.isExplicit(annoMethod) || conf == null ? annotation.getAsBoolean(annoMethod) : conf.booleanValue();
    }

    protected static class FieldAccess {
        public final Field field;
        public final int dim;

        FieldAccess(Field field, int dim) {
            this.field = field;
            this.dim = dim;
        }
    }

    public static enum Kind {
        COMPILATION_UNIT,
        TYPE,
        FIELD,
        INITIALIZER,
        METHOD,
        ANNOTATION,
        ARGUMENT,
        LOCAL,
        STATEMENT,
        TYPE_USE;

    }
}
