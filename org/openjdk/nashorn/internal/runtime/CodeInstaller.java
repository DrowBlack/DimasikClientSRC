package org.openjdk.nashorn.internal.runtime;

import java.util.Collection;
import java.util.Map;
import org.openjdk.nashorn.internal.runtime.Context;
import org.openjdk.nashorn.internal.runtime.FunctionInitializer;
import org.openjdk.nashorn.internal.runtime.Source;
import org.openjdk.nashorn.internal.runtime.StoredScript;

public interface CodeInstaller {
    public Context getContext();

    public Class<?> install(String var1, byte[] var2);

    public void initialize(Collection<Class<?>> var1, Source var2, Object[] var3);

    public void verify(byte[] var1);

    public long getUniqueScriptId();

    public void storeScript(String var1, Source var2, String var3, Map<String, byte[]> var4, Map<Integer, FunctionInitializer> var5, Object[] var6, int var7);

    public StoredScript loadScript(Source var1, String var2);

    public CodeInstaller getOnDemandCompilationInstaller();

    public CodeInstaller getMultiClassCodeInstaller();

    public boolean isCompatibleWith(CodeInstaller var1);
}
