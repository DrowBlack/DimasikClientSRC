package dimasik.managers.mods.voicechat.gui.onboarding;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.configbuilder.entry.ConfigEntry;
import dimasik.managers.mods.voicechat.gui.audiodevice.AudioDeviceList;
import dimasik.managers.mods.voicechat.gui.onboarding.OnboardingScreenBase;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public abstract class DeviceOnboardingScreen
extends OnboardingScreenBase {
    protected AudioDeviceList deviceList;
    protected List<String> micNames;

    public DeviceOnboardingScreen(ITextComponent title, @Nullable Screen previous) {
        super(title, previous);
        this.minecraft = Minecraft.getInstance();
        this.micNames = this.getNames();
        if (this.micNames.isEmpty()) {
            this.minecraft.displayGuiScreen(this.getNextScreen());
        }
    }

    public abstract List<String> getNames();

    public abstract ResourceLocation getIcon();

    public abstract ConfigEntry<String> getConfigEntry();

    @Override
    protected void init() {
        super.init();
        if (this.deviceList != null) {
            this.deviceList.updateSize(this.width, this.contentHeight - this.font.FONT_HEIGHT - 20 - 16, this.guiTop + this.font.FONT_HEIGHT + 8);
        } else {
            this.deviceList = new AudioDeviceList(this.width, this.contentHeight - this.font.FONT_HEIGHT - 20 - 16, this.guiTop + this.font.FONT_HEIGHT + 8).setIcon(this.getIcon()).setConfigEntry(this.getConfigEntry());
        }
        this.deviceList.setAudioDevices(this.getNames());
        this.addListener(this.deviceList);
        this.addBackOrCancelButton();
        this.addNextButton();
    }

    @Override
    public abstract Screen getNextScreen();

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);
        this.deviceList.render(stack, mouseX, mouseY, partialTicks);
        this.renderTitle(stack, this.title);
    }
}
