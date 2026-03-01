package net.minecraft.world.gen.feature.jigsaw;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import net.minecraft.block.JigsawBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.jigsaw.EmptyJigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.JigsawJunction;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPatternRegistry;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JigsawManager {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void func_242837_a(DynamicRegistries p_242837_0_, VillageConfig p_242837_1_, IPieceFactory p_242837_2_, ChunkGenerator p_242837_3_, TemplateManager p_242837_4_, BlockPos p_242837_5_, List<? super AbstractVillagePiece> p_242837_6_, Random p_242837_7_, boolean p_242837_8_, boolean p_242837_9_) {
        Structure.func_236397_g_();
        MutableRegistry<JigsawPattern> mutableregistry = p_242837_0_.getRegistry(Registry.JIGSAW_POOL_KEY);
        Rotation rotation = Rotation.randomRotation(p_242837_7_);
        JigsawPattern jigsawpattern = p_242837_1_.func_242810_c().get();
        JigsawPiece jigsawpiece = jigsawpattern.getRandomPiece(p_242837_7_);
        AbstractVillagePiece abstractvillagepiece = p_242837_2_.create(p_242837_4_, jigsawpiece, p_242837_5_, jigsawpiece.getGroundLevelDelta(), rotation, jigsawpiece.getBoundingBox(p_242837_4_, p_242837_5_, rotation));
        MutableBoundingBox mutableboundingbox = abstractvillagepiece.getBoundingBox();
        int i = (mutableboundingbox.maxX + mutableboundingbox.minX) / 2;
        int j = (mutableboundingbox.maxZ + mutableboundingbox.minZ) / 2;
        int k = p_242837_9_ ? p_242837_5_.getY() + p_242837_3_.getNoiseHeight(i, j, Heightmap.Type.WORLD_SURFACE_WG) : p_242837_5_.getY();
        int l = mutableboundingbox.minY + abstractvillagepiece.getGroundLevelDelta();
        abstractvillagepiece.offset(0, k - l, 0);
        p_242837_6_.add(abstractvillagepiece);
        if (p_242837_1_.func_236534_a_() > 0) {
            int i1 = 80;
            AxisAlignedBB axisalignedbb = new AxisAlignedBB(i - 80, k - 80, j - 80, i + 80 + 1, k + 80 + 1, j + 80 + 1);
            Assembler jigsawmanager$assembler = new Assembler(mutableregistry, p_242837_1_.func_236534_a_(), p_242837_2_, p_242837_3_, p_242837_4_, p_242837_6_, p_242837_7_);
            jigsawmanager$assembler.availablePieces.addLast(new Entry(abstractvillagepiece, new MutableObject<VoxelShape>(VoxelShapes.combineAndSimplify(VoxelShapes.create(axisalignedbb), VoxelShapes.create(AxisAlignedBB.toImmutable(mutableboundingbox)), IBooleanFunction.ONLY_FIRST)), k + 80, 0));
            while (!jigsawmanager$assembler.availablePieces.isEmpty()) {
                Entry jigsawmanager$entry = jigsawmanager$assembler.availablePieces.removeFirst();
                jigsawmanager$assembler.func_236831_a_(jigsawmanager$entry.villagePiece, jigsawmanager$entry.free, jigsawmanager$entry.boundsTop, jigsawmanager$entry.depth, p_242837_8_);
            }
        }
    }

    public static void func_242838_a(DynamicRegistries p_242838_0_, AbstractVillagePiece p_242838_1_, int p_242838_2_, IPieceFactory p_242838_3_, ChunkGenerator p_242838_4_, TemplateManager p_242838_5_, List<? super AbstractVillagePiece> p_242838_6_, Random p_242838_7_) {
        MutableRegistry<JigsawPattern> mutableregistry = p_242838_0_.getRegistry(Registry.JIGSAW_POOL_KEY);
        Assembler jigsawmanager$assembler = new Assembler(mutableregistry, p_242838_2_, p_242838_3_, p_242838_4_, p_242838_5_, p_242838_6_, p_242838_7_);
        jigsawmanager$assembler.availablePieces.addLast(new Entry(p_242838_1_, new MutableObject<VoxelShape>(VoxelShapes.INFINITY), 0, 0));
        while (!jigsawmanager$assembler.availablePieces.isEmpty()) {
            Entry jigsawmanager$entry = jigsawmanager$assembler.availablePieces.removeFirst();
            jigsawmanager$assembler.func_236831_a_(jigsawmanager$entry.villagePiece, jigsawmanager$entry.free, jigsawmanager$entry.boundsTop, jigsawmanager$entry.depth, false);
        }
    }

    public static interface IPieceFactory {
        public AbstractVillagePiece create(TemplateManager var1, JigsawPiece var2, BlockPos var3, int var4, Rotation var5, MutableBoundingBox var6);
    }

    static final class Assembler {
        private final Registry<JigsawPattern> field_242839_a;
        private final int maxDepth;
        private final IPieceFactory pieceFactory;
        private final ChunkGenerator chunkGenerator;
        private final TemplateManager templateManager;
        private final List<? super AbstractVillagePiece> structurePieces;
        private final Random rand;
        private final Deque<Entry> availablePieces = Queues.newArrayDeque();

        private Assembler(Registry<JigsawPattern> p_i242005_1_, int p_i242005_2_, IPieceFactory p_i242005_3_, ChunkGenerator p_i242005_4_, TemplateManager p_i242005_5_, List<? super AbstractVillagePiece> p_i242005_6_, Random p_i242005_7_) {
            this.field_242839_a = p_i242005_1_;
            this.maxDepth = p_i242005_2_;
            this.pieceFactory = p_i242005_3_;
            this.chunkGenerator = p_i242005_4_;
            this.templateManager = p_i242005_5_;
            this.structurePieces = p_i242005_6_;
            this.rand = p_i242005_7_;
        }

        private void func_236831_a_(AbstractVillagePiece p_236831_1_, MutableObject<VoxelShape> p_236831_2_, int p_236831_3_, int p_236831_4_, boolean p_236831_5_) {
            JigsawPiece jigsawpiece = p_236831_1_.getJigsawPiece();
            BlockPos blockpos = p_236831_1_.getPos();
            Rotation rotation = p_236831_1_.getRotation();
            JigsawPattern.PlacementBehaviour jigsawpattern$placementbehaviour = jigsawpiece.getPlacementBehaviour();
            boolean flag = jigsawpattern$placementbehaviour == JigsawPattern.PlacementBehaviour.RIGID;
            MutableObject<VoxelShape> mutableobject = new MutableObject<VoxelShape>();
            MutableBoundingBox mutableboundingbox = p_236831_1_.getBoundingBox();
            int i = mutableboundingbox.minY;
            block0: for (Template.BlockInfo template$blockinfo : jigsawpiece.getJigsawBlocks(this.templateManager, blockpos, rotation, this.rand)) {
                Direction direction = JigsawBlock.getConnectingDirection(template$blockinfo.state);
                BlockPos blockpos1 = template$blockinfo.pos;
                BlockPos blockpos2 = blockpos1.offset(direction);
                int j = blockpos1.getY() - i;
                int k = -1;
                ResourceLocation resourcelocation = new ResourceLocation(template$blockinfo.nbt.getString("pool"));
                Optional<JigsawPattern> optional = this.field_242839_a.getOptional(resourcelocation);
                if (optional.isPresent() && (optional.get().getNumberOfPieces() != 0 || Objects.equals(resourcelocation, JigsawPatternRegistry.field_244091_a.getLocation()))) {
                    ResourceLocation resourcelocation1 = optional.get().getFallback();
                    Optional<JigsawPattern> optional1 = this.field_242839_a.getOptional(resourcelocation1);
                    if (optional1.isPresent() && (optional1.get().getNumberOfPieces() != 0 || Objects.equals(resourcelocation1, JigsawPatternRegistry.field_244091_a.getLocation()))) {
                        JigsawPiece jigsawpiece1;
                        int l;
                        MutableObject<Object> mutableobject1;
                        boolean flag1 = mutableboundingbox.isVecInside(blockpos2);
                        if (flag1) {
                            mutableobject1 = mutableobject;
                            l = i;
                            if (mutableobject.getValue() == null) {
                                mutableobject.setValue(VoxelShapes.create(AxisAlignedBB.toImmutable(mutableboundingbox)));
                            }
                        } else {
                            mutableobject1 = p_236831_2_;
                            l = p_236831_3_;
                        }
                        ArrayList<JigsawPiece> list = Lists.newArrayList();
                        if (p_236831_4_ != this.maxDepth) {
                            list.addAll(optional.get().getShuffledPieces(this.rand));
                        }
                        list.addAll(optional1.get().getShuffledPieces(this.rand));
                        Iterator iterator = list.iterator();
                        while (iterator.hasNext() && (jigsawpiece1 = (JigsawPiece)iterator.next()) != EmptyJigsawPiece.INSTANCE) {
                            for (Rotation rotation1 : Rotation.shuffledRotations(this.rand)) {
                                List<Template.BlockInfo> list1 = jigsawpiece1.getJigsawBlocks(this.templateManager, BlockPos.ZERO, rotation1, this.rand);
                                MutableBoundingBox mutableboundingbox1 = jigsawpiece1.getBoundingBox(this.templateManager, BlockPos.ZERO, rotation1);
                                int i1 = p_236831_5_ && mutableboundingbox1.getYSize() <= 16 ? list1.stream().mapToInt(p_242841_2_ -> {
                                    if (!mutableboundingbox1.isVecInside(p_242841_2_.pos.offset(JigsawBlock.getConnectingDirection(p_242841_2_.state)))) {
                                        return 0;
                                    }
                                    ResourceLocation resourcelocation2 = new ResourceLocation(p_242841_2_.nbt.getString("pool"));
                                    Optional<JigsawPattern> optional2 = this.field_242839_a.getOptional(resourcelocation2);
                                    Optional<Integer> optional3 = optional2.flatMap(p_242843_1_ -> this.field_242839_a.getOptional(p_242843_1_.getFallback()));
                                    int k3 = optional2.map(p_242842_1_ -> p_242842_1_.getMaxSize(this.templateManager)).orElse(0);
                                    int l3 = optional3.map(p_242840_1_ -> p_242840_1_.getMaxSize(this.templateManager)).orElse(0);
                                    return Math.max(k3, l3);
                                }).max().orElse(0) : 0;
                                for (Template.BlockInfo template$blockinfo1 : list1) {
                                    int i3;
                                    int i2;
                                    if (!JigsawBlock.hasJigsawMatch(template$blockinfo, template$blockinfo1)) continue;
                                    BlockPos blockpos3 = template$blockinfo1.pos;
                                    BlockPos blockpos4 = new BlockPos(blockpos2.getX() - blockpos3.getX(), blockpos2.getY() - blockpos3.getY(), blockpos2.getZ() - blockpos3.getZ());
                                    MutableBoundingBox mutableboundingbox2 = jigsawpiece1.getBoundingBox(this.templateManager, blockpos4, rotation1);
                                    int j1 = mutableboundingbox2.minY;
                                    JigsawPattern.PlacementBehaviour jigsawpattern$placementbehaviour1 = jigsawpiece1.getPlacementBehaviour();
                                    boolean flag2 = jigsawpattern$placementbehaviour1 == JigsawPattern.PlacementBehaviour.RIGID;
                                    int k1 = blockpos3.getY();
                                    int l1 = j - k1 + JigsawBlock.getConnectingDirection(template$blockinfo.state).getYOffset();
                                    if (flag && flag2) {
                                        i2 = i + l1;
                                    } else {
                                        if (k == -1) {
                                            k = this.chunkGenerator.getNoiseHeight(blockpos1.getX(), blockpos1.getZ(), Heightmap.Type.WORLD_SURFACE_WG);
                                        }
                                        i2 = k - k1;
                                    }
                                    int j2 = i2 - j1;
                                    MutableBoundingBox mutableboundingbox3 = mutableboundingbox2.func_215127_b(0, j2, 0);
                                    BlockPos blockpos5 = blockpos4.add(0, j2, 0);
                                    if (i1 > 0) {
                                        int k2 = Math.max(i1 + 1, mutableboundingbox3.maxY - mutableboundingbox3.minY);
                                        mutableboundingbox3.maxY = mutableboundingbox3.minY + k2;
                                    }
                                    if (VoxelShapes.compare((VoxelShape)mutableobject1.getValue(), VoxelShapes.create(AxisAlignedBB.toImmutable(mutableboundingbox3).shrink(0.25)), IBooleanFunction.ONLY_SECOND)) continue;
                                    mutableobject1.setValue(VoxelShapes.combine((VoxelShape)mutableobject1.getValue(), VoxelShapes.create(AxisAlignedBB.toImmutable(mutableboundingbox3)), IBooleanFunction.ONLY_FIRST));
                                    int j3 = p_236831_1_.getGroundLevelDelta();
                                    int l2 = flag2 ? j3 - l1 : jigsawpiece1.getGroundLevelDelta();
                                    AbstractVillagePiece abstractvillagepiece = this.pieceFactory.create(this.templateManager, jigsawpiece1, blockpos5, l2, rotation1, mutableboundingbox3);
                                    if (flag) {
                                        i3 = i + j;
                                    } else if (flag2) {
                                        i3 = i2 + k1;
                                    } else {
                                        if (k == -1) {
                                            k = this.chunkGenerator.getNoiseHeight(blockpos1.getX(), blockpos1.getZ(), Heightmap.Type.WORLD_SURFACE_WG);
                                        }
                                        i3 = k + l1 / 2;
                                    }
                                    p_236831_1_.addJunction(new JigsawJunction(blockpos2.getX(), i3 - j + j3, blockpos2.getZ(), l1, jigsawpattern$placementbehaviour1));
                                    abstractvillagepiece.addJunction(new JigsawJunction(blockpos1.getX(), i3 - k1 + l2, blockpos1.getZ(), -l1, jigsawpattern$placementbehaviour));
                                    this.structurePieces.add(abstractvillagepiece);
                                    if (p_236831_4_ + 1 > this.maxDepth) continue block0;
                                    this.availablePieces.addLast(new Entry(abstractvillagepiece, mutableobject1, l, p_236831_4_ + 1));
                                    continue block0;
                                }
                            }
                        }
                        continue;
                    }
                    LOGGER.warn("Empty or none existent fallback pool: {}", (Object)resourcelocation1);
                    continue;
                }
                LOGGER.warn("Empty or none existent pool: {}", (Object)resourcelocation);
            }
        }
    }

    static final class Entry {
        private final AbstractVillagePiece villagePiece;
        private final MutableObject<VoxelShape> free;
        private final int boundsTop;
        private final int depth;

        private Entry(AbstractVillagePiece p_i232042_1_, MutableObject<VoxelShape> p_i232042_2_, int p_i232042_3_, int p_i232042_4_) {
            this.villagePiece = p_i232042_1_;
            this.free = p_i232042_2_;
            this.boundsTop = p_i232042_3_;
            this.depth = p_i232042_4_;
        }
    }
}
