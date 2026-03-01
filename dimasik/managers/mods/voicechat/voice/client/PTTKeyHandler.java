package dimasik.managers.mods.voicechat.voice.client;

import dimasik.managers.mods.voicechat.intercompatibility.ClientCompatibilityManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.InputMappings;

public class PTTKeyHandler {
    private static final PTTKeyHandler INSTANCE = new PTTKeyHandler();
    public boolean pttKeyDown;
    public boolean whisperKeyDown;

    private PTTKeyHandler() {
        ClientCompatibilityManager.INSTANCE.onKeyboardEvent(this::onKeyboardEvent);
        ClientCompatibilityManager.INSTANCE.onMouseEvent(this::onMouseEvent);
    }

    public static PTTKeyHandler getInstance() {
        return INSTANCE;
    }

    public void onKeyboardEvent(long window, int key, int scancode) {
        InputMappings.Input whisperKey;
        InputMappings.Input pttKey = ClientCompatibilityManager.INSTANCE.getBoundKeyOf(Minecraft.getInstance().gameSettings.KEY_PTT);
        if (pttKey.getKeyCode() != -1 && !pttKey.getType().equals((Object)InputMappings.Type.MOUSE)) {
            this.pttKeyDown = InputMappings.isKeyDown(window, pttKey.getKeyCode());
        }
        if ((whisperKey = ClientCompatibilityManager.INSTANCE.getBoundKeyOf(Minecraft.getInstance().gameSettings.KEY_WHISPER)).getKeyCode() != -1 && !whisperKey.getType().equals((Object)InputMappings.Type.MOUSE)) {
            this.whisperKeyDown = InputMappings.isKeyDown(window, whisperKey.getKeyCode());
        }
    }

    public void onMouseEvent(long window, int button, int action, int mods) {
        InputMappings.Input whisperKey;
        InputMappings.Input pttKey = ClientCompatibilityManager.INSTANCE.getBoundKeyOf(Minecraft.getInstance().gameSettings.KEY_PTT);
        if (pttKey.getKeyCode() != -1 && pttKey.getType().equals((Object)InputMappings.Type.MOUSE) && pttKey.getKeyCode() == button) {
            boolean bl = this.pttKeyDown = action != 0;
        }
        if ((whisperKey = ClientCompatibilityManager.INSTANCE.getBoundKeyOf(Minecraft.getInstance().gameSettings.KEY_WHISPER)).getKeyCode() != -1 && whisperKey.getType().equals((Object)InputMappings.Type.MOUSE) && whisperKey.getKeyCode() == button) {
            this.whisperKeyDown = action != 0;
        }
    }

    public boolean isPTTDown() {
        return this.pttKeyDown;
    }

    public boolean isWhisperDown() {
        return this.whisperKeyDown;
    }

    public boolean isAnyDown() {
        boolean result = this.pttKeyDown || this.whisperKeyDown;
        return result;
    }
}
