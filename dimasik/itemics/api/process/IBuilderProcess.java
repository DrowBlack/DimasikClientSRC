package dimasik.itemics.api.process;

import dimasik.itemics.api.process.IItemicsProcess;
import dimasik.itemics.api.schematic.ISchematic;
import java.io.File;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;

public interface IBuilderProcess
extends IItemicsProcess {
    public void build(String var1, ISchematic var2, Vector3i var3);

    public boolean build(String var1, File var2, Vector3i var3);

    default public boolean build(String schematicFile, BlockPos origin) {
        File file = new File(new File(Minecraft.getInstance().gameDir, "schematics"), schematicFile);
        return this.build(schematicFile, file, (Vector3i)origin);
    }

    public void buildOpenSchematic();

    public void buildOpenLitematic(int var1);

    public void pause();

    public boolean isPaused();

    public void resume();

    public void clearArea(BlockPos var1, BlockPos var2);

    public List<BlockState> getApproxPlaceable();
}
