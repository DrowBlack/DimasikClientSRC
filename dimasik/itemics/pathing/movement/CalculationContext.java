package dimasik.itemics.pathing.movement;

import dimasik.itemics.Itemics;
import dimasik.itemics.api.IItemics;
import dimasik.itemics.cache.WorldData;
import dimasik.itemics.pathing.precompute.PrecomputedData;
import dimasik.itemics.utils.BlockStateInterface;
import dimasik.itemics.utils.ToolSet;
import dimasik.itemics.utils.pathing.BetterWorldBorder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CalculationContext {
    private static final ItemStack STACK_BUCKET_WATER = new ItemStack(Items.WATER_BUCKET);
    public final boolean safeForThreadedUse;
    public final IItemics itemics;
    public final World world;
    public final WorldData worldData;
    public final BlockStateInterface bsi;
    public final ToolSet toolSet;
    public final boolean hasWaterBucket;
    public final boolean hasThrowaway;
    public final boolean canSprint;
    protected final double placeBlockCost;
    public final boolean allowBreak;
    public final List<Block> allowBreakAnyway;
    public final boolean allowParkour;
    public final boolean allowParkourPlace;
    public final boolean allowJumpAt256;
    public final boolean allowParkourAscend;
    public final boolean assumeWalkOnWater;
    public final int frostWalker;
    public final boolean allowDiagonalDescend;
    public final boolean allowDiagonalAscend;
    public final boolean allowDownward;
    public final int maxFallHeightNoWater;
    public final int maxFallHeightBucket;
    public final double waterWalkSpeed;
    public final double breakBlockAdditionalCost;
    public double backtrackCostFavoringCoefficient;
    public double jumpPenalty;
    public final double walkOnWaterOnePenalty;
    public final BetterWorldBorder worldBorder;
    public final PrecomputedData precomputedData = new PrecomputedData();

    public CalculationContext(IItemics itemics) {
        this(itemics, false);
    }

    public CalculationContext(IItemics itemics, boolean forUseOnAnotherThread) {
        this.safeForThreadedUse = forUseOnAnotherThread;
        this.itemics = itemics;
        ClientPlayerEntity player = itemics.getPlayerContext().player();
        this.world = itemics.getPlayerContext().world();
        this.worldData = (WorldData)itemics.getWorldProvider().getCurrentWorld();
        this.bsi = new BlockStateInterface(this.world, this.worldData, forUseOnAnotherThread);
        this.toolSet = new ToolSet(player);
        this.hasThrowaway = (Boolean)Itemics.settings().allowPlace.value != false && ((Itemics)itemics).getInventoryBehavior().hasGenericThrowaway();
        this.hasWaterBucket = (Boolean)Itemics.settings().allowWaterBucketFall.value != false && PlayerInventory.isHotbar(player.inventory.getSlotFor(STACK_BUCKET_WATER)) && this.world.getDimensionKey() != World.THE_NETHER;
        this.canSprint = (Boolean)Itemics.settings().allowSprint.value != false && player.getFoodStats().getFoodLevel() > 6;
        this.placeBlockCost = (Double)Itemics.settings().blockPlacementPenalty.value;
        this.allowBreak = (Boolean)Itemics.settings().allowBreak.value;
        this.allowBreakAnyway = new ArrayList<Block>((Collection)Itemics.settings().allowBreakAnyway.value);
        this.allowParkour = (Boolean)Itemics.settings().allowParkour.value;
        this.allowParkourPlace = (Boolean)Itemics.settings().allowParkourPlace.value;
        this.allowJumpAt256 = (Boolean)Itemics.settings().allowJumpAt256.value;
        this.allowParkourAscend = (Boolean)Itemics.settings().allowParkourAscend.value;
        this.assumeWalkOnWater = (Boolean)Itemics.settings().assumeWalkOnWater.value;
        this.frostWalker = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FROST_WALKER, itemics.getPlayerContext().player());
        this.allowDiagonalDescend = (Boolean)Itemics.settings().allowDiagonalDescend.value;
        this.allowDiagonalAscend = (Boolean)Itemics.settings().allowDiagonalAscend.value;
        this.allowDownward = (Boolean)Itemics.settings().allowDownward.value;
        this.maxFallHeightNoWater = (Integer)Itemics.settings().maxFallHeightNoWater.value;
        this.maxFallHeightBucket = (Integer)Itemics.settings().maxFallHeightBucket.value;
        int depth = EnchantmentHelper.getDepthStriderModifier(player);
        if (depth > 3) {
            depth = 3;
        }
        float mult = (float)depth / 3.0f;
        this.waterWalkSpeed = 9.09090909090909 * (double)(1.0f - mult) + 4.63284688441047 * (double)mult;
        this.breakBlockAdditionalCost = (Double)Itemics.settings().blockBreakAdditionalPenalty.value;
        this.backtrackCostFavoringCoefficient = (Double)Itemics.settings().backtrackCostFavoringCoefficient.value;
        this.jumpPenalty = (Double)Itemics.settings().jumpPenalty.value;
        this.walkOnWaterOnePenalty = (Double)Itemics.settings().walkOnWaterOnePenalty.value;
        this.worldBorder = new BetterWorldBorder(this.world.getWorldBorder());
    }

    public final IItemics getItemics() {
        return this.itemics;
    }

    public BlockState get(int x, int y, int z) {
        return this.bsi.get0(x, y, z);
    }

    public boolean isLoaded(int x, int z) {
        return this.bsi.isLoaded(x, z);
    }

    public BlockState get(BlockPos pos) {
        return this.get(pos.getX(), pos.getY(), pos.getZ());
    }

    public Block getBlock(int x, int y, int z) {
        return this.get(x, y, z).getBlock();
    }

    public double costOfPlacingAt(int x, int y, int z, BlockState current) {
        if (!this.hasThrowaway) {
            return 1000000.0;
        }
        if (this.isPossiblyProtected(x, y, z)) {
            return 1000000.0;
        }
        if (!this.worldBorder.canPlaceAt(x, z)) {
            return 1000000.0;
        }
        return this.placeBlockCost;
    }

    public double breakCostMultiplierAt(int x, int y, int z, BlockState current) {
        if (!this.allowBreak && !this.allowBreakAnyway.contains(current.getBlock())) {
            return 1000000.0;
        }
        if (this.isPossiblyProtected(x, y, z)) {
            return 1000000.0;
        }
        return 1.0;
    }

    public double placeBucketCost() {
        return this.placeBlockCost;
    }

    public boolean isPossiblyProtected(int x, int y, int z) {
        return false;
    }
}
