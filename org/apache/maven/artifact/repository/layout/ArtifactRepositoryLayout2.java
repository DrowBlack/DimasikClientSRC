package org.apache.maven.artifact.repository.layout;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;

public interface ArtifactRepositoryLayout2
extends ArtifactRepositoryLayout {
    public ArtifactRepository newMavenArtifactRepository(String var1, String var2, ArtifactRepositoryPolicy var3, ArtifactRepositoryPolicy var4);
}
