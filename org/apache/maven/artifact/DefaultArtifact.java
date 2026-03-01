package org.apache.maven.artifact;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.artifact.InvalidArtifactRTException;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.OverConstrainedVersionException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.codehaus.plexus.util.StringUtils;

public class DefaultArtifact
implements Artifact {
    private String groupId;
    private String artifactId;
    private String baseVersion;
    private final String type;
    private final String classifier;
    private volatile String scope;
    private volatile File file;
    private ArtifactRepository repository;
    private String downloadUrl;
    private ArtifactFilter dependencyFilter;
    private ArtifactHandler artifactHandler;
    private List<String> dependencyTrail;
    private volatile String version;
    private VersionRange versionRange;
    private volatile boolean resolved;
    private boolean release;
    private List<ArtifactVersion> availableVersions;
    private Map<Object, ArtifactMetadata> metadataMap;
    private boolean optional;

    public DefaultArtifact(String groupId, String artifactId, String version, String scope, String type, String classifier, ArtifactHandler artifactHandler) {
        this(groupId, artifactId, VersionRange.createFromVersion(version), scope, type, classifier, artifactHandler, false);
    }

    public DefaultArtifact(String groupId, String artifactId, VersionRange versionRange, String scope, String type, String classifier, ArtifactHandler artifactHandler) {
        this(groupId, artifactId, versionRange, scope, type, classifier, artifactHandler, false);
    }

    public DefaultArtifact(String groupId, String artifactId, VersionRange versionRange, String scope, String type, String classifier, ArtifactHandler artifactHandler, boolean optional) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.versionRange = versionRange;
        this.selectVersionFromNewRangeIfAvailable();
        this.artifactHandler = artifactHandler;
        this.scope = scope;
        this.type = type;
        if (classifier == null) {
            classifier = artifactHandler.getClassifier();
        }
        this.classifier = classifier;
        this.optional = optional;
        this.validateIdentity();
    }

    private void validateIdentity() {
        if (this.empty(this.groupId)) {
            throw new InvalidArtifactRTException(this.groupId, this.artifactId, this.getVersion(), this.type, "The groupId cannot be empty.");
        }
        if (this.artifactId == null) {
            throw new InvalidArtifactRTException(this.groupId, this.artifactId, this.getVersion(), this.type, "The artifactId cannot be empty.");
        }
        if (this.type == null) {
            throw new InvalidArtifactRTException(this.groupId, this.artifactId, this.getVersion(), this.type, "The type cannot be empty.");
        }
        if (this.version == null && this.versionRange == null) {
            throw new InvalidArtifactRTException(this.groupId, this.artifactId, this.getVersion(), this.type, "The version cannot be empty.");
        }
    }

    private boolean empty(String value) {
        return value == null || value.trim().length() < 1;
    }

    @Override
    public String getClassifier() {
        return this.classifier;
    }

    @Override
    public boolean hasClassifier() {
        return StringUtils.isNotEmpty(this.classifier);
    }

    @Override
    public String getScope() {
        return this.scope;
    }

    @Override
    public String getGroupId() {
        return this.groupId;
    }

    @Override
    public String getArtifactId() {
        return this.artifactId;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
        this.setBaseVersionInternal(version);
        this.versionRange = null;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public File getFile() {
        return this.file;
    }

    @Override
    public ArtifactRepository getRepository() {
        return this.repository;
    }

    @Override
    public void setRepository(ArtifactRepository repository) {
        this.repository = repository;
    }

    @Override
    public String getId() {
        return this.getDependencyConflictId() + ":" + this.getBaseVersion();
    }

    @Override
    public String getDependencyConflictId() {
        StringBuilder sb = new StringBuilder(128);
        sb.append(this.getGroupId());
        sb.append(':');
        this.appendArtifactTypeClassifierString(sb);
        return sb.toString();
    }

    private void appendArtifactTypeClassifierString(StringBuilder sb) {
        sb.append(this.getArtifactId());
        sb.append(':');
        sb.append(this.getType());
        if (this.hasClassifier()) {
            sb.append(':');
            sb.append(this.getClassifier());
        }
    }

    @Override
    public void addMetadata(ArtifactMetadata metadata) {
        ArtifactMetadata m;
        if (this.metadataMap == null) {
            this.metadataMap = new HashMap<Object, ArtifactMetadata>();
        }
        if ((m = this.metadataMap.get(metadata.getKey())) != null) {
            m.merge(metadata);
        } else {
            this.metadataMap.put(metadata.getKey(), metadata);
        }
    }

    @Override
    public Collection<ArtifactMetadata> getMetadataList() {
        if (this.metadataMap == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableCollection(this.metadataMap.values());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.getGroupId() != null) {
            sb.append(this.getGroupId());
            sb.append(':');
        }
        this.appendArtifactTypeClassifierString(sb);
        sb.append(':');
        if (this.getBaseVersionInternal() != null) {
            sb.append(this.getBaseVersionInternal());
        } else {
            sb.append(this.versionRange.toString());
        }
        if (this.scope != null) {
            sb.append(':');
            sb.append(this.scope);
        }
        return sb.toString();
    }

    public int hashCode() {
        int result = 17;
        result = 37 * result + this.groupId.hashCode();
        result = 37 * result + this.artifactId.hashCode();
        result = 37 * result + this.type.hashCode();
        if (this.version != null) {
            result = 37 * result + this.version.hashCode();
        }
        result = 37 * result + (this.classifier != null ? this.classifier.hashCode() : 0);
        return result;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Artifact)) {
            return false;
        }
        Artifact a = (Artifact)o;
        if (!a.getGroupId().equals(this.groupId)) {
            return false;
        }
        if (!a.getArtifactId().equals(this.artifactId)) {
            return false;
        }
        if (!a.getVersion().equals(this.version)) {
            return false;
        }
        if (!a.getType().equals(this.type)) {
            return false;
        }
        return !(a.getClassifier() == null ? this.classifier != null : !a.getClassifier().equals(this.classifier));
    }

    @Override
    public String getBaseVersion() {
        if (this.baseVersion == null && this.version != null) {
            this.setBaseVersionInternal(this.version);
        }
        return this.baseVersion;
    }

    protected String getBaseVersionInternal() {
        if (this.baseVersion == null && this.version != null) {
            this.setBaseVersionInternal(this.version);
        }
        return this.baseVersion;
    }

    @Override
    public void setBaseVersion(String baseVersion) {
        this.setBaseVersionInternal(baseVersion);
    }

    protected void setBaseVersionInternal(String baseVersion) {
        this.baseVersion = ArtifactUtils.toSnapshotVersion(baseVersion);
    }

    @Override
    public int compareTo(Artifact a) {
        int result = this.groupId.compareTo(a.getGroupId());
        if (result == 0 && (result = this.artifactId.compareTo(a.getArtifactId())) == 0 && (result = this.type.compareTo(a.getType())) == 0) {
            if (this.classifier == null) {
                if (a.getClassifier() != null) {
                    result = 1;
                }
            } else {
                result = a.getClassifier() != null ? this.classifier.compareTo(a.getClassifier()) : -1;
            }
            if (result == 0) {
                result = new DefaultArtifactVersion(this.version).compareTo(new DefaultArtifactVersion(a.getVersion()));
            }
        }
        return result;
    }

    @Override
    public void updateVersion(String version, ArtifactRepository localRepository) {
        this.setResolvedVersion(version);
        this.setFile(new File(localRepository.getBasedir(), localRepository.pathOf(this)));
    }

    @Override
    public String getDownloadUrl() {
        return this.downloadUrl;
    }

    @Override
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    @Override
    public ArtifactFilter getDependencyFilter() {
        return this.dependencyFilter;
    }

    @Override
    public void setDependencyFilter(ArtifactFilter artifactFilter) {
        this.dependencyFilter = artifactFilter;
    }

    @Override
    public ArtifactHandler getArtifactHandler() {
        return this.artifactHandler;
    }

    @Override
    public List<String> getDependencyTrail() {
        return this.dependencyTrail;
    }

    @Override
    public void setDependencyTrail(List<String> dependencyTrail) {
        this.dependencyTrail = dependencyTrail;
    }

    @Override
    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public VersionRange getVersionRange() {
        return this.versionRange;
    }

    @Override
    public void setVersionRange(VersionRange versionRange) {
        this.versionRange = versionRange;
        this.selectVersionFromNewRangeIfAvailable();
    }

    private void selectVersionFromNewRangeIfAvailable() {
        if (this.versionRange != null && this.versionRange.getRecommendedVersion() != null) {
            this.selectVersion(this.versionRange.getRecommendedVersion().toString());
        } else {
            this.version = null;
            this.baseVersion = null;
        }
    }

    @Override
    public void selectVersion(String version) {
        this.version = version;
        this.setBaseVersionInternal(version);
    }

    @Override
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    @Override
    public boolean isSnapshot() {
        return this.getBaseVersion() != null && (this.getBaseVersion().endsWith("SNAPSHOT") || this.getBaseVersion().equals("LATEST"));
    }

    @Override
    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    @Override
    public boolean isResolved() {
        return this.resolved;
    }

    @Override
    public void setResolvedVersion(String version) {
        this.version = version;
    }

    @Override
    public void setArtifactHandler(ArtifactHandler artifactHandler) {
        this.artifactHandler = artifactHandler;
    }

    @Override
    public void setRelease(boolean release) {
        this.release = release;
    }

    @Override
    public boolean isRelease() {
        return this.release;
    }

    @Override
    public List<ArtifactVersion> getAvailableVersions() {
        return this.availableVersions;
    }

    @Override
    public void setAvailableVersions(List<ArtifactVersion> availableVersions) {
        this.availableVersions = availableVersions;
    }

    @Override
    public boolean isOptional() {
        return this.optional;
    }

    @Override
    public ArtifactVersion getSelectedVersion() throws OverConstrainedVersionException {
        return this.versionRange.getSelectedVersion(this);
    }

    @Override
    public boolean isSelectedVersionKnown() throws OverConstrainedVersionException {
        return this.versionRange.isSelectedVersionKnown(this);
    }

    @Override
    public void setOptional(boolean optional) {
        this.optional = optional;
    }
}
