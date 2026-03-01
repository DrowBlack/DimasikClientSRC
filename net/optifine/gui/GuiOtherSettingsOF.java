package net.optifine.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.FullscreenResolutionOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import net.optifine.gui.GuiButtonOF;
import net.optifine.gui.GuiScreenOF;
import net.optifine.gui.TooltipManager;
import net.optifine.gui.TooltipProviderOptions;

public class GuiOtherSettingsOF
extends GuiScreenOF {
    private Screen prevScreen;
    private GameSettings settings;
    private TooltipManager tooltipManager = new TooltipManager(this, new TooltipProviderOptions());

    public GuiOtherSettingsOF(Screen guiscreen, GameSettings gamesettings) {
        super(new StringTextComponent(I18n.format("of.options.otherTitle", new Object[0])));
        this.prevScreen = guiscreen;
        this.settings = gamesettings;
    }

    @Override
    public void init() {
        this.buttonList.clear();
        FullscreenResolutionOption abstractoption = new FullscreenResolutionOption(this.minecraft.getMainWindow());
        AbstractOption[] aabstractoption = new AbstractOption[]{AbstractOption.LAGOMETER, AbstractOption.PROFILER, AbstractOption.SHOW_FPS, AbstractOption.ADVANCED_TOOLTIPS, AbstractOption.WEATHER, AbstractOption.TIME, AbstractOption.FULLSCREEN, AbstractOption.AUTOSAVE_TICKS, AbstractOption.SCREENSHOT_SIZE, AbstractOption.SHOW_GL_ERRORS, abstractoption, null};
        for (int i = 0; i < aabstractoption.length; ++i) {
            AbstractOption abstractoption1 = aabstractoption[i];
            int j = this.width / 2 - 155 + i % 2 * 160;
            int k = this.height / 6 + 21 * (i / 2) - 12;
            Widget widget = this.addButton(abstractoption1.createWidget(this.minecraft.gameSettings, j, k, 150));
            if (abstractoption1 != abstractoption) continue;
            widget.setWidth(310);
            ++i;
        }
        this.addButton(new GuiButtonOF(210, this.width / 2 - 100, this.height / 6 + 168 + 11 - 44, I18n.format("of.options.other.reset", new Object[0])));
        this.addButton(new GuiButtonOF(200, this.width / 2 - 100, this.height / 6 + 168 + 11, I18n.format("gui.done", new Object[0])));
    }

    @Override
    protected void actionPerformed(Widget guiElement) {
        if (guiElement instanceof GuiButtonOF) {
            GuiButtonOF guibuttonof = (GuiButtonOF)guiElement;
            if (guibuttonof.active) {
                if (guibuttonof.id == 200) {
                    this.minecraft.gameSettings.saveOptions();
                    this.minecraft.getMainWindow().update();
                    this.minecraft.displayGuiScreen(this.prevScreen);
                }
                if (guibuttonof.id == 210) {
                    this.minecraft.gameSettings.saveOptions();
                    String s = I18n.format("of.message.other.reset", new Object[0]);
                    ConfirmScreen confirmscreen = new ConfirmScreen(this::confirmResult, new StringTextComponent(s), new StringTextComponent(""));
                    this.minecraft.displayGuiScreen(confirmscreen);
                }
            }
        }
    }

    @Override
    public void onClose() {
        this.minecraft.gameSettings.saveOptions();
        this.minecraft.getMainWindow().update();
        super.onClose();
    }

    public void confirmResult(boolean flag) {
        if (flag) {
            this.minecraft.gameSettings.resetSettings();
        }
        this.minecraft.displayGuiScreen(this);
    }

    @Override
    public void render(MatrixStack matrixStackIn, int x, int y, float partialTicks) {
        this.renderBackground(matrixStackIn);
        GuiOtherSettingsOF.drawCenteredString(matrixStackIn, this.fontRenderer, this.title, this.width / 2, 15, 0xFFFFFF);
        super.render(matrixStackIn, x, y, partialTicks);
        this.tooltipManager.drawTooltips(matrixStackIn, x, y, this.buttonList);
    }
}
