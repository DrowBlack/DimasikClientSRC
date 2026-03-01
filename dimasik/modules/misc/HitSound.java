package dimasik.modules.misc;

import dimasik.events.api.EventListener;
import dimasik.events.main.misc.AttackEvent;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.SliderOption;
import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import net.minecraft.client.audio.Sound;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class HitSound
extends Module {
    private final SliderOption volume = new SliderOption("Volume", 70.0f, 0.0f, 100.0f).increment(1.0f);
    private final EventListener<AttackEvent> attack = this::attack;

    public HitSound() {
        super("HitSound", Category.MISC);
        this.settings(this.volume);
    }

    public void attack(AttackEvent e) {
        if (HitSound.mc.player == null || HitSound.mc.world == null) {
            return;
        }
        this.playHitSound(e.entity);
    }

    public void playHitSound(Entity target) {
        if (HitSound.mc.player == null || target == null) {
            return;
        }
        try {
            InputStream audioSrc = Sound.class.getResourceAsStream("/assets/minecraft/main/sounds/bubble.wav");
            if (audioSrc == null) {
                return;
            }
            BufferedInputStream bufferedIn = new BufferedInputStream(audioSrc);
            Clip clip = AudioSystem.getClip();
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
            clip.open(audioInputStream);
            try {
                Vector3d playerPos = HitSound.mc.player.getPositionVec();
                Vector3d targetPos = target.getPositionVec();
                Vector3d vec = targetPos.subtract(playerPos);
                double yawToTarget = Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90.0;
                double delta = MathHelper.wrapDegrees(yawToTarget - (double)HitSound.mc.player.rotationYaw);
                if (Math.abs(delta) > 180.0) {
                    delta -= Math.signum(delta) * 360.0;
                }
                FloatControl balance = (FloatControl)clip.getControl(FloatControl.Type.BALANCE);
                balance.setValue((float)delta / 180.0f);
            }
            catch (Exception playerPos) {
                // empty catch block
            }
            try {
                FloatControl floatControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
                float min = floatControl.getMinimum();
                float max = floatControl.getMaximum();
                float volumeInDecibels = (float)((double)min * (1.0 - (double)((Float)this.volume.getValue()).floatValue() / 100.0) + (double)max * ((double)((Float)this.volume.getValue()).floatValue() / 100.0));
                floatControl.setValue(volumeInDecibels);
            }
            catch (Exception exception) {
                // empty catch block
            }
            clip.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
