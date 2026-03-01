package dimasik.itemics.api.command.datatypes;

import dimasik.itemics.api.IItemics;
import dimasik.itemics.api.command.argument.IArgConsumer;

public interface IDatatypeContext {
    public IItemics getItemics();

    public IArgConsumer getConsumer();
}
