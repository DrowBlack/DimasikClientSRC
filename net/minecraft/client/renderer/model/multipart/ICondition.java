package net.minecraft.client.renderer.model.multipart;

import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;

@FunctionalInterface
public interface ICondition {
    public static final ICondition TRUE = container -> state -> true;
    public static final ICondition FALSE = container -> state -> false;

    public Predicate<BlockState> getPredicate(StateContainer<Block, BlockState> var1);
}
