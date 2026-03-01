package net.optifine;

import net.optifine.ChunkSectionDataOF;

public class ChunkDataOF {
    private ChunkSectionDataOF[] chunkSectionDatas;

    public ChunkDataOF(ChunkSectionDataOF[] chunkSectionDatas) {
        this.chunkSectionDatas = chunkSectionDatas;
    }

    public ChunkSectionDataOF[] getChunkSectionDatas() {
        return this.chunkSectionDatas;
    }

    public void setChunkSectionDatas(ChunkSectionDataOF[] chunkSectionDatas) {
        this.chunkSectionDatas = chunkSectionDatas;
    }
}
