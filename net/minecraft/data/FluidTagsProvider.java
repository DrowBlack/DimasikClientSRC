package net.minecraft.data;

import java.nio.file.Path;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.TagsProvider;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class FluidTagsProvider
extends TagsProvider<Fluid> {
    public FluidTagsProvider(DataGenerator generatorIn) {
        super(generatorIn, Registry.FLUID);
    }

    @Override
    protected void registerTags() {
        this.getOrCreateBuilder(FluidTags.WATER).add((Fluid[])new Fluid[]{Fluids.WATER, Fluids.FLOWING_WATER});
        this.getOrCreateBuilder(FluidTags.LAVA).add((Fluid[])new Fluid[]{Fluids.LAVA, Fluids.FLOWING_LAVA});
    }

    @Override
    protected Path makePath(ResourceLocation id) {
        return this.generator.getOutputFolder().resolve("data/" + id.getNamespace() + "/tags/fluids/" + id.getPath() + ".json");
    }

    @Override
    public String getName() {
        return "Fluid Tags";
    }
}
