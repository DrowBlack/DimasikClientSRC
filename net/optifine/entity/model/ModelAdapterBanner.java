package net.optifine.entity.model;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.BannerTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntityType;
import net.optifine.Config;
import net.optifine.entity.model.BannerModel;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;

public class ModelAdapterBanner
extends ModelAdapter {
    public ModelAdapterBanner() {
        super(TileEntityType.BANNER, "banner", 0.0f);
    }

    @Override
    public Model makeModel() {
        return new BannerModel();
    }

    @Override
    public ModelRenderer getModelRenderer(Model model, String modelPart) {
        if (!(model instanceof BannerModel)) {
            return null;
        }
        BannerModel bannermodel = (BannerModel)model;
        if (modelPart.equals("slate")) {
            return bannermodel.bannerSlate;
        }
        if (modelPart.equals("stand")) {
            return bannermodel.bannerStand;
        }
        return modelPart.equals("top") ? bannermodel.bannerTop : null;
    }

    @Override
    public String[] getModelRendererNames() {
        return new String[]{"slate", "stand", "top"};
    }

    @Override
    public IEntityRenderer makeEntityRender(Model model, float shadowSize) {
        TileEntityRendererDispatcher tileentityrendererdispatcher = TileEntityRendererDispatcher.instance;
        TileEntityRenderer tileentityrenderer = tileentityrendererdispatcher.getRenderer(TileEntityType.BANNER);
        if (!(tileentityrenderer instanceof BannerTileEntityRenderer)) {
            return null;
        }
        if (tileentityrenderer.getType() == null) {
            tileentityrenderer = new BannerTileEntityRenderer(tileentityrendererdispatcher);
        }
        if (!(model instanceof BannerModel)) {
            Config.warn("Not a banner model: " + String.valueOf(model));
            return null;
        }
        BannerModel bannermodel = (BannerModel)model;
        return bannermodel.updateRenderer(tileentityrenderer);
    }
}
