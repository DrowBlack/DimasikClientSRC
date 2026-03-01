package dimasik.itemics.behavior;

import dimasik.itemics.Itemics;
import dimasik.itemics.api.behavior.IBehavior;
import dimasik.itemics.api.utils.IPlayerContext;

public class Behavior
implements IBehavior {
    public final Itemics itemics;
    public final IPlayerContext ctx;

    protected Behavior(Itemics itemics) {
        this.itemics = itemics;
        this.ctx = itemics.getPlayerContext();
        itemics.registerBehavior(this);
    }
}
