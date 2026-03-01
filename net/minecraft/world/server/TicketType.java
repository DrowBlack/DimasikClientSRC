package net.minecraft.world.server;

import java.util.Comparator;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.vector.Vector3i;

public class TicketType<T> {
    private final String name;
    private final Comparator<T> typeComparator;
    private final long lifespan;
    public static final TicketType<Unit> START = TicketType.create("start", (p_219486_0_, p_219486_1_) -> 0);
    public static final TicketType<Unit> DRAGON = TicketType.create("dragon", (p_219485_0_, p_219485_1_) -> 0);
    public static final TicketType<ChunkPos> PLAYER = TicketType.create("player", Comparator.comparingLong(ChunkPos::asLong));
    public static final TicketType<ChunkPos> FORCED = TicketType.create("forced", Comparator.comparingLong(ChunkPos::asLong));
    public static final TicketType<ChunkPos> LIGHT = TicketType.create("light", Comparator.comparingLong(ChunkPos::asLong));
    public static final TicketType<BlockPos> PORTAL = TicketType.create("portal", Vector3i::compareTo, 300);
    public static final TicketType<Integer> POST_TELEPORT = TicketType.create("post_teleport", Integer::compareTo, 5);
    public static final TicketType<ChunkPos> UNKNOWN = TicketType.create("unknown", Comparator.comparingLong(ChunkPos::asLong), 1);

    public static <T> TicketType<T> create(String nameIn, Comparator<T> comparator) {
        return new TicketType<T>(nameIn, comparator, 0L);
    }

    public static <T> TicketType<T> create(String nameIn, Comparator<T> comparator, int lifespanIn) {
        return new TicketType<T>(nameIn, comparator, lifespanIn);
    }

    protected TicketType(String nameIn, Comparator<T> comparator, long lifespanIn) {
        this.name = nameIn;
        this.typeComparator = comparator;
        this.lifespan = lifespanIn;
    }

    public String toString() {
        return this.name;
    }

    public Comparator<T> getComparator() {
        return this.typeComparator;
    }

    public long getLifespan() {
        return this.lifespan;
    }
}
