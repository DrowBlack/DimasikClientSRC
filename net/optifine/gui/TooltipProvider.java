package net.optifine.gui;

import java.awt.Rectangle;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;

public interface TooltipProvider {
    public Rectangle getTooltipBounds(Screen var1, int var2, int var3);

    public String[] getTooltipLines(Widget var1, int var2);

    public boolean isRenderBorder();
}
