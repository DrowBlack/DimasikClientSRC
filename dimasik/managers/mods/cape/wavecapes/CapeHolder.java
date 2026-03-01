package dimasik.managers.mods.cape.wavecapes;

import dimasik.managers.mods.cape.wavecapes.math.Vector2;
import dimasik.managers.mods.cape.wavecapes.math.Vector3;
import dimasik.managers.mods.cape.wavecapes.sim.StickSimulation;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.util.math.MathHelper;

public interface CapeHolder {
    public StickSimulation getSimulation();

    default public void updateSimulation(AbstractClientPlayerEntity abstractClientPlayer, int partCount) {
        StickSimulation simulation = this.getSimulation();
        if (simulation == null) {
            return;
        }
        boolean dirty = simulation.init(partCount);
        if (dirty) {
            simulation.applyMovement(new Vector3(1.0f, 1.0f, 0.0f));
            for (int i = 0; i < 5; ++i) {
                this.simulate(abstractClientPlayer);
            }
        }
    }

    default public void simulate(AbstractClientPlayerEntity abstractClientPlayer) {
        StickSimulation simulation = this.getSimulation();
        if (simulation == null || simulation.empty()) {
            return;
        }
        double d = abstractClientPlayer.chasingPosX - abstractClientPlayer.getPosX();
        double m = abstractClientPlayer.chasingPosZ - abstractClientPlayer.getPosZ();
        float n = abstractClientPlayer.prevRenderYawOffset + abstractClientPlayer.renderYawOffset - abstractClientPlayer.prevRenderYawOffset;
        double o = MathHelper.sin(n * ((float)Math.PI / 180));
        double p = -MathHelper.cos(n * ((float)Math.PI / 180));
        float heightMul = 5.0f;
        float straveMul = 3.0f;
        if (abstractClientPlayer.canSwim()) {
            heightMul *= 2.0f;
        }
        double fallHack = MathHelper.clamp((abstractClientPlayer.prevPosY - abstractClientPlayer.getPosY()) * 10.0, 0.0, 1.0);
        if (abstractClientPlayer.canSwim()) {
            simulation.setGravity(2.5f);
        } else {
            simulation.setGravity(25.0f);
        }
        Vector3 gravity = new Vector3(0.0f, -1.0f, 0.0f);
        Vector2 strave = new Vector2((float)(abstractClientPlayer.getPosX() - abstractClientPlayer.prevPosX), (float)(abstractClientPlayer.getPosZ() - abstractClientPlayer.prevPosZ));
        strave.rotateDegrees(-abstractClientPlayer.rotationYaw);
        double changeX = d * o + m * p + fallHack + (double)(abstractClientPlayer.isCrouching() && !simulation.isSneaking() ? 3 : 0);
        double changeY = (abstractClientPlayer.getPosY() - abstractClientPlayer.prevPosY) * (double)heightMul + (double)(abstractClientPlayer.isCrouching() && !simulation.isSneaking() ? 1 : 0);
        double changeZ = -strave.x * straveMul;
        simulation.setSneaking(abstractClientPlayer.isCrouching());
        Vector3 change = new Vector3((float)changeX, (float)changeY, (float)changeZ);
        if (abstractClientPlayer.isActualySwimming()) {
            float rotation = abstractClientPlayer.rotationPitch;
            gravity.rotateDegrees(rotation += 90.0f);
            change.rotateDegrees(rotation);
        }
        simulation.setGravityDirection(gravity);
        simulation.applyMovement(change);
        simulation.simulate();
    }
}
