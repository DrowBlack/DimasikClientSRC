package dimasik.itemics.api.command.datatypes;

import dimasik.itemics.api.command.datatypes.IDatatypeContext;
import dimasik.itemics.api.command.datatypes.IDatatypeFor;
import dimasik.itemics.api.command.exception.CommandException;
import dimasik.itemics.api.command.helpers.TabCompleteHelper;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;

public enum NearbyPlayer implements IDatatypeFor<PlayerEntity>
{
    INSTANCE;


    @Override
    public PlayerEntity get(IDatatypeContext ctx) throws CommandException {
        String username = ctx.getConsumer().getString();
        return NearbyPlayer.getPlayers(ctx).stream().filter(s -> s.getName().getString().equalsIgnoreCase(username)).findFirst().orElse(null);
    }

    @Override
    public Stream<String> tabComplete(IDatatypeContext ctx) throws CommandException {
        return new TabCompleteHelper().append(NearbyPlayer.getPlayers(ctx).stream().map(PlayerEntity::getName).map(ITextComponent::getString)).filterPrefix(ctx.getConsumer().getString()).sortAlphabetically().stream();
    }

    private static List<? extends PlayerEntity> getPlayers(IDatatypeContext ctx) {
        return ctx.getItemics().getPlayerContext().world().getPlayers();
    }
}
