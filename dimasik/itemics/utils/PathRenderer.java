package dimasik.itemics.utils;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dimasik.itemics.api.ItemicsAPI;
import dimasik.itemics.api.event.events.RenderEvent;
import dimasik.itemics.api.pathing.calc.IPath;
import dimasik.itemics.api.pathing.goals.Goal;
import dimasik.itemics.api.pathing.goals.GoalComposite;
import dimasik.itemics.api.pathing.goals.GoalGetToBlock;
import dimasik.itemics.api.pathing.goals.GoalInverted;
import dimasik.itemics.api.pathing.goals.GoalTwoBlocks;
import dimasik.itemics.api.pathing.goals.GoalXZ;
import dimasik.itemics.api.pathing.goals.GoalYLevel;
import dimasik.itemics.api.utils.BetterBlockPos;
import dimasik.itemics.api.utils.Helper;
import dimasik.itemics.api.utils.interfaces.IGoalRenderPos;
import dimasik.itemics.behavior.PathingBehavior;
import dimasik.itemics.pathing.path.PathExecutor;
import dimasik.itemics.utils.BlockStateInterface;
import dimasik.itemics.utils.GuiClick;
import dimasik.itemics.utils.IRenderer;
import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.tileentity.BeaconTileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.DimensionType;
import org.lwjgl.opengl.GL11;

