package cpw.mods.modlauncher.api;

import cpw.mods.modlauncher.api.ITransformerActivity;
import java.util.List;

public interface ITransformerAuditTrail {
    public List<ITransformerActivity> getActivityFor(String var1);

    public String getAuditString(String var1);
}
