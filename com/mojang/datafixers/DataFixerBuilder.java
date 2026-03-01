package com.mojang.datafixers;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerUpper;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataFixerBuilder {
    private static final Logger LOGGER = LogManager.getLogger();
    private final int dataVersion;
    private final Int2ObjectSortedMap<Schema> schemas = new Int2ObjectAVLTreeMap<Schema>();
    private final List<DataFix> globalList = Lists.newArrayList();
    private final IntSortedSet fixerVersions = new IntAVLTreeSet();

    public DataFixerBuilder(int dataVersion) {
        this.dataVersion = dataVersion;
    }

    public Schema addSchema(int version, BiFunction<Integer, Schema, Schema> factory) {
        return this.addSchema(version, 0, factory);
    }

    public Schema addSchema(int version, int subVersion, BiFunction<Integer, Schema, Schema> factory) {
        int key = DataFixUtils.makeKey(version, subVersion);
        Schema parent = this.schemas.isEmpty() ? null : (Schema)this.schemas.get(DataFixerUpper.getLowestSchemaSameVersion(this.schemas, key - 1));
        Schema schema = factory.apply(DataFixUtils.makeKey(version, subVersion), parent);
        this.addSchema(schema);
        return schema;
    }

    public void addSchema(Schema schema) {
        this.schemas.put(schema.getVersionKey(), schema);
    }

    public void addFixer(DataFix fix) {
        int version = DataFixUtils.getVersion(fix.getVersionKey());
        if (version > this.dataVersion) {
            LOGGER.warn("Ignored fix registered for version: {} as the DataVersion of the game is: {}", (Object)version, (Object)this.dataVersion);
            return;
        }
        this.globalList.add(fix);
        this.fixerVersions.add(fix.getVersionKey());
    }

    public DataFixer build(Executor executor) {
        DataFixerUpper fixerUpper = new DataFixerUpper(new Int2ObjectAVLTreeMap<Schema>(this.schemas), new ArrayList<DataFix>(this.globalList), new IntAVLTreeSet(this.fixerVersions));
        IntBidirectionalIterator iterator = fixerUpper.fixerVersions().iterator();
        while (iterator.hasNext()) {
            int versionKey = iterator.nextInt();
            Schema schema = (Schema)this.schemas.get(versionKey);
            for (String typeName : schema.types()) {
                CompletableFuture.runAsync(() -> {
                    Type<?> dataType = schema.getType(() -> typeName);
                    TypeRewriteRule rule = fixerUpper.getRule(DataFixUtils.getVersion(versionKey), this.dataVersion);
                    dataType.rewrite(rule, DataFixerUpper.OPTIMIZATION_RULE);
                }, executor).exceptionally(e -> {
                    LOGGER.error("Unable to build datafixers", (Throwable)e);
                    Runtime.getRuntime().exit(1);
                    return null;
                });
            }
        }
        return fixerUpper;
    }
}
