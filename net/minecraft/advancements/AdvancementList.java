package net.minecraft.advancements;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdvancementList {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<ResourceLocation, Advancement> advancements = Maps.newHashMap();
    private final Set<Advancement> roots = Sets.newLinkedHashSet();
    private final Set<Advancement> nonRoots = Sets.newLinkedHashSet();
    private IListener listener;

    private void remove(Advancement advancementIn) {
        for (Advancement advancement : advancementIn.getChildren()) {
            this.remove(advancement);
        }
        LOGGER.info("Forgot about advancement {}", (Object)advancementIn.getId());
        this.advancements.remove(advancementIn.getId());
        if (advancementIn.getParent() == null) {
            this.roots.remove(advancementIn);
            if (this.listener != null) {
                this.listener.rootAdvancementRemoved(advancementIn);
            }
        } else {
            this.nonRoots.remove(advancementIn);
            if (this.listener != null) {
                this.listener.nonRootAdvancementRemoved(advancementIn);
            }
        }
    }

    public void removeAll(Set<ResourceLocation> ids) {
        for (ResourceLocation resourcelocation : ids) {
            Advancement advancement = this.advancements.get(resourcelocation);
            if (advancement == null) {
                LOGGER.warn("Told to remove advancement {} but I don't know what that is", (Object)resourcelocation);
                continue;
            }
            this.remove(advancement);
        }
    }

    public void loadAdvancements(Map<ResourceLocation, Advancement.Builder> advancementsIn) {
        Function<ResourceLocation, Advancement> function = Functions.forMap(this.advancements, null);
        while (!advancementsIn.isEmpty()) {
            boolean flag = false;
            Iterator<Map.Entry<ResourceLocation, Advancement.Builder>> iterator = advancementsIn.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<ResourceLocation, Advancement.Builder> entry = iterator.next();
                ResourceLocation resourcelocation = entry.getKey();
                Advancement.Builder advancement$builder = entry.getValue();
                if (!advancement$builder.resolveParent(function)) continue;
                Advancement advancement = advancement$builder.build(resourcelocation);
                this.advancements.put(resourcelocation, advancement);
                flag = true;
                iterator.remove();
                if (advancement.getParent() == null) {
                    this.roots.add(advancement);
                    if (this.listener == null) continue;
                    this.listener.rootAdvancementAdded(advancement);
                    continue;
                }
                this.nonRoots.add(advancement);
                if (this.listener == null) continue;
                this.listener.nonRootAdvancementAdded(advancement);
            }
            if (flag) continue;
            for (Map.Entry<ResourceLocation, Advancement.Builder> entry1 : advancementsIn.entrySet()) {
                LOGGER.error("Couldn't load advancement {}: {}", (Object)entry1.getKey(), (Object)entry1.getValue());
            }
        }
        LOGGER.info("Loaded {} advancements", (Object)this.advancements.size());
    }

    public void clear() {
        this.advancements.clear();
        this.roots.clear();
        this.nonRoots.clear();
        if (this.listener != null) {
            this.listener.advancementsCleared();
        }
    }

    public Iterable<Advancement> getRoots() {
        return this.roots;
    }

    public Collection<Advancement> getAll() {
        return this.advancements.values();
    }

    @Nullable
    public Advancement getAdvancement(ResourceLocation id) {
        return this.advancements.get(id);
    }

    public void setListener(@Nullable IListener listenerIn) {
        this.listener = listenerIn;
        if (listenerIn != null) {
            for (Advancement advancement : this.roots) {
                listenerIn.rootAdvancementAdded(advancement);
            }
            for (Advancement advancement1 : this.nonRoots) {
                listenerIn.nonRootAdvancementAdded(advancement1);
            }
        }
    }

    public static interface IListener {
        public void rootAdvancementAdded(Advancement var1);

        public void rootAdvancementRemoved(Advancement var1);

        public void nonRootAdvancementAdded(Advancement var1);

        public void nonRootAdvancementRemoved(Advancement var1);

        public void advancementsCleared();
    }
}
