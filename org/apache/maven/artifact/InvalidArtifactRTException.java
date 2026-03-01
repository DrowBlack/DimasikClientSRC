package org.apache.maven.artifact;

public class InvalidArtifactRTException
extends RuntimeException {
    private final String groupId;
    private final String artifactId;
    private final String version;
    private final String type;
    private final String baseMessage;

    public InvalidArtifactRTException(String groupId, String artifactId, String version, String type, String message) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.type = type;
        this.baseMessage = message;
    }

    public InvalidArtifactRTException(String groupId, String artifactId, String version, String type, String message, Throwable cause) {
        super(cause);
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.type = type;
        this.baseMessage = message;
    }

    @Override
    public String getMessage() {
        return "For artifact {" + this.getArtifactKey() + "}: " + this.getBaseMessage();
    }

    public String getBaseMessage() {
        return this.baseMessage;
    }

    public String getArtifactId() {
        return this.artifactId;
    }

    public String getGroupId() {
        return this.groupId;
    }

    public String getType() {
        return this.type;
    }

    public String getVersion() {
        return this.version;
    }

    public String getArtifactKey() {
        return this.groupId + ":" + this.artifactId + ":" + this.version + ":" + this.type;
    }
}
