package net.minecraft.util;

import dimasik.Load;
import dimasik.events.main.input.EventMoveInput;
import net.minecraft.client.GameSettings;
import net.minecraft.util.MovementInput;

public class MovementInputFromOptions
extends MovementInput {
    private final GameSettings gameSettings;

    public MovementInputFromOptions(GameSettings gameSettingsIn) {
        this.gameSettings = gameSettingsIn;
    }

    @Override
    public void tickMovement(boolean p_225607_1_) {
        this.forwardKeyDown = this.gameSettings.keyBindForward.isKeyDown();
        this.backKeyDown = this.gameSettings.keyBindBack.isKeyDown();
        this.leftKeyDown = this.gameSettings.keyBindLeft.isKeyDown();
        this.rightKeyDown = this.gameSettings.keyBindRight.isKeyDown();
        this.jump = this.gameSettings.keyBindJump.isKeyDown();
        this.sneaking = this.gameSettings.keyBindSneak.isKeyDown();
        EventMoveInput eventMoveInput = new EventMoveInput(this.forwardKeyDown == this.backKeyDown ? 0.0f : (this.forwardKeyDown ? 1.0f : -1.0f), this.leftKeyDown == this.rightKeyDown ? 0.0f : (this.leftKeyDown ? 1.0f : -1.0f), this.jump, this.sneaking);
        Load.getInstance().getEvents().call(eventMoveInput);
        this.moveForward = eventMoveInput.getForward();
        this.moveStrafe = eventMoveInput.getStrafe();
        if (p_225607_1_) {
            this.moveStrafe = (float)((double)this.moveStrafe * 0.3);
            this.moveForward = (float)((double)this.moveForward * 0.3);
        }
    }
}
