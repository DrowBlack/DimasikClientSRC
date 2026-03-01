package dimasik.itemics.utils.player;

import dimasik.itemics.api.ItemicsAPI;
import dimasik.itemics.api.cache.IWorldData;
import dimasik.itemics.api.utils.Helper;
import dimasik.itemics.api.utils.IPlayerContext;
import dimasik.itemics.api.utils.IPlayerController;
import dimasik.itemics.api.utils.RayTraceUtils;
import dimasik.itemics.utils.player.PrimaryPlayerController;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public enum PrimaryPlayerContext implements IPlayerContext,
Helper
{
    INSTANCE;


    @Override
    public ClientPlayerEntity player() {
        return PrimaryPlayerContext.mc.player;
    }

    @Override
    public IPlayerController playerController() {
        return PrimaryPlayerController.INSTANCE;
    }

    @Override
    public World world() {
        return PrimaryPlayerContext.mc.world;
    }

    @Override
    public IWorldData worldData() {
        return ItemicsAPI.getProvider().getPrimaryItemics().getWorldProvider().getCurrentWorld();
    }

    @Override
    public RayTraceResult objectMouseOver() {
        return RayTraceUtils.rayTraceTowards(this.player(), this.playerRotations(), this.playerController().getBlockReachDistance());
    }
}
