package net.minecraft.advancements;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SAdvancementInfoPacket;
import net.minecraft.network.play.server.SSelectAdvancementsTabPacket;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerAdvancements {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter((Type)((Object)AdvancementProgress.class), new AdvancementProgress.Serializer()).registerTypeAdapter((Type)((Object)ResourceLocation.class), new ResourceLocation.Serializer()).setPrettyPrinting().create();
    private static final TypeToken<Map<ResourceLocation, AdvancementProgress>> MAP_TOKEN = new TypeToken<Map<ResourceLocation, AdvancementProgress>>(){};
    private final DataFixer dataFixer;
    private final PlayerList playerList;
    private final File progressFile;
    private final Map<Advancement, AdvancementProgress> progress = Maps.newLinkedHashMap();
    private final Set<Advancement> visible = Sets.newLinkedHashSet();
    private final Set<Advancement> visibilityChanged = Sets.newLinkedHashSet();
    private final Set<Advancement> progressChanged = Sets.newLinkedHashSet();
    private ServerPlayerEntity player;
    @Nullable
    private Advancement lastSelectedTab;
    private boolean isFirstPacket = true;

    public PlayerAdvancements(DataFixer dataFixer, PlayerList playerList, AdvancementManager advancementManager, File progressFile, ServerPlayerEntity player) {
        this.dataFixer = dataFixer;
        this.playerList = playerList;
        this.progressFile = progressFile;
        this.player = player;
        this.deserialize(advancementManager);
    }

    public void setPlayer(ServerPlayerEntity player) {
        this.player = player;
    }

    public void dispose() {
        for (ICriterionTrigger<?> icriteriontrigger : CriteriaTriggers.getAll()) {
            icriteriontrigger.removeAllListeners(this);
        }
    }

    public void reset(AdvancementManager manager) {
        this.dispose();
        this.progress.clear();
        this.visible.clear();
        this.visibilityChanged.clear();
        this.progressChanged.clear();
        this.isFirstPacket = true;
        this.lastSelectedTab = null;
        this.deserialize(manager);
    }

    private void registerAchievementListeners(AdvancementManager manager) {
        for (Advancement advancement : manager.getAllAdvancements()) {
            this.registerListeners(advancement);
        }
    }

    private void ensureAllVisible() {
        ArrayList<Advancement> list = Lists.newArrayList();
        for (Map.Entry<Advancement, AdvancementProgress> entry : this.progress.entrySet()) {
            if (!entry.getValue().isDone()) continue;
            list.add(entry.getKey());
            this.progressChanged.add(entry.getKey());
        }
        for (Advancement advancement : list) {
            this.ensureVisibility(advancement);
        }
    }

    private void unlockDefaultAdvancements(AdvancementManager manager) {
        for (Advancement advancement : manager.getAllAdvancements()) {
            if (!advancement.getCriteria().isEmpty()) continue;
            this.grantCriterion(advancement, "");
            advancement.getRewards().apply(this.player);
        }
    }

    private void deserialize(AdvancementManager manager) {
        if (this.progressFile.isFile()) {
            try (JsonReader jsonreader = new JsonReader(new StringReader(Files.toString(this.progressFile, StandardCharsets.UTF_8)));){
                jsonreader.setLenient(false);
                Dynamic<JsonElement> dynamic = new Dynamic<JsonElement>(JsonOps.INSTANCE, Streams.parse(jsonreader));
                if (!dynamic.get("DataVersion").asNumber().result().isPresent()) {
                    dynamic = dynamic.set("DataVersion", dynamic.createInt(1343));
                }
                dynamic = this.dataFixer.update(DefaultTypeReferences.ADVANCEMENTS.getTypeReference(), dynamic, dynamic.get("DataVersion").asInt(0), SharedConstants.getVersion().getWorldVersion());
                dynamic = dynamic.remove("DataVersion");
                Map<ResourceLocation, AdvancementProgress> map = GSON.getAdapter(MAP_TOKEN).fromJsonTree(dynamic.getValue());
                if (map == null) {
                    throw new JsonParseException("Found null for advancements");
                }
                Stream<Map.Entry> stream = map.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getValue));
                for (Map.Entry entry : stream.collect(Collectors.toList())) {
                    Advancement advancement = manager.getAdvancement((ResourceLocation)entry.getKey());
                    if (advancement == null) {
                        LOGGER.warn("Ignored advancement '{}' in progress file {} - it doesn't exist anymore?", entry.getKey(), (Object)this.progressFile);
                        continue;
                    }
                    this.startProgress(advancement, (AdvancementProgress)entry.getValue());
                }
            }
            catch (JsonParseException jsonparseexception) {
                LOGGER.error("Couldn't parse player advancements in {}", (Object)this.progressFile, (Object)jsonparseexception);
            }
            catch (IOException ioexception) {
                LOGGER.error("Couldn't access player advancements in {}", (Object)this.progressFile, (Object)ioexception);
            }
        }
        this.unlockDefaultAdvancements(manager);
        this.ensureAllVisible();
        this.registerAchievementListeners(manager);
    }

    public void save() {
        HashMap<ResourceLocation, AdvancementProgress> map = Maps.newHashMap();
        for (Map.Entry<Advancement, AdvancementProgress> entry : this.progress.entrySet()) {
            AdvancementProgress advancementprogress = entry.getValue();
            if (!advancementprogress.hasProgress()) continue;
            map.put(entry.getKey().getId(), advancementprogress);
        }
        if (this.progressFile.getParentFile() != null) {
            this.progressFile.getParentFile().mkdirs();
        }
        JsonElement jsonelement = GSON.toJsonTree(map);
        jsonelement.getAsJsonObject().addProperty("DataVersion", SharedConstants.getVersion().getWorldVersion());
        try (FileOutputStream outputstream = new FileOutputStream(this.progressFile);
             OutputStreamWriter writer = new OutputStreamWriter((OutputStream)outputstream, Charsets.UTF_8.newEncoder());){
            GSON.toJson(jsonelement, (Appendable)writer);
        }
        catch (IOException ioexception) {
            LOGGER.error("Couldn't save player advancements to {}", (Object)this.progressFile, (Object)ioexception);
        }
    }

    public boolean grantCriterion(Advancement advancementIn, String criterionKey) {
        boolean flag = false;
        AdvancementProgress advancementprogress = this.getProgress(advancementIn);
        boolean flag1 = advancementprogress.isDone();
        if (advancementprogress.grantCriterion(criterionKey)) {
            this.unregisterListeners(advancementIn);
            this.progressChanged.add(advancementIn);
            flag = true;
            if (!flag1 && advancementprogress.isDone()) {
                advancementIn.getRewards().apply(this.player);
                if (advancementIn.getDisplay() != null && advancementIn.getDisplay().shouldAnnounceToChat() && this.player.world.getGameRules().getBoolean(GameRules.ANNOUNCE_ADVANCEMENTS)) {
                    this.playerList.func_232641_a_(new TranslationTextComponent("chat.type.advancement." + advancementIn.getDisplay().getFrame().getName(), this.player.getDisplayName(), advancementIn.getDisplayText()), ChatType.SYSTEM, Util.DUMMY_UUID);
                }
            }
        }
        if (advancementprogress.isDone()) {
            this.ensureVisibility(advancementIn);
        }
        return flag;
    }

    public boolean revokeCriterion(Advancement advancementIn, String criterionKey) {
        boolean flag = false;
        AdvancementProgress advancementprogress = this.getProgress(advancementIn);
        if (advancementprogress.revokeCriterion(criterionKey)) {
            this.registerListeners(advancementIn);
            this.progressChanged.add(advancementIn);
            flag = true;
        }
        if (!advancementprogress.hasProgress()) {
            this.ensureVisibility(advancementIn);
        }
        return flag;
    }

    private void registerListeners(Advancement advancementIn) {
        AdvancementProgress advancementprogress = this.getProgress(advancementIn);
        if (!advancementprogress.isDone()) {
            for (Map.Entry<String, Criterion> entry : advancementIn.getCriteria().entrySet()) {
                ICriterionTrigger<ICriterionInstance> icriteriontrigger;
                ICriterionInstance icriterioninstance;
                CriterionProgress criterionprogress = advancementprogress.getCriterionProgress(entry.getKey());
                if (criterionprogress == null || criterionprogress.isObtained() || (icriterioninstance = entry.getValue().getCriterionInstance()) == null || (icriteriontrigger = CriteriaTriggers.get(icriterioninstance.getId())) == null) continue;
                icriteriontrigger.addListener(this, new ICriterionTrigger.Listener<ICriterionInstance>(icriterioninstance, advancementIn, entry.getKey()));
            }
        }
    }

    private void unregisterListeners(Advancement advancementIn) {
        AdvancementProgress advancementprogress = this.getProgress(advancementIn);
        for (Map.Entry<String, Criterion> entry : advancementIn.getCriteria().entrySet()) {
            ICriterionTrigger<ICriterionInstance> icriteriontrigger;
            ICriterionInstance icriterioninstance;
            CriterionProgress criterionprogress = advancementprogress.getCriterionProgress(entry.getKey());
            if (criterionprogress == null || !criterionprogress.isObtained() && !advancementprogress.isDone() || (icriterioninstance = entry.getValue().getCriterionInstance()) == null || (icriteriontrigger = CriteriaTriggers.get(icriterioninstance.getId())) == null) continue;
            icriteriontrigger.removeListener(this, new ICriterionTrigger.Listener<ICriterionInstance>(icriterioninstance, advancementIn, entry.getKey()));
        }
    }

    public void flushDirty(ServerPlayerEntity serverPlayer) {
        if (this.isFirstPacket || !this.visibilityChanged.isEmpty() || !this.progressChanged.isEmpty()) {
            HashMap<ResourceLocation, AdvancementProgress> map = Maps.newHashMap();
            LinkedHashSet<Advancement> set = Sets.newLinkedHashSet();
            LinkedHashSet<ResourceLocation> set1 = Sets.newLinkedHashSet();
            for (Advancement advancement : this.progressChanged) {
                if (!this.visible.contains(advancement)) continue;
                map.put(advancement.getId(), this.progress.get(advancement));
            }
            for (Advancement advancement1 : this.visibilityChanged) {
                if (this.visible.contains(advancement1)) {
                    set.add(advancement1);
                    continue;
                }
                set1.add(advancement1.getId());
            }
            if (this.isFirstPacket || !map.isEmpty() || !set.isEmpty() || !set1.isEmpty()) {
                serverPlayer.connection.sendPacket(new SAdvancementInfoPacket(this.isFirstPacket, set, set1, map));
                this.visibilityChanged.clear();
                this.progressChanged.clear();
            }
        }
        this.isFirstPacket = false;
    }

    public void setSelectedTab(@Nullable Advancement advancementIn) {
        Advancement advancement = this.lastSelectedTab;
        this.lastSelectedTab = advancementIn != null && advancementIn.getParent() == null && advancementIn.getDisplay() != null ? advancementIn : null;
        if (advancement != this.lastSelectedTab) {
            this.player.connection.sendPacket(new SSelectAdvancementsTabPacket(this.lastSelectedTab == null ? null : this.lastSelectedTab.getId()));
        }
    }

    public AdvancementProgress getProgress(Advancement advancementIn) {
        AdvancementProgress advancementprogress = this.progress.get(advancementIn);
        if (advancementprogress == null) {
            advancementprogress = new AdvancementProgress();
            this.startProgress(advancementIn, advancementprogress);
        }
        return advancementprogress;
    }

    private void startProgress(Advancement advancementIn, AdvancementProgress progress) {
        progress.update(advancementIn.getCriteria(), advancementIn.getRequirements());
        this.progress.put(advancementIn, progress);
    }

    private void ensureVisibility(Advancement advancementIn) {
        boolean flag = this.shouldBeVisible(advancementIn);
        boolean flag1 = this.visible.contains(advancementIn);
        if (flag && !flag1) {
            this.visible.add(advancementIn);
            this.visibilityChanged.add(advancementIn);
            if (this.progress.containsKey(advancementIn)) {
                this.progressChanged.add(advancementIn);
            }
        } else if (!flag && flag1) {
            this.visible.remove(advancementIn);
            this.visibilityChanged.add(advancementIn);
        }
        if (flag != flag1 && advancementIn.getParent() != null) {
            this.ensureVisibility(advancementIn.getParent());
        }
        for (Advancement advancement : advancementIn.getChildren()) {
            this.ensureVisibility(advancement);
        }
    }

    private boolean shouldBeVisible(Advancement advancement) {
        for (int i = 0; advancement != null && i <= 2; advancement = advancement.getParent(), ++i) {
            if (i == 0 && this.hasCompletedChildrenOrSelf(advancement)) {
                return true;
            }
            if (advancement.getDisplay() == null) {
                return false;
            }
            AdvancementProgress advancementprogress = this.getProgress(advancement);
            if (advancementprogress.isDone()) {
                return true;
            }
            if (!advancement.getDisplay().isHidden()) continue;
            return false;
        }
        return false;
    }

    private boolean hasCompletedChildrenOrSelf(Advancement advancementIn) {
        AdvancementProgress advancementprogress = this.getProgress(advancementIn);
        if (advancementprogress.isDone()) {
            return true;
        }
        for (Advancement advancement : advancementIn.getChildren()) {
            if (!this.hasCompletedChildrenOrSelf(advancement)) continue;
            return true;
        }
        return false;
    }
}
