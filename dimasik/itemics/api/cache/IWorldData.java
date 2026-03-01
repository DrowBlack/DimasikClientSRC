package dimasik.itemics.api.cache;

import dimasik.itemics.api.cache.ICachedWorld;
import dimasik.itemics.api.cache.IWaypointCollection;

public interface IWorldData {
    public ICachedWorld getCachedWorld();

    public IWaypointCollection getWaypoints();
}
