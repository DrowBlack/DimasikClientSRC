package dimasik.modules.player;

import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.packet.EventReceivePacket;
import dimasik.events.main.packet.EventSendPacket;
import dimasik.events.main.player.EventLivingUpdate;
import dimasik.events.main.player.EventSync;
import dimasik.events.main.render.EventRender2D;
import dimasik.helpers.render.ColorHelpers;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.CheckboxOption;
import dimasik.managers.module.option.main.SliderOption;
import dimasik.utils.player.CameraUtils;
import dimasik.utils.player.MoveUtils;
import net.minecraft.item.BlockItem;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.network.play.client.CUseEntityPacket;
import net.minecraft.network.play.server.SJoinGamePacket;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.math.vector.Vector3d;

public class FreeCam
extends Module {
    private final SliderOption speed = new SliderOption("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u043f\u043e XZ", 1.0f, 0.1f, 5.0f).increment(0.05f);
    private final CheckboxOption gamma = new CheckboxOption("Gamma", false);
    private Vector3d clientPosition = null;
    public CameraUtils player = null;
    boolean oldIsFlying;
    double gammasave = 0.0;
    private final EventListener<EventSendPacket> send = this::onPacket;
    private final EventListener<EventReceivePacket> receive = this::lox;
    private final EventListener<EventLivingUpdate> update = this::EventLiving;
    private final EventListener<EventRender2D.Pre> render2d = this::render;
    private final EventListener<EventSync> sync = this::onMotion;
    private final EventListener<EventUpdate> livingUpdate = this::handleLivingUpdate;

    public FreeCam() {
        super("FreeCam", Category.MOVEMENT);
        this.settings(this.speed, this.gamma);
    }

    private void onPacket(EventSendPacket e) {
        IPacket iPacket;
        if (FreeCam.mc.world == null || FreeCam.mc.player == null || !FreeCam.mc.player.isAlive()) {
            this.toggle();
        }
        if (this.player != null || !FreeCam.mc.player.isAlive()) {
            if (e.getPacket() instanceof CUseEntityPacket && ((CUseEntityPacket)e.getPacket()).getEntityFromWorld(FreeCam.mc.world).getEntityId() == FreeCam.mc.player.getEntityId()) {
                e.setCancelled(true);
            }
            if (e.getPacket() instanceof CPlayerTryUseItemOnBlockPacket && !(FreeCam.mc.player.inventory.getCurrentItem().getItem() instanceof BlockItem)) {
                e.setCancelled(true);
            }
        }
        if ((iPacket = e.getPacket()) instanceof CPlayerPacket) {
            CPlayerPacket p = (CPlayerPacket)iPacket;
            if (this.player != null || !FreeCam.mc.player.isAlive()) {
                if (p.isMoving()) {
                    p.setX(this.player.getPosX());
                    p.setY(this.player.getPosY());
                    p.setZ(this.player.getPosZ());
                }
                p.setOnGround(this.player.isOnGround());
                if (p.isRotating()) {
                    p.setYaw(this.player.rotationYaw);
                    p.setPitch(this.player.rotationPitch);
                }
            }
            if (this.player == null) {
                this.toggle();
            }
        }
    }

    public void lox(EventReceivePacket e) {
        if (e.getPacket() instanceof SJoinGamePacket) {
            this.toggle();
        }
    }

    private void EventLiving(EventLivingUpdate livingUpdateEvent) {
        if (FreeCam.mc.world == null || FreeCam.mc.player == null || !FreeCam.mc.player.isAlive()) {
            this.toggle();
        }
        if (this.player != null) {
            this.player.noClip = true;
            this.player.setOnGround(false);
            FreeCam.mc.player.motion = Vector3d.ZERO;
            if (FreeCam.mc.gameSettings.keyBindJump.isKeyDown()) {
                this.player.setMotion(new Vector3d(0.0, ((Float)this.speed.getValue()).floatValue(), 0.0));
            }
            if (!FreeCam.mc.gameSettings.keyBindJump.isKeyDown()) {
                this.player.setMotion(new Vector3d(0.0, 0.0, 0.0));
            }
            if (FreeCam.mc.gameSettings.keyBindSneak.isKeyDown()) {
                this.player.setMotion(new Vector3d(0.0, -((Float)this.speed.getValue()).floatValue(), 0.0));
            }
            MoveUtils.setMotion(((Float)this.speed.getValue()).floatValue(), this.player);
            this.player.abilities.isFlying = true;
        }
    }

    public void render(EventRender2D.Pre event) {
        suisse_intl.drawCenteredText(event.getMatrixStack(), "Vclip: " + (int)(this.player.getPosY() - FreeCam.mc.player.getPosY()), (float)mc.getMainWindow().getWidth() / 2.0f, (float)mc.getMainWindow().getHeight() / 2.0f - 30.0f, ColorHelpers.rgba(255, 255, 255, 255), 16.0f);
    }

    private void onMotion(EventSync eventSync) {
        FreeCam.mc.player.motion = Vector3d.ZERO;
        eventSync.setCancelled(true);
    }

    @Override
    public void onEnabled() {
        if (((Boolean)this.gamma.getValue()).booleanValue()) {
            this.gammasave = FreeCam.mc.gameSettings.gamma;
        }
        if (FreeCam.mc.player != null && FreeCam.mc.world != null) {
            FreeCam.mc.player.setJumping(false);
            this.initializeFakePlayer();
            this.addFakePlayer();
            this.player.spawn();
            FreeCam.mc.player.movementInput = new MovementInput();
            FreeCam.mc.player.moveForward = 0.0f;
            FreeCam.mc.player.moveStrafing = 0.0f;
            mc.setRenderViewEntity(this.player);
        }
    }

    @Override
    public void onDisabled() {
        if (((Boolean)this.gamma.getValue()).booleanValue()) {
            FreeCam.mc.gameSettings.gamma = this.gammasave;
        }
        if (FreeCam.mc.player != null) {
            this.removeFakePlayer();
            mc.setRenderViewEntity(null);
            FreeCam.mc.player.movementInput = new MovementInputFromOptions(FreeCam.mc.gameSettings);
        }
    }

    private void handleLivingUpdate(EventUpdate eventUpdate) {
        if (((Boolean)this.gamma.getValue()).booleanValue()) {
            FreeCam.mc.gameSettings.gamma = 1000.0;
        }
        this.player.noClip = true;
        this.player.setOnGround(false);
        MoveUtils.setMotion(((Float)this.speed.getValue()).floatValue(), this.player);
        this.oldIsFlying = this.player.abilities.isFlying;
        this.player.abilities.isFlying = true;
    }

    private void initializeFakePlayer() {
        this.clientPosition = FreeCam.mc.player.getPositionVec();
        this.player = new CameraUtils(1337);
        this.player.copyLocationAndAnglesFrom(FreeCam.mc.player);
        this.player.rotationYawHead = FreeCam.mc.player.rotationYawHead;
        this.player.rotationPitchHead = FreeCam.mc.player.rotationPitchHead;
    }

    private void addFakePlayer() {
        this.clientPosition = FreeCam.mc.player.getPositionVec();
        FreeCam.mc.world.addEntity(1337, this.player);
    }

    private void removeFakePlayer() {
        this.resetFlying();
        FreeCam.mc.world.removeEntityFromWorld(1337);
        this.player = null;
        this.clientPosition = null;
    }

    private void resetFlying() {
        if (this.oldIsFlying) {
            FreeCam.mc.player.abilities.isFlying = false;
            this.oldIsFlying = false;
        }
    }
}
