package net.optifine.entity.model;

import java.util.Map;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.renderer.entity.model.GenericHeadModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.SkullTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntityType;
import net.optifine.Config;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.reflect.Reflector;

public class ModelAdapterHeadSkeleton
extends ModelAdapter {
    public ModelAdapterHeadSkeleton() {
        super(TileEntityType.SKULL, "head_skeleton", 0.0f);
    }

    @Override
    public Model makeModel() {
        return new GenericHeadModel(0, 0, 64, 32);
    }

    @Override
    public ModelRenderer getModelRenderer(Model model, String modelPart) {
        if (!(model instanceof GenericHeadModel)) {
            return null;
        }
        GenericHeadModel genericheadmodel = (GenericHeadModel)model;
        return modelPart.equals("head") ? (ModelRenderer)Reflector.ModelGenericHead_skeletonHead.getValue(genericheadmodel) : null;
    }

    @Override
    public String[] getModelRendererNames() {
        return new String[]{"head"};
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        Map map;
        TileEntityRendererDispatcher tileentityrendererdispatcher = TileEntityRendererDispatcher.instance;
        TileEntityRenderer tileentityrenderer = tileentityrendererdispatcher.getRenderer(TileEntityType.SKULL);
        if (!(tileentityrenderer instanceof SkullTileEntityRenderer)) {
            return null;
        }
        if (tileentityrenderer.getType() == null) {
            tileentityrenderer = new SkullTileEntityRenderer(tileentityrendererdispatcher);
        }
        if ((map = (Map)Reflector.TileEntitySkullRenderer_MODELS.getValue()) == null) {
            Config.warn("Field not found: TileEntitySkullRenderer.MODELS");
            return null;
        }
        map.put(SkullBlock.Types.SKELETON, modelBase);
        return tileentityrenderer;
    }
}
