package dimasik.managers.mods.voicechat.plugins;

import dimasik.managers.mods.voicechat.plugins.impl.VolumeCategoryImpl;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public class CategoryManager {
    protected final Map<String, VolumeCategoryImpl> categories = new ConcurrentHashMap<String, VolumeCategoryImpl>();

    public void addCategory(VolumeCategoryImpl category) {
        this.categories.put(category.getId(), category);
    }

    @Nullable
    public VolumeCategoryImpl removeCategory(String categoryId) {
        return this.categories.remove(categoryId);
    }

    public Collection<VolumeCategoryImpl> getCategories() {
        return this.categories.values();
    }
}
