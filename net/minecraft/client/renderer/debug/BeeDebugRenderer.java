package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.debug.PathfindingDebugRenderer;
import net.minecraft.dispenser.IPosition;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.RandomObjectDescriptor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3i;

public class BeeDebugRenderer
implements DebugRenderer.IDebugRenderer {
    private final Minecraft field_228958_a_;
    private final Map<BlockPos, Hive> field_228959_b_ = Maps.newHashMap();
    private final Map<UUID, Bee> field_228960_c_ = Maps.newHashMap();
    private UUID field_228961_d_;

    public BeeDebugRenderer(Minecraft p_i226027_1_) {
        this.field_228958_a_ = p_i226027_1_;
    }

    @Override
    public void clear() {
        this.field_228959_b_.clear();
        this.field_228960_c_.clear();
        this.field_228961_d_ = null;
    }

    public void func_228966_a_(Hive p_228966_1_) {
        this.field_228959_b_.put(p_228966_1_.field_229011_a_, p_228966_1_);
    }

    public void func_228964_a_(Bee p_228964_1_) {
        this.field_228960_c_.put(p_228964_1_.field_228998_a_, p_228964_1_);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, double camX, double camY, double camZ) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        this.func_228987_c_();
        this.func_228981_b_();
        this.func_228989_d_();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
        if (!this.field_228958_a_.player.isSpectator()) {
            this.func_228997_i_();
        }
    }

    private void func_228981_b_() {
        this.field_228960_c_.entrySet().removeIf(p_228984_1_ -> this.field_228958_a_.world.getEntityByID(((Bee)p_228984_1_.getValue()).field_228999_b_) == null);
    }

    private void func_228987_c_() {
        long i = this.field_228958_a_.world.getGameTime() - 20L;
        this.field_228959_b_.entrySet().removeIf(p_228962_2_ -> ((Hive)p_228962_2_.getValue()).field_229016_f_ < i);
    }

    private void func_228989_d_() {
        BlockPos blockpos = this.func_228995_g_().getBlockPos();
        this.field_228960_c_.values().forEach(p_228994_1_ -> {
            if (this.func_228992_e_((Bee)p_228994_1_)) {
                this.func_228988_c_((Bee)p_228994_1_);
            }
        });
        this.func_228993_f_();
        for (BlockPos blockpos1 : this.field_228959_b_.keySet()) {
            if (!blockpos.withinDistance(blockpos1, 30.0)) continue;
            BeeDebugRenderer.func_228968_a_(blockpos1);
        }
        Map<BlockPos, Set<UUID>> map = this.func_228991_e_();
        this.field_228959_b_.values().forEach(p_228973_3_ -> {
            if (blockpos.withinDistance(p_228973_3_.field_229011_a_, 30.0)) {
                Set set = (Set)map.get(p_228973_3_.field_229011_a_);
                this.func_228967_a_((Hive)p_228973_3_, set == null ? Sets.newHashSet() : set);
            }
        });
        this.func_228996_h_().forEach((p_228971_2_, p_228971_3_) -> {
            if (blockpos.withinDistance((Vector3i)p_228971_2_, 30.0)) {
                this.func_228972_a_((BlockPos)p_228971_2_, (List<String>)p_228971_3_);
            }
        });
    }

    private Map<BlockPos, Set<UUID>> func_228991_e_() {
        HashMap<BlockPos, Set<UUID>> map = Maps.newHashMap();
        this.field_228960_c_.values().forEach(p_228985_1_ -> p_228985_1_.field_229006_i_.forEach(p_228986_2_ -> map.computeIfAbsent((BlockPos)p_228986_2_, p_241727_0_ -> Sets.newHashSet()).add(p_228985_1_.func_229007_a_())));
        return map;
    }

    private void func_228993_f_() {
        HashMap map = Maps.newHashMap();
        this.field_228960_c_.values().stream().filter(Bee::func_229010_c_).forEach(p_241722_1_ -> map.computeIfAbsent(p_241722_1_.field_229003_f_, p_241726_0_ -> Sets.newHashSet()).add(p_241722_1_.func_229007_a_()));
        map.entrySet().forEach(p_228978_0_ -> {
            BlockPos blockpos = (BlockPos)p_228978_0_.getKey();
            Set set = (Set)p_228978_0_.getValue();
            Set set1 = set.stream().map(RandomObjectDescriptor::getRandomObjectDescriptor).collect(Collectors.toSet());
            int i = 1;
            BeeDebugRenderer.func_228976_a_(set1.toString(), blockpos, i++, -256);
            BeeDebugRenderer.func_228976_a_("Flower", blockpos, i++, -1);
            float f = 0.05f;
            BeeDebugRenderer.func_228969_a_(blockpos, 0.05f, 0.8f, 0.8f, 0.0f, 0.3f);
        });
    }

    private static String func_228977_a_(Collection<UUID> p_228977_0_) {
        if (p_228977_0_.isEmpty()) {
            return "-";
        }
        return p_228977_0_.size() > 3 ? p_228977_0_.size() + " bees" : p_228977_0_.stream().map(RandomObjectDescriptor::getRandomObjectDescriptor).collect(Collectors.toSet()).toString();
    }

    private static void func_228968_a_(BlockPos p_228968_0_) {
        float f = 0.05f;
        BeeDebugRenderer.func_228969_a_(p_228968_0_, 0.05f, 0.2f, 0.2f, 1.0f, 0.3f);
    }

    private void func_228972_a_(BlockPos p_228972_1_, List<String> p_228972_2_) {
        float f = 0.05f;
        BeeDebugRenderer.func_228969_a_(p_228972_1_, 0.05f, 0.2f, 0.2f, 1.0f, 0.3f);
        BeeDebugRenderer.func_228976_a_(String.valueOf(p_228972_2_), p_228972_1_, 0, -256);
        BeeDebugRenderer.func_228976_a_("Ghost Hive", p_228972_1_, 1, -65536);
    }

    private static void func_228969_a_(BlockPos p_228969_0_, float p_228969_1_, float p_228969_2_, float p_228969_3_, float p_228969_4_, float p_228969_5_) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        DebugRenderer.renderBox(p_228969_0_, p_228969_1_, p_228969_2_, p_228969_3_, p_228969_4_, p_228969_5_);
    }

    private void func_228967_a_(Hive p_228967_1_, Collection<UUID> p_228967_2_) {
        int i = 0;
        if (!p_228967_2_.isEmpty()) {
            BeeDebugRenderer.func_228975_a_("Blacklisted by " + BeeDebugRenderer.func_228977_a_(p_228967_2_), p_228967_1_, i++, -65536);
        }
        BeeDebugRenderer.func_228975_a_("Out: " + BeeDebugRenderer.func_228977_a_(this.func_228983_b_(p_228967_1_.field_229011_a_)), p_228967_1_, i++, -3355444);
        if (p_228967_1_.field_229013_c_ == 0) {
            BeeDebugRenderer.func_228975_a_("In: -", p_228967_1_, i++, -256);
        } else if (p_228967_1_.field_229013_c_ == 1) {
            BeeDebugRenderer.func_228975_a_("In: 1 bee", p_228967_1_, i++, -256);
        } else {
            BeeDebugRenderer.func_228975_a_("In: " + p_228967_1_.field_229013_c_ + " bees", p_228967_1_, i++, -256);
        }
        BeeDebugRenderer.func_228975_a_("Honey: " + p_228967_1_.field_229014_d_, p_228967_1_, i++, -23296);
        BeeDebugRenderer.func_228975_a_(p_228967_1_.field_229012_b_ + (p_228967_1_.field_229015_e_ ? " (sedated)" : ""), p_228967_1_, i++, -1);
    }

    private void func_228982_b_(Bee p_228982_1_) {
        if (p_228982_1_.field_229001_d_ != null) {
            PathfindingDebugRenderer.func_229032_a_(p_228982_1_.field_229001_d_, 0.5f, false, false, this.func_228995_g_().getProjectedView().getX(), this.func_228995_g_().getProjectedView().getY(), this.func_228995_g_().getProjectedView().getZ());
        }
    }

    private void func_228988_c_(Bee p_228988_1_) {
        boolean flag = this.func_228990_d_(p_228988_1_);
        int i = 0;
        BeeDebugRenderer.func_228974_a_(p_228988_1_.field_229000_c_, i++, p_228988_1_.toString(), -1, 0.03f);
        if (p_228988_1_.field_229002_e_ == null) {
            BeeDebugRenderer.func_228974_a_(p_228988_1_.field_229000_c_, i++, "No hive", -98404, 0.02f);
        } else {
            BeeDebugRenderer.func_228974_a_(p_228988_1_.field_229000_c_, i++, "Hive: " + this.func_228965_a_(p_228988_1_, p_228988_1_.field_229002_e_), -256, 0.02f);
        }
        if (p_228988_1_.field_229003_f_ == null) {
            BeeDebugRenderer.func_228974_a_(p_228988_1_.field_229000_c_, i++, "No flower", -98404, 0.02f);
        } else {
            BeeDebugRenderer.func_228974_a_(p_228988_1_.field_229000_c_, i++, "Flower: " + this.func_228965_a_(p_228988_1_, p_228988_1_.field_229003_f_), -256, 0.02f);
        }
        for (String s : p_228988_1_.field_229005_h_) {
            BeeDebugRenderer.func_228974_a_(p_228988_1_.field_229000_c_, i++, s, -16711936, 0.02f);
        }
        if (flag) {
            this.func_228982_b_(p_228988_1_);
        }
        if (p_228988_1_.field_229004_g_ > 0) {
            int j = p_228988_1_.field_229004_g_ < 600 ? -3355444 : -23296;
            BeeDebugRenderer.func_228974_a_(p_228988_1_.field_229000_c_, i++, "Travelling: " + p_228988_1_.field_229004_g_ + " ticks", j, 0.02f);
        }
    }

    private static void func_228975_a_(String p_228975_0_, Hive p_228975_1_, int p_228975_2_, int p_228975_3_) {
        BlockPos blockpos = p_228975_1_.field_229011_a_;
        BeeDebugRenderer.func_228976_a_(p_228975_0_, blockpos, p_228975_2_, p_228975_3_);
    }

    private static void func_228976_a_(String p_228976_0_, BlockPos p_228976_1_, int p_228976_2_, int p_228976_3_) {
        double d0 = 1.3;
        double d1 = 0.2;
        double d2 = (double)p_228976_1_.getX() + 0.5;
        double d3 = (double)p_228976_1_.getY() + 1.3 + (double)p_228976_2_ * 0.2;
        double d4 = (double)p_228976_1_.getZ() + 0.5;
        DebugRenderer.renderText(p_228976_0_, d2, d3, d4, p_228976_3_, 0.02f, true, 0.0f, true);
    }

    private static void func_228974_a_(IPosition p_228974_0_, int p_228974_1_, String p_228974_2_, int p_228974_3_, float p_228974_4_) {
        double d0 = 2.4;
        double d1 = 0.25;
        BlockPos blockpos = new BlockPos(p_228974_0_);
        double d2 = (double)blockpos.getX() + 0.5;
        double d3 = p_228974_0_.getY() + 2.4 + (double)p_228974_1_ * 0.25;
        double d4 = (double)blockpos.getZ() + 0.5;
        float f = 0.5f;
        DebugRenderer.renderText(p_228974_2_, d2, d3, d4, p_228974_3_, p_228974_4_, false, 0.5f, true);
    }

    private ActiveRenderInfo func_228995_g_() {
        return this.field_228958_a_.gameRenderer.getActiveRenderInfo();
    }

    private String func_228965_a_(Bee p_228965_1_, BlockPos p_228965_2_) {
        float f = MathHelper.sqrt(p_228965_2_.distanceSq(p_228965_1_.field_229000_c_.getX(), p_228965_1_.field_229000_c_.getY(), p_228965_1_.field_229000_c_.getZ(), true));
        double d0 = (double)Math.round(f * 10.0f) / 10.0;
        return p_228965_2_.getCoordinatesAsString() + " (dist " + d0 + ")";
    }

    private boolean func_228990_d_(Bee p_228990_1_) {
        return Objects.equals(this.field_228961_d_, p_228990_1_.field_228998_a_);
    }

    private boolean func_228992_e_(Bee p_228992_1_) {
        ClientPlayerEntity playerentity = this.field_228958_a_.player;
        BlockPos blockpos = new BlockPos(playerentity.getPosX(), p_228992_1_.field_229000_c_.getY(), playerentity.getPosZ());
        BlockPos blockpos1 = new BlockPos(p_228992_1_.field_229000_c_);
        return blockpos.withinDistance(blockpos1, 30.0);
    }

    private Collection<UUID> func_228983_b_(BlockPos p_228983_1_) {
        return this.field_228960_c_.values().stream().filter(p_228970_1_ -> p_228970_1_.func_229008_a_(p_228983_1_)).map(Bee::func_229007_a_).collect(Collectors.toSet());
    }

    private Map<BlockPos, List<String>> func_228996_h_() {
        HashMap<BlockPos, List<String>> map = Maps.newHashMap();
        for (Bee beedebugrenderer$bee : this.field_228960_c_.values()) {
            if (beedebugrenderer$bee.field_229002_e_ == null || this.field_228959_b_.containsKey(beedebugrenderer$bee.field_229002_e_)) continue;
            map.computeIfAbsent(beedebugrenderer$bee.field_229002_e_, p_241725_0_ -> Lists.newArrayList()).add(beedebugrenderer$bee.func_229009_b_());
        }
        return map;
    }

    private void func_228997_i_() {
        DebugRenderer.getTargetEntity(this.field_228958_a_.getRenderViewEntity(), 8).ifPresent(p_228963_1_ -> {
            this.field_228961_d_ = p_228963_1_.getUniqueID();
        });
    }

    public static class Hive {
        public final BlockPos field_229011_a_;
        public final String field_229012_b_;
        public final int field_229013_c_;
        public final int field_229014_d_;
        public final boolean field_229015_e_;
        public final long field_229016_f_;

        public Hive(BlockPos p_i226029_1_, String p_i226029_2_, int p_i226029_3_, int p_i226029_4_, boolean p_i226029_5_, long p_i226029_6_) {
            this.field_229011_a_ = p_i226029_1_;
            this.field_229012_b_ = p_i226029_2_;
            this.field_229013_c_ = p_i226029_3_;
            this.field_229014_d_ = p_i226029_4_;
            this.field_229015_e_ = p_i226029_5_;
            this.field_229016_f_ = p_i226029_6_;
        }
    }

    public static class Bee {
        public final UUID field_228998_a_;
        public final int field_228999_b_;
        public final IPosition field_229000_c_;
        @Nullable
        public final Path field_229001_d_;
        @Nullable
        public final BlockPos field_229002_e_;
        @Nullable
        public final BlockPos field_229003_f_;
        public final int field_229004_g_;
        public final List<String> field_229005_h_ = Lists.newArrayList();
        public final Set<BlockPos> field_229006_i_ = Sets.newHashSet();

        public Bee(UUID p_i226028_1_, int p_i226028_2_, IPosition p_i226028_3_, Path p_i226028_4_, BlockPos p_i226028_5_, BlockPos p_i226028_6_, int p_i226028_7_) {
            this.field_228998_a_ = p_i226028_1_;
            this.field_228999_b_ = p_i226028_2_;
            this.field_229000_c_ = p_i226028_3_;
            this.field_229001_d_ = p_i226028_4_;
            this.field_229002_e_ = p_i226028_5_;
            this.field_229003_f_ = p_i226028_6_;
            this.field_229004_g_ = p_i226028_7_;
        }

        public boolean func_229008_a_(BlockPos p_229008_1_) {
            return this.field_229002_e_ != null && this.field_229002_e_.equals(p_229008_1_);
        }

        public UUID func_229007_a_() {
            return this.field_228998_a_;
        }

        public String func_229009_b_() {
            return RandomObjectDescriptor.getRandomObjectDescriptor(this.field_228998_a_);
        }

        public String toString() {
            return this.func_229009_b_();
        }

        public boolean func_229010_c_() {
            return this.field_229003_f_ != null;
        }
    }
}
