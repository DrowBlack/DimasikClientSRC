package org.apache.maven.artifact.repository.layout;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.ArtifactRepository;

public interface ArtifactRepositoryLayout {
    public static final String ROLE = ArtifactRepositoryLayout.class.getName();

    public String getId();

    public String pathOf(Artifact var1);

    public String pathOfLocalRepositoryMetadata(ArtifactMetadata var1, ArtifactRepository var2);

    public String pathOfRemoteRepositoryMetadata(ArtifactMetadata var1);
}
