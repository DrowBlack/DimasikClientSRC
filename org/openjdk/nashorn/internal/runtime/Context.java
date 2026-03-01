package org.openjdk.nashorn.internal.runtime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.SwitchPoint;
import java.lang.module.Configuration;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReader;
import java.lang.module.ModuleReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.script.ScriptEngine;
import jdk.dynalink.DynamicLinker;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.openjdk.nashorn.api.scripting.ClassFilter;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;
import org.openjdk.nashorn.internal.WeakValueCache;
import org.openjdk.nashorn.internal.codegen.Compiler;
import org.openjdk.nashorn.internal.codegen.CompilerConstants;
import org.openjdk.nashorn.internal.ir.FunctionNode;
import org.openjdk.nashorn.internal.ir.debug.ASTWriter;
import org.openjdk.nashorn.internal.ir.debug.PrintVisitor;
import org.openjdk.nashorn.internal.lookup.MethodHandleFactory;
import org.openjdk.nashorn.internal.objects.Global;
import org.openjdk.nashorn.internal.parser.Parser;
import org.openjdk.nashorn.internal.runtime.CodeInstaller;
import org.openjdk.nashorn.internal.runtime.CodeStore;
import org.openjdk.nashorn.internal.runtime.ConsString;
import org.openjdk.nashorn.internal.runtime.DebuggerSupport;
import org.openjdk.nashorn.internal.runtime.ECMAErrors;
import org.openjdk.nashorn.internal.runtime.ErrorManager;
import org.openjdk.nashorn.internal.runtime.FunctionInitializer;
import org.openjdk.nashorn.internal.runtime.GlobalConstants;
import org.openjdk.nashorn.internal.runtime.JSType;
import org.openjdk.nashorn.internal.runtime.NashornLoader;
import org.openjdk.nashorn.internal.runtime.ParserException;
import org.openjdk.nashorn.internal.runtime.PropertyMap;
import org.openjdk.nashorn.internal.runtime.Scope;
import org.openjdk.nashorn.internal.runtime.ScriptEnvironment;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;
import org.openjdk.nashorn.internal.runtime.ScriptLoader;
import org.openjdk.nashorn.internal.runtime.ScriptObject;
import org.openjdk.nashorn.internal.runtime.ScriptRuntime;
import org.openjdk.nashorn.internal.runtime.Source;
import org.openjdk.nashorn.internal.runtime.StoredScript;
import org.openjdk.nashorn.internal.runtime.StructureLoader;
import org.openjdk.nashorn.internal.runtime.Version;
import org.openjdk.nashorn.internal.runtime.events.RuntimeEvent;
import org.openjdk.nashorn.internal.runtime.linker.Bootstrap;
import org.openjdk.nashorn.internal.runtime.logging.DebugLogger;
import org.openjdk.nashorn.internal.runtime.logging.Loggable;
import org.openjdk.nashorn.internal.runtime.logging.Logger;
import org.openjdk.nashorn.internal.runtime.options.LoggingOption;
import org.openjdk.nashorn.internal.runtime.options.Options;
import sun.misc.Unsafe;

public final class Context {
    public static final String NASHORN_SET_CONFIG = "nashorn.setConfig";
    public static final String NASHORN_CREATE_CONTEXT = "nashorn.createContext";
    public static final String NASHORN_CREATE_GLOBAL = "nashorn.createGlobal";
    public static final String NASHORN_GET_CONTEXT = "nashorn.getContext";
    public static final String NASHORN_JAVA_REFLECTION = "nashorn.JavaReflection";
    public static final String NASHORN_DEBUG_MODE = "nashorn.debugMode";
    private static final String LOAD_CLASSPATH = "classpath:";
    private static final String LOAD_FX = "fx:";
    private static final String LOAD_NASHORN = "nashorn:";
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final MethodType CREATE_PROGRAM_FUNCTION_TYPE = MethodType.methodType(ScriptFunction.class, ScriptObject.class);
    private static final LongAdder NAMED_INSTALLED_SCRIPT_COUNT = new LongAdder();
    private static final LongAdder ANONYMOUS_INSTALLED_SCRIPT_COUNT = new LongAdder();
    private final FieldMode fieldMode;
    private final Map<String, SwitchPoint> builtinSwitchPoints = new HashMap<String, SwitchPoint>();
    private final WeakValueCache<CodeSource, Class<?>> anonymousHostClasses = new WeakValueCache();
    public static final boolean DEBUG;
    private static final ThreadLocal<Global> currentGlobal;
    private ClassCache classCache;
    private CodeStore codeStore;
    private final AtomicReference<GlobalConstants> globalConstantsRef = new AtomicReference();
    static final boolean javaSqlFound;
    static final boolean javaSqlRowsetFound;
    private final ScriptEnvironment env;
    final boolean _strict;
    private final ClassLoader appLoader;
    private final ScriptLoader scriptLoader;
    private final DynamicLinker dynamicLinker;
    private final ErrorManager errors;
    private final AtomicLong uniqueScriptId;
    private final ClassFilter classFilter;
    private static final StructureLoader theStructLoader;
    private static final ConcurrentMap<String, Class<?>> structureClasses;
    private static final AccessControlContext NO_PERMISSIONS_ACC_CTXT;
    private static final AccessControlContext CREATE_LOADER_ACC_CTXT;
    private static final AccessControlContext CREATE_GLOBAL_ACC_CTXT;
    private static final AccessControlContext GET_LOADER_ACC_CTXT;
    private final Map<String, DebugLogger> loggers = new HashMap<String, DebugLogger>();

