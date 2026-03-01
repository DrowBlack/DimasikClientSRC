package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dimasik.Load;
import dimasik.helpers.render.ColorHelpers;
import dimasik.managers.client.ClientManagers;
import dimasik.utils.client.Easing;
import java.util.Deque;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.RenderComponentsUtil;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.entity.player.ChatVisibility;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NewChatGui
extends AbstractGui {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Minecraft mc;
    private final List<String> sentMessages = Lists.newArrayList();
    public final List<ChatLine<ITextComponent>> chatLines = Lists.newArrayList();
    public final List<ChatLine<IReorderingProcessor>> drawnChatLines = Lists.newArrayList();
    private final Deque<ITextComponent> field_238489_i_ = Queues.newArrayDeque();
    private int scrollPos;
    private boolean isScrolled;
    private long field_238490_l_ = 0L;
    private int lastChatWidth = 0;

    public NewChatGui(Minecraft mcIn) {
        this.mc = mcIn;
    }

    public void func_238492_a_(MatrixStack p_238492_1_, int p_238492_2_) {
        int i = this.getChatWidth();
        if (this.lastChatWidth != i) {
            this.lastChatWidth = i;
            this.refreshChat();
        }
        if (!this.func_238496_i_()) {
            this.func_238498_k_();
            int j = this.getLineCount();
            int k = this.drawnChatLines.size();
            if (k > 0) {
                int j2;
                int i2;
                int l3;
                int j1;
                boolean flag = false;
                if (this.getChatOpen()) {
                    flag = true;
                }
                double d0 = this.getScale();
                int l = MathHelper.ceil((double)this.getChatWidth() / d0);
                RenderSystem.pushMatrix();
                RenderSystem.translatef(2.0f, 8.0f, 0.0f);
                RenderSystem.scaled(d0, d0, 1.0);
                double d1 = this.mc.gameSettings.chatOpacity * (double)0.9f + (double)0.1f;
                double d2 = this.mc.gameSettings.accessibilityTextBackgroundOpacity;
                double d3 = 9.0 * (this.mc.gameSettings.chatLineSpacing + 1.0);
                double d4 = -8.0 * (this.mc.gameSettings.chatLineSpacing + 1.0) + 4.0 * this.mc.gameSettings.chatLineSpacing;
                int i1 = 0;
                for (j1 = 0; j1 + this.scrollPos < this.drawnChatLines.size() && j1 < j; ++j1) {
                    ChatLine<IReorderingProcessor> chatline = this.drawnChatLines.get(j1 + this.scrollPos);
                    if (chatline == null || (l3 = p_238492_2_ - chatline.getUpdatedCounter()) >= 200 && !flag) continue;
                    double d5 = flag ? 1.0 : NewChatGui.getLineBrightness(l3);
                    i2 = (int)(255.0 * d5 * d1);
                    j2 = (int)(255.0 * d5 * d2);
                    ++i1;
                    if (i2 <= 3) continue;
                    boolean k2 = false;
                    double d6 = (double)(-j1) * d3;
                    p_238492_1_.push();
                    chatline.yAnim.run(i2 < 254 ? 0.0 : 1.0);
                    chatline.yAnim.setEasing(i2 < 254 ? Easing.EASE_IN_BACK : Easing.EASE_OUT_BACK);
                    chatline.alphaAnim.run(i2 < 254 ? 0.0 : 1.0);
                    chatline.alphaAnim.setDuration(i2 < 254 ? 450L : 1100L);
                    chatline.alphaAnim.setEasing(i2 < 254 ? Easing.EASE_IN_BACK : Easing.EASE_OUT_BACK);
                    p_238492_1_.translate((double)(-NewChatGui.calculateChatboxWidth(this.mc.gameSettings.chatWidth)) + (double)NewChatGui.calculateChatboxWidth(this.mc.gameSettings.chatWidth) * ((Boolean)Load.getInstance().getHooks().getModuleManagers().getBetterChat().getAnimationChat().getValue() != false && Load.getInstance().getHooks().getModuleManagers().getBetterChat().isToggled() ? chatline.yAnim.getValue() : 1.0), 0.0, 50.0);
                    if (this.mc.gameSettings.ofChatBackground == 5) {
                        l = this.mc.fontRenderer.func_243245_a(chatline.getLineString()) - 2;
                    }
                    if (this.mc.gameSettings.ofChatBackground != 3) {
                        boolean can;
                        NewChatGui.fill(p_238492_1_, -2, (int)(d6 - d3), 0 + l + 4, (int)d6, ColorHelpers.setAlpha(j2 << 24, i2 < 122 ? i2 : ((Boolean)Load.getInstance().getHooks().getModuleManagers().getBetterChat().getAnimationChat().getValue() != false && Load.getInstance().getHooks().getModuleManagers().getBetterChat().isToggled() ? (int)(chatline.alphaAnim.getValue() * 122.0) : 122)));
                        String msg = this.getStringFromReorderingProcessor(chatline.getLineString()).toLowerCase();
                        boolean bl = can = !ClientManagers.isUnHook();
                        if (can && msg.contains(this.mc.player.getName().getString().toLowerCase())) {
                            NewChatGui.fill(p_238492_1_, -2, (int)(d6 - d3), 0 + l + 4, (int)d6, ColorHelpers.setAlpha(ColorHelpers.rgba(22, 161, 225, 150), (int)(((Boolean)Load.getInstance().getHooks().getModuleManagers().getBetterChat().getAnimationChat().getValue() != false && Load.getInstance().getHooks().getModuleManagers().getBetterChat().isToggled() ? chatline.alphaAnim.getValue() : 1.0) * 122.0)));
                        }
                    }
                    RenderSystem.enableBlend();
                    p_238492_1_.translate(0.0, 0.0, 50.0);
                    if (!this.mc.gameSettings.ofChatShadow) {
                        this.mc.fontRenderer.func_238422_b_(p_238492_1_, chatline.getLineString(), 0.0f, (int)(d6 + d4), ColorHelpers.setAlpha(0xFFFFFF, i2 < 254 ? i2 : (int)(((Boolean)Load.getInstance().getHooks().getModuleManagers().getBetterChat().getAnimationChat().getValue() != false && Load.getInstance().getHooks().getModuleManagers().getBetterChat().isToggled() ? chatline.alphaAnim.getValue() : 1.0) * 254.0)));
                    } else {
                        this.mc.fontRenderer.func_238407_a_(p_238492_1_, chatline.getLineString(), 0.0f, (int)(d6 + d4), ColorHelpers.setAlpha(0xFFFFFF, i2 < 254 ? i2 : (int)(((Boolean)Load.getInstance().getHooks().getModuleManagers().getBetterChat().getAnimationChat().getValue() != false && Load.getInstance().getHooks().getModuleManagers().getBetterChat().isToggled() ? chatline.alphaAnim.getValue() : 1.0) * 254.0)));
                    }
                    RenderSystem.disableAlphaTest();
                    RenderSystem.disableBlend();
                    p_238492_1_.pop();
                }
                if (!this.field_238489_i_.isEmpty()) {
                    j1 = (int)(128.0 * d1);
                    int k3 = (int)(255.0 * d2);
                    p_238492_1_.push();
                    p_238492_1_.translate(0.0, 0.0, 50.0);
                    NewChatGui.fill(p_238492_1_, -2, 0, l + 4, 9, k3 << 24);
                    RenderSystem.enableBlend();
                    p_238492_1_.translate(0.0, 0.0, 50.0);
                    this.mc.fontRenderer.func_243246_a(p_238492_1_, new TranslationTextComponent("chat.queue", this.field_238489_i_.size()), 0.0f, 1.0f, 0xFFFFFF + (j1 << 24));
                    p_238492_1_.pop();
                    RenderSystem.disableAlphaTest();
                    RenderSystem.disableBlend();
                }
                if (flag) {
                    int i3 = 9;
                    RenderSystem.translatef(-3.0f, 0.0f, 0.0f);
                    int k3 = k * i3 + k;
                    l3 = i1 * i3 + i1;
                    int i4 = this.scrollPos * l3 / k;
                    int l1 = l3 * l3 / k3;
                    if (k3 != l3) {
                        i2 = i4 > 0 ? 170 : 96;
                        j2 = this.isScrolled ? 0xCC3333 : 0x3333AA;
                        NewChatGui.fill(p_238492_1_, 0, -i4, 2, -i4 - l1, j2 + (i2 << 24));
                        NewChatGui.fill(p_238492_1_, 2, -i4, 1, -i4 - l1, 0xCCCCCC + (i2 << 24));
                    }
                }
                RenderSystem.popMatrix();
            }
        }
    }

    private boolean func_238496_i_() {
        return this.mc.gameSettings.chatVisibility == ChatVisibility.HIDDEN;
    }

    private static double getLineBrightness(int counterIn) {
        double d0 = (double)counterIn / 200.0;
        d0 = 1.0 - d0;
        d0 *= 10.0;
        d0 = MathHelper.clamp(d0, 0.0, 1.0);
        return d0 * d0;
    }

    public void clearChatMessages(boolean clearSentMsgHistory) {
        this.field_238489_i_.clear();
        this.drawnChatLines.clear();
        this.chatLines.clear();
        if (clearSentMsgHistory) {
            this.sentMessages.clear();
        }
    }

    public void printChatMessage(ITextComponent chatComponent) {
        this.printChatMessageWithOptionalDeletion(chatComponent, 0, false);
    }

    public void printChatMessageWithOptionalDeletion(ITextComponent chatComponent, int chatLineId, boolean client) {
        this.func_238493_a_(chatComponent, chatLineId, this.mc.ingameGUI.getTicks(), false, client);
        if (!client) {
            LOGGER.info("[CHAT] {}", (Object)chatComponent.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
        }
    }

    private void func_238493_a_(ITextComponent p_238493_1_, int p_238493_2_, int p_238493_3_, boolean p_238493_4_, boolean client) {
        if (p_238493_2_ != 0) {
            this.deleteChatLine(p_238493_2_);
        }
        int i = MathHelper.floor((double)this.getChatWidth() / this.getScale());
        List<IReorderingProcessor> list = RenderComponentsUtil.func_238505_a_(p_238493_1_, i, this.mc.fontRenderer);
        boolean flag = this.getChatOpen();
        for (IReorderingProcessor ireorderingprocessor : list) {
            if (flag && this.scrollPos > 0) {
                this.isScrolled = true;
                this.addScrollPos(1.0);
            }
            this.drawnChatLines.add(0, new ChatLine<IReorderingProcessor>(p_238493_3_, ireorderingprocessor, p_238493_2_, client));
        }
        while (this.drawnChatLines.size() > 100) {
            this.drawnChatLines.remove(this.drawnChatLines.size() - 1);
        }
        if (!p_238493_4_) {
            this.chatLines.add(0, new ChatLine<ITextComponent>(p_238493_3_, p_238493_1_, p_238493_2_, client));
            while (this.chatLines.size() > 100) {
                this.chatLines.remove(this.chatLines.size() - 1);
            }
        }
    }

    public String getStringFromReorderingProcessor(IReorderingProcessor reorderingProcessor) {
        StringBuilder stringBuilder = new StringBuilder();
        reorderingProcessor.accept((index, style, codePoint) -> {
            stringBuilder.append(Character.toChars(codePoint));
            return true;
        });
        return stringBuilder.toString();
    }

    public void refreshChat() {
        this.drawnChatLines.clear();
        this.resetScroll();
        for (int i = this.chatLines.size() - 1; i >= 0; --i) {
            ChatLine<ITextComponent> chatline = this.chatLines.get(i);
            this.func_238493_a_(chatline.getLineString(), chatline.getChatLineID(), chatline.getUpdatedCounter(), true, chatline.isClient());
        }
    }

    public List<String> getSentMessages() {
        return this.sentMessages;
    }

    public void addToSentMessages(String message) {
        if (this.sentMessages.isEmpty() || !this.sentMessages.get(this.sentMessages.size() - 1).equals(message)) {
            this.sentMessages.add(message);
        }
    }

    public void resetScroll() {
        this.scrollPos = 0;
        this.isScrolled = false;
    }

    public void addScrollPos(double posInc) {
        this.scrollPos = (int)((double)this.scrollPos + posInc);
        int i = this.drawnChatLines.size();
        if (this.scrollPos > i - this.getLineCount()) {
            this.scrollPos = i - this.getLineCount();
        }
        if (this.scrollPos <= 0) {
            this.scrollPos = 0;
            this.isScrolled = false;
        }
    }

    public boolean func_238491_a_(double p_238491_1_, double p_238491_3_) {
        if (this.getChatOpen() && !this.mc.gameSettings.hideGUI && !this.func_238496_i_() && !this.field_238489_i_.isEmpty()) {
            double d0 = p_238491_1_ - 2.0;
            double d1 = (double)this.mc.getMainWindow().getScaledHeight() - p_238491_3_ - 40.0;
            if (d0 <= (double)MathHelper.floor((double)this.getChatWidth() / this.getScale()) && d1 < 0.0 && d1 > (double)MathHelper.floor(-9.0 * this.getScale())) {
                this.printChatMessage(this.field_238489_i_.remove());
                this.field_238490_l_ = System.currentTimeMillis();
                return true;
            }
            return false;
        }
        return false;
    }

    @Nullable
    public ChatLine<ITextComponent> chatLine(double p_238494_1_, double p_238494_3_) {
        if (this.getChatOpen() && !this.mc.gameSettings.hideGUI && !this.func_238496_i_()) {
            double d0 = p_238494_1_ - 2.0;
            double d1 = (double)this.mc.getMainWindow().getScaledHeight() - p_238494_3_ - 40.0;
            d0 = MathHelper.floor(d0 / this.getScale());
            d1 = MathHelper.floor(d1 / (this.getScale() * (this.mc.gameSettings.chatLineSpacing + 1.0)));
            if (!(d0 < 0.0) && !(d1 < 0.0)) {
                int j;
                int i = Math.min(this.getLineCount(), this.chatLines.size());
                if (d0 <= (double)MathHelper.floor((double)this.getChatWidth() / this.getScale()) && d1 < (double)(9 * i + i) && (j = (int)(d1 / 9.0 + (double)this.scrollPos)) >= 0 && j < this.chatLines.size()) {
                    ChatLine<ITextComponent> chatline = this.chatLines.get(j);
                    return chatline;
                }
                return null;
            }
            return null;
        }
        return null;
    }

    @Nullable
    public Style func_238494_b_(double p_238494_1_, double p_238494_3_) {
        if (this.getChatOpen() && !this.mc.gameSettings.hideGUI && !this.func_238496_i_()) {
            double d0 = p_238494_1_ - 2.0;
            double d1 = (double)this.mc.getMainWindow().getScaledHeight() - p_238494_3_ - 40.0;
            d0 = MathHelper.floor(d0 / this.getScale());
            d1 = MathHelper.floor(d1 / (this.getScale() * (this.mc.gameSettings.chatLineSpacing + 1.0)));
            if (!(d0 < 0.0) && !(d1 < 0.0)) {
                int j;
                int i = Math.min(this.getLineCount(), this.drawnChatLines.size());
                if (d0 <= (double)MathHelper.floor((double)this.getChatWidth() / this.getScale()) && d1 < (double)(9 * i + i) && (j = (int)(d1 / 9.0 + (double)this.scrollPos)) >= 0 && j < this.drawnChatLines.size()) {
                    ChatLine<IReorderingProcessor> chatline = this.drawnChatLines.get(j);
                    return this.mc.fontRenderer.getCharacterManager().func_243239_a(chatline.getLineString(), (int)d0);
                }
                return null;
            }
            return null;
        }
        return null;
    }

    private boolean getChatOpen() {
        return this.mc.currentScreen instanceof ChatScreen;
    }

    public void deleteChatLine(int id) {
        this.drawnChatLines.removeIf(p_lambda$deleteChatLine$0_1_ -> p_lambda$deleteChatLine$0_1_.getChatLineID() == id);
        this.chatLines.removeIf(p_lambda$deleteChatLine$1_1_ -> p_lambda$deleteChatLine$1_1_.getChatLineID() == id);
    }

    public int getChatWidth() {
        int i = NewChatGui.calculateChatboxWidth(this.mc.gameSettings.chatWidth);
        MainWindow mainwindow = Minecraft.getInstance().getMainWindow();
        int j = (int)((double)(mainwindow.getFramebufferWidth() - 3) / mainwindow.getGuiScaleFactor());
        return MathHelper.clamp(i, 0, j);
    }

    public int getChatHeight() {
        return NewChatGui.calculateChatboxHeight((this.getChatOpen() ? this.mc.gameSettings.chatHeightFocused : this.mc.gameSettings.chatHeightUnfocused) / (this.mc.gameSettings.chatLineSpacing + 1.0));
    }

    public double getScale() {
        return this.mc.gameSettings.chatScale;
    }

    public static int calculateChatboxWidth(double p_194814_0_) {
        boolean i = true;
        boolean j = true;
        return MathHelper.floor(p_194814_0_ * 280.0 + 40.0);
    }

    public static int calculateChatboxHeight(double p_194816_0_) {
        boolean i = true;
        boolean j = true;
        return MathHelper.floor(p_194816_0_ * 160.0 + 20.0);
    }

    public int getLineCount() {
        return this.getChatHeight() / 9;
    }

    private long func_238497_j_() {
        return (long)(this.mc.gameSettings.chatDelay * 1000.0);
    }

    private void func_238498_k_() {
        long i;
        if (!this.field_238489_i_.isEmpty() && (i = System.currentTimeMillis()) - this.field_238490_l_ >= this.func_238497_j_()) {
            this.printChatMessage(this.field_238489_i_.remove());
            this.field_238490_l_ = i;
        }
    }

    public void removeChatMessages(ITextComponent filter) {
        this.drawnChatLines.removeIf(chatLine -> ((IReorderingProcessor)chatLine.getLineString()).equals(filter));
        this.chatLines.removeIf(chatLine -> ((ITextComponent)chatLine.getLineString()).equals(filter));
    }

    public void func_238495_b_(ITextComponent p_238495_1_) {
        if (this.mc.gameSettings.chatDelay <= 0.0) {
            this.printChatMessage(p_238495_1_);
        } else {
            long i = System.currentTimeMillis();
            if (i - this.field_238490_l_ >= this.func_238497_j_()) {
                this.printChatMessage(p_238495_1_);
                this.field_238490_l_ = i;
            } else {
                this.field_238489_i_.add(p_238495_1_);
            }
        }
    }
}
