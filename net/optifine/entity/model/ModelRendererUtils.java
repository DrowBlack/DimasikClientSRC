package net.optifine.entity.model;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ModelRendererUtils {
    public static ModelRenderer getModelRenderer(Iterator<ModelRenderer> iterator, int index) {
        if (iterator == null) {
            return null;
        }
        if (index < 0) {
            return null;
        }
        for (int i = 0; i < index; ++i) {
            if (!iterator.hasNext()) {
                return null;
            }
            ModelRenderer modelRenderer = iterator.next();
        }
        return !iterator.hasNext() ? null : iterator.next();
    }

    public static ModelRenderer getModelRenderer(ImmutableList<ModelRenderer> models, int index) {
        if (models == null) {
            return null;
        }
        if (index < 0) {
            return null;
        }
        return index >= models.size() ? null : (ModelRenderer)models.get(index);
    }
}
