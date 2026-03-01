package org.apache.maven.artifact.versioning;

public interface ArtifactVersion
extends Comparable<ArtifactVersion> {
    public int getMajorVersion();

    public int getMinorVersion();

    public int getIncrementalVersion();

    public int getBuildNumber();

    public String getQualifier();

    public void parseVersion(String var1);
}
