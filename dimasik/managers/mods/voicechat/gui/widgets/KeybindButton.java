package dimasik.managers.mods.voicechat.gui.widgets;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class KeybindButton
extends AbstractButton {
    private static final Minecraft mc = Minecraft.getInstance();
    protected KeyBinding keyMapping;
    @Nullable
    protected ITextComponent description;
    protected boolean listening;

    public KeybindButton(KeyBinding mapping, int x, int y, int width, int height, @Nullable ITextComponent description) {
        super(x, y, width, height, StringTextComponent.EMPTY);
        this.keyMapping = mapping;
        this.description = description;
        this.updateText();
    }

    public KeybindButton(KeyBinding mapping, int x, int y, int width, int height) {
        this(mapping, x, y, width, height, null);
    }

    protected void updateText() {
        IFormattableTextComponent text = this.listening ? new StringTextComponent("> ").append(KeybindButton.getText(this.keyMapping).copyRaw().mergeStyle(TextFormatting.WHITE, TextFormatting.UNDERLINE)).append(new StringTextComponent(" <")).mergeStyle(TextFormatting.YELLOW) : KeybindButton.getText(this.keyMapping).copyRaw();
        if (this.description != null) {
            text = this.description.copyRaw().append(new StringTextComponent(": ")).append(text);
        }
        this.setMessage(text);
    }

    private static ITextComponent getText(KeyBinding keyMapping) {
        String translationKey = keyMapping.getTranslationKey();
        String[] parts = translationKey.split("\\.");
        String keyName = parts[parts.length - 1].toUpperCase();
        return new StringTextComponent(keyName);
    }

    @Override
    public boolean isHovered() {
        return this.isHovered;
    }

    @Override
    public void onPress() {
        this.listening = true;
        this.updateText();
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (this.listening) {
            KeybindButton.mc.gameSettings.setKeyBindingCode(this.keyMapping, InputMappings.Type.MOUSE.getOrMakeInput(button));
            this.listening = false;
            this.updateText();
            return true;
        }
        return super.mouseClicked(x, y, button);
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (this.listening) {
            if (key == 256) {
                KeybindButton.mc.gameSettings.setKeyBindingCode(this.keyMapping, InputMappings.INPUT_INVALID);
            } else {
                KeybindButton.mc.gameSettings.setKeyBindingCode(this.keyMapping, InputMappings.getInputByCode(key, scanCode));
            }
            this.listening = false;
            this.updateText();
            return true;
        }
        return super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int key, int scanCode, int modifiers) {
        if (this.listening && key == 256) {
            return true;
        }
        return super.keyReleased(key, scanCode, modifiers);
    }

    public boolean isListening() {
        return this.listening;
    }
}
