package dimasik.itemics.utils;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dimasik.itemics.Itemics;
import dimasik.itemics.api.ItemicsAPI;
import dimasik.itemics.api.command.IItemicsChatControl;
import dimasik.itemics.api.pathing.goals.GoalBlock;
import dimasik.itemics.api.utils.BetterBlockPos;
import dimasik.itemics.api.utils.Helper;
import dimasik.itemics.utils.IRenderer;
import dimasik.itemics.utils.PathRenderer;
import java.awt.Color;
import java.util.Collections;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;

public class GuiClick
extends Screen
implements Helper {
    private Matrix4f projectionViewMatrix;
    private BlockPos clickStart;
    private BlockPos currentMouseOver;

    public GuiClick() {
        super(new StringTextComponent("CLICK"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        double mx = GuiClick.mc.mouseHelper.getMouseX();
        double my = GuiClick.mc.mouseHelper.getMouseY();
        my = (double)mc.getMainWindow().getHeight() - my;
        Vector3d near = this.toWorld(mx *= (double)mc.getMainWindow().getFramebufferWidth() / (double)mc.getMainWindow().getWidth(), my *= (double)mc.getMainWindow().getFramebufferHeight() / (double)mc.getMainWindow().getHeight(), 0.0);
        Vector3d far = this.toWorld(mx, my, 1.0);
        if (near != null && far != null) {
            Vector3d viewerPos = new Vector3d(PathRenderer.posX(), PathRenderer.posY(), PathRenderer.posZ());
            ClientPlayerEntity player = ItemicsAPI.getProvider().getPrimaryItemics().getPlayerContext().player();
            BlockRayTraceResult result = player.world.rayTraceBlocks(new RayTraceContext(near.add(viewerPos), far.add(viewerPos), RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, player));
            if (result != null && ((RayTraceResult)result).getType() == RayTraceResult.Type.BLOCK) {
                this.currentMouseOver = result.getPos();
            }
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        if (this.currentMouseOver != null) {
            if (mouseButton == 0) {
                if (this.clickStart != null && !this.clickStart.equals(this.currentMouseOver)) {
                    ItemicsAPI.getProvider().getPrimaryItemics().getSelectionManager().removeAllSelections();
                    ItemicsAPI.getProvider().getPrimaryItemics().getSelectionManager().addSelection(BetterBlockPos.from(this.clickStart), BetterBlockPos.from(this.currentMouseOver));
                    StringTextComponent component = new StringTextComponent("Selection made! For usage: " + (String)Itemics.settings().prefix.value + "help sel");
                    component.setStyle(component.getStyle().setFormatting(TextFormatting.WHITE).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, IItemicsChatControl.FORCE_COMMAND_PREFIX + "help sel")));
                    Helper.HELPER.logDirect(component);
                    this.clickStart = null;
                } else {
                    ItemicsAPI.getProvider().getPrimaryItemics().getCustomGoalProcess().setGoalAndPath(new GoalBlock(this.currentMouseOver));
                }
            } else if (mouseButton == 1) {
                ItemicsAPI.getProvider().getPrimaryItemics().getCustomGoalProcess().setGoalAndPath(new GoalBlock(this.currentMouseOver.up()));
            }
        }
        this.clickStart = null;
        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        this.clickStart = this.currentMouseOver;
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void onRender(MatrixStack modelViewStack, Matrix4f projectionMatrix) {
        this.projectionViewMatrix = projectionMatrix.copy();
        this.projectionViewMatrix.mul(modelViewStack.getLast().getMatrix());
        this.projectionViewMatrix.invert();
        if (this.currentMouseOver != null) {
            Entity e = mc.getRenderViewEntity();
            PathRenderer.drawManySelectionBoxes(modelViewStack, e, Collections.singletonList(this.currentMouseOver), Color.CYAN);
            if (this.clickStart != null && !this.clickStart.equals(this.currentMouseOver)) {
                RenderSystem.enableBlend();
                RenderSystem.blendFuncSeparate(770, 771, 1, 0);
                RenderSystem.color4f(Color.RED.getColorComponents(null)[0], Color.RED.getColorComponents(null)[1], Color.RED.getColorComponents(null)[2], 0.4f);
                RenderSystem.lineWidth(((Float)Itemics.settings().pathRenderLineWidthPixels.value).floatValue());
                RenderSystem.disableTexture();
                RenderSystem.depthMask(false);
                RenderSystem.disableDepthTest();
                BetterBlockPos a = new BetterBlockPos(this.currentMouseOver);
                BetterBlockPos b = new BetterBlockPos(this.clickStart);
                IRenderer.drawAABB(modelViewStack, new AxisAlignedBB(Math.min(a.x, b.x), Math.min(a.y, b.y), Math.min(a.z, b.z), Math.max(a.x, b.x) + 1, Math.max(a.y, b.y) + 1, Math.max(a.z, b.z) + 1));
                RenderSystem.enableDepthTest();
                RenderSystem.depthMask(true);
                RenderSystem.enableTexture();
                RenderSystem.disableBlend();
            }
        }
    }

    private Vector3d toWorld(double x, double y, double z) {
        if (this.projectionViewMatrix == null) {
            return null;
        }
        x /= (double)mc.getMainWindow().getFramebufferWidth();
        y /= (double)mc.getMainWindow().getFramebufferHeight();
        x = x * 2.0 - 1.0;
        y = y * 2.0 - 1.0;
        Vector4f pos = new Vector4f((float)x, (float)y, (float)z, 1.0f);
        pos.transform(this.projectionViewMatrix);
        if (pos.getW() == 0.0f) {
            return null;
        }
        pos.perspectiveDivide();
        return new Vector3d(pos.getX(), pos.getY(), pos.getZ());
    }
}
