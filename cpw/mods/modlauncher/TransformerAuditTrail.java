package cpw.mods.modlauncher;

import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerActivity;
import cpw.mods.modlauncher.api.ITransformerAuditTrail;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TransformerAuditTrail
implements ITransformerAuditTrail {
    private Map<String, List<ITransformerActivity>> audit = new ConcurrentHashMap<String, List<ITransformerActivity>>();

    @Override
    public List<ITransformerActivity> getActivityFor(String className) {
        return Collections.unmodifiableList(this.getTransformerActivities(className));
    }

    public void addReason(String clazz, String reason) {
        this.getTransformerActivities(clazz).add(new TransformerActivity(ITransformerActivity.Type.REASON, new String[]{reason}));
    }

    public void addPluginCustomAuditTrail(String clazz, ILaunchPluginService plugin, String ... data) {
        this.getTransformerActivities(clazz).add(new TransformerActivity(ITransformerActivity.Type.PLUGIN, this.concat(plugin.name(), data)));
    }

    public void addPluginAuditTrail(String clazz, ILaunchPluginService plugin, ILaunchPluginService.Phase phase) {
        this.getTransformerActivities(clazz).add(new TransformerActivity(ITransformerActivity.Type.PLUGIN, new String[]{plugin.name(), phase.name().substring(0, 1)}));
    }

    public void addTransformerAuditTrail(String clazz, ITransformationService transformService, ITransformer<?> transformer) {
        this.getTransformerActivities(clazz).add(new TransformerActivity(ITransformerActivity.Type.TRANSFORMER, this.concat(transformService.name(), transformer.labels())));
    }

    private String[] concat(String first, String[] rest) {
        String[] res = new String[rest.length + 1];
        res[0] = first;
        System.arraycopy(rest, 0, res, 1, rest.length);
        return res;
    }

    private List<ITransformerActivity> getTransformerActivities(String clazz) {
        return this.audit.computeIfAbsent(clazz, k -> new ArrayList());
    }

    @Override
    public String getAuditString(String clazz) {
        return this.audit.getOrDefault(clazz, Collections.emptyList()).stream().map(ITransformerActivity::getActivityString).collect(Collectors.joining(","));
    }

    private static class TransformerActivity
    implements ITransformerActivity {
        private final ITransformerActivity.Type type;
        private final String[] context;

        private TransformerActivity(ITransformerActivity.Type type, String ... context) {
            this.type = type;
            this.context = context;
        }

        @Override
        public String[] getContext() {
            return this.context;
        }

        @Override
        public ITransformerActivity.Type getType() {
            return this.type;
        }

        @Override
        public String getActivityString() {
            return this.type.getLabel() + ":" + String.join((CharSequence)":", this.context);
        }
    }
}
