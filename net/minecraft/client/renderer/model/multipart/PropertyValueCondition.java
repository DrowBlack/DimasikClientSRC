package net.minecraft.client.renderer.model.multipart;

import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.multipart.ICondition;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;

public class PropertyValueCondition
implements ICondition {
    private static final Splitter SPLITTER = Splitter.on('|').omitEmptyStrings();
    private final String key;
    private final String value;

    public PropertyValueCondition(String keyIn, String valueIn) {
        this.key = keyIn;
        this.value = valueIn;
    }

    @Override
    public Predicate<BlockState> getPredicate(StateContainer<Block, BlockState> p_getPredicate_1_) {
        Predicate<BlockState> predicate;
        List<String> list;
        boolean flag;
        Property<?> property = p_getPredicate_1_.getProperty(this.key);
        if (property == null) {
            throw new RuntimeException(String.format("Unknown property '%s' on '%s'", this.key, p_getPredicate_1_.getOwner().toString()));
        }
        String s = this.value;
        boolean bl = flag = !s.isEmpty() && s.charAt(0) == '!';
        if (flag) {
            s = s.substring(1);
        }
        if ((list = SPLITTER.splitToList(s)).isEmpty()) {
            throw new RuntimeException(String.format("Empty value '%s' for property '%s' on '%s'", this.value, this.key, p_getPredicate_1_.getOwner().toString()));
        }
        if (list.size() == 1) {
            predicate = this.makePropertyPredicate(p_getPredicate_1_, property, s);
        } else {
            List list1 = list.stream().map(value -> this.makePropertyPredicate(p_getPredicate_1_, property, (String)value)).collect(Collectors.toList());
            predicate = state -> list1.stream().anyMatch(pred -> pred.test(state));
        }
        return flag ? predicate.negate() : predicate;
    }

    private Predicate<BlockState> makePropertyPredicate(StateContainer<Block, BlockState> container, Property<?> property, String value) {
        Optional<?> optional = property.parseValue(value);
        if (!optional.isPresent()) {
            throw new RuntimeException(String.format("Unknown value '%s' for property '%s' on '%s' in '%s'", value, this.key, container.getOwner().toString(), this.value));
        }
        return state -> state.get(property).equals(optional.get());
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("key", this.key).add("value", this.value).toString();
    }
}
