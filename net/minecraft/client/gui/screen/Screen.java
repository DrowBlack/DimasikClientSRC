package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dimasik.Load;
import dimasik.events.main.player.EventItemTooltip;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.screen.ConfirmOpenLinkScreen;
import net.minecraft.client.gui.screen.IScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.InputMappings;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Screen
extends FocusableGui
implements IScreen,
IRenderable {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<String> ALLOWED_PROTOCOLS = Sets.newHashSet("http", "https");
    protected final ITextComponent title;
    protected final List<IGuiEventListener> children = Lists.newArrayList();
    @Nullable
    protected Minecraft minecraft;
    protected ItemRenderer itemRenderer;
    public int width;
    public int height;
    protected final List<Widget> buttons = Lists.newArrayList();
    public boolean passEvents;
    protected FontRenderer font;
    private URI clickedLink;

    protected Screen(ITextComponent titleIn) {
        this.title = titleIn;
    }

    public ITextComponent getTitle() {
        return this.title;
    }

    public String getNarrationMessage() {
        return this.getTitle().getString();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        for (int i = 0; i < this.buttons.size(); ++i) {
            this.buttons.get(i).render(matrixStack, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 && this.shouldCloseOnEsc()) {
            this.closeScreen();
            return true;
        }
        if (keyCode == 258) {
            boolean flag;
            boolean bl = flag = !Screen.hasShiftDown();
            if (!this.changeFocus(flag)) {
                this.changeFocus(flag);
            }
            return false;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public boolean shouldCloseOnEsc() {
        return true;
    }

    public void closeScreen() {
        this.minecraft.displayGuiScreen(null);
    }

    protected <T extends Widget> T addButton(T button) {
        this.buttons.add(button);
        return this.addListener(button);
    }

    protected <T extends IGuiEventListener> T addListener(T listener) {
        this.children.add(listener);
        return listener;
    }

    protected void renderTooltip(MatrixStack matrixStack, ItemStack itemStack, int mouseX, int mouseY) {
        EventItemTooltip eventItemTooltip = new EventItemTooltip(itemStack);
        Load.getInstance().getEvents().call(eventItemTooltip);
        this.func_243308_b(matrixStack, this.getTooltipFromItem(itemStack), mouseX, mouseY);
    }

    public List<ITextComponent> getTooltipFromItem(ItemStack itemStack) {
        return itemStack.getTooltip(this.minecraft.player, this.minecraft.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
    }

    public void renderTooltip(MatrixStack matrixStack, ITextComponent text, int mouseX, int mouseY) {
        this.renderTooltip(matrixStack, Arrays.asList(text.func_241878_f()), mouseX, mouseY);
    }

    public void func_243308_b(MatrixStack p_243308_1_, List<ITextComponent> p_243308_2_, int p_243308_3_, int p_243308_4_) {
        this.renderTooltip(p_243308_1_, Lists.transform(p_243308_2_, ITextComponent::func_241878_f), p_243308_3_, p_243308_4_);
    }

    /*
     * WARNING - void declaration
     */
    public void renderTooltip(MatrixStack matrixStack, List<? extends IReorderingProcessor> tooltips, int mouseX, int mouseY) {
        if (!tooltips.isEmpty()) {
            int n;
            int i = 0;
            for (IReorderingProcessor iReorderingProcessor : tooltips) {
                int j = this.font.func_243245_a(iReorderingProcessor);
                if (j <= i) continue;
                i = j;
            }
            int i2 = mouseX + 12;
            int n2 = mouseY - 12;
            int k = 8;
            if (tooltips.size() > 1) {
                k += 2 + (tooltips.size() - 1) * 10;
            }
            if (i2 + i > this.width) {
                i2 -= 28 + i;
            }
            if (n2 + k + 6 > this.height) {
                n = this.height - k - 6;
            }
            matrixStack.push();
            int l = -267386864;
            int i1 = 0x505000FF;
            int j1 = 1344798847;
            int k1 = 400;
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
            Matrix4f matrix4f = matrixStack.getLast().getMatrix();
            Screen.fillGradient(matrix4f, bufferbuilder, i2 - 3, n - 4, i2 + i + 3, n - 3, 400, -267386864, -267386864);
            Screen.fillGradient(matrix4f, bufferbuilder, i2 - 3, n + k + 3, i2 + i + 3, n + k + 4, 400, -267386864, -267386864);
            Screen.fillGradient(matrix4f, bufferbuilder, i2 - 3, n - 3, i2 + i + 3, n + k + 3, 400, -267386864, -267386864);
            Screen.fillGradient(matrix4f, bufferbuilder, i2 - 4, n - 3, i2 - 3, n + k + 3, 400, -267386864, -267386864);
            Screen.fillGradient(matrix4f, bufferbuilder, i2 + i + 3, n - 3, i2 + i + 4, n + k + 3, 400, -267386864, -267386864);
            Screen.fillGradient(matrix4f, bufferbuilder, i2 - 3, n - 3 + 1, i2 - 3 + 1, n + k + 3 - 1, 400, 0x505000FF, 1344798847);
            Screen.fillGradient(matrix4f, bufferbuilder, i2 + i + 2, n - 3 + 1, i2 + i + 3, n + k + 3 - 1, 400, 0x505000FF, 1344798847);
            Screen.fillGradient(matrix4f, bufferbuilder, i2 - 3, n - 3, i2 + i + 3, n - 3 + 1, 400, 0x505000FF, 0x505000FF);
            Screen.fillGradient(matrix4f, bufferbuilder, i2 - 3, n + k + 2, i2 + i + 3, n + k + 3, 400, 1344798847, 1344798847);
            RenderSystem.enableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.shadeModel(7425);
            bufferbuilder.finishDrawing();
            WorldVertexBufferUploader.draw(bufferbuilder);
            RenderSystem.shadeModel(7424);
            RenderSystem.disableBlend();
            RenderSystem.enableTexture();
            IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
            matrixStack.translate(0.0, 0.0, 400.0);
            for (int l1 = 0; l1 < tooltips.size(); ++l1) {
                IReorderingProcessor ireorderingprocessor1 = tooltips.get(l1);
                if (ireorderingprocessor1 != null) {
                    void var7_11;
                    this.font.func_238416_a_(ireorderingprocessor1, i2, (float)var7_11, -1, true, matrix4f, irendertypebuffer$impl, false, 0, 0xF000F0);
                }
                if (l1 == 0) {
                    var7_11 += 2;
                }
                var7_11 += 10;
            }
            irendertypebuffer$impl.finish();
            matrixStack.pop();
        }
    }

    protected void renderComponentHoverEffect(MatrixStack matrixStack, @Nullable Style style, int mouseX, int mouseY) {
        if (style != null && style.getHoverEvent() != null) {
            HoverEvent hoverevent = style.getHoverEvent();
            HoverEvent.ItemHover hoverevent$itemhover = hoverevent.getParameter(HoverEvent.Action.SHOW_ITEM);
            if (hoverevent$itemhover != null) {
                this.renderTooltip(matrixStack, hoverevent$itemhover.createStack(), mouseX, mouseY);
            } else {
                HoverEvent.EntityHover hoverevent$entityhover = hoverevent.getParameter(HoverEvent.Action.SHOW_ENTITY);
                if (hoverevent$entityhover != null) {
                    if (this.minecraft.gameSettings.advancedItemTooltips) {
                        this.func_243308_b(matrixStack, hoverevent$entityhover.getTooltip(), mouseX, mouseY);
                    }
                } else {
                    ITextComponent itextcomponent = hoverevent.getParameter(HoverEvent.Action.SHOW_TEXT);
                    if (itextcomponent != null) {
                        this.renderTooltip(matrixStack, this.minecraft.fontRenderer.trimStringToWidth(itextcomponent, Math.max(this.width / 2, 200)), mouseX, mouseY);
                    }
                }
            }
        }
    }

    protected void insertText(String text, boolean overwrite) {
    }

    public boolean handleComponentClicked(@Nullable Style style) {
        if (style == null) {
            return false;
        }
        ClickEvent clickevent = style.getClickEvent();
        if (Screen.hasShiftDown()) {
            if (style.getInsertion() != null) {
                this.insertText(style.getInsertion(), false);
            }
        } else if (clickevent != null) {
            block21: {
                if (clickevent.getAction() == ClickEvent.Action.OPEN_URL) {
                    if (!this.minecraft.gameSettings.chatLinks) {
                        return false;
                    }
                    try {
                        URI uri = new URI(clickevent.getValue());
                        String s = uri.getScheme();
                        if (s == null) {
                            throw new URISyntaxException(clickevent.getValue(), "Missing protocol");
                        }
                        if (!ALLOWED_PROTOCOLS.contains(s.toLowerCase(Locale.ROOT))) {
                            throw new URISyntaxException(clickevent.getValue(), "Unsupported protocol: " + s.toLowerCase(Locale.ROOT));
                        }
                        if (this.minecraft.gameSettings.chatLinksPrompt) {
                            this.clickedLink = uri;
                            this.minecraft.displayGuiScreen(new ConfirmOpenLinkScreen(this::confirmLink, clickevent.getValue(), false));
                            break block21;
                        }
                        this.openLink(uri);
                    }
                    catch (URISyntaxException urisyntaxexception) {
                        LOGGER.error("Can't open url for {}", (Object)clickevent, (Object)urisyntaxexception);
                    }
                } else if (clickevent.getAction() == ClickEvent.Action.OPEN_FILE) {
                    URI uri1 = new File(clickevent.getValue()).toURI();
                    this.openLink(uri1);
                } else if (clickevent.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
                    this.insertText(clickevent.getValue(), true);
                } else if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                    this.sendMessage(clickevent.getValue(), false);
                } else if (clickevent.getAction() == ClickEvent.Action.COPY_TO_CLIPBOARD) {
                    this.minecraft.keyboardListener.setClipboardString(clickevent.getValue());
                } else {
                    LOGGER.error("Don't know how to handle {}", (Object)clickevent);
                }
            }
            return true;
        }
        return false;
    }

    public void sendMessage(String text) {
        this.sendMessage(text, true);
    }

    public void sendMessage(String text, boolean addToChat) {
        if (addToChat) {
            this.minecraft.ingameGUI.getChatGUI().addToSentMessages(text);
        }
        this.minecraft.player.sendChatMessage(text);
    }

    public void init(Minecraft minecraft, int width, int height) {
        this.minecraft = minecraft;
        this.itemRenderer = minecraft.getItemRenderer();
        this.font = minecraft.fontRenderer;
        this.width = width;
        this.height = height;
        this.buttons.clear();
        this.children.clear();
        this.setListener(null);
        this.init();
    }

    @Override
    public List<? extends IGuiEventListener> getEventListeners() {
        return this.children;
    }

    protected void init() {
    }

    @Override
    public void tick() {
    }

    public void onClose() {
    }

    public void renderBackground(MatrixStack matrixStack) {
        this.renderBackground(matrixStack, 0);
    }

    public void renderBackground(MatrixStack matrixStack, int vOffset) {
        if (this.minecraft.world != null) {
            this.fillGradient(matrixStack, 0, 0, this.width, this.height, -1072689136, -804253680);
        } else {
            this.renderDirtBackground(vOffset);
        }
    }

    public void renderDirtBackground(int vOffset) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        this.minecraft.getTextureManager().bindTexture(BACKGROUND_LOCATION);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float f = 32.0f;
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(0.0, this.height, 0.0).tex(0.0f, (float)this.height / 32.0f + (float)vOffset).color(64, 64, 64, 255).endVertex();
        bufferbuilder.pos(this.width, this.height, 0.0).tex((float)this.width / 32.0f, (float)this.height / 32.0f + (float)vOffset).color(64, 64, 64, 255).endVertex();
        bufferbuilder.pos(this.width, 0.0, 0.0).tex((float)this.width / 32.0f, vOffset).color(64, 64, 64, 255).endVertex();
        bufferbuilder.pos(0.0, 0.0, 0.0).tex(0.0f, vOffset).color(64, 64, 64, 255).endVertex();
        tessellator.draw();
    }

    public boolean isPauseScreen() {
        return true;
    }

    private void confirmLink(boolean doOpen) {
        if (doOpen) {
            this.openLink(this.clickedLink);
        }
        this.clickedLink = null;
        this.minecraft.displayGuiScreen(this);
    }

    private void openLink(URI uri) {
        Util.getOSType().openURI(uri);
    }

    public static boolean hasControlDown() {
        if (Minecraft.IS_RUNNING_ON_MAC) {
            return InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 343) || InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 347);
        }
        return InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 341) || InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 345);
    }

    public static boolean hasShiftDown() {
        return InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 340) || InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 344);
    }

    public static boolean hasAltDown() {
        return InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 342) || InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 346);
    }

    public static boolean isCut(int keyCode) {
        return keyCode == 88 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    public static boolean isPaste(int keyCode) {
        return keyCode == 86 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    public static boolean isCopy(int keyCode) {
        return keyCode == 67 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    public static boolean isSelectAll(int keyCode) {
        return keyCode == 65 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    public void resize(Minecraft minecraft, int width, int height) {
        this.init(minecraft, width, height);
    }

    public static void wrapScreenError(Runnable action, String errorDesc, String screenName) {
        try {
            action.run();
        }
        catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, errorDesc);
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Affected screen");
            crashreportcategory.addDetail("Screen name", () -> screenName);
            throw new ReportedException(crashreport);
        }
    }

    protected boolean isValidCharacterForName(String text, char charTyped, int cursorPos) {
        int i = text.indexOf(58);
        int j = text.indexOf(47);
        if (charTyped == ':') {
            return (j == -1 || cursorPos <= j) && i == -1;
        }
        if (charTyped == '/') {
            return cursorPos > i;
        }
        return charTyped == '_' || charTyped == '-' || charTyped >= 'a' && charTyped <= 'z' || charTyped >= '0' && charTyped <= '9' || charTyped == '.';
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return true;
    }

    public void addPacks(List<Path> packs) {
    }
}
