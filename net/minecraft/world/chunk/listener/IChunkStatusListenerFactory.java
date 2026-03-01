package net.minecraft.world.chunk.listener;

import net.minecraft.world.chunk.listener.IChunkStatusListener;

public interface IChunkStatusListenerFactory {
    public IChunkStatusListener create(int var1);
}
