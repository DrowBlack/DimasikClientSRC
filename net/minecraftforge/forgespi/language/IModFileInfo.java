package net.minecraftforge.forgespi.language;

import java.util.List;
import java.util.Map;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.maven.artifact.versioning.VersionRange;

public interface IModFileInfo {
    public List<IModInfo> getMods();

    public String getModLoader();

    public VersionRange getModLoaderVersion();

    public boolean showAsResourcePack();

    public Map<String, Object> getFileProperties();

    public String getLicense();
}
