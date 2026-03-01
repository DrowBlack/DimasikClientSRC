package dimasik.itemics.api.behavior;

import dimasik.itemics.api.behavior.IBehavior;
import dimasik.itemics.api.utils.Rotation;

public interface ILookBehavior
extends IBehavior {
    public void updateTarget(Rotation var1, boolean var2);
}
