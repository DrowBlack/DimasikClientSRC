package net.minecraft.client.renderer.chunk;

import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.client.renderer.chunk.SetVisibility;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;

public class VisGraph {
    private static final int DX = (int)Math.pow(16.0, 0.0);
    private static final int DZ = (int)Math.pow(16.0, 1.0);
    private static final int DY = (int)Math.pow(16.0, 2.0);
    private static final Direction[] DIRECTIONS = Direction.values();
    private final BitSet bitSet = new BitSet(4096);
    private static final int[] INDEX_OF_EDGES = Util.make(new int[1352], p_lambda$static$0_0_ -> {
        boolean i = false;
        int j = 15;
        int k = 0;
        for (int l = 0; l < 16; ++l) {
            for (int i1 = 0; i1 < 16; ++i1) {
                for (int j1 = 0; j1 < 16; ++j1) {
                    if (l != 0 && l != 15 && i1 != 0 && i1 != 15 && j1 != 0 && j1 != 15) continue;
                    p_lambda$static$0_0_[k++] = VisGraph.getIndex(l, i1, j1);
                }
            }
        }
    });
    private int empty = 4096;

    public void setOpaqueCube(BlockPos pos) {
        this.bitSet.set(VisGraph.getIndex(pos), true);
        --this.empty;
    }

    private static int getIndex(BlockPos pos) {
        return VisGraph.getIndex(pos.getX() & 0xF, pos.getY() & 0xF, pos.getZ() & 0xF);
    }

    private static int getIndex(int x, int y, int z) {
        return x << 0 | y << 8 | z << 4;
    }

    public SetVisibility computeVisibility() {
        SetVisibility setvisibility = new SetVisibility();
        if (4096 - this.empty < 256) {
            setvisibility.setAllVisible(true);
        } else if (this.empty == 0) {
            setvisibility.setAllVisible(false);
        } else {
            for (int i : INDEX_OF_EDGES) {
                if (this.bitSet.get(i)) continue;
                setvisibility.setManyVisible(this.floodFill(i));
            }
        }
        return setvisibility;
    }

    private Set<Direction> floodFill(int pos) {
        EnumSet<Direction> set = EnumSet.noneOf(Direction.class);
        IntArrayFIFOQueue intpriorityqueue = new IntArrayFIFOQueue(384);
        intpriorityqueue.enqueue(pos);
        this.bitSet.set(pos, true);
        while (!intpriorityqueue.isEmpty()) {
            int i = intpriorityqueue.dequeueInt();
            this.addEdges(i, set);
            for (Direction direction : DIRECTIONS) {
                int j = this.getNeighborIndexAtFace(i, direction);
                if (j < 0 || this.bitSet.get(j)) continue;
                this.bitSet.set(j, true);
                intpriorityqueue.enqueue(j);
            }
        }
        return set;
    }

    private void addEdges(int pos, Set<Direction> setFacings) {
        int i = pos >> 0 & 0xF;
        if (i == 0) {
            setFacings.add(Direction.WEST);
        } else if (i == 15) {
            setFacings.add(Direction.EAST);
        }
        int j = pos >> 8 & 0xF;
        if (j == 0) {
            setFacings.add(Direction.DOWN);
        } else if (j == 15) {
            setFacings.add(Direction.UP);
        }
        int k = pos >> 4 & 0xF;
        if (k == 0) {
            setFacings.add(Direction.NORTH);
        } else if (k == 15) {
            setFacings.add(Direction.SOUTH);
        }
    }

    private int getNeighborIndexAtFace(int pos, Direction facing) {
        switch (facing) {
            case DOWN: {
                if ((pos >> 8 & 0xF) == 0) {
                    return -1;
                }
                return pos - DY;
            }
            case UP: {
                if ((pos >> 8 & 0xF) == 15) {
                    return -1;
                }
                return pos + DY;
            }
            case NORTH: {
                if ((pos >> 4 & 0xF) == 0) {
                    return -1;
                }
                return pos - DZ;
            }
            case SOUTH: {
                if ((pos >> 4 & 0xF) == 15) {
                    return -1;
                }
                return pos + DZ;
            }
            case WEST: {
                if ((pos >> 0 & 0xF) == 0) {
                    return -1;
                }
                return pos - DX;
            }
            case EAST: {
                if ((pos >> 0 & 0xF) == 15) {
                    return -1;
                }
                return pos + DX;
            }
        }
        return -1;
    }
}
