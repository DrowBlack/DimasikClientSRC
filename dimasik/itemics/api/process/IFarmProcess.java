package dimasik.itemics.api.process;

import dimasik.itemics.api.process.IItemicsProcess;
import net.minecraft.util.math.BlockPos;

public interface IFarmProcess
extends IItemicsProcess {
    public void farm(int var1, BlockPos var2);

    default public void farm() {
        this.farm(0, null);
    }

    default public void farm(int range) {
        this.farm(range, null);
    }
}
