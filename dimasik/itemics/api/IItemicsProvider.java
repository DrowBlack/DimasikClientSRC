package dimasik.itemics.api;

import dimasik.itemics.api.IItemics;
import dimasik.itemics.api.cache.IWorldScanner;
import dimasik.itemics.api.command.ICommandSystem;
import dimasik.itemics.api.schematic.ISchematicSystem;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.entity.player.ClientPlayerEntity;

public interface IItemicsProvider {
    public IItemics getPrimaryItemics();

    public List<IItemics> getAllItemics();

    default public IItemics getItemicsForPlayer(ClientPlayerEntity player) {
        for (IItemics itemics : this.getAllItemics()) {
            if (!Objects.equals(player, itemics.getPlayerContext().player())) continue;
            return itemics;
        }
        return null;
    }

    public IWorldScanner getWorldScanner();

    public ICommandSystem getCommandSystem();

    public ISchematicSystem getSchematicSystem();
}
