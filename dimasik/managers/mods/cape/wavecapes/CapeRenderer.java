package dimasik.managers.mods.cape.wavecapes;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ModelRenderer;

public interface CapeRenderer {
    public void render(AbstractClientPlayerEntity var1, int var2, ModelRenderer var3, MatrixStack var4, IRenderTypeBuffer var5, int var6, int var7);

    default public IVertexBuilder getVertexConsumer(IRenderTypeBuffer multiBufferSource, AbstractClientPlayerEntity player) {
        return null;
    }

    public boolean vanillaUvValues();
}
