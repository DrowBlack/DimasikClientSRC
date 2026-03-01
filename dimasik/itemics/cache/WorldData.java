package dimasik.itemics.cache;

import dimasik.itemics.Itemics;
import dimasik.itemics.api.cache.ICachedWorld;
import dimasik.itemics.api.cache.IWaypointCollection;
import dimasik.itemics.api.cache.IWorldData;
import dimasik.itemics.cache.CachedWorld;
import dimasik.itemics.cache.WaypointCollection;
import java.nio.file.Path;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import org.apache.commons.codec.digest.DigestUtils;

public class WorldData
implements IWorldData {
    public final CachedWorld cache;
    private final WaypointCollection waypoints;
    public final Path directory;
    public final RegistryKey<World> dimension;

    WorldData(Path directory, RegistryKey<World> dimension) {
        this.directory = directory;
        this.cache = new CachedWorld(directory.resolve(DigestUtils.sha512Hex(DigestUtils.sha512Hex("cache"))), dimension);
        this.waypoints = new WaypointCollection(directory.resolve(DigestUtils.sha512Hex(DigestUtils.sha512Hex("waypoints"))));
        this.dimension = dimension;
    }

    public void onClose() {
        Itemics.getExecutor().execute(() -> {
            System.out.println("Started saving the world in a new thread");
            this.cache.save();
        });
    }

    @Override
    public ICachedWorld getCachedWorld() {
        return this.cache;
    }

    @Override
    public IWaypointCollection getWaypoints() {
        return this.waypoints;
    }
}
