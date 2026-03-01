package net.minecraft.world.lighting;

import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import java.util.function.LongPredicate;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.lighting.BlockLightEngine;
import net.minecraft.world.lighting.BlockLightStorage;
import net.minecraft.world.lighting.SkyLightEngine;
import net.minecraft.world.lighting.SkyLightStorage;

public abstract class LevelBasedGraph {
    private final int levelCount;
    private final LongLinkedOpenHashSet[] updatesByLevel;
    private final Long2ByteMap propagationLevels;
    private int minLevelToUpdate;
    private volatile boolean needsUpdate;

    protected LevelBasedGraph(int levelCount, final int p_i51298_2_, final int p_i51298_3_) {
        if (levelCount >= 254) {
            throw new IllegalArgumentException("Level count must be < 254.");
        }
        this.levelCount = levelCount;
        this.updatesByLevel = new LongLinkedOpenHashSet[levelCount];
        int i = p_i51298_2_;
        int j = p_i51298_3_;
        if (this.getClass() != BlockLightEngine.class && this.getClass() != SkyLightEngine.class) {
            if (this.getClass() == BlockLightStorage.class || this.getClass() == SkyLightStorage.class) {
                i = Math.max(p_i51298_2_, 2048);
                j = Math.max(p_i51298_3_, 2048);
            }
        } else {
            i = Math.max(p_i51298_2_, 8192);
            j = Math.max(p_i51298_3_, 8192);
        }
        for (int k = 0; k < levelCount; ++k) {
            this.updatesByLevel[k] = new LongLinkedOpenHashSet(i, 0.5f){

                @Override
                protected void rehash(int p_rehash_1_) {
                    if (p_rehash_1_ > p_i51298_2_) {
                        super.rehash(p_rehash_1_);
                    }
                }
            };
        }
        this.propagationLevels = new Long2ByteOpenHashMap(j, 0.5f){

            @Override
            protected void rehash(int p_rehash_1_) {
                if (p_rehash_1_ > p_i51298_3_) {
                    super.rehash(p_rehash_1_);
                }
            }
        };
        this.propagationLevels.defaultReturnValue((byte)-1);
        this.minLevelToUpdate = levelCount;
    }

    private int minLevel(int level1, int level2) {
        int i = level1;
        if (level1 > level2) {
            i = level2;
        }
        if (i > this.levelCount - 1) {
            i = this.levelCount - 1;
        }
        return i;
    }

    private void updateMinLevel(int maxLevel) {
        int i = this.minLevelToUpdate;
        this.minLevelToUpdate = maxLevel;
        for (int j = i + 1; j < maxLevel; ++j) {
            if (this.updatesByLevel[j].isEmpty()) continue;
            this.minLevelToUpdate = j;
            break;
        }
    }

    protected void cancelUpdate(long positionIn) {
        int i = this.propagationLevels.get(positionIn) & 0xFF;
        if (i != 255) {
            int j = this.getLevel(positionIn);
            int k = this.minLevel(j, i);
            this.removeToUpdate(positionIn, k, this.levelCount, true);
            this.needsUpdate = this.minLevelToUpdate < this.levelCount;
        }
    }

    public void func_227465_a_(LongPredicate p_227465_1_) {
        LongArrayList longlist = new LongArrayList();
        this.propagationLevels.keySet().forEach(p_lambda$func_227465_a_$0_2_ -> {
            if (p_227465_1_.test((long)p_lambda$func_227465_a_$0_2_)) {
                longlist.add((Long)p_lambda$func_227465_a_$0_2_);
            }
        });
        longlist.forEach(this::cancelUpdate);
    }

    private void removeToUpdate(long pos, int level, int maxLevel, boolean removeAll) {
        if (removeAll) {
            this.propagationLevels.remove(pos);
        }
        this.updatesByLevel[level].remove(pos);
        if (this.updatesByLevel[level].isEmpty() && this.minLevelToUpdate == level) {
            this.updateMinLevel(maxLevel);
        }
    }

    private void addToUpdate(long pos, int levelToSet, int updateLevel) {
        this.propagationLevels.put(pos, (byte)levelToSet);
        this.updatesByLevel[updateLevel].add(pos);
        if (this.minLevelToUpdate > updateLevel) {
            this.minLevelToUpdate = updateLevel;
        }
    }

