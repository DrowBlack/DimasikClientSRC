package dimasik.itemics;

import dimasik.itemics.Itemics;
import dimasik.itemics.api.IItemics;
import dimasik.itemics.api.IItemicsProvider;
import dimasik.itemics.api.cache.IWorldScanner;
import dimasik.itemics.api.command.ICommandSystem;
import dimasik.itemics.api.schematic.ISchematicSystem;
import dimasik.itemics.cache.WorldScanner;
import dimasik.itemics.command.CommandSystem;
import dimasik.itemics.command.ExampleItemicsControl;
import dimasik.itemics.utils.schematic.SchematicSystem;
import java.util.Collections;
import java.util.List;

public final class ItemicsProvider
implements IItemicsProvider {
    private final Itemics primary = new Itemics();
    private final List<IItemics> all = Collections.singletonList(this.primary);

    public ItemicsProvider() {
        new ExampleItemicsControl(this.primary);
    }

    @Override
    public IItemics getPrimaryItemics() {
        return this.primary;
    }

    @Override
    public List<IItemics> getAllItemics() {
        return this.all;
    }

    @Override
    public IWorldScanner getWorldScanner() {
        return WorldScanner.INSTANCE;
    }

    @Override
    public ICommandSystem getCommandSystem() {
        return CommandSystem.INSTANCE;
    }

    @Override
    public ISchematicSystem getSchematicSystem() {
        return SchematicSystem.INSTANCE;
    }
}
