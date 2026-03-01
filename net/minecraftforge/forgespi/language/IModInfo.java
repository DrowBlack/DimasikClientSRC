package net.minecraftforge.forgespi.language;

import java.net.URL;
import java.util.List;
import java.util.Map;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.forgespi.Environment;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.MavenVersionAdapter;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;

public interface IModInfo {
    public static final VersionRange UNBOUNDED = MavenVersionAdapter.createFromVersionSpec("");

    public IModFileInfo getOwningFile();

    public String getModId();

    public String getDisplayName();

    public String getDescription();

    public ArtifactVersion getVersion();

    public List<? extends ModVersion> getDependencies();

    public String getNamespace();

    public Map<String, Object> getModProperties();

    public URL getUpdateURL();

    public static interface ModVersion {
        public String getModId();

        public VersionRange getVersionRange();

        public boolean isMandatory();

        public Ordering getOrdering();

        public DependencySide getSide();

        public void setOwner(IModInfo var1);

        public IModInfo getOwner();
    }

    public static enum DependencySide {
        CLIENT(Dist.CLIENT),
        SERVER(Dist.DEDICATED_SERVER),
        BOTH(Dist.values());

        private final Dist[] dist;

        private DependencySide(Dist ... dist) {
            this.dist = dist;
        }

        public boolean isCorrectSide() {
            return this == BOTH || Environment.get().getDist().equals((Object)this.dist[0]);
        }
    }

    public static enum Ordering {
        BEFORE,
        AFTER,
        NONE;

    }
}
