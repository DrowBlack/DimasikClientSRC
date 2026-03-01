package net.minecraft.network.play.server;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class SAdvancementInfoPacket
implements IPacket<IClientPlayNetHandler> {
    private boolean firstSync;
    private Map<ResourceLocation, Advancement.Builder> advancementsToAdd;
    private Set<ResourceLocation> advancementsToRemove;
    private Map<ResourceLocation, AdvancementProgress> progressUpdates;

    public SAdvancementInfoPacket() {
    }

    public SAdvancementInfoPacket(boolean p_i47519_1_, Collection<Advancement> p_i47519_2_, Set<ResourceLocation> p_i47519_3_, Map<ResourceLocation, AdvancementProgress> p_i47519_4_) {
        this.firstSync = p_i47519_1_;
        this.advancementsToAdd = Maps.newHashMap();
        for (Advancement advancement : p_i47519_2_) {
            this.advancementsToAdd.put(advancement.getId(), advancement.copy());
        }
        this.advancementsToRemove = p_i47519_3_;
        this.progressUpdates = Maps.newHashMap(p_i47519_4_);
    }

    @Override
    public void processPacket(IClientPlayNetHandler handler) {
        handler.handleAdvancementInfo(this);
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.firstSync = buf.readBoolean();
        this.advancementsToAdd = Maps.newHashMap();
        this.advancementsToRemove = Sets.newLinkedHashSet();
        this.progressUpdates = Maps.newHashMap();
        int i = buf.readVarInt();
        for (int j = 0; j < i; ++j) {
            ResourceLocation resourcelocation = buf.readResourceLocation();
            Advancement.Builder advancement$builder = Advancement.Builder.readFrom(buf);
            this.advancementsToAdd.put(resourcelocation, advancement$builder);
        }
        i = buf.readVarInt();
        for (int k = 0; k < i; ++k) {
            ResourceLocation resourcelocation1 = buf.readResourceLocation();
            this.advancementsToRemove.add(resourcelocation1);
        }
        i = buf.readVarInt();
        for (int l = 0; l < i; ++l) {
            ResourceLocation resourcelocation2 = buf.readResourceLocation();
            this.progressUpdates.put(resourcelocation2, AdvancementProgress.fromNetwork(buf));
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeBoolean(this.firstSync);
        buf.writeVarInt(this.advancementsToAdd.size());
        for (Map.Entry<ResourceLocation, Advancement.Builder> entry : this.advancementsToAdd.entrySet()) {
            ResourceLocation resourcelocation = entry.getKey();
            Advancement.Builder advancement$builder = entry.getValue();
            buf.writeResourceLocation(resourcelocation);
            advancement$builder.writeTo(buf);
        }
        buf.writeVarInt(this.advancementsToRemove.size());
        for (ResourceLocation resourceLocation : this.advancementsToRemove) {
            buf.writeResourceLocation(resourceLocation);
        }
        buf.writeVarInt(this.progressUpdates.size());
        for (Map.Entry entry : this.progressUpdates.entrySet()) {
            buf.writeResourceLocation((ResourceLocation)entry.getKey());
            ((AdvancementProgress)entry.getValue()).serializeToNetwork(buf);
        }
    }

    public Map<ResourceLocation, Advancement.Builder> getAdvancementsToAdd() {
        return this.advancementsToAdd;
    }

    public Set<ResourceLocation> getAdvancementsToRemove() {
        return this.advancementsToRemove;
    }

    public Map<ResourceLocation, AdvancementProgress> getProgressUpdates() {
        return this.progressUpdates;
    }

    public boolean isFirstSync() {
        return this.firstSync;
    }
}
