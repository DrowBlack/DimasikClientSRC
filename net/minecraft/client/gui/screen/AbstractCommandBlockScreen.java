package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.CommandSuggestionHelper;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public abstract class AbstractCommandBlockScreen
extends Screen {
    private static final ITextComponent field_243330_s = new TranslationTextComponent("advMode.setCommand");
    private static final ITextComponent field_243331_t = new TranslationTextComponent("advMode.command");
    private static final ITextComponent field_243332_u = new TranslationTextComponent("advMode.previousOutput");
    protected TextFieldWidget commandTextField;
    protected TextFieldWidget resultTextField;
    protected Button doneButton;
    protected Button cancelButton;
    protected Button trackOutputButton;
    protected boolean trackOutput;
    private CommandSuggestionHelper suggestionHelper;

    public AbstractCommandBlockScreen() {
        super(NarratorChatListener.EMPTY);
    }

    @Override
    public void tick() {
        this.commandTextField.tick();
    }

    abstract CommandBlockLogic getLogic();

    abstract int func_195236_i();

    @Override
    protected void init() {
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        this.doneButton = this.addButton(new Button(this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20, DialogTexts.GUI_DONE, p_214187_1_ -> this.func_195234_k()));
        this.cancelButton = this.addButton(new Button(this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20, DialogTexts.GUI_CANCEL, p_214186_1_ -> this.closeScreen()));
        this.trackOutputButton = this.addButton(new Button(this.width / 2 + 150 - 20, this.func_195236_i(), 20, 20, new StringTextComponent("O"), p_214184_1_ -> {
            CommandBlockLogic commandblocklogic;
            commandblocklogic.setTrackOutput(!(commandblocklogic = this.getLogic()).shouldTrackOutput());
            this.updateTrackOutput();
        }));
        this.commandTextField = new TextFieldWidget(this.font, this.width / 2 - 150, 50, 300, 20, (ITextComponent)new TranslationTextComponent("advMode.command")){

            @Override
            protected IFormattableTextComponent getNarrationMessage() {
                return super.getNarrationMessage().appendString(AbstractCommandBlockScreen.this.suggestionHelper.getSuggestionMessage());
            }
        };
        this.commandTextField.setMaxStringLength(32500);
        this.commandTextField.setResponder(this::func_214185_b);
        this.children.add(this.commandTextField);
        this.resultTextField = new TextFieldWidget(this.font, this.width / 2 - 150, this.func_195236_i(), 276, 20, new TranslationTextComponent("advMode.previousOutput"));
        this.resultTextField.setMaxStringLength(32500);
        this.resultTextField.setEnabled(false);
        this.resultTextField.setText("-");
        this.children.add(this.resultTextField);
        this.setFocusedDefault(this.commandTextField);
        this.commandTextField.setFocused2(true);
        this.suggestionHelper = new CommandSuggestionHelper(this.minecraft, this, this.commandTextField, this.font, true, true, 0, 7, false, Integer.MIN_VALUE);
        this.suggestionHelper.shouldAutoSuggest(true);
        this.suggestionHelper.init();
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String s = this.commandTextField.getText();
        this.init(minecraft, width, height);
        this.commandTextField.setText(s);
        this.suggestionHelper.init();
    }

    protected void updateTrackOutput() {
        if (this.getLogic().shouldTrackOutput()) {
            this.trackOutputButton.setMessage(new StringTextComponent("O"));
            this.resultTextField.setText(this.getLogic().getLastOutput().getString());
        } else {
            this.trackOutputButton.setMessage(new StringTextComponent("X"));
            this.resultTextField.setText("-");
        }
    }

    protected void func_195234_k() {
        CommandBlockLogic commandblocklogic = this.getLogic();
        this.func_195235_a(commandblocklogic);
        if (!commandblocklogic.shouldTrackOutput()) {
            commandblocklogic.setLastOutput(null);
        }
        this.minecraft.displayGuiScreen(null);
    }

    @Override
    public void onClose() {
        this.minecraft.keyboardListener.enableRepeatEvents(false);
    }

    protected abstract void func_195235_a(CommandBlockLogic var1);

    @Override
    public void closeScreen() {
        this.getLogic().setTrackOutput(this.trackOutput);
        this.minecraft.displayGuiScreen(null);
    }

    private void func_214185_b(String p_214185_1_) {
        this.suggestionHelper.init();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.suggestionHelper.onKeyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode != 257 && keyCode != 335) {
            return false;
        }
        this.func_195234_k();
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return this.suggestionHelper.onScroll(delta) ? true : super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.suggestionHelper.onClick(mouseX, mouseY, button) ? true : super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        AbstractCommandBlockScreen.drawCenteredString(matrixStack, this.font, field_243330_s, this.width / 2, 20, 0xFFFFFF);
        AbstractCommandBlockScreen.drawString(matrixStack, this.font, field_243331_t, this.width / 2 - 150, 40, 0xA0A0A0);
        this.commandTextField.render(matrixStack, mouseX, mouseY, partialTicks);
        int i = 75;
        if (!this.resultTextField.getText().isEmpty()) {
            AbstractCommandBlockScreen.drawString(matrixStack, this.font, field_243332_u, this.width / 2 - 150, (i += 46 + this.func_195236_i() - 135) + 4, 0xA0A0A0);
            this.resultTextField.render(matrixStack, mouseX, mouseY, partialTicks);
        }
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.suggestionHelper.drawSuggestionList(matrixStack, mouseX, mouseY);
    }
}
