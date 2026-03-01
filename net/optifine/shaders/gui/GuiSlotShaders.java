package net.optifine.shaders.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import net.optifine.Config;
import net.optifine.Lang;
import net.optifine.gui.SlotGui;
import net.optifine.shaders.IShaderPack;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.gui.GuiShaders;
import net.optifine.util.ResUtils;

class GuiSlotShaders
extends SlotGui {
    private ArrayList shaderslist;
    private int selectedIndex;
    private long lastClicked = Long.MIN_VALUE;
    private long lastClickedCached = 0L;
    final GuiShaders shadersGui;

    public GuiSlotShaders(GuiShaders par1GuiShaders, int width, int height, int top, int bottom, int slotHeight) {
        super(par1GuiShaders.getMc(), width, height, top, bottom, slotHeight);
        this.shadersGui = par1GuiShaders;
        this.updateList();
        this.yo = 0.0;
        int i = this.selectedIndex * slotHeight;
        int j = (bottom - top) / 2;
        if (i > j) {
            this.scroll(i - j);
        }
    }

    @Override
    public int getRowWidth() {
        return this.width - 20;
    }

    public void updateList() {
        this.shaderslist = Shaders.listOfShaders();
        this.selectedIndex = 0;
        int j = this.shaderslist.size();
        for (int i = 0; i < j; ++i) {
            if (!((String)this.shaderslist.get(i)).equals(Shaders.currentShaderName)) continue;
            this.selectedIndex = i;
            break;
        }
    }

    @Override
    protected int getItemCount() {
        return this.shaderslist.size();
    }

    @Override
    protected boolean selectItem(int index, int buttons, double x, double y) {
        if (index == this.selectedIndex && this.lastClicked == this.lastClickedCached) {
            return false;
        }
        String s = (String)this.shaderslist.get(index);
        IShaderPack ishaderpack = Shaders.getShaderPack(s);
        if (!this.checkCompatible(ishaderpack, index)) {
            return false;
        }
        this.selectIndex(index);
        return true;
    }

    private void selectIndex(int index) {
        this.selectedIndex = index;
        this.lastClickedCached = this.lastClicked;
        Shaders.setShaderPack((String)this.shaderslist.get(index));
        Shaders.uninit();
        this.shadersGui.updateButtons();
    }

    private boolean checkCompatible(IShaderPack sp, int index) {
        if (sp == null) {
            return true;
        }
        InputStream inputstream = sp.getResourceAsStream("/shaders/shaders.properties");
        Properties properties = ResUtils.readProperties(inputstream, "Shaders");
        if (properties == null) {
            return true;
        }
        String s = "version.1.16.5";
        String s1 = properties.getProperty(s);
        if (s1 == null) {
            return true;
        }
        String s2 = "G8";
        int i = Config.compareRelease(s2, s1 = s1.trim());
        if (i >= 0) {
            return true;
        }
        String s3 = ("HD_U_" + s1).replace('_', ' ');
        String s4 = I18n.format("of.message.shaders.nv1", s3);
        String s5 = I18n.format("of.message.shaders.nv2", new Object[0]);
        BooleanConsumer booleanconsumer = result -> {
            if (result) {
                this.selectIndex(index);
            }
            this.minecraft.displayGuiScreen(this.shadersGui);
        };
        ConfirmScreen confirmscreen = new ConfirmScreen(booleanconsumer, new StringTextComponent(s4), new StringTextComponent(s5));
        this.minecraft.displayGuiScreen(confirmscreen);
        return false;
    }

    @Override
    protected boolean isSelectedItem(int index) {
        return index == this.selectedIndex;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.width - 6;
    }

    @Override
    public int getItemHeight() {
        return this.getItemCount() * 18;
    }

    @Override
    protected void renderBackground() {
    }

    @Override
    protected void renderItem(MatrixStack matrixStackIn, int index, int posX, int posY, int contentY, int mouseX, int mouseY, float partialTicks) {
        String s = (String)this.shaderslist.get(index);
        if (s.equals("OFF")) {
            s = Lang.get("of.options.shaders.packNone");
        } else if (s.equals("(internal)")) {
            s = Lang.get("of.options.shaders.packDefault");
        }
        this.shadersGui.drawCenteredString(matrixStackIn, s, this.width / 2, posY + 1, 0xE0E0E0);
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double amount) {
        return super.mouseScrolled(x, y, amount * 3.0);
    }
}
