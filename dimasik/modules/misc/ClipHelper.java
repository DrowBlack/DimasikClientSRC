package dimasik.modules.misc;

import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.input.EventInput;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.BindOption;
import java.util.Optional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;

public class ClipHelper
extends Module {
    private final BindOption clip = new BindOption("Tp Key", -1);
    public static boolean throughBlocks;
    public static PlayerEntity target;
    private final EventListener<EventInput> update = this::bind;
    private final EventListener<EventUpdate> update2 = this::update;

    public ClipHelper() {
        super("ClipHelper (beta)", Category.MISC);
        this.settings(this.clip);
    }

    public void update(EventUpdate eventUpdate) {
        double reach = 50.0;
        Vector3d eyes = ClipHelper.mc.player.getEyePosition(1.0f);
        Vector3d look = ClipHelper.mc.player.getLookVec();
        Vector3d end = eyes.add(look.x * reach, look.y * reach, look.z * reach);
        double minDist = Double.MAX_VALUE;
        for (PlayerEntity playerEntity : ClipHelper.mc.world.getPlayers()) {
            double dist;
            AxisAlignedBB bb;
            Optional<Vector3d> res;
            if (playerEntity == ClipHelper.mc.player || !(res = (bb = playerEntity.getBoundingBox().grow(0.3)).rayTrace(eyes, end)).isPresent() || !((dist = eyes.distanceTo(res.get())) < minDist)) continue;
            minDist = dist;
            target = playerEntity;
        }
        if (target == null) {
            return;
        }
        BlockRayTraceResult blockRes = ClipHelper.mc.world.rayTraceBlocks(new RayTraceContext(eyes, target.getPositionVec().add(0.0, (double)target.getHeight() / 2.0, 0.0), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, ClipHelper.mc.player));
        throughBlocks = blockRes != null && blockRes.getPos().distanceSq(target.getPosition()) > 1.0;
    }

    public void bind(EventInput eventInput) {
        if (this.clip.getKey() == eventInput.getKey()) {
            double reach = 50.0;
            Vector3d eyes = ClipHelper.mc.player.getEyePosition(1.0f);
            Vector3d look = ClipHelper.mc.player.getLookVec();
            Vector3d end = eyes.add(look.x * reach, look.y * reach, look.z * reach);
            PlayerEntity aimedPlayer = null;
            double minDist = Double.MAX_VALUE;
            for (PlayerEntity playerEntity : ClipHelper.mc.world.getPlayers()) {
                double dist;
                AxisAlignedBB bb;
                Optional<Vector3d> res;
                if (playerEntity == ClipHelper.mc.player || !(res = (bb = playerEntity.getBoundingBox().grow(0.3)).rayTrace(eyes, end)).isPresent() || !((dist = eyes.distanceTo(res.get())) < minDist)) continue;
                minDist = dist;
                aimedPlayer = playerEntity;
            }
            if (aimedPlayer == null) {
                return;
            }
            ClipHelper.mc.player.sendChatMessage(".vclip down");
        }
    }

    static {
        target = null;
    }
}
