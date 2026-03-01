package dimasik.managers.mods.voicechat.gui.tooltips;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.managers.mods.voicechat.VoicechatClient;
import dimasik.managers.mods.voicechat.gui.widgets.ImageButton;
import java.util.ArrayList;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.TranslationTextComponent;

public class HideGroupHudTooltipSupplier
implements ImageButton.TooltipSupplier {
    public static final TranslationTextComponent SHOW_GROUP_HUD_ENABLED = new TranslationTextComponent("message.voicechat.show_group_hud.enabled");
    public static final TranslationTextComponent SHOW_GROUP_HUD_DISABLED = new TranslationTextComponent("message.voicechat.show_group_hud.disabled");
    private final Screen screen;

    public HideGroupHudTooltipSupplier(Screen screen) {
        this.screen = screen;
    }

    @Override
    public void onTooltip(ImageButton button, MatrixStack matrices, int mouseX, int mouseY) {
        ArrayList<IReorderingProcessor> tooltip = new ArrayList<IReorderingProcessor>();
        if (VoicechatClient.CLIENT_CONFIG.showGroupHUD.get().booleanValue()) {
            tooltip.add(SHOW_GROUP_HUD_ENABLED.func_241878_f());
        } else {
            tooltip.add(SHOW_GROUP_HUD_DISABLED.func_241878_f());
        }
        this.screen.renderTooltip(matrices, tooltip, mouseX, mouseY);
    }
}
