package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.storage.MapData;
import net.optifine.Config;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import net.optifine.shaders.Shaders;

public class ItemFrameRenderer
extends EntityRenderer<ItemFrameEntity> {
    private static final ModelResourceLocation LOCATION_MODEL = new ModelResourceLocation("item_frame", "map=false");
    private static final ModelResourceLocation LOCATION_MODEL_MAP = new ModelResourceLocation("item_frame", "map=true");
    private final Minecraft mc = Minecraft.getInstance();
    private final ItemRenderer itemRenderer;
    private static double itemRenderDistanceSq = 4096.0;

    public ItemFrameRenderer(EntityRendererManager renderManagerIn, ItemRenderer itemRendererIn) {
        super(renderManagerIn);
        this.itemRenderer = itemRendererIn;
    }

    @Override
    public void render(ItemFrameEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        ItemStack itemstack;
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.push();
        Direction direction = entityIn.getHorizontalFacing();
        Vector3d vector3d = this.getRenderOffset(entityIn, partialTicks);
        matrixStackIn.translate(-vector3d.getX(), -vector3d.getY(), -vector3d.getZ());
        double d0 = 0.46875;
        matrixStackIn.translate((double)direction.getXOffset() * 0.46875, (double)direction.getYOffset() * 0.46875, (double)direction.getZOffset() * 0.46875);
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(entityIn.rotationPitch));
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0f - entityIn.rotationYaw));
        boolean flag = entityIn.isInvisible();
        if (!flag) {
            BlockRendererDispatcher blockrendererdispatcher = this.mc.getBlockRendererDispatcher();
            ModelManager modelmanager = blockrendererdispatcher.getBlockModelShapes().getModelManager();
            ModelResourceLocation modelresourcelocation = entityIn.getDisplayedItem().getItem() instanceof FilledMapItem ? LOCATION_MODEL_MAP : LOCATION_MODEL;
            matrixStackIn.push();
            matrixStackIn.translate(-0.5, -0.5, -0.5);
            blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(matrixStackIn.getLast(), bufferIn.getBuffer(Atlases.getSolidBlockType()), null, modelmanager.getModel(modelresourcelocation), 1.0f, 1.0f, 1.0f, packedLightIn, OverlayTexture.NO_OVERLAY);
            matrixStackIn.pop();
        }
        if (!(itemstack = entityIn.getDisplayedItem()).isEmpty()) {
            boolean flag1 = itemstack.getItem() instanceof FilledMapItem;
            if (flag) {
                matrixStackIn.translate(0.0, 0.0, 0.5);
            } else {
                matrixStackIn.translate(0.0, 0.0, 0.4375);
            }
            int i = flag1 ? entityIn.getRotation() % 4 * 2 : entityIn.getRotation();
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees((float)i * 360.0f / 8.0f));
            if (!Reflector.postForgeBusEvent(Reflector.RenderItemInFrameEvent_Constructor, entityIn, this, matrixStackIn, bufferIn, packedLightIn)) {
                if (flag1) {
                    matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(180.0f));
                    float f = 0.0078125f;
                    matrixStackIn.scale(0.0078125f, 0.0078125f, 0.0078125f);
                    matrixStackIn.translate(-64.0, -64.0, 0.0);
                    MapData mapdata = ReflectorForge.getMapData(itemstack, entityIn.world);
                    matrixStackIn.translate(0.0, 0.0, -1.0);
                    if (mapdata != null) {
                        this.mc.gameRenderer.getMapItemRenderer().renderMap(matrixStackIn, bufferIn, mapdata, true, packedLightIn);
                    }
                } else {
                    matrixStackIn.scale(0.5f, 0.5f, 0.5f);
                    if (this.isRenderItem(entityIn)) {
                        this.itemRenderer.renderItem(itemstack, ItemCameraTransforms.TransformType.FIXED, packedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn);
                    }
                }
            }
        }
        matrixStackIn.pop();
    }

    @Override
    public Vector3d getRenderOffset(ItemFrameEntity entityIn, float partialTicks) {
        return new Vector3d((float)entityIn.getHorizontalFacing().getXOffset() * 0.3f, -0.25, (float)entityIn.getHorizontalFacing().getZOffset() * 0.3f);
    }

    @Override
    public ResourceLocation getEntityTexture(ItemFrameEntity entity) {
        return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
    }

    @Override
    protected boolean canRenderName(ItemFrameEntity entity) {
        if (Minecraft.isGuiEnabled() && !entity.getDisplayedItem().isEmpty() && entity.getDisplayedItem().hasDisplayName() && this.renderManager.pointedEntity == entity) {
            double d0 = this.renderManager.squareDistanceTo(entity);
            float f = entity.isDiscrete() ? 32.0f : 64.0f;
            return d0 < (double)(f * f);
        }
        return false;
    }

    @Override
    protected void renderName(ItemFrameEntity entityIn, ITextComponent displayNameIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        super.renderName(entityIn, entityIn.getDisplayedItem().getDisplayName(), matrixStackIn, bufferIn, packedLightIn);
    }

    private boolean isRenderItem(ItemFrameEntity p_isRenderItem_1_) {
        Entity entity;
        double d0;
        if (Shaders.isShadowPass) {
            return false;
        }
        return Config.zoomMode || !((d0 = p_isRenderItem_1_.getDistanceSq((entity = this.mc.getRenderViewEntity()).getPosX(), entity.getPosY(), entity.getPosZ())) > itemRenderDistanceSq);
    }

    public static void updateItemRenderDistance() {
        Minecraft minecraft = Minecraft.getInstance();
        double d0 = Config.limit(minecraft.gameSettings.fov, 1.0, 120.0);
        double d1 = Math.max(6.0 * (double)minecraft.getMainWindow().getHeight() / d0, 16.0);
        itemRenderDistanceSq = d1 * d1;
    }
}
