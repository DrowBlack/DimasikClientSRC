package dimasik.managers.mods.voicechat.gui.tooltips;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.managers.mods.voicechat.gui.widgets.ImageButton;
import dimasik.managers.mods.voicechat.voice.client.ClientPlayerStateManager;
import java.util.ArrayList;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.TranslationTextComponent;

public class DisableTooltipSupplier
implements ImageButton.TooltipSupplier {
    public static final TranslationTextComponent DISABLE_ENABLED = new TranslationTextComponent("message.voicechat.disable.enabled");
    public static final TranslationTextComponent DISABLE_DISABLED = new TranslationTextComponent("message.voicechat.disable.disabled");
    public static final TranslationTextComponent DISABLE_NO_SPEAKER = new TranslationTextComponent("message.voicechat.disable.no_speaker");
    private final Screen screen;
    private final ClientPlayerStateManager stateManager;

    public DisableTooltipSupplier(Screen screen, ClientPlayerStateManager stateManager) {
        this.screen = screen;
        this.stateManager = stateManager;
    }

    @Override
    public void onTooltip(ImageButton button, MatrixStack matrices, int mouseX, int mouseY) {
        ArrayList<IReorderingProcessor> tooltip = new ArrayList<IReorderingProcessor>();
        if (!this.stateManager.canEnable()) {
            tooltip.add(DISABLE_NO_SPEAKER.func_241878_f());
        } else if (this.stateManager.isDisabled()) {
            tooltip.add(DISABLE_ENABLED.func_241878_f());
        } else {
            tooltip.add(DISABLE_DISABLED.func_241878_f());
        }
        this.screen.renderTooltip(matrices, tooltip, mouseX, mouseY);
    }
}
