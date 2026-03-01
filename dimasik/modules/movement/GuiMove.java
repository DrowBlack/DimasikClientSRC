package dimasik.modules.movement;

import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.packet.EventSendPacket;
import dimasik.events.main.player.EventCloseScreen;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.CheckboxOption;
import dimasik.utils.player.MoveUtils;
import dimasik.utils.time.TimerUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.EditSignScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CClickWindowPacket;

public class GuiMove
extends Module {
    public boolean update = true;
    private final List<IPacket<?>> packet = new ArrayList();
    private final CheckboxOption lox1 = new CheckboxOption("\u041e\u0431\u0445\u043e\u0434 \u0413\u0440\u0438\u043c", false);
    TimerUtils timerUtility = new TimerUtils();
    private final EventListener<EventUpdate> updates = this::update;
    private final EventListener<EventSendPacket> packetes = this::packet;
    private final EventListener<EventCloseScreen> lox = this::onclos;

    public GuiMove() {
        super("GuiMove", Category.MOVEMENT);
        this.settings(this.lox1);
    }

    public void update(EventUpdate eventUpdate) {
        if (GuiMove.mc.player != null && this.update) {
            KeyBinding[] pressedKeys = new KeyBinding[]{GuiMove.mc.gameSettings.keyBindForward, GuiMove.mc.gameSettings.keyBindBack, GuiMove.mc.gameSettings.keyBindLeft, GuiMove.mc.gameSettings.keyBindRight, GuiMove.mc.gameSettings.keyBindJump};
            if (((Boolean)this.lox1.getValue()).booleanValue() && !this.timerUtility.isReached(100L)) {
                for (KeyBinding keyBinding : pressedKeys) {
                    keyBinding.setPressed(false);
                }
                return;
            }
            if (GuiMove.mc.currentScreen instanceof ChatScreen || GuiMove.mc.currentScreen instanceof EditSignScreen) {
                return;
            }
            this.updateKeyBindingState(pressedKeys);
        }
    }

    public void packet(EventSendPacket eventUpdate) {
        IPacket iPacket;
        if (((Boolean)this.lox1.getValue()).booleanValue() && this.update && (iPacket = eventUpdate.getPacket()) instanceof CClickWindowPacket) {
            CClickWindowPacket p = (CClickWindowPacket)iPacket;
            if (MoveUtils.isMoving() && GuiMove.mc.currentScreen instanceof InventoryScreen) {
                this.packet.add(p);
                eventUpdate.setCancelled(true);
            }
        }
    }

    private void updateKeyBindingState(KeyBinding[] keyBindings) {
        for (KeyBinding keyBinding : keyBindings) {
            boolean isKeyPressed = InputMappings.isKeyDown(mc.getMainWindow().getHandle(), keyBinding.getDefault().getKeyCode());
            keyBinding.setPressed(isKeyPressed);
        }
    }

    public void onclos(EventCloseScreen eventCloseScreen) {
        if (((Boolean)this.lox1.getValue()).booleanValue() && GuiMove.mc.currentScreen instanceof InventoryScreen && !this.packet.isEmpty() && MoveUtils.isMoving()) {
            new Thread(() -> {
                this.timerUtility.reset();
                try {
                    Thread.sleep(50L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                for (IPacket<?> p : this.packet) {
                    GuiMove.mc.player.connection.sendPacket(p);
                }
                this.packet.clear();
            }).start();
            eventCloseScreen.setCancelled(true);
        }
    }
}
