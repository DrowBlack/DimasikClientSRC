package dimasik.managers.mods.voicechat.gui.onboarding;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.managers.mods.voicechat.VoicechatClient;
import dimasik.managers.mods.voicechat.gui.VoiceChatScreen;
import dimasik.managers.mods.voicechat.gui.onboarding.OnboardingManager;
import dimasik.managers.mods.voicechat.gui.onboarding.OnboardingScreenBase;
import dimasik.managers.mods.voicechat.intercompatibility.ClientCompatibilityManager;
import dimasik.managers.mods.voicechat.voice.client.MicrophoneActivationType;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class FinalOnboardingScreen
extends OnboardingScreenBase {
    private static final ITextComponent TITLE = new TranslationTextComponent("message.voicechat.onboarding.final").mergeStyle(TextFormatting.BOLD);
    private static final ITextComponent FINISH_SETUP = new TranslationTextComponent("message.voicechat.onboarding.final.finish_setup");
    protected ITextComponent description = new StringTextComponent("");

    public FinalOnboardingScreen(@Nullable Screen previous) {
        super(TITLE, previous);
    }

    @Override
    protected void init() {
        super.init();
        IFormattableTextComponent text = new TranslationTextComponent("message.voicechat.onboarding.final.description.success", new StringTextComponent(this.minecraft.gameSettings.KEY_VOICE_CHAT.getTranslationKey()).mergeStyle(TextFormatting.BOLD, TextFormatting.UNDERLINE)).append(new StringTextComponent("\n\n"));
        text = VoicechatClient.CLIENT_CONFIG.microphoneActivationType.get().equals((Object)MicrophoneActivationType.PTT) ? text.append(new TranslationTextComponent("message.voicechat.onboarding.final.description.ptt", new StringTextComponent(this.minecraft.gameSettings.KEY_PTT.getTranslationKey()).mergeStyle(TextFormatting.BOLD, TextFormatting.UNDERLINE)).mergeStyle(TextFormatting.BOLD)).append(new StringTextComponent("\n\n")) : text.append(new TranslationTextComponent("message.voicechat.onboarding.final.description.voice", new StringTextComponent(this.minecraft.gameSettings.KEY_MUTE.getTranslationKey()).mergeStyle(TextFormatting.BOLD, TextFormatting.UNDERLINE)).mergeStyle(TextFormatting.BOLD)).append(new StringTextComponent("\n\n"));
        this.description = text.append(new TranslationTextComponent("message.voicechat.onboarding.final.description.configuration"));
        this.addBackOrCancelButton();
        this.addPositiveButton(FINISH_SETUP, button -> OnboardingManager.finishOnboarding());
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);
        this.renderTitle(stack, TITLE);
        this.renderMultilineText(stack, this.description);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            OnboardingManager.finishOnboarding();
            return true;
        }
        if (keyCode == ClientCompatibilityManager.INSTANCE.getBoundKeyOf(this.minecraft.gameSettings.KEY_VOICE_CHAT).getKeyCode()) {
            OnboardingManager.finishOnboarding();
            this.minecraft.displayGuiScreen(new VoiceChatScreen());
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
