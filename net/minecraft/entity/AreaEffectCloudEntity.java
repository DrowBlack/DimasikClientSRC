package net.minecraft.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.material.PushReaction;
import net.minecraft.command.arguments.ParticleArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AreaEffectCloudEntity
extends Entity {
    private static final Logger PRIVATE_LOGGER = LogManager.getLogger();
    private static final DataParameter<Float> RADIUS = EntityDataManager.createKey(AreaEffectCloudEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> COLOR = EntityDataManager.createKey(AreaEffectCloudEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> IGNORE_RADIUS = EntityDataManager.createKey(AreaEffectCloudEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<IParticleData> PARTICLE = EntityDataManager.createKey(AreaEffectCloudEntity.class, DataSerializers.PARTICLE_DATA);
    private Potion potion = Potions.EMPTY;
    private final List<EffectInstance> effects = Lists.newArrayList();
    private final Map<Entity, Integer> reapplicationDelayMap = Maps.newHashMap();
    private int duration = 600;
    private int waitTime = 20;
    private int reapplicationDelay = 20;
    private boolean colorSet;
    private int durationOnUse;
    private float radiusOnUse;
    private float radiusPerTick;
    private LivingEntity owner;
    private UUID ownerUniqueId;

    public AreaEffectCloudEntity(EntityType<? extends AreaEffectCloudEntity> cloud, World world) {
        super(cloud, world);
        this.noClip = true;
        this.setRadius(3.0f);
    }

    public AreaEffectCloudEntity(World worldIn, double x, double y, double z) {
        this((EntityType<? extends AreaEffectCloudEntity>)EntityType.AREA_EFFECT_CLOUD, worldIn);
        this.setPosition(x, y, z);
    }

    @Override
    protected void registerData() {
        this.getDataManager().register(COLOR, 0);
        this.getDataManager().register(RADIUS, Float.valueOf(0.5f));
        this.getDataManager().register(IGNORE_RADIUS, false);
        this.getDataManager().register(PARTICLE, ParticleTypes.ENTITY_EFFECT);
    }

    public void setRadius(float radiusIn) {
        if (!this.world.isRemote) {
            this.getDataManager().set(RADIUS, Float.valueOf(radiusIn));
        }
    }

    @Override
    public void recalculateSize() {
        double d0 = this.getPosX();
        double d1 = this.getPosY();
        double d2 = this.getPosZ();
        super.recalculateSize();
        this.setPosition(d0, d1, d2);
    }

    public float getRadius() {
        return this.getDataManager().get(RADIUS).floatValue();
    }

    public void setPotion(Potion potionIn) {
        this.potion = potionIn;
        if (!this.colorSet) {
            this.updateFixedColor();
        }
    }

    private void updateFixedColor() {
        if (this.potion == Potions.EMPTY && this.effects.isEmpty()) {
            this.getDataManager().set(COLOR, 0);
        } else {
            this.getDataManager().set(COLOR, PotionUtils.getPotionColorFromEffectList(PotionUtils.mergeEffects(this.potion, this.effects)));
        }
    }

    public void addEffect(EffectInstance effect) {
        this.effects.add(effect);
        if (!this.colorSet) {
            this.updateFixedColor();
        }
    }

    public int getColor() {
        return this.getDataManager().get(COLOR);
    }

    public void setColor(int colorIn) {
        this.colorSet = true;
        this.getDataManager().set(COLOR, colorIn);
    }

    public IParticleData getParticleData() {
        return this.getDataManager().get(PARTICLE);
    }

    public void setParticleData(IParticleData particleData) {
        this.getDataManager().set(PARTICLE, particleData);
    }

    protected void setIgnoreRadius(boolean ignoreRadius) {
        this.getDataManager().set(IGNORE_RADIUS, ignoreRadius);
    }

    public boolean shouldIgnoreRadius() {
        return this.getDataManager().get(IGNORE_RADIUS);
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int durationIn) {
        this.duration = durationIn;
    }

    @Override
    public void tick() {
        block23: {
            boolean flag1;
            float f;
            boolean flag;
            block21: {
                IParticleData iparticledata;
                block22: {
                    super.tick();
                    flag = this.shouldIgnoreRadius();
                    f = this.getRadius();
                    if (!this.world.isRemote) break block21;
                    iparticledata = this.getParticleData();
                    if (!flag) break block22;
                    if (!this.rand.nextBoolean()) break block23;
                    for (int i = 0; i < 2; ++i) {
                        float f1 = this.rand.nextFloat() * ((float)Math.PI * 2);
                        float f2 = MathHelper.sqrt(this.rand.nextFloat()) * 0.2f;
                        float f3 = MathHelper.cos(f1) * f2;
                        float f4 = MathHelper.sin(f1) * f2;
                        if (iparticledata.getType() == ParticleTypes.ENTITY_EFFECT) {
                            int j = this.rand.nextBoolean() ? 0xFFFFFF : this.getColor();
                            int k = j >> 16 & 0xFF;
                            int l = j >> 8 & 0xFF;
                            int i1 = j & 0xFF;
                            this.world.addOptionalParticle(iparticledata, this.getPosX() + (double)f3, this.getPosY(), this.getPosZ() + (double)f4, (float)k / 255.0f, (float)l / 255.0f, (float)i1 / 255.0f);
                            continue;
                        }
                        this.world.addOptionalParticle(iparticledata, this.getPosX() + (double)f3, this.getPosY(), this.getPosZ() + (double)f4, 0.0, 0.0, 0.0);
                    }
                    break block23;
                }
                float f5 = (float)Math.PI * f * f;
                int k1 = 0;
                while ((float)k1 < f5) {
                    float f6 = this.rand.nextFloat() * ((float)Math.PI * 2);
                    float f7 = MathHelper.sqrt(this.rand.nextFloat()) * f;
                    float f8 = MathHelper.cos(f6) * f7;
                    float f9 = MathHelper.sin(f6) * f7;
                    if (iparticledata.getType() == ParticleTypes.ENTITY_EFFECT) {
                        int l1 = this.getColor();
                        int i2 = l1 >> 16 & 0xFF;
                        int j2 = l1 >> 8 & 0xFF;
                        int j1 = l1 & 0xFF;
                        this.world.addOptionalParticle(iparticledata, this.getPosX() + (double)f8, this.getPosY(), this.getPosZ() + (double)f9, (float)i2 / 255.0f, (float)j2 / 255.0f, (float)j1 / 255.0f);
                    } else {
                        this.world.addOptionalParticle(iparticledata, this.getPosX() + (double)f8, this.getPosY(), this.getPosZ() + (double)f9, (0.5 - this.rand.nextDouble()) * 0.15, 0.01f, (0.5 - this.rand.nextDouble()) * 0.15);
                    }
                    ++k1;
                }
                break block23;
            }
            if (this.ticksExisted >= this.waitTime + this.duration) {
                this.remove();
                return;
            }
            boolean bl = flag1 = this.ticksExisted < this.waitTime;
            if (flag != flag1) {
                this.setIgnoreRadius(flag1);
            }
            if (flag1) {
                return;
            }
            if (this.radiusPerTick != 0.0f) {
                if ((f += this.radiusPerTick) < 0.5f) {
                    this.remove();
                    return;
                }
                this.setRadius(f);
            }
            if (this.ticksExisted % 5 == 0) {
                Iterator<Map.Entry<Entity, Integer>> iterator = this.reapplicationDelayMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Entity, Integer> entry = iterator.next();
                    if (this.ticksExisted < entry.getValue()) continue;
                    iterator.remove();
                }
                ArrayList<EffectInstance> list = Lists.newArrayList();
                for (EffectInstance effectinstance1 : this.potion.getEffects()) {
                    list.add(new EffectInstance(effectinstance1.getPotion(), effectinstance1.getDuration() / 4, effectinstance1.getAmplifier(), effectinstance1.isAmbient(), effectinstance1.doesShowParticles()));
                }
                list.addAll(this.effects);
                if (list.isEmpty()) {
                    this.reapplicationDelayMap.clear();
                } else {
                    List<LivingEntity> list1 = this.world.getEntitiesWithinAABB(LivingEntity.class, this.getBoundingBox());
                    if (!list1.isEmpty()) {
                        for (LivingEntity livingentity : list1) {
                            double d1;
                            double d0;
                            double d2;
                            if (this.reapplicationDelayMap.containsKey(livingentity) || !livingentity.canBeHitWithPotion() || !((d2 = (d0 = livingentity.getPosX() - this.getPosX()) * d0 + (d1 = livingentity.getPosZ() - this.getPosZ()) * d1) <= (double)(f * f))) continue;
                            this.reapplicationDelayMap.put(livingentity, this.ticksExisted + this.reapplicationDelay);
                            for (EffectInstance effectinstance : list) {
                                if (effectinstance.getPotion().isInstant()) {
                                    effectinstance.getPotion().affectEntity(this, this.getOwner(), livingentity, effectinstance.getAmplifier(), 0.5);
                                    continue;
                                }
                                livingentity.addPotionEffect(new EffectInstance(effectinstance));
                            }
                            if (this.radiusOnUse != 0.0f) {
                                if ((f += this.radiusOnUse) < 0.5f) {
                                    this.remove();
                                    return;
                                }
                                this.setRadius(f);
                            }
                            if (this.durationOnUse == 0) continue;
                            this.duration += this.durationOnUse;
                            if (this.duration > 0) continue;
                            this.remove();
                            return;
                        }
                    }
                }
            }
        }
    }

    public void setRadiusOnUse(float radiusOnUseIn) {
        this.radiusOnUse = radiusOnUseIn;
    }

    public void setRadiusPerTick(float radiusPerTickIn) {
        this.radiusPerTick = radiusPerTickIn;
    }

    public void setWaitTime(int waitTimeIn) {
        this.waitTime = waitTimeIn;
    }

    public void setOwner(@Nullable LivingEntity ownerIn) {
        this.owner = ownerIn;
        this.ownerUniqueId = ownerIn == null ? null : ownerIn.getUniqueID();
    }

    @Nullable
    public LivingEntity getOwner() {
        Entity entity;
        if (this.owner == null && this.ownerUniqueId != null && this.world instanceof ServerWorld && (entity = ((ServerWorld)this.world).getEntityByUuid(this.ownerUniqueId)) instanceof LivingEntity) {
            this.owner = (LivingEntity)entity;
        }
        return this.owner;
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        this.ticksExisted = compound.getInt("Age");
        this.duration = compound.getInt("Duration");
        this.waitTime = compound.getInt("WaitTime");
        this.reapplicationDelay = compound.getInt("ReapplicationDelay");
        this.durationOnUse = compound.getInt("DurationOnUse");
        this.radiusOnUse = compound.getFloat("RadiusOnUse");
        this.radiusPerTick = compound.getFloat("RadiusPerTick");
        this.setRadius(compound.getFloat("Radius"));
        if (compound.hasUniqueId("Owner")) {
            this.ownerUniqueId = compound.getUniqueId("Owner");
        }
        if (compound.contains("Particle", 8)) {
            try {
                this.setParticleData(ParticleArgument.parseParticle(new StringReader(compound.getString("Particle"))));
            }
            catch (CommandSyntaxException commandsyntaxexception) {
                PRIVATE_LOGGER.warn("Couldn't load custom particle {}", (Object)compound.getString("Particle"), (Object)commandsyntaxexception);
            }
        }
        if (compound.contains("Color", 99)) {
            this.setColor(compound.getInt("Color"));
        }
        if (compound.contains("Potion", 8)) {
            this.setPotion(PotionUtils.getPotionTypeFromNBT(compound));
        }
        if (compound.contains("Effects", 9)) {
            ListNBT listnbt = compound.getList("Effects", 10);
            this.effects.clear();
            for (int i = 0; i < listnbt.size(); ++i) {
                EffectInstance effectinstance = EffectInstance.read(listnbt.getCompound(i));
                if (effectinstance == null) continue;
                this.addEffect(effectinstance);
            }
        }
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        compound.putInt("Age", this.ticksExisted);
        compound.putInt("Duration", this.duration);
        compound.putInt("WaitTime", this.waitTime);
        compound.putInt("ReapplicationDelay", this.reapplicationDelay);
        compound.putInt("DurationOnUse", this.durationOnUse);
        compound.putFloat("RadiusOnUse", this.radiusOnUse);
        compound.putFloat("RadiusPerTick", this.radiusPerTick);
        compound.putFloat("Radius", this.getRadius());
        compound.putString("Particle", this.getParticleData().getParameters());
        if (this.ownerUniqueId != null) {
            compound.putUniqueId("Owner", this.ownerUniqueId);
        }
        if (this.colorSet) {
            compound.putInt("Color", this.getColor());
        }
        if (this.potion != Potions.EMPTY && this.potion != null) {
            compound.putString("Potion", Registry.POTION.getKey(this.potion).toString());
        }
        if (!this.effects.isEmpty()) {
            ListNBT listnbt = new ListNBT();
            for (EffectInstance effectinstance : this.effects) {
                listnbt.add(effectinstance.write(new CompoundNBT()));
            }
            compound.put("Effects", listnbt);
        }
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (RADIUS.equals(key)) {
            this.recalculateSize();
        }
        super.notifyDataManagerChange(key);
    }

    @Override
    public PushReaction getPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return new SSpawnObjectPacket(this);
    }

    @Override
    public EntitySize getSize(Pose poseIn) {
        return EntitySize.flexible(this.getRadius() * 2.0f, 0.5f);
    }
}
