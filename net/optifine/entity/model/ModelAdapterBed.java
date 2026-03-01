package net.optifine.entity.model;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.BedTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntityType;
import net.optifine.Config;
import net.optifine.entity.model.BedModel;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;

public class ModelAdapterBed
extends ModelAdapter {
    public ModelAdapterBed() {
        super(TileEntityType.BED, "bed", 0.0f);
    }

    @Override
    public Model makeModel() {
        return new BedModel();
    }

    @Override
    public ModelRenderer getModelRenderer(Model model, String modelPart) {
        if (!(model instanceof BedModel)) {
            return null;
        }
        BedModel bedmodel = (BedModel)model;
        if (modelPart.equals("head")) {
            return bedmodel.headPiece;
        }
        if (modelPart.equals("foot")) {
            return bedmodel.footPiece;
        }
        ModelRenderer[] amodelrenderer = bedmodel.legs;
        if (amodelrenderer != null) {
            if (modelPart.equals("leg1")) {
                return amodelrenderer[0];
            }
            if (modelPart.equals("leg2")) {
                return amodelrenderer[1];
            }
            if (modelPart.equals("leg3")) {
                return amodelrenderer[2];
            }
            if (modelPart.equals("leg4")) {
                return amodelrenderer[3];
            }
        }
        return null;
    }

    @Override
    public String[] getModelRendererNames() {
        return new String[]{"head", "foot", "leg1", "leg2", "leg3", "leg4"};
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        TileEntityRendererDispatcher tileentityrendererdispatcher = TileEntityRendererDispatcher.instance;
        TileEntityRenderer tileentityrenderer = tileentityrendererdispatcher.getRenderer(TileEntityType.BED);
        if (!(tileentityrenderer instanceof BedTileEntityRenderer)) {
            return null;
        }
        if (tileentityrenderer.getType() == null) {
            tileentityrenderer = new BedTileEntityRenderer(tileentityrendererdispatcher);
        }
        if (!(modelBase instanceof BedModel)) {
            Config.warn("Not a BedModel: " + String.valueOf(modelBase));
            return null;
        }
        BedModel bedmodel = (BedModel)modelBase;
        return bedmodel.updateRenderer(tileentityrenderer);
    }
}
