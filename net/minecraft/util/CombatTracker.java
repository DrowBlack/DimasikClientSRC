package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.CombatEntry;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class CombatTracker {
    private final List<CombatEntry> combatEntries = Lists.newArrayList();
    private final LivingEntity fighter;
    private int lastDamageTime;
    private int combatStartTime;
    private int combatEndTime;
    private boolean inCombat;
    private boolean takingDamage;
    private String fallSuffix;

    public CombatTracker(LivingEntity fighterIn) {
        this.fighter = fighterIn;
    }

    public void calculateFallSuffix() {
        this.resetFallSuffix();
        Optional<BlockPos> optional = this.fighter.func_233644_dn_();
        if (optional.isPresent()) {
            BlockState blockstate = this.fighter.world.getBlockState(optional.get());
            this.fallSuffix = !blockstate.isIn(Blocks.LADDER) && !blockstate.isIn(BlockTags.TRAPDOORS) ? (blockstate.isIn(Blocks.VINE) ? "vines" : (!blockstate.isIn(Blocks.WEEPING_VINES) && !blockstate.isIn(Blocks.WEEPING_VINES_PLANT) ? (!blockstate.isIn(Blocks.TWISTING_VINES) && !blockstate.isIn(Blocks.TWISTING_VINES_PLANT) ? (blockstate.isIn(Blocks.SCAFFOLDING) ? "scaffolding" : "other_climbable") : "twisting_vines") : "weeping_vines")) : "ladder";
        } else if (this.fighter.isInWater()) {
            this.fallSuffix = "water";
        }
    }

    public void trackDamage(DamageSource damageSrc, float healthIn, float damageAmount) {
        this.reset();
        this.calculateFallSuffix();
        CombatEntry combatentry = new CombatEntry(damageSrc, this.fighter.ticksExisted, healthIn, damageAmount, this.fallSuffix, this.fighter.fallDistance);
        this.combatEntries.add(combatentry);
        this.lastDamageTime = this.fighter.ticksExisted;
        this.takingDamage = true;
        if (combatentry.isLivingDamageSrc() && !this.inCombat && this.fighter.isAlive()) {
            this.inCombat = true;
            this.combatEndTime = this.combatStartTime = this.fighter.ticksExisted;
            this.fighter.sendEnterCombat();
        }
    }

    public ITextComponent getDeathMessage() {
        ITextComponent itextcomponent;
        if (this.combatEntries.isEmpty()) {
            return new TranslationTextComponent("death.attack.generic", this.fighter.getDisplayName());
        }
        CombatEntry combatentry = this.getBestCombatEntry();
        CombatEntry combatentry1 = this.combatEntries.get(this.combatEntries.size() - 1);
        ITextComponent itextcomponent1 = combatentry1.getDamageSrcDisplayName();
        Entity entity = combatentry1.getDamageSrc().getTrueSource();
        if (combatentry != null && combatentry1.getDamageSrc() == DamageSource.FALL) {
            ITextComponent itextcomponent2 = combatentry.getDamageSrcDisplayName();
            if (combatentry.getDamageSrc() != DamageSource.FALL && combatentry.getDamageSrc() != DamageSource.OUT_OF_WORLD) {
                if (!(itextcomponent2 == null || itextcomponent1 != null && itextcomponent2.equals(itextcomponent1))) {
                    ItemStack itemstack1;
                    Entity entity1 = combatentry.getDamageSrc().getTrueSource();
                    ItemStack itemStack = itemstack1 = entity1 instanceof LivingEntity ? ((LivingEntity)entity1).getHeldItemMainhand() : ItemStack.EMPTY;
                    itextcomponent = !itemstack1.isEmpty() && itemstack1.hasDisplayName() ? new TranslationTextComponent("death.fell.assist.item", this.fighter.getDisplayName(), itextcomponent2, itemstack1.getTextComponent()) : new TranslationTextComponent("death.fell.assist", this.fighter.getDisplayName(), itextcomponent2);
                } else if (itextcomponent1 != null) {
                    ItemStack itemstack;
                    ItemStack itemStack = itemstack = entity instanceof LivingEntity ? ((LivingEntity)entity).getHeldItemMainhand() : ItemStack.EMPTY;
                    itextcomponent = !itemstack.isEmpty() && itemstack.hasDisplayName() ? new TranslationTextComponent("death.fell.finish.item", this.fighter.getDisplayName(), itextcomponent1, itemstack.getTextComponent()) : new TranslationTextComponent("death.fell.finish", this.fighter.getDisplayName(), itextcomponent1);
                } else {
                    itextcomponent = new TranslationTextComponent("death.fell.killer", this.fighter.getDisplayName());
                }
            } else {
                itextcomponent = new TranslationTextComponent("death.fell.accident." + this.getFallSuffix(combatentry), this.fighter.getDisplayName());
            }
        } else {
            itextcomponent = combatentry1.getDamageSrc().getDeathMessage(this.fighter);
        }
        return itextcomponent;
    }

    @Nullable
    public LivingEntity getBestAttacker() {
        LivingEntity livingentity = null;
        PlayerEntity playerentity = null;
        float f = 0.0f;
        float f1 = 0.0f;
        for (CombatEntry combatentry : this.combatEntries) {
            if (combatentry.getDamageSrc().getTrueSource() instanceof PlayerEntity && (playerentity == null || combatentry.getDamage() > f1)) {
                f1 = combatentry.getDamage();
                playerentity = (PlayerEntity)combatentry.getDamageSrc().getTrueSource();
            }
            if (!(combatentry.getDamageSrc().getTrueSource() instanceof LivingEntity) || livingentity != null && !(combatentry.getDamage() > f)) continue;
            f = combatentry.getDamage();
            livingentity = (LivingEntity)combatentry.getDamageSrc().getTrueSource();
        }
        return playerentity != null && f1 >= f / 3.0f ? playerentity : livingentity;
    }

    @Nullable
    private CombatEntry getBestCombatEntry() {
        CombatEntry combatentry = null;
        CombatEntry combatentry1 = null;
        float f = 0.0f;
        float f1 = 0.0f;
        for (int i = 0; i < this.combatEntries.size(); ++i) {
            CombatEntry combatentry3;
            CombatEntry combatentry2 = this.combatEntries.get(i);
            CombatEntry combatEntry = combatentry3 = i > 0 ? this.combatEntries.get(i - 1) : null;
            if ((combatentry2.getDamageSrc() == DamageSource.FALL || combatentry2.getDamageSrc() == DamageSource.OUT_OF_WORLD) && combatentry2.getDamageAmount() > 0.0f && (combatentry == null || combatentry2.getDamageAmount() > f1)) {
                combatentry = i > 0 ? combatentry3 : combatentry2;
                f1 = combatentry2.getDamageAmount();
            }
            if (combatentry2.getFallSuffix() == null || combatentry1 != null && !(combatentry2.getDamage() > f)) continue;
            combatentry1 = combatentry2;
            f = combatentry2.getDamage();
        }
        if (f1 > 5.0f && combatentry != null) {
            return combatentry;
        }
        return f > 5.0f && combatentry1 != null ? combatentry1 : null;
    }

    private String getFallSuffix(CombatEntry entry) {
        return entry.getFallSuffix() == null ? "generic" : entry.getFallSuffix();
    }

    public int getCombatDuration() {
        return this.inCombat ? this.fighter.ticksExisted - this.combatStartTime : this.combatEndTime - this.combatStartTime;
    }

    private void resetFallSuffix() {
        this.fallSuffix = null;
    }

    public void reset() {
        int i;
        int n = i = this.inCombat ? 300 : 100;
        if (this.takingDamage && (!this.fighter.isAlive() || this.fighter.ticksExisted - this.lastDamageTime > i)) {
            boolean flag = this.inCombat;
            this.takingDamage = false;
            this.inCombat = false;
            this.combatEndTime = this.fighter.ticksExisted;
            if (flag) {
                this.fighter.sendEndCombat();
            }
            this.combatEntries.clear();
        }
    }

    public LivingEntity getFighter() {
        return this.fighter;
    }
}