    static long getNamedInstalledScriptCount() {
        return NAMED_INSTALLED_SCRIPT_COUNT.sum();
    }

    static long getAnonymousInstalledScriptCount() {
        return ANONYMOUS_INSTALLED_SCRIPT_COUNT.sum();
    }

    public static Global getGlobal() {
        return currentGlobal.get();
    }

    public static void setGlobal(ScriptObject global) {
        if (global != null && !(global instanceof Global)) {
            throw new IllegalArgumentException("not a global!");
        }
        Context.setGlobal((Global)global);
    }

    public static void setGlobal(Global global) {
        GlobalConstants globalConstants;
        assert (Context.getGlobal() != global);
        if (global != null && (globalConstants = Context.getContext(global).getGlobalConstants()) != null) {
            globalConstants.invalidateAll();
        }
        currentGlobal.set(global);
    }

    public static Context getContext() {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission(NASHORN_GET_CONTEXT));
        }
        return Context.getContextTrusted();
    }

    public static PrintWriter getCurrentErr() {
        Global global = Context.getGlobal();
        return global != null ? ((ScriptObject)global).getContext().getErr() : new PrintWriter(System.err);
    }

    public static void err(String str) {
        Context.err(str, true);
    }

    public static void err(String str, boolean crlf) {
        PrintWriter err = Context.getCurrentErr();
        if (err != null) {
            if (crlf) {
                err.println(str);
            } else {
                err.print(str);
            }
        }
    }

    ClassLoader getAppLoader() {
        return this.appLoader;
    }

    StructureLoader getStructLoader() {
        return theStructLoader;
    }

    private static AccessControlContext createNoPermAccCtxt() {
        return new AccessControlContext(new ProtectionDomain[]{new ProtectionDomain(null, new Permissions())});
    }

    private static AccessControlContext createPermAccCtxt(String permName) {
        Permissions perms = new Permissions();
        perms.add(new RuntimePermission(permName));
        return new AccessControlContext(new ProtectionDomain[]{new ProtectionDomain(null, perms)});
    }

    public Context(Options options, ErrorManager errors, ClassLoader appLoader) {
        this(options, errors, appLoader, null);
    }

    public Context(Options options, ErrorManager errors, ClassLoader appLoader, ClassFilter classFilter) {
        this(options, errors, new PrintWriter(System.out, true), new PrintWriter(System.err, true), appLoader, classFilter);
    }

    public Context(Options options, ErrorManager errors, PrintWriter out, PrintWriter err, ClassLoader appLoader) {
        this(options, errors, out, err, appLoader, null);
    }

    public Context(Options options, ErrorManager errors, PrintWriter out, PrintWriter err, ClassLoader appLoader, ClassFilter classFilter) {
        ClassLoader appCl;
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission(NASHORN_CREATE_CONTEXT));
        }
        this.classFilter = classFilter;
        this.env = new ScriptEnvironment(options, out, err);
        this._strict = this.env._strict;
        if (this.env._loader_per_compile) {
            this.scriptLoader = null;
            this.uniqueScriptId = null;
        } else {
            this.scriptLoader = this.createNewLoader();
            this.uniqueScriptId = new AtomicLong();
        }
        this.errors = errors;
        String modulePath = this.env._module_path;
        if (!this.env._compile_only && modulePath != null && !modulePath.isEmpty()) {
            if (sm != null) {
                sm.checkCreateClassLoader();
            }
            appCl = AccessController.doPrivileged(() -> Context.createModuleLoader(appLoader, modulePath, this.env._add_modules));
        } else {
            appCl = appLoader;
        }
        String classPath = this.env._classpath;
        if (!this.env._compile_only && classPath != null && !classPath.isEmpty()) {
            if (sm != null) {
                sm.checkCreateClassLoader();
            }
            appCl = NashornLoader.createClassLoader(classPath, appCl);
        }
        this.appLoader = appCl;
        this.dynamicLinker = Bootstrap.createDynamicLinker(this.appLoader, this.env._unstable_relink_threshold);
        int cacheSize = this.env._class_cache_size;
        if (cacheSize > 0) {
            this.classCache = new ClassCache(this, cacheSize);
        }
        if (this.env._persistent_cache) {
            this.codeStore = CodeStore.newCodeStore(this);
        }
        if (this.env._version) {
            this.getErr().println("nashorn " + Version.version());
        }
        if (this.env._fullversion) {
            this.getErr().println("nashorn full version " + Version.fullVersion());
        }
        this.fieldMode = Options.getBooleanProperty("nashorn.fields.dual") ? FieldMode.DUAL : (Options.getBooleanProperty("nashorn.fields.objects") ? FieldMode.OBJECTS : FieldMode.AUTO);
        this.initLoggers();
    }

    public ClassFilter getClassFilter() {
        return this.classFilter;
    }

    GlobalConstants getGlobalConstants() {
        return this.globalConstantsRef.get();
    }

    public ErrorManager getErrorManager() {
        return this.errors;
    }

    public ScriptEnvironment getEnv() {
        return this.env;
    }

    public PrintWriter getOut() {
        return this.env.getOut();
    }

    public PrintWriter getErr() {
        return this.env.getErr();
    }

    public boolean useDualFields() {
        return this.fieldMode == FieldMode.DUAL || this.fieldMode == FieldMode.AUTO && this.env._optimistic_types;
    }

    public static PropertyMap getGlobalMap() {
        return Context.getGlobal().getMap();
    }

    public ScriptFunction compileScript(Source source, ScriptObject scope) {
        return this.compileScript(source, scope, this.errors);
    }

    public MultiGlobalCompiledScript compileScript(Source source) {
        Class<?> clazz = this.compile(source, this.errors, this._strict);
        MethodHandle createProgramFunctionHandle = Context.getCreateProgramFunctionHandle(clazz);
        return newGlobal -> Context.invokeCreateProgramFunctionHandle(createProgramFunctionHandle, newGlobal);
    }

    public Object eval(ScriptObject initialScope, String string, Object callThis, Object location) {
        return this.eval(initialScope, string, callThis, location, false, false);
    }

    public Object eval(ScriptObject initialScope, String string, Object callThis, Object location, boolean strict, boolean evalCall) {
        Class<?> clazz;
        String file = location == ScriptRuntime.UNDEFINED || location == null ? "<eval>" : location.toString();
        Source source = Source.sourceFor(file, string, evalCall);
        boolean directEval = evalCall && location != ScriptRuntime.UNDEFINED;
        Global global = Context.getGlobal();
        ScriptObject scope = initialScope;
        boolean strictFlag = strict || this._strict;
        try {
            clazz = this.compile(source, new ThrowErrorManager(), strictFlag);
        }
        catch (ParserException e) {
            e.throwAsEcmaException(global);
            return null;
        }
        if (!strictFlag) {
            try {
                strictFlag = clazz.getField(CompilerConstants.STRICT_MODE.symbolName()).getBoolean(null);
            }
            catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
                strictFlag = false;
            }
        }
        if (strictFlag) {
            scope = Context.newScope(scope);
        }
        ScriptFunction func = Context.getProgramFunction(clazz, scope);
        Object evalThis = directEval ? (callThis != ScriptRuntime.UNDEFINED && callThis != null || strictFlag ? callThis : global) : callThis;
        return ScriptRuntime.apply(func, evalThis, new Object[0]);
    }

    private static ScriptObject newScope(ScriptObject callerScope) {
        return new Scope(callerScope, PropertyMap.newMap(Scope.class));
    }

    private static Source loadInternal(String srcStr, String prefix, String resourcePath) {
        if (srcStr.startsWith(prefix)) {
            String resource = resourcePath + srcStr.substring(prefix.length());
            return AccessController.doPrivileged(() -> {
                try {
                    InputStream resStream = Context.class.getResourceAsStream(resource);
                    return resStream != null ? Source.sourceFor(srcStr, Source.readFully(resStream)) : null;
                }
                catch (IOException exp) {
                    return null;
                }
            });
        }
        return null;
    }

    public Object load(Object scope, Object from) throws IOException {
        Map map;
        String name;
        String script;
        ScriptObject sobj;
        Object src = from instanceof ConsString ? from.toString() : from;
        Source source = null;
        if (src instanceof String) {
            String srcStr = (String)src;
            if (srcStr.startsWith(LOAD_CLASSPATH)) {
                URL url = this.getResourceURL(srcStr.substring(LOAD_CLASSPATH.length()));
                source = url != null ? Source.sourceFor(url.toString(), url) : null;
            } else {
                File file = new File(srcStr);
                if (srcStr.indexOf(58) != -1) {
                    source = Context.loadInternal(srcStr, LOAD_NASHORN, "resources/");
                    if (source == null && (source = Context.loadInternal(srcStr, LOAD_FX, "resources/fx/")) == null) {
                        URL url;
                        try {
                            url = new URL(srcStr);
                        }
                        catch (MalformedURLException e) {
                            url = file.toURI().toURL();
                        }
                        source = Source.sourceFor(url.toString(), url);
                    }
                } else if (file.isFile()) {
                    source = Source.sourceFor(srcStr, file);
                }
            }
        } else if (src instanceof File && ((File)src).isFile()) {
            File file = (File)src;
            source = Source.sourceFor(file.getName(), file);
        } else if (src instanceof URL) {
            URL url = (URL)src;
            source = Source.sourceFor(url.toString(), url);
        } else if (src instanceof ScriptObject) {
            sobj = (ScriptObject)src;
            if (sobj.has("script") && sobj.has("name")) {
                script = JSType.toString(sobj.get("script"));
                name = JSType.toString(sobj.get("name"));
                source = Source.sourceFor(name, script);
            }
        } else if (src instanceof Map && (map = (Map)src).containsKey("script") && map.containsKey("name")) {
            script = JSType.toString(map.get("script"));
            name = JSType.toString(map.get("name"));
            source = Source.sourceFor(name, script);
        }
        if (source != null) {
            Global global;
            if (scope instanceof ScriptObject && ((ScriptObject)scope).isScope()) {
                sobj = (ScriptObject)scope;
                assert (sobj.isGlobal()) : "non-Global scope object!!";
                return this.evaluateSource(source, sobj, sobj);
            }
            if (scope == null || scope == ScriptRuntime.UNDEFINED) {
                global = Context.getGlobal();
                return this.evaluateSource(source, global, global);
            }
            global = Context.getGlobal();
            ScriptObject evalScope = Context.newScope(global);
            ScriptObject withObj = ScriptRuntime.openWith(evalScope, scope);
            return this.evaluateSource(source, withObj, global);
        }
        throw ECMAErrors.typeError("cant.load.script", ScriptRuntime.safeToString(from));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object loadWithNewGlobal(Object from, Object ... args) throws IOException {
        Global oldGlobal = Context.getGlobal();
        Global newGlobal = AccessController.doPrivileged(() -> {
            try {
                return this.newGlobal();
            }
            catch (RuntimeException e) {
                if (DEBUG) {
                    e.printStackTrace();
                }
                throw e;
            }
        }, CREATE_GLOBAL_ACC_CTXT);
        this.initGlobal(newGlobal);
        Context.setGlobal(newGlobal);
        Object[] wrapped = args == null ? ScriptRuntime.EMPTY_ARRAY : ScriptObjectMirror.wrapArray(args, oldGlobal);
        newGlobal.put("arguments", newGlobal.wrapAsObject(wrapped), this.env._strict);
        try {
            Object object = ScriptObjectMirror.unwrap(ScriptObjectMirror.wrap(this.load(newGlobal, from), newGlobal), oldGlobal);
            return object;
        }
        finally {
            Context.setGlobal(oldGlobal);
        }
    }

    public static Class<? extends ScriptObject> forStructureClass(String fullName) throws ClassNotFoundException {
        if (System.getSecurityManager() != null && !StructureLoader.isStructureClass(fullName)) {
            throw new ClassNotFoundException(fullName);
        }
        return structureClasses.computeIfAbsent(fullName, name -> {
            try {
                return Class.forName(name, true, theStructLoader);
            }
            catch (ClassNotFoundException e) {
                throw new AssertionError((Object)e);
            }
        });
    }

    public static boolean isStructureClass(String className) {
        return StructureLoader.isStructureClass(className);
    }

    public static void checkPackageAccess(Class<?> clazz) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            Class<?> bottomClazz = clazz;
            while (bottomClazz.isArray()) {
                bottomClazz = bottomClazz.getComponentType();
            }
            Context.checkPackageAccess(sm, bottomClazz.getName());
        }
    }

    public static void checkPackageAccess(String pkgName) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            Context.checkPackageAccess(sm, (String)(pkgName.endsWith(".") ? pkgName : pkgName + "."));
        }
    }

    private static void checkPackageAccess(SecurityManager sm, String fullName) {
        Objects.requireNonNull(sm);
        int index = fullName.lastIndexOf(46);
        if (index != -1) {
            String pkgName = fullName.substring(0, index);
            AccessController.doPrivileged(() -> {
                sm.checkPackageAccess(pkgName);
                return null;
            }, NO_PERMISSIONS_ACC_CTXT);
        }
    }

    private static boolean isAccessiblePackage(Class<?> clazz) {
        try {
            Context.checkPackageAccess(clazz);
            return true;
        }
        catch (SecurityException se) {
            return false;
        }
    }

    public static boolean isAccessibleClass(Class<?> clazz) {
        return Modifier.isPublic(clazz.getModifiers()) && Context.isAccessiblePackage(clazz);
    }

    public Class<?> findClass(String fullName) throws ClassNotFoundException {
        if (fullName.indexOf(91) != -1 || fullName.indexOf(47) != -1) {
            throw new ClassNotFoundException(fullName);
        }
        if (this.classFilter != null && !this.classFilter.exposeToScripts(fullName)) {
            throw new ClassNotFoundException(fullName);
        }
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            Context.checkPackageAccess(sm, fullName);
        }
        if (this.appLoader != null) {
            return Class.forName(fullName, true, this.appLoader);
        }
        Class<?> cl = Class.forName(fullName);
        if (cl.getClassLoader() == null) {
            return cl;
        }
        throw new ClassNotFoundException(fullName);
    }

    public static void printStackTrace(Throwable t) {
        if (DEBUG) {
            t.printStackTrace(Context.getCurrentErr());
        }
    }

    public void verify(byte[] bytecode) {
        if (this.env._verify_code && System.getSecurityManager() == null) {
            CheckClassAdapter.verify(new ClassReader(bytecode), theStructLoader, false, new PrintWriter(System.err, true));
        }
    }

    public Global createGlobal() {
        return this.initGlobal(this.newGlobal());
    }

    public Global newGlobal() {
        this.createOrInvalidateGlobalConstants();
        return new Global(this);
    }

    private void createOrInvalidateGlobalConstants() {
        GlobalConstants newGlobalConstants;
        do {
            GlobalConstants currentGlobalConstants;
            if ((currentGlobalConstants = this.getGlobalConstants()) == null) continue;
            currentGlobalConstants.invalidateForever();
            return;
        } while (!this.globalConstantsRef.compareAndSet(null, newGlobalConstants = new GlobalConstants(this.getLogger(GlobalConstants.class))));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Global initGlobal(Global global, ScriptEngine engine) {
        if (!this.env._compile_only) {
            Global oldGlobal = Context.getGlobal();
            try {
                Context.setGlobal(global);
                global.initBuiltinObjects(engine);
            }
            finally {
                Context.setGlobal(oldGlobal);
            }
        }
        return global;
    }

    public Global initGlobal(Global global) {
        return this.initGlobal(global, null);
    }

    static Context getContextTrusted() {
        return Context.getContext(Context.getGlobal());
    }

    public static DynamicLinker getDynamicLinker(Class<?> clazz) {
        return Context.fromClass(clazz).dynamicLinker;
    }

    public static DynamicLinker getDynamicLinker() {
        return Context.getContextTrusted().dynamicLinker;
    }

    static Module createModuleTrusted(ModuleLayer parent, ModuleDescriptor descriptor, ClassLoader loader) {
        final String mn = descriptor.name();
        final ModuleReference mref = new ModuleReference(descriptor, null){

            @Override
            public ModuleReader open() {
                throw new UnsupportedOperationException();
            }
        };
        ModuleFinder finder = new ModuleFinder(){

            @Override
            public Optional<ModuleReference> find(String name) {
                if (name.equals(mn)) {
                    return Optional.of(mref);
                }
                return Optional.empty();
            }

            @Override
            public Set<ModuleReference> findAll() {
                return Set.of(mref);
            }
        };
        Configuration cf = parent.configuration().resolve(finder, ModuleFinder.of(new Path[0]), Set.of(mn));
        PrivilegedAction<ModuleLayer> pa = () -> parent.defineModules(cf, name -> loader);
        ModuleLayer layer = AccessController.doPrivileged(pa, GET_LOADER_ACC_CTXT);
        Module m = layer.findModule(mn).get();
        assert (m.getLayer() == layer);
        return m;
    }

    static Context getContextTrustedOrNull() {
        Global global = Context.getGlobal();
        return global == null ? null : Context.getContext(global);
    }

    private static Context getContext(Global global) {
        return ((ScriptObject)global).getContext();
    }

    static Context fromClass(Class<?> clazz) {
        ClassLoader loader = null;
        try {
            loader = clazz.getClassLoader();
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        if (loader instanceof ScriptLoader) {
            return ((ScriptLoader)loader).getContext();
        }
        return Context.getContextTrusted();
    }

    private URL getResourceURL(String resName) {
        if (this.appLoader != null) {
            return this.appLoader.getResource(resName);
        }
        return ClassLoader.getSystemResource(resName);
    }

    private Object evaluateSource(Source source, ScriptObject scope, ScriptObject thiz) {
        ScriptFunction script = null;
        try {
            script = this.compileScript(source, scope, new ThrowErrorManager());
        }
        catch (ParserException e) {
            e.throwAsEcmaException();
        }
        return ScriptRuntime.apply(script, thiz, new Object[0]);
    }

    private static ScriptFunction getProgramFunction(Class<?> script, ScriptObject scope) {
        if (script == null) {
            return null;
        }
        return Context.invokeCreateProgramFunctionHandle(Context.getCreateProgramFunctionHandle(script), scope);
    }

    private static MethodHandle getCreateProgramFunctionHandle(Class<?> script) {
        try {
            return LOOKUP.findStatic(script, CompilerConstants.CREATE_PROGRAM_FUNCTION.symbolName(), CREATE_PROGRAM_FUNCTION_TYPE);
        }
        catch (IllegalAccessException | NoSuchMethodException e) {
            throw new AssertionError("Failed to retrieve a handle for the program function for " + script.getName(), e);
        }
    }

    private static ScriptFunction invokeCreateProgramFunctionHandle(MethodHandle createProgramFunctionHandle, ScriptObject scope) {
        try {
            return createProgramFunctionHandle.invokeExact(scope);
        }
        catch (Error | RuntimeException e) {
            throw e;
        }
        catch (Throwable t) {
            throw new AssertionError("Failed to create a program function", t);
        }
    }

    private ScriptFunction compileScript(Source source, ScriptObject scope, ErrorManager errMan) {
        return Context.getProgramFunction(this.compile(source, errMan, this._strict), scope);
    }

    private synchronized Class<?> compile(Source source, ErrorManager errMan, boolean strict) {
        ContextCodeInstaller installer;
        String cacheKey;
        errMan.reset();
        Class<?> script = this.findCachedClass(source);
        if (script != null) {
            DebugLogger log = this.getLogger(Compiler.class);
            if (log.isEnabled()) {
                log.fine(new RuntimeEvent<Source>(Level.INFO, source), "Code cache hit for ", source, " avoiding recompile.");
            }
            return script;
        }
        StoredScript storedScript = null;
        FunctionNode functionNode = null;
        boolean useCodeStore = this.codeStore != null && !this.env._parse_only && (!this.env._optimistic_types || this.env._lazy_compilation);
        String string = cacheKey = useCodeStore ? CodeStore.getCacheKey("script", null) : null;
        if (useCodeStore) {
            storedScript = this.codeStore.load(source, cacheKey);
        }
        if (storedScript == null) {
            if (this.env._dest_dir != null) {
                source.dump(this.env._dest_dir);
            }
            functionNode = new Parser(this.env, source, errMan, strict, this.getLogger(Parser.class)).parse();
            if (errMan.hasErrors()) {
                return null;
            }
            if (this.env._print_ast || functionNode.getDebugFlag(4)) {
                this.getErr().println(new ASTWriter(functionNode));
            }
            if (this.env._print_parse || functionNode.getDebugFlag(1)) {
                this.getErr().println(new PrintVisitor(functionNode, true, false));
            }
        }
        if (this.env._parse_only) {
            return null;
        }
        URL url = source.getURL();
        CodeSource cs = new CodeSource(url, (CodeSigner[])null);
        if (this.env._persistent_cache || !this.env._lazy_compilation || !this.env.useAnonymousClasses(source.getLength(), () -> AnonymousContextCodeInstaller.initFailure)) {
            ScriptLoader loader = this.env._loader_per_compile ? this.createNewLoader() : this.scriptLoader;
            installer = new NamedContextCodeInstaller(this, cs, loader);
        } else {
            installer = new AnonymousContextCodeInstaller(this, cs, this.anonymousHostClasses.getOrCreate(cs, key -> this.createNewLoader().installClass(AnonymousContextCodeInstaller.ANONYMOUS_HOST_CLASS_NAME, AnonymousContextCodeInstaller.ANONYMOUS_HOST_CLASS_BYTES, cs)));
        }
        if (storedScript == null) {
            Compiler.CompilationPhases phases = Compiler.CompilationPhases.COMPILE_ALL;
            Compiler compiler = Compiler.forInitialCompilation(installer, source, errMan, strict | functionNode.isStrict());
            FunctionNode compiledFunction = compiler.compile(functionNode, phases);
            if (errMan.hasErrors()) {
                return null;
            }
            script = compiledFunction.getRootClass();
            compiler.persistClassInfo(cacheKey, compiledFunction);
        } else {
            Compiler.updateCompilationId(storedScript.getCompilationId());
            script = storedScript.installScript(source, installer);
        }
        this.cacheClass(source, script);
        return script;
    }

    private ScriptLoader createNewLoader() {
        return AccessController.doPrivileged(() -> new ScriptLoader(this), CREATE_LOADER_ACC_CTXT);
    }

    private long getUniqueScriptId() {
        return this.uniqueScriptId.getAndIncrement();
    }

    private Class<?> findCachedClass(Source source) {
        ClassReference ref = this.classCache == null ? null : this.classCache.get(source);
        return ref != null ? (Class)ref.get() : null;
    }

    private void cacheClass(Source source, Class<?> clazz) {
        if (this.classCache != null) {
            this.classCache.cache(source, clazz);
        }
    }

    private void initLoggers() {
        ((Loggable)((Object)MethodHandleFactory.getFunctionality())).initLogger(this);
    }

    public DebugLogger getLogger(Class<? extends Loggable> clazz) {
        return this.getLogger(clazz, null);
    }

    public DebugLogger getLogger(Class<? extends Loggable> clazz, Consumer<DebugLogger> initHook) {
        String name = Context.getLoggerName(clazz);
        DebugLogger logger = this.loggers.get(name);
        if (logger == null) {
            if (!this.env.hasLogger(name)) {
                return DebugLogger.DISABLED_LOGGER;
            }
            LoggingOption.LoggerInfo info = this.env._loggers.get(name);
            logger = new DebugLogger(name, info.getLevel(), info.isQuiet());
            if (initHook != null) {
                initHook.accept(logger);
            }
            this.loggers.put(name, logger);
        }
        return logger;
    }

    public MethodHandle addLoggingToHandle(Class<? extends Loggable> clazz, MethodHandle mh, Supplier<String> text) {
        return this.addLoggingToHandle(clazz, Level.INFO, mh, Integer.MAX_VALUE, false, text);
    }

    public MethodHandle addLoggingToHandle(Class<? extends Loggable> clazz, Level level, MethodHandle mh, int paramStart, boolean printReturnValue, Supplier<String> text) {
        DebugLogger log = this.getLogger(clazz);
        if (log.isEnabled()) {
            return MethodHandleFactory.addDebugPrintout(log, level, mh, paramStart, printReturnValue, text.get());
        }
        return mh;
    }

    private static String getLoggerName(Class<?> clazz) {
        for (Class<?> current = clazz; current != null; current = current.getSuperclass()) {
            Logger log = current.getAnnotation(Logger.class);
            if (log == null) continue;
            assert (!"".equals(log.name()));
            return log.name();
        }
        assert (false);
        return null;
    }

    public SwitchPoint newBuiltinSwitchPoint(String name) {
        assert (this.builtinSwitchPoints.get(name) == null);
        BuiltinSwitchPoint sp = new BuiltinSwitchPoint();
        this.builtinSwitchPoints.put(name, sp);
        return sp;
    }

    public SwitchPoint getBuiltinSwitchPoint(String name) {
        return this.builtinSwitchPoints.get(name);
    }

    private static ClassLoader createModuleLoader(ClassLoader cl, String modulePath, String addModules) {
        if (addModules == null) {
            throw new IllegalArgumentException("--module-path specified with no --add-modules");
        }
        Path[] paths = (Path[])Stream.of(modulePath.split(File.pathSeparator)).map(x$0 -> Paths.get(x$0, new String[0])).toArray(Path[]::new);
        ModuleFinder mf = ModuleFinder.of(paths);
        Set<ModuleReference> mrefs = mf.findAll();
        if (mrefs.isEmpty()) {
            throw new RuntimeException("No modules in script --module-path: " + modulePath);
        }
        Set<String> rootMods = addModules.equals("ALL-MODULE-PATH") ? mrefs.stream().map(mr -> mr.descriptor().name()).collect(Collectors.toSet()) : Stream.of(addModules.split(",")).map(String::trim).collect(Collectors.toSet());
        ModuleLayer boot = ModuleLayer.boot();
        Configuration conf = boot.configuration().resolve(mf, ModuleFinder.of(new Path[0]), rootMods);
        String firstMod = rootMods.iterator().next();
        return boot.defineModulesWithOneLoader(conf, cl).findLoader(firstMod);
    }

    static {
        DebuggerSupport.FORCELOAD = true;
        DEBUG = Options.getBooleanProperty("nashorn.debug");
        currentGlobal = new ThreadLocal();
        ModuleLayer boot = ModuleLayer.boot();
        javaSqlFound = boot.findModule("java.sql").isPresent();
        javaSqlRowsetFound = boot.findModule("java.sql.rowset").isPresent();
        structureClasses = new ConcurrentHashMap();
        NO_PERMISSIONS_ACC_CTXT = Context.createNoPermAccCtxt();
        CREATE_LOADER_ACC_CTXT = Context.createPermAccCtxt("createClassLoader");
        CREATE_GLOBAL_ACC_CTXT = Context.createPermAccCtxt(NASHORN_CREATE_GLOBAL);
        GET_LOADER_ACC_CTXT = Context.createPermAccCtxt("getClassLoader");
        ClassLoader myLoader = Context.class.getClassLoader();
        theStructLoader = AccessController.doPrivileged(() -> new StructureLoader(myLoader), CREATE_LOADER_ACC_CTXT);
    }

    public static final class BuiltinSwitchPoint
    extends SwitchPoint {
    }

    private static class ClassReference
    extends SoftReference<Class<?>> {
        private final Source source;

        ClassReference(Class<?> clazz, ReferenceQueue<Class<?>> queue, Source source) {
            super(clazz, queue);
            this.source = source;
        }
    }

    @Logger(name="classcache")
    private static class ClassCache
    extends LinkedHashMap<Source, ClassReference>
    implements Loggable {
        private final int size;
        private final ReferenceQueue<Class<?>> queue;
        private final DebugLogger log;

        ClassCache(Context context, int size) {
            super(size, 0.75f, true);
            this.size = size;
            this.queue = new ReferenceQueue();
            this.log = this.initLogger(context);
        }

        void cache(Source source, Class<?> clazz) {
            if (this.log.isEnabled()) {
                this.log.info("Caching ", source, " in class cache");
            }
            this.put(source, new ClassReference(clazz, this.queue, source));
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<Source, ClassReference> eldest) {
            return this.size() > this.size;
        }

        @Override
        public ClassReference get(Object key) {
            ClassReference ref;
            while ((ref = (ClassReference)this.queue.poll()) != null) {
                Source source = ref.source;
                if (this.log.isEnabled()) {
                    this.log.info("Evicting ", source, " from class cache.");
                }
                this.remove(source);
            }
            ref = (ClassReference)super.get(key);
            if (ref != null && this.log.isEnabled()) {
                this.log.info("Retrieved class reference for ", ref.source, " from class cache");
            }
            return ref;
        }

        @Override
        public DebugLogger initLogger(Context context) {
            return context.getLogger(this.getClass());
        }

        @Override
        public DebugLogger getLogger() {
            return this.log;
        }
    }

    public static interface MultiGlobalCompiledScript {
        public ScriptFunction getFunction(Global var1);
    }

    public static class ThrowErrorManager
    extends ErrorManager {
        @Override
        public void error(String message) {
            throw new ParserException(message);
        }

        @Override
        public void error(ParserException e) {
            throw e;
        }
    }

    private static final class AnonymousContextCodeInstaller
    extends ContextCodeInstaller {
        private static final MethodHandle DEFINE_ANONYMOUS_CLASS = AnonymousContextCodeInstaller.getDefineAnonymousClass();
        private static final String ANONYMOUS_HOST_CLASS_NAME = "org/openjdk/nashorn/internal/scripts".replace('/', '.') + ".AnonymousHost";
        private static final byte[] ANONYMOUS_HOST_CLASS_BYTES = AnonymousContextCodeInstaller.getAnonymousHostClassBytes();
        static volatile Exception initFailure;
        private final Class<?> hostClass;

        private static MethodHandle getDefineAnonymousClass() {
            return AccessController.doPrivileged(() -> {
                try {
                    MethodHandle mh = MethodHandles.lookup().findVirtual(Unsafe.class, "defineAnonymousClass", MethodType.methodType(Class.class, Class.class, byte[].class, Object[].class));
                    Field f = Unsafe.class.getDeclaredField("theUnsafe");
                    f.setAccessible(true);
                    return mh.bindTo(f.get(null));
                }
                catch (Exception e) {
                    initFailure = e;
                    return null;
                }
            });
        }

        private AnonymousContextCodeInstaller(Context context, CodeSource codeSource, Class<?> hostClass) {
            super(context, codeSource);
            this.hostClass = hostClass;
        }

        @Override
        public Class<?> install(String className, byte[] bytecode) {
            ANONYMOUS_INSTALLED_SCRIPT_COUNT.increment();
            try {
                return DEFINE_ANONYMOUS_CLASS.invokeExact(this.hostClass, bytecode, null);
            }
            catch (Error | RuntimeException e) {
                throw e;
            }
            catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public CodeInstaller getOnDemandCompilationInstaller() {
            return this;
        }

        @Override
        public CodeInstaller getMultiClassCodeInstaller() {
            return new NamedContextCodeInstaller(this.context, this.codeSource, this.context.createNewLoader());
        }

        private static byte[] getAnonymousHostClassBytes() {
            ClassWriter cw = new ClassWriter(3);
            cw.visit(51, 1536, ANONYMOUS_HOST_CLASS_NAME.replace('.', '/'), null, "java/lang/Object", null);
            cw.visitEnd();
            return cw.toByteArray();
        }
    }

    private static class NamedContextCodeInstaller
    extends ContextCodeInstaller {
        private final ScriptLoader loader;
        private int usageCount = 0;
        private int bytesDefined = 0;
        private static final int MAX_USAGES = 10;
        private static final int MAX_BYTES_DEFINED = 200000;

        private NamedContextCodeInstaller(Context context, CodeSource codeSource, ScriptLoader loader) {
            super(context, codeSource);
            this.loader = loader;
        }

        @Override
        public Class<?> install(String className, byte[] bytecode) {
            ++this.usageCount;
            this.bytesDefined += bytecode.length;
            NAMED_INSTALLED_SCRIPT_COUNT.increment();
            return this.loader.installClass(Compiler.binaryName(className), bytecode, this.codeSource);
        }

        @Override
        public CodeInstaller getOnDemandCompilationInstaller() {
            if (this.usageCount < 10 && this.bytesDefined < 200000) {
                return this;
            }
            return new NamedContextCodeInstaller(this.context, this.codeSource, this.context.createNewLoader());
        }

        @Override
        public CodeInstaller getMultiClassCodeInstaller() {
            return this;
        }
    }

    private static abstract class ContextCodeInstaller
    implements CodeInstaller {
        final Context context;
        final CodeSource codeSource;

        ContextCodeInstaller(Context context, CodeSource codeSource) {
            this.context = context;
            this.codeSource = codeSource;
        }

        @Override
        public Context getContext() {
            return this.context;
        }

        @Override
        public void initialize(Collection<Class<?>> classes, Source source, Object[] constants) {
            try {
                AccessController.doPrivileged(() -> {
                    for (Class clazz : classes) {
                        Field sourceField = clazz.getDeclaredField(CompilerConstants.SOURCE.symbolName());
                        sourceField.setAccessible(true);
                        sourceField.set(null, source);
                        Field constantsField = clazz.getDeclaredField(CompilerConstants.CONSTANTS.symbolName());
                        constantsField.setAccessible(true);
                        constantsField.set(null, constants);
                    }
                    return null;
                });
            }
            catch (PrivilegedActionException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void verify(byte[] code) {
            this.context.verify(code);
        }

        @Override
        public long getUniqueScriptId() {
            return this.context.getUniqueScriptId();
        }

        @Override
        public void storeScript(String cacheKey, Source source, String mainClassName, Map<String, byte[]> classBytes, Map<Integer, FunctionInitializer> initializers, Object[] constants, int compilationId) {
            if (this.context.codeStore != null) {
                this.context.codeStore.store(cacheKey, source, mainClassName, classBytes, initializers, constants, compilationId);
            }
        }

        @Override
        public StoredScript loadScript(Source source, String functionKey) {
            if (this.context.codeStore != null) {
                return this.context.codeStore.load(source, functionKey);
            }
            return null;
        }

        @Override
        public boolean isCompatibleWith(CodeInstaller other) {
            if (other instanceof ContextCodeInstaller) {
                ContextCodeInstaller cci = (ContextCodeInstaller)other;
                return cci.context == this.context && cci.codeSource == this.codeSource;
            }
            return false;
        }
    }

    private static enum FieldMode {
        AUTO,
        OBJECTS,
        DUAL;

    }
}
