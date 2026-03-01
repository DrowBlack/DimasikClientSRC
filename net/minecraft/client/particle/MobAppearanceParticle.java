package net.minecraft.client.particle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ElderGuardianRenderer;
import net.minecraft.client.renderer.entity.model.GuardianModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class MobAppearanceParticle
extends Particle {
    private final Model model = new GuardianModel();
    private final RenderType renderType = RenderType.getEntityTranslucent(ElderGuardianRenderer.GUARDIAN_ELDER_TEXTURE);

    private MobAppearanceParticle(ClientWorld world, double x, double y, double z) {
        super(world, x, y, z);
        this.particleGravity = 0.0f;
        this.maxAge = 30;
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.CUSTOM;
    }

    @Override
    public void renderParticle(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks) {
        float f = ((float)this.age + partialTicks) / (float)this.maxAge;
        float f1 = 0.05f + 0.5f * MathHelper.sin(f * (float)Math.PI);
        MatrixStack matrixstack = new MatrixStack();
        matrixstack.rotate(renderInfo.getRotation());
        matrixstack.rotate(Vector3f.XP.rotationDegrees(150.0f * f - 60.0f));
        matrixstack.scale(-1.0f, -1.0f, 1.0f);
        matrixstack.translate(0.0, -1.101f, 1.5);
        IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        IVertexBuilder ivertexbuilder = irendertypebuffer$impl.getBuffer(this.renderType);
        this.model.render(matrixstack, ivertexbuilder, 0xF000F0, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, f1);
        irendertypebuffer$impl.finish();
    }

    public static class Factory
    implements IParticleFactory<BasicParticleType> {
        @Override
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new MobAppearanceParticle(worldIn, x, y, z);
        }
    }
}
