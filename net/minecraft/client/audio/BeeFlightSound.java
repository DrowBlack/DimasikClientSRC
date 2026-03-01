package net.minecraft.client.audio;

import net.minecraft.client.audio.BeeAngrySound;
import net.minecraft.client.audio.BeeSound;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;

public class BeeFlightSound
extends BeeSound {
    public BeeFlightSound(BeeEntity entity) {
        super(entity, SoundEvents.ENTITY_BEE_LOOP, SoundCategory.NEUTRAL);
    }

    @Override
    protected TickableSound getNextSound() {
        return new BeeAngrySound(this.beeInstance);
    }

    @Override
    protected boolean shouldSwitchSound() {
        return this.beeInstance.func_233678_J__();
    }
}
