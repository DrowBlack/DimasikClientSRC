package cpw.mods.modlauncher.api;

import java.util.function.Predicate;

public interface ITransformingClassLoader {
    default public ClassLoader getInstance() {
        return (ClassLoader)((Object)this);
    }

    public void addTargetPackageFilter(Predicate<String> var1);
}
