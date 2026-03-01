package dimasik.managers.mods.voicechat.gui.onboarding;

import dimasik.managers.mods.voicechat.VoicechatClient;
import dimasik.managers.mods.voicechat.gui.onboarding.IntroductionOnboardingScreen;
import dimasik.managers.mods.voicechat.voice.client.ClientManager;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;

public class OnboardingManager {
    private static final Minecraft MC = Minecraft.getInstance();

    public static boolean isOnboarding() {
        return VoicechatClient.CLIENT_CONFIG.onboardingFinished.get() == false;
    }

    public static void startOnboarding(@Nullable Screen parent) {
        MC.displayGuiScreen(OnboardingManager.getOnboardingScreen(parent));
    }

    public static Screen getOnboardingScreen(@Nullable Screen parent) {
        return new IntroductionOnboardingScreen(parent);
    }

    public static void finishOnboarding() {
        VoicechatClient.CLIENT_CONFIG.muted.set(true).save();
        VoicechatClient.CLIENT_CONFIG.disabled.set(false).save();
        VoicechatClient.CLIENT_CONFIG.onboardingFinished.set(true).save();
        ClientManager.getPlayerStateManager().onFinishOnboarding();
        MC.displayGuiScreen(null);
    }

    public static void onConnecting() {
        if (!OnboardingManager.isOnboarding()) {
            return;
        }
        OnboardingManager.finishOnboarding();
    }
}
