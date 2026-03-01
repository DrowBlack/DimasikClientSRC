package dimasik.managers.mods.voicechat.gui.tooltips;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.managers.mods.voicechat.VoicechatClient;
import dimasik.managers.mods.voicechat.gui.widgets.ImageButton;
import dimasik.managers.mods.voicechat.voice.client.ClientPlayerStateManager;
import dimasik.managers.mods.voicechat.voice.client.MicrophoneActivationType;
import java.util.ArrayList;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.TranslationTextComponent;

public class MuteTooltipSupplier
implements ImageButton.TooltipSupplier {
    public static final TranslationTextComponent MUTE_UNMUTED = new TranslationTextComponent("message.voicechat.mute.disabled");
    public static final TranslationTextComponent MUTE_MUTED = new TranslationTextComponent("message.voicechat.mute.enabled");
    public static final TranslationTextComponent MUTE_DISABLED_PTT = new TranslationTextComponent("message.voicechat.mute.disabled_ptt");
    private Screen screen;
    private ClientPlayerStateManager stateManager;

    public MuteTooltipSupplier(Screen screen, ClientPlayerStateManager stateManager) {
        this.screen = screen;
        this.stateManager = stateManager;
    }

    @Override
    public void onTooltip(ImageButton button, MatrixStack matrices, int mouseX, int mouseY) {
        ArrayList<IReorderingProcessor> tooltip = new ArrayList<IReorderingProcessor>();
        if (!MuteTooltipSupplier.canMuteMic()) {
            tooltip.add(MUTE_DISABLED_PTT.func_241878_f());
        } else if (this.stateManager.isMuted()) {
            tooltip.add(MUTE_MUTED.func_241878_f());
        } else {
            tooltip.add(MUTE_UNMUTED.func_241878_f());
        }
        this.screen.renderTooltip(matrices, tooltip, mouseX, mouseY);
    }

    public static boolean canMuteMic() {
        return VoicechatClient.CLIENT_CONFIG.microphoneActivationType.get().equals((Object)MicrophoneActivationType.VOICE);
    }
}
