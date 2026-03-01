package dimasik.modules.player;

import dimasik.Load;
import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.block.EventCollision;
import dimasik.events.main.packet.EventSendPacket;
import dimasik.events.main.player.EventSwimming;
import dimasik.events.main.player.EventSync;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.SelectOption;
import dimasik.managers.module.option.main.SelectOptionValue;
import dimasik.managers.module.option.main.SliderOption;
import dimasik.utils.player.MoveUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.block.AbstractBlock;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.potion.Effects;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class Noclip
extends Module {
    private final List<IPacket<?>> bufferedPackets = new ArrayList();
    private final SliderOption semiPackets = new SliderOption("Packet", 2.0f, 1.0f, 15.0f).increment(1.0f);
    private final SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("Phase"), new SelectOptionValue("Block"));
    private final SelectOption releaseMode = new SelectOption("Mode", 0, new SelectOptionValue("Old"), new SelectOptionValue("None")).visible(() -> this.mode.getSelected("Phase"));
    private final SelectOption speedSettings = new SelectOption("Speed Settings", 0, new SelectOptionValue("Slow"), new SelectOptionValue("Fast")).visible(() -> this.releaseMode.getSelected("Old") && this.mode.getSelected("Block"));
    private final SliderOption speed = new SliderOption("Speed", 0.2f, 0.01f, 1.0f).increment(0.05f).visible(() -> this.speedSettings.getSelected("Slow") && this.releaseMode.getSelected("Old") && this.mode.getSelected("Phase"));
    private final EventListener<EventUpdate> update = this::update;
    private final EventListener<EventSendPacket> gei = this::onEvent;
    private final EventListener<EventSync> sync = this::sync;
    private final EventListener<EventSwimming> swim = this::swim;
    private final EventListener<EventCollision> collision = this::collision;
    private boolean semiPacketSent;
    private boolean skipReleaseOnDisable;

    public Noclip() {
        super("Noclip", Category.PLAYER);
        this.settings(this.mode, this.releaseMode, this.speedSettings, this.semiPackets);
    }

    public void sync(EventSync event) {
        if (this.mode.getSelected("Block") && Noclip.mc.gameSettings.keyBindSneak.isKeyDown()) {
            event.setPitch(90.0f);
            Noclip.mc.player.rotationPitchHead = 90.0f;
        } else if (this.mode.getSelected("Phase") && this.releaseMode.getSelected("Old") && this.speedSettings.getSelected("Slow")) {
            MoveUtils.setMotion(0.001f);
        }
    }

    public void swim(EventSwimming event) {
        if (this.mode.getSelected("Block") && Noclip.mc.gameSettings.keyBindSneak.isKeyDown()) {
            event.setPitch(90.0f);
        }
    }

    public void onEvent(EventSendPacket eventPacket) {
        if (Noclip.mc.player == null || Noclip.mc.player.connection == null || this.mode.getSelected("Block")) {
            return;
        }
        IPacket packet = eventPacket.getPacket();
        if (packet instanceof CPlayerPacket) {
            this.bufferedPackets.add(packet);
            eventPacket.setCancelled(true);
        }
    }

    public void update(EventUpdate eventUpdate) {
        boolean semiInsideBlock;
        if (Load.getInstance().getHooks().getModuleManagers().getNoClip().isToggled() && Noclip.mc.player.removePotionEffect(Effects.SPEED)) {
            MoveUtils.setMotion(0.001f);
        }
        if (Noclip.mc.player == null || Noclip.mc.world == null) {
            return;
        }
        if (this.mode.getSelected("Block") && Noclip.mc.gameSettings.keyBindSneak.isKeyDown()) {
            Noclip.mc.playerController.onPlayerDamageBlock(new BlockPos(Noclip.mc.player.getPosX(), Noclip.mc.player.getPosY() - 1.0, Noclip.mc.player.getPosZ()), Noclip.mc.player.getHorizontalFacing());
            Noclip.mc.player.swingArm(Hand.MAIN_HAND);
        }
        if (!this.releaseMode.getSelected("Old") || this.mode.getSelected("Block")) {
            return;
        }
        boolean noSolidInAABB = Noclip.mc.world.getStatesInArea(Noclip.mc.player.getBoundingBox().shrink(0.001)).noneMatch(AbstractBlock.AbstractBlockState::isSolid);
        long totalStates = Noclip.mc.world.getStatesInArea(Noclip.mc.player.getBoundingBox().shrink(0.001)).count();
        long solidStates = Noclip.mc.world.getStatesInArea(Noclip.mc.player.getBoundingBox().shrink(0.001)).filter(AbstractBlock.AbstractBlockState::isSolid).count();
        boolean bl = semiInsideBlock = solidStates > 0L && solidStates < totalStates;
        if (!this.semiPacketSent && semiInsideBlock) {
            double x = Noclip.mc.player.getPosX();
            double y = Noclip.mc.player.getPosY();
            double z = Noclip.mc.player.getPosZ();
            float yaw = Noclip.mc.player.rotationYaw;
            float pitch = Noclip.mc.player.rotationPitch;
            boolean onGround = Noclip.mc.player.isOnGround();
            int i = 0;
            while ((float)i < ((Float)this.semiPackets.getValue()).floatValue()) {
                Noclip.mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(x, y, z, yaw, pitch, onGround));
                ++i;
            }
            this.semiPacketSent = true;
            return;
        }
        if (this.semiPacketSent && noSolidInAABB) {
            this.skipReleaseOnDisable = true;
            this.toggle();
        }
    }

    public void collision(EventCollision event) {
        if (this.mode.getSelected("Phase") && this.releaseMode.getSelected("Old") && ((double)event.getBlockPos().getY() >= Noclip.mc.player.getPosY() || Noclip.mc.gameSettings.keyBindSneak.isKeyDown())) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onDisabled() {
        if (this.mode.getSelected("Phase")) {
            if (!this.skipReleaseOnDisable && this.semiPacketSent) {
                if (!this.releaseMode.getSelected("None")) {
                    this.runReleaseSequence(this.releaseMode.getSettingName());
                } else {
                    Noclip.mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(Noclip.mc.player.getPosX(), Noclip.mc.player.getPosY(), Noclip.mc.player.getPosZ(), Noclip.mc.player.rotationYaw, Noclip.mc.player.rotationPitch, Noclip.mc.player.isOnGround()));
                }
            }
            if (Noclip.mc.player != null && Noclip.mc.player.connection != null && !this.bufferedPackets.isEmpty()) {
                for (IPacket<?> packet : this.bufferedPackets) {
                    Noclip.mc.player.connection.sendPacketWithoutEvent(packet);
                }
                this.bufferedPackets.clear();
            }
        }
    }

    @Override
    public void onEnabled() {
        if (this.mode.getSelected("Phase")) {
            this.bufferedPackets.clear();
            this.semiPacketSent = false;
            this.skipReleaseOnDisable = false;
        }
    }

    private void runReleaseSequence(String mode) {
        if (Noclip.mc.player == null || Noclip.mc.player.connection == null) {
            return;
        }
        double x = Noclip.mc.player.getPosX();
        double y = Noclip.mc.player.getPosY();
        double z = Noclip.mc.player.getPosZ();
        float yaw = Noclip.mc.player.rotationYaw;
        float pitch = Noclip.mc.player.rotationPitch;
        switch (mode.toLowerCase()) {
            case "simple": {
                Noclip.mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(x - 5000.0, y, z - 5000.0, yaw, pitch, false));
                Noclip.mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(x, y, z, yaw, pitch, Noclip.mc.player.isOnGround()));
                break;
            }
            case "double": {
                Noclip.mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(x - 5000.0, y, z - 5000.0, yaw, pitch, false));
                Noclip.mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(x + 5000.0, y, z + 5000.0, yaw, pitch, false));
                Noclip.mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(x, y, z, yaw, pitch, Noclip.mc.player.isOnGround()));
                break;
            }
            case "old": {
                Noclip.mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(x, y + 0.0625, z, yaw, pitch, false));
                Noclip.mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(x, y, z, yaw, pitch, false));
                Noclip.mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(x, y + 0.03125, z, yaw, pitch, true));
                Noclip.mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(x, y, z, yaw, pitch, Noclip.mc.player.isOnGround()));
                break;
            }
        }
    }

    @Generated
    public SelectOption getReleaseMode() {
        return this.releaseMode;
    }
}
