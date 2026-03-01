package dimasik.itemics.api.utils;

import dimasik.itemics.api.behavior.IBehavior;
import dimasik.itemics.api.utils.input.Input;

public interface IInputOverrideHandler
extends IBehavior {
    public boolean isInputForcedDown(Input var1);

    public void setInputForceState(Input var1, boolean var2);

    public void clearAllKeys();
}
