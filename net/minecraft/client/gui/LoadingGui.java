package net.minecraft.client.gui;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IRenderable;

public abstract class LoadingGui
extends AbstractGui
implements IRenderable {
    public boolean isPauseScreen() {
        return true;
    }
}
