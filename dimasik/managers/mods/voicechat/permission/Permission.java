package dimasik.managers.mods.voicechat.permission;

import dimasik.managers.mods.voicechat.permission.PermissionType;
import net.minecraft.entity.player.ServerPlayerEntity;

public interface Permission {
    public boolean hasPermission(ServerPlayerEntity var1);

    public PermissionType getPermissionType();
}
