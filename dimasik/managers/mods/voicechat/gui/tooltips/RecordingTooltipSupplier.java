package dimasik.managers.mods.voicechat.gui.tooltips;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.managers.mods.voicechat.gui.widgets.ImageButton;
import dimasik.managers.mods.voicechat.voice.client.ClientManager;
import dimasik.managers.mods.voicechat.voice.client.ClientVoicechat;
import java.util.ArrayList;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.TranslationTextComponent;

public class RecordingTooltipSupplier
implements ImageButton.TooltipSupplier {
    public static final TranslationTextComponent RECORDING_ENABLED = new TranslationTextComponent("message.voicechat.recording.enabled");
    public static final TranslationTextComponent RECORDING_DISABLED = new TranslationTextComponent("message.voicechat.recording.disabled");
    private final Screen screen;

    public RecordingTooltipSupplier(Screen screen) {
        this.screen = screen;
    }

    @Override
    public void onTooltip(ImageButton button, MatrixStack matrices, int mouseX, int mouseY) {
        ClientVoicechat client = ClientManager.getClient();
        if (client == null) {
            return;
        }
        ArrayList<IReorderingProcessor> tooltip = new ArrayList<IReorderingProcessor>();
        if (client.getRecorder() == null) {
            tooltip.add(RECORDING_DISABLED.func_241878_f());
        } else {
            tooltip.add(RECORDING_ENABLED.func_241878_f());
        }
        this.screen.renderTooltip(matrices, tooltip, mouseX, mouseY);
    }
}
