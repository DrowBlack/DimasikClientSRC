package cpw.mods.modlauncher;

import cpw.mods.modlauncher.TransformTargetLabel;
import cpw.mods.modlauncher.api.ITransformer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TransformList<T> {
    private final Map<TransformTargetLabel, List<ITransformer<T>>> transformers = new ConcurrentHashMap<TransformTargetLabel, List<ITransformer<T>>>();
    private final Class<T> nodeType;

    TransformList(Class<T> nodeType) {
        this.nodeType = nodeType;
    }

    private Map<TransformTargetLabel, List<ITransformer<T>>> getTransformers() {
        return this.transformers;
    }

    void addTransformer(TransformTargetLabel targetLabel, ITransformer<T> transformer) {
        this.transformers.computeIfAbsent(targetLabel, v -> new ArrayList());
        this.transformers.computeIfPresent(targetLabel, (k, l) -> {
            l.add(transformer);
            return l;
        });
    }

    List<ITransformer<T>> getTransformersForLabel(TransformTargetLabel label) {
        return this.transformers.computeIfAbsent(label, v -> new ArrayList());
    }
}
