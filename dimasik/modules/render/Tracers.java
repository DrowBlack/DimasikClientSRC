package dimasik.modules.render;

import com.mojang.blaze3d.platform.GlStateManager;
import dimasik.Load;
import dimasik.events.api.EventListener;
import dimasik.events.main.render.EventRender3D;
import dimasik.helpers.render.ColorHelpers;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.SliderOption;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import org.lwjgl.opengl.GL11;

public class Tracers
extends Module {
    private final SliderOption width = new SliderOption("Width", 1.0f, 1.0f, 5.0f).increment(0.1f);
    private final EventListener<EventRender3D.Post> render = this::render3d;

    public Tracers() {
        super("Tracers", Category.RENDER);
        this.settings(this.width);
    }

    public void render3d(EventRender3D.Post event) {
        GL11.glPushMatrix();
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glLineWidth(((Float)this.width.getValue()).floatValue());
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        Vector3d vec = new Vector3d(0.0, 0.0, 150.0).rotatePitch((float)(-Math.toRadians(Tracers.mc.player.rotationPitch))).rotateYaw((float)(-Math.toRadians(Tracers.mc.player.rotationYaw)));
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        float alpha = 1.0f;
        for (PlayerEntity playerEntity : Tracers.mc.world.getPlayers()) {
            if (!(playerEntity instanceof RemoteClientPlayerEntity) || Tracers.mc.gameSettings.getPointOfView() != PointOfView.FIRST_PERSON) continue;
            int tracersColor = Load.getInstance().getHooks().getFriendManagers().is(playerEntity.getGameProfile().getName()) ? ColorHelpers.rgba(0, 255, 0, 255) : ColorHelpers.rgba(255, 255, 255, 255);
            double x = playerEntity.lastTickPosX + (playerEntity.getPosX() - playerEntity.lastTickPosX) * (double)mc.getRenderPartialTicks() - Tracers.mc.getRenderManager().info.getProjectedView().getX();
            double y = playerEntity.lastTickPosY + (playerEntity.getPosY() - playerEntity.lastTickPosY) * (double)mc.getRenderPartialTicks() - Tracers.mc.getRenderManager().info.getProjectedView().getY();
            double z = playerEntity.lastTickPosZ + (playerEntity.getPosZ() - playerEntity.lastTickPosZ) * (double)mc.getRenderPartialTicks() - Tracers.mc.getRenderManager().info.getProjectedView().getZ();
            ColorHelpers.setColor(tracersColor);
            bufferBuilder.begin(3, DefaultVertexFormats.POSITION);
            bufferBuilder.pos(vec.x, vec.y, vec.z).endVertex();
            bufferBuilder.pos(x, y, z).endVertex();
            Tessellator.getInstance().draw();
        }
        GL11.glHint(3154, 4352);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }
}
