package net.minecraft.client.renderer.model.multipart;

import com.google.common.collect.Streams;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.multipart.ICondition;
import net.minecraft.state.StateContainer;

public class AndCondition
implements ICondition {
    private final Iterable<? extends ICondition> conditions;

    public AndCondition(Iterable<? extends ICondition> conditionsIn) {
        this.conditions = conditionsIn;
    }

    @Override
    public Predicate<BlockState> getPredicate(StateContainer<Block, BlockState> p_getPredicate_1_) {
        List list = Streams.stream(this.conditions).map(condition -> condition.getPredicate(p_getPredicate_1_)).collect(Collectors.toList());
        return state -> list.stream().allMatch(predicate -> predicate.test(state));
    }
}
