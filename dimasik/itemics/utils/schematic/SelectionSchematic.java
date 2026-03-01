package dimasik.itemics.utils.schematic;

import dimasik.itemics.api.schematic.ISchematic;
import dimasik.itemics.api.schematic.MaskSchematic;
import dimasik.itemics.api.selection.ISelection;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3i;

public class SelectionSchematic
extends MaskSchematic {
    private final ISelection[] selections;

    public SelectionSchematic(ISchematic schematic, Vector3i origin, ISelection[] selections) {
        super(schematic);
        this.selections = (ISelection[])Stream.of(selections).map(sel -> sel.shift(Direction.WEST, origin.getX()).shift(Direction.DOWN, origin.getY()).shift(Direction.NORTH, origin.getZ())).toArray(ISelection[]::new);
    }

    @Override
    protected boolean partOfMask(int x, int y, int z, BlockState currentState) {
        for (ISelection selection : this.selections) {
            if (x < selection.min().x || y < selection.min().y || z < selection.min().z || x > selection.max().x || y > selection.max().y || z > selection.max().z) continue;
            return true;
        }
        return false;
    }
}
