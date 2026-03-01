package dimasik.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dimasik.events.api.EventListener;
import dimasik.events.main.render.EventRender2D;
import dimasik.helpers.render.ColorHelpers;
import dimasik.helpers.render.ScreenHelpers;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.MultiOption;
import dimasik.managers.module.option.main.MultiOptionValue;
import dimasik.managers.module.option.main.SelectOption;
import dimasik.managers.module.option.main.SelectOptionValue;
import dimasik.modules.render.ESP;
import dimasik.utils.math.MathUtils;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector4f;
import org.joml.Vector4i;
import org.lwjgl.opengl.GL11;

public class ItemESP
extends Module {
    private final HashMap<Entity, Vector4f> positions = new HashMap();
    private final MultiOption elemens = new MultiOption("Elements", new MultiOptionValue("Icon", true), new MultiOptionValue("Tags", true), new MultiOptionValue("Box", true));
    private final SelectOption boxType = new SelectOption("Box Type", 0, new SelectOptionValue("2D Box"), new SelectOptionValue("Corners"));
    private final EventListener<EventRender2D.Post> render = this::render;

    public ItemESP() {
        super("ItemESP", Category.RENDER);
        this.settings(this.elemens, this.boxType);
    }

    public void render(EventRender2D.Post eventRender2D) {
        if (ItemESP.mc.world == null) {
            return;
        }
        this.positions.clear();
        for (Entity entity : ItemESP.mc.world.getAllEntities()) {
            if (!this.isValid(entity) || !(entity instanceof ItemEntity)) continue;
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
            if (!(entity instanceof ItemEntity)) continue;
            ItemEntity itemEntity = (ItemEntity)entity;
            Vector4f position = entry.getValue();
            ItemStack itemStack = itemEntity.getItem();
            int itemCount = itemStack.getCount();
            float width = position.z - position.x;
            double x = position.x;
            double y = position.y;
            String itemName = itemStack.getDisplayName().getString();
            int color = -1;
            Item item = itemStack.getItem();
            if (item instanceof ArmorItem) {
                color = ColorHelpers.rgba(0, 255, 255, 255);
            } else if (item == Items.TOTEM_OF_UNDYING) {
                color = ColorHelpers.rgba(255, 255, 0, 235);
            } else if (item == Items.GOLDEN_APPLE) {
                color = ColorHelpers.rgba(0, 255, 255, 255);
            } else if (item == Items.ENCHANTED_GOLDEN_APPLE) {
                color = ColorHelpers.rgba(255, 0, 255, 255);
            }
            float length = sf_semibold.getWidth(itemName + (String)(itemCount == 1 ? "" : " x" + itemCount), 13.0f);
            Vector4i colors = new Vector4i(ColorHelpers.getThemeColor(2), ColorHelpers.getThemeColor(1), ColorHelpers.getThemeColor(1), ColorHelpers.getThemeColor(2));
            RenderSystem.enableBlend();
            RenderSystem.disableTexture();
            RenderSystem.defaultBlendFunc();
            RenderSystem.shadeModel(7425);
            buffer.endVertex();
            buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            GL11.glPushMatrix();
            this.glCenteredScale(position.x + width / 2.0f - length / 2.0f, position.y - 8.0f, length, 10.0f, 0.5f);
            if (this.elemens.getSelected("Box")) {
                if (this.boxType.getSelected("2D Box")) {
                    VisualHelpers.drawPlayerBox(position.x - 0.5f, position.y - 0.5f, position.z + 0.5f, position.w + 0.5f, 2.0, ColorHelpers.rgba(0, 0, 0, 128));
                    VisualHelpers.drawVectorBox(position.x, position.y, position.z, position.w, 1.0, colors);
                } else if (this.boxType.getSelected("Corners")) {
                    double endX = position.z;
                    double endY = position.w;
                    int getColor = ColorHelpers.getThemeColor(1);
                    int getColor2 = ColorHelpers.getThemeColor(2);
                    int backcolor = ColorHelpers.rgb(26, 26, 26);
                    double xproc = 0.2;
                    double yproc = 0.15;
                    double xdist = endX - x;
                    double ydist = endY - y;
                    double xoff = xdist * xproc;
                    double yoff = ydist * yproc;
                    ItemESP.drawMcRect(x - 2.0, y - 2.0, x + xoff + 1.0, y + 2.0, backcolor);
                    ItemESP.drawMcRect(endX - xoff - 1.0, y - 2.0, endX + 2.0, y + 2.0, backcolor);
                    ItemESP.drawMcRect(x - 2.0, endY - 2.0, x + xoff + 1.0, endY + 2.0, backcolor);
                    ItemESP.drawMcRect(endX - xoff - 1.0, endY - 2.0, endX + 2.0, endY + 2.0, backcolor);
                    ItemESP.drawMcRect(x - 2.0, y + 1.0, x + 2.0, y + yoff + 2.0, backcolor);
                    ItemESP.drawMcRect(x - 2.0, endY - yoff - 2.0, x + 2.0, endY + 1.0, backcolor);
                    ItemESP.drawMcRect(endX - 2.0, y + 1.0, endX + 2.0, y + yoff + 2.0, backcolor);
                    ItemESP.drawMcRect(endX - 2.0, endY - yoff - 2.0, endX + 2.0, endY + 1.0, backcolor);
                    ItemESP.drawMcRect(x - 1.0, y - 1.0, x + xoff, y + 1.0, getColor);
                    ItemESP.drawMcRect(endX - xoff, y - 1.0, endX + 1.0, y + 1.0, getColor2);
                    ItemESP.drawMcRect(x - 1.0, endY - 1.0, x + xoff, endY + 1.0, getColor2);
                    ItemESP.drawMcRect(endX - xoff, endY - 1.0, endX + 1.0, endY + 1.0, getColor);
                    ItemESP.drawMcRect(x - 1.0, y + 1.0, x + 1.0, y + yoff, getColor);
                    ItemESP.drawMcRect(x - 1.0, endY - yoff, x + 1.0, endY, getColor2);
                    ItemESP.drawMcRect(endX - 1.0, y + 1.0, endX + 1.0, y + yoff, getColor2);
                    ItemESP.drawMcRect(endX - 1.0, endY - yoff, endX + 1.0, endY, getColor);
                }
            }
            Tessellator.getInstance().draw();
            RenderSystem.shadeModel(7424);
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
            if (this.elemens.getSelected("Icon")) {
                VisualHelpers.drawRoundedRect((float)(x + (double)(width / 2.0f) - 8.75 - 3.5), (float)(y - 51.5), 26.0f, 26.0f, 0.0f, ColorHelpers.rgba(3, 3, 3, 175));
                MathUtils.scaleElements((float)(x + (double)(width / 2.0f) - 7.0 - 3.5), (float)y, 1.35f, () -> ESP.drawItemStack(new ItemStack(itemStack.getItem()), (float)(x + (double)(width / 2.0f) - 7.0 - 3.5), y - 37.0, null, false));
            }
            if (this.elemens.getSelected("Tags")) {
                VisualHelpers.drawRoundedRect((float)(x + (double)(width / 2.0f) - (double)(length / 2.0f) - 3.5), (float)(y - 26.0), length + 7.0f, 20.5f, 0.0f, ColorHelpers.rgba(10, 10, 10, 140));
                sf_semibold.drawText(eventRender2D.getMatrixStack(), itemName, (float)(x + (double)(width / 2.0f) - (double)(length / 2.0f)), (float)(y - 22.0), color, 13.0f);
                sf_semibold.drawText(eventRender2D.getMatrixStack(), (String)(itemCount == 1 ? "" : " x" + itemCount), (float)(x + (double)(width / 2.0f) - (double)(length / 2.0f) + (double)sf_semibold.getWidth(itemName, 13.0f)), (float)(y - 22.0), color, 13.0f);
            }
            GL11.glPopMatrix();
        }
    }

    public boolean isInView(Entity ent) {
        if (mc.getRenderViewEntity() == null) {
            return false;
        }
        WorldRenderer.frustum.setCameraPosition(ItemESP.mc.getRenderManager().info.getProjectedView().x, ItemESP.mc.getRenderManager().info.getProjectedView().y, ItemESP.mc.getRenderManager().info.getProjectedView().z);
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

    public static void drawMcRect(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }
        float f3 = (float)(color >> 24 & 0xFF) / 255.0f;
        float f = (float)(color >> 16 & 0xFF) / 255.0f;
        float f1 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f2 = (float)(color & 0xFF) / 255.0f;
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        bufferbuilder.pos(left, bottom, 1.0).color(f, f1, f2, f3).endVertex();
        bufferbuilder.pos(right, bottom, 1.0).color(f, f1, f2, f3).endVertex();
        bufferbuilder.pos(right, top, 1.0).color(f, f1, f2, f3).endVertex();
        bufferbuilder.pos(left, top, 1.0).color(f, f1, f2, f3).endVertex();
    }
}
