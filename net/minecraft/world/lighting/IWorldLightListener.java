package net.minecraft.world.lighting;

import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.lighting.ILightListener;

public interface IWorldLightListener
extends ILightListener {
    @Nullable
    public NibbleArray getData(SectionPos var1);

    public int getLightFor(BlockPos var1);

    public static enum Dummy implements IWorldLightListener
    {
        INSTANCE;


        @Override
        @Nullable
        public NibbleArray getData(SectionPos p_215612_1_) {
            return null;
        }

        @Override
        public int getLightFor(BlockPos worldPos) {
            return 0;
        }

        @Override
        public void updateSectionStatus(SectionPos pos, boolean isEmpty) {
        }
    }
}
