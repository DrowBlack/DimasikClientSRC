package net.minecraft.client.renderer.entity.layers;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dimasik.Load;
import dimasik.helpers.render.ColorHelpers;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.SkullTileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;

public class HeadLayer<T extends LivingEntity, M extends EntityModel<T>>
extends LayerRenderer<T, M> {
    private final float field_239402_a_;
    private final float field_239403_b_;
    private final float field_239404_c_;

    public HeadLayer(IEntityRenderer<T, M> p_i50946_1_) {
        this(p_i50946_1_, 1.0f, 1.0f, 1.0f);
    }

    public HeadLayer(IEntityRenderer<T, M> p_i232475_1_, float p_i232475_2_, float p_i232475_3_, float p_i232475_4_) {
        super(p_i232475_1_);
        this.field_239402_a_ = p_i232475_2_;
        this.field_239403_b_ = p_i232475_3_;
        this.field_239404_c_ = p_i232475_4_;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        PlayerEntity player;
        ItemStack itemstack = ((LivingEntity)entitylivingbaseIn).getItemStackFromSlot(EquipmentSlotType.HEAD);
        if (!itemstack.isEmpty()) {
            boolean flag;
            Item item = itemstack.getItem();
            matrixStackIn.push();
            matrixStackIn.scale(this.field_239402_a_, this.field_239403_b_, this.field_239404_c_);
            boolean bl = flag = entitylivingbaseIn instanceof VillagerEntity || entitylivingbaseIn instanceof ZombieVillagerEntity;
            if (((LivingEntity)entitylivingbaseIn).isChild() && !(entitylivingbaseIn instanceof VillagerEntity)) {
                matrixStackIn.translate(0.0, 0.03125, 0.0);
                matrixStackIn.scale(0.7f, 0.7f, 0.7f);
                matrixStackIn.translate(0.0, 1.0, 0.0);
            }
            ((IHasHead)this.getEntityModel()).getModelHead().translateRotate(matrixStackIn);
            if (item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof AbstractSkullBlock) {
                float f3 = 1.1875f;
                matrixStackIn.scale(f3, -f3, -f3);
                if (flag) {
                    matrixStackIn.translate(0.0, 0.0625, 0.0);
                }
                GameProfile gameprofile = null;
                if (itemstack.hasTag()) {
                    String s;
                    CompoundNBT compoundnbt = itemstack.getTag();
                    if (compoundnbt.contains("SkullOwner", 10)) {
                        gameprofile = NBTUtil.readGameProfile(compoundnbt.getCompound("SkullOwner"));
                    } else if (compoundnbt.contains("SkullOwner", 8) && !StringUtils.isBlank(s = compoundnbt.getString("SkullOwner"))) {
                        gameprofile = SkullTileEntity.updateGameProfile(new GameProfile(null, s));
                        compoundnbt.put("SkullOwner", NBTUtil.writeGameProfile(new CompoundNBT(), gameprofile));
                    }
                }
                matrixStackIn.translate(-0.5, 0.0, -0.5);
                SkullTileEntityRenderer.render(null, 180.0f, ((AbstractSkullBlock)((BlockItem)item).getBlock()).getSkullType(), gameprofile, limbSwing, matrixStackIn, bufferIn, packedLightIn);
            } else if (!(item instanceof ArmorItem) || ((ArmorItem)item).getEquipmentSlot() != EquipmentSlotType.HEAD) {
                float f2 = 0.625f;
                matrixStackIn.translate(0.0, -0.25, 0.0);
                matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0f));
                matrixStackIn.scale(f2, -f2, -f2);
                if (flag) {
                    matrixStackIn.translate(0.0, 0.1875, 0.0);
                }
                Minecraft.getInstance().getFirstPersonRenderer().renderItemSide((LivingEntity)entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.HEAD, false, matrixStackIn, bufferIn, packedLightIn);
            }
            matrixStackIn.pop();
        }
        if (Load.getInstance().getHooks().getModuleManagers().getChinaHat().isToggled() && entitylivingbaseIn instanceof PlayerEntity && ((player = (PlayerEntity)entitylivingbaseIn) instanceof ClientPlayerEntity || Load.getInstance().getHooks().getFriendManagers().is(TextFormatting.getTextWithoutFormattingCodes(player.getName().getString())))) {
            int i;
            float width = player.getWidth();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            GlStateManager.enableDepthTest();
            GlStateManager.disableTexture();
            GlStateManager.enableBlend();
            RenderSystem.defaultBlendFunc();
            GlStateManager.disableCull();
            GlStateManager.shadeModel(7425);
            GL11.glEnable(2848);
            GlStateManager.lineWidth(1.0f);
            matrixStackIn.push();
            float offset = player.inventory.armorInventory.get(3).isEmpty() ? -0.41f : -0.5f;
            ((IHasHead)this.getEntityModel()).getModelHead().translateRotate(matrixStackIn);
            matrixStackIn.translate(0.0, offset, 0.0);
            matrixStackIn.rotate(Vector3f.ZN.rotationDegrees(180.0f));
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90.0f));
            buffer.begin(6, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(matrixStackIn.getLast().getMatrix(), 0.0f, 0.3f, 0.0f).color(ColorHelpers.setAlphaColor(ColorHelpers.getThemeColor(2), 150.0f)).endVertex();
            int size = 360;
            for (i = 0; i <= size; ++i) {
                buffer.pos(matrixStackIn.getLast().getMatrix(), -MathHelper.sin((float)i * ((float)Math.PI * 2) / (float)size) * width, 0.0f, MathHelper.cos((float)i * ((float)Math.PI * 2) / (float)size) * width).color(ColorHelpers.setAlphaColor(ColorHelpers.getThemeColor(1), 150.0f)).endVertex();
            }
            tessellator.draw();
            buffer.begin(2, DefaultVertexFormats.POSITION_COLOR);
            size = 360;
            for (i = 0; i <= size; ++i) {
                buffer.pos(matrixStackIn.getLast().getMatrix(), -MathHelper.sin((float)i * ((float)Math.PI * 2) / (float)size) * width, 0.0f, MathHelper.cos((float)i * ((float)Math.PI * 2) / (float)size) * width).color(ColorHelpers.setAlphaColor(ColorHelpers.getThemeColor(1), 150.0f)).endVertex();
            }
            GlStateManager.depthMask(false);
            tessellator.draw();
            GlStateManager.depthMask(true);
            matrixStackIn.pop();
            GlStateManager.disableDepthTest();
            GlStateManager.disableBlend();
            GlStateManager.enableTexture();
            GlStateManager.shadeModel(7424);
            GlStateManager.enableCull();
        }
    }
}
