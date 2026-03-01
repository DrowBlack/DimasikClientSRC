package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.Load;
import dimasik.helpers.interfaces.IFastAccess;
import dimasik.modules.render.CustomModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.vector.Vector3f;

public class HeldItemLayer<T extends LivingEntity, M extends EntityModel<T>>
extends LayerRenderer<T, M> {
    public HeldItemLayer(IEntityRenderer<T, M> p_i50934_1_) {
        super(p_i50934_1_);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean flag = ((LivingEntity)entitylivingbaseIn).getPrimaryHand() == HandSide.RIGHT;
        ItemStack itemstack = flag ? ((LivingEntity)entitylivingbaseIn).getHeldItemOffhand() : ((LivingEntity)entitylivingbaseIn).getHeldItemMainhand();
        ItemStack itemstack1 = flag ? ((LivingEntity)entitylivingbaseIn).getHeldItemMainhand() : ((LivingEntity)entitylivingbaseIn).getHeldItemOffhand();
        CustomModel customModel = Load.getInstance().getHooks().getModuleManagers().getCustomModel();
        if (!itemstack.isEmpty() || !itemstack1.isEmpty()) {
            boolean others;
            matrixStackIn.push();
            boolean self = customModel.getElements().getSelected("Self") && entitylivingbaseIn == IFastAccess.mc.player;
            boolean friend = customModel.getElements().getSelected("Friends") && Load.getInstance().getHooks().getFriendManagers().is(((Entity)entitylivingbaseIn).getName().getString());
            boolean bl = others = customModel.getElements().getSelected("Others") && !Load.getInstance().getHooks().getFriendManagers().is(((Entity)entitylivingbaseIn).getName().getString()) && entitylivingbaseIn != IFastAccess.mc.player;
            if (((EntityModel)this.getEntityModel()).isChild) {
                float f = 0.5f;
                matrixStackIn.translate(0.0, 0.75, 0.0);
                matrixStackIn.scale(0.5f, 0.5f, 0.5f);
            } else if (customModel.isToggled() && customModel.getMode().getSelected("Rabbit") && (self || friend || others)) {
                float f = 0.5f;
                matrixStackIn.translate(0.0, 0.3, 0.0);
                matrixStackIn.scale(0.8f, 0.8f, 0.8f);
            } else if (customModel.isToggled() && customModel.getMode().getSelected("Demon") && (self || friend || others)) {
                float f = 0.5f;
                matrixStackIn.translate(0.07, -0.3, 0.06);
                matrixStackIn.scale(1.0f, 1.0f, 1.0f);
            } else if (customModel.isToggled() && customModel.getMode().getSelected("White Demon") && (self || friend || others)) {
                float f = 0.5f;
                matrixStackIn.translate(0.07, -0.3, 0.06);
                matrixStackIn.scale(1.0f, 1.0f, 1.0f);
            } else if (customModel.isToggled() && customModel.getMode().getSelected("Freddy Bear") && (self || friend || others)) {
                float f = 0.5f;
                matrixStackIn.translate(0.07, -0.3, 0.06);
                matrixStackIn.scale(0.79f, 0.9f, 1.15f);
            }
            if (!(customModel.getMode().getSelected("Jeff Killer") || customModel.getMode().getSelected("Chinchilla") || self && friend && others)) {
                this.renderHeldItem((LivingEntity)entitylivingbaseIn, itemstack1, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, HandSide.RIGHT, matrixStackIn, bufferIn, packedLightIn);
                this.renderHeldItem((LivingEntity)entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, HandSide.LEFT, matrixStackIn, bufferIn, packedLightIn);
            }
            matrixStackIn.pop();
        }
    }

    private void renderHeldItem(LivingEntity entity, ItemStack itemStack, ItemCameraTransforms.TransformType transformType, HandSide handSide, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int combinedLightIn) {
        boolean others;
        CustomModel customModel = Load.getInstance().getHooks().getModuleManagers().getCustomModel();
        boolean self = customModel.getElements().getSelected("Self") && entity == IFastAccess.mc.player;
        boolean friend = customModel.getElements().getSelected("Friends") && Load.getInstance().getHooks().getFriendManagers().is(entity.getName().getString());
        boolean bl = others = customModel.getElements().getSelected("Others") && !Load.getInstance().getHooks().getFriendManagers().is(entity.getName().getString()) && entity != IFastAccess.mc.player;
        if (customModel.isToggled() && (self || friend || others)) {
            if (!itemStack.isEmpty()) {
                boolean flag;
                matrixStack.push();
                ((IHasArm)this.getEntityModel()).translateHand(handSide, matrixStack);
                matrixStack.rotate(Vector3f.XP.rotationDegrees(-90.0f));
                matrixStack.rotate(Vector3f.YP.rotationDegrees(180.0f));
                boolean bl2 = flag = handSide == HandSide.LEFT;
                if (transformType == ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND || transformType == ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND) {
                    matrixStack.translate((float)(flag ? -1 : 1) / 6.0f, 0.125, -0.999);
                }
                Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(entity, itemStack, transformType, flag, matrixStack, bufferIn, combinedLightIn);
                matrixStack.pop();
            }
        } else if (!itemStack.isEmpty()) {
            boolean flag;
            matrixStack.push();
            ((IHasArm)this.getEntityModel()).translateHand(handSide, matrixStack);
            matrixStack.rotate(Vector3f.XP.rotationDegrees(-90.0f));
            matrixStack.rotate(Vector3f.YP.rotationDegrees(180.0f));
            boolean bl3 = flag = handSide == HandSide.LEFT;
            if (transformType == ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND || transformType == ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND) {
                matrixStack.translate((float)(flag ? -1 : 1) / 16.0f, 0.125, -0.625);
            }
            Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(entity, itemStack, transformType, flag, matrixStack, bufferIn, combinedLightIn);
            matrixStack.pop();
        }
    }
}
