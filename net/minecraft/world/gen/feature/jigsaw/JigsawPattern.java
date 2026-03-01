package net.minecraft.world.gen.feature.jigsaw;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKeyCodec;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.template.GravityStructureProcessor;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.TemplateManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JigsawPattern {
    private static final Logger field_236853_d_ = LogManager.getLogger();
    public static final Codec<JigsawPattern> field_236852_a_ = RecordCodecBuilder.create(p_236854_0_ -> p_236854_0_.group(((MapCodec)ResourceLocation.CODEC.fieldOf("name")).forGetter(JigsawPattern::getName), ((MapCodec)ResourceLocation.CODEC.fieldOf("fallback")).forGetter(JigsawPattern::getFallback), ((MapCodec)Codec.mapPair(JigsawPiece.field_236847_e_.fieldOf("element"), Codec.INT.fieldOf("weight")).codec().listOf().promotePartial((Consumer)Util.func_240982_a_("Pool element: ", field_236853_d_::error)).fieldOf("elements")).forGetter(p_236857_0_ -> p_236857_0_.rawTemplates)).apply((Applicative<JigsawPattern, ?>)p_236854_0_, JigsawPattern::new));
    public static final Codec<Supplier<JigsawPattern>> field_244392_b_ = RegistryKeyCodec.create(Registry.JIGSAW_POOL_KEY, field_236852_a_);
    private final ResourceLocation name;
    private final List<Pair<JigsawPiece, Integer>> rawTemplates;
    private final List<JigsawPiece> jigsawPieces;
    private final ResourceLocation fallback;
    private int maxSize = Integer.MIN_VALUE;

    public JigsawPattern(ResourceLocation p_i242010_1_, ResourceLocation p_i242010_2_, List<Pair<JigsawPiece, Integer>> p_i242010_3_) {
        this.name = p_i242010_1_;
        this.rawTemplates = p_i242010_3_;
        this.jigsawPieces = Lists.newArrayList();
        for (Pair<JigsawPiece, Integer> pair : p_i242010_3_) {
            JigsawPiece jigsawpiece = pair.getFirst();
            for (int i = 0; i < pair.getSecond(); ++i) {
                this.jigsawPieces.add(jigsawpiece);
            }
        }
        this.fallback = p_i242010_2_;
    }

    public JigsawPattern(ResourceLocation nameIn, ResourceLocation p_i51397_2_, List<Pair<Function<PlacementBehaviour, ? extends JigsawPiece>, Integer>> p_i51397_3_, PlacementBehaviour placementBehaviourIn) {
        this.name = nameIn;
        this.rawTemplates = Lists.newArrayList();
        this.jigsawPieces = Lists.newArrayList();
        for (Pair<Function<PlacementBehaviour, ? extends JigsawPiece>, Integer> pair : p_i51397_3_) {
            JigsawPiece jigsawpiece = pair.getFirst().apply(placementBehaviourIn);
            this.rawTemplates.add(Pair.of(jigsawpiece, pair.getSecond()));
            for (int i = 0; i < pair.getSecond(); ++i) {
                this.jigsawPieces.add(jigsawpiece);
            }
        }
        this.fallback = p_i51397_2_;
    }

    public int getMaxSize(TemplateManager templateManagerIn) {
        if (this.maxSize == Integer.MIN_VALUE) {
            this.maxSize = this.jigsawPieces.stream().mapToInt(p_236856_1_ -> p_236856_1_.getBoundingBox(templateManagerIn, BlockPos.ZERO, Rotation.NONE).getYSize()).max().orElse(0);
        }
        return this.maxSize;
    }

    public ResourceLocation getFallback() {
        return this.fallback;
    }

    public JigsawPiece getRandomPiece(Random rand) {
        return this.jigsawPieces.get(rand.nextInt(this.jigsawPieces.size()));
    }

    public List<JigsawPiece> getShuffledPieces(Random rand) {
        return ImmutableList.copyOf(ObjectArrays.shuffle(this.jigsawPieces.toArray(new JigsawPiece[0]), rand));
    }

    public ResourceLocation getName() {
        return this.name;
    }

    public int getNumberOfPieces() {
        return this.jigsawPieces.size();
    }

    public static enum PlacementBehaviour implements IStringSerializable
    {
        TERRAIN_MATCHING("terrain_matching", ImmutableList.of(new GravityStructureProcessor(Heightmap.Type.WORLD_SURFACE_WG, -1))),
        RIGID("rigid", ImmutableList.of());

        public static final Codec<PlacementBehaviour> field_236858_c_;
        private static final Map<String, PlacementBehaviour> BEHAVIOURS;
        private final String name;
        private final ImmutableList<StructureProcessor> structureProcessors;

        private PlacementBehaviour(String nameIn, ImmutableList<StructureProcessor> structureProcessorsIn) {
            this.name = nameIn;
            this.structureProcessors = structureProcessorsIn;
        }

        public String getName() {
            return this.name;
        }

        public static PlacementBehaviour getBehaviour(String nameIn) {
            return BEHAVIOURS.get(nameIn);
        }

        public ImmutableList<StructureProcessor> getStructureProcessors() {
            return this.structureProcessors;
        }

        @Override
        public String getString() {
            return this.name;
        }

        static {
            field_236858_c_ = IStringSerializable.createEnumCodec(PlacementBehaviour::values, PlacementBehaviour::getBehaviour);
            BEHAVIOURS = Arrays.stream(PlacementBehaviour.values()).collect(Collectors.toMap(PlacementBehaviour::getName, p_214935_0_ -> p_214935_0_));
        }
    }
}