    protected void scheduleUpdate(long worldPos) {
        this.scheduleUpdate(worldPos, worldPos, this.levelCount - 1, false);
    }

    protected void scheduleUpdate(long fromPos, long toPos, int newLevel, boolean isDecreasing) {
        this.propagateLevel(fromPos, toPos, newLevel, this.getLevel(toPos), this.propagationLevels.get(toPos) & 0xFF, isDecreasing);
        this.needsUpdate = this.minLevelToUpdate < this.levelCount;
    }

    private void propagateLevel(long fromPos, long toPos, int newLevel, int previousLevel, int propagationLevel, boolean isDecreasing) {
        if (!this.isRoot(toPos)) {
            boolean flag;
            newLevel = MathHelper.clamp(newLevel, 0, this.levelCount - 1);
            previousLevel = MathHelper.clamp(previousLevel, 0, this.levelCount - 1);
            if (propagationLevel == 255) {
                flag = true;
                propagationLevel = previousLevel;
            } else {
                flag = false;
            }
            int i = isDecreasing ? Math.min(propagationLevel, newLevel) : MathHelper.clamp(this.computeLevel(toPos, fromPos, newLevel), 0, this.levelCount - 1);
            int j = this.minLevel(previousLevel, propagationLevel);
            if (previousLevel != i) {
                int k = this.minLevel(previousLevel, i);
                if (j != k && !flag) {
                    this.removeToUpdate(toPos, j, k, false);
                }
                this.addToUpdate(toPos, i, k);
            } else if (!flag) {
                this.removeToUpdate(toPos, j, this.levelCount, true);
            }
        }
    }

    protected final void propagateLevel(long fromPos, long toPos, int sourceLevel, boolean isDecreasing) {
        int i = this.propagationLevels.get(toPos) & 0xFF;
        int j = MathHelper.clamp(this.getEdgeLevel(fromPos, toPos, sourceLevel), 0, this.levelCount - 1);
        if (isDecreasing) {
            this.propagateLevel(fromPos, toPos, j, this.getLevel(toPos), i, true);
        } else {
            int k;
            boolean flag;
            if (i == 255) {
                flag = true;
                k = MathHelper.clamp(this.getLevel(toPos), 0, this.levelCount - 1);
            } else {
                k = i;
                flag = false;
            }
            if (j == k) {
                this.propagateLevel(fromPos, toPos, this.levelCount - 1, flag ? k : this.getLevel(toPos), i, false);
            }
        }
    }

    protected final boolean needsUpdate() {
        return this.needsUpdate;
    }

    protected final int processUpdates(int toUpdateCount) {
        if (this.minLevelToUpdate >= this.levelCount) {
            return toUpdateCount;
        }
        while (this.minLevelToUpdate < this.levelCount && toUpdateCount > 0) {
            int k;
            --toUpdateCount;
            LongLinkedOpenHashSet longlinkedopenhashset = this.updatesByLevel[this.minLevelToUpdate];
            long i = longlinkedopenhashset.removeFirstLong();
            int j = MathHelper.clamp(this.getLevel(i), 0, this.levelCount - 1);
            if (longlinkedopenhashset.isEmpty()) {
                this.updateMinLevel(this.levelCount);
            }
            if ((k = this.propagationLevels.remove(i) & 0xFF) < j) {
                this.setLevel(i, k);
                this.notifyNeighbors(i, k, true);
                continue;
            }
            if (k <= j) continue;
            this.addToUpdate(i, k, this.minLevel(this.levelCount - 1, k));
            this.setLevel(i, this.levelCount - 1);
            this.notifyNeighbors(i, j, false);
        }
        this.needsUpdate = this.minLevelToUpdate < this.levelCount;
        return toUpdateCount;
    }

    public int func_227467_c_() {
        return this.propagationLevels.size();
    }

    protected abstract boolean isRoot(long var1);

    protected abstract int computeLevel(long var1, long var3, int var5);

    protected abstract void notifyNeighbors(long var1, int var3, boolean var4);

    protected abstract int getLevel(long var1);

    protected abstract void setLevel(long var1, int var3);

    protected abstract int getEdgeLevel(long var1, long var3, int var5);

    protected int queuedUpdateSize() {
        return this.propagationLevels.size();
    }
}
