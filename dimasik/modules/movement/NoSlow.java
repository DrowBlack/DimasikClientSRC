package dimasik.modules.movement;

import dimasik.events.api.EventListener;
import dimasik.events.main.EventTick;
import dimasik.events.main.movement.EventNoSlow;
import dimasik.helpers.module.swap.SwapHelpers;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.SelectOption;
import dimasik.managers.module.option.main.SelectOptionValue;
import dimasik.managers.module.option.main.SliderOption;
import dimasik.utils.player.MoveUtils;
import dimasik.utils.time.TimerUtils;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;

public class NoSlow
extends Module {
    private final SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("ReallyWorld"), new SelectOptionValue("Vanilla"), new SelectOptionValue("Grim"), new SelectOptionValue("Matrix"), new SelectOptionValue("Test"));
    private final SliderOption speed = new SliderOption("Speed", 1.0f, 0.1f, 1.0f).increment(0.05f).visible(() -> this.mode.getSelected("Vanilla"));
    private final SwapHelpers swap = new SwapHelpers();
    private int ticks = 0;
    private int tiks = 0;
    private final EventListener<EventNoSlow> noSlow = this::noSlow;
    private final EventListener<EventTick> update = this::update;
    TimerUtils stopWatch = new TimerUtils();

    public NoSlow() {
        super("NoSlow", Category.MOVEMENT);
        this.settings(this.mode, this.speed);
    }

    public void noSlow(EventNoSlow event) {
        switch (((SelectOptionValue)this.mode.getValue()).getName()) {
            case "ReallyWorld": {
                this.reallyWorldBow(event);
                break;
            }
            case "Vanilla": {
                this.vanilla(event);
                break;
            }
            case "Grim": {
                this.grim(event);
                break;
            }
            case "Test": {
                this.spokky(event);
                break;
            }
            case "Matrix": {
                this.matrix(event);
            }
        }
    }

    public void update(EventTick e) {
        if (NoSlow.mc.player != null && NoSlow.mc.player.isHandActive()) {
            ++this.ticks;
            ++this.tiks;
        } else {
            this.ticks = 0;
            this.tiks = 0;
        }
    }

    private void reallyWorld(EventNoSlow event) {
        if ((NoSlow.mc.player.getItemInUseCount() < 25 || NoSlow.mc.player.getHeldItemOffhand().getItem() == Items.SHIELD && NoSlow.mc.player.getItemInUseCount() < 71993) && NoSlow.mc.player.getActiveHand() == Hand.OFF_HAND) {
            int old = NoSlow.mc.player.inventory.currentItem;
            NoSlow.mc.player.connection.sendPacket(new CHeldItemChangePacket(old + 1 > 8 ? old - 1 : old + 1));
            NoSlow.mc.player.connection.sendPacket(new CHeldItemChangePacket(NoSlow.mc.player.inventory.currentItem));
            event.setCancelled(true);
        }
    }

    private void vanilla(EventNoSlow event) {
        event.setSpeed(((Float)this.speed.getValue()).floatValue());
    }

    private void grim(EventNoSlow event) {
        if (NoSlow.mc.player.getHeldItemOffhand().getUseAction() == UseAction.BLOCK && NoSlow.mc.player.getActiveHand() == Hand.MAIN_HAND || NoSlow.mc.player.getHeldItemOffhand().getUseAction() == UseAction.EAT && NoSlow.mc.player.getActiveHand() == Hand.MAIN_HAND) {
            return;
        }
        if (NoSlow.mc.player.getActiveHand() == Hand.MAIN_HAND) {
            NoSlow.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.OFF_HAND));
            event.setCancelled(true);
            return;
        }
        event.setCancelled(true);
        this.sendItemChangePacket();
    }

    private void matrix(EventNoSlow event) {
        boolean isFalling = (double)NoSlow.mc.player.fallDistance > 0.725;
        event.setCancelled(true);
        if (NoSlow.mc.player.isOnGround() && !NoSlow.mc.player.movementInput.jump) {
            if (NoSlow.mc.player.ticksExisted % 2 == 0) {
                boolean isNotStrafing = NoSlow.mc.player.moveStrafing == 0.0f;
                float speedMultiplier = isNotStrafing ? 0.5f : 0.4f;
                NoSlow.mc.player.getMotion().x *= (double)speedMultiplier;
                NoSlow.mc.player.getMotion().z *= (double)speedMultiplier;
            }
        } else if (isFalling) {
            boolean isVeryFastFalling = (double)NoSlow.mc.player.fallDistance > 1.4;
            float speedMultiplier = isVeryFastFalling ? 0.95f : 0.97f;
            NoSlow.mc.player.getMotion().x *= (double)speedMultiplier;
            NoSlow.mc.player.getMotion().z *= (double)speedMultiplier;
        }
    }

    private void spokky(EventNoSlow event) {
        if (this.stopWatch.hasReached(125.0)) {
            event.setCancelled(true);
            NoSlow.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            this.stopWatch.reset();
        }
    }

    private void sendItemChangePacket() {
        if (MoveUtils.isMoving()) {
            NoSlow.mc.player.connection.sendPacket(new CHeldItemChangePacket(NoSlow.mc.player.inventory.currentItem % 8 + 1));
            NoSlow.mc.player.connection.sendPacket(new CHeldItemChangePacket(NoSlow.mc.player.inventory.currentItem));
        }
    }

    private void reallyWorldBow(EventNoSlow event) {
        int bowSlot;
        if (NoSlow.mc.player.getActiveHand() == Hand.OFF_HAND && (bowSlot = this.swap.find(Items.BOW)) != -1 && this.swap.haveHotBar(bowSlot)) {
            int cur;
            int oldSlot = NoSlow.mc.player.inventory.currentItem;
            int n = cur = NoSlow.mc.player.inventory.currentItem + 1 > 8 ? NoSlow.mc.player.inventory.currentItem - 1 : NoSlow.mc.player.inventory.currentItem + 1;
            if (NoSlow.mc.player.getItemInUseCount() > 25 && NoSlow.mc.player.getHeldItemOffhand().getItem() != Items.SHIELD || NoSlow.mc.player.getItemInUseCount() > 71993) {
                if (bowSlot % 9 != NoSlow.mc.player.inventory.currentItem) {
                    NoSlow.mc.player.connection.sendPacket(new CHeldItemChangePacket(bowSlot % 9));
                }
                NoSlow.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                if (bowSlot % 9 != NoSlow.mc.player.inventory.currentItem) {
                    NoSlow.mc.player.connection.sendPacket(new CHeldItemChangePacket(NoSlow.mc.player.inventory.currentItem));
                }
            }
            event.setCancelled(true);
        }
    }
}
