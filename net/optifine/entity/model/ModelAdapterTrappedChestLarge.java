package net.optifine.entity.model;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.ChestTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntityType;
import net.optifine.Config;
import net.optifine.entity.model.ChestLargeModel;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;

public class ModelAdapterTrappedChestLarge
extends ModelAdapter {
    public ModelAdapterTrappedChestLarge() {
        super(TileEntityType.TRAPPED_CHEST, "trapped_chest_large", 0.0f);
    }

    @Override
    public Model makeModel() {
        return new ChestLargeModel();
    }

    @Override
    public ModelRenderer getModelRenderer(Model model, String modelPart) {
        if (!(model instanceof ChestLargeModel)) {
            return null;
        }
        ChestLargeModel chestlargemodel = (ChestLargeModel)model;
        if (modelPart.equals("lid_left")) {
            return chestlargemodel.lid_left;
        }
        if (modelPart.equals("base_left")) {
            return chestlargemodel.base_left;
        }
        if (modelPart.equals("knob_left")) {
            return chestlargemodel.knob_left;
        }
        if (modelPart.equals("lid_right")) {
            return chestlargemodel.lid_right;
        }
        if (modelPart.equals("base_right")) {
            return chestlargemodel.base_right;
        }
        return modelPart.equals("knob_right") ? chestlargemodel.knob_right : null;
    }

    @Override
    public String[] getModelRendererNames() {
        return new String[]{"lid_left", "base_left", "knob_left", "lid_right", "base_right", "knob_right"};
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
        if (!(modelBase instanceof ChestLargeModel)) {
            Config.warn("Not a large chest model: " + String.valueOf(modelBase));
            return null;
        }
        ChestLargeModel chestlargemodel = (ChestLargeModel)modelBase;
        return chestlargemodel.updateRenderer(tileentityrenderer);
    }
}
