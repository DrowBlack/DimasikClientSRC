package dimasik.modules.movement;

import dimasik.Load;
import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.utils.player.MoveUtils;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import ru.dreamix.class2native.experimental.AdditionalReferenceProtection;
import ru.dreamix.class2native.experimental.AdditionalReferenceProtectionType;

@AdditionalReferenceProtection(value=AdditionalReferenceProtectionType.GENERATIVE_CONFUSION)
public class NoWeb
extends Module {
    private final EventListener<EventUpdate> update = this::update;

    public NoWeb() {
        super("NoWeb", Category.MOVEMENT);
    }

    public void update(EventUpdate eventUpdate) {
        if (!NoWeb.mc.player.isSneaking() || !NoWeb.mc.player.isOnGround()) {
            BlockPos aboveHeadPos;
            double z;
            double x;
            boolean headInWeb = false;
            boolean feetInWeb = false;
            for (x = -0.295; x <= 0.295; x += 0.05) {
                block1: for (z = -0.295; z <= 0.295; z += 0.05) {
                    for (double y = (double)NoWeb.mc.player.getEyeHeight(); y >= 0.0; y -= 0.1) {
                        BlockPos headPos = new BlockPos(NoWeb.mc.player.getPosX() + x, NoWeb.mc.player.getPosY() + y, NoWeb.mc.player.getPosZ() + z);
                        if (NoWeb.mc.world.getBlockState(headPos).getBlock() != Blocks.COBWEB) continue;
                        headInWeb = true;
                        continue block1;
                    }
                }
            }
            if (!headInWeb) {
                block3: for (x = -0.295; x <= 0.295; x += 0.05) {
                    for (z = -0.295; z <= 0.295; z += 0.05) {
                        BlockPos pos = new BlockPos(NoWeb.mc.player.getPosX() + x, NoWeb.mc.player.getPosY(), NoWeb.mc.player.getPosZ() + z);
                        if (NoWeb.mc.world.getBlockState(pos).getBlock() != Blocks.COBWEB) continue;
                        feetInWeb = true;
                        continue block3;
                    }
                }
            }
            if (!headInWeb && !feetInWeb && NoWeb.mc.world.getBlockState(aboveHeadPos = new BlockPos(NoWeb.mc.player.getPosX(), NoWeb.mc.player.getPosY() + (double)NoWeb.mc.player.getEyeHeight() + (double)0.2f, NoWeb.mc.player.getPosZ())).getBlock() == Blocks.COBWEB) {
                headInWeb = true;
            }
            if (!Load.getInstance().getHooks().getModuleManagers().getFreeCam().isToggled() && (headInWeb || feetInWeb)) {
                if (NoWeb.mc.gameSettings.keyBindJump.isKeyDown()) {
                    NoWeb.mc.player.setMotion(new Vector3d(0.0, 0.8000000476837158, 0.0));
                }
                if (!NoWeb.mc.gameSettings.keyBindJump.isKeyDown()) {
                    NoWeb.mc.player.setMotion(new Vector3d(0.0, 0.0, 0.0));
                }
                if (NoWeb.mc.gameSettings.keyBindSneak.isKeyDown()) {
                    NoWeb.mc.player.setMotion(new Vector3d(0.0, -0.8000000476837158, 0.0));
                }
                MoveUtils.setMotion(0.21);
            }
        }
    }
}
