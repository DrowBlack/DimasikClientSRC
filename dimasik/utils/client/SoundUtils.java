package dimasik.utils.client;

import dimasik.Load;
import dimasik.helpers.interfaces.IFastAccess;
import dimasik.modules.misc.ClientSound;
import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import lombok.Generated;
import net.minecraft.client.audio.Sound;

public final class SoundUtils
implements IFastAccess {
    public static void playSound(String name) {
        ClientSound clientSounds = Load.getInstance().getHooks().getModuleManagers().getClientSound();
        boolean start = true;
        if (!ClientSound.getSettings().getSelected("Scrolling") && name.contains("scroll")) {
            start = false;
        } else if (!ClientSound.getSettings().getSelected("Module") && (name.contains("enable") || name.contains("disable"))) {
            start = false;
        } else if (!ClientSound.getSettings().getSelected("Notification") && name.contains("notification")) {
            start = false;
        }
        if (SoundUtils.mc.player != null && clientSounds.isToggled() && start) {
            try {
                InputStream audioSrc = Sound.class.getResourceAsStream("/assets/minecraft/main/sounds/" + name + ".wav");
                BufferedInputStream bufferedIn = new BufferedInputStream(audioSrc);
                Clip clip = AudioSystem.getClip();
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
                if (audioInputStream == null) {
                    return;
                }
                clip.open(audioInputStream);
                clip.start();
                FloatControl floatControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
                float min = floatControl.getMinimum();
                float max = floatControl.getMaximum();
                float volumeInDecibels = (float)((double)min * (1.0 - (double)((Float)clientSounds.volume.getValue()).floatValue() / 100.0) + (double)max * ((double)((Float)clientSounds.volume.getValue()).floatValue() / 100.0));
                floatControl.setValue(volumeInDecibels);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    @Generated
    private SoundUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
