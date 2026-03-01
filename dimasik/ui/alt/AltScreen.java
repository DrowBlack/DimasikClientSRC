package dimasik.ui.alt;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.helpers.animation.Animation;
import dimasik.helpers.animation.EasingList;
import dimasik.helpers.interfaces.IFastAccess;
import dimasik.helpers.render.ColorHelpers;
import dimasik.helpers.render.GLHelpers;
import dimasik.helpers.render.ScreenHelpers;
import dimasik.helpers.visual.StencilHelpers;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.ui.alt.Account;
import dimasik.utils.client.StringUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Generated;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.text.StringTextComponent;

public class AltScreen
extends Screen
implements IFastAccess {
    private String username = "";
    private String password = "";
    private boolean usernameTyping;
    private boolean passwordTyping;
    private boolean hoveredBack;
    private boolean hoveredCreate;
    private float scrolling;
    private float scrollingOut;
    private final ArrayList<Account> accounts = new ArrayList();
    private final Animation usernameAnimation = new Animation();
    private final Animation passwordAnimation = new Animation();
    private final Animation buttonCreate = new Animation();
    private final Animation buttonBack = new Animation();

    public AltScreen() {
        super(new StringTextComponent("AltScreen"));
    }

    @Override
    public void render(@NonNull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (matrixStack == null) {
            throw new NullPointerException("matrixStack is marked non-null but is null");
        }
        this.scrollingOut = Animation.animate(this.scrollingOut, this.scrolling);
        this.renderBackground(matrixStack);
        this.usernameAnimation.animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, partialTicks);
        this.passwordAnimation.animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, partialTicks);
        this.buttonBack.animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, partialTicks);
        this.buttonCreate.animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, partialTicks);
        Vector2f fixedCoords = GLHelpers.INSTANCE.normalizeCords(mouseX, mouseY, 1.0);
        mouseX = (int)fixedCoords.x;
        mouseY = (int)fixedCoords.y;
        GLHelpers.INSTANCE.rescale(1.0);
        int back = ColorHelpers.rgba(190, 190, 190, 15.299999999999999);
        int outline = ColorHelpers.rgba(190, 190, 190, 15.299999999999999);
        VisualHelpers.drawImage(matrixStack, new ResourceLocation("main/textures/images/alt.png"), 0.0f, 0.0f, (float)mc.getMainWindow().getWidth(), (float)mc.getMainWindow().getHeight());
        float widthInput = 408.0f;
        float heightInput = 164.0f;
        float widthMenu = Math.min(1046.0f, (float)mc.getMainWindow().getWidth() / 2.0f + widthInput / 2.0f);
        float xInput = Math.max((float)mc.getMainWindow().getWidth() / 2.0f - widthInput / 2.0f - widthMenu / 2.0f - 27.0f, 0.0f);
        float yInput = (float)mc.getMainWindow().getHeight() / 2.0f - 110.0f;
        BLUR_RUNNABLES.add(() -> VisualHelpers.drawRoundedRect(matrixStack, xInput, yInput, widthInput, heightInput, 12.0f, -1));
        this.blurSetting(partialTicks, 12.0f, 1.0f);
        VisualHelpers.drawRoundedRect(matrixStack, xInput, yInput, widthInput, heightInput, 12.0f, back);
        VisualHelpers.drawRoundedOutline(matrixStack, xInput, yInput, widthInput, heightInput, 12.0f, 1.0f, outline);
        suisse_intl.drawCenteredText(matrixStack, "Hey, Let's create an account!", xInput + widthInput / 2.0f, yInput + 32.0f - suisse_intl.getHeight(14.0f) / 2.0f, ColorHelpers.rgba(255, 255, 255, 255), 14.0f);
        int userValue = ColorHelpers.interpolateColor(ColorHelpers.rgba(190, 190, 190, 10.200000000000001), ColorHelpers.rgba(48, 207, 151, 15.299999999999999), this.usernameAnimation.getAnimationValue());
        int userValueOutline = ColorHelpers.interpolateColor(ColorHelpers.rgba(190, 190, 190, 15.299999999999999), ColorHelpers.rgba(48, 207, 151, 30.599999999999998), this.usernameAnimation.getAnimationValue());
        int userText = ColorHelpers.rgba(255, 255, 255, 122.39999999999999 + 132.6 * (double)this.usernameAnimation.getAnimationValue());
        int userImage = ColorHelpers.interpolateColor(ColorHelpers.rgba(255, 255, 255, 61.199999999999996), ColorHelpers.rgba(48, 207, 151, 255), this.usernameAnimation.getAnimationValue());
        float widthVal = 384.0f;
        float heightVal = 40.0f;
        float xVal = xInput + (widthInput - widthVal) / 2.0f;
        float yVal = yInput + 64.0f;
        float offset = 0.0f;
        VisualHelpers.drawRoundedRect(matrixStack, xVal, yVal + offset, widthVal, heightVal, 6.0f, userValue);
        VisualHelpers.drawRoundedOutline(matrixStack, xVal, yVal + offset, widthVal, heightVal, 6.0f, 1.0f, userValueOutline);
        VisualHelpers.drawImage(matrixStack, new ResourceLocation("main/textures/images/alt/username.png"), xVal + 14.0f, yVal + offset + heightVal / 2.0f - 7.0f, 14.0f, 14.0f, userImage);
        suisse_intl.drawText(matrixStack, this.usernameTyping || !this.username.isEmpty() ? this.username : "Username", xVal + 35.0f, yVal + offset + heightVal / 2.0f - suisse_intl.getHeight(14.0f) / 2.0f, userText, 14.0f);
        int passValue = ColorHelpers.interpolateColor(ColorHelpers.rgba(190, 190, 190, 10.200000000000001), ColorHelpers.rgba(48, 207, 151, 15.299999999999999), this.passwordAnimation.getAnimationValue());
        int passValueOutline = ColorHelpers.interpolateColor(ColorHelpers.rgba(190, 190, 190, 15.299999999999999), ColorHelpers.rgba(48, 207, 151, 30.599999999999998), this.passwordAnimation.getAnimationValue());
        int passText = ColorHelpers.rgba(255, 255, 255, 122.39999999999999 + 132.6 * (double)this.passwordAnimation.getAnimationValue());
        int passImage = ColorHelpers.interpolateColor(ColorHelpers.rgba(255, 255, 255, 61.199999999999996), ColorHelpers.rgba(48, 207, 151, 255), this.passwordAnimation.getAnimationValue());
        VisualHelpers.drawRoundedRect(matrixStack, xVal, yVal + (offset += heightVal + 8.0f), widthVal, heightVal, 6.0f, passValue);
        VisualHelpers.drawRoundedOutline(matrixStack, xVal, yVal + offset, widthVal, heightVal, 6.0f, 1.0f, passValueOutline);
        VisualHelpers.drawImage(matrixStack, new ResourceLocation("main/textures/images/alt/password.png"), xVal + 14.0f, yVal + offset + heightVal / 2.0f - 7.0f, 14.0f, 14.0f, passImage);
        suisse_intl.drawText(matrixStack, this.passwordTyping || !this.password.isEmpty() ? this.password : "Password", xVal + 35.0f, yVal + offset + heightVal / 2.0f - suisse_intl.getHeight(14.0f) / 2.0f, passText, 14.0f);
        int buttonBack = ColorHelpers.interpolateColor(ColorHelpers.rgba(190, 190, 190, 10.200000000000001), ColorHelpers.rgba(48, 207, 151, 15.299999999999999), this.buttonBack.getAnimationValue());
        int buttonOutline = ColorHelpers.interpolateColor(ColorHelpers.rgba(190, 190, 190, 15.299999999999999), ColorHelpers.rgba(48, 207, 151, 30.599999999999998), this.buttonBack.getAnimationValue());
        int backText = ColorHelpers.rgba(255, 255, 255, 122.39999999999999 + 132.6 * (double)this.buttonBack.getAnimationValue());
        int backImage = ColorHelpers.interpolateColor(ColorHelpers.rgba(255, 255, 255, 61.199999999999996), ColorHelpers.rgba(48, 207, 151, 255), this.buttonBack.getAnimationValue());
        float buttonBackWidth = 103.0f;
        float buttonBackHeight = 48.0f;
        this.hoveredBack = ScreenHelpers.isHovered(mouseX, mouseY, xInput, yInput + heightInput + 8.0f, buttonBackWidth, buttonBackHeight);
        BLUR_RUNNABLES.add(() -> VisualHelpers.drawRoundedRect(matrixStack, xInput, yInput + heightInput + 8.0f, buttonBackWidth, buttonBackHeight, 12.0f, -1));
        this.blurSetting(partialTicks, 12.0f, 1.0f);
        VisualHelpers.drawRoundedRect(matrixStack, xInput, yInput + heightInput + 8.0f, buttonBackWidth, buttonBackHeight, 12.0f, buttonBack);
        VisualHelpers.drawRoundedOutline(matrixStack, xInput, yInput + heightInput + 8.0f, buttonBackWidth, buttonBackHeight, 12.0f, 1.0f, buttonOutline);
        VisualHelpers.drawImage(matrixStack, new ResourceLocation("main/textures/images/alt/back.png"), xInput + buttonBackWidth / 2.0f - suisse_intl.getWidth("Back", 14.0f) / 2.0f - 11.0f, yInput + heightInput + 8.0f + buttonBackHeight / 2.0f - 7.0f, 14.0f, 14.0f, backImage);
        suisse_intl.drawCenteredText(matrixStack, "Back", xInput + buttonBackWidth / 2.0f + 11.0f, yInput + heightInput + 8.0f + buttonBackHeight / 2.0f - suisse_intl.getHeight(14.0f) / 2.0f, backText, 14.0f);
        int buttonCreateBack = ColorHelpers.interpolateColor(ColorHelpers.rgba(190, 190, 190, 10.200000000000001), ColorHelpers.rgba(48, 207, 151, 15.299999999999999), this.buttonCreate.getAnimationValue());
        int buttonCreateOutline = ColorHelpers.interpolateColor(ColorHelpers.rgba(190, 190, 190, 15.299999999999999), ColorHelpers.rgba(48, 207, 151, 30.599999999999998), this.buttonCreate.getAnimationValue());
        int createText = ColorHelpers.rgba(255, 255, 255, 122.39999999999999 + 132.6 * (double)this.buttonCreate.getAnimationValue());
        int createImage = ColorHelpers.interpolateColor(ColorHelpers.rgba(255, 255, 255, 61.199999999999996), ColorHelpers.rgba(48, 207, 151, 255), this.buttonCreate.getAnimationValue());
        float buttonCreateWidth = 297.0f;
        float buttonCreateHeight = 48.0f;
        this.hoveredCreate = ScreenHelpers.isHovered(mouseX, mouseY, xInput + 111.0f, yInput + heightInput + 8.0f, buttonCreateWidth, buttonCreateHeight);
        BLUR_RUNNABLES.add(() -> VisualHelpers.drawRoundedRect(matrixStack, xInput + 111.0f, yInput + heightInput + 8.0f, buttonCreateWidth, buttonCreateHeight, 12.0f, -1));
        this.blurSetting(partialTicks, 12.0f, 1.0f);
        VisualHelpers.drawRoundedRect(matrixStack, xInput + 111.0f, yInput + heightInput + 8.0f, buttonCreateWidth, buttonCreateHeight, 12.0f, buttonCreateBack);
        VisualHelpers.drawRoundedOutline(matrixStack, xInput + 111.0f, yInput + heightInput + 8.0f, buttonCreateWidth, buttonCreateHeight, 12.0f, 1.0f, buttonCreateOutline);
        VisualHelpers.drawImage(matrixStack, new ResourceLocation("main/textures/images/alt/create.png"), xInput + buttonCreateWidth / 2.0f - suisse_intl.getWidth("Create", 14.0f) / 2.0f + 100.0f, yInput + heightInput + 8.0f + buttonBackHeight / 2.0f - 7.0f, 14.0f, 14.0f, createImage);
        suisse_intl.drawCenteredText(matrixStack, "Create", xInput + buttonCreateWidth / 2.0f + 122.0f, yInput + heightInput + 8.0f + buttonCreateHeight / 2.0f - suisse_intl.getHeight(14.0f) / 2.0f, createText, 14.0f);
        float heightMenu = 650.0f;
        float xMenu = xInput + widthMenu / 2.0f + 18.0f;
        float yMenu = (float)mc.getMainWindow().getHeight() / 2.0f - heightMenu / 2.0f;
        int menuBack = ColorHelpers.rgba(190, 190, 190, 10.200000000000001);
        int menuOutline = ColorHelpers.rgba(190, 190, 190, 15.299999999999999);
        BLUR_RUNNABLES.add(() -> VisualHelpers.drawRoundedRect(matrixStack, xMenu, yMenu, widthMenu, heightMenu, 12.0f, -1));
        VisualHelpers.drawRoundedRect(matrixStack, xMenu, yMenu, widthMenu, heightMenu, 12.0f, menuBack);
        VisualHelpers.drawRoundedOutline(matrixStack, xMenu, yMenu, widthMenu, heightMenu, 12.0f, 1.0f, menuOutline);
        float scrollOffset = this.scrollingOut;
        float accWidth = (widthMenu - 48.0f) / 3.0f;
        float accHeight = 64.0f;
        float accY = yMenu + 12.0f + scrollOffset;
        float accX = xMenu + 12.0f;
        float accOffset = 0.0f;
        int i = 0;
        StencilHelpers.init();
        VisualHelpers.drawRoundedRect(matrixStack, xMenu, yMenu, widthMenu, heightMenu, 12.0f, -1);
        StencilHelpers.read(1);
        for (Account account : this.accounts) {
            account.getAnimation().animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, partialTicks);
            int accBack = ColorHelpers.interpolateColor(ColorHelpers.rgba(190, 190, 190, 10.200000000000001), ColorHelpers.rgba(48, 207, 151, 15.299999999999999), account.getAnimation().getAnimationValue());
            int accOutline = ColorHelpers.interpolateColor(ColorHelpers.rgba(190, 190, 190, 15.299999999999999), ColorHelpers.rgba(48, 207, 151, 30.599999999999998), account.getAnimation().getAnimationValue());
            int accText = ColorHelpers.rgba(255, 255, 255, 183.6 + 71.4 * (double)account.getAnimation().getAnimationValue());
            int dateText = ColorHelpers.rgba(255, 255, 255, 132.6 + 71.4 * (double)account.getAnimation().getAnimationValue());
            VisualHelpers.drawRoundedRect(matrixStack, accX, accY + accOffset, accWidth, accHeight, 6.0f, accBack);
            VisualHelpers.drawRoundedOutline(matrixStack, accX, accY + accOffset, accWidth, accHeight, 6.0f, 1.0f, accOutline);
            VisualHelpers.drawRoundedHead(matrixStack, accX + 8.0f, accY + accOffset + 8.0f, 48.0f, 48.0f, 4.0f, 1.0f, account.skin, 0.0f);
            suisse_intl.drawText(matrixStack, StringUtils.trim(account.accountName, accWidth, suisse_intl, 14.0f), accX + 64.0f, accY + accOffset + 9.0f, accText, 14.0f);
            Date date = new Date(account.dateAdded);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            String formattedDate = dateFormat.format(date);
            String formattedTime = timeFormat.format(date);
            suisse_intl.drawText(matrixStack, formattedTime, accX + 64.0f, accY + accOffset + 9.0f + suisse_intl.getHeight(14.0f), dateText, 14.0f);
            suisse_intl.drawText(matrixStack, formattedDate, accX + 64.0f, accY + accOffset + 9.0f + suisse_intl.getHeight(14.0f) * 2.0f, dateText, 14.0f);
            if (i >= 2) {
                accX = xMenu + 12.0f;
                accOffset += accHeight + 12.0f;
                i = 0;
                continue;
            }
            accX += accWidth + 12.0f;
            ++i;
        }
        StencilHelpers.uninit();
        this.scrolling = accOffset < accHeight - 12.0f ? 0.0f : MathHelper.clamp(this.scrolling, -(accOffset - accHeight - 12.0f), 0.0f);
        GLHelpers.INSTANCE.rescaleMC();
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 259) {
            if (!this.username.isEmpty() && this.usernameTyping) {
                this.username = this.username.substring(0, this.username.length() - 1);
            }
            if (!this.password.isEmpty() && this.passwordTyping) {
                this.password = this.password.substring(0, this.password.length() - 1);
            }
        }
        if (keyCode == 261) {
            if (this.usernameTyping) {
                this.username = "";
            }
            if (this.passwordTyping) {
                this.password = "";
            }
        }
        if (keyCode == 257) {
            if (!this.username.isEmpty()) {
                this.accounts.add(new Account(this.username));
                AltScreen.mc.session = new Session(this.username, "", "", "mojang");
                this.username = "";
            }
            this.usernameTyping = false;
        }
        if (this.usernameTyping && Screen.isPaste(keyCode)) {
            this.username = this.username + Minecraft.getInstance().keyboardListener.getClipboardString();
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.usernameTyping && this.username.length() <= 20) {
            this.username = this.username + Character.toString(codePoint);
        }
        if (this.passwordTyping && this.password.length() <= 20) {
            this.password = this.password + Character.toString(codePoint);
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float buttonCreateHeight;
        float buttonCreateWidth;
        float buttonBackHeight;
        float buttonBackWidth;
        float heightVal;
        float offset;
        float yInput;
        float yVal;
        float widthVal;
        Vector2f fixedMouse = GLHelpers.INSTANCE.normalizeCords(mouseX, mouseY, 1.0);
        float widthInput = 408.0f;
        float heightInput = 164.0f;
        float widthMenu = Math.min(1046.0f, (float)mc.getMainWindow().getWidth() / 2.0f + widthInput / 2.0f);
        float xInput = Math.max((float)mc.getMainWindow().getWidth() / 2.0f - widthInput / 2.0f - widthMenu / 2.0f - 27.0f, 0.0f);
        float xVal = xInput + (widthInput - (widthVal = 384.0f)) / 2.0f;
        if (ScreenHelpers.isHovered(fixedMouse.x, fixedMouse.y, xVal, (yVal = (yInput = (float)mc.getMainWindow().getHeight() / 2.0f - 110.0f) + 64.0f) + (offset = 0.0f), widthVal, heightVal = 40.0f)) {
            this.usernameTyping = !this.usernameTyping;
            this.passwordTyping = false;
        }
        if (ScreenHelpers.isHovered(fixedMouse.x, fixedMouse.y, xVal, yVal + (offset += heightVal + 8.0f), widthVal, heightVal)) {
            this.passwordTyping = !this.passwordTyping;
            this.usernameTyping = false;
        }
        if (!ScreenHelpers.isHovered(fixedMouse.x, fixedMouse.y, xVal, yVal, widthVal, heightVal) && !ScreenHelpers.isHovered(fixedMouse.x, fixedMouse.y, xVal, yVal + offset, widthVal, heightVal)) {
            this.usernameTyping = false;
            this.passwordTyping = false;
        }
        float heightMenu = 650.0f;
        float xMenu = xInput + widthMenu / 2.0f + 18.0f;
        float yMenu = (float)mc.getMainWindow().getHeight() / 2.0f - heightMenu / 2.0f;
        int menuBack = ColorHelpers.rgba(190, 190, 190, 10.200000000000001);
        int menuOutline = ColorHelpers.rgba(190, 190, 190, 15.299999999999999);
        float scrollOffset = this.scrollingOut;
        float accWidth = (widthMenu - 48.0f) / 3.0f;
        float accHeight = 64.0f;
        float accY = yMenu + 12.0f + scrollOffset;
        float accX = xMenu + 12.0f;
        int i = 0;
        try {
            for (Account account : this.accounts) {
                if (ScreenHelpers.isHovered(fixedMouse.x, fixedMouse.y, accX, accY, accWidth, accHeight)) {
                    if (button == 0) {
                        AltScreen.mc.session = new Session(account.accountName, "", "", "mojang");
                        System.out.println(account.accountName);
                    }
                    if (button == 1) {
                        this.accounts.remove(account);
                    }
                }
                if (i >= 2) {
                    accX = xMenu + 12.0f;
                    accY += accHeight + 12.0f;
                    i = 0;
                    continue;
                }
                accX += accWidth + 12.0f;
                ++i;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (ScreenHelpers.isHovered(fixedMouse.x, fixedMouse.y, xInput, yInput + heightInput + 8.0f, buttonBackWidth = 103.0f, buttonBackHeight = 48.0f)) {
            this.closeScreen();
        }
        if (ScreenHelpers.isHovered(fixedMouse.x, fixedMouse.y, xInput + 111.0f, yInput + heightInput + 8.0f, buttonCreateWidth = 297.0f, buttonCreateHeight = 48.0f) && !this.username.isEmpty()) {
            this.accounts.add(new Account(this.username));
            AltScreen.mc.session = new Session(this.username, "", "", "mojang");
            this.username = "";
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        this.scrolling += (float)(delta * 30.0);
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public void tick() {
        this.usernameAnimation.update(this.usernameTyping);
        this.passwordAnimation.update(this.passwordTyping);
        this.buttonBack.update(this.hoveredBack);
        this.buttonCreate.update(this.hoveredCreate);
        for (Account account : this.accounts) {
            account.getAnimation().update(AltScreen.mc.session.getUsername().equals(account.accountName));
        }
    }

    @Deprecated(forRemoval=true, since="3.0")
    public List<String> get() {
        ArrayList<String> accounts = new ArrayList<String>();
        for (Account account : this.accounts) {
            String name = account.accountName;
            accounts.add(name);
        }
        return accounts;
    }

    @Generated
    public ArrayList<Account> getAccounts() {
        return this.accounts;
    }
}
