package dimasik.proxy;

import dimasik.proxy.Proxy;
import net.minecraft.client.gui.widget.button.Button;

public class ProxyServer {
    public static boolean proxyEnabled = false;
    public static Proxy proxy = new Proxy();
    public static Proxy lastUsedProxy = new Proxy();
    public static Button proxyMenuButton;

    public static String getLastUsedProxyIp() {
        return ProxyServer.lastUsedProxy.ipPort.isEmpty() ? "none" : lastUsedProxy.getIp();
    }
}
