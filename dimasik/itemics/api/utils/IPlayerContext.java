package dimasik.itemics.api.utils;

import dimasik.itemics.api.cache.IWorldData;
import dimasik.itemics.api.utils.BetterBlockPos;
import dimasik.itemics.api.utils.IPlayerController;
import dimasik.itemics.api.utils.Rotation;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.block.SlabBlock;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public interface IPlayerContext {
    public ClientPlayerEntity player();

    public IPlayerController playerController();

    public World world();

    default public Iterable<Entity> entities() {
        return ((ClientWorld)this.world()).getAllEntities();
    }

    default public Stream<Entity> entitiesStream() {
        return StreamSupport.stream(this.entities().spliterator(), false);
    }

    public IWorldData worldData();

    public RayTraceResult objectMouseOver();

    default public BetterBlockPos playerFeet() {
        BetterBlockPos feet = new BetterBlockPos(this.player().getPositionVec().x, this.player().getPositionVec().y + 0.1251, this.player().getPositionVec().z);
        try {
            if (this.world().getBlockState(feet).getBlock() instanceof SlabBlock) {
                return feet.up();
            }
        }
        catch (NullPointerException nullPointerException) {
            // empty catch block
        }
        return feet;
    }

    default public Vector3d playerFeetAsVec() {
        return new Vector3d(this.player().getPositionVec().x, this.player().getPositionVec().y, this.player().getPositionVec().z);
    }

    default public Vector3d playerHead() {
        return new Vector3d(this.player().getPositionVec().x, this.player().getPositionVec().y + (double)this.player().getEyeHeight(), this.player().getPositionVec().z);
    }

    default public Rotation playerRotations() {
        return new Rotation(this.player().packetYaw, this.player().packetPitch);
    }

    public static double eyeHeight(boolean ifSneaking) {
        return ifSneaking ? 1.27 : 1.62;
    }

    default public Optional<BlockPos> getSelectedBlock() {
        RayTraceResult result = this.objectMouseOver();
        if (result != null && result.getType() == RayTraceResult.Type.BLOCK) {
            return Optional.of(((BlockRayTraceResult)result).getPos());
        }
        return Optional.empty();
    }

    default public boolean isLookingAt(BlockPos pos) {
        return this.getSelectedBlock().equals(Optional.of(pos));
    }
}
