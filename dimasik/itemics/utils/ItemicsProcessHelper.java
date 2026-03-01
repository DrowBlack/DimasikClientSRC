package dimasik.itemics.utils;

import dimasik.itemics.Itemics;
import dimasik.itemics.api.process.IItemicsProcess;
import dimasik.itemics.api.utils.Helper;
import dimasik.itemics.api.utils.IPlayerContext;

public abstract class ItemicsProcessHelper
implements IItemicsProcess,
Helper {
    protected final Itemics itemics;
    protected final IPlayerContext ctx;

    public ItemicsProcessHelper(Itemics itemics) {
        this.itemics = itemics;
        this.ctx = itemics.getPlayerContext();
    }

    @Override
    public boolean isTemporary() {
        return false;
    }
}
