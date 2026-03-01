package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.resources.IResource;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WinGameScreen
extends Screen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation MINECRAFT_LOGO = new ResourceLocation("textures/gui/title/minecraft.png");
    private static final ResourceLocation MINECRAFT_EDITION = new ResourceLocation("textures/gui/title/edition.png");
    private static final ResourceLocation VIGNETTE_TEXTURE = new ResourceLocation("textures/misc/vignette.png");
    private static final String field_238663_q_ = String.valueOf((Object)TextFormatting.WHITE) + String.valueOf((Object)TextFormatting.OBFUSCATED) + String.valueOf((Object)TextFormatting.GREEN) + String.valueOf((Object)TextFormatting.AQUA);
    private final boolean poem;
    private final Runnable onFinished;
    private float time;
    private List<IReorderingProcessor> lines;
    private IntSet field_238664_v_;
    private int totalScrollLength;
    private float scrollSpeed = 0.5f;

    public WinGameScreen(boolean poemIn, Runnable onFinishedIn) {
        super(NarratorChatListener.EMPTY);
        this.poem = poemIn;
        this.onFinished = onFinishedIn;
        if (!poemIn) {
            this.scrollSpeed = 0.75f;
        }
    }

    @Override
    public void tick() {
        this.minecraft.getMusicTicker().tick();
        this.minecraft.getSoundHandler().tick(false);
        float f = (float)(this.totalScrollLength + this.height + this.height + 24) / this.scrollSpeed;
        if (this.time > f) {
            this.sendRespawnPacket();
        }
    }

    @Override
    public void closeScreen() {
        this.sendRespawnPacket();
    }

    private void sendRespawnPacket() {
        this.onFinished.run();
        this.minecraft.displayGuiScreen(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void init() {
        if (this.lines == null) {
            this.lines = Lists.newArrayList();
            this.field_238664_v_ = new IntOpenHashSet();
            IResource iresource = null;
            try {
                String s3;
                int i = 274;
                if (this.poem) {
                    Object s;
                    iresource = this.minecraft.getResourceManager().getResource(new ResourceLocation("texts/end.txt"));
                    InputStream inputstream = iresource.getInputStream();
                    BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));
                    Random random = new Random(8124371L);
                    while ((s = bufferedreader.readLine()) != null) {
                        int j;
                        s = ((String)s).replaceAll("PLAYERNAME", this.minecraft.getSession().getUsername());
                        while ((j = ((String)s).indexOf(field_238663_q_)) != -1) {
                            String s1 = ((String)s).substring(0, j);
                            String s2 = ((String)s).substring(j + field_238663_q_.length());
                            s = s1 + String.valueOf((Object)TextFormatting.WHITE) + String.valueOf((Object)TextFormatting.OBFUSCATED) + "XXXXXXXX".substring(0, random.nextInt(4) + 3) + s2;
                        }
                        this.lines.addAll(this.minecraft.fontRenderer.trimStringToWidth(new StringTextComponent((String)s), 274));
                        this.lines.add(IReorderingProcessor.field_242232_a);
                    }
                    inputstream.close();
                    for (int k = 0; k < 8; ++k) {
                        this.lines.add(IReorderingProcessor.field_242232_a);
                    }
                }
                InputStream inputstream1 = this.minecraft.getResourceManager().getResource(new ResourceLocation("texts/credits.txt")).getInputStream();
                BufferedReader bufferedreader1 = new BufferedReader(new InputStreamReader(inputstream1, StandardCharsets.UTF_8));
                while ((s3 = bufferedreader1.readLine()) != null) {
                    boolean flag;
                    s3 = s3.replaceAll("PLAYERNAME", this.minecraft.getSession().getUsername());
                    if ((s3 = s3.replaceAll("\t", "    ")).startsWith("[C]")) {
                        s3 = s3.substring(3);
                        flag = true;
                    } else {
                        flag = false;
                    }
                    for (IReorderingProcessor ireorderingprocessor : this.minecraft.fontRenderer.trimStringToWidth(new StringTextComponent(s3), 274)) {
                        if (flag) {
                            this.field_238664_v_.add(this.lines.size());
                        }
                        this.lines.add(ireorderingprocessor);
                    }
                    this.lines.add(IReorderingProcessor.field_242232_a);
                }
                inputstream1.close();
                this.totalScrollLength = this.lines.size() * 12;
                IOUtils.closeQuietly((Closeable)iresource);
            }
            catch (Exception exception) {
                LOGGER.error("Couldn't load credits", (Throwable)exception);
            }
            finally {
                IOUtils.closeQuietly(iresource);
            }
        }
    }

    private void drawWinGameScreen(int mouseX, int mouseY, float partialTicks) {
        this.minecraft.getTextureManager().bindTexture(AbstractGui.BACKGROUND_LOCATION);
        int i = this.width;
        float f = -this.time * 0.5f * this.scrollSpeed;
        float f1 = (float)this.height - this.time * 0.5f * this.scrollSpeed;
        float f2 = 0.015625f;
        float f3 = this.time * 0.02f;
        float f4 = (float)(this.totalScrollLength + this.height + this.height + 24) / this.scrollSpeed;
        float f5 = (f4 - 20.0f - this.time) * 0.005f;
        if (f5 < f3) {
            f3 = f5;
        }
        if (f3 > 1.0f) {
            f3 = 1.0f;
        }
        f3 *= f3;
        f3 = f3 * 96.0f / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(0.0, this.height, this.getBlitOffset()).tex(0.0f, f * 0.015625f).color(f3, f3, f3, 1.0f).endVertex();
        bufferbuilder.pos(i, this.height, this.getBlitOffset()).tex((float)i * 0.015625f, f * 0.015625f).color(f3, f3, f3, 1.0f).endVertex();
        bufferbuilder.pos(i, 0.0, this.getBlitOffset()).tex((float)i * 0.015625f, f1 * 0.015625f).color(f3, f3, f3, 1.0f).endVertex();
        bufferbuilder.pos(0.0, 0.0, this.getBlitOffset()).tex(0.0f, f1 * 0.015625f).color(f3, f3, f3, 1.0f).endVertex();
        tessellator.draw();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.drawWinGameScreen(mouseX, mouseY, partialTicks);
        int i = 274;
        int j = this.width / 2 - 137;
        int k = this.height + 50;
        this.time += partialTicks;
        float f = -this.time * this.scrollSpeed;
        RenderSystem.pushMatrix();
        RenderSystem.translatef(0.0f, f, 0.0f);
        this.minecraft.getTextureManager().bindTexture(MINECRAFT_LOGO);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableAlphaTest();
        RenderSystem.enableBlend();
        this.blitBlackOutline(j, k, (p_238665_2_, p_238665_3_) -> {
            this.blit(matrixStack, p_238665_2_ + 0, (int)p_238665_3_, 0, 0, 155, 44);
            this.blit(matrixStack, p_238665_2_ + 155, (int)p_238665_3_, 0, 45, 155, 44);
        });
        RenderSystem.disableBlend();
        this.minecraft.getTextureManager().bindTexture(MINECRAFT_EDITION);
        WinGameScreen.blit(matrixStack, j + 88, k + 37, 0.0f, 0.0f, 98, 14, 128, 16);
        RenderSystem.disableAlphaTest();
        int l = k + 100;
        for (int i1 = 0; i1 < this.lines.size(); ++i1) {
            float f1;
            if (i1 == this.lines.size() - 1 && (f1 = (float)l + f - (float)(this.height / 2 - 6)) < 0.0f) {
                RenderSystem.translatef(0.0f, -f1, 0.0f);
            }
            if ((float)l + f + 12.0f + 8.0f > 0.0f && (float)l + f < (float)this.height) {
                IReorderingProcessor ireorderingprocessor = this.lines.get(i1);
                if (this.field_238664_v_.contains(i1)) {
                    this.font.func_238407_a_(matrixStack, ireorderingprocessor, j + (274 - this.font.func_243245_a(ireorderingprocessor)) / 2, l, 0xFFFFFF);
                } else {
                    this.font.random.setSeed((long)((float)((long)i1 * 4238972211L) + this.time / 4.0f));
                    this.font.func_238407_a_(matrixStack, ireorderingprocessor, j, l, 0xFFFFFF);
                }
            }
            l += 12;
        }
        RenderSystem.popMatrix();
        this.minecraft.getTextureManager().bindTexture(VIGNETTE_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR);
        int j1 = this.width;
        int k1 = this.height;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(0.0, k1, this.getBlitOffset()).tex(0.0f, 1.0f).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
        bufferbuilder.pos(j1, k1, this.getBlitOffset()).tex(1.0f, 1.0f).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
        bufferbuilder.pos(j1, 0.0, this.getBlitOffset()).tex(1.0f, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
        bufferbuilder.pos(0.0, 0.0, this.getBlitOffset()).tex(0.0f, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
        tessellator.draw();
        RenderSystem.disableBlend();
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
