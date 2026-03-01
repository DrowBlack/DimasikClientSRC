package dimasik.itemics.cache;

import dimasik.itemics.Itemics;
import dimasik.itemics.api.cache.IWorldProvider;
import dimasik.itemics.api.utils.Helper;
import dimasik.itemics.cache.WorldData;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.storage.FolderName;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.SystemUtils;

public class WorldProvider
implements IWorldProvider,
Helper {
    private static final Map<Path, WorldData> worldCache = new HashMap<Path, WorldData>();
    private WorldData currentWorld;
    private World mcWorld;

    @Override
    public final WorldData getCurrentWorld() {
        this.detectAndHandleBrokenLoading();
        return this.currentWorld;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void initWorld(RegistryKey<World> world) {
        File directory;
        IntegratedServer integratedServer = mc.getIntegratedServer();
        if (mc.isSingleplayer()) {
            directory = DimensionType.getDimensionFolder(world, integratedServer.func_240776_a_(FolderName.DOT).toFile());
            if (directory.toPath().relativize(WorldProvider.mc.gameDir.toPath()).getNameCount() != 2) {
                directory = directory.getParentFile();
            }
            File readme = directory = new File(directory, "Itemics");
        } else {
            if (mc.getCurrentServerData() == null) {
                System.out.println("World seems to be a replay. Not loading cache.");
                this.currentWorld = null;
                this.mcWorld = WorldProvider.mc.world;
                return;
            }
            String folderName = mc.isConnectedToRealms() ? "realms" : DigestUtils.md5Hex(DigestUtils.md5Hex(WorldProvider.mc.getCurrentServerData().serverIP));
            if (SystemUtils.IS_OS_WINDOWS) {
                folderName = folderName.replace(":", "_");
            }
            directory = new File(Itemics.getDir(), folderName);
            File readme = Itemics.getDir();
        }
        Path dir = DimensionType.getDimensionFolder(world, directory).toPath();
        if (!Files.exists(dir, new LinkOption[0])) {
            try {
                Files.createDirectories(dir, new FileAttribute[0]);
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        System.out.println("world data dir: " + String.valueOf(dir));
        Map<Path, WorldData> map = worldCache;
        synchronized (map) {
            this.currentWorld = worldCache.computeIfAbsent(dir, d -> new WorldData((Path)d, world));
        }
        this.mcWorld = WorldProvider.mc.world;
    }

    public final void closeWorld() {
        WorldData world = this.currentWorld;
        this.currentWorld = null;
        this.mcWorld = null;
        if (world == null) {
            return;
        }
        world.onClose();
    }

    public final void ifWorldLoaded(Consumer<WorldData> currentWorldConsumer) {
        this.detectAndHandleBrokenLoading();
        if (this.currentWorld != null) {
            currentWorldConsumer.accept(this.currentWorld);
        }
    }

    private final void detectAndHandleBrokenLoading() {
        if (this.mcWorld != WorldProvider.mc.world) {
            if (this.currentWorld != null) {
                System.out.println("mc.world unloaded unnoticed! Unloading cache now.");
                this.closeWorld();
            }
            if (WorldProvider.mc.world != null) {
                System.out.println("mc.world loaded unnoticed! Loading cache now.");
                this.initWorld(WorldProvider.mc.world.getDimensionKey());
            }
        } else if (this.currentWorld == null && WorldProvider.mc.world != null && (mc.isSingleplayer() || mc.getCurrentServerData() != null)) {
            System.out.println("Retrying to load cache");
            this.initWorld(WorldProvider.mc.world.getDimensionKey());
        }
    }
}
