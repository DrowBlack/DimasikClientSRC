package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.Load;
import dimasik.helpers.interfaces.IFastAccess;
import dimasik.helpers.render.GLHelpers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.CommandSuggestionHelper;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;

public class ChatScreen
extends Screen
implements IFastAccess {
    private String historyBuffer = "";
    private int sentHistoryCursor = -1;
    protected TextFieldWidget inputField;
    private String defaultInputFieldText = "";
    private CommandSuggestionHelper commandSuggestionHelper;

    public ChatScreen(String defaultText) {
        super(NarratorChatListener.EMPTY);
        this.defaultInputFieldText = defaultText;
    }

    @Override
    protected void init() {
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        this.sentHistoryCursor = this.minecraft.ingameGUI.getChatGUI().getSentMessages().size();
        this.inputField = new TextFieldWidget(this.font, 4, this.height - 12, this.width - 4, 12, (ITextComponent)new TranslationTextComponent("chat.editBox")){

            @Override
            protected IFormattableTextComponent getNarrationMessage() {
                return super.getNarrationMessage().appendString(ChatScreen.this.commandSuggestionHelper.getSuggestionMessage());
            }
        };
        this.inputField.setMaxStringLength(256);
        this.inputField.setEnableBackgroundDrawing(false);
        this.inputField.setText(this.defaultInputFieldText);
        this.inputField.setResponder(this::func_212997_a);
        this.children.add(this.inputField);
        this.commandSuggestionHelper = new CommandSuggestionHelper(this.minecraft, this, this.inputField, this.font, false, false, 1, 10, true, -805306368);
        this.commandSuggestionHelper.init();
        this.setFocusedDefault(this.inputField);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String s = this.inputField.getText();
        this.init(minecraft, width, height);
        this.setChatLine(s);
        this.commandSuggestionHelper.init();
    }

    @Override
    public void onClose() {
        Load.getInstance().getHooks().getDraggableController().release();
        this.minecraft.keyboardListener.enableRepeatEvents(false);
        this.minecraft.ingameGUI.getChatGUI().resetScroll();
    }

    @Override
    public void tick() {
        Load.getInstance().getHooks().getDraggableController().tick();
        this.inputField.tick();
    }

    private void func_212997_a(String p_212997_1_) {
        String s = this.inputField.getText();
        this.commandSuggestionHelper.shouldAutoSuggest(!s.equals(this.defaultInputFieldText));
        this.commandSuggestionHelper.init();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.commandSuggestionHelper.onKeyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == 256) {
            this.minecraft.displayGuiScreen(null);
            return true;
        }
        if (keyCode != 257 && keyCode != 335) {
            if (keyCode == 265) {
                this.getSentHistory(-1);
                return true;
            }
            if (keyCode == 264) {
                this.getSentHistory(1);
                return true;
            }
            if (keyCode == 266) {
                this.minecraft.ingameGUI.getChatGUI().addScrollPos(this.minecraft.ingameGUI.getChatGUI().getLineCount() - 1);
                return true;
            }
            if (keyCode == 267) {
                this.minecraft.ingameGUI.getChatGUI().addScrollPos(-this.minecraft.ingameGUI.getChatGUI().getLineCount() + 1);
                return true;
            }
            return false;
        }
        String s = this.inputField.getText().trim();
        if (!s.isEmpty()) {
            this.sendMessage(s);
        }
        this.minecraft.displayGuiScreen(null);
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (delta > 1.0) {
            delta = 1.0;
        }
        if (delta < -1.0) {
            delta = -1.0;
        }
        if (this.commandSuggestionHelper.onScroll(delta)) {
            return true;
        }
        if (!ChatScreen.hasShiftDown()) {
            delta *= 7.0;
        }
        this.minecraft.ingameGUI.getChatGUI().addScrollPos(delta);
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Vector2f vec2f = GLHelpers.INSTANCE.normalizeCords(mouseX, mouseY, 1.0);
        Load.getInstance().getHooks().getDraggableController().click((int)vec2f.x, (int)vec2f.y, button);
        if (this.commandSuggestionHelper.onClick((int)mouseX, (int)mouseY, button)) {
            return true;
        }
        if (button == 0) {
            NewChatGui newchatgui = this.minecraft.ingameGUI.getChatGUI();
            if (newchatgui.func_238491_a_(mouseX, mouseY)) {
                return true;
            }
            Style style = newchatgui.func_238494_b_(mouseX, mouseY);
            if (style != null && this.handleComponentClicked(style)) {
                return true;
            }
        }
        return this.inputField.mouseClicked(mouseX, mouseY, button) ? true : super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        Load.getInstance().getHooks().getDraggableController().release();
        Vector2f vec2f = GLHelpers.INSTANCE.normalizeCords(mouseX, mouseY, 1.0);
        Load.getInstance().getHooks().getDraggableController().release((int)vec2f.x, (int)vec2f.y, button);
        return false;
    }

    @Override
    protected void insertText(String text, boolean overwrite) {
        if (overwrite) {
            this.inputField.setText(text);
        } else {
            this.inputField.writeText(text);
        }
    }

    public void getSentHistory(int msgPos) {
        int i = this.sentHistoryCursor + msgPos;
        int j = this.minecraft.ingameGUI.getChatGUI().getSentMessages().size();
        if ((i = MathHelper.clamp(i, 0, j)) != this.sentHistoryCursor) {
            if (i == j) {
                this.sentHistoryCursor = j;
                this.inputField.setText(this.historyBuffer);
            } else {
                if (this.sentHistoryCursor == j) {
                    this.historyBuffer = this.inputField.getText();
                }
                this.inputField.setText(this.minecraft.ingameGUI.getChatGUI().getSentMessages().get(i));
                this.commandSuggestionHelper.shouldAutoSuggest(false);
                this.sentHistoryCursor = i;
            }
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Vector2f vec2f = GLHelpers.INSTANCE.normalizeCords(mouseX, mouseY, 1.0);
        Load.getInstance().getHooks().getDraggableController().render(matrixStack, (int)vec2f.x, (int)vec2f.y, partialTicks);
        this.setListener(this.inputField);
        this.inputField.setFocused2(true);
        ChatScreen.fill(matrixStack, 2, this.height - 14, this.width - 2, this.height - 2, this.minecraft.gameSettings.getChatBackgroundColor(Integer.MIN_VALUE));
        this.inputField.render(matrixStack, mouseX, mouseY, partialTicks);
        this.commandSuggestionHelper.drawSuggestionList(matrixStack, mouseX, mouseY);
        Style style = this.minecraft.ingameGUI.getChatGUI().func_238494_b_(mouseX, mouseY);
        if (style != null && style.getHoverEvent() != null) {
            this.renderComponentHoverEffect(matrixStack, style, mouseX, mouseY);
        }
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void setChatLine(String p_208604_1_) {
        this.inputField.setText(p_208604_1_);
    }
}
