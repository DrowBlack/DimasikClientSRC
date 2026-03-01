package dimasik.managers.mods.voicechat.permission;

import dimasik.managers.mods.voicechat.intercompatibility.CommonCompatibilityManager;
import dimasik.managers.mods.voicechat.permission.Permission;
import dimasik.managers.mods.voicechat.permission.PermissionType;
import java.util.ArrayList;
import java.util.List;

public abstract class PermissionManager {
    public static PermissionManager INSTANCE = CommonCompatibilityManager.INSTANCE.createPermissionManager();
    public final Permission LISTEN_PERMISSION;
    public final Permission SPEAK_PERMISSION;
    public final Permission GROUPS_PERMISSION;
    public final Permission ADMIN_PERMISSION;
    protected List<Permission> permissions = new ArrayList<Permission>();

    public PermissionManager() {
        this.LISTEN_PERMISSION = this.createPermission("voicechat", "listen", PermissionType.EVERYONE);
        this.SPEAK_PERMISSION = this.createPermission("voicechat", "speak", PermissionType.EVERYONE);
        this.GROUPS_PERMISSION = this.createPermission("voicechat", "groups", PermissionType.EVERYONE);
        this.ADMIN_PERMISSION = this.createPermission("voicechat", "admin", PermissionType.OPS);
    }

    public abstract Permission createPermissionInternal(String var1, String var2, PermissionType var3);

    public Permission createPermission(String modId, String node, PermissionType type) {
        Permission p = this.createPermissionInternal(modId, node, type);
        this.permissions.add(p);
        return p;
    }

    public List<Permission> getPermissions() {
        return this.permissions;
    }
}
