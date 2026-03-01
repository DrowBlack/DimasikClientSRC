package net.minecraft.client.renderer;

import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import dimasik.Load;
import dimasik.events.main.visual.EventHand;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShootableItem;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.storage.MapData;
import net.optifine.Config;
import net.optifine.CustomItems;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.Shaders;

public class FirstPersonRenderer {
    private static final RenderType MAP_BACKGROUND = RenderType.getText(new ResourceLocation("textures/map/map_background.png"));
    private static final RenderType MAP_BACKGROUND_CHECKERBOARD = RenderType.getText(new ResourceLocation("textures/map/map_background_checkerboard.png"));
    private final Minecraft mc;
    private ItemStack itemStackMainHand = ItemStack.EMPTY;
    private ItemStack itemStackOffHand = ItemStack.EMPTY;
    private float equippedProgressMainHand;
    private float prevEquippedProgressMainHand;
    private float equippedProgressOffHand;
    private float prevEquippedProgressOffHand;
    private final EntityRendererManager renderManager;
    private final ItemRenderer itemRenderer;

    public FirstPersonRenderer(Minecraft mcIn) {
        this.mc = mcIn;
        this.renderManager = mcIn.getRenderManager();
        this.itemRenderer = mcIn.getItemRenderer();
    }

    public void renderItemSide(LivingEntity livingEntityIn, ItemStack itemStackIn, ItemCameraTransforms.TransformType transformTypeIn, boolean leftHand, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn) {
        CustomItems.setRenderOffHand(leftHand);
        if (!itemStackIn.isEmpty()) {
            this.itemRenderer.renderItem(livingEntityIn, itemStackIn, transformTypeIn, leftHand, matrixStackIn, bufferIn, livingEntityIn.world, combinedLightIn, OverlayTexture.NO_OVERLAY);
        }
        CustomItems.setRenderOffHand(false);
    }

    private float getMapAngleFromPitch(float pitch) {
        float f = 1.0f - pitch / 45.0f + 0.1f;
        f = MathHelper.clamp(f, 0.0f, 1.0f);
        return -MathHelper.cos(f * (float)Math.PI) * 0.5f + 0.5f;
    }

