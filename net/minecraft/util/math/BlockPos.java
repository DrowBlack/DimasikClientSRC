package net.minecraft.util.math;

import com.google.common.collect.AbstractIterator;
import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.concurrent.Immutable;
import net.minecraft.dispenser.IPosition;
import net.minecraft.util.AxisRotation;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Immutable
public class BlockPos
extends Vector3i {
    public static final Codec<BlockPos> CODEC = Codec.INT_STREAM.comapFlatMap(stream -> Util.validateIntStreamSize(stream, 3).map(coordinates -> new BlockPos(coordinates[0], coordinates[1], coordinates[2])), pos -> IntStream.of(pos.getX(), pos.getY(), pos.getZ())).stable();
    private static final Logger LOGGER = LogManager.getLogger();
    public static final BlockPos ZERO = new BlockPos(0, 0, 0);
    private static final int NUM_X_BITS;
    private static final int NUM_Z_BITS;
    private static final int NUM_Y_BITS;
    private static final long X_MASK;
    private static final long Y_MASK;
    private static final long Z_MASK;
    private static final int INVERSE_START_BITS_Z;
    private static final int INVERSE_START_BITS_X;

    public BlockPos(int x, int y, int z) {
        super(x, y, z);
    }

    public BlockPos(double x, double y, double z) {
        super(x, y, z);
    }

    public BlockPos(Vector3d vec) {
        this(vec.x, vec.y, vec.z);
    }

    public BlockPos(IPosition position) {
        this(position.getX(), position.getY(), position.getZ());
    }

    public BlockPos(Vector3i source) {
        this(source.getX(), source.getY(), source.getZ());
    }

    public static long offset(long pos, Direction direction) {
        return BlockPos.offset(pos, direction.getXOffset(), direction.getYOffset(), direction.getZOffset());
    }

    public static long offset(long pos, int dx, int dy, int dz) {
        return BlockPos.pack(BlockPos.unpackX(pos) + dx, BlockPos.unpackY(pos) + dy, BlockPos.unpackZ(pos) + dz);
    }

    public static int unpackX(long packedPos) {
        return (int)(packedPos << 64 - INVERSE_START_BITS_X - NUM_X_BITS >> 64 - NUM_X_BITS);
    }

    public static int unpackY(long packedPos) {
        return (int)(packedPos << 64 - NUM_Y_BITS >> 64 - NUM_Y_BITS);
    }

    public static int unpackZ(long packedPos) {
        return (int)(packedPos << 64 - INVERSE_START_BITS_Z - NUM_Z_BITS >> 64 - NUM_Z_BITS);
    }

    public static BlockPos fromLong(long packedPos) {
        return new BlockPos(BlockPos.unpackX(packedPos), BlockPos.unpackY(packedPos), BlockPos.unpackZ(packedPos));
    }

    public long toLong() {
        return BlockPos.pack(this.getX(), this.getY(), this.getZ());
    }

    public static long pack(int x, int y, int z) {
        long i = 0L;
        i |= ((long)x & X_MASK) << INVERSE_START_BITS_X;
        return (i |= ((long)y & Y_MASK) << 0) | ((long)z & Z_MASK) << INVERSE_START_BITS_Z;
    }

    public static long atSectionBottomY(long packedPos) {
        return packedPos & 0xFFFFFFFFFFFFFFF0L;
    }

    public BlockPos add(double x, double y, double z) {
        return x == 0.0 && y == 0.0 && z == 0.0 ? this : new BlockPos((double)this.getX() + x, (double)this.getY() + y, (double)this.getZ() + z);
    }

    public BlockPos add(int x, int y, int z) {
        return x == 0 && y == 0 && z == 0 ? this : new BlockPos(this.getX() + x, this.getY() + y, this.getZ() + z);
    }

    public BlockPos add(Vector3i vec) {
        return this.add(vec.getX(), vec.getY(), vec.getZ());
    }

    public BlockPos subtract(Vector3i vec) {
        return this.add(-vec.getX(), -vec.getY(), -vec.getZ());
    }

    @Override
    public BlockPos up() {
        return this.offset(Direction.UP);
    }

    @Override
    public BlockPos up(int n) {
        return this.offset(Direction.UP, n);
    }

    @Override
    public BlockPos down() {
        return this.offset(Direction.DOWN);
    }

    @Override
    public BlockPos down(int n) {
        return this.offset(Direction.DOWN, n);
    }

    public BlockPos north() {
        return this.offset(Direction.NORTH);
    }

    public BlockPos north(int n) {
        return this.offset(Direction.NORTH, n);
    }

    public BlockPos south() {
        return this.offset(Direction.SOUTH);
    }

    public BlockPos south(int n) {
        return this.offset(Direction.SOUTH, n);
    }

    public BlockPos west() {
        return this.offset(Direction.WEST);
    }

    public BlockPos west(int n) {
        return this.offset(Direction.WEST, n);
    }

    public BlockPos east() {
        return this.offset(Direction.EAST);
    }

    public BlockPos east(int n) {
        return this.offset(Direction.EAST, n);
    }

    public BlockPos offset(Direction facing) {
        return new BlockPos(this.getX() + facing.getXOffset(), this.getY() + facing.getYOffset(), this.getZ() + facing.getZOffset());
    }

    @Override
    public BlockPos offset(Direction facing, int n) {
        return n == 0 ? this : new BlockPos(this.getX() + facing.getXOffset() * n, this.getY() + facing.getYOffset() * n, this.getZ() + facing.getZOffset() * n);
    }

    public BlockPos func_241872_a(Direction.Axis p_241872_1_, int p_241872_2_) {
        if (p_241872_2_ == 0) {
            return this;
        }
        int i = p_241872_1_ == Direction.Axis.X ? p_241872_2_ : 0;
        int j = p_241872_1_ == Direction.Axis.Y ? p_241872_2_ : 0;
        int k = p_241872_1_ == Direction.Axis.Z ? p_241872_2_ : 0;
        return new BlockPos(this.getX() + i, this.getY() + j, this.getZ() + k);
    }

    public BlockPos rotate(Rotation rotationIn) {
        switch (rotationIn) {
            default: {
                return this;
            }
            case CLOCKWISE_90: {
                return new BlockPos(-this.getZ(), this.getY(), this.getX());
            }
            case CLOCKWISE_180: {
                return new BlockPos(-this.getX(), this.getY(), -this.getZ());
            }
            case COUNTERCLOCKWISE_90: 
        }
        return new BlockPos(this.getZ(), this.getY(), -this.getX());
    }

    @Override
    public BlockPos crossProduct(Vector3i vec) {
        return new BlockPos(this.getY() * vec.getZ() - this.getZ() * vec.getY(), this.getZ() * vec.getX() - this.getX() * vec.getZ(), this.getX() * vec.getY() - this.getY() * vec.getX());
    }

    public BlockPos toImmutable() {
        return this;
    }

    public Mutable toMutable() {
        return new Mutable(this.getX(), this.getY(), this.getZ());
    }

    public static Iterable<BlockPos> getRandomPositions(final Random rand, final int amount, final int minX, final int minY, final int minZ, int maxX, int maxY, int maxZ) {
        final int i = maxX - minX + 1;
        final int j = maxY - minY + 1;
        final int k = maxZ - minZ + 1;
        return () -> new AbstractIterator<BlockPos>(){
            final Mutable pos = new Mutable();
            int remainingAmount = amount;

            @Override
            protected BlockPos computeNext() {
                if (this.remainingAmount <= 0) {
                    return (BlockPos)this.endOfData();
                }
                Mutable blockpos = this.pos.setPos(minX + rand.nextInt(i), minY + rand.nextInt(j), minZ + rand.nextInt(k));
                --this.remainingAmount;
                return blockpos;
            }
        };
    }

    public static Iterable<BlockPos> getProximitySortedBoxPositionsIterator(BlockPos pos, final int xWidth, final int yHeight, final int zWidth) {
        final int i = xWidth + yHeight + zWidth;
        final int j = pos.getX();
        final int k = pos.getY();
        final int l = pos.getZ();
        return () -> new AbstractIterator<BlockPos>(){
            private final Mutable coordinateIterator = new Mutable();
            private int field_239604_i_;
            private int field_239605_j_;
            private int field_239606_k_;
            private int field_239607_l_;
            private int field_239608_m_;
            private boolean field_239609_n_;

            @Override
            protected BlockPos computeNext() {
                if (this.field_239609_n_) {
                    this.field_239609_n_ = false;
                    this.coordinateIterator.setZ(l - (this.coordinateIterator.getZ() - l));
                    return this.coordinateIterator;
                }
                Mutable blockpos = null;
                while (blockpos == null) {
                    if (this.field_239608_m_ > this.field_239606_k_) {
                        ++this.field_239607_l_;
                        if (this.field_239607_l_ > this.field_239605_j_) {
                            ++this.field_239604_i_;
                            if (this.field_239604_i_ > i) {
                                return (BlockPos)this.endOfData();
                            }
                            this.field_239605_j_ = Math.min(xWidth, this.field_239604_i_);
                            this.field_239607_l_ = -this.field_239605_j_;
                        }
                        this.field_239606_k_ = Math.min(yHeight, this.field_239604_i_ - Math.abs(this.field_239607_l_));
                        this.field_239608_m_ = -this.field_239606_k_;
                    }
                    int i1 = this.field_239607_l_;
                    int j1 = this.field_239608_m_;
                    int k1 = this.field_239604_i_ - Math.abs(i1) - Math.abs(j1);
                    if (k1 <= zWidth) {
                        this.field_239609_n_ = k1 != 0;
                        blockpos = this.coordinateIterator.setPos(j + i1, k + j1, l + k1);
                    }
                    ++this.field_239608_m_;
                }
                return blockpos;
            }
        };
    }

    public static Optional<BlockPos> getClosestMatchingPosition(BlockPos pos, int width, int height, Predicate<BlockPos> posFilter) {
        return BlockPos.getProximitySortedBoxPositions(pos, width, height, width).filter(posFilter).findFirst();
    }

    public static Stream<BlockPos> getProximitySortedBoxPositions(BlockPos pos, int xWidth, int yHeight, int zWidth) {
        return StreamSupport.stream(BlockPos.getProximitySortedBoxPositionsIterator(pos, xWidth, yHeight, zWidth).spliterator(), false);
    }

    public static Iterable<BlockPos> getAllInBoxMutable(BlockPos firstPos, BlockPos secondPos) {
        return BlockPos.getAllInBoxMutable(Math.min(firstPos.getX(), secondPos.getX()), Math.min(firstPos.getY(), secondPos.getY()), Math.min(firstPos.getZ(), secondPos.getZ()), Math.max(firstPos.getX(), secondPos.getX()), Math.max(firstPos.getY(), secondPos.getY()), Math.max(firstPos.getZ(), secondPos.getZ()));
    }

    public static Stream<BlockPos> getAllInBox(BlockPos firstPos, BlockPos secondPos) {
        return StreamSupport.stream(BlockPos.getAllInBoxMutable(firstPos, secondPos).spliterator(), false);
    }

    public static Stream<BlockPos> getAllInBox(MutableBoundingBox box) {
        return BlockPos.getAllInBox(Math.min(box.minX, box.maxX), Math.min(box.minY, box.maxY), Math.min(box.minZ, box.maxZ), Math.max(box.minX, box.maxX), Math.max(box.minY, box.maxY), Math.max(box.minZ, box.maxZ));
    }

    public static Stream<BlockPos> getAllInBox(AxisAlignedBB aabb) {
        return BlockPos.getAllInBox(MathHelper.floor(aabb.minX), MathHelper.floor(aabb.minY), MathHelper.floor(aabb.minZ), MathHelper.floor(aabb.maxX), MathHelper.floor(aabb.maxY), MathHelper.floor(aabb.maxZ));
    }

    public static Stream<BlockPos> getAllInBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        return StreamSupport.stream(BlockPos.getAllInBoxMutable(minX, minY, minZ, maxX, maxY, maxZ).spliterator(), false);
    }

    public static Iterable<BlockPos> getAllInBoxMutable(final int x1, final int y1, final int z1, int x2, int y2, int z2) {
        final int i = x2 - x1 + 1;
        final int j = y2 - y1 + 1;
        int k = z2 - z1 + 1;
        final int l = i * j * k;
        return () -> new AbstractIterator<BlockPos>(){
            private final Mutable mutablePos = new Mutable();
            private int totalAmount;

            @Override
            protected BlockPos computeNext() {
                if (this.totalAmount == l) {
                    return (BlockPos)this.endOfData();
                }
                int i1 = this.totalAmount % i;
                int j1 = this.totalAmount / i;
                int k1 = j1 % j;
                int l1 = j1 / j;
                ++this.totalAmount;
                return this.mutablePos.setPos(x1 + i1, y1 + k1, z1 + l1);
            }
        };
    }

    public static Iterable<Mutable> func_243514_a(final BlockPos p_243514_0_, final int p_243514_1_, final Direction p_243514_2_, final Direction p_243514_3_) {
        Validate.validState(p_243514_2_.getAxis() != p_243514_3_.getAxis(), "The two directions cannot be on the same axis", new Object[0]);
        return () -> new AbstractIterator<Mutable>(){
            private final Direction[] field_243520_e;
            private final Mutable field_243521_f;
            private final int field_243522_g;
            private int field_243523_h;
            private int field_243524_i;
            private int field_243525_j;
            private int field_243526_k;
            private int field_243527_l;
            private int field_243528_m;
            {
                this.field_243520_e = new Direction[]{p_243514_2_, p_243514_3_, p_243514_2_.getOpposite(), p_243514_3_.getOpposite()};
                this.field_243521_f = p_243514_0_.toMutable().move(p_243514_3_);
                this.field_243522_g = 4 * p_243514_1_;
                this.field_243523_h = -1;
                this.field_243526_k = this.field_243521_f.getX();
                this.field_243527_l = this.field_243521_f.getY();
                this.field_243528_m = this.field_243521_f.getZ();
            }

            @Override
            protected Mutable computeNext() {
                this.field_243521_f.setPos(this.field_243526_k, this.field_243527_l, this.field_243528_m).move(this.field_243520_e[(this.field_243523_h + 4) % 4]);
                this.field_243526_k = this.field_243521_f.getX();
                this.field_243527_l = this.field_243521_f.getY();
                this.field_243528_m = this.field_243521_f.getZ();
                if (this.field_243525_j >= this.field_243524_i) {
                    if (this.field_243523_h >= this.field_243522_g) {
                        return (Mutable)this.endOfData();
                    }
                    ++this.field_243523_h;
                    this.field_243525_j = 0;
                    this.field_243524_i = this.field_243523_h / 2 + 1;
                }
                ++this.field_243525_j;
                return this.field_243521_f;
            }
        };
    }

    static {
        NUM_Z_BITS = NUM_X_BITS = 1 + MathHelper.log2(MathHelper.smallestEncompassingPowerOfTwo(30000000));
        NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS;
        X_MASK = (1L << NUM_X_BITS) - 1L;
        Y_MASK = (1L << NUM_Y_BITS) - 1L;
        Z_MASK = (1L << NUM_Z_BITS) - 1L;
        INVERSE_START_BITS_Z = NUM_Y_BITS;
        INVERSE_START_BITS_X = NUM_Y_BITS + NUM_Z_BITS;
    }

    public static class Mutable
    extends BlockPos {
        public Mutable() {
            this(0, 0, 0);
        }

        public Mutable(int x_, int y_, int z_) {
            super(x_, y_, z_);
        }

        public Mutable(double x, double y, double z) {
            this(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
        }

        @Override
        public BlockPos add(double x, double y, double z) {
            return super.add(x, y, z).toImmutable();
        }

        @Override
        public BlockPos add(int x, int y, int z) {
            return super.add(x, y, z).toImmutable();
        }

        @Override
        public BlockPos offset(Direction facing, int n) {
            return super.offset(facing, n).toImmutable();
        }

        @Override
        public BlockPos func_241872_a(Direction.Axis p_241872_1_, int p_241872_2_) {
            return super.func_241872_a(p_241872_1_, p_241872_2_).toImmutable();
        }

        @Override
        public BlockPos rotate(Rotation rotationIn) {
            return super.rotate(rotationIn).toImmutable();
        }

        public Mutable setPos(int xIn, int yIn, int zIn) {
            this.setX(xIn);
            this.setY(yIn);
            this.setZ(zIn);
            return this;
        }

        public Mutable setPos(double xIn, double yIn, double zIn) {
            return this.setPos(MathHelper.floor(xIn), MathHelper.floor(yIn), MathHelper.floor(zIn));
        }

        public Mutable setPos(Vector3i vec) {
            return this.setPos(vec.getX(), vec.getY(), vec.getZ());
        }

        public Mutable setPos(long packedPos) {
            return this.setPos(Mutable.unpackX(packedPos), Mutable.unpackY(packedPos), Mutable.unpackZ(packedPos));
        }

        public Mutable setPos(AxisRotation rotation, int x, int y, int z) {
            return this.setPos(rotation.getCoordinate(x, y, z, Direction.Axis.X), rotation.getCoordinate(x, y, z, Direction.Axis.Y), rotation.getCoordinate(x, y, z, Direction.Axis.Z));
        }

        public Mutable setAndMove(Vector3i pos, Direction direction) {
            return this.setPos(pos.getX() + direction.getXOffset(), pos.getY() + direction.getYOffset(), pos.getZ() + direction.getZOffset());
        }

        public Mutable setAndOffset(Vector3i pos, int offsetX, int offsetY, int offsetZ) {
            return this.setPos(pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ);
        }

        public Mutable move(Direction facing) {
            return this.move(facing, 1);
        }

        public Mutable move(Direction facing, int n) {
            return this.setPos(this.getX() + facing.getXOffset() * n, this.getY() + facing.getYOffset() * n, this.getZ() + facing.getZOffset() * n);
        }

        public Mutable move(int xIn, int yIn, int zIn) {
            return this.setPos(this.getX() + xIn, this.getY() + yIn, this.getZ() + zIn);
        }

        public Mutable func_243531_h(Vector3i p_243531_1_) {
            return this.setPos(this.getX() + p_243531_1_.getX(), this.getY() + p_243531_1_.getY(), this.getZ() + p_243531_1_.getZ());
        }

        public Mutable clampAxisCoordinate(Direction.Axis axis, int min, int max) {
            switch (axis) {
                case X: {
                    return this.setPos(MathHelper.clamp(this.getX(), min, max), this.getY(), this.getZ());
                }
                case Y: {
                    return this.setPos(this.getX(), MathHelper.clamp(this.getY(), min, max), this.getZ());
                }
                case Z: {
                    return this.setPos(this.getX(), this.getY(), MathHelper.clamp(this.getZ(), min, max));
                }
            }
            throw new IllegalStateException("Unable to clamp axis " + String.valueOf(axis));
        }

        @Override
        public void setX(int xIn) {
            super.setX(xIn);
        }

        @Override
        public void setY(int yIn) {
            super.setY(yIn);
        }

        @Override
        public void setZ(int zIn) {
            super.setZ(zIn);
        }

        @Override
        public BlockPos toImmutable() {
            return new BlockPos(this);
        }
    }
}
