package dimasik.utils.player;

import com.mojang.authlib.GameProfile;
import dimasik.helpers.interfaces.IFastAccess;
import java.util.UUID;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.util.MovementInputFromOptions;

public class CameraUtils
extends ClientPlayerEntity
implements IFastAccess {
    private boolean isFlying;
    private static final ClientPlayNetHandler NETWORK_HANDLER = new ClientPlayNetHandler(IFastAccess.mc, IFastAccess.mc.currentScreen, IFastAccess.mc.getConnection().getNetworkManager(), new GameProfile(UUID.randomUUID(), "fakeplayer")){

        @Override
        public void sendPacket(IPacket<?> packetIn) {
            super.sendPacket(packetIn);
        }
    };

    public CameraUtils(int i) {
        super(IFastAccess.mc, IFastAccess.mc.world, NETWORK_HANDLER, IFastAccess.mc.player.getStats(), IFastAccess.mc.player.getRecipeBook(), false, false);
        this.setEntityId(i);
        this.movementInput = new MovementInputFromOptions(IFastAccess.mc.gameSettings);
    }

    public void spawn() {
        if (this.world != null) {
            this.world.addEntity(this);
        }
    }

    @Override
    public void livingTick() {
        super.livingTick();
    }

    @Override
    public void rotateTowards(double yaw, double pitch) {
        super.rotateTowards(yaw, pitch);
    }
}