    private void renderArm(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, HandSide side) {
        this.mc.getTextureManager().bindTexture(this.mc.player.getLocationSkin());
        PlayerRenderer playerrenderer = (PlayerRenderer)this.renderManager.getRenderer(this.mc.player);
        matrixStackIn.push();
        float f = side == HandSide.RIGHT ? 1.0f : -1.0f;
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(92.0f));
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(45.0f));
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(f * -41.0f));
        matrixStackIn.translate(f * 0.3f, -1.1f, 0.45f);
        if (side == HandSide.RIGHT) {
            playerrenderer.renderRightArm(matrixStackIn, bufferIn, combinedLightIn, this.mc.player);
        } else {
            playerrenderer.renderLeftArm(matrixStackIn, bufferIn, combinedLightIn, this.mc.player);
        }
        matrixStackIn.pop();
    }

    private void renderMapFirstPersonSide(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, float equippedProgress, HandSide handIn, float swingProgress, ItemStack stack) {
        float f = handIn == HandSide.RIGHT ? 1.0f : -1.0f;
        matrixStackIn.translate(f * 0.125f, -0.125, 0.0);
        if (!this.mc.player.isInvisible()) {
            matrixStackIn.push();
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(f * 10.0f));
            this.renderArmFirstPerson(matrixStackIn, bufferIn, combinedLightIn, equippedProgress, swingProgress, handIn);
            matrixStackIn.pop();
        }
        matrixStackIn.push();
        matrixStackIn.translate(f * 0.51f, -0.08f + equippedProgress * -1.2f, -0.75);
        float f1 = MathHelper.sqrt(swingProgress);
        float f2 = MathHelper.sin(f1 * (float)Math.PI);
        float f3 = -0.5f * f2;
        float f4 = 0.4f * MathHelper.sin(f1 * ((float)Math.PI * 2));
        float f5 = -0.3f * MathHelper.sin(swingProgress * (float)Math.PI);
        matrixStackIn.translate(f * f3, f4 - 0.3f * f2, f5);
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(f2 * -45.0f));
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f * f2 * -30.0f));
        this.renderMapFirstPerson(matrixStackIn, bufferIn, combinedLightIn, stack);
        matrixStackIn.pop();
    }

    private void renderMapFirstPerson(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, float pitch, float equippedProgress, float swingProgress) {
        float f = MathHelper.sqrt(swingProgress);
        float f1 = -0.2f * MathHelper.sin(swingProgress * (float)Math.PI);
        float f2 = -0.4f * MathHelper.sin(f * (float)Math.PI);
        matrixStackIn.translate(0.0, -f1 / 2.0f, f2);
        float f3 = this.getMapAngleFromPitch(pitch);
        matrixStackIn.translate(0.0, 0.04f + equippedProgress * -1.2f + f3 * -0.5f, -0.72f);
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(f3 * -85.0f));
        if (!this.mc.player.isInvisible()) {
            matrixStackIn.push();
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90.0f));
            this.renderArm(matrixStackIn, bufferIn, combinedLightIn, HandSide.RIGHT);
            this.renderArm(matrixStackIn, bufferIn, combinedLightIn, HandSide.LEFT);
            matrixStackIn.pop();
        }
        float f4 = MathHelper.sin(f * (float)Math.PI);
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(f4 * 20.0f));
        matrixStackIn.scale(2.0f, 2.0f, 2.0f);
        this.renderMapFirstPerson(matrixStackIn, bufferIn, combinedLightIn, this.itemStackMainHand);
    }

    private void renderMapFirstPerson(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, ItemStack stack) {
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0f));
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(180.0f));
        matrixStackIn.scale(0.38f, 0.38f, 0.38f);
        matrixStackIn.translate(-0.5, -0.5, 0.0);
        matrixStackIn.scale(0.0078125f, 0.0078125f, 0.0078125f);
        MapData mapdata = FilledMapItem.getMapData(stack, this.mc.world);
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(mapdata == null ? MAP_BACKGROUND : MAP_BACKGROUND_CHECKERBOARD);
        Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
        ivertexbuilder.pos(matrix4f, -7.0f, 135.0f, 0.0f).color(255, 255, 255, 255).tex(0.0f, 1.0f).lightmap(combinedLightIn).endVertex();
        ivertexbuilder.pos(matrix4f, 135.0f, 135.0f, 0.0f).color(255, 255, 255, 255).tex(1.0f, 1.0f).lightmap(combinedLightIn).endVertex();
        ivertexbuilder.pos(matrix4f, 135.0f, -7.0f, 0.0f).color(255, 255, 255, 255).tex(1.0f, 0.0f).lightmap(combinedLightIn).endVertex();
        ivertexbuilder.pos(matrix4f, -7.0f, -7.0f, 0.0f).color(255, 255, 255, 255).tex(0.0f, 0.0f).lightmap(combinedLightIn).endVertex();
        if (mapdata != null) {
            this.mc.gameRenderer.getMapItemRenderer().renderMap(matrixStackIn, bufferIn, mapdata, false, combinedLightIn);
        }
    }

    private void renderArmFirstPerson(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, float equippedProgress, float swingProgress, HandSide side) {
        boolean flag = side != HandSide.LEFT;
        float f = flag ? 1.0f : -1.0f;
        float f1 = MathHelper.sqrt(swingProgress);
        float f2 = -0.3f * MathHelper.sin(f1 * (float)Math.PI);
        float f3 = 0.4f * MathHelper.sin(f1 * ((float)Math.PI * 2));
        float f4 = -0.4f * MathHelper.sin(swingProgress * (float)Math.PI);
        matrixStackIn.translate(f * (f2 + 0.64000005f), f3 + -0.6f + equippedProgress * -0.6f, f4 + -0.71999997f);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f * 45.0f));
        float f5 = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        float f6 = MathHelper.sin(f1 * (float)Math.PI);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f * f6 * 70.0f));
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(f * f5 * -20.0f));
        ClientPlayerEntity abstractclientplayerentity = this.mc.player;
        this.mc.getTextureManager().bindTexture(abstractclientplayerentity.getLocationSkin());
        matrixStackIn.translate(f * -1.0f, 3.6f, 3.5);
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(f * 120.0f));
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(200.0f));
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f * -135.0f));
        matrixStackIn.translate(f * 5.6f, 0.0, 0.0);
        PlayerRenderer playerrenderer = (PlayerRenderer)this.renderManager.getRenderer(abstractclientplayerentity);
        if (flag) {
            playerrenderer.renderRightArm(matrixStackIn, bufferIn, combinedLightIn, abstractclientplayerentity);
        } else {
            playerrenderer.renderLeftArm(matrixStackIn, bufferIn, combinedLightIn, abstractclientplayerentity);
        }
    }

    private void transformEatFirstPerson(MatrixStack matrixStackIn, float partialTicks, HandSide handIn, ItemStack stack) {
        float f = (float)this.mc.player.getItemInUseCount() - partialTicks + 1.0f;
        float f1 = f / (float)stack.getUseDuration();
        if (f1 < 0.8f) {
            float f2 = MathHelper.abs(MathHelper.cos(f / 4.0f * (float)Math.PI) * 0.1f);
            matrixStackIn.translate(0.0, f2, 0.0);
        }
        float f3 = 1.0f - (float)Math.pow(f1, 27.0);
        int i = handIn == HandSide.RIGHT ? 1 : -1;
        matrixStackIn.translate(f3 * 0.6f * (float)i, f3 * -0.5f, f3 * 0.0f);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees((float)i * f3 * 90.0f));
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(f3 * 10.0f));
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees((float)i * f3 * 30.0f));
    }

    private void transformFirstPerson(MatrixStack matrixStackIn, HandSide handIn, float swingProgress) {
        int i = handIn == HandSide.RIGHT ? 1 : -1;
        float f = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees((float)i * (45.0f + f * -20.0f)));
        float f1 = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees((float)i * f1 * -20.0f));
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(f1 * -80.0f));
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees((float)i * -45.0f));
    }

    private void transformSideFirstPerson(MatrixStack matrixStackIn, HandSide handIn, float equippedProg) {
        int i = handIn == HandSide.RIGHT ? 1 : -1;
        EventHand.Update eventHand = new EventHand.Update(true);
        Load.getInstance().getEvents().call(eventHand);
        float yOffset = eventHand.isUpdate() ? -0.6f : 0.0f;
        matrixStackIn.translate((float)i * 0.56f, -0.52f + equippedProg * yOffset, -0.72f);
    }

    public void renderItemInFirstPerson(float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, ClientPlayerEntity playerEntityIn, int combinedLightIn) {
        float f = playerEntityIn.getSwingProgress(partialTicks);
        Hand hand = MoreObjects.firstNonNull(playerEntityIn.swingingHand, Hand.MAIN_HAND);
        float f1 = MathHelper.lerp(partialTicks, playerEntityIn.prevRotationPitch, playerEntityIn.rotationPitch);
        boolean flag = true;
        boolean flag1 = true;
        if (playerEntityIn.isHandActive()) {
            ItemStack itemstack1;
            Hand hand1;
            ItemStack itemstack = playerEntityIn.getActiveItemStack();
            if (itemstack.getItem() instanceof ShootableItem) {
                flag = playerEntityIn.getActiveHand() == Hand.MAIN_HAND;
                boolean bl = flag1 = !flag;
            }
            if ((hand1 = playerEntityIn.getActiveHand()) == Hand.MAIN_HAND && (itemstack1 = playerEntityIn.getHeldItemOffhand()).getItem() instanceof CrossbowItem && CrossbowItem.isCharged(itemstack1)) {
                flag1 = false;
            }
        } else {
            ItemStack itemstack2 = playerEntityIn.getHeldItemMainhand();
            ItemStack itemstack3 = playerEntityIn.getHeldItemOffhand();
            if (itemstack2.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(itemstack2)) {
                boolean bl = flag1 = !flag;
            }
            if (itemstack3.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(itemstack3)) {
                flag = !itemstack2.isEmpty();
                flag1 = !flag;
            }
        }
        float f3 = MathHelper.lerp(partialTicks, playerEntityIn.prevRenderArmPitch, playerEntityIn.renderArmPitch);
        float f4 = MathHelper.lerp(partialTicks, playerEntityIn.prevRenderArmYaw, playerEntityIn.renderArmYaw);
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees((playerEntityIn.getPitch(partialTicks) - f3) * 0.1f));
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees((playerEntityIn.getYaw(partialTicks) - f4) * 0.1f));
        if (flag) {
            float f5 = hand == Hand.MAIN_HAND ? f : 0.0f;
            float f2 = 1.0f - MathHelper.lerp(partialTicks, this.prevEquippedProgressMainHand, this.equippedProgressMainHand);
            if (!Reflector.ForgeHooksClient_renderSpecificFirstPersonHand.exists() || !Reflector.callBoolean(Reflector.ForgeHooksClient_renderSpecificFirstPersonHand, new Object[]{Hand.MAIN_HAND, matrixStackIn, bufferIn, combinedLightIn, Float.valueOf(partialTicks), Float.valueOf(f1), Float.valueOf(f5), Float.valueOf(f2), this.itemStackMainHand})) {
                this.renderItemInFirstPerson(playerEntityIn, partialTicks, f1, Hand.MAIN_HAND, f5, this.itemStackMainHand, f2, matrixStackIn, bufferIn, combinedLightIn);
            }
        }
        if (flag1) {
            float f6 = hand == Hand.OFF_HAND ? f : 0.0f;
            float f7 = 1.0f - MathHelper.lerp(partialTicks, this.prevEquippedProgressOffHand, this.equippedProgressOffHand);
            if (!Reflector.ForgeHooksClient_renderSpecificFirstPersonHand.exists() || !Reflector.callBoolean(Reflector.ForgeHooksClient_renderSpecificFirstPersonHand, new Object[]{Hand.OFF_HAND, matrixStackIn, bufferIn, combinedLightIn, Float.valueOf(partialTicks), Float.valueOf(f1), Float.valueOf(f6), Float.valueOf(f7), this.itemStackOffHand})) {
                this.renderItemInFirstPerson(playerEntityIn, partialTicks, f1, Hand.OFF_HAND, f6, this.itemStackOffHand, f7, matrixStackIn, bufferIn, combinedLightIn);
            }
        }
        bufferIn.finish();
    }

    private void renderItemInFirstPerson(AbstractClientPlayerEntity player, float partialTicks, float pitch, Hand handIn, float swingProgress, ItemStack stack, float equippedProgress, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn) {
        if (!Config.isShaders() || !Shaders.isSkipRenderHand(handIn)) {
            boolean flag = handIn == Hand.MAIN_HAND;
            HandSide handside = flag ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
            matrixStackIn.push();
            EventHand.Position position = new EventHand.Position(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
            Load.getInstance().getEvents().call(position);
            if (stack.isEmpty()) {
                if (!player.isInvisible() && flag) {
                    matrixStackIn.translate(position.getRightX(), position.getRightY(), position.getRightZ());
                    this.renderArmFirstPerson(matrixStackIn, bufferIn, combinedLightIn, equippedProgress, swingProgress, handside);
                }
            } else if (stack.getItem() instanceof FilledMapItem) {
                if (flag && this.itemStackOffHand.isEmpty()) {
                    this.renderMapFirstPerson(matrixStackIn, bufferIn, combinedLightIn, pitch, equippedProgress, swingProgress);
                } else {
                    this.renderMapFirstPersonSide(matrixStackIn, bufferIn, combinedLightIn, equippedProgress, handside, swingProgress, stack);
                }
            } else if (stack.getItem() instanceof CrossbowItem) {
                int i;
                boolean flag1 = CrossbowItem.isCharged(stack);
                boolean flag2 = handside == HandSide.RIGHT;
                int n = i = flag2 ? 1 : -1;
                if (player.isHandActive() && player.getItemInUseCount() > 0 && player.getActiveHand() == handIn) {
                    this.transformSideFirstPerson(matrixStackIn, handside, equippedProgress);
                    matrixStackIn.translate((float)i * -0.4785682f, -0.094387f, 0.05731530860066414);
                    matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-11.935f));
                    matrixStackIn.rotate(Vector3f.YP.rotationDegrees((float)i * 65.3f));
                    matrixStackIn.rotate(Vector3f.ZP.rotationDegrees((float)i * -9.785f));
                    float f9 = (float)stack.getUseDuration() - ((float)this.mc.player.getItemInUseCount() - partialTicks + 1.0f);
                    float f12 = f9 / (float)CrossbowItem.getChargeTime(stack);
                    if (f12 > 1.0f) {
                        f12 = 1.0f;
                    }
                    if (f12 > 0.1f) {
                        float f15 = MathHelper.sin((f9 - 0.1f) * 1.3f);
                        float f3 = f12 - 0.1f;
                        float f4 = f15 * f3;
                        matrixStackIn.translate(f4 * 0.0f, f4 * 0.004f, f4 * 0.0f);
                    }
                    matrixStackIn.translate(f12 * 0.0f, f12 * 0.0f, f12 * 0.04f);
                    matrixStackIn.scale(1.0f, 1.0f, 1.0f + f12 * 0.2f);
                    matrixStackIn.rotate(Vector3f.YN.rotationDegrees((float)i * 45.0f));
                } else {
                    float f = -0.4f * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
                    float f1 = 0.2f * MathHelper.sin(MathHelper.sqrt(swingProgress) * ((float)Math.PI * 2));
                    float f2 = -0.2f * MathHelper.sin(swingProgress * (float)Math.PI);
                    matrixStackIn.translate((float)i * f, f1, f2);
                    this.transformSideFirstPerson(matrixStackIn, handside, equippedProgress);
                    this.transformFirstPerson(matrixStackIn, handside, swingProgress);
                    if (flag1 && swingProgress < 0.001f) {
                        matrixStackIn.translate((float)i * -0.641864f, 0.0, 0.0);
                        matrixStackIn.rotate(Vector3f.YP.rotationDegrees((float)i * 10.0f));
                    }
                }
                this.renderItemSide(player, stack, flag2 ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !flag2, matrixStackIn, bufferIn, combinedLightIn);
            } else {
                boolean flag3;
                boolean bl = flag3 = handside == HandSide.RIGHT;
                if (flag3) {
                    matrixStackIn.translate(position.getRightX(), position.getRightY(), position.getRightZ());
                } else {
                    matrixStackIn.translate(position.getLeftX(), position.getLeftY(), position.getLeftZ());
                }
                if (player.isHandActive() && player.getItemInUseCount() > 0 && player.getActiveHand() == handIn) {
                    int k = flag3 ? 1 : -1;
                    switch (stack.getUseAction()) {
                        case NONE: {
                            this.transformSideFirstPerson(matrixStackIn, handside, equippedProgress);
                            break;
                        }
                        case EAT: 
                        case DRINK: {
                            this.transformEatFirstPerson(matrixStackIn, partialTicks, handside, stack);
                            this.transformSideFirstPerson(matrixStackIn, handside, equippedProgress);
                            break;
                        }
                        case BLOCK: {
                            this.transformSideFirstPerson(matrixStackIn, handside, equippedProgress);
                            break;
                        }
                        case BOW: {
                            this.transformSideFirstPerson(matrixStackIn, handside, equippedProgress);
                            matrixStackIn.translate((float)k * -0.2785682f, 0.18344387412071228, 0.15731531381607056);
                            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-13.935f));
                            matrixStackIn.rotate(Vector3f.YP.rotationDegrees((float)k * 35.3f));
                            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees((float)k * -9.785f));
                            float f8 = (float)stack.getUseDuration() - ((float)this.mc.player.getItemInUseCount() - partialTicks + 1.0f);
                            float f11 = f8 / 20.0f;
                            f11 = (f11 * f11 + f11 * 2.0f) / 3.0f;
                            if (f11 > 1.0f) {
                                f11 = 1.0f;
                            }
                            if (f11 > 0.1f) {
                                float f14 = MathHelper.sin((f8 - 0.1f) * 1.3f);
                                float f17 = f11 - 0.1f;
                                float f19 = f14 * f17;
                                matrixStackIn.translate(f19 * 0.0f, f19 * 0.004f, f19 * 0.0f);
                            }
                            matrixStackIn.translate(f11 * 0.0f, f11 * 0.0f, f11 * 0.04f);
                            matrixStackIn.scale(1.0f, 1.0f, 1.0f + f11 * 0.2f);
                            matrixStackIn.rotate(Vector3f.YN.rotationDegrees((float)k * 45.0f));
                            break;
                        }
                        case SPEAR: {
                            this.transformSideFirstPerson(matrixStackIn, handside, equippedProgress);
                            matrixStackIn.translate((float)k * -0.5f, 0.7f, 0.1f);
                            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-55.0f));
                            matrixStackIn.rotate(Vector3f.YP.rotationDegrees((float)k * 35.3f));
                            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees((float)k * -9.785f));
                            float f13 = (float)stack.getUseDuration() - ((float)this.mc.player.getItemInUseCount() - partialTicks + 1.0f);
                            float f16 = f13 / 10.0f;
                            if (f16 > 1.0f) {
                                f16 = 1.0f;
                            }
                            if (f16 > 0.1f) {
                                float f18 = MathHelper.sin((f13 - 0.1f) * 1.3f);
                                float f20 = f16 - 0.1f;
                                float f5 = f18 * f20;
                                matrixStackIn.translate(f5 * 0.0f, f5 * 0.004f, f5 * 0.0f);
                            }
                            matrixStackIn.translate(0.0, 0.0, f16 * 0.2f);
                            matrixStackIn.scale(1.0f, 1.0f, 1.0f + f16 * 0.2f);
                            matrixStackIn.rotate(Vector3f.YN.rotationDegrees((float)k * 45.0f));
                        }
                    }
                } else if (player.isSpinAttacking()) {
                    this.transformSideFirstPerson(matrixStackIn, handside, equippedProgress);
                    int j = flag3 ? 1 : -1;
                    matrixStackIn.translate((float)j * -0.4f, 0.8f, 0.3f);
                    matrixStackIn.rotate(Vector3f.YP.rotationDegrees((float)j * 65.0f));
                    matrixStackIn.rotate(Vector3f.ZP.rotationDegrees((float)j * -85.0f));
                } else {
                    float f6 = -0.4f * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
                    float f7 = 0.2f * MathHelper.sin(MathHelper.sqrt(swingProgress) * ((float)Math.PI * 2));
                    float f10 = -0.2f * MathHelper.sin(swingProgress * (float)Math.PI);
                    float anim = (float)Math.sin((double)swingProgress * 1.5707963267948966 * 2.0);
                    float sin1 = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
                    float sin2 = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
                    int l = flag3 ? 1 : -1;
                    EventHand.Update update = new EventHand.Update(true);
                    Load.getInstance().getEvents().call(update);
                    if (update.isUpdate()) {
                        matrixStackIn.translate((float)l * f6, f7, f10);
                    }
                    this.transformSideFirstPerson(matrixStackIn, handside, equippedProgress);
                    EventHand.Animation eventHand = new EventHand.Animation(matrixStackIn, swingProgress);
                    if (handside == HandSide.RIGHT) {
                        Load.getInstance().getEvents().call(eventHand);
                    }
                    if (update.isUpdate()) {
                        this.transformFirstPerson(eventHand.getMatrixStack(), handside, swingProgress);
                    }
                }
                this.renderItemSide(player, stack, flag3 ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !flag3, matrixStackIn, bufferIn, combinedLightIn);
            }
            matrixStackIn.pop();
        }
    }

    public void tick() {
        this.prevEquippedProgressMainHand = this.equippedProgressMainHand;
        this.prevEquippedProgressOffHand = this.equippedProgressOffHand;
        ClientPlayerEntity clientplayerentity = this.mc.player;
        ItemStack itemstack = clientplayerentity.getHeldItemMainhand();
        ItemStack itemstack1 = clientplayerentity.getHeldItemOffhand();
        if (ItemStack.areItemStacksEqual(this.itemStackMainHand, itemstack)) {
            this.itemStackMainHand = itemstack;
        }
        if (ItemStack.areItemStacksEqual(this.itemStackOffHand, itemstack1)) {
            this.itemStackOffHand = itemstack1;
        }
        if (clientplayerentity.isRowingBoat()) {
            this.equippedProgressMainHand = MathHelper.clamp(this.equippedProgressMainHand - 0.4f, 0.0f, 1.0f);
            this.equippedProgressOffHand = MathHelper.clamp(this.equippedProgressOffHand - 0.4f, 0.0f, 1.0f);
        } else {
            float f = clientplayerentity.getCooledAttackStrength(1.0f);
            if (Reflector.ForgeHooksClient_shouldCauseReequipAnimation.exists()) {
                boolean flag = Reflector.callBoolean(Reflector.ForgeHooksClient_shouldCauseReequipAnimation, this.itemStackMainHand, itemstack, clientplayerentity.inventory.currentItem);
                boolean flag1 = Reflector.callBoolean(Reflector.ForgeHooksClient_shouldCauseReequipAnimation, this.itemStackOffHand, itemstack1, -1);
                if (!flag && !Objects.equals(this.itemStackMainHand, itemstack)) {
                    this.itemStackMainHand = itemstack;
                }
                if (!flag1 && !Objects.equals(this.itemStackOffHand, itemstack1)) {
                    this.itemStackOffHand = itemstack1;
                }
            }
            this.equippedProgressMainHand += MathHelper.clamp((this.itemStackMainHand == itemstack ? f * f * f : 0.0f) - this.equippedProgressMainHand, -0.4f, 0.4f);
            this.equippedProgressOffHand += MathHelper.clamp((float)(this.itemStackOffHand == itemstack1 ? 1 : 0) - this.equippedProgressOffHand, -0.4f, 0.4f);
        }
        if (this.equippedProgressMainHand < 0.1f) {
            this.itemStackMainHand = itemstack;
            if (Config.isShaders()) {
                Shaders.setItemToRenderMain(this.itemStackMainHand);
            }
        }
        if (this.equippedProgressOffHand < 0.1f) {
            this.itemStackOffHand = itemstack1;
            if (Config.isShaders()) {
                Shaders.setItemToRenderOff(this.itemStackOffHand);
            }
        }
    }

    public void resetEquippedProgress(Hand hand) {
        if (hand == Hand.MAIN_HAND) {
            this.equippedProgressMainHand = 0.0f;
        } else {
            this.equippedProgressOffHand = 0.0f;
        }
    }
}
