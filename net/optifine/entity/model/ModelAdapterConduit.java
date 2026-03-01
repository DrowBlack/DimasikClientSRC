package net.optifine.entity.model;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.ConduitTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntityType;
import net.optifine.Config;
import net.optifine.entity.model.ConduitModel;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;

public class ModelAdapterConduit
extends ModelAdapter {
    public ModelAdapterConduit() {
        super(TileEntityType.CONDUIT, "conduit", 0.0f);
    }

    @Override
    public Model makeModel() {
        return new ConduitModel();
    }

    @Override
    public ModelRenderer getModelRenderer(Model model, String modelPart) {
        if (!(model instanceof ConduitModel)) {
            return null;
        }
        ConduitModel conduitmodel = (ConduitModel)model;
        if (modelPart.equals("eye")) {
            return conduitmodel.eye;
        }
        if (modelPart.equals("wind")) {
            return conduitmodel.wind;
        }
        if (modelPart.equals("base")) {
            return conduitmodel.base;
        }
        return modelPart.equals("cage") ? conduitmodel.cage : null;
    }

    @Override
    public String[] getModelRendererNames() {
        return new String[]{"eye", "wind", "base", "cage"};
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        TileEntityRendererDispatcher tileentityrendererdispatcher = TileEntityRendererDispatcher.instance;
        TileEntityRenderer tileentityrenderer = tileentityrendererdispatcher.getRenderer(TileEntityType.CONDUIT);
        if (!(tileentityrenderer instanceof ConduitTileEntityRenderer)) {
            return null;
        }
        if (tileentityrenderer.getType() == null) {
            tileentityrenderer = new ConduitTileEntityRenderer(tileentityrendererdispatcher);
        }
        if (!(modelBase instanceof ConduitModel)) {
            Config.warn("Not a conduit model: " + String.valueOf(modelBase));
            return null;
        }
        ConduitModel conduitmodel = (ConduitModel)modelBase;
        return conduitmodel.updateRenderer(tileentityrenderer);
    }
}
