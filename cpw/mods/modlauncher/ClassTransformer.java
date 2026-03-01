package cpw.mods.modlauncher;

import cpw.mods.modlauncher.LaunchPluginHandler;
import cpw.mods.modlauncher.LogMarkers;
import cpw.mods.modlauncher.TransformStore;
import cpw.mods.modlauncher.TransformerAuditTrail;
import cpw.mods.modlauncher.TransformerClassWriter;
import cpw.mods.modlauncher.TransformerHolder;
import cpw.mods.modlauncher.TransformerVote;
import cpw.mods.modlauncher.TransformingClassLoader;
import cpw.mods.modlauncher.VoteDeadlockException;
import cpw.mods.modlauncher.VoteRejectedException;
import cpw.mods.modlauncher.VotingContext;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.MarkerManager;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class ClassTransformer {
    private static final byte[] EMPTY = new byte[0];
    private static final Logger LOGGER = LogManager.getLogger();
    private final TransformStore transformers;
    private final LaunchPluginHandler pluginHandler;
    private final TransformingClassLoader transformingClassLoader;
    private final TransformerAuditTrail auditTrail;
    private static Path tempDir;

    ClassTransformer(TransformStore transformStore, LaunchPluginHandler pluginHandler, TransformingClassLoader transformingClassLoader) {
        this(transformStore, pluginHandler, transformingClassLoader, new TransformerAuditTrail());
    }

    ClassTransformer(TransformStore transformStore, LaunchPluginHandler pluginHandler, TransformingClassLoader transformingClassLoader, TransformerAuditTrail tat) {
        this.transformers = transformStore;
        this.pluginHandler = pluginHandler;
        this.transformingClassLoader = transformingClassLoader;
        this.auditTrail = tat;
    }

    byte[] transform(byte[] inputClass, String className, String reason) {
        boolean empty;
        Supplier<byte[]> digest;
        Type classDesc = Type.getObjectType(className.replace('.', '/'));
        EnumMap<ILaunchPluginService.Phase, List<ILaunchPluginService>> launchPluginTransformerSet = this.pluginHandler.computeLaunchPluginTransformerSet(classDesc, inputClass.length == 0, reason, this.auditTrail);
        boolean needsTransforming = this.transformers.needsTransforming(className);
        if (!needsTransforming && launchPluginTransformerSet.isEmpty()) {
            return inputClass;
        }
        ClassNode clazz = new ClassNode(458752);
        if (inputClass.length > 0) {
            ClassReader classReader = new ClassReader(inputClass);
            classReader.accept(clazz, 0);
            digest = () -> this.getSha256().digest(inputClass);
            empty = false;
        } else {
            clazz.name = classDesc.getInternalName();
            clazz.version = 52;
            clazz.superName = "java/lang/Object";
            digest = () -> this.getSha256().digest(EMPTY);
            empty = true;
        }
        this.auditTrail.addReason(classDesc.getClassName(), reason);
        boolean preresult = this.pluginHandler.offerClassNodeToPlugins(ILaunchPluginService.Phase.BEFORE, launchPluginTransformerSet.getOrDefault((Object)ILaunchPluginService.Phase.BEFORE, Collections.emptyList()), clazz, classDesc, this.auditTrail, reason);
        if (!preresult && !needsTransforming && launchPluginTransformerSet.getOrDefault((Object)ILaunchPluginService.Phase.AFTER, Collections.emptyList()).isEmpty()) {
            return inputClass;
        }
        if (needsTransforming) {
            VotingContext context = new VotingContext(className, empty, digest, this.auditTrail.getActivityFor(className), reason);
            ArrayList<FieldNode> fieldList = new ArrayList<FieldNode>(clazz.fields.size());
            for (FieldNode fieldNode : clazz.fields) {
                ArrayList fieldTransformers = new ArrayList(this.transformers.getTransformersFor(className, fieldNode));
                fieldList.add(this.performVote(fieldTransformers, fieldNode, context));
            }
            ArrayList<MethodNode> methodList = new ArrayList<MethodNode>(clazz.methods.size());
            for (MethodNode method : clazz.methods) {
                ArrayList methodTransformers = new ArrayList(this.transformers.getTransformersFor(className, method));
                methodList.add(this.performVote(methodTransformers, method, context));
            }
            clazz.fields = fieldList;
            clazz.methods = methodList;
            ArrayList arrayList = new ArrayList(this.transformers.getTransformersFor(className));
            clazz = this.performVote(arrayList, clazz, context);
        }
        boolean postresult = this.pluginHandler.offerClassNodeToPlugins(ILaunchPluginService.Phase.AFTER, launchPluginTransformerSet.getOrDefault((Object)ILaunchPluginService.Phase.AFTER, Collections.emptyList()), clazz, classDesc, this.auditTrail, reason);
        if (!(preresult || postresult || needsTransforming)) {
            return inputClass;
        }
        TransformerClassWriter cw = new TransformerClassWriter(this, clazz);
        clazz.accept(cw);
        if (MarkerManager.exists("CLASSDUMP") && LOGGER.isEnabled(Level.TRACE) && LOGGER.isEnabled(Level.TRACE, MarkerManager.getMarker("CLASSDUMP"))) {
            this.dumpClass(cw.toByteArray(), className);
        }
        return cw.toByteArray();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    private void dumpClass(byte[] clazz, String className) {
        if (tempDir == null) {
            Class<ClassTransformer> clazz2 = ClassTransformer.class;
            // MONITORENTER : cpw.mods.modlauncher.ClassTransformer.class
            if (tempDir == null) {
                try {
                    tempDir = Files.createTempDirectory("classDump", new FileAttribute[0]);
                }
                catch (IOException e) {
                    LOGGER.error(LogMarkers.MODLAUNCHER, "Failed to create temporary directory");
                    // MONITOREXIT : clazz2
                    return;
                }
            }
            // MONITOREXIT : clazz2
        }
        try {
            Path tempFile = Files.createTempFile(tempDir, className, ".class", new FileAttribute[0]);
            Files.write(tempFile, clazz, new OpenOption[0]);
            LOGGER.info(LogMarkers.MODLAUNCHER, "Wrote {} byte class file {} to {}", (Object)clazz.length, (Object)className, (Object)tempFile);
            return;
        }
        catch (IOException e) {
            LOGGER.error(LogMarkers.MODLAUNCHER, "Failed to write class file {}", (Object)className, (Object)e);
        }
    }

    private <T> T performVote(List<ITransformer<T>> transformers, T node, VotingContext context) {
        context.setNode(node);
        do {
            Stream<TransformerVote> voteResultStream;
            Map<TransformerVoteResult, List<TransformerVote>> results;
            if ((results = (voteResultStream = transformers.stream().map(t -> this.gatherVote((ITransformer)t, context))).collect(Collectors.groupingBy(TransformerVote::getResult))).containsKey((Object)TransformerVoteResult.REJECT)) {
                throw new VoteRejectedException(results.get((Object)TransformerVoteResult.REJECT), node.getClass());
            }
            if (results.containsKey((Object)TransformerVoteResult.NO)) {
                transformers.removeAll(results.get((Object)TransformerVoteResult.NO).stream().map(TransformerVote::getTransformer).collect(Collectors.toList()));
            }
            if (results.containsKey((Object)TransformerVoteResult.YES)) {
                ITransformer<T> transformer = results.get((Object)TransformerVoteResult.YES).get(0).getTransformer();
                node = transformer.transform(node, context);
                this.auditTrail.addTransformerAuditTrail(context.getClassName(), ((TransformerHolder)transformer).owner(), transformer);
                transformers.remove(transformer);
                continue;
            }
            if (!results.containsKey((Object)TransformerVoteResult.DEFER)) continue;
            throw new VoteDeadlockException(results.get((Object)TransformerVoteResult.DEFER), node.getClass());
        } while (!transformers.isEmpty());
        return node;
    }

    private <T> TransformerVote<T> gatherVote(ITransformer<T> transformer, VotingContext context) {
        TransformerVoteResult vr = transformer.castVote(context);
        return new TransformerVote<T>(vr, transformer);
    }

    private MessageDigest getSha256() {
        try {
            return MessageDigest.getInstance("SHA-256");
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("HUH");
        }
    }

    TransformingClassLoader getTransformingClassLoader() {
        return this.transformingClassLoader;
    }
}
