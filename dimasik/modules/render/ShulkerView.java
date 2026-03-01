package dimasik.modules.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import dimasik.events.api.EventListener;
import dimasik.events.main.render.EventRender2D;
import dimasik.helpers.render.ColorHelpers;
import dimasik.helpers.render.ScreenHelpers;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.utils.math.MathUtils;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

public class ShulkerView
extends Module {
    private final EventListener<EventRender2D> render = this::render;

    public ShulkerView() {
        super("ShulkerView", Category.RENDER);
    }

    public void render(EventRender2D e) {
        for (Entity entity : ShulkerView.mc.world.getAllEntities()) {
            ItemStack stack;
            if (entity instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity)entity;
                if (player.getName().equals(ShulkerView.mc.player.getName()) || !this.isValid(player)) continue;
                stack = player.inventory.getStackInSlot(player.inventory.currentItem);
                if (Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock) {
                    this.renderShulker(stack, player, true);
                }
                for (ItemStack itemStack : player.inventory.mainInventory) {
                    if (!(Block.getBlockFromItem(itemStack.getItem()) instanceof ShulkerBoxBlock)) continue;
                    this.renderShulker(itemStack, player, false);
                }
            }
            if (!(entity instanceof ItemEntity)) continue;
            ItemEntity itemEntity = (ItemEntity)entity;
            if (!this.isValid(entity) || !(Block.getBlockFromItem((stack = itemEntity.getItem()).getItem()) instanceof ShulkerBoxBlock)) continue;
            this.renderShulker(stack, itemEntity, false);
        }
    }

    private void renderShulker(ItemStack stack, Entity entity, boolean isHeldByPlayer) {
        CompoundNBT blocksTag;
        MatrixStack matrixStack = new MatrixStack();
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains("BlockEntityTag", 10) && (blocksTag = tag.getCompound("BlockEntityTag")).contains("Items", 9)) {
            NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
            ItemStackHelper.loadAllItems(blocksTag, items);
            if (!items.isEmpty()) {
                GlStateManager.pushMatrix();
                Vector3d pos = entity.getPositionVec();
                if (isHeldByPlayer) {
                    pos = new Vector3d(pos.x, pos.y, pos.z);
                }
                Direction direction = ShulkerView.mc.player.getHorizontalFacing();
                Vector2f vec = ScreenHelpers.worldToScreen((float)pos.x - (direction.equals(Direction.WEST) ? entity.getWidth() * 0.0f : -(entity.getWidth() * 0.0f)), (float)pos.y + entity.getHeight(), (float)pos.z);
                double scale = MathUtils.getScale(entity.getPositionVec(), 0.35f);
                float startX = vec.x;
                float startY = vec.y;
                GlStateManager.translated(startX, startY, 0.0);
                GlStateManager.scaled(scale, scale, scale);
                float width = 188.0f;
                float height = 68.0f;
                VisualHelpers.drawRoundedRect(matrixStack, 0.0f, 0.0f, width, height, 4.0f, ColorHelpers.rgba(15, 15, 15, 255));
                for (int slot = 0; slot < items.size(); ++slot) {
                    ItemStack item = items.get(slot);
                    if (item.isEmpty()) continue;
                    int row = slot / 9;
                    int col = slot % 9;
                    int itemX = 4 + col * 20;
                    int itemY = 4 + row * 20;
                    mc.getItemRenderer().renderItemAndEffectIntoGUI(item, itemX, itemY);
                    mc.getItemRenderer().renderItemOverlayIntoGUI(ShulkerView.mc.fontRenderer, item, itemX, itemY, null);
                }
                GlStateManager.popMatrix();
            }
        }
    }

    public boolean isInView(Entity ent) {
        if (mc.getRenderViewEntity() == null) {
            return false;
        }
        WorldRenderer.frustum.setCameraPosition(ShulkerView.mc.getRenderManager().info.getProjectedView().x, ShulkerView.mc.getRenderManager().info.getProjectedView().y, ShulkerView.mc.getRenderManager().info.getProjectedView().z);
        return WorldRenderer.frustum.isBoundingBoxInFrustum(ent.getBoundingBox()) || ent.ignoreFrustumCheck;
    }

    public boolean isValid(Entity e) {
        return this.isInView(e);
    }
}
