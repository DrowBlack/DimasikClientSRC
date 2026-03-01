package cpw.mods.modlauncher;

import cpw.mods.modlauncher.LogMarkers;
import cpw.mods.modlauncher.TransformList;
import cpw.mods.modlauncher.TransformTargetLabel;
import cpw.mods.modlauncher.TransformerHolder;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class TransformStore {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Set<TransformTargetLabel> classNeedsTransforming = new HashSet<TransformTargetLabel>();
    private final EnumMap<TransformTargetLabel.LabelType, TransformList<?>> transformers = new EnumMap(TransformTargetLabel.LabelType.class);

    public TransformStore() {
        this.transformers.put(TransformTargetLabel.LabelType.CLASS, new TransformList<ClassNode>(ClassNode.class));
        this.transformers.put(TransformTargetLabel.LabelType.METHOD, new TransformList<MethodNode>(MethodNode.class));
        this.transformers.put(TransformTargetLabel.LabelType.FIELD, new TransformList<FieldNode>(FieldNode.class));
    }

    List<ITransformer<FieldNode>> getTransformersFor(String className, FieldNode field) {
        TransformTargetLabel tl = new TransformTargetLabel(className, field.name);
        TransformList transformerlist = TransformTargetLabel.LabelType.FIELD.getFromMap(this.transformers);
        return transformerlist.getTransformersForLabel(tl);
    }

    List<ITransformer<MethodNode>> getTransformersFor(String className, MethodNode method) {
        TransformTargetLabel tl = new TransformTargetLabel(className, method.name, method.desc);
        TransformList transformerlist = TransformTargetLabel.LabelType.METHOD.getFromMap(this.transformers);
        return transformerlist.getTransformersForLabel(tl);
    }

    List<ITransformer<ClassNode>> getTransformersFor(String className) {
        TransformTargetLabel tl = new TransformTargetLabel(className);
        TransformList transformerlist = TransformTargetLabel.LabelType.CLASS.getFromMap(this.transformers);
        return transformerlist.getTransformersForLabel(tl);
    }

    <T> void addTransformer(TransformTargetLabel targetLabel, ITransformer<T> transformer, ITransformationService service) {
        LOGGER.debug(LogMarkers.MODLAUNCHER, "Adding transformer {} to {}", () -> transformer, () -> targetLabel);
        this.classNeedsTransforming.add(new TransformTargetLabel(targetLabel.getClassName().getInternalName()));
        TransformList<?> transformList = this.transformers.get((Object)targetLabel.getLabelType());
        transformList.addTransformer(targetLabel, new TransformerHolder<T>(transformer, service));
    }

    boolean needsTransforming(String className) {
        return this.classNeedsTransforming.contains(new TransformTargetLabel(className));
    }
}
