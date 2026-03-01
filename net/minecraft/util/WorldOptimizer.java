package net.minecraft.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenCustomHashMap;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.crash.ReportedException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.ChunkLoader;
import net.minecraft.world.chunk.storage.RegionFile;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.SaveFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldOptimizer {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder().setDaemon(true).build();
    private final ImmutableSet<RegistryKey<World>> field_233529_c_;
    private final boolean field_219957_d;
    private final SaveFormat.LevelSave worldStorage;
    private final Thread thread;
    private final DataFixer field_233530_g_;
    private volatile boolean active = true;
    private volatile boolean done;
    private volatile float totalProgress;
    private volatile int totalChunks;
    private volatile int converted;
    private volatile int skipped;
    private final Object2FloatMap<RegistryKey<World>> progress = Object2FloatMaps.synchronize(new Object2FloatOpenCustomHashMap(Util.identityHashStrategy()));
    private volatile ITextComponent statusText = new TranslationTextComponent("optimizeWorld.stage.counting");
    private static final Pattern REGION_FILE_PATTERN = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");
    private final DimensionSavedDataManager savedDataManager;

    public WorldOptimizer(SaveFormat.LevelSave p_i231486_1_, DataFixer p_i231486_2_, ImmutableSet<RegistryKey<World>> p_i231486_3_, boolean p_i231486_4_) {
        this.field_233529_c_ = p_i231486_3_;
        this.field_219957_d = p_i231486_4_;
        this.field_233530_g_ = p_i231486_2_;
        this.worldStorage = p_i231486_1_;
        this.savedDataManager = new DimensionSavedDataManager(new File(this.worldStorage.getDimensionFolder(World.OVERWORLD), "data"), p_i231486_2_);
        this.thread = THREAD_FACTORY.newThread(this::optimize);
        this.thread.setUncaughtExceptionHandler((p_219956_1_, p_219956_2_) -> {
            LOGGER.error("Error upgrading world", p_219956_2_);
            this.statusText = new TranslationTextComponent("optimizeWorld.stage.failed");
            this.done = true;
        });
        this.thread.start();
    }

    public void cancel() {
        this.active = false;
        try {
            this.thread.join();
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
    }

    private void optimize() {
        this.totalChunks = 0;
        ImmutableMap.Builder<RegistryKey, ListIterator<ChunkPos>> builder = ImmutableMap.builder();
        for (RegistryKey registryKey : this.field_233529_c_) {
            List<ChunkPos> list = this.func_233532_b_(registryKey);
            builder.put(registryKey, list.listIterator());
            this.totalChunks += list.size();
        }
        if (this.totalChunks == 0) {
            this.done = true;
        } else {
            float f1 = this.totalChunks;
            ImmutableMap immutableMap = builder.build();
            ImmutableMap.Builder<RegistryKey, ChunkLoader> builder1 = ImmutableMap.builder();
            for (RegistryKey registryKey : this.field_233529_c_) {
                File file1 = this.worldStorage.getDimensionFolder(registryKey);
                builder1.put(registryKey, new ChunkLoader(new File(file1, "region"), this.field_233530_g_, true));
            }
            ImmutableMap immutablemap1 = builder1.build();
            long l = Util.milliTime();
            this.statusText = new TranslationTextComponent("optimizeWorld.stage.upgrading");
            while (this.active) {
                boolean flag = false;
                float f = 0.0f;
                for (RegistryKey registryKey : this.field_233529_c_) {
                    ListIterator listiterator = (ListIterator)immutableMap.get(registryKey);
                    ChunkLoader chunkloader = (ChunkLoader)immutablemap1.get(registryKey);
                    if (listiterator.hasNext()) {
                        ChunkPos chunkpos = (ChunkPos)listiterator.next();
                        boolean flag1 = false;
                        try {
                            CompoundNBT compoundnbt = chunkloader.readChunk(chunkpos);
                            if (compoundnbt != null) {
                                boolean flag2;
                                int j = ChunkLoader.getDataVersion(compoundnbt);
                                CompoundNBT compoundnbt1 = chunkloader.func_235968_a_(registryKey, () -> this.savedDataManager, compoundnbt);
                                CompoundNBT compoundnbt2 = compoundnbt1.getCompound("Level");
                                ChunkPos chunkpos1 = new ChunkPos(compoundnbt2.getInt("xPos"), compoundnbt2.getInt("zPos"));
                                if (!chunkpos1.equals(chunkpos)) {
                                    LOGGER.warn("Chunk {} has invalid position {}", (Object)chunkpos, (Object)chunkpos1);
                                }
                                boolean bl = flag2 = j < SharedConstants.getVersion().getWorldVersion();
                                if (this.field_219957_d) {
                                    flag2 = flag2 || compoundnbt2.contains("Heightmaps");
                                    compoundnbt2.remove("Heightmaps");
                                    flag2 = flag2 || compoundnbt2.contains("isLightOn");
                                    compoundnbt2.remove("isLightOn");
                                }
                                if (flag2) {
                                    chunkloader.writeChunk(chunkpos, compoundnbt1);
                                    flag1 = true;
                                }
                            }
                        }
                        catch (ReportedException reportedexception) {
                            Throwable throwable = reportedexception.getCause();
                            if (!(throwable instanceof IOException)) {
                                throw reportedexception;
                            }
                            LOGGER.error("Error upgrading chunk {}", (Object)chunkpos, (Object)throwable);
                        }
                        catch (IOException ioexception1) {
                            LOGGER.error("Error upgrading chunk {}", (Object)chunkpos, (Object)ioexception1);
                        }
                        if (flag1) {
                            ++this.converted;
                        } else {
                            ++this.skipped;
                        }
                        flag = true;
                    }
                    float f2 = (float)listiterator.nextIndex() / f1;
                    this.progress.put((RegistryKey<World>)registryKey, f2);
                    f += f2;
                }
                this.totalProgress = f;
                if (flag) continue;
                this.active = false;
            }
            this.statusText = new TranslationTextComponent("optimizeWorld.stage.finished");
            for (ChunkLoader chunkloader1 : immutablemap1.values()) {
                try {
                    chunkloader1.close();
                }
                catch (IOException ioexception) {
                    LOGGER.error("Error upgrading chunk", (Throwable)ioexception);
                }
            }
            this.savedDataManager.save();
            l = Util.milliTime() - l;
            LOGGER.info("World optimizaton finished after {} ms", (Object)l);
            this.done = true;
        }
    }

    private List<ChunkPos> func_233532_b_(RegistryKey<World> p_233532_1_) {
        File file1 = this.worldStorage.getDimensionFolder(p_233532_1_);
        File file2 = new File(file1, "region");
        File[] afile = file2.listFiles((p_219954_0_, p_219954_1_) -> p_219954_1_.endsWith(".mca"));
        if (afile == null) {
            return ImmutableList.of();
        }
        ArrayList<ChunkPos> list = Lists.newArrayList();
        for (File file3 : afile) {
            Matcher matcher = REGION_FILE_PATTERN.matcher(file3.getName());
            if (!matcher.matches()) continue;
            int i = Integer.parseInt(matcher.group(1)) << 5;
            int j = Integer.parseInt(matcher.group(2)) << 5;
            try (RegionFile regionfile = new RegionFile(file3, file2, true);){
                for (int k = 0; k < 32; ++k) {
                    for (int l = 0; l < 32; ++l) {
                        ChunkPos chunkpos = new ChunkPos(k + i, l + j);
                        if (!regionfile.func_222662_b(chunkpos)) continue;
                        list.add(chunkpos);
                    }
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return list;
    }

    public boolean isFinished() {
        return this.done;
    }

    public ImmutableSet<RegistryKey<World>> func_233533_c_() {
        return this.field_233529_c_;
    }

    public float func_233531_a_(RegistryKey<World> p_233531_1_) {
        return this.progress.getFloat(p_233531_1_);
    }

    public float getTotalProgress() {
        return this.totalProgress;
    }

    public int getTotalChunks() {
        return this.totalChunks;
    }

    public int getConverted() {
        return this.converted;
    }

    public int getSkipped() {
        return this.skipped;
    }

    public ITextComponent getStatusText() {
        return this.statusText;
    }
}
