package net.minecraft.client.audio;

import net.minecraft.client.audio.TickableSound;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

public class UnderwaterAmbientSounds {

    public static class UnderWaterSound
    extends TickableSound {
        private final ClientPlayerEntity player;
        private int ticksInWater;

        public UnderWaterSound(ClientPlayerEntity playerIn) {
            super(SoundEvents.AMBIENT_UNDERWATER_LOOP, SoundCategory.AMBIENT);
            this.player = playerIn;
            this.repeat = true;
            this.repeatDelay = 0;
            this.volume = 1.0f;
            this.priority = true;
            this.global = true;
        }

        @Override
        public void tick() {
            if (!this.player.removed && this.ticksInWater >= 0) {
                this.ticksInWater = this.player.canSwim() ? ++this.ticksInWater : (this.ticksInWater -= 2);
                this.ticksInWater = Math.min(this.ticksInWater, 40);
                this.volume = Math.max(0.0f, Math.min((float)this.ticksInWater / 40.0f, 1.0f));
            } else {
                this.finishPlaying();
            }
        }
    }

    public static class SubSound
    extends TickableSound {
        private final ClientPlayerEntity player;

        protected SubSound(ClientPlayerEntity playerIn, SoundEvent soundIn) {
            super(soundIn, SoundCategory.AMBIENT);
            this.player = playerIn;
            this.repeat = false;
            this.repeatDelay = 0;
            this.volume = 1.0f;
            this.priority = true;
            this.global = true;
        }

        @Override
        public void tick() {
            if (this.player.removed || !this.player.canSwim()) {
                this.finishPlaying();
            }
        }
    }
}
