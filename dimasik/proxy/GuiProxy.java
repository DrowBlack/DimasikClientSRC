package dimasik.proxy;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.proxy.Proxy;
import dimasik.proxy.ProxyConfig;
import dimasik.proxy.ProxyServer;
import dimasik.proxy.TestPing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.StringUtils;

public class GuiProxy
extends Screen {
    private boolean isSocks4 = false;
    private TextFieldWidget ipPort;
    private TextFieldWidget username;
    private TextFieldWidget password;
    private CheckboxButton enabledCheck;
    private final Screen parentScreen;
    private String msg = "";
    private int[] positionY;
    private int positionX;
    private TestPing testPing = new TestPing();

    public GuiProxy(Screen parentScreen) {
        super(new StringTextComponent("Proxy"));
        this.parentScreen = parentScreen;
    }

    private boolean checkProxy() {
        if (!GuiProxy.isValidIpPort(this.ipPort.getText())) {
            this.msg = String.valueOf((Object)TextFormatting.RED) + "Invalid IP:PORT";
            this.ipPort.setFocused2(true);
            return false;
        }
        return true;
    }

    private static boolean isValidIpPort(String ipP) {
        String[] split = ipP.split(":");
        if (split.length > 1) {
            if (!StringUtils.isNumeric(split[1])) {
                return false;
            }
            int port = Integer.parseInt(split[1]);
            return port >= 0 && port <= 65535;
        }
        return false;
    }

    private void centerButtons(int amount, int buttonLength, int gap) {
        this.positionX = this.width / 2 - buttonLength / 2;
        this.positionY = new int[amount];
        int center = (this.height + amount * gap) / 2;
        int buttonStarts = center - amount * gap;
        for (int i = 0; i != amount; ++i) {
            this.positionY[i] = buttonStarts + gap * i;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        super.keyPressed(keyCode, scanCode, modifiers);
        this.msg = "";
        this.testPing.state = "";
        return true;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        if (this.enabledCheck.isChecked() && !GuiProxy.isValidIpPort(this.ipPort.getText())) {
            this.enabledCheck.onPress();
        }
        GuiProxy.drawStringWithShadow(matrixStack, this.font, "Proxy Type:", this.width / 2 - 149, this.positionY[1] + 5, 0xA0A0A0);
        GuiProxy.drawCenteredStringWithShadow(matrixStack, this.font, "Proxy Auth", this.width / 2, this.positionY[3] + 8, TextFormatting.WHITE.getColor());
        GuiProxy.drawStringWithShadow(matrixStack, this.font, "IP:PORT: ", this.width / 2 - 125, this.positionY[2] + 5, 0xA0A0A0);
        this.ipPort.render(matrixStack, mouseX, mouseY, partialTicks);
        if (this.isSocks4) {
            GuiProxy.drawStringWithShadow(matrixStack, this.font, "User ID: ", this.width / 2 - 140, this.positionY[4] + 5, 0xA0A0A0);
            this.username.render(matrixStack, mouseX, mouseY, partialTicks);
        } else {
            GuiProxy.drawStringWithShadow(matrixStack, this.font, "Username: ", this.width / 2 - 140, this.positionY[4] + 5, 0xA0A0A0);
            GuiProxy.drawStringWithShadow(matrixStack, this.font, "Password: ", this.width / 2 - 140, this.positionY[5] + 5, 0xA0A0A0);
            this.username.render(matrixStack, mouseX, mouseY, partialTicks);
            this.password.render(matrixStack, mouseX, mouseY, partialTicks);
        }
        GuiProxy.drawCenteredStringWithShadow(matrixStack, this.font, !this.msg.isEmpty() ? this.msg : this.testPing.state, this.width / 2, this.positionY[6] + 5, 0xA0A0A0);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void tick() {
        this.testPing.pingPendingNetworks();
        this.ipPort.tick();
        this.username.tick();
        this.password.tick();
    }

    @Override
    public void init() {
        Minecraft.getInstance().keyboardListener.enableRepeatEvents(true);
        int buttonLength = 160;
        this.centerButtons(10, buttonLength, 26);
        this.isSocks4 = ProxyServer.proxy.type == Proxy.ProxyType.SOCKS4;
        Button proxyType = new Button(this.positionX, this.positionY[1], buttonLength, 20, new StringTextComponent(this.isSocks4 ? "Socks 4" : "Socks 5"), button -> {
            this.isSocks4 = !this.isSocks4;
            button.setMessage(new StringTextComponent(this.isSocks4 ? "Socks 4" : "Socks 5"));
        });
        this.addButton(proxyType);
        this.ipPort = new TextFieldWidget(this.font, this.positionX, this.positionY[2], buttonLength, 20, new StringTextComponent(""));
        this.ipPort.setText(ProxyServer.proxy.ipPort);
        this.ipPort.setMaxStringLength(1024);
        this.ipPort.setFocused2(true);
        this.children.add(this.ipPort);
        this.username = new TextFieldWidget(this.font, this.positionX, this.positionY[4], buttonLength, 20, new StringTextComponent(""));
        this.username.setMaxStringLength(255);
        this.username.setText(ProxyServer.proxy.username);
        this.children.add(this.username);
        this.password = new TextFieldWidget(this.font, this.positionX, this.positionY[5], buttonLength, 20, new StringTextComponent(""));
        this.password.setMaxStringLength(255);
        this.password.setText(ProxyServer.proxy.password);
        this.children.add(this.password);
        int posXButtons = this.width / 2 - buttonLength / 2 * 3 / 2;
        Button apply = new Button(posXButtons, this.positionY[8], buttonLength / 2 - 3, 20, new StringTextComponent("Confirm"), button -> {
            if (this.checkProxy()) {
                ProxyServer.proxy = new Proxy(this.isSocks4, this.ipPort.getText(), this.username.getText(), this.password.getText());
                ProxyServer.proxyEnabled = this.enabledCheck.isChecked();
                ProxyConfig.setDefaultProxy(ProxyServer.proxy);
                ProxyConfig.saveConfig();
                Minecraft.getInstance().displayGuiScreen(new MultiplayerScreen(new MainMenuScreen()));
            }
        });
        this.addButton(apply);
        Button test = new Button(posXButtons + buttonLength / 2 + 3, this.positionY[8], buttonLength / 2 - 3, 20, new StringTextComponent("Ping"), button -> {
            if (this.ipPort.getText().isEmpty() || this.ipPort.getText().equalsIgnoreCase("none")) {
                this.msg = String.valueOf((Object)TextFormatting.RED) + "Specify proxy to test";
                return;
            }
            if (this.checkProxy()) {
                this.testPing = new TestPing();
                this.testPing.run("mc.bravohvh.space", 25565, new Proxy(this.isSocks4, this.ipPort.getText(), this.username.getText(), this.password.getText()));
            }
        });
        this.addButton(test);
        this.enabledCheck = new CheckboxButton(this.width / 2 - (15 + this.font.getStringWidth("Proxy Enabled")) / 2, this.positionY[7], buttonLength, 20, new StringTextComponent("Proxy Enabled"), ProxyServer.proxyEnabled);
        this.addButton(this.enabledCheck);
        Button cancel = new Button(posXButtons + (buttonLength / 2 + 3) * 2, this.positionY[8], buttonLength / 2 - 3, 20, new StringTextComponent("Cancel"), button -> Minecraft.getInstance().displayGuiScreen(this.parentScreen));
        this.addButton(cancel);
    }

    @Override
    public void onClose() {
        this.msg = "";
        Minecraft.getInstance().keyboardListener.enableRepeatEvents(false);
    }
}
