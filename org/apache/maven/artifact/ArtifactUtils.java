package org.apache.maven.artifact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import org.apache.commons.lang3.Validate;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.versioning.VersionRange;

public final class ArtifactUtils {
    public static boolean isSnapshot(String version) {
        if (version != null) {
            if (version.regionMatches(true, version.length() - "SNAPSHOT".length(), "SNAPSHOT", 0, "SNAPSHOT".length())) {
                return true;
            }
            if (Artifact.VERSION_FILE_PATTERN.matcher(version).matches()) {
                return true;
            }
        }
        return false;
    }

    public static String toSnapshotVersion(String version) {
        Matcher m;
        int prevHyphen;
        ArtifactUtils.notBlank(version, "version can neither be null, empty nor blank");
        int lastHyphen = version.lastIndexOf(45);
        if (lastHyphen > 0 && (prevHyphen = version.lastIndexOf(45, lastHyphen - 1)) > 0 && (m = Artifact.VERSION_FILE_PATTERN.matcher(version)).matches()) {
            return m.group(1) + "-" + "SNAPSHOT";
        }
        return version;
    }

    public static String versionlessKey(Artifact artifact) {
        return ArtifactUtils.versionlessKey(artifact.getGroupId(), artifact.getArtifactId());
    }

    public static String versionlessKey(String groupId, String artifactId) {
        ArtifactUtils.notBlank(groupId, "groupId can neither be null, empty nor blank");
        ArtifactUtils.notBlank(artifactId, "artifactId can neither be null, empty nor blank");
        return groupId + ":" + artifactId;
    }

    public static String key(Artifact artifact) {
        return ArtifactUtils.key(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion());
    }

    public static String key(String groupId, String artifactId, String version) {
        ArtifactUtils.notBlank(groupId, "groupId can neither be null, empty nor blank");
        ArtifactUtils.notBlank(artifactId, "artifactId can neither be null, empty nor blank");
        ArtifactUtils.notBlank(version, "version can neither be null, empty nor blank");
        return groupId + ":" + artifactId + ":" + version;
    }

    private static void notBlank(String str, String message) {
        int c;
        int n = c = str != null && str.length() > 0 ? (int)str.charAt(0) : 0;
        if (!(c >= 48 && c <= 57 || c >= 97 && c <= 122)) {
            Validate.notBlank(str, message, new Object[0]);
        }
    }

    public static Map<String, Artifact> artifactMapByVersionlessId(Collection<Artifact> artifacts) {
        LinkedHashMap<String, Artifact> artifactMap = new LinkedHashMap<String, Artifact>();
        if (artifacts != null) {
            for (Artifact artifact : artifacts) {
                artifactMap.put(ArtifactUtils.versionlessKey(artifact), artifact);
            }
        }
        return artifactMap;
    }

    public static Artifact copyArtifactSafe(Artifact artifact) {
        return artifact != null ? ArtifactUtils.copyArtifact(artifact) : null;
    }

    public static Artifact copyArtifact(Artifact artifact) {
        VersionRange range = artifact.getVersionRange();
        if (range == null) {
            range = VersionRange.createFromVersion(artifact.getVersion());
        }
        DefaultArtifact clone = new DefaultArtifact(artifact.getGroupId(), artifact.getArtifactId(), range, artifact.getScope(), artifact.getType(), artifact.getClassifier(), artifact.getArtifactHandler(), artifact.isOptional());
        clone.setRelease(artifact.isRelease());
        clone.setResolvedVersion(artifact.getVersion());
        clone.setResolved(artifact.isResolved());
        clone.setFile(artifact.getFile());
        clone.setAvailableVersions(ArtifactUtils.copyList(artifact.getAvailableVersions()));
        if (artifact.getVersion() != null) {
            clone.setBaseVersion(artifact.getBaseVersion());
        }
        clone.setDependencyFilter(artifact.getDependencyFilter());
        clone.setDependencyTrail(ArtifactUtils.copyList(artifact.getDependencyTrail()));
        clone.setDownloadUrl(artifact.getDownloadUrl());
        clone.setRepository(artifact.getRepository());
        return clone;
    }

    public static <T extends Collection<Artifact>> T copyArtifacts(Collection<Artifact> from, T to) {
        for (Artifact artifact : from) {
            to.add((Artifact)ArtifactUtils.copyArtifact(artifact));
        }
        return to;
    }

    public static <K, T extends Map<K, Artifact>> T copyArtifacts(Map<K, ? extends Artifact> from, T to) {
        if (from != null) {
            for (Map.Entry<K, Artifact> entry : from.entrySet()) {
                to.put(entry.getKey(), (Artifact)ArtifactUtils.copyArtifact(entry.getValue()));
            }
        }
        return to;
    }

    private static <T> List<T> copyList(List<T> original) {
        ArrayList<T> copy = null;
        if (original != null) {
            copy = new ArrayList<T>();
            if (!original.isEmpty()) {
                copy.addAll(original);
            }
        }
        return copy;
    }
}
