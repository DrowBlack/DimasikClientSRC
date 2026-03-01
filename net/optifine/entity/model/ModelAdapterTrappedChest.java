package net.optifine.entity.model;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.ChestTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntityType;
import net.optifine.Config;
import net.optifine.entity.model.ChestModel;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;

public class ModelAdapterTrappedChest
extends ModelAdapter {
    public ModelAdapterTrappedChest() {
        super(TileEntityType.TRAPPED_CHEST, "trapped_chest", 0.0f);
    }

    @Override
    public Model makeModel() {
        return new ChestModel();
    }

    @Override
    public ModelRenderer getModelRenderer(Model model, String modelPart) {
        if (!(model instanceof ChestModel)) {
            return null;
        }
        ChestModel chestmodel = (ChestModel)model;
        if (modelPart.equals("lid")) {
            return chestmodel.lid;
        }
        if (modelPart.equals("base")) {
            return chestmodel.base;
        }
        return modelPart.equals("knob") ? chestmodel.knob : null;
    }

    @Override
    public String[] getModelRendererNames() {
        return new String[]{"lid", "base", "knob"};
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        TileEntityRendererDispatcher tileentityrendererdispatcher = TileEntityRendererDispatcher.instance;
        ChestTileEntityRenderer tileentityrenderer = tileentityrendererdispatcher.getRenderer(TileEntityType.TRAPPED_CHEST);
        if (!(tileentityrenderer instanceof ChestTileEntityRenderer)) {
            return null;
        }
        if (tileentityrenderer.getType() == null) {
            tileentityrenderer = new ChestTileEntityRenderer(tileentityrendererdispatcher);
        }
        if (!(modelBase instanceof ChestModel)) {
            Config.warn("Not a chest model: " + String.valueOf(modelBase));
            return null;
        }
        ChestModel chestmodel = (ChestModel)modelBase;
        return chestmodel.updateRenderer(tileentityrenderer);
    }
}
