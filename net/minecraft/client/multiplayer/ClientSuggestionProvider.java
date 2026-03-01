package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.network.play.client.CTabCompletePacket;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.World;

public class ClientSuggestionProvider
implements ISuggestionProvider {
    private final ClientPlayNetHandler connection;
    private final Minecraft mc;
    private int currentTransaction = -1;
    private CompletableFuture<Suggestions> future;

    public ClientSuggestionProvider(ClientPlayNetHandler p_i49558_1_, Minecraft p_i49558_2_) {
        this.connection = p_i49558_1_;
        this.mc = p_i49558_2_;
    }

    @Override
    public Collection<String> getPlayerNames() {
        ArrayList<String> list = Lists.newArrayList();
        for (NetworkPlayerInfo networkplayerinfo : this.connection.getPlayerInfoMap()) {
            list.add(networkplayerinfo.getGameProfile().getName());
        }
        return list;
    }

    @Override
    public Collection<String> getTargetedEntity() {
        return this.mc.objectMouseOver != null && this.mc.objectMouseOver.getType() == RayTraceResult.Type.ENTITY ? Collections.singleton(((EntityRayTraceResult)this.mc.objectMouseOver).getEntity().getCachedUniqueIdString()) : Collections.emptyList();
    }

    @Override
    public Collection<String> getTeamNames() {
        return this.connection.getWorld().getScoreboard().getTeamNames();
    }

    @Override
    public Collection<ResourceLocation> getSoundResourceLocations() {
        return this.mc.getSoundHandler().getAvailableSounds();
    }

    @Override
    public Stream<ResourceLocation> getRecipeResourceLocations() {
        return this.connection.getRecipeManager().getKeys();
    }

    @Override
    public boolean hasPermissionLevel(int level) {
        ClientPlayerEntity clientplayerentity = this.mc.player;
        return clientplayerentity != null ? clientplayerentity.hasPermissionLevel(level) : level == 0;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestionsFromServer(CommandContext<ISuggestionProvider> context, SuggestionsBuilder suggestionsBuilder) {
        if (this.future != null) {
            this.future.cancel(false);
        }
        this.future = new CompletableFuture();
        int i = ++this.currentTransaction;
        this.connection.sendPacket(new CTabCompletePacket(i, context.getInput()));
        return this.future;
    }

    private static String formatDouble(double p_209001_0_) {
        return String.format(Locale.ROOT, "%.2f", p_209001_0_);
    }

    private static String formatInt(int p_209002_0_) {
        return Integer.toString(p_209002_0_);
    }

    @Override
    public Collection<ISuggestionProvider.Coordinates> func_217294_q() {
        RayTraceResult raytraceresult = this.mc.objectMouseOver;
        if (raytraceresult != null && raytraceresult.getType() == RayTraceResult.Type.BLOCK) {
            BlockPos blockpos = ((BlockRayTraceResult)raytraceresult).getPos();
            return Collections.singleton(new ISuggestionProvider.Coordinates(ClientSuggestionProvider.formatInt(blockpos.getX()), ClientSuggestionProvider.formatInt(blockpos.getY()), ClientSuggestionProvider.formatInt(blockpos.getZ())));
        }
        return ISuggestionProvider.super.func_217294_q();
    }

    @Override
    public Collection<ISuggestionProvider.Coordinates> func_217293_r() {
        RayTraceResult raytraceresult = this.mc.objectMouseOver;
        if (raytraceresult != null && raytraceresult.getType() == RayTraceResult.Type.BLOCK) {
            Vector3d vector3d = raytraceresult.getHitVec();
            return Collections.singleton(new ISuggestionProvider.Coordinates(ClientSuggestionProvider.formatDouble(vector3d.x), ClientSuggestionProvider.formatDouble(vector3d.y), ClientSuggestionProvider.formatDouble(vector3d.z)));
        }
        return ISuggestionProvider.super.func_217293_r();
    }

    @Override
    public Set<RegistryKey<World>> func_230390_p_() {
        return this.connection.func_239164_m_();
    }

    @Override
    public DynamicRegistries func_241861_q() {
        return this.connection.func_239165_n_();
    }

    public void handleResponse(int transaction, Suggestions result) {
        if (transaction == this.currentTransaction) {
            this.future.complete(result);
            this.future = null;
            this.currentTransaction = -1;
        }
    }
}
