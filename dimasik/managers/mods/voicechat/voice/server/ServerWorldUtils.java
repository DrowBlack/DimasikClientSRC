package dimasik.managers.mods.voicechat.voice.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class ServerWorldUtils {
    public static Collection<ServerPlayerEntity> getPlayersInRange(ServerWorld level, Vector3d pos, double range, @Nullable Predicate<ServerPlayerEntity> filter) {
        ArrayList<ServerPlayerEntity> nearbyPlayers = new ArrayList<ServerPlayerEntity>();
        List<ServerPlayerEntity> players = level.getPlayers();
        for (int i = 0; i < players.size(); ++i) {
            ServerPlayerEntity player = players.get(i);
            if (!ServerWorldUtils.isInRange(player.getPositionVec(), pos, range) || filter != null && !filter.test(player)) continue;
            nearbyPlayers.add(player);
        }
        return nearbyPlayers;
    }

    public static boolean isInRange(Vector3d pos1, Vector3d pos2, double range) {
        return pos1.distanceTo(pos2) <= range * range;
    }
}
