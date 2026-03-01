package net.minecraft.entity.passive.fish;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.fish.AbstractGroupFishEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

public class TropicalFishEntity
extends AbstractGroupFishEntity {
    private static final DataParameter<Integer> VARIANT = EntityDataManager.createKey(TropicalFishEntity.class, DataSerializers.VARINT);
    private static final ResourceLocation[] BODY_TEXTURES = new ResourceLocation[]{new ResourceLocation("textures/entity/fish/tropical_a.png"), new ResourceLocation("textures/entity/fish/tropical_b.png")};
    private static final ResourceLocation[] PATTERN_TEXTURES_A = new ResourceLocation[]{new ResourceLocation("textures/entity/fish/tropical_a_pattern_1.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_2.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_3.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_4.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_5.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_6.png")};
    private static final ResourceLocation[] PATTERN_TEXTURES_B = new ResourceLocation[]{new ResourceLocation("textures/entity/fish/tropical_b_pattern_1.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_2.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_3.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_4.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_5.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_6.png")};
    public static final int[] SPECIAL_VARIANTS = new int[]{TropicalFishEntity.pack(Type.STRIPEY, DyeColor.ORANGE, DyeColor.GRAY), TropicalFishEntity.pack(Type.FLOPPER, DyeColor.GRAY, DyeColor.GRAY), TropicalFishEntity.pack(Type.FLOPPER, DyeColor.GRAY, DyeColor.BLUE), TropicalFishEntity.pack(Type.CLAYFISH, DyeColor.WHITE, DyeColor.GRAY), TropicalFishEntity.pack(Type.SUNSTREAK, DyeColor.BLUE, DyeColor.GRAY), TropicalFishEntity.pack(Type.KOB, DyeColor.ORANGE, DyeColor.WHITE), TropicalFishEntity.pack(Type.SPOTTY, DyeColor.PINK, DyeColor.LIGHT_BLUE), TropicalFishEntity.pack(Type.BLOCKFISH, DyeColor.PURPLE, DyeColor.YELLOW), TropicalFishEntity.pack(Type.CLAYFISH, DyeColor.WHITE, DyeColor.RED), TropicalFishEntity.pack(Type.SPOTTY, DyeColor.WHITE, DyeColor.YELLOW), TropicalFishEntity.pack(Type.GLITTER, DyeColor.WHITE, DyeColor.GRAY), TropicalFishEntity.pack(Type.CLAYFISH, DyeColor.WHITE, DyeColor.ORANGE), TropicalFishEntity.pack(Type.DASHER, DyeColor.CYAN, DyeColor.PINK), TropicalFishEntity.pack(Type.BRINELY, DyeColor.LIME, DyeColor.LIGHT_BLUE), TropicalFishEntity.pack(Type.BETTY, DyeColor.RED, DyeColor.WHITE), TropicalFishEntity.pack(Type.SNOOPER, DyeColor.GRAY, DyeColor.RED), TropicalFishEntity.pack(Type.BLOCKFISH, DyeColor.RED, DyeColor.WHITE), TropicalFishEntity.pack(Type.FLOPPER, DyeColor.WHITE, DyeColor.YELLOW), TropicalFishEntity.pack(Type.KOB, DyeColor.RED, DyeColor.WHITE), TropicalFishEntity.pack(Type.SUNSTREAK, DyeColor.GRAY, DyeColor.WHITE), TropicalFishEntity.pack(Type.DASHER, DyeColor.CYAN, DyeColor.YELLOW), TropicalFishEntity.pack(Type.FLOPPER, DyeColor.YELLOW, DyeColor.YELLOW)};
    private boolean field_204228_bA = true;

    private static int pack(Type size, DyeColor pattern, DyeColor bodyColor) {
        return size.func_212550_a() & 0xFF | (size.func_212551_b() & 0xFF) << 8 | (pattern.getId() & 0xFF) << 16 | (bodyColor.getId() & 0xFF) << 24;
    }

    public TropicalFishEntity(EntityType<? extends TropicalFishEntity> p_i50242_1_, World p_i50242_2_) {
        super((EntityType<? extends AbstractGroupFishEntity>)p_i50242_1_, p_i50242_2_);
    }

    public static String func_212324_b(int p_212324_0_) {
        return "entity.minecraft.tropical_fish.predefined." + p_212324_0_;
    }

    public static DyeColor func_212326_d(int p_212326_0_) {
        return DyeColor.byId(TropicalFishEntity.getBodyColor(p_212326_0_));
    }

    public static DyeColor func_212323_p(int p_212323_0_) {
        return DyeColor.byId(TropicalFishEntity.getPatternColor(p_212323_0_));
    }

    public static String func_212327_q(int p_212327_0_) {
        int i = TropicalFishEntity.func_212325_s(p_212327_0_);
        int j = TropicalFishEntity.getPattern(p_212327_0_);
        return "entity.minecraft.tropical_fish.type." + Type.func_212548_a(i, j);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(VARIANT, 0);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("Variant", this.getVariant());
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setVariant(compound.getInt("Variant"));
    }

    public void setVariant(int p_204215_1_) {
        this.dataManager.set(VARIANT, p_204215_1_);
    }

    @Override
    public boolean isMaxGroupSize(int sizeIn) {
        return !this.field_204228_bA;
    }

    public int getVariant() {
        return this.dataManager.get(VARIANT);
    }

    @Override
    protected void setBucketData(ItemStack bucket) {
        super.setBucketData(bucket);
        CompoundNBT compoundnbt = bucket.getOrCreateTag();
        compoundnbt.putInt("BucketVariantTag", this.getVariant());
    }

    @Override
    protected ItemStack getFishBucket() {
        return new ItemStack(Items.TROPICAL_FISH_BUCKET);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_TROPICAL_FISH_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_TROPICAL_FISH_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_TROPICAL_FISH_HURT;
    }

    @Override
    protected SoundEvent getFlopSound() {
        return SoundEvents.ENTITY_TROPICAL_FISH_FLOP;
    }

    private static int getBodyColor(int p_204216_0_) {
        return (p_204216_0_ & 0xFF0000) >> 16;
    }

    public float[] func_204219_dC() {
        return DyeColor.byId(TropicalFishEntity.getBodyColor(this.getVariant())).getColorComponentValues();
    }

    private static int getPatternColor(int p_204212_0_) {
        return (p_204212_0_ & 0xFF000000) >> 24;
    }

    public float[] func_204222_dD() {
        return DyeColor.byId(TropicalFishEntity.getPatternColor(this.getVariant())).getColorComponentValues();
    }

    public static int func_212325_s(int p_212325_0_) {
        return Math.min(p_212325_0_ & 0xFF, 1);
    }

    public int getSize() {
        return TropicalFishEntity.func_212325_s(this.getVariant());
    }

    private static int getPattern(int p_204213_0_) {
        return Math.min((p_204213_0_ & 0xFF00) >> 8, 5);
    }

    public ResourceLocation getPatternTexture() {
        return TropicalFishEntity.func_212325_s(this.getVariant()) == 0 ? PATTERN_TEXTURES_A[TropicalFishEntity.getPattern(this.getVariant())] : PATTERN_TEXTURES_B[TropicalFishEntity.getPattern(this.getVariant())];
    }

    public ResourceLocation getBodyTexture() {
        return BODY_TEXTURES[TropicalFishEntity.func_212325_s(this.getVariant())];
    }

    @Override
    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        int l;
        int k;
        int j;
        int i;
        spawnDataIn = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        if (dataTag != null && dataTag.contains("BucketVariantTag", 3)) {
            this.setVariant(dataTag.getInt("BucketVariantTag"));
            return spawnDataIn;
        }
        if (spawnDataIn instanceof TropicalFishData) {
            TropicalFishData tropicalfishentity$tropicalfishdata = (TropicalFishData)spawnDataIn;
            i = tropicalfishentity$tropicalfishdata.size;
            j = tropicalfishentity$tropicalfishdata.pattern;
            k = tropicalfishentity$tropicalfishdata.bodyColor;
            l = tropicalfishentity$tropicalfishdata.patternColor;
        } else if ((double)this.rand.nextFloat() < 0.9) {
            int i1 = Util.getRandomInt(SPECIAL_VARIANTS, this.rand);
            i = i1 & 0xFF;
            j = (i1 & 0xFF00) >> 8;
            k = (i1 & 0xFF0000) >> 16;
            l = (i1 & 0xFF000000) >> 24;
            spawnDataIn = new TropicalFishData(this, i, j, k, l);
        } else {
            this.field_204228_bA = false;
            i = this.rand.nextInt(2);
            j = this.rand.nextInt(6);
            k = this.rand.nextInt(15);
            l = this.rand.nextInt(15);
        }
        this.setVariant(i | j << 8 | k << 16 | l << 24);
        return spawnDataIn;
    }

    static enum Type {
        KOB(0, 0),
        SUNSTREAK(0, 1),
        SNOOPER(0, 2),
        DASHER(0, 3),
        BRINELY(0, 4),
        SPOTTY(0, 5),
        FLOPPER(1, 0),
        STRIPEY(1, 1),
        GLITTER(1, 2),
        BLOCKFISH(1, 3),
        BETTY(1, 4),
        CLAYFISH(1, 5);

        private final int field_212552_m;
        private final int field_212553_n;
        private static final Type[] field_212554_o;

        private Type(int p_i49832_3_, int p_i49832_4_) {
            this.field_212552_m = p_i49832_3_;
            this.field_212553_n = p_i49832_4_;
        }

        public int func_212550_a() {
            return this.field_212552_m;
        }

        public int func_212551_b() {
            return this.field_212553_n;
        }

        public static String func_212548_a(int p_212548_0_, int p_212548_1_) {
            return field_212554_o[p_212548_1_ + 6 * p_212548_0_].func_212549_c();
        }

        public String func_212549_c() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        static {
            field_212554_o = Type.values();
        }
    }

    static class TropicalFishData
    extends AbstractGroupFishEntity.GroupData {
        private final int size;
        private final int pattern;
        private final int bodyColor;
        private final int patternColor;

        private TropicalFishData(TropicalFishEntity p_i49859_1_, int p_i49859_2_, int p_i49859_3_, int p_i49859_4_, int p_i49859_5_) {
            super(p_i49859_1_);
            this.size = p_i49859_2_;
            this.pattern = p_i49859_3_;
            this.bodyColor = p_i49859_4_;
            this.patternColor = p_i49859_5_;
        }
    }
}
