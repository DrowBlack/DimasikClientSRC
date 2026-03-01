package cpw.mods.modlauncher;

import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.LogMarkers;
import cpw.mods.modlauncher.ServiceLoaderStreamUtils;
import cpw.mods.modlauncher.TransformerAuditTrail;
import cpw.mods.modlauncher.TransformingClassLoader;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

public class LaunchPluginHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<String, ILaunchPluginService> plugins;

    public LaunchPluginHandler() {
        ServiceLoader<ILaunchPluginService> services = ServiceLoaderStreamUtils.errorHandlingServiceLoader(ILaunchPluginService.class, e -> LOGGER.fatal(LogMarkers.MODLAUNCHER, "Encountered serious error loading launch plugin service. Things will not work well", (Throwable)e));
        this.plugins = ServiceLoaderStreamUtils.toMap(services, ILaunchPluginService::name);
        ArrayList modlist = new ArrayList();
        this.plugins.forEach((name, plugin) -> {
            HashMap<String, String> mod = new HashMap<String, String>();
            mod.put("name", (String)name);
            mod.put("type", "PLUGINSERVICE");
            String fName = plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
            mod.put("file", fName.substring(fName.lastIndexOf("/")));
            modlist.add(mod);
        });
        if (Launcher.INSTANCE != null) {
            List<Map<String, String>> mods = Launcher.INSTANCE.environment().getProperty(IEnvironment.Keys.MODLIST.get()).orElseThrow(() -> new RuntimeException("The MODLIST isn't set, huh?"));
            mods.addAll(modlist);
        }
        LOGGER.debug(LogMarkers.MODLAUNCHER, "Found launch plugins: [{}]", () -> String.join((CharSequence)",", this.plugins.keySet()));
    }

    public Optional<ILaunchPluginService> get(String name) {
        return Optional.ofNullable(this.plugins.get(name));
    }

    public EnumMap<ILaunchPluginService.Phase, List<ILaunchPluginService>> computeLaunchPluginTransformerSet(Type className, boolean isEmpty, String reason, TransformerAuditTrail auditTrail) {
        EnumMap<ILaunchPluginService.Phase, List<ILaunchPluginService>> phaseObjectEnumMap = new EnumMap<ILaunchPluginService.Phase, List<ILaunchPluginService>>(ILaunchPluginService.Phase.class);
        this.plugins.forEach((n, pl) -> pl.handlesClass(className, isEmpty, reason).forEach(ph -> phaseObjectEnumMap.computeIfAbsent((ILaunchPluginService.Phase)((Object)ph), e -> new ArrayList()).add(pl)));
        phaseObjectEnumMap.values().stream().flatMap(Collection::stream).distinct().forEach(pl -> pl.customAuditConsumer(className.getClassName(), strings -> auditTrail.addPluginCustomAuditTrail(className.getClassName(), (ILaunchPluginService)pl, (String)strings)));
        LOGGER.debug(LogMarkers.LAUNCHPLUGIN, "LaunchPluginService {}", () -> phaseObjectEnumMap);
        return phaseObjectEnumMap;
    }

    void offerScanResultsToPlugins(List<Map.Entry<String, Path>> scanResults) {
        this.plugins.forEach((n, p) -> p.addResources(scanResults));
    }

    boolean offerClassNodeToPlugins(ILaunchPluginService.Phase phase, List<ILaunchPluginService> plugins, @Nullable ClassNode node, Type className, TransformerAuditTrail auditTrail, String reason) {
        boolean needsRewriting = false;
        for (ILaunchPluginService iLaunchPluginService : plugins) {
            LOGGER.debug(LogMarkers.LAUNCHPLUGIN, "LauncherPluginService {} offering transform {}", (Object)iLaunchPluginService.name(), (Object)className.getClassName());
            if (!iLaunchPluginService.processClass(phase, node, className, reason)) continue;
            auditTrail.addPluginAuditTrail(className.getClassName(), iLaunchPluginService, phase);
            LOGGER.debug(LogMarkers.LAUNCHPLUGIN, "LauncherPluginService {} transformed {}", (Object)iLaunchPluginService.name(), (Object)className.getClassName());
            needsRewriting = true;
        }
        return needsRewriting;
    }

    void announceLaunch(TransformingClassLoader transformerLoader, Path[] specialPaths) {
        this.plugins.forEach((k, p) -> p.initializeLaunch(s -> transformerLoader.buildTransformedClassNodeFor(s, (String)k), specialPaths));
    }
}
