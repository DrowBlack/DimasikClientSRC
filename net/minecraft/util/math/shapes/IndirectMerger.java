package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.util.math.shapes.IDoubleListMerger;

public final class IndirectMerger
implements IDoubleListMerger {
    private final DoubleArrayList field_197856_a;
    private final IntArrayList list1;
    private final IntArrayList list2;

    protected IndirectMerger(DoubleList list1In, DoubleList list2In, boolean p_i47685_3_, boolean p_i47685_4_) {
        int i = 0;
        int j = 0;
        double d0 = Double.NaN;
        int k = list1In.size();
        int l = list2In.size();
        int i1 = k + l;
        this.field_197856_a = new DoubleArrayList(i1);
        this.list1 = new IntArrayList(i1);
        this.list2 = new IntArrayList(i1);
        while (true) {
            double d1;
            boolean flag1;
            boolean flag = i < k;
            boolean bl = flag1 = j < l;
            if (!flag && !flag1) {
                if (this.field_197856_a.isEmpty()) {
                    this.field_197856_a.add(Math.min(list1In.getDouble(k - 1), list2In.getDouble(l - 1)));
                }
                return;
            }
            boolean flag2 = flag && (!flag1 || list1In.getDouble(i) < list2In.getDouble(j) + 1.0E-7);
            double d = d1 = flag2 ? list1In.getDouble(i++) : list2In.getDouble(j++);
            if ((i == 0 || !flag) && !flag2 && !p_i47685_4_ || (j == 0 || !flag1) && flag2 && !p_i47685_3_) continue;
            if (!(d0 >= d1 - 1.0E-7)) {
                this.list1.add(i - 1);
                this.list2.add(j - 1);
                this.field_197856_a.add(d1);
                d0 = d1;
                continue;
            }
            if (this.field_197856_a.isEmpty()) continue;
            this.list1.set(this.list1.size() - 1, i - 1);
            this.list2.set(this.list2.size() - 1, j - 1);
        }
    }

    @Override
    public boolean forMergedIndexes(IDoubleListMerger.IConsumer consumer) {
        for (int i = 0; i < this.field_197856_a.size() - 1; ++i) {
            if (consumer.merge(this.list1.getInt(i), this.list2.getInt(i), i)) continue;
            return false;
        }
        return true;
    }

    @Override
    public DoubleList func_212435_a() {
        return this.field_197856_a;
    }
}
