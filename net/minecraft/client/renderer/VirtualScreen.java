package net.minecraft.client.renderer;

import javax.annotation.Nullable;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Monitor;
import net.minecraft.client.renderer.MonitorHandler;
import net.minecraft.client.renderer.ScreenSize;

public final class VirtualScreen
implements AutoCloseable {
    private final Minecraft mc;
    private final MonitorHandler monitorHandler;

    public VirtualScreen(Minecraft mcIn) {
        this.mc = mcIn;
        this.monitorHandler = new MonitorHandler(Monitor::new);
    }

    public MainWindow create(ScreenSize screenSizeIn, @Nullable String videoModeName, String titleIn) {
        return new MainWindow(this.mc, this.monitorHandler, screenSizeIn, videoModeName, titleIn);
    }

    @Override
    public void close() {
        this.monitorHandler.close();
    }
}
