package dimasik.managers.mods.cape.wavecapes.renderlayers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import dimasik.Load;
import dimasik.managers.client.ClientManagers;
import dimasik.managers.mods.cape.wavecapes.CapeMovement;
import dimasik.managers.mods.cape.wavecapes.CapeRenderer;
import dimasik.managers.mods.cape.wavecapes.VanillaCapeRenderer;
import dimasik.managers.mods.cape.wavecapes.WindMode;
import dimasik.managers.mods.cape.wavecapes.math.Vector3;
import dimasik.managers.mods.cape.wavecapes.sim.StickSimulation;
import dimasik.modules.render.CustomModel;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.optifine.Config;

public class CustomCapeRenderLayer
extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {
    private static int partCount;
    private ModelRenderer[] customCape = new ModelRenderer[partCount];
    private static final VanillaCapeRenderer vanillaCape;
    private static final int scale = 3600000;

    public CustomCapeRenderLayer(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> renderLayerParent) {
        super(renderLayerParent);
        partCount = 16;
        this.buildMesh();
    }

    private void buildMesh() {
        this.customCape = new ModelRenderer[partCount];
        for (int i = 0; i < partCount; ++i) {
            ModelRenderer base = new ModelRenderer(64, 32, 0, i);
            this.customCape[i] = base.addBox(-5.0f, i, -1.0f, 10.0f, 1.0f, 1.0f);
        }
    }

    @Override
    public void render(MatrixStack poseStack, IRenderTypeBuffer multiBufferSource, int i, AbstractClientPlayerEntity abstractClientPlayer, float f, float g, float delta, float j, float k, float l) {
        CapeRenderer renderer = this.getCapeRenderer(abstractClientPlayer, multiBufferSource);
        if (renderer == null) {
            return;
        }
        ItemStack itemStack = abstractClientPlayer.getItemStackFromSlot(EquipmentSlotType.CHEST);
        if (itemStack.getItem() == Items.ELYTRA) {
            return;
        }
        if (ClientManagers.isUnHook()) {
            return;
        }
        if (abstractClientPlayer.hasCustomCape()) {
            abstractClientPlayer.updateSimulation(abstractClientPlayer, partCount);
            if (renderer.vanillaUvValues()) {
                this.renderSmoothCape(poseStack, multiBufferSource, renderer, abstractClientPlayer, delta, i);
            } else {
                ModelRenderer[] parts = this.customCape;
                for (int part = 0; part < partCount; ++part) {
                    ModelRenderer model = parts[part];
                    this.modifyPoseStack(poseStack, abstractClientPlayer, delta, part);
                    renderer.render(abstractClientPlayer, part, model, poseStack, multiBufferSource, i, OverlayTexture.NO_OVERLAY);
                    poseStack.pop();
                }
            }
        } else {
            this.renderVanillaCape(poseStack, multiBufferSource, i, abstractClientPlayer, f, g, delta, j, k, l);
        }
    }

    public void renderVanillaCape(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemstack;
        if (player.hasPlayerInfo() && !player.isInvisible() && player.isWearing(PlayerModelPart.CAPE) && player.getLocationCape() != null && (itemstack = player.getItemStackFromSlot(EquipmentSlotType.CHEST)).getItem() != Items.ELYTRA) {
            matrixStackIn.push();
            matrixStackIn.translate(0.0, 0.0, 0.125);
            double d0 = MathHelper.lerp((double)partialTicks, player.prevChasingPosX, player.chasingPosX) - MathHelper.lerp((double)partialTicks, player.prevPosX, player.getPosX());
            double d1 = MathHelper.lerp((double)partialTicks, player.prevChasingPosY, player.chasingPosY) - MathHelper.lerp((double)partialTicks, player.prevPosY, player.getPosY());
            double d2 = MathHelper.lerp((double)partialTicks, player.prevChasingPosZ, player.chasingPosZ) - MathHelper.lerp((double)partialTicks, player.prevPosZ, player.getPosZ());
            float f = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset);
            double d3 = MathHelper.sin(f * ((float)Math.PI / 180));
            double d4 = -MathHelper.cos(f * ((float)Math.PI / 180));
            float f1 = (float)d1 * 10.0f;
            f1 = MathHelper.clamp(f1, -6.0f, 32.0f);
            float f2 = (float)(d0 * d3 + d2 * d4) * 100.0f;
            f2 = MathHelper.clamp(f2, 0.0f, 150.0f);
            float f3 = (float)(d0 * d4 - d2 * d3) * 100.0f;
            f3 = MathHelper.clamp(f3, -20.0f, 20.0f);
            if (f2 < 0.0f) {
                f2 = 0.0f;
            }
            if (f2 > 165.0f) {
                f2 = 165.0f;
            }
            if (f1 < -5.0f) {
                f1 = -5.0f;
            }
            float f4 = MathHelper.lerp(partialTicks, player.prevCameraYaw, player.cameraYaw);
            f1 += MathHelper.sin(MathHelper.lerp(partialTicks, player.prevDistanceWalkedModified, player.distanceWalkedModified) * 6.0f) * 32.0f * f4;
            if (player.isCrouching()) {
                f1 += 25.0f;
            }
            float f5 = Config.getAverageFrameTimeSec() * 20.0f;
            f5 = Config.limit(f5, 0.02f, 1.0f);
            player.capeRotateX = MathHelper.lerp(f5, player.capeRotateX, 6.0f + f2 / 2.0f + f1);
            player.capeRotateZ = MathHelper.lerp(f5, player.capeRotateZ, f3 / 2.0f);
            player.capeRotateY = MathHelper.lerp(f5, player.capeRotateY, 180.0f - f3 / 2.0f);
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(player.capeRotateX));
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(player.capeRotateZ));
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(player.capeRotateY));
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEntitySolid(player.getLocationCape()));
            ((PlayerModel)this.getEntityModel()).renderCape(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY);
            matrixStackIn.pop();
        }
    }

    private void renderSmoothCape(MatrixStack poseStack, IRenderTypeBuffer multiBufferSource, CapeRenderer capeRenderer, AbstractClientPlayerEntity abstractClientPlayer, float delta, int light) {
        IVertexBuilder bufferBuilder = capeRenderer.getVertexConsumer(multiBufferSource, abstractClientPlayer);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        Matrix4f oldPositionMatrix = null;
        for (int part = 0; part < partCount; ++part) {
            CustomModel customModel;
            this.modifyPoseStack(poseStack, abstractClientPlayer, delta, part);
            if (oldPositionMatrix == null) {
                oldPositionMatrix = poseStack.getLast().getMatrix();
            }
            if ((customModel = Load.getInstance().getHooks().getModuleManagers().getCustomModel()).isToggled() && customModel.getMode().getSelected("Rabbit") && customModel.getElements().getSelected("Self")) {
                if (part == 0) {
                    CustomCapeRenderLayer.addTopVertex(bufferBuilder, poseStack.getLast().getMatrix(), oldPositionMatrix, 0.3f, 0.0f, 0.0f, -0.3f, 0.0f, -0.06f, part, light);
                }
                if (part == partCount - 1) {
                    CustomCapeRenderLayer.addBottomVertex(bufferBuilder, poseStack.getLast().getMatrix(), poseStack.getLast().getMatrix(), 0.3f, (float)(part + 1) * (0.76f / (float)partCount), 0.0f, -0.3f, (float)(part + 1) * (0.76f / (float)partCount), -0.06f, part, light);
                }
                CustomCapeRenderLayer.addLeftVertex(bufferBuilder, poseStack.getLast().getMatrix(), oldPositionMatrix, -0.3f, (float)(part + 1) * (0.76f / (float)partCount), 0.0f, -0.3f, (float)part * (0.76f / (float)partCount), -0.06f, part, light);
                CustomCapeRenderLayer.addRightVertex(bufferBuilder, poseStack.getLast().getMatrix(), oldPositionMatrix, 0.3f, (float)(part + 1) * (0.76f / (float)partCount), 0.0f, 0.3f, (float)part * (0.76f / (float)partCount), -0.06f, part, light);
                CustomCapeRenderLayer.addBackVertex(bufferBuilder, poseStack.getLast().getMatrix(), oldPositionMatrix, 0.3f, (float)(part + 1) * (0.76f / (float)partCount), -0.06f, -0.3f, (float)part * (0.76f / (float)partCount), -0.06f, part, light);
                CustomCapeRenderLayer.addFrontVertex(bufferBuilder, oldPositionMatrix, poseStack.getLast().getMatrix(), 0.3f, (float)(part + 1) * (0.76f / (float)partCount), 0.0f, -0.3f, (float)part * (0.76f / (float)partCount), 0.0f, part, light);
            } else {
                if (part == 0) {
                    CustomCapeRenderLayer.addTopVertex(bufferBuilder, poseStack.getLast().getMatrix(), oldPositionMatrix, 0.3f, 0.0f, 0.0f, -0.3f, 0.0f, -0.06f, part, light);
                }
                if (part == partCount - 1) {
                    CustomCapeRenderLayer.addBottomVertex(bufferBuilder, poseStack.getLast().getMatrix(), poseStack.getLast().getMatrix(), 0.3f, (float)(part + 1) * (0.96f / (float)partCount), 0.0f, -0.3f, (float)(part + 1) * (0.96f / (float)partCount), -0.06f, part, light);
                }
                CustomCapeRenderLayer.addLeftVertex(bufferBuilder, poseStack.getLast().getMatrix(), oldPositionMatrix, -0.3f, (float)(part + 1) * (0.96f / (float)partCount), 0.0f, -0.3f, (float)part * (0.96f / (float)partCount), -0.06f, part, light);
                CustomCapeRenderLayer.addRightVertex(bufferBuilder, poseStack.getLast().getMatrix(), oldPositionMatrix, 0.3f, (float)(part + 1) * (0.96f / (float)partCount), 0.0f, 0.3f, (float)part * (0.96f / (float)partCount), -0.06f, part, light);
                CustomCapeRenderLayer.addBackVertex(bufferBuilder, poseStack.getLast().getMatrix(), oldPositionMatrix, 0.3f, (float)(part + 1) * (0.96f / (float)partCount), -0.06f, -0.3f, (float)part * (0.96f / (float)partCount), -0.06f, part, light);
                CustomCapeRenderLayer.addFrontVertex(bufferBuilder, oldPositionMatrix, poseStack.getLast().getMatrix(), 0.3f, (float)(part + 1) * (0.96f / (float)partCount), 0.0f, -0.3f, (float)part * (0.96f / (float)partCount), 0.0f, part, light);
            }
            oldPositionMatrix = poseStack.getLast().getMatrix().copy();
            poseStack.pop();
        }
    }

    private void modifyPoseStack(MatrixStack poseStack, AbstractClientPlayerEntity abstractClientPlayer, float h, int part) {
        if (CapeMovement.BASIC_SIMULATION == CapeMovement.BASIC_SIMULATION) {
            this.modifyPoseStackSimulation(poseStack, abstractClientPlayer, h, part);
            return;
        }
        this.modifyPoseStackVanilla(poseStack, abstractClientPlayer, h, part);
    }

    private void modifyPoseStackSimulation(MatrixStack poseStack, AbstractClientPlayerEntity abstractClientPlayer, float delta, int part) {
        StickSimulation simulation = abstractClientPlayer.getSimulation();
        poseStack.push();
        ItemStack itemStack = abstractClientPlayer.getItemStackFromSlot(EquipmentSlotType.CHEST);
        double z1 = !itemStack.isEmpty() ? 0.15 : 0.125;
        poseStack.translate(0.0, 0.0, z1);
        StickSimulation.Point capePoint = simulation.getPoints().get(0);
        float x = simulation.getPoints().get(part).getLerpX(delta) - capePoint.getLerpX(delta);
        if (x > 0.0f) {
            x = 0.0f;
        }
        float y = capePoint.getLerpY(delta) - (float)part - simulation.getPoints().get(part).getLerpY(delta);
        float z = capePoint.getLerpZ(delta) - simulation.getPoints().get(part).getLerpZ(delta);
        float sidewaysRotationOffset = 0.0f;
        float partRotation = this.getRotation(delta, part, simulation);
        float height = 0.0f;
        CustomModel customModel = Load.getInstance().getHooks().getModuleManagers().getCustomModel();
        if (customModel.isToggled() && customModel.getMode().getSelected("Rabbit") && customModel.getElements().getSelected("Self")) {
            poseStack.translate(0.0, 0.7000000059604645, 0.0);
        } else if (abstractClientPlayer.isCrouching()) {
            height += 25.0f;
            poseStack.translate(0.0, 0.15f, 0.0);
        }
        float naturalWindSwing = this.getNatrualWindSwing(part, abstractClientPlayer.canSwim());
        poseStack.rotate(Vector3f.XP.rotationDegrees(6.0f + height + naturalWindSwing));
        poseStack.rotate(Vector3f.ZP.rotationDegrees(sidewaysRotationOffset / 2.0f));
        poseStack.rotate(Vector3f.YP.rotationDegrees(180.0f - sidewaysRotationOffset / 2.0f));
        poseStack.translate(-z / (float)partCount, y / (float)partCount, x / (float)partCount);
        poseStack.translate(0.0, 0.03, -0.03);
        poseStack.translate(0.0, (float)part * 1.0f / (float)partCount, 0.0);
        poseStack.rotate(Vector3f.XP.rotationDegrees(-partRotation));
        poseStack.translate(0.0, (float)(-part) * 1.0f / (float)partCount, 0.0);
        poseStack.translate(0.0, -0.03, 0.03);
    }

    private float getRotation(float delta, int part, StickSimulation simulation) {
        if (part == partCount - 1) {
            return this.getRotation(delta, part - 1, simulation);
        }
        return (float)this.getAngle(simulation.points.get(part).getLerpedPos(delta), simulation.points.get(part + 1).getLerpedPos(delta));
    }

    private double getAngle(Vector3 a, Vector3 b) {
        Vector3 angle = b.subtract(a);
        return Math.toDegrees(Math.atan2(angle.x, angle.y)) + 180.0;
    }

    private void modifyPoseStackVanilla(MatrixStack poseStack, AbstractClientPlayerEntity abstractClientPlayer, float h, int part) {
        poseStack.push();
        poseStack.translate(0.0, 0.0, 0.125);
        double d = MathHelper.lerp((double)h, abstractClientPlayer.prevChasingPosX, abstractClientPlayer.chasingPosX) - MathHelper.lerp((double)h, abstractClientPlayer.prevPosX, abstractClientPlayer.getPosX());
        double e = MathHelper.lerp((double)h, abstractClientPlayer.prevChasingPosY, abstractClientPlayer.chasingPosY) - MathHelper.lerp((double)h, abstractClientPlayer.prevPosY, abstractClientPlayer.getPosY());
        double m = MathHelper.lerp((double)h, abstractClientPlayer.prevChasingPosZ, abstractClientPlayer.chasingPosZ) - MathHelper.lerp((double)h, abstractClientPlayer.prevPosZ, abstractClientPlayer.getPosZ());
        float n = abstractClientPlayer.prevRenderYawOffset + abstractClientPlayer.renderYawOffset - abstractClientPlayer.prevRenderYawOffset;
        double o = MathHelper.sin(n * ((float)Math.PI / 180));
        double p = -MathHelper.cos(n * ((float)Math.PI / 180));
        float height = (float)e * 10.0f;
        height = MathHelper.clamp(height, -6.0f, 32.0f);
        float swing = (float)(d * o + m * p) * CustomCapeRenderLayer.easeOutSine(1.0f / (float)partCount * (float)part) * 100.0f;
        swing = MathHelper.clamp(swing, 0.0f, 150.0f * CustomCapeRenderLayer.easeOutSine(1.0f / (float)partCount * (float)part));
        float sidewaysRotationOffset = (float)(d * p - m * o) * 100.0f;
        sidewaysRotationOffset = MathHelper.clamp(sidewaysRotationOffset, -20.0f, 20.0f);
        float t = MathHelper.lerp(h, abstractClientPlayer.prevCameraYaw, abstractClientPlayer.cameraYaw);
        height += MathHelper.sin(MathHelper.lerp(h, abstractClientPlayer.prevDistanceWalkedModified, abstractClientPlayer.distanceWalkedModified) * 6.0f) * 32.0f * t;
        if (abstractClientPlayer.isCrouching()) {
            height += 25.0f;
            poseStack.translate(0.0, 0.15f, 0.0);
        }
        float naturalWindSwing = this.getNatrualWindSwing(part, abstractClientPlayer.canSwim());
        poseStack.rotate(Vector3f.XP.rotationDegrees(6.0f + swing / 2.0f + height + naturalWindSwing));
        poseStack.rotate(Vector3f.ZP.rotationDegrees(sidewaysRotationOffset / 2.0f));
        poseStack.rotate(Vector3f.YP.rotationDegrees(180.0f - sidewaysRotationOffset / 2.0f));
    }

    private float getNatrualWindSwing(int part, boolean underwater) {
        long highlightedPart = System.currentTimeMillis() / (long)(underwater ? 9 : 3) % 360L;
        float relativePart = (float)(part + 1) / (float)partCount;
        if (WindMode.WAVES == WindMode.WAVES) {
            return (float)(Math.sin(Math.toRadians(relativePart * 360.0f - (float)highlightedPart)) * 3.0);
        }
        return 0.0f;
    }

    private static void addBackVertex(IVertexBuilder bufferBuilder, Matrix4f matrix, Matrix4f oldMatrix, float x1, float y1, float z1, float x2, float y2, float z2, int part, int light) {
        float i;
        if (x1 < x2) {
            i = x1;
            x1 = x2;
            x2 = i;
        }
        if (y1 < y2) {
            i = y1;
            y1 = y2;
            y2 = i;
            Matrix4f k = matrix;
            matrix = oldMatrix;
            oldMatrix = k;
        }
        float minU = 0.015625f;
        float maxU = 0.171875f;
        float minV = 0.03125f;
        float maxV = 0.53125f;
        float deltaV = maxV - minV;
        float vPerPart = deltaV / (float)partCount;
        maxV = minV + vPerPart * (float)(part + 1);
        bufferBuilder.pos(oldMatrix, x1, y2, z1).color(1.0f, 1.0f, 1.0f, 1.0f).tex(maxU, minV += vPerPart * (float)part).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(1.0f, 0.0f, 0.0f).endVertex();
        bufferBuilder.pos(oldMatrix, x2, y2, z1).color(1.0f, 1.0f, 1.0f, 1.0f).tex(minU, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(1.0f, 0.0f, 0.0f).endVertex();
        bufferBuilder.pos(matrix, x2, y1, z2).color(1.0f, 1.0f, 1.0f, 1.0f).tex(minU, maxV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(1.0f, 0.0f, 0.0f).endVertex();
        bufferBuilder.pos(matrix, x1, y1, z2).color(1.0f, 1.0f, 1.0f, 1.0f).tex(maxU, maxV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(1.0f, 0.0f, 0.0f).endVertex();
    }

    private static void addFrontVertex(IVertexBuilder bufferBuilder, Matrix4f matrix, Matrix4f oldMatrix, float x1, float y1, float z1, float x2, float y2, float z2, int part, int light) {
        float i;
        if (x1 < x2) {
            i = x1;
            x1 = x2;
            x2 = i;
        }
        if (y1 < y2) {
            i = y1;
            y1 = y2;
            y2 = i;
            Matrix4f k = matrix;
            matrix = oldMatrix;
            oldMatrix = k;
        }
        float minU = 0.1875f;
        float maxU = 0.34375f;
        float minV = 0.03125f;
        float maxV = 0.53125f;
        float deltaV = maxV - minV;
        float vPerPart = deltaV / (float)partCount;
        maxV = minV + vPerPart * (float)(part + 1);
        bufferBuilder.pos(oldMatrix, x1, y1, z1).color(1.0f, 1.0f, 1.0f, 1.0f).tex(maxU, maxV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(1.0f, 0.0f, 0.0f).endVertex();
        bufferBuilder.pos(oldMatrix, x2, y1, z1).color(1.0f, 1.0f, 1.0f, 1.0f).tex(minU, maxV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(1.0f, 0.0f, 0.0f).endVertex();
        bufferBuilder.pos(matrix, x2, y2, z2).color(1.0f, 1.0f, 1.0f, 1.0f).tex(minU, minV += vPerPart * (float)part).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(1.0f, 0.0f, 0.0f).endVertex();
        bufferBuilder.pos(matrix, x1, y2, z2).color(1.0f, 1.0f, 1.0f, 1.0f).tex(maxU, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(1.0f, 0.0f, 0.0f).endVertex();
    }

    private static void addLeftVertex(IVertexBuilder bufferBuilder, Matrix4f matrix, Matrix4f oldMatrix, float x1, float y1, float z1, float x2, float y2, float z2, int part, int light) {
        float i;
        if (x1 < x2) {
            i = x1;
            x1 = x2;
            x2 = i;
        }
        if (y1 < y2) {
            i = y1;
            y1 = y2;
            y2 = i;
        }
        float minU = 0.0f;
        float maxU = 0.015625f;
        float minV = 0.03125f;
        float maxV = 0.53125f;
        float deltaV = maxV - minV;
        float vPerPart = deltaV / (float)partCount;
        maxV = minV + vPerPart * (float)(part + 1);
        bufferBuilder.pos(matrix, x2, y1, z1).color(1.0f, 1.0f, 1.0f, 1.0f).tex(maxU, maxV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(1.0f, 0.0f, 0.0f).endVertex();
        bufferBuilder.pos(matrix, x2, y1, z2).color(1.0f, 1.0f, 1.0f, 1.0f).tex(minU, maxV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(1.0f, 0.0f, 0.0f).endVertex();
        bufferBuilder.pos(oldMatrix, x2, y2, z2).color(1.0f, 1.0f, 1.0f, 1.0f).tex(minU, minV += vPerPart * (float)part).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(1.0f, 0.0f, 0.0f).endVertex();
        bufferBuilder.pos(oldMatrix, x2, y2, z1).color(1.0f, 1.0f, 1.0f, 1.0f).tex(maxU, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(1.0f, 0.0f, 0.0f).endVertex();
    }

    private static void addRightVertex(IVertexBuilder bufferBuilder, Matrix4f matrix, Matrix4f oldMatrix, float x1, float y1, float z1, float x2, float y2, float z2, int part, int light) {
        float i;
        if (x1 < x2) {
            i = x1;
            x1 = x2;
            x2 = i;
        }
        if (y1 < y2) {
            i = y1;
            y1 = y2;
            y2 = i;
        }
        float minU = 0.171875f;
        float maxU = 0.1875f;
        float minV = 0.03125f;
        float maxV = 0.53125f;
        float deltaV = maxV - minV;
        float vPerPart = deltaV / (float)partCount;
        maxV = minV + vPerPart * (float)(part + 1);
        bufferBuilder.pos(matrix, x2, y1, z2).color(1.0f, 1.0f, 1.0f, 1.0f).tex(minU, maxV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(1.0f, 0.0f, 0.0f).endVertex();
        bufferBuilder.pos(matrix, x2, y1, z1).color(1.0f, 1.0f, 1.0f, 1.0f).tex(maxU, maxV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(1.0f, 0.0f, 0.0f).endVertex();
        bufferBuilder.pos(oldMatrix, x2, y2, z1).color(1.0f, 1.0f, 1.0f, 1.0f).tex(maxU, minV += vPerPart * (float)part).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(1.0f, 0.0f, 0.0f).endVertex();
        bufferBuilder.pos(oldMatrix, x2, y2, z2).color(1.0f, 1.0f, 1.0f, 1.0f).tex(minU, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(1.0f, 0.0f, 0.0f).endVertex();
    }

    private static void addBottomVertex(IVertexBuilder bufferBuilder, Matrix4f matrix, Matrix4f oldMatrix, float x1, float y1, float z1, float x2, float y2, float z2, int part, int light) {
        float i;
        if (x1 < x2) {
            i = x1;
            x1 = x2;
            x2 = i;
        }
        if (y1 < y2) {
            i = y1;
            y1 = y2;
            y2 = i;
        }
        float minU = 0.171875f;
        float maxU = 0.328125f;
        float minV = 0.0f;
        float maxV = 0.03125f;
        float deltaV = maxV - minV;
        float vPerPart = deltaV / (float)partCount;
        maxV = minV + vPerPart * (float)(part + 1);
        bufferBuilder.pos(oldMatrix, x1, y2, z2).color(1.0f, 1.0f, 1.0f, 1.0f).tex(maxU, minV += vPerPart * (float)part).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(1.0f, 0.0f, 0.0f).endVertex();
        bufferBuilder.pos(oldMatrix, x2, y2, z2).color(1.0f, 1.0f, 1.0f, 1.0f).tex(minU, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(1.0f, 0.0f, 0.0f).endVertex();
        bufferBuilder.pos(matrix, x2, y1, z1).color(1.0f, 1.0f, 1.0f, 1.0f).tex(minU, maxV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(1.0f, 0.0f, 0.0f).endVertex();
        bufferBuilder.pos(matrix, x1, y1, z1).color(1.0f, 1.0f, 1.0f, 1.0f).tex(maxU, maxV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(1.0f, 0.0f, 0.0f).endVertex();
    }

    private static void addTopVertex(IVertexBuilder bufferBuilder, Matrix4f matrix, Matrix4f oldMatrix, float x1, float y1, float z1, float x2, float y2, float z2, int part, int light) {
        float i;
        if (x1 < x2) {
            i = x1;
            x1 = x2;
            x2 = i;
        }
        if (y1 < y2) {
            i = y1;
            y1 = y2;
            y2 = i;
        }
        float minU = 0.015625f;
        float maxU = 0.171875f;
        float minV = 0.0f;
        float maxV = 0.03125f;
        float deltaV = maxV - minV;
        float vPerPart = deltaV / (float)partCount;
        maxV = minV + vPerPart * (float)(part + 1);
        bufferBuilder.pos(oldMatrix, x1, y2, z1).color(1.0f, 1.0f, 1.0f, 1.0f).tex(maxU, maxV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(0.0f, 1.0f, 0.0f).endVertex();
        bufferBuilder.pos(oldMatrix, x2, y2, z1).color(1.0f, 1.0f, 1.0f, 1.0f).tex(minU, maxV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(0.0f, 1.0f, 0.0f).endVertex();
        bufferBuilder.pos(matrix, x2, y1, z2).color(1.0f, 1.0f, 1.0f, 1.0f).tex(minU, minV += vPerPart * (float)part).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(0.0f, 1.0f, 0.0f).endVertex();
        bufferBuilder.pos(matrix, x1, y1, z2).color(1.0f, 1.0f, 1.0f, 1.0f).tex(maxU, minV).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(0.0f, 1.0f, 0.0f).endVertex();
    }

    private CapeRenderer getCapeRenderer(AbstractClientPlayerEntity abstractClientPlayer, IRenderTypeBuffer multiBufferSource) {
        if (!abstractClientPlayer.hasPlayerInfo() || !abstractClientPlayer.isWearing(PlayerModelPart.CAPE) || abstractClientPlayer.getLocationCape() == null) {
            return null;
        }
        CustomCapeRenderLayer.vanillaCape.vertexConsumer = multiBufferSource.getBuffer(RenderType.getEntityCutout(abstractClientPlayer.getLocationCape()));
        return vanillaCape;
    }

    private static float getWind(double posY) {
        float x = (float)(System.currentTimeMillis() % 3600000L) / 10000.0f;
        float mod = MathHelper.clamp(0.005f * (float)posY, 0.0f, 1.0f);
        return MathHelper.clamp((float)(Math.sin(2.0f * x) + Math.sin(Math.PI * (double)x)) * mod, 0.0f, 2.0f);
    }

    private static float easeOutSine(float x) {
        return (float)Math.sin((double)x * Math.PI / 2.0);
    }

    static {
        vanillaCape = new VanillaCapeRenderer();
    }
}
