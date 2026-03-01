package dimasik.modules.misc;

import dimasik.Load;
import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.utils.client.ChatUtils;
import dimasik.utils.time.TimerUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;

public class LeaveTracker
extends Module {
    private final TimerUtils timerUtil = new TimerUtils();
    private final EventListener<EventUpdate> update = this::update;

    public LeaveTracker() {
        super("LeaveTracker", Category.MISC);
    }

    public void update(EventUpdate e) {
        if (LeaveTracker.mc.player != null && LeaveTracker.mc.world != null) {
            for (LivingEntity livingEntity : LeaveTracker.mc.world.getPlayers()) {
                if (Load.getInstance().getHooks().getFriendManagers().is(livingEntity.getName().getString()) || livingEntity.getName().getString().equals("Friend") || !(livingEntity instanceof PlayerEntity) || !(Math.sqrt(Math.pow(livingEntity.getPosX() - LeaveTracker.mc.player.getPosX(), 2.0) + Math.pow(livingEntity.getPosZ() - LeaveTracker.mc.player.getPosZ(), 2.0)) > 500.0) || LeaveTracker.mc.player.ticksExisted % 11 != 0) continue;
                int x = (int)livingEntity.getPosX();
                int y = (int)livingEntity.getPosY();
                int z = (int)livingEntity.getPosZ();
                TextComponent displayName = (TextComponent)livingEntity.getDisplayName();
                displayName.append(ITextComponent.getTextComponentOrEmpty(String.valueOf((Object)TextFormatting.WHITE) + "\u041b\u0438\u0432\u043d\u0443\u043b \u043d\u0430: " + x + " " + y + " " + z));
                ChatUtils.addClientMessage(displayName.getString());
                this.timerUtil.reset();
            }
        }
    }
}
