package net.minecraft.world;

import java.util.Comparator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;

public class NextTickListEntry<T> {
    private static long nextTickEntryID;
    private final T target;
    public final BlockPos position;
    public final long field_235017_b_;
    public final TickPriority priority;
    private final long tickEntryID = nextTickEntryID++;

    public NextTickListEntry(BlockPos positionIn, T p_i48977_2_) {
        this(positionIn, p_i48977_2_, 0L, TickPriority.NORMAL);
    }

    public NextTickListEntry(BlockPos positionIn, T p_i48978_2_, long scheduledTimeIn, TickPriority priorityIn) {
        this.position = positionIn.toImmutable();
        this.target = p_i48978_2_;
        this.field_235017_b_ = scheduledTimeIn;
        this.priority = priorityIn;
    }

    public boolean equals(Object p_equals_1_) {
        if (!(p_equals_1_ instanceof NextTickListEntry)) {
            return false;
        }
        NextTickListEntry nextticklistentry = (NextTickListEntry)p_equals_1_;
        return this.position.equals(nextticklistentry.position) && this.target == nextticklistentry.target;
    }

    public int hashCode() {
        return this.position.hashCode();
    }

    public static <T> Comparator<NextTickListEntry<T>> func_223192_a() {
        return Comparator.comparingLong(p_226710_0_ -> p_226710_0_.field_235017_b_).thenComparing(p_226709_0_ -> p_226709_0_.priority).thenComparingLong(p_226708_0_ -> p_226708_0_.tickEntryID);
    }

    public String toString() {
        return String.valueOf(this.target) + ": " + String.valueOf(this.position) + ", " + this.field_235017_b_ + ", " + String.valueOf((Object)this.priority) + ", " + this.tickEntryID;
    }

    public T getTarget() {
        return this.target;
    }
}
