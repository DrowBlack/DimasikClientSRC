package net.minecraft.advancements;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.advancements.AdvancementTreeNode;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.LootPredicateManager;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdvancementManager
extends JsonReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().create();
    private AdvancementList advancementList = new AdvancementList();
    private final LootPredicateManager lootPredicateManager;

    public AdvancementManager(LootPredicateManager lootPredicateManager) {
        super(GSON, "advancements");
        this.lootPredicateManager = lootPredicateManager;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn) {
        HashMap<ResourceLocation, Advancement.Builder> map = Maps.newHashMap();
        objectIn.forEach((conditions, advancement) -> {
            try {
                JsonObject jsonobject = JSONUtils.getJsonObject(advancement, "advancement");
                Advancement.Builder advancement$builder = Advancement.Builder.deserialize(jsonobject, new ConditionArrayParser((ResourceLocation)conditions, this.lootPredicateManager));
                map.put((ResourceLocation)conditions, advancement$builder);
            }
            catch (JsonParseException | IllegalArgumentException jsonparseexception) {
                LOGGER.error("Parsing error loading custom advancement {}: {}", conditions, (Object)jsonparseexception.getMessage());
            }
        });
        AdvancementList advancementlist = new AdvancementList();
        advancementlist.loadAdvancements(map);
        for (Advancement advancement2 : advancementlist.getRoots()) {
            if (advancement2.getDisplay() == null) continue;
            AdvancementTreeNode.layout(advancement2);
        }
        this.advancementList = advancementlist;
    }

    @Nullable
    public Advancement getAdvancement(ResourceLocation id) {
        return this.advancementList.getAdvancement(id);
    }

    public Collection<Advancement> getAllAdvancements() {
        return this.advancementList.getAll();
    }
}
