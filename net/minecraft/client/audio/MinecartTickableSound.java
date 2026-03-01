package net.minecraft.client.audio;

import net.minecraft.client.audio.TickableSound;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;

public class MinecartTickableSound
extends TickableSound {
    private final AbstractMinecartEntity minecart;
    private float distance = 0.0f;

    public MinecartTickableSound(AbstractMinecartEntity minecartIn) {
        super(SoundEvents.ENTITY_MINECART_RIDING, SoundCategory.NEUTRAL);
        this.minecart = minecartIn;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.0f;
        this.x = (float)minecartIn.getPosX();
        this.y = (float)minecartIn.getPosY();
        this.z = (float)minecartIn.getPosZ();
    }

    @Override
    public boolean shouldPlaySound() {
        return !this.minecart.isSilent();
    }

    @Override
    public boolean canBeSilent() {
        return true;
    }

    @Override
    public void tick() {
        if (this.minecart.removed) {
            this.finishPlaying();
        } else {
            this.x = (float)this.minecart.getPosX();
            this.y = (float)this.minecart.getPosY();
            this.z = (float)this.minecart.getPosZ();
            float f = MathHelper.sqrt(Entity.horizontalMag(this.minecart.getMotion()));
            if ((double)f >= 0.01) {
                this.distance = MathHelper.clamp(this.distance + 0.0025f, 0.0f, 1.0f);
                this.volume = MathHelper.lerp(MathHelper.clamp(f, 0.0f, 0.5f), 0.0f, 0.7f);
            } else {
                this.distance = 0.0f;
                this.volume = 0.0f;
            }
        }
    }
}
