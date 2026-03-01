package dimasik.itemics.command.defaults;

import dimasik.itemics.KeepName;
import dimasik.itemics.api.IItemics;
import dimasik.itemics.api.command.Command;
import dimasik.itemics.api.command.argument.IArgConsumer;
import dimasik.itemics.api.command.datatypes.EntityClassById;
import dimasik.itemics.api.command.datatypes.IDatatypeFor;
import dimasik.itemics.api.command.datatypes.NearbyPlayer;
import dimasik.itemics.api.command.exception.CommandErrorMessageException;
import dimasik.itemics.api.command.exception.CommandException;
import dimasik.itemics.api.command.helpers.TabCompleteHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class FollowCommand
extends Command {
    public FollowCommand(IItemics itemics) {
        super(itemics, "follow");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        FollowGroup group;
        args.requireMin(1);
        ArrayList<Entity> entities = new ArrayList<Entity>();
        ArrayList<EntityType> classes = new ArrayList<EntityType>();
        if (args.hasExactlyOne()) {
            group = args.getEnum(FollowGroup.class);
            this.itemics.getFollowProcess().follow(group.filter);
        } else {
            args.requireMin(2);
            group = null;
            FollowList list = args.getEnum(FollowList.class);
            while (args.hasAny()) {
                Object gotten = args.getDatatypeFor(list.datatype);
                if (gotten instanceof EntityType) {
                    classes.add((EntityType)gotten);
                    continue;
                }
                if (gotten == null) continue;
                entities.add((Entity)gotten);
            }
            this.itemics.getFollowProcess().follow(classes.isEmpty() ? entities::contains : e -> classes.stream().anyMatch(c -> e.getType().equals(c)));
        }
        if (group != null) {
            this.logDirect(String.format("Following all %s", group.name().toLowerCase(Locale.US)));
        } else if (classes.isEmpty()) {
            if (entities.isEmpty()) {
                throw new NoEntitiesException();
            }
            this.logDirect("Following these entities:");
            entities.stream().map(Entity::toString).forEach(this::logDirect);
        } else {
            this.logDirect("Following these types of entities:");
            classes.stream().map(Registry.ENTITY_TYPE::getKey).map(Objects::requireNonNull).map(ResourceLocation::toString).forEach(this::logDirect);
        }
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        IDatatypeFor followType;
        if (args.hasExactlyOne()) {
            return new TabCompleteHelper().append(FollowGroup.class).append(FollowList.class).filterPrefix(args.getString()).stream();
        }
        try {
            followType = args.getEnum(FollowList.class).datatype;
        }
        catch (NullPointerException e) {
            return Stream.empty();
        }
        while (args.has(2)) {
            if (args.peekDatatypeOrNull(followType) == null) {
                return Stream.empty();
            }
            args.get();
        }
        return args.tabCompleteDatatype(followType);
    }

    @Override
    public String getShortDesc() {
        return "Follow entity things";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("The follow command tells Itemics to follow certain kinds of entities.", "", "Usage:", "> follow entities - Follows all entities.", "> follow entity <entity1> <entity2> <...> - Follow certain entities (for example 'skeleton', 'horse' etc.)", "> follow players - Follow players", "> follow player <username1> <username2> <...> - Follow certain players");
    }

    @KeepName
    private static enum FollowGroup {
        ENTITIES(LivingEntity.class::isInstance),
        PLAYERS(PlayerEntity.class::isInstance);

        final Predicate<Entity> filter;

        private FollowGroup(Predicate<Entity> filter) {
            this.filter = filter;
        }
    }

    @KeepName
    private static enum FollowList {
        ENTITY(EntityClassById.INSTANCE),
        PLAYER(NearbyPlayer.INSTANCE);

        final IDatatypeFor datatype;

        private FollowList(IDatatypeFor datatype) {
            this.datatype = datatype;
        }
    }

    public static class NoEntitiesException
    extends CommandErrorMessageException {
        protected NoEntitiesException() {
            super("No valid entities in range!");
        }
    }
}
