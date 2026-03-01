package org.apache.maven.repository.legacy.metadata;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataStoreException;

public interface ArtifactMetadata {
    public boolean storedInArtifactVersionDirectory();

    public boolean storedInGroupDirectory();

    public String getGroupId();

    public String getArtifactId();

    public String getBaseVersion();

    public Object getKey();

    public String getLocalFilename(ArtifactRepository var1);

    public String getRemoteFilename();

    public void merge(ArtifactMetadata var1);

    public void storeInLocalRepository(ArtifactRepository var1, ArtifactRepository var2) throws RepositoryMetadataStoreException;

    public String extendedToString();
}
