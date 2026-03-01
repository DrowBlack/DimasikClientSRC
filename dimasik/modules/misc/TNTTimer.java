package dimasik.modules.misc;

import com.mojang.blaze3d.systems.RenderSystem;
import dimasik.events.api.EventListener;
import dimasik.events.main.render.EventRender2D;
import dimasik.helpers.render.ColorHelpers;
import dimasik.helpers.render.ScreenHelpers;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.utils.math.MathUtils;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector4f;
import org.lwjgl.opengl.GL11;

public class TNTTimer
extends Module {
    private final HashMap<Entity, Vector4f> positions = new HashMap();
    private final EventListener<EventRender2D.Post> render = this::render;

    public TNTTimer() {
        super("TNTTimer", Category.MISC);
    }

    public void render(EventRender2D.Post eventRender2D) {
        if (TNTTimer.mc.world == null) {
            return;
        }
        this.positions.clear();
        for (Entity entity : TNTTimer.mc.world.getAllEntities()) {
            if (!this.isValid(entity) || !(entity instanceof TNTEntity)) continue;
            double x = MathUtils.interpolate(entity.getPosX(), entity.lastTickPosX, (double)eventRender2D.getPartialTicks());
            double y = MathUtils.interpolate(entity.getPosY(), entity.lastTickPosY, (double)eventRender2D.getPartialTicks());
            double z = MathUtils.interpolate(entity.getPosZ(), entity.lastTickPosZ, (double)eventRender2D.getPartialTicks());
            Vector3d size = new Vector3d(entity.getBoundingBox().maxX - entity.getBoundingBox().minX, entity.getBoundingBox().maxY - entity.getBoundingBox().minY, entity.getBoundingBox().maxZ - entity.getBoundingBox().minZ);
            AxisAlignedBB aabb = new AxisAlignedBB(x - size.x / 2.0, y, z - size.z / 2.0, x + size.x / 2.0, y + size.y, z + size.z / 2.0);
            Vector4f position = null;
            for (int i = 0; i < 8; ++i) {
                Vector2f vector = ScreenHelpers.worldToScreen(i % 2 == 0 ? aabb.minX : aabb.maxX, i / 2 % 2 == 0 ? aabb.minY : aabb.maxY, i / 4 % 2 == 0 ? aabb.minZ : aabb.maxZ);
                if (position == null) {
                    position = new Vector4f(vector.x, vector.y, 1.0f, 1.0f);
                    continue;
                }
                position.x = Math.min(vector.x, position.x);
                position.y = Math.min(vector.y, position.y);
                position.z = Math.max(vector.x, position.z);
                position.w = Math.max(vector.y, position.w);
            }
            this.positions.put(entity, position);
        }
        for (Map.Entry<Entity, Vector4f> entry : this.positions.entrySet()) {
            Entity entity = entry.getKey();
            if (!(entity instanceof TNTEntity)) continue;
            TNTEntity itemEntity = (TNTEntity)entity;
            Vector4f position = entry.getValue();
            float width = position.z - position.x;
            double x = position.x;
            double y = position.y;
            String itemName = "\u0414\u043e \u0432\u0437\u0440\u044b\u0432\u0430: " + (float)itemEntity.getFuse() / 10.0f;
            int color = -1;
            float length = sf_semibold.getWidth(itemName, 13.0f);
            RenderSystem.enableBlend();
            RenderSystem.disableTexture();
            RenderSystem.defaultBlendFunc();
            RenderSystem.shadeModel(7425);
            buffer.endVertex();
            buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            GL11.glPushMatrix();
            this.glCenteredScale(position.x + width / 2.0f - length / 2.0f, position.y - 8.0f, length, 10.0f, 0.5f);
            Tessellator.getInstance().draw();
            RenderSystem.shadeModel(7424);
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
            VisualHelpers.drawRoundedRect((float)(x + (double)(width / 2.0f) - (double)(length / 2.0f) - 3.5), (float)(y - 26.0), length + 7.0f, 20.5f, 0.0f, ColorHelpers.rgba(10, 10, 10, 140));
            sf_semibold.drawText(eventRender2D.getMatrixStack(), itemName, (float)(x + (double)(width / 2.0f) - (double)(length / 2.0f)), (float)(y - 22.0), color, 13.0f);
            if ((float)itemEntity.getFuse() / 10.0f > 0.0f) {
                Vector3d vector3d = itemEntity.getPositionVec();
            }
            GL11.glPopMatrix();
        }
    }

    public boolean isInView(Entity ent) {
        if (mc.getRenderViewEntity() == null) {
            return false;
        }
        WorldRenderer.frustum.setCameraPosition(TNTTimer.mc.getRenderManager().info.getProjectedView().x, TNTTimer.mc.getRenderManager().info.getProjectedView().y, TNTTimer.mc.getRenderManager().info.getProjectedView().z);
        return WorldRenderer.frustum.isBoundingBoxInFrustum(ent.getBoundingBox()) || ent.ignoreFrustumCheck;
    }

    public boolean isValid(Entity e) {
        return this.isInView(e);
    }

    public void glCenteredScale(float x, float y, float w, float h, float f) {
        GL11.glTranslatef(x + w / 2.0f, y + h / 2.0f, 0.0f);
        GL11.glScalef(f, f, 1.0f);
        GL11.glTranslatef(-x - w / 2.0f, -y - h / 2.0f, 0.0f);
    }
}
