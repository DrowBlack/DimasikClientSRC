package net.minecraft.client.renderer;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.SortedMap;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.OutlineLayerBuffer;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.util.Util;

public class RenderTypeBuffers {
    private final RegionRenderCacheBuilder fixedBuilder = new RegionRenderCacheBuilder();
    private final SortedMap<RenderType, BufferBuilder> fixedBuffers = Util.make(new Object2ObjectLinkedOpenHashMap(), p_228485_1_ -> {
        p_228485_1_.put(Atlases.getSolidBlockType(), this.fixedBuilder.getBuilder(RenderType.getSolid()));
        p_228485_1_.put(Atlases.getCutoutBlockType(), this.fixedBuilder.getBuilder(RenderType.getCutout()));
        p_228485_1_.put(Atlases.getBannerType(), this.fixedBuilder.getBuilder(RenderType.getCutoutMipped()));
        p_228485_1_.put(Atlases.getTranslucentCullBlockType(), this.fixedBuilder.getBuilder(RenderType.getTranslucent()));
        RenderTypeBuffers.put(p_228485_1_, Atlases.getShieldType());
        RenderTypeBuffers.put(p_228485_1_, Atlases.getBedType());
        RenderTypeBuffers.put(p_228485_1_, Atlases.getShulkerBoxType());
        RenderTypeBuffers.put(p_228485_1_, Atlases.getSignType());
        RenderTypeBuffers.put(p_228485_1_, Atlases.getChestType());
        RenderTypeBuffers.put(p_228485_1_, RenderType.getTranslucentNoCrumbling());
        RenderTypeBuffers.put(p_228485_1_, RenderType.getArmorGlint());
        RenderTypeBuffers.put(p_228485_1_, RenderType.getArmorEntityGlint());
        RenderTypeBuffers.put(p_228485_1_, RenderType.getGlint());
        RenderTypeBuffers.put(p_228485_1_, RenderType.getGlintDirect());
        RenderTypeBuffers.put(p_228485_1_, RenderType.getGlintTranslucent());
        RenderTypeBuffers.put(p_228485_1_, RenderType.getEntityGlint());
        RenderTypeBuffers.put(p_228485_1_, RenderType.getEntityGlintDirect());
        RenderTypeBuffers.put(p_228485_1_, RenderType.getWaterMask());
        ModelBakery.DESTROY_RENDER_TYPES.forEach(p_228488_1_ -> RenderTypeBuffers.put(p_228485_1_, p_228488_1_));
    });
    private final IRenderTypeBuffer.Impl bufferSource = IRenderTypeBuffer.getImpl(this.fixedBuffers, new BufferBuilder(256));
    private final IRenderTypeBuffer.Impl crumblingBufferSource = IRenderTypeBuffer.getImpl(new BufferBuilder(256));
    private final OutlineLayerBuffer outlineBufferSource = new OutlineLayerBuffer(this.bufferSource);

    private static void put(Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder> mapBuildersIn, RenderType renderTypeIn) {
        mapBuildersIn.put(renderTypeIn, new BufferBuilder(renderTypeIn.getBufferSize()));
    }

    public RegionRenderCacheBuilder getFixedBuilder() {
        return this.fixedBuilder;
    }

    public IRenderTypeBuffer.Impl getBufferSource() {
        return this.bufferSource;
    }

    public IRenderTypeBuffer.Impl getCrumblingBufferSource() {
        return this.crumblingBufferSource;
    }

    public OutlineLayerBuffer getOutlineBufferSource() {
        return this.outlineBufferSource;
    }
}
