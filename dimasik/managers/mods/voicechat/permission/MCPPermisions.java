package dimasik.managers.mods.voicechat.permission;

import dimasik.managers.mods.voicechat.permission.Permission;
import dimasik.managers.mods.voicechat.permission.PermissionManager;
import dimasik.managers.mods.voicechat.permission.PermissionType;
import net.minecraft.entity.player.ServerPlayerEntity;

public class MCPPermisions
extends PermissionManager {
    @Override
    public Permission createPermissionInternal(String modId, String node, final PermissionType type) {
        return new Permission(){

            @Override
            public boolean hasPermission(ServerPlayerEntity player) {
                return true;
            }

            @Override
            public PermissionType getPermissionType() {
                return type;
            }
        };
    }
}
