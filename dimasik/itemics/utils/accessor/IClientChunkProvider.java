package dimasik.itemics.utils.accessor;

import dimasik.itemics.utils.accessor.IChunkArray;
import net.minecraft.client.multiplayer.ClientChunkProvider;

public interface IClientChunkProvider {
    public ClientChunkProvider createThreadSafeCopy();

    public IChunkArray extractReferenceArray();
}
