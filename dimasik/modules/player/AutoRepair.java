package dimasik.modules.player;

import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.managers.client.ClientManagers;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.utils.client.StringUtils;
import dimasik.utils.time.TimerUtils;
import net.minecraft.item.ItemStack;

public class AutoRepair
extends Module {
    private TimerUtils timerUtils = new TimerUtils();
    private final EventListener<EventUpdate> update = this::update;

    public AutoRepair() {
        super("AutoRepair", Category.PLAYER);
    }

    public void update(EventUpdate event) {
        if (StringUtils.priority(StringUtils.getDonate(AutoRepair.mc.player)) >= 4) {
            ItemStack helmet = AutoRepair.mc.player.inventory.armorInventory.get(3);
            ItemStack chest = AutoRepair.mc.player.inventory.armorInventory.get(2);
            ItemStack legs = AutoRepair.mc.player.inventory.armorInventory.get(1);
            ItemStack boots = AutoRepair.mc.player.inventory.armorInventory.get(0);
            if (!(helmet.isEmpty() && chest.isEmpty() && legs.isEmpty() && boots.isEmpty() || helmet.getDamage() >= helmet.getMaxDamage() - helmet.getDamage() && chest.getDamage() >= chest.getMaxDamage() - chest.getDamage() && legs.getDamage() >= legs.getMaxDamage() - legs.getDamage() && boots.getDamage() >= boots.getMaxDamage() - boots.getDamage() && ClientManagers.isPvpMode() || !this.timerUtils.hasTimeElapsed(500L))) {
                AutoRepair.mc.player.sendChatMessage("/repair all");
                this.timerUtils.reset();
            }
        }
    }
}
