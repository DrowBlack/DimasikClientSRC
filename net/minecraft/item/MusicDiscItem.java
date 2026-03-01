package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class MusicDiscItem
extends Item {
    private static final Map<SoundEvent, MusicDiscItem> RECORDS = Maps.newHashMap();
    private final int comparatorValue;
    private final SoundEvent sound;

    protected MusicDiscItem(int comparatorValueIn, SoundEvent soundIn, Item.Properties builder) {
        super(builder);
        this.comparatorValue = comparatorValueIn;
        this.sound = soundIn;
        RECORDS.put(this.sound, this);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        BlockPos blockpos;
        World world = context.getWorld();
        BlockState blockstate = world.getBlockState(blockpos = context.getPos());
        if (blockstate.isIn(Blocks.JUKEBOX) && !blockstate.get(JukeboxBlock.HAS_RECORD).booleanValue()) {
            ItemStack itemstack = context.getItem();
            if (!world.isRemote) {
                ((JukeboxBlock)Blocks.JUKEBOX).insertRecord(world, blockpos, blockstate, itemstack);
                world.playEvent(null, 1010, blockpos, Item.getIdFromItem(this));
                itemstack.shrink(1);
                PlayerEntity playerentity = context.getPlayer();
                if (playerentity != null) {
                    playerentity.addStat(Stats.PLAY_RECORD);
                }
            }
            return ActionResultType.func_233537_a_(world.isRemote);
        }
        return ActionResultType.PASS;
    }

    public int getComparatorValue() {
        return this.comparatorValue;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(this.getDescription().mergeStyle(TextFormatting.GRAY));
    }

    public IFormattableTextComponent getDescription() {
        return new TranslationTextComponent(this.getTranslationKey() + ".desc");
    }

    @Nullable
    public static MusicDiscItem getBySound(SoundEvent soundIn) {
        return RECORDS.get(soundIn);
    }

    public SoundEvent getSound() {
        return this.sound;
    }
}
