package net.minecraft.util.math.shapes;

import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;

public class EntitySelectionContext
implements ISelectionContext {
    protected static final ISelectionContext DUMMY = new EntitySelectionContext(false, -1.7976931348623157E308, Items.AIR, fluid -> false){

        @Override
        public boolean func_216378_a(VoxelShape shape, BlockPos pos, boolean p_216378_3_) {
            return p_216378_3_;
        }
    };
    private final boolean sneaking;
    private final double posY;
    private final Item item;
    private final Predicate<Fluid> fluidPredicate;

    protected EntitySelectionContext(boolean sneaking, double posY, Item item, Predicate<Fluid> fluidPredicate) {
        this.sneaking = sneaking;
        this.posY = posY;
        this.item = item;
        this.fluidPredicate = fluidPredicate;
    }

    @Deprecated
    protected EntitySelectionContext(Entity entityIn) {
        this(entityIn.isDescending(), entityIn.getPosY(), entityIn instanceof LivingEntity ? ((LivingEntity)entityIn).getHeldItemMainhand().getItem() : Items.AIR, entityIn instanceof LivingEntity ? ((LivingEntity)entityIn)::func_230285_a_ : fluid -> false);
    }

    @Override
    public boolean hasItem(Item itemIn) {
        return this.item == itemIn;
    }

    @Override
    public boolean func_230426_a_(FluidState p_230426_1_, FlowingFluid p_230426_2_) {
        return this.fluidPredicate.test(p_230426_2_) && !p_230426_1_.getFluid().isEquivalentTo(p_230426_2_);
    }

    @Override
    public boolean getPosY() {
        return this.sneaking;
    }

    @Override
    public boolean func_216378_a(VoxelShape shape, BlockPos pos, boolean p_216378_3_) {
        return this.posY > (double)pos.getY() + shape.getEnd(Direction.Axis.Y) - (double)1.0E-5f;
    }
}
