package net.minecraftforge.forgespi.language;

import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraftforge.forgespi.language.ILifecycleEvent;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.ModFileScanData;

public interface IModLanguageProvider {
    public String name();

    public Consumer<ModFileScanData> getFileVisitor();

    public <R extends ILifecycleEvent<R>> void consumeLifecycleEvent(Supplier<R> var1);

    public static interface IModLanguageLoader {
        public <T> T loadMod(IModInfo var1, ClassLoader var2, ModFileScanData var3);
    }
}