public final class PathRenderer
implements IRenderer,
Helper {
    private static final ResourceLocation TEXTURE_BEACON_BEAM = new ResourceLocation("textures/entity/beacon_beam.png");

    private PathRenderer() {
    }

    public static double posX() {
        return renderManager.renderPosX();
    }

    public static double posY() {
        return renderManager.renderPosY();
    }

    public static double posZ() {
        return renderManager.renderPosZ();
    }

    public static void render(RenderEvent event, PathingBehavior behavior) {
        DimensionType currentRenderViewDimension;
        DimensionType thisPlayerDimension;
        float partialTicks = event.getPartialTicks();
        Goal goal = behavior.getGoal();
        if (Helper.mc.currentScreen instanceof GuiClick) {
            ((GuiClick)Helper.mc.currentScreen).onRender(event.getModelViewStack(), event.getProjectionMatrix());
        }
        if ((thisPlayerDimension = behavior.itemics.getPlayerContext().world().getDimensionType()) != (currentRenderViewDimension = ItemicsAPI.getProvider().getPrimaryItemics().getPlayerContext().world().getDimensionType())) {
            return;
        }
        Entity renderView = Helper.mc.getRenderViewEntity();
        if (renderView.world != ItemicsAPI.getProvider().getPrimaryItemics().getPlayerContext().world()) {
            System.out.println("I have no idea what's going on");
            System.out.println("The primary itemics is in a different world than the render view entity");
            System.out.println("Not rendering the path");
            return;
        }
        if (goal != null && ((Boolean)PathRenderer.settings.renderGoal.value).booleanValue()) {
            PathRenderer.drawDankLitGoalBox(event.getModelViewStack(), renderView, goal, partialTicks, (Color)PathRenderer.settings.colorGoalBox.value);
        }
        if (!((Boolean)PathRenderer.settings.renderPath.value).booleanValue()) {
            return;
        }
        PathExecutor current = behavior.getCurrent();
        PathExecutor next = behavior.getNext();
        if (current != null && ((Boolean)PathRenderer.settings.renderSelectionBoxes.value).booleanValue()) {
            PathRenderer.drawManySelectionBoxes(event.getModelViewStack(), renderView, current.toBreak(), (Color)PathRenderer.settings.colorBlocksToBreak.value);
            PathRenderer.drawManySelectionBoxes(event.getModelViewStack(), renderView, current.toPlace(), (Color)PathRenderer.settings.colorBlocksToPlace.value);
            PathRenderer.drawManySelectionBoxes(event.getModelViewStack(), renderView, current.toWalkInto(), (Color)PathRenderer.settings.colorBlocksToWalkInto.value);
        }
        if (current != null && current.getPath() != null) {
            int renderBegin = Math.max(current.getPosition() - 3, 0);
            PathRenderer.drawPath(event.getModelViewStack(), current.getPath(), renderBegin, (Color)PathRenderer.settings.colorCurrentPath.value, (Boolean)PathRenderer.settings.fadePath.value, 10, 20);
        }
        if (next != null && next.getPath() != null) {
            PathRenderer.drawPath(event.getModelViewStack(), next.getPath(), 0, (Color)PathRenderer.settings.colorNextPath.value, (Boolean)PathRenderer.settings.fadePath.value, 10, 20);
        }
        behavior.getInProgress().ifPresent(currentlyRunning -> {
            currentlyRunning.bestPathSoFar().ifPresent(p -> PathRenderer.drawPath(event.getModelViewStack(), p, 0, (Color)PathRenderer.settings.colorBestPathSoFar.value, (Boolean)PathRenderer.settings.fadePath.value, 10, 20));
            currentlyRunning.pathToMostRecentNodeConsidered().ifPresent(mr -> {
                PathRenderer.drawPath(event.getModelViewStack(), mr, 0, (Color)PathRenderer.settings.colorMostRecentConsidered.value, (Boolean)PathRenderer.settings.fadePath.value, 10, 20);
                PathRenderer.drawManySelectionBoxes(event.getModelViewStack(), renderView, Collections.singletonList(mr.getDest()), (Color)PathRenderer.settings.colorMostRecentConsidered.value);
            });
        });
    }

    public static void drawPath(MatrixStack stack, IPath path, int startIndex, Color color, boolean fadeOut, int fadeStart0, int fadeEnd0) {
        IRenderer.startLines(color, ((Float)PathRenderer.settings.pathRenderLineWidthPixels.value).floatValue(), (Boolean)PathRenderer.settings.renderPathIgnoreDepth.value);
        int fadeStart = fadeStart0 + startIndex;
        int fadeEnd = fadeEnd0 + startIndex;
        List<BetterBlockPos> positions = path.positions();
        int i = startIndex;
        while (i < positions.size() - 1) {
            BetterBlockPos start = positions.get(i);
            int next = i + 1;
            BetterBlockPos end = positions.get(next);
            int dirX = end.x - start.x;
            int dirY = end.y - start.y;
            int dirZ = end.z - start.z;
            while (!(next + 1 >= positions.size() || fadeOut && next + 1 >= fadeStart || dirX != positions.get((int)(next + 1)).x - end.x || dirY != positions.get((int)(next + 1)).y - end.y || dirZ != positions.get((int)(next + 1)).z - end.z)) {
                end = positions.get(++next);
            }
            if (fadeOut) {
                float alpha;
                if (i <= fadeStart) {
                    alpha = 0.4f;
                } else {
                    if (i > fadeEnd) break;
                    alpha = 0.4f * (1.0f - (float)(i - fadeStart) / (float)(fadeEnd - fadeStart));
                }
                IRenderer.glColor(color, alpha);
            }
            PathRenderer.drawLine(stack, start.x, start.y, start.z, end.x, end.y, end.z);
            tessellator.draw();
            i = next;
        }
        IRenderer.endLines((Boolean)PathRenderer.settings.renderPathIgnoreDepth.value);
    }

    public static void drawLine(MatrixStack stack, double x1, double y1, double z1, double x2, double y2, double z2) {
        Matrix4f matrix4f = stack.getLast().getMatrix();
        double vpX = PathRenderer.posX();
        double vpY = PathRenderer.posY();
        double vpZ = PathRenderer.posZ();
        boolean renderPathAsFrickinThingy = (Boolean)PathRenderer.settings.renderPathAsLine.value == false;
        buffer.begin(renderPathAsFrickinThingy ? 3 : 1, DefaultVertexFormats.POSITION);
        buffer.pos(matrix4f, (float)(x1 + 0.5 - vpX), (float)(y1 + 0.5 - vpY), (float)(z1 + 0.5 - vpZ)).endVertex();
        buffer.pos(matrix4f, (float)(x2 + 0.5 - vpX), (float)(y2 + 0.5 - vpY), (float)(z2 + 0.5 - vpZ)).endVertex();
        if (renderPathAsFrickinThingy) {
            buffer.pos(matrix4f, (float)(x2 + 0.5 - vpX), (float)(y2 + 0.53 - vpY), (float)(z2 + 0.5 - vpZ)).endVertex();
            buffer.pos(matrix4f, (float)(x1 + 0.5 - vpX), (float)(y1 + 0.53 - vpY), (float)(z1 + 0.5 - vpZ)).endVertex();
            buffer.pos(matrix4f, (float)(x1 + 0.5 - vpX), (float)(y1 + 0.5 - vpY), (float)(z1 + 0.5 - vpZ)).endVertex();
        }
    }

    public static void drawManySelectionBoxes(MatrixStack stack, Entity player, Collection<BlockPos> positions, Color color) {
        IRenderer.startLines(color, ((Float)PathRenderer.settings.pathRenderLineWidthPixels.value).floatValue(), (Boolean)PathRenderer.settings.renderSelectionBoxesIgnoreDepth.value);
        BlockStateInterface bsi = new BlockStateInterface(ItemicsAPI.getProvider().getPrimaryItemics().getPlayerContext());
        positions.forEach(pos -> {
            BlockState state = bsi.get0((BlockPos)pos);
            VoxelShape shape = state.getShape(player.world, (BlockPos)pos);
            AxisAlignedBB toDraw = shape.isEmpty() ? VoxelShapes.fullCube().getBoundingBox() : shape.getBoundingBox();
            toDraw = toDraw.offset((BlockPos)pos);
            IRenderer.drawAABB(stack, toDraw, 0.002);
        });
        IRenderer.endLines((Boolean)PathRenderer.settings.renderSelectionBoxesIgnoreDepth.value);
    }

    public static void drawDankLitGoalBox(MatrixStack stack, Entity player, Goal goal, float partialTicks, Color color) {
        double maxY;
        double minY;
        double y2;
        double y1;
        double maxZ;
        double minZ;
        double maxX;
        double minX;
        double renderPosX = PathRenderer.posX();
        double renderPosY = PathRenderer.posY();
        double renderPosZ = PathRenderer.posZ();
        double y = (Boolean)PathRenderer.settings.renderGoalAnimated.value == false ? (double)0.999f : (double)MathHelper.cos((float)((double)((float)(System.nanoTime() / 100000L % 20000L) / 20000.0f) * Math.PI * 2.0));
        if (goal instanceof IGoalRenderPos) {
            goalPos = ((IGoalRenderPos)((Object)goal)).getGoalPos();
            minX = (double)((Vector3i)goalPos).getX() + 0.002 - renderPosX;
            maxX = (double)(((Vector3i)goalPos).getX() + 1) - 0.002 - renderPosX;
            minZ = (double)((Vector3i)goalPos).getZ() + 0.002 - renderPosZ;
            maxZ = (double)(((Vector3i)goalPos).getZ() + 1) - 0.002 - renderPosZ;
            if (goal instanceof GoalGetToBlock || goal instanceof GoalTwoBlocks) {
                y /= 2.0;
            }
            y1 = 1.0 + y + (double)((Vector3i)goalPos).getY() - renderPosY;
            y2 = 1.0 - y + (double)((Vector3i)goalPos).getY() - renderPosY;
            minY = (double)((Vector3i)goalPos).getY() - renderPosY;
            maxY = minY + 2.0;
            if (goal instanceof GoalGetToBlock || goal instanceof GoalTwoBlocks) {
                y1 -= 0.5;
                y2 -= 0.5;
                maxY -= 1.0;
            }
        } else if (goal instanceof GoalXZ) {
            goalPos = (GoalXZ)goal;
            if (((Boolean)PathRenderer.settings.renderGoalXZBeacon.value).booleanValue()) {
                GL11.glPushAttrib(64);
                Helper.mc.getTextureManager().bindTexture(TEXTURE_BEACON_BEAM);
                if (((Boolean)PathRenderer.settings.renderGoalIgnoreDepth.value).booleanValue()) {
                    RenderSystem.disableDepthTest();
                }
                stack.push();
                stack.translate((double)((GoalXZ)goalPos).getX() - renderPosX, -renderPosY, (double)((GoalXZ)goalPos).getZ() - renderPosZ);
                BeaconTileEntityRenderer.renderBeamSegment(stack, mc.getRenderTypeBuffers().getBufferSource(), TEXTURE_BEACON_BEAM, (Boolean)PathRenderer.settings.renderGoalAnimated.value != false ? partialTicks : 0.0f, 1.0f, (Boolean)PathRenderer.settings.renderGoalAnimated.value != false ? player.world.getGameTime() : 0L, 0, 256, color.getColorComponents(null), 0.2f, 0.25f);
                stack.pop();
                if (((Boolean)PathRenderer.settings.renderGoalIgnoreDepth.value).booleanValue()) {
                    RenderSystem.enableDepthTest();
                }
                GL11.glPopAttrib();
                return;
            }
            minX = (double)((GoalXZ)goalPos).getX() + 0.002 - renderPosX;
            maxX = (double)(((GoalXZ)goalPos).getX() + 1) - 0.002 - renderPosX;
            minZ = (double)((GoalXZ)goalPos).getZ() + 0.002 - renderPosZ;
            maxZ = (double)(((GoalXZ)goalPos).getZ() + 1) - 0.002 - renderPosZ;
            y1 = 0.0;
            y2 = 0.0;
            minY = 0.0 - renderPosY;
            maxY = 256.0 - renderPosY;
        } else {
            if (goal instanceof GoalComposite) {
                for (Goal g : ((GoalComposite)goal).goals()) {
                    PathRenderer.drawDankLitGoalBox(stack, player, g, partialTicks, color);
                }
                return;
            }
            if (goal instanceof GoalInverted) {
                PathRenderer.drawDankLitGoalBox(stack, player, ((GoalInverted)goal).origin, partialTicks, (Color)PathRenderer.settings.colorInvertedGoalBox.value);
                return;
            }
            if (goal instanceof GoalYLevel) {
                GoalYLevel goalpos = (GoalYLevel)goal;
                minX = player.getPositionVec().x - (Double)PathRenderer.settings.yLevelBoxSize.value - renderPosX;
                minZ = player.getPositionVec().z - (Double)PathRenderer.settings.yLevelBoxSize.value - renderPosZ;
                maxX = player.getPositionVec().x + (Double)PathRenderer.settings.yLevelBoxSize.value - renderPosX;
                maxZ = player.getPositionVec().z + (Double)PathRenderer.settings.yLevelBoxSize.value - renderPosZ;
                minY = (double)((GoalYLevel)goal).level - renderPosY;
                maxY = minY + 2.0;
                y1 = 1.0 + y + (double)goalpos.level - renderPosY;
                y2 = 1.0 - y + (double)goalpos.level - renderPosY;
            } else {
                return;
            }
        }
        IRenderer.startLines(color, ((Float)PathRenderer.settings.goalRenderLineWidthPixels.value).floatValue(), (Boolean)PathRenderer.settings.renderGoalIgnoreDepth.value);
        PathRenderer.renderHorizontalQuad(stack, minX, maxX, minZ, maxZ, y1);
        PathRenderer.renderHorizontalQuad(stack, minX, maxX, minZ, maxZ, y2);
        Matrix4f matrix4f = stack.getLast().getMatrix();
        buffer.begin(1, DefaultVertexFormats.POSITION);
        buffer.pos(matrix4f, (float)minX, (float)minY, (float)minZ).endVertex();
        buffer.pos(matrix4f, (float)minX, (float)maxY, (float)minZ).endVertex();
        buffer.pos(matrix4f, (float)maxX, (float)minY, (float)minZ).endVertex();
        buffer.pos(matrix4f, (float)maxX, (float)maxY, (float)minZ).endVertex();
        buffer.pos(matrix4f, (float)maxX, (float)minY, (float)maxZ).endVertex();
        buffer.pos(matrix4f, (float)maxX, (float)maxY, (float)maxZ).endVertex();
        buffer.pos(matrix4f, (float)minX, (float)minY, (float)maxZ).endVertex();
        buffer.pos(matrix4f, (float)minX, (float)maxY, (float)maxZ).endVertex();
        tessellator.draw();
        IRenderer.endLines((Boolean)PathRenderer.settings.renderGoalIgnoreDepth.value);
    }

    private static void renderHorizontalQuad(MatrixStack stack, double minX, double maxX, double minZ, double maxZ, double y) {
        if (y != 0.0) {
            Matrix4f matrix4f = stack.getLast().getMatrix();
            buffer.begin(2, DefaultVertexFormats.POSITION);
            buffer.pos(matrix4f, (float)minX, (float)y, (float)minZ).endVertex();
            buffer.pos(matrix4f, (float)maxX, (float)y, (float)minZ).endVertex();
            buffer.pos(matrix4f, (float)maxX, (float)y, (float)maxZ).endVertex();
            buffer.pos(matrix4f, (float)minX, (float)y, (float)maxZ).endVertex();
            tessellator.draw();
        }
    }
}
