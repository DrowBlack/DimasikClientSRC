package com.mojang.datafixers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.functions.PointFreeRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataFixerUpper
implements DataFixer {
    public static boolean ERRORS_ARE_FATAL = false;
    private static final Logger LOGGER = LogManager.getLogger();
    protected static final PointFreeRule OPTIMIZATION_RULE = DataFixUtils.make(() -> {
        PointFreeRule opSimple = PointFreeRule.orElse(PointFreeRule.orElse(PointFreeRule.CataFuseSame.INSTANCE, PointFreeRule.orElse(PointFreeRule.CataFuseDifferent.INSTANCE, PointFreeRule.LensAppId.INSTANCE)), PointFreeRule.orElse(PointFreeRule.LensComp.INSTANCE, PointFreeRule.orElse(PointFreeRule.AppNest.INSTANCE, PointFreeRule.LensCompFunc.INSTANCE)));
        PointFreeRule opLeft = PointFreeRule.many(PointFreeRule.once(PointFreeRule.orElse(opSimple, PointFreeRule.CompAssocLeft.INSTANCE)));
        PointFreeRule opComp = PointFreeRule.many(PointFreeRule.once(PointFreeRule.orElse(PointFreeRule.SortInj.INSTANCE, PointFreeRule.SortProj.INSTANCE)));
        PointFreeRule opRight = PointFreeRule.many(PointFreeRule.once(PointFreeRule.orElse(opSimple, PointFreeRule.CompAssocRight.INSTANCE)));
        return PointFreeRule.seq(ImmutableList.of(() -> opLeft, () -> opComp, () -> opRight, () -> opLeft, () -> opRight));
    });
    private final Int2ObjectSortedMap<Schema> schemas;
    private final List<DataFix> globalList;
    private final IntSortedSet fixerVersions;
    private final Long2ObjectMap<TypeRewriteRule> rules = Long2ObjectMaps.synchronize(new Long2ObjectOpenHashMap());

    protected DataFixerUpper(Int2ObjectSortedMap<Schema> schemas, List<DataFix> globalList, IntSortedSet fixerVersions) {
        this.schemas = schemas;
        this.globalList = globalList;
        this.fixerVersions = fixerVersions;
    }

    @Override
    public <T> Dynamic<T> update(DSL.TypeReference type, Dynamic<T> input, int version, int newVersion) {
        if (version < newVersion) {
            Type<?> dataType = this.getType(type, version);
            DataResult read = dataType.readAndWrite(input.getOps(), this.getType(type, newVersion), this.getRule(version, newVersion), OPTIMIZATION_RULE, input.getValue());
            Object result = read.resultOrPartial(LOGGER::error).orElse(input.getValue());
            return new Dynamic(input.getOps(), result);
        }
        return input;
    }

    @Override
    public Schema getSchema(int key) {
        return (Schema)this.schemas.get(DataFixerUpper.getLowestSchemaSameVersion(this.schemas, key));
    }

    protected Type<?> getType(DSL.TypeReference type, int version) {
        return this.getSchema(DataFixUtils.makeKey(version)).getType(type);
    }

    protected static int getLowestSchemaSameVersion(Int2ObjectSortedMap<Schema> schemas, int versionKey) {
        if (versionKey < schemas.firstIntKey()) {
            return schemas.firstIntKey();
        }
        return schemas.subMap(0, versionKey + 1).lastIntKey();
    }

    private int getLowestFixSameVersion(int versionKey) {
        if (versionKey < this.fixerVersions.firstInt()) {
            return this.fixerVersions.firstInt() - 1;
        }
        return this.fixerVersions.subSet(0, versionKey + 1).lastInt();
    }

    protected TypeRewriteRule getRule(int version, int dataVersion) {
        if (version >= dataVersion) {
            return TypeRewriteRule.nop();
        }
        int expandedVersion = this.getLowestFixSameVersion(DataFixUtils.makeKey(version));
        int expandedDataVersion = DataFixUtils.makeKey(dataVersion);
        long key = (long)expandedVersion << 32 | (long)expandedDataVersion;
        return (TypeRewriteRule)this.rules.computeIfAbsent((Object)key, k -> {
            ArrayList<TypeRewriteRule> rules = Lists.newArrayList();
            for (DataFix fix : this.globalList) {
                TypeRewriteRule fixRule;
                int fixVersion = fix.getVersionKey();
                if (fixVersion <= expandedVersion || fixVersion > expandedDataVersion || (fixRule = fix.getRule()) == TypeRewriteRule.nop()) continue;
                rules.add(fixRule);
            }
            return TypeRewriteRule.seq(rules);
        });
    }

    protected IntSortedSet fixerVersions() {
        return this.fixerVersions;
    }
}
