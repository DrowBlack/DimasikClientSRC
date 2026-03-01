package net.minecraft.client.gui;

public interface IProgressMeter {
    public static final String[] LOADING_STRINGS = new String[]{"oooooo", "Oooooo", "oOoooo", "ooOooo", "oooOoo", "ooooOo", "oooooO"};

    public void onStatsUpdated();
}
