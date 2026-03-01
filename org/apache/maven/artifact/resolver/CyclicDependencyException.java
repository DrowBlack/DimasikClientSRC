package org.apache.maven.artifact.resolver;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;

public class CyclicDependencyException
extends ArtifactResolutionException {
    private Artifact artifact;

    public CyclicDependencyException(String message, Artifact artifact) {
        super(message, artifact);
        this.artifact = artifact;
    }

    @Override
    public Artifact getArtifact() {
        return this.artifact;
    }
}
