package org.apache.maven.artifact.versioning;

import org.apache.maven.artifact.versioning.ArtifactVersion;

public class Restriction {
    private final ArtifactVersion lowerBound;
    private final boolean lowerBoundInclusive;
    private final ArtifactVersion upperBound;
    private final boolean upperBoundInclusive;
    public static final Restriction EVERYTHING = new Restriction(null, false, null, false);

    public Restriction(ArtifactVersion lowerBound, boolean lowerBoundInclusive, ArtifactVersion upperBound, boolean upperBoundInclusive) {
        this.lowerBound = lowerBound;
        this.lowerBoundInclusive = lowerBoundInclusive;
        this.upperBound = upperBound;
        this.upperBoundInclusive = upperBoundInclusive;
    }

    public ArtifactVersion getLowerBound() {
        return this.lowerBound;
    }

    public boolean isLowerBoundInclusive() {
        return this.lowerBoundInclusive;
    }

    public ArtifactVersion getUpperBound() {
        return this.upperBound;
    }

    public boolean isUpperBoundInclusive() {
        return this.upperBoundInclusive;
    }

    public boolean containsVersion(ArtifactVersion version) {
        int comparison;
        if (this.lowerBound != null) {
            comparison = this.lowerBound.compareTo(version);
            if (comparison == 0 && !this.lowerBoundInclusive) {
                return false;
            }
            if (comparison > 0) {
                return false;
            }
        }
        if (this.upperBound != null) {
            comparison = this.upperBound.compareTo(version);
            if (comparison == 0 && !this.upperBoundInclusive) {
                return false;
            }
            if (comparison < 0) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int result = 13;
        result = this.lowerBound == null ? ++result : (result += this.lowerBound.hashCode());
        result *= this.lowerBoundInclusive ? 1 : 2;
        result = this.upperBound == null ? (result -= 3) : (result -= this.upperBound.hashCode());
        return result *= this.upperBoundInclusive ? 2 : 3;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Restriction)) {
            return false;
        }
        Restriction restriction = (Restriction)other;
        if (this.lowerBound != null ? !this.lowerBound.equals(restriction.lowerBound) : restriction.lowerBound != null) {
            return false;
        }
        if (this.lowerBoundInclusive != restriction.lowerBoundInclusive) {
            return false;
        }
        if (this.upperBound != null ? !this.upperBound.equals(restriction.upperBound) : restriction.upperBound != null) {
            return false;
        }
        return this.upperBoundInclusive == restriction.upperBoundInclusive;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(this.isLowerBoundInclusive() ? (char)'[' : '(');
        if (this.getLowerBound() != null) {
            buf.append(this.getLowerBound().toString());
        }
        buf.append(',');
        if (this.getUpperBound() != null) {
            buf.append(this.getUpperBound().toString());
        }
        buf.append(this.isUpperBoundInclusive() ? (char)']' : ')');
        return buf.toString();
    }
}
