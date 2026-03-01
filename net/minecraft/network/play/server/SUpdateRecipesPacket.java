package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class SUpdateRecipesPacket
implements IPacket<IClientPlayNetHandler> {
    private List<IRecipe<?>> recipes;

    public SUpdateRecipesPacket() {
    }

    public SUpdateRecipesPacket(Collection<IRecipe<?>> p_i48176_1_) {
        this.recipes = Lists.newArrayList(p_i48176_1_);
    }

    @Override
    public void processPacket(IClientPlayNetHandler handler) {
        handler.handleUpdateRecipes(this);
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.recipes = Lists.newArrayList();
        int i = buf.readVarInt();
        for (int j = 0; j < i; ++j) {
            this.recipes.add(SUpdateRecipesPacket.func_218772_c(buf));
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarInt(this.recipes.size());
        for (IRecipe<?> irecipe : this.recipes) {
            SUpdateRecipesPacket.func_218771_a(irecipe, buf);
        }
    }

    public List<IRecipe<?>> getRecipes() {
        return this.recipes;
    }

    public static IRecipe<?> func_218772_c(PacketBuffer p_218772_0_) {
        ResourceLocation resourcelocation = p_218772_0_.readResourceLocation();
        ResourceLocation resourcelocation1 = p_218772_0_.readResourceLocation();
        return Registry.RECIPE_SERIALIZER.getOptional(resourcelocation).orElseThrow(() -> new IllegalArgumentException("Unknown recipe serializer " + String.valueOf(resourcelocation))).read(resourcelocation1, p_218772_0_);
    }

    public static <T extends IRecipe<?>> void func_218771_a(T p_218771_0_, PacketBuffer p_218771_1_) {
        p_218771_1_.writeResourceLocation(Registry.RECIPE_SERIALIZER.getKey(p_218771_0_.getSerializer()));
        p_218771_1_.writeResourceLocation(p_218771_0_.getId());
        p_218771_0_.getSerializer().write(p_218771_1_, p_218771_0_);
    }
}
