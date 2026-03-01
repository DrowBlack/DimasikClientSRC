package dimasik.itemics.utils.schematic.litematica;

import dimasik.itemics.utils.schematic.format.defaults.LitematicaSchematic;
import fi.dy.masa.litematica.Litematica;
import fi.dy.masa.litematica.data.DataManager;
import java.io.File;
import net.minecraft.block.BlockState;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.vector.Vector3i;

public final class LitematicaHelper {
    public static boolean isLitematicaPresent() {
        try {
            Class.forName(Litematica.class.getName());
            return true;
        }
        catch (ClassNotFoundException | NoClassDefFoundError ex) {
            return false;
        }
    }

    public static boolean hasLoadedSchematic() {
        return DataManager.getSchematicPlacementManager().getAllSchematicsPlacements().size() > 0;
    }

    public static String getName(int i) {
        return DataManager.getSchematicPlacementManager().getAllSchematicsPlacements().get(i).getName();
    }

    public static Vector3i getOrigin(int i) {
        return DataManager.getSchematicPlacementManager().getAllSchematicsPlacements().get(i).getOrigin();
    }

    public static File getSchematicFile(int i) {
        return DataManager.getSchematicPlacementManager().getAllSchematicsPlacements().get(i).getSchematicFile();
    }

    public static Rotation getRotation(int i) {
        return DataManager.getSchematicPlacementManager().getAllSchematicsPlacements().get(i).getRotation();
    }

    public static Mirror getMirror(int i) {
        return DataManager.getSchematicPlacementManager().getAllSchematicsPlacements().get(i).getMirror();
    }

    public static Vector3i getCorrectedOrigin(LitematicaSchematic schematic, int i) {
        int x = LitematicaHelper.getOrigin(i).getX();
        int y = LitematicaHelper.getOrigin(i).getY();
        int z = LitematicaHelper.getOrigin(i).getZ();
        int mx = schematic.getOffsetMinCorner().getX();
        int my = schematic.getOffsetMinCorner().getY();
        int mz = schematic.getOffsetMinCorner().getZ();
        int sx = (schematic.getX() - 1) * -1;
        int sz = (schematic.getZ() - 1) * -1;
        Mirror mirror = LitematicaHelper.getMirror(i);
        Rotation rotation = LitematicaHelper.getRotation(i);
        return switch (mirror) {
            case Mirror.FRONT_BACK, Mirror.LEFT_RIGHT -> {
                switch ((mirror.ordinal() * 2 + rotation.ordinal()) % 4) {
                    case 1: {
                        yield new Vector3i(x + (sz - mz), y + my, z + (sx - mx));
                    }
                    case 2: {
                        yield new Vector3i(x + mx, y + my, z + (sz - mz));
                    }
                    case 3: {
                        yield new Vector3i(x + mz, y + my, z + mx);
                    }
                }
                yield new Vector3i(x + (sx - mx), y + my, z + mz);
            }
            default -> {
                switch (rotation) {
                    case CLOCKWISE_90: {
                        yield new Vector3i(x + (sz - mz), y + my, z + mx);
                    }
                    case CLOCKWISE_180: {
                        yield new Vector3i(x + (sx - mx), y + my, z + (sz - mz));
                    }
                    case COUNTERCLOCKWISE_90: {
                        yield new Vector3i(x + mz, y + my, z + (sx - mx));
                    }
                }
                yield new Vector3i(x + mx, y + my, z + mz);
            }
        };
    }

    public static Vector3i doMirroring(Vector3i in, int sizeX, int sizeZ, Mirror mirror) {
        int xOut = in.getX();
        int zOut = in.getZ();
        if (mirror == Mirror.LEFT_RIGHT) {
            zOut = sizeZ - in.getZ();
        } else if (mirror == Mirror.FRONT_BACK) {
            xOut = sizeX - in.getX();
        }
        return new Vector3i(xOut, in.getY(), zOut);
    }

    public static Vector3i rotate(Vector3i in, int sizeX, int sizeZ) {
        return new Vector3i(sizeX - (sizeX - sizeZ) - in.getZ(), in.getY(), in.getX());
    }

    public static LitematicaSchematic blackMagicFuckery(LitematicaSchematic schemIn, int i) {
        LitematicaSchematic tempSchem = schemIn.getCopy(LitematicaHelper.getRotation(i).ordinal() % 2 == 1);
        for (int yCounter = 0; yCounter < schemIn.getY(); ++yCounter) {
            for (int zCounter = 0; zCounter < schemIn.getZ(); ++zCounter) {
                for (int xCounter = 0; xCounter < schemIn.getX(); ++xCounter) {
                    Vector3i xyzHolder = new Vector3i(xCounter, yCounter, zCounter);
                    xyzHolder = LitematicaHelper.doMirroring(xyzHolder, schemIn.getX() - 1, schemIn.getZ() - 1, LitematicaHelper.getMirror(i));
                    for (int turns = 0; turns < LitematicaHelper.getRotation(i).ordinal(); ++turns) {
                        xyzHolder = turns % 2 == 0 ? LitematicaHelper.rotate(xyzHolder, schemIn.getX() - 1, schemIn.getZ() - 1) : LitematicaHelper.rotate(xyzHolder, schemIn.getZ() - 1, schemIn.getX() - 1);
                    }
                    BlockState state = schemIn.getDirect(xCounter, yCounter, zCounter);
                    try {
                        state = state.mirror(LitematicaHelper.getMirror(i)).rotate(LitematicaHelper.getRotation(i));
                    }
                    catch (NullPointerException nullPointerException) {
                        // empty catch block
                    }
                    tempSchem.setDirect(xyzHolder.getX(), xyzHolder.getY(), xyzHolder.getZ(), state);
                }
            }
        }
        return tempSchem;
    }
}
