package dimasik.managers.mods.voicechat.gui.tooltips;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.managers.mods.voicechat.VoicechatClient;
import dimasik.managers.mods.voicechat.gui.widgets.ImageButton;
import java.util.ArrayList;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.TranslationTextComponent;

public class HideTooltipSupplier
implements ImageButton.TooltipSupplier {
    public static final TranslationTextComponent HIDE_ICONS_ENABLED = new TranslationTextComponent("message.voicechat.hide_icons.enabled");
    public static final TranslationTextComponent HIDE_ICONS_DISABLED = new TranslationTextComponent("message.voicechat.hide_icons.disabled");
    private final Screen screen;

    public HideTooltipSupplier(Screen screen) {
        this.screen = screen;
    }

    @Override
    public void onTooltip(ImageButton button, MatrixStack matrices, int mouseX, int mouseY) {
        ArrayList<IReorderingProcessor> tooltip = new ArrayList<IReorderingProcessor>();
        if (VoicechatClient.CLIENT_CONFIG.hideIcons.get().booleanValue()) {
            tooltip.add(HIDE_ICONS_ENABLED.func_241878_f());
        } else {
            tooltip.add(HIDE_ICONS_DISABLED.func_241878_f());
        }
        this.screen.renderTooltip(matrices, tooltip, mouseX, mouseY);
    }
}
