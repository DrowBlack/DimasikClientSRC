package dimasik.helpers.module.interfaces;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dimasik.Load;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.render.EventRender2D;
import dimasik.helpers.animation.Animation;
import dimasik.helpers.animation.EasingList;
import dimasik.helpers.render.ColorHelpers;
import dimasik.helpers.visual.StencilHelpers;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.managers.draggable.api.Component;
import dimasik.managers.module.option.main.CheckboxOption;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.renderer.texture.PotionSpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector4f;

public class PotionList
extends Component {
    private float width = 0.0f;
    private float widthName = 0.0f;
    private float widthTime = 0.0f;
    private float time = 0.0f;
    private final CheckboxOption hide = new CheckboxOption("Hide", true);

    public PotionList() {
        super("PotionList", new Vector2f(100.0f, 226.0f), 156.0f, 66.0f);
        this.getDraggableOption().settings(this.getDesign(), this.getCompression(), this.hide);
    }

    @Override
    public void update(EventUpdate event) {
        for (EffectInstance effect : PotionList.mc.player.getActivePotionEffects()) {
            effect.getAnimation().update(effect.getDuration() > 10);
        }
        boolean show = (!PotionList.mc.player.getActivePotionEffects().isEmpty() || PotionList.mc.currentScreen instanceof ChatScreen) && Load.getInstance().getHooks().getModuleManagers().getInterfaces().getElements().getSelected("PotionList") || (Boolean)this.hide.getValue() == false;
        this.getShowAnimation().update(show);
    }

    @Override
    public void render(EventRender2D.Pre event) {
        float x = ((Vector2f)this.getDraggableOption().getValue()).x;
        float y = ((Vector2f)this.getDraggableOption().getValue()).y;
        MatrixStack matrixStack = event.getMatrixStack();
        float staticWidth = 192.0f;
        float height = 46.0f;
        float staticTime = 10.0f;
        for (EffectInstance effect : PotionList.mc.player.getActivePotionEffects()) {
            height += 18.0f * effect.getAnimation().getAnimationValue();
            staticWidth = Math.max(staticWidth, sf_medium.getWidth(I18n.format(effect.getEffectName(), new Object[0]), 14.0f) + sf_medium.getWidth(EffectUtils.getPotionDurationString(effect, 1.0f), 14.0f));
            staticTime = Math.max(staticTime, sf_medium.getWidth(EffectUtils.getPotionDurationString(effect, 1.0f), 14.0f));
        }
        this.width = Animation.animate(this.width, staticWidth);
        this.time = Animation.animate(this.time, staticTime);
        this.getShowAnimation().animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, PotionList.mc.getTimer().renderPartialTicks);
        int back = ColorHelpers.rgba(15, 15, 15, 255.0f * this.getShowAnimation().getAnimationValue());
        int glow = ColorHelpers.setAlpha(ColorHelpers.getThemeColor(1), (int)(30.599999999999998 * (double)this.getShowAnimation().getAnimationValue()));
        int back2 = ColorHelpers.rgba(15, 15, 15, 30.599999999999998 * (double)this.getShowAnimation().getAnimationValue());
        int indicator = ColorHelpers.setAlpha(ColorHelpers.getThemeColor(1), (int)(255.0f * this.getShowAnimation().getAnimationValue()));
        if ((double)this.getShowAnimation().getAnimationValue() > 0.1) {
            if (this.getDesign().getSelected("Transparent")) {
                float finalHeight = height;
                BLUR_RUNNABLES.add(() -> VisualHelpers.drawRoundedRect(matrixStack, x, y, this.width, finalHeight, 12.0f, back));
                this.blurSetting(PotionList.mc.getTimer().renderPartialTicks, 10.0f, ((Float)this.getCompression().getValue()).floatValue());
                VisualHelpers.drawRoundedRect(matrixStack, x, y, this.width, height, 12.0f, ColorHelpers.getColorWithAlpha(ColorHelpers.getThemeColor(1), 40.800000000000004 * (double)this.getShowAnimation().getAnimationValue()));
            } else if (this.getDesign().getSelected("Standard")) {
                VisualHelpers.drawRoundedRect(matrixStack, x, y, this.width, height, 12.0f, back);
            }
            VisualHelpers.drawRoundedOutline(matrixStack, x, y, this.width, height, 12.0f, 2.0f, ColorHelpers.rgba(190, 190, 190, 15.299999999999999 * (double)this.getShowAnimation().getAnimationValue()));
            StencilHelpers.init();
            VisualHelpers.drawRoundedRect(matrixStack, x, y, this.width, height, 12.0f, -1);
            StencilHelpers.read(1);
            if (this.getDesign().getSelected("Standard")) {
                VisualHelpers.drawGlow(matrixStack, x, y, this.width, 192.0f, 40.0f, glow);
            }
            VisualHelpers.drawRoundedRect(matrixStack, x + this.width - 30.0f, y, 26.0f, 3.0f, new Vector4f(0.0f, 4.0f, 4.0f, 0.0f), indicator);
            suisse_intl.drawText(matrixStack, "Potions", x + 12.0f, y + 15.0f, ColorHelpers.rgba(255, 255, 255, (int)(255.0f * this.getShowAnimation().getAnimationValue())), 14.0f);
            dimasIcon.drawText(matrixStack, "O", x + this.width - 26.0f, y + 14.0f, ColorHelpers.setAlpha(ColorHelpers.getThemeColor(1), (int)(255.0f * this.getShowAnimation().getAnimationValue())), 12.0f);
            float i = 32.0f;
            for (EffectInstance effect : PotionList.mc.player.getActivePotionEffects()) {
                effect.getAnimation().animate(0.0f, 1.0f, 0.2f, EasingList.NONE, PotionList.mc.getTimer().renderPartialTicks);
                PotionSpriteUploader potionspriteuploader = mc.getPotionSpriteUploader();
                Effect effectStatis = effect.getPotion();
                TextureAtlasSprite textureatlassprite = potionspriteuploader.getSprite(effectStatis);
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                mc.getTextureManager().bindTexture(textureatlassprite.getAtlasTexture().getTextureLocation());
                DisplayEffectsScreen.blit(matrixStack, (int)(x + 12.0f), (int)(y + i + 10.0f), 10, 12, 12, textureatlassprite);
                if (!((double)effect.getAnimation().getAnimationValue() > 0.1)) continue;
                suisse_intl.drawText(matrixStack, I18n.format(effect.getEffectName(), new Object[0]), x + 29.0f, y + i + 10.0f, ColorHelpers.rgba(255, 255, 255, (int)(255.0f * effect.getAnimation().getAnimationValue() * this.getShowAnimation().getAnimationValue())), 12.0f);
                suisse_intl.drawText(matrixStack, EffectUtils.getPotionDurationString(effect, 1.0f), x + this.width - sf_medium.getWidth(EffectUtils.getPotionDurationString(effect, 1.0f), 14.0f) - 12.0f, y + i + 10.0f, indicator, 12.0f);
                suisse_intl.drawText(matrixStack, (String)(effect.getAmplifier() > 0 ? " " + (effect.getAmplifier() + 1) : ""), x + 29.0f + suisse_intl.getWidth(I18n.format(effect.getEffectName(), new Object[0]), 12.0f), y + i + 10.0f, ColorHelpers.rgba(255, 255, 255, (int)(183.6 * (double)effect.getAnimation().getAnimationValue() * (double)this.getShowAnimation().getAnimationValue())), 12.0f);
                i += 18.0f * effect.getAnimation().getAnimationValue();
            }
            StencilHelpers.uninit();
            this.getDraggableOption().setWidth(this.width);
            this.getDraggableOption().setHeight(height);
        }
    }
}
