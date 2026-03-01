package dimasik.modules.combat;

import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.CheckboxOption;
import dimasik.managers.module.option.main.SliderOption;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;

public class HitBox
extends Module {
    public final SliderOption size = new SliderOption("Size", 0.2f, 0.0f, 3.0f).increment(0.1f);
    public final CheckboxOption visible = new CheckboxOption("Visible", false);
    private final EventListener<EventUpdate> update = this::update;

    public HitBox() {
        super("HitBox", Category.COMBAT);
        this.settings(this.size, this.visible);
    }

    public void update(EventUpdate e) {
        if (!((Boolean)this.visible.getValue()).booleanValue() || HitBox.mc.player == null) {
            return;
        }
        float sizeMultiplier = ((Float)this.size.getValue()).floatValue() * 2.5f;
        for (PlayerEntity playerEntity : HitBox.mc.world.getPlayers()) {
            if (this.isNotValid(playerEntity)) continue;
            playerEntity.setBoundingBox(this.calculateBoundingBox(playerEntity, sizeMultiplier));
        }
    }

    private boolean isNotValid(PlayerEntity player) {
        return player == HitBox.mc.player || !player.isAlive();
    }

    private AxisAlignedBB calculateBoundingBox(Entity entity, float size) {
        double minX = entity.getPosX() - (double)size;
        double minY = entity.getBoundingBox().minY;
        double minZ = entity.getPosZ() - (double)size;
        double maxX = entity.getPosX() + (double)size;
        double maxY = entity.getBoundingBox().maxY;
        double maxZ = entity.getPosZ() + (double)size;
        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
