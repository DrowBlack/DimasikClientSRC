package dimasik.modules.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dimasik.events.api.EventListener;
import dimasik.events.main.render.EventRender2D;
import dimasik.events.main.render.EventRender3D;
import dimasik.helpers.render.ColorHelpers;
import dimasik.helpers.render.ScreenHelpers;
import dimasik.helpers.visual.StencilHelpers;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.CheckboxOption;
import dimasik.managers.module.option.main.MultiOption;
import dimasik.managers.module.option.main.MultiOptionValue;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.lwjgl.opengl.GL11;

public class PearlPrediction
extends Module {
    private final CheckboxOption tag = new CheckboxOption("Tag", true);
    private final MultiOption list = new MultiOption("List", new MultiOptionValue("Pearl", true), new MultiOptionValue("Item", false));
    private final EventListener<EventRender2D.Post> renderListener = this::render2;
    private final EventListener<EventRender3D.Post> renderListener2 = this::render;

    public PearlPrediction() {
        super("Predictions", Category.RENDER);
        this.settings(this.list, this.tag);
    }

    public void render(EventRender3D.Post event) {
        RenderSystem.pushMatrix();
        RenderSystem.translated(-mc.getRenderManager().renderPosX(), -mc.getRenderManager().renderPosY(), -mc.getRenderManager().renderPosZ());
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();
        GL11.glEnable(2848);
        RenderSystem.lineWidth(2.0f);
        Tessellator.getInstance().getBuffer().begin(1, DefaultVertexFormats.POSITION_COLOR);
        for (Entity entity : PearlPrediction.mc.world.getAllEntities()) {
            if (entity instanceof EnderPearlEntity) {
                EnderPearlEntity pearl = (EnderPearlEntity)entity;
                if (this.list.getSelected("Pearl")) {
                    this.renderLine(pearl);
                }
            }
            if (!(entity instanceof ItemEntity)) continue;
            ItemEntity arrow = (ItemEntity)entity;
            if (!this.list.getSelected("Item")) continue;
            this.renderLine(arrow);
        }
        Tessellator.getInstance().getBuffer().finishDrawing();
        WorldVertexBufferUploader.draw(Tessellator.getInstance().getBuffer());
        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        GL11.glDisable(2848);
        RenderSystem.popMatrix();
    }

    public void render2(EventRender2D.Post event) {
        MatrixStack matrixStack = event.getMatrixStack();
        for (Entity entity : PearlPrediction.mc.world.getAllEntities()) {
            int back;
            String name;
            double time;
            float y;
            float x;
            float height;
            float width;
            float widthAboba;
            Vector2f position;
            if (!((Boolean)this.tag.getValue()).booleanValue()) continue;
            if (entity instanceof EnderPearlEntity) {
                EnderPearlEntity pearl = (EnderPearlEntity)entity;
                if (this.list.getSelected("Pearl")) {
                    position = ScreenHelpers.worldToScreen(this.calculateLandingPosition(pearl));
                    widthAboba = 0.0f;
                    width = 40.0f + widthAboba;
                    height = 12.0f;
                    x = position.x - width / 2.0f;
                    y = position.y;
                    time = this.calculateTimeToFall(pearl);
                    String var10000 = String.format("%.1f", time);
                    name = var10000.replace(",", ".") + " \u0441\u0435\u043a.";
                    back = ColorHelpers.rgba(30, 30, 30, 150);
                    VisualHelpers.drawRoundedRect(matrixStack, x, y, width, height, 1.0f, back);
                    StencilHelpers.init();
                    VisualHelpers.drawRoundedRect(matrixStack, x, y, width, height, 1.0f, -1);
                    StencilHelpers.read(1);
                    sf_medium.drawText(matrixStack, name, x + width / 2.0f - sf_medium.getWidth(name, 7.0f) / 2.0f + 4.0f, y + height / 2.0f - sf_medium.getHeight(7.0f) / 2.0f, ColorHelpers.rgba(255, 255, 255, 255), 7.0f);
                    this.drawItemStack(new ItemStack(Items.ENDER_PEARL), x + width / 2.0f - sf_medium.getWidth(name, 7.0f) / 2.0f - 6.0f, y + 2.0f, 8.0, 8.0);
                    StencilHelpers.uninit();
                    widthAboba = sf_medium.getWidth(name, 7.0f);
                }
            }
            if (!(entity instanceof ItemEntity)) continue;
            ItemEntity arrow = (ItemEntity)entity;
            if (!this.list.getSelected("Item") || arrow.isOnGround() || arrow.isOnGround()) continue;
            position = ScreenHelpers.worldToScreen(this.calculateLandingPosition(arrow));
            widthAboba = 0.0f;
            width = 40.0f + widthAboba;
            height = 12.0f;
            x = position.x - width / 2.0f;
            y = position.y;
            time = this.calculateTimeToFall(arrow);
            String var28 = String.format("%.1f", time);
            name = var28.replace(",", ".") + " \u0441\u0435\u043a.";
            back = ColorHelpers.rgba(30, 30, 30, 150);
            VisualHelpers.drawRoundedRect(matrixStack, x, y, width, height, 1.0f, back);
            StencilHelpers.init();
            VisualHelpers.drawRoundedRect(matrixStack, x, y, width, height, 1.0f, -1);
            StencilHelpers.read(1);
            sf_medium.drawText(matrixStack, name, x + width / 2.0f - sf_medium.getWidth(name, 7.0f) / 2.0f + 4.0f, y + height / 2.0f - sf_medium.getHeight(7.0f) / 2.0f, ColorHelpers.rgba(255, 255, 255, 255), 7.0f);
            this.drawItemStack(new ItemStack(arrow.getItem().getItem()), x + width / 2.0f - sf_medium.getWidth(name, 7.0f) / 2.0f - 6.0f, y + 2.0f, 8.0, 8.0);
            StencilHelpers.uninit();
            widthAboba = sf_medium.getWidth(name, 7.0f);
        }
    }

    private void renderLine(Entity pearl) {
        Vector3d pearlPosition = pearl.getPositionVec().add(0.0, 0.0, 0.0);
        Vector3d pearlMotion = pearl.getMotion();
        if (!pearl.isOnGround() && !pearl.isOnGround()) {
            for (int i = 0; i <= 600; ++i) {
                Vector3d lastPosition = new Vector3d(pearlPosition.x, pearlPosition.y, pearlPosition.z);
                pearlPosition = pearlPosition.add(pearlMotion);
                pearlMotion = this.updatePearlMotion(pearl, pearlMotion);
                if (this.shouldEntityHit(pearlPosition.add(0.0, 1.0, 0.0), lastPosition.add(0.0, 1.0, 0.0)) || pearlPosition.y <= 0.0) break;
                float[] colors = this.getLineColor(i);
                buffer.pos(lastPosition.x, lastPosition.y, lastPosition.z).color(colors[0], colors[1], colors[2], 1.0f).endVertex();
                buffer.pos(pearlPosition.x, pearlPosition.y, pearlPosition.z).color(colors[0], colors[1], colors[2], 1.0f).endVertex();
            }
        }
    }

    private Vector3d updatePearlMotion(Entity pearl, Vector3d originalPearlMotion) {
        Vector3d pearlMotion = pearl.isInWater() ? originalPearlMotion.scale(0.8f) : originalPearlMotion.scale(0.99f);
        if (!(!pearl.hasNoGravity() && pearl instanceof ArrowEntity || pearl instanceof ItemEntity)) {
            if (!pearl.hasNoGravity() && pearl instanceof EnderPearlEntity) {
                pearlMotion.y -= (double)0.03f;
            }
        } else {
            pearlMotion.y -= (double)0.05f;
        }
        return pearlMotion;
    }

    private boolean shouldEntityHit(Vector3d pearlPosition, Vector3d lastPosition) {
        RayTraceContext rayTraceContext = new RayTraceContext(lastPosition, pearlPosition, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, PearlPrediction.mc.player);
        BlockRayTraceResult blockHitResult = PearlPrediction.mc.world.rayTraceBlocks(rayTraceContext);
        return blockHitResult.getType() == RayTraceResult.Type.BLOCK;
    }

    private float[] getLineColor(int index) {
        int color = ColorHelpers.getTheme(index * 2);
        return ColorHelpers.rgb(color);
    }

    private Vector3d calculateLandingPosition(Entity pearl) {
        Vector3d pearlPosition = pearl.getPositionVec().add(0.0, 0.0, 0.0);
        Vector3d pearlMotion = pearl.getMotion();
        for (int i = 0; i <= 300; ++i) {
            Vector3d lastPosition = pearlPosition;
            pearlPosition = pearlPosition.add(pearlMotion);
            pearlMotion = this.updatePearlMotion(pearl, pearlMotion);
            if (this.shouldEntityHit(pearlPosition.add(0.0, 1.0, 0.0), lastPosition.add(0.0, 1.0, 0.0)) || pearlPosition.y <= 0.0) break;
        }
        return pearlPosition;
    }

    private double calculateTimeToFall(Entity pearl) {
        Vector3d pearlPosition = pearl.getPositionVec().add(0.0, 0.0, 0.0);
        Vector3d pearlMotion = pearl.getMotion();
        double time = 0.0;
        for (int i = 0; i <= 300; ++i) {
            Vector3d lastPosition = pearlPosition;
            pearlPosition = pearlPosition.add(pearlMotion);
            pearlMotion = this.updatePearlMotion(pearl, pearlMotion);
            time += 0.05;
            if (this.shouldEntityHit(pearlPosition.add(0.0, 1.0, 0.0), lastPosition.add(0.0, 1.0, 0.0)) || pearlPosition.y <= 0.0) break;
        }
        return time;
    }

    public void drawItemStack(ItemStack stack, double x, double y, double width, double height) {
        RenderSystem.translated(x, y, 0.0);
        double scaleX = width / 16.0;
        double scaleY = height / 16.0;
        RenderSystem.scaled(scaleX, scaleY, 1.0);
        mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, 0, 0);
        RenderSystem.scaled(1.0 / scaleX, 1.0 / scaleY, 1.0);
        RenderSystem.translated(-x, -y, 0.0);
    }
}
