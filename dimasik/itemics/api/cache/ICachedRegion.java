package dimasik.itemics.api.cache;

import dimasik.itemics.api.cache.IBlockTypeAccess;

public interface ICachedRegion
extends IBlockTypeAccess {
    public boolean isCached(int var1, int var2);

    public int getX();

    public int getZ();
}
