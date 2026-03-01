package net.minecraft.client;

import dimasik.Load;
import dimasik.events.main.player.EventMouseButtonPress;
import dimasik.events.main.player.EventRotate;
import dimasik.helpers.input.InputHelpers;
import dimasik.helpers.interfaces.IFastAccess;
import dimasik.modules.player.FreeCam;
import dimasik.ui.screen.UIScreen;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ControlsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.AnvilScreen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.util.MouseSmoother;
import net.minecraft.client.util.NativeUtil;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFWDropCallback;

public class MouseHelper
implements IFastAccess {
    private final Minecraft minecraft;
    private boolean leftDown;
    private boolean middleDown;
    private boolean rightDown;
    private double mouseX;
    private double mouseY;
    private int simulatedRightClicks;
    private int activeButton = -1;
    private boolean ignoreFirstMove = true;
    private int touchScreenCounter;
    private double eventTime;
    private final MouseSmoother xSmoother = new MouseSmoother();
    private final MouseSmoother ySmoother = new MouseSmoother();
    private double xVelocity;
    private double yVelocity;
    private double accumulatedScrollDelta;
    private double lastLookTime = Double.MIN_VALUE;
    private boolean mouseGrabbed;
    EventMouseButtonPress eventMouseButtonPress = new EventMouseButtonPress(1);
    private final EventRotate eventRotate = new EventRotate(0.0, 0.0);

    public MouseHelper(Minecraft minecraftIn) {
        this.minecraft = minecraftIn;
    }

    private void mouseButtonCallback(long handle, int button, int action, int mods) {
        if (handle == this.minecraft.getMainWindow().getHandle()) {
            boolean flag;
            boolean bl = flag = action == 1;
            if (Minecraft.IS_RUNNING_ON_MAC && button == 0) {
                if (flag) {
                    if ((mods & 2) == 2) {
                        button = 1;
                        ++this.simulatedRightClicks;
                    }
                } else if (this.simulatedRightClicks > 0) {
                    button = 1;
                    --this.simulatedRightClicks;
                }
            }
            int i = button;
            if (flag) {
                if (!(MouseHelper.mc.world == null || MouseHelper.mc.currentScreen instanceof CreativeScreen || MouseHelper.mc.currentScreen instanceof AnvilScreen || MouseHelper.mc.currentScreen instanceof ControlsScreen || MouseHelper.mc.currentScreen instanceof ChatScreen || MouseHelper.mc.currentScreen instanceof UIScreen)) {
                    new InputHelpers(i);
                }
                if (MouseHelper.mc.world == null || MouseHelper.mc.currentScreen instanceof ChatScreen) {
                    // empty if block
                }
                if (this.minecraft.gameSettings.touchscreen && this.touchScreenCounter++ > 0) {
                    return;
                }
                this.activeButton = i;
                this.eventTime = NativeUtil.getTime();
            } else if (this.activeButton != -1) {
                if (this.minecraft.gameSettings.touchscreen && --this.touchScreenCounter > 0) {
                    return;
                }
                this.activeButton = -1;
            }
            boolean[] aboolean = new boolean[]{false};
            if (this.minecraft.loadingGui == null) {
                if (this.minecraft.currentScreen == null) {
                    if (!this.mouseGrabbed && flag) {
                        this.grabMouse();
                    }
                } else {
                    double d0 = this.mouseX * (double)this.minecraft.getMainWindow().getScaledWidth() / (double)this.minecraft.getMainWindow().getWidth();
                    double d1 = this.mouseY * (double)this.minecraft.getMainWindow().getScaledHeight() / (double)this.minecraft.getMainWindow().getHeight();
                    if (flag) {
                        Screen.wrapScreenError(() -> {
                            aboolean[0] = this.minecraft.currentScreen.mouseClicked(d0, d1, i);
                        }, "mouseClicked event handler", this.minecraft.currentScreen.getClass().getCanonicalName());
                    } else {
                        Screen.wrapScreenError(() -> {
                            aboolean[0] = this.minecraft.currentScreen.mouseReleased(d0, d1, i);
                        }, "mouseReleased event handler", this.minecraft.currentScreen.getClass().getCanonicalName());
                    }
                }
            }
            if (!aboolean[0] && (this.minecraft.currentScreen == null || this.minecraft.currentScreen.passEvents) && this.minecraft.loadingGui == null) {
                if (i == 0) {
                    this.leftDown = flag;
                } else if (i == 2) {
                    this.middleDown = flag;
                } else if (i == 1) {
                    this.rightDown = flag;
                }
                KeyBinding.setKeyBindState(InputMappings.Type.MOUSE.getOrMakeInput(i), flag);
                if (flag) {
                    this.eventMouseButtonPress.setButton(button);
                    Load.getInstance().getEvents().call(this.eventMouseButtonPress);
                    if (this.minecraft.player.isSpectator() && i == 2) {
                        this.minecraft.ingameGUI.getSpectatorGui().onMiddleClick();
                    } else {
                        KeyBinding.onTick(InputMappings.Type.MOUSE.getOrMakeInput(i));
                    }
                }
            }
        }
    }

    private void scrollCallback(long handle, double xoffset, double yoffset) {
        if (handle == Minecraft.getInstance().getMainWindow().getHandle()) {
            double d0 = (this.minecraft.gameSettings.discreteMouseScroll ? Math.signum(yoffset) : yoffset) * this.minecraft.gameSettings.mouseWheelSensitivity;
            if (this.minecraft.loadingGui == null) {
                if (this.minecraft.currentScreen != null) {
                    double d1 = this.mouseX * (double)this.minecraft.getMainWindow().getScaledWidth() / (double)this.minecraft.getMainWindow().getWidth();
                    double d2 = this.mouseY * (double)this.minecraft.getMainWindow().getScaledHeight() / (double)this.minecraft.getMainWindow().getHeight();
                    this.minecraft.currentScreen.mouseScrolled(d1, d2, d0);
                } else if (this.minecraft.player != null) {
                    if (this.accumulatedScrollDelta != 0.0 && Math.signum(d0) != Math.signum(this.accumulatedScrollDelta)) {
                        this.accumulatedScrollDelta = 0.0;
                    }
                    this.accumulatedScrollDelta += d0;
                    float f1 = (int)this.accumulatedScrollDelta;
                    if (f1 == 0.0f) {
                        return;
                    }
                    this.accumulatedScrollDelta -= (double)f1;
                    if (this.minecraft.player.isSpectator()) {
                        if (this.minecraft.ingameGUI.getSpectatorGui().isMenuActive()) {
                            this.minecraft.ingameGUI.getSpectatorGui().onMouseScroll(-f1);
                        } else {
                            float f = MathHelper.clamp(this.minecraft.player.abilities.getFlySpeed() + f1 * 0.005f, 0.0f, 0.2f);
                            this.minecraft.player.abilities.setFlySpeed(f);
                        }
                    } else {
                        this.minecraft.player.inventory.changeCurrentItem(f1);
                    }
                }
            }
        }
    }

    private void addPacksToScreen(long window, List<Path> paths) {
        if (this.minecraft.currentScreen != null) {
            this.minecraft.currentScreen.addPacks(paths);
        }
    }

    public void registerCallbacks(long handle) {
        InputMappings.setMouseCallbacks(handle, (handle1, xPos, yPos) -> this.minecraft.execute(() -> this.cursorPosCallback(handle1, xPos, yPos)), (handle1, button, action, modifiers) -> this.minecraft.execute(() -> this.mouseButtonCallback(handle1, button, action, modifiers)), (handle1, xOffset, yOffset) -> this.minecraft.execute(() -> this.scrollCallback(handle1, xOffset, yOffset)), (window, callbackCount, names) -> {
            Path[] apath = new Path[callbackCount];
            for (int i = 0; i < callbackCount; ++i) {
                apath[i] = Paths.get(GLFWDropCallback.getName(names, i), new String[0]);
            }
            this.minecraft.execute(() -> this.addPacksToScreen(window, Arrays.asList(apath)));
        });
    }

    private void cursorPosCallback(long handle, double xpos, double ypos) {
        if (handle == Minecraft.getInstance().getMainWindow().getHandle()) {
            Screen iguieventlistener;
            if (this.ignoreFirstMove) {
                this.mouseX = xpos;
                this.mouseY = ypos;
                this.ignoreFirstMove = false;
            }
            if ((iguieventlistener = this.minecraft.currentScreen) != null && this.minecraft.loadingGui == null) {
                double d0 = xpos * (double)this.minecraft.getMainWindow().getScaledWidth() / (double)this.minecraft.getMainWindow().getWidth();
                double d1 = ypos * (double)this.minecraft.getMainWindow().getScaledHeight() / (double)this.minecraft.getMainWindow().getHeight();
                Screen.wrapScreenError(() -> iguieventlistener.mouseMoved(d0, d1), "mouseMoved event handler", iguieventlistener.getClass().getCanonicalName());
                if (this.activeButton != -1 && this.eventTime > 0.0) {
                    double d2 = (xpos - this.mouseX) * (double)this.minecraft.getMainWindow().getScaledWidth() / (double)this.minecraft.getMainWindow().getWidth();
                    double d3 = (ypos - this.mouseY) * (double)this.minecraft.getMainWindow().getScaledHeight() / (double)this.minecraft.getMainWindow().getHeight();
                    Screen.wrapScreenError(() -> iguieventlistener.mouseDragged(d0, d1, this.activeButton, d2, d3), "mouseDragged event handler", iguieventlistener.getClass().getCanonicalName());
                }
            }
            this.minecraft.getProfiler().startSection("mouse");
            if (this.isMouseGrabbed() && this.minecraft.isGameFocused()) {
                this.xVelocity += xpos - this.mouseX;
                this.yVelocity += ypos - this.mouseY;
            }
            this.updatePlayerLook();
            this.mouseX = xpos;
            this.mouseY = ypos;
            this.minecraft.getProfiler().endSection();
        }
    }

    public void updatePlayerLook() {
        double d0 = NativeUtil.getTime();
        double d1 = d0 - this.lastLookTime;
        this.lastLookTime = d0;
        if (this.isMouseGrabbed() && this.minecraft.isGameFocused()) {
            double d3;
            double d2;
            double d4 = this.minecraft.gameSettings.mouseSensitivity * (double)0.6f + (double)0.2f;
            double d5 = d4 * d4 * d4 * 8.0;
            if (this.minecraft.gameSettings.smoothCamera) {
                double d6 = this.xSmoother.smooth(this.xVelocity * d5, d1 * d5);
                double d7 = this.ySmoother.smooth(this.yVelocity * d5, d1 * d5);
                d2 = d6;
                d3 = d7;
            } else {
                this.xSmoother.reset();
                this.ySmoother.reset();
                d2 = this.xVelocity * d5;
                d3 = this.yVelocity * d5;
            }
            this.xVelocity = 0.0;
            this.yVelocity = 0.0;
            int i = 1;
            if (this.minecraft.gameSettings.invertMouse) {
                i = -1;
            }
            this.minecraft.getTutorial().onMouseMove(d2, d3);
            FreeCam freeCam = Load.getInstance().getHooks().getModuleManagers().getFreeCam();
            if (freeCam.isToggled() && freeCam.player != null) {
                freeCam.player.rotateTowards(d2, d3 * (double)i);
            } else if (this.minecraft.player != null) {
                EventRotate event = new EventRotate(d2, d3 * (double)i);
                Load.getInstance().getEvents().call(event);
                if (!event.isCancelled()) {
                    this.minecraft.player.rotateTowards(d2, d3 * (double)i);
                }
            }
        } else {
            this.xVelocity = 0.0;
            this.yVelocity = 0.0;
        }
    }

    public boolean isLeftDown() {
        return this.leftDown;
    }

    public boolean isRightDown() {
        return this.rightDown;
    }

    public double getMouseX() {
        return this.mouseX;
    }

    public double getMouseY() {
        return this.mouseY;
    }

    public void setIgnoreFirstMove() {
        this.ignoreFirstMove = true;
    }

    public boolean isMouseGrabbed() {
        return this.mouseGrabbed;
    }

    public void grabMouse() {
        if (this.minecraft.isGameFocused() && !this.mouseGrabbed) {
            if (!Minecraft.IS_RUNNING_ON_MAC) {
                KeyBinding.updateKeyBindState();
            }
            this.mouseGrabbed = true;
            this.mouseX = this.minecraft.getMainWindow().getWidth() / 2;
            this.mouseY = this.minecraft.getMainWindow().getHeight() / 2;
            InputMappings.setCursorPosAndMode(this.minecraft.getMainWindow().getHandle(), 212995, this.mouseX, this.mouseY);
            this.minecraft.displayGuiScreen(null);
            this.minecraft.leftClickCounter = 10000;
            this.ignoreFirstMove = true;
        }
    }

    public void ungrabMouse() {
        if (this.mouseGrabbed) {
            this.mouseGrabbed = false;
            this.mouseX = this.minecraft.getMainWindow().getWidth() / 2;
            this.mouseY = this.minecraft.getMainWindow().getHeight() / 2;
            InputMappings.setCursorPosAndMode(this.minecraft.getMainWindow().getHandle(), 212993, this.mouseX, this.mouseY);
        }
    }

    public void ignoreFirstMove() {
        this.ignoreFirstMove = true;
    }
}
