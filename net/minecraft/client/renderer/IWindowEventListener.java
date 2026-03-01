package net.minecraft.client.renderer;

public interface IWindowEventListener {
    public void setGameFocused(boolean var1);

    public void updateWindowSize();

    public void ignoreFirstMove();
}
