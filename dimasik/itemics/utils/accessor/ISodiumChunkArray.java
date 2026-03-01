package dimasik.itemics.utils.accessor;

import dimasik.itemics.utils.accessor.IChunkArray;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

public interface ISodiumChunkArray
extends IChunkArray {
    public ObjectIterator<Long2ObjectMap.Entry<Object>> callIterator();
}
