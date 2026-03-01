package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.math.shapes.IDoubleListMerger;

public class SimpleDoubleMerger
implements IDoubleListMerger {
    private final DoubleList list;

    public SimpleDoubleMerger(DoubleList list) {
        this.list = list;
    }

    @Override
    public boolean forMergedIndexes(IDoubleListMerger.IConsumer consumer) {
        for (int i = 0; i <= this.list.size(); ++i) {
            if (consumer.merge(i, i, i)) continue;
            return false;
        }
        return true;
    }

    @Override
    public DoubleList func_212435_a() {
        return this.list;
    }
}
