package org.openjdk.nashorn.internal.runtime;

import java.security.AccessControlContext;
import java.security.Permission;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.util.stream.Stream;

public final class AccessControlContextFactory {
    private AccessControlContextFactory() {
    }

    public static AccessControlContext createAccessControlContext() {
        return AccessControlContextFactory.createAccessControlContext(new Permission[0]);
    }

    public static AccessControlContext createAccessControlContext(Permission ... permissions) {
        Permissions perms = new Permissions();
        for (Permission permission : permissions) {
            perms.add(permission);
        }
        return new AccessControlContext(new ProtectionDomain[]{new ProtectionDomain(null, perms)});
    }

    public static AccessControlContext createAccessControlContext(String ... runtimePermissionNames) {
        return AccessControlContextFactory.createAccessControlContext(AccessControlContextFactory.makeRuntimePermissions(runtimePermissionNames));
    }

    private static Permission[] makeRuntimePermissions(String ... runtimePermissionNames) {
        return (Permission[])Stream.of(runtimePermissionNames).map(RuntimePermission::new).toArray(Permission[]::new);
    }
}
