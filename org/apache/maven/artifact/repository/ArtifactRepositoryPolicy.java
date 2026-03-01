package org.apache.maven.artifact.repository;

import java.util.Calendar;
import java.util.Date;

public class ArtifactRepositoryPolicy {
    public static final String UPDATE_POLICY_NEVER = "never";
    public static final String UPDATE_POLICY_ALWAYS = "always";
    public static final String UPDATE_POLICY_DAILY = "daily";
    public static final String UPDATE_POLICY_INTERVAL = "interval";
    public static final String CHECKSUM_POLICY_FAIL = "fail";
    public static final String CHECKSUM_POLICY_WARN = "warn";
    public static final String CHECKSUM_POLICY_IGNORE = "ignore";
    private boolean enabled;
    private String updatePolicy;
    private String checksumPolicy;

    public ArtifactRepositoryPolicy() {
        this(true, null, null);
    }

    public ArtifactRepositoryPolicy(ArtifactRepositoryPolicy policy) {
        this(policy.isEnabled(), policy.getUpdatePolicy(), policy.getChecksumPolicy());
    }

    public ArtifactRepositoryPolicy(boolean enabled, String updatePolicy, String checksumPolicy) {
        this.enabled = enabled;
        if (updatePolicy == null) {
            updatePolicy = UPDATE_POLICY_DAILY;
        }
        this.updatePolicy = updatePolicy;
        if (checksumPolicy == null) {
            checksumPolicy = CHECKSUM_POLICY_WARN;
        }
        this.checksumPolicy = checksumPolicy;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setUpdatePolicy(String updatePolicy) {
        if (updatePolicy != null) {
            this.updatePolicy = updatePolicy;
        }
    }

    public void setChecksumPolicy(String checksumPolicy) {
        if (checksumPolicy != null) {
            this.checksumPolicy = checksumPolicy;
        }
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public String getUpdatePolicy() {
        return this.updatePolicy;
    }

    public String getChecksumPolicy() {
        return this.checksumPolicy;
    }

    public boolean checkOutOfDate(Date lastModified) {
        boolean checkForUpdates = false;
        if (UPDATE_POLICY_ALWAYS.equals(this.updatePolicy)) {
            checkForUpdates = true;
        } else if (UPDATE_POLICY_DAILY.equals(this.updatePolicy)) {
            Calendar cal = Calendar.getInstance();
            cal.set(11, 0);
            cal.set(12, 0);
            cal.set(13, 0);
            cal.set(14, 0);
            if (cal.getTime().after(lastModified)) {
                checkForUpdates = true;
            }
        } else if (this.updatePolicy.startsWith(UPDATE_POLICY_INTERVAL)) {
            String s = this.updatePolicy.substring(UPDATE_POLICY_INTERVAL.length() + 1);
            int minutes = Integer.valueOf(s);
            Calendar cal = Calendar.getInstance();
            cal.add(12, -minutes);
            if (cal.getTime().after(lastModified)) {
                checkForUpdates = true;
            }
        }
        return checkForUpdates;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder(64);
        buffer.append("{enabled=");
        buffer.append(this.enabled);
        buffer.append(", checksums=");
        buffer.append(this.checksumPolicy);
        buffer.append(", updates=");
        buffer.append(this.updatePolicy);
        buffer.append('}');
        return buffer.toString();
    }

    public void merge(ArtifactRepositoryPolicy policy) {
        if (policy != null && policy.isEnabled()) {
            this.setEnabled(true);
            if (this.ordinalOfCksumPolicy(policy.getChecksumPolicy()) < this.ordinalOfCksumPolicy(this.getChecksumPolicy())) {
                this.setChecksumPolicy(policy.getChecksumPolicy());
            }
            if (this.ordinalOfUpdatePolicy(policy.getUpdatePolicy()) < this.ordinalOfUpdatePolicy(this.getUpdatePolicy())) {
                this.setUpdatePolicy(policy.getUpdatePolicy());
            }
        }
    }

    private int ordinalOfCksumPolicy(String policy) {
        if (CHECKSUM_POLICY_FAIL.equals(policy)) {
            return 2;
        }
        if (CHECKSUM_POLICY_IGNORE.equals(policy)) {
            return 0;
        }
        return 1;
    }

    private int ordinalOfUpdatePolicy(String policy) {
        if (UPDATE_POLICY_DAILY.equals(policy)) {
            return 1440;
        }
        if (UPDATE_POLICY_ALWAYS.equals(policy)) {
            return 0;
        }
        if (policy != null && policy.startsWith(UPDATE_POLICY_INTERVAL)) {
            String s = policy.substring(UPDATE_POLICY_INTERVAL.length() + 1);
            return Integer.valueOf(s);
        }
        return Integer.MAX_VALUE;
    }
}
