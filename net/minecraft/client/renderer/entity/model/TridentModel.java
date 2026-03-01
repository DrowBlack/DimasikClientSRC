package net.minecraft.client.renderer.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

public class TridentModel
extends Model {
    public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/trident.png");
    private final ModelRenderer modelRenderer = new ModelRenderer(32, 32, 0, 6);

    public TridentModel() {
        super(RenderType::getEntitySolid);
        this.modelRenderer.addBox(-0.5f, 2.0f, -0.5f, 1.0f, 25.0f, 1.0f, 0.0f);
        ModelRenderer modelrenderer = new ModelRenderer(32, 32, 4, 0);
        modelrenderer.addBox(-1.5f, 0.0f, -0.5f, 3.0f, 2.0f, 1.0f);
        this.modelRenderer.addChild(modelrenderer);
        ModelRenderer modelrenderer1 = new ModelRenderer(32, 32, 4, 3);
        modelrenderer1.addBox(-2.5f, -3.0f, -0.5f, 1.0f, 4.0f, 1.0f);
        this.modelRenderer.addChild(modelrenderer1);
        ModelRenderer modelrenderer2 = new ModelRenderer(32, 32, 0, 0);
        modelrenderer2.addBox(-0.5f, -4.0f, -0.5f, 1.0f, 4.0f, 1.0f, 0.0f);
        this.modelRenderer.addChild(modelrenderer2);
        ModelRenderer modelrenderer3 = new ModelRenderer(32, 32, 4, 3);
        modelrenderer3.mirror = true;
        modelrenderer3.addBox(1.5f, -3.0f, -0.5f, 1.0f, 4.0f, 1.0f);
        this.modelRenderer.addChild(modelrenderer3);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
}
