package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.model.BookModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.EnchantmentContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnchantmentNameParts;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class EnchantmentScreen
extends ContainerScreen<EnchantmentContainer> {
    private static final ResourceLocation ENCHANTMENT_TABLE_GUI_TEXTURE = new ResourceLocation("textures/gui/container/enchanting_table.png");
    private static final ResourceLocation ENCHANTMENT_TABLE_BOOK_TEXTURE = new ResourceLocation("textures/entity/enchanting_table_book.png");
    private static final BookModel MODEL_BOOK = new BookModel();
    private final Random random = new Random();
    public int ticks;
    public float flip;
    public float oFlip;
    public float flipT;
    public float flipA;
    public float open;
    public float oOpen;
    private ItemStack last = ItemStack.EMPTY;

    public EnchantmentScreen(EnchantmentContainer container, PlayerInventory playerInventory, ITextComponent textComponent) {
        super(container, playerInventory, textComponent);
    }

    @Override
    public void tick() {
        super.tick();
        this.tickBook();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        for (int k = 0; k < 3; ++k) {
            double d0 = mouseX - (double)(i + 60);
            double d1 = mouseY - (double)(j + 14 + 19 * k);
            if (!(d0 >= 0.0) || !(d1 >= 0.0) || !(d0 < 108.0) || !(d1 < 19.0) || !((EnchantmentContainer)this.container).enchantItem(this.minecraft.player, k)) continue;
            this.minecraft.playerController.sendEnchantPacket(((EnchantmentContainer)this.container).windowId, k);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderHelper.setupGuiFlatDiffuseLighting();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(ENCHANTMENT_TABLE_GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
        RenderSystem.matrixMode(5889);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        int k = (int)this.minecraft.getMainWindow().getGuiScaleFactor();
        RenderSystem.viewport((this.width - 320) / 2 * k, (this.height - 240) / 2 * k, 320 * k, 240 * k);
        RenderSystem.translatef(-0.34f, 0.23f, 0.0f);
        RenderSystem.multMatrix(Matrix4f.perspective(90.0, 1.3333334f, 9.0f, 80.0f));
        RenderSystem.matrixMode(5888);
        matrixStack.push();
        MatrixStack.Entry matrixstack$entry = matrixStack.getLast();
        matrixstack$entry.getMatrix().setIdentity();
        matrixstack$entry.getNormal().setIdentity();
        matrixStack.translate(0.0, 3.3f, 1984.0);
        float f = 5.0f;
        matrixStack.scale(5.0f, 5.0f, 5.0f);
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(180.0f));
        matrixStack.rotate(Vector3f.XP.rotationDegrees(20.0f));
        float f1 = MathHelper.lerp(partialTicks, this.oOpen, this.open);
        matrixStack.translate((1.0f - f1) * 0.2f, (1.0f - f1) * 0.1f, (1.0f - f1) * 0.25f);
        float f2 = -(1.0f - f1) * 90.0f - 90.0f;
        matrixStack.rotate(Vector3f.YP.rotationDegrees(f2));
        matrixStack.rotate(Vector3f.XP.rotationDegrees(180.0f));
        float f3 = MathHelper.lerp(partialTicks, this.oFlip, this.flip) + 0.25f;
        float f4 = MathHelper.lerp(partialTicks, this.oFlip, this.flip) + 0.75f;
        f3 = (f3 - (float)MathHelper.fastFloor(f3)) * 1.6f - 0.3f;
        f4 = (f4 - (float)MathHelper.fastFloor(f4)) * 1.6f - 0.3f;
        if (f3 < 0.0f) {
            f3 = 0.0f;
        }
        if (f4 < 0.0f) {
            f4 = 0.0f;
        }
        if (f3 > 1.0f) {
            f3 = 1.0f;
        }
        if (f4 > 1.0f) {
            f4 = 1.0f;
        }
        RenderSystem.enableRescaleNormal();
        MODEL_BOOK.setBookState(0.0f, f3, f4, f1);
        IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
        IVertexBuilder ivertexbuilder = irendertypebuffer$impl.getBuffer(MODEL_BOOK.getRenderType(ENCHANTMENT_TABLE_BOOK_TEXTURE));
        MODEL_BOOK.render(matrixStack, ivertexbuilder, 0xF000F0, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        irendertypebuffer$impl.finish();
        matrixStack.pop();
        RenderSystem.matrixMode(5889);
        RenderSystem.viewport(0, 0, this.minecraft.getMainWindow().getFramebufferWidth(), this.minecraft.getMainWindow().getFramebufferHeight());
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(5888);
        RenderHelper.setupGui3DDiffuseLighting();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        EnchantmentNameParts.getInstance().reseedRandomGenerator(((EnchantmentContainer)this.container).func_217005_f());
        int l = ((EnchantmentContainer)this.container).getLapisAmount();
        for (int i1 = 0; i1 < 3; ++i1) {
            int j1 = i + 60;
            int k1 = j1 + 20;
            this.setBlitOffset(0);
            this.minecraft.getTextureManager().bindTexture(ENCHANTMENT_TABLE_GUI_TEXTURE);
            int l1 = ((EnchantmentContainer)this.container).enchantLevels[i1];
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            if (l1 == 0) {
                this.blit(matrixStack, j1, j + 14 + 19 * i1, 0, 185, 108, 19);
                continue;
            }
            String s = "" + l1;
            int i2 = 86 - this.font.getStringWidth(s);
            ITextProperties itextproperties = EnchantmentNameParts.getInstance().getGalacticEnchantmentName(this.font, i2);
            int j2 = 6839882;
            if (!(l >= i1 + 1 && this.minecraft.player.experienceLevel >= l1 || this.minecraft.player.abilities.isCreativeMode)) {
                this.blit(matrixStack, j1, j + 14 + 19 * i1, 0, 185, 108, 19);
                this.blit(matrixStack, j1 + 1, j + 15 + 19 * i1, 16 * i1, 239, 16, 16);
                this.font.func_238418_a_(itextproperties, k1, j + 16 + 19 * i1, i2, (j2 & 0xFEFEFE) >> 1);
                j2 = 4226832;
            } else {
                int k2 = x - (i + 60);
                int l2 = y - (j + 14 + 19 * i1);
                if (k2 >= 0 && l2 >= 0 && k2 < 108 && l2 < 19) {
                    this.blit(matrixStack, j1, j + 14 + 19 * i1, 0, 204, 108, 19);
                    j2 = 0xFFFF80;
                } else {
                    this.blit(matrixStack, j1, j + 14 + 19 * i1, 0, 166, 108, 19);
                }
                this.blit(matrixStack, j1 + 1, j + 15 + 19 * i1, 16 * i1, 223, 16, 16);
                this.font.func_238418_a_(itextproperties, k1, j + 16 + 19 * i1, i2, j2);
                j2 = 8453920;
            }
            this.font.drawStringWithShadow(matrixStack, s, (float)(k1 + 86 - this.font.getStringWidth(s)), (float)(j + 16 + 19 * i1 + 7), j2);
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        partialTicks = this.minecraft.getRenderPartialTicks();
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
        boolean flag = this.minecraft.player.abilities.isCreativeMode;
        int i = ((EnchantmentContainer)this.container).getLapisAmount();
        for (int j = 0; j < 3; ++j) {
            int k = ((EnchantmentContainer)this.container).enchantLevels[j];
            Enchantment enchantment = Enchantment.getEnchantmentByID(((EnchantmentContainer)this.container).enchantClue[j]);
            int l = ((EnchantmentContainer)this.container).worldClue[j];
            int i1 = j + 1;
            if (!this.isPointInRegion(60, 14 + 19 * j, 108, 17, mouseX, mouseY) || k <= 0 || l < 0 || enchantment == null) continue;
            ArrayList<ITextComponent> list = Lists.newArrayList();
            list.add(new TranslationTextComponent("container.enchant.clue", enchantment.getDisplayName(l)).mergeStyle(TextFormatting.WHITE));
            if (!flag) {
                list.add(StringTextComponent.EMPTY);
                if (this.minecraft.player.experienceLevel < k) {
                    list.add(new TranslationTextComponent("container.enchant.level.requirement", ((EnchantmentContainer)this.container).enchantLevels[j]).mergeStyle(TextFormatting.RED));
                } else {
                    TranslationTextComponent iformattabletextcomponent = i1 == 1 ? new TranslationTextComponent("container.enchant.lapis.one") : new TranslationTextComponent("container.enchant.lapis.many", i1);
                    list.add(iformattabletextcomponent.mergeStyle(i >= i1 ? TextFormatting.GRAY : TextFormatting.RED));
                    TranslationTextComponent iformattabletextcomponent1 = i1 == 1 ? new TranslationTextComponent("container.enchant.level.one") : new TranslationTextComponent("container.enchant.level.many", i1);
                    list.add(iformattabletextcomponent1.mergeStyle(TextFormatting.GRAY));
                }
            }
            this.func_243308_b(matrixStack, list, mouseX, mouseY);
            break;
        }
    }

    public void tickBook() {
        ItemStack itemstack = ((EnchantmentContainer)this.container).getSlot(0).getStack();
        if (!ItemStack.areItemStacksEqual(itemstack, this.last)) {
            this.last = itemstack;
            do {
                this.flipT += (float)(this.random.nextInt(4) - this.random.nextInt(4));
            } while (this.flip <= this.flipT + 1.0f && this.flip >= this.flipT - 1.0f);
        }
        ++this.ticks;
        this.oFlip = this.flip;
        this.oOpen = this.open;
        boolean flag = false;
        for (int i = 0; i < 3; ++i) {
            if (((EnchantmentContainer)this.container).enchantLevels[i] == 0) continue;
            flag = true;
        }
        this.open = flag ? (this.open += 0.2f) : (this.open -= 0.2f);
        this.open = MathHelper.clamp(this.open, 0.0f, 1.0f);
        float f1 = (this.flipT - this.flip) * 0.4f;
        float f = 0.2f;
        f1 = MathHelper.clamp(f1, -0.2f, 0.2f);
        this.flipA += (f1 - this.flipA) * 0.9f;
        this.flip += this.flipA;
    }
}
