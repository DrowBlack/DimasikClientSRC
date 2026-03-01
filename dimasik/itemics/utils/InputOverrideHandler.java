package dimasik.itemics.utils;

import dimasik.itemics.Itemics;
import dimasik.itemics.api.ItemicsAPI;
import dimasik.itemics.api.event.events.TickEvent;
import dimasik.itemics.api.utils.IInputOverrideHandler;
import dimasik.itemics.api.utils.input.Input;
import dimasik.itemics.behavior.Behavior;
import dimasik.itemics.utils.BlockBreakHelper;
import dimasik.itemics.utils.BlockPlaceHelper;
import dimasik.itemics.utils.PlayerMovementInput;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MovementInputFromOptions;

public final class InputOverrideHandler
extends Behavior
implements IInputOverrideHandler {
    private final Map<Input, Boolean> inputForceStateMap = new HashMap<Input, Boolean>();
    private final BlockBreakHelper blockBreakHelper;
    private final BlockPlaceHelper blockPlaceHelper;

    public InputOverrideHandler(Itemics itemics) {
        super(itemics);
        this.blockBreakHelper = new BlockBreakHelper(itemics.getPlayerContext());
        this.blockPlaceHelper = new BlockPlaceHelper(itemics.getPlayerContext());
    }

    @Override
    public final boolean isInputForcedDown(Input input) {
        return input == null ? false : this.inputForceStateMap.getOrDefault((Object)input, false);
    }

    @Override
    public final void setInputForceState(Input input, boolean forced) {
        this.inputForceStateMap.put(input, forced);
    }

    @Override
    public final void clearAllKeys() {
        this.inputForceStateMap.clear();
    }

    @Override
    public final void onTick(TickEvent event) {
        if (event.getType() == TickEvent.Type.OUT) {
            return;
        }
        if (this.isInputForcedDown(Input.CLICK_LEFT)) {
            this.setInputForceState(Input.CLICK_RIGHT, false);
        }
        this.blockBreakHelper.tick(this.isInputForcedDown(Input.CLICK_LEFT));
        this.blockPlaceHelper.tick(this.isInputForcedDown(Input.CLICK_RIGHT));
        if (this.inControl()) {
            if (this.ctx.player().movementInput.getClass() != PlayerMovementInput.class) {
                this.ctx.player().movementInput = new PlayerMovementInput(this);
            }
        } else if (this.ctx.player().movementInput.getClass() == PlayerMovementInput.class) {
            this.ctx.player().movementInput = new MovementInputFromOptions(Minecraft.getInstance().gameSettings);
        }
    }

    private boolean inControl() {
        for (Input input : new Input[]{Input.MOVE_FORWARD, Input.MOVE_BACK, Input.MOVE_LEFT, Input.MOVE_RIGHT, Input.SNEAK}) {
            if (!this.isInputForcedDown(input)) continue;
            return true;
        }
        return this.itemics.getPathingBehavior().isPathing() || this.itemics != ItemicsAPI.getProvider().getPrimaryItemics();
    }

    public BlockBreakHelper getBlockBreakHelper() {
        return this.blockBreakHelper;
    }
}
