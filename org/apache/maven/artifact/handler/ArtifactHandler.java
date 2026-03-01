package org.apache.maven.artifact.handler;

public interface ArtifactHandler {
    public static final String ROLE = ArtifactHandler.class.getName();

    public String getExtension();

    public String getDirectory();

    public String getClassifier();

    public String getPackaging();

    public boolean isIncludesDependencies();

    public String getLanguage();

    public boolean isAddedToClasspath();
}
