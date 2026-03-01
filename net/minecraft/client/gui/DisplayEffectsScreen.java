package net.minecraft.client.gui;

import com.google.common.collect.Ordering;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.texture.PotionSpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.util.text.ITextComponent;

public abstract class DisplayEffectsScreen<T extends Container>
extends ContainerScreen<T> {
    protected boolean hasActivePotionEffects;

    public DisplayEffectsScreen(T screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void init() {
        super.init();
        this.updateActivePotionEffects();
    }

    protected void updateActivePotionEffects() {
        if (this.minecraft.player.getActivePotionEffects().isEmpty()) {
            this.guiLeft = (this.width - this.xSize) / 2;
            this.hasActivePotionEffects = false;
        } else {
            this.guiLeft = 160 + (this.width - this.xSize - 200) / 2;
            this.hasActivePotionEffects = true;
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        if (this.hasActivePotionEffects) {
            this.func_238811_b_(matrixStack);
        }
    }

    private void func_238811_b_(MatrixStack p_238811_1_) {
        int i = this.guiLeft - 124;
        Collection<EffectInstance> collection = this.minecraft.player.getActivePotionEffects();
        if (!collection.isEmpty()) {
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            int j = 33;
            if (collection.size() > 5) {
                j = 132 / (collection.size() - 1);
            }
            List<EffectInstance> iterable = Ordering.natural().sortedCopy(collection);
            this.func_238810_a_(p_238811_1_, i, j, iterable);
            this.func_238812_b_(p_238811_1_, i, j, iterable);
            this.func_238813_c_(p_238811_1_, i, j, iterable);
        }
    }

    private void func_238810_a_(MatrixStack p_238810_1_, int p_238810_2_, int p_238810_3_, Iterable<EffectInstance> p_238810_4_) {
        this.minecraft.getTextureManager().bindTexture(INVENTORY_BACKGROUND);
        int i = this.guiTop;
        for (EffectInstance effectinstance : p_238810_4_) {
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.blit(p_238810_1_, p_238810_2_, i, 0, 166, 140, 32);
            i += p_238810_3_;
        }
    }

    private void func_238812_b_(MatrixStack p_238812_1_, int p_238812_2_, int p_238812_3_, Iterable<EffectInstance> p_238812_4_) {
        PotionSpriteUploader potionspriteuploader = this.minecraft.getPotionSpriteUploader();
        int i = this.guiTop;
        for (EffectInstance effectinstance : p_238812_4_) {
            Effect effect = effectinstance.getPotion();
            TextureAtlasSprite textureatlassprite = potionspriteuploader.getSprite(effect);
            this.minecraft.getTextureManager().bindTexture(textureatlassprite.getAtlasTexture().getTextureLocation());
            DisplayEffectsScreen.blit(p_238812_1_, p_238812_2_ + 6, i + 7, this.getBlitOffset(), 18, 18, textureatlassprite);
            i += p_238812_3_;
        }
    }

    private void func_238813_c_(MatrixStack p_238813_1_, int p_238813_2_, int p_238813_3_, Iterable<EffectInstance> p_238813_4_) {
        int i = this.guiTop;
        for (EffectInstance effectinstance : p_238813_4_) {
            Object s = I18n.format(effectinstance.getPotion().getName(), new Object[0]);
            if (effectinstance.getAmplifier() >= 1 && effectinstance.getAmplifier() <= 9) {
                s = (String)s + " " + I18n.format("enchantment.level." + (effectinstance.getAmplifier() + 1), new Object[0]);
            }
            this.font.drawStringWithShadow(p_238813_1_, (String)s, (float)(p_238813_2_ + 10 + 18), (float)(i + 6), 0xFFFFFF);
            String s1 = EffectUtils.getPotionDurationString(effectinstance, 1.0f);
            this.font.drawStringWithShadow(p_238813_1_, s1, (float)(p_238813_2_ + 10 + 18), (float)(i + 6 + 10), 0x7F7F7F);
            i += p_238813_3_;
        }
    }
}
