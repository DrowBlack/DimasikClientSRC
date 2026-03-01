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
import dimasik.managers.module.option.main.SelectOption;
import dimasik.managers.module.option.main.SelectOptionValue;
import dimasik.modules.player.FixHP;
import dimasik.utils.client.AnimationTest;
import dimasik.utils.client.Easing;
import dimasik.utils.time.TimerUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Score;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector4f;
import org.lwjgl.opengl.GL11;

public class TargetHud
extends Component {
    private float healthBar = 0.0f;
    private float healthBar2 = 0.0f;
    private float absortionBar = 0.0f;
    private float absortion2 = 0.0f;
    private final SelectOption modeHp = new SelectOption("HP Mode", 0, new SelectOptionValue("Theme client"), new SelectOptionValue("Health Color"));
    private LivingEntity target = null;
    private final Animation absorption = new Animation();
    private final TimerUtils timerUtils = new TimerUtils();
    AnimationTest animation4 = new AnimationTest(Easing.EASE_IN_OUT_CUBIC, 500L);
    private static float abs = 0.0f;
    private static float hpAn = 0.0f;

    public TargetHud() {
        super("TargetHud", new Vector2f(10.0f, 226.0f), 145.0f, 66.0f);
        this.getDraggableOption().settings(this.getDesign(), this.getCompression(), this.modeHp);
    }

    @Override
    public void update(EventUpdate event) {
        boolean update;
        if (Load.getInstance().getHooks().getModuleManagers().getAura().getTarget() != null && Load.getInstance().getHooks().getModuleManagers().getAura().getTarget() instanceof PlayerEntity) {
            this.target = Load.getInstance().getHooks().getModuleManagers().getAura().getTarget();
            update = true;
        } else if (TargetHud.mc.currentScreen instanceof ChatScreen) {
            this.target = TargetHud.mc.player;
            update = true;
        } else {
            update = false;
        }
        boolean updateAbsorption = this.target != null && this.target.getAbsorptionAmount() > 0.1f;
        boolean show = (Load.getInstance().getHooks().getModuleManagers().getAura().getTarget() != null || TargetHud.mc.currentScreen instanceof ChatScreen) && Load.getInstance().getHooks().getModuleManagers().getInterfaces().getElements().getSelected("TargetHud");
        this.getShowAnimation().update(update && Load.getInstance().getHooks().getModuleManagers().getInterfaces().getElements().getSelected("TargetHud"));
        this.absorption.update(updateAbsorption);
    }

    @Override
    public void render(EventRender2D.Pre event) {
        float x = ((Vector2f)this.getDraggableOption().getValue()).x;
        float y = ((Vector2f)this.getDraggableOption().getValue()).y;
        MatrixStack matrixStack = event.getMatrixStack();
        float width = 174.0f;
        float height = 62.0f;
        int back = ColorHelpers.rgba(15, 15, 15, 255.0f * this.getShowAnimation().getAnimationValue());
        int glow = ColorHelpers.setAlpha(ColorHelpers.getThemeColor(1), (int)(30.599999999999998 * (double)this.getShowAnimation().getAnimationValue()));
        int back2 = ColorHelpers.rgba(15, 15, 15, 30.599999999999998 * (double)this.getShowAnimation().getAnimationValue());
        int indicator = ColorHelpers.setAlpha(ColorHelpers.getThemeColor(1), (int)(255.0f * this.getShowAnimation().getAnimationValue()));
        this.getShowAnimation().animate(0.0f, 1.0f, 0.2f, EasingList.NONE, TargetHud.mc.getTimer().renderPartialTicks);
        if (this.getShowAnimation().getAnimationValue() == 0.0f) {
            this.target = null;
        }
        if (this.target != null && (double)this.getShowAnimation().getAnimationValue() > 0.1) {
            if (this.getDesign().getSelected("Transparent")) {
                BLUR_RUNNABLES.add(() -> VisualHelpers.drawRoundedRect(matrixStack, x, y, width, height, 12.0f, back));
                this.blurSetting(TargetHud.mc.getTimer().renderPartialTicks, 10.0f, ((Float)this.getCompression().getValue()).floatValue());
                VisualHelpers.drawRoundedRect(matrixStack, x, y, width, height, 12.0f, ColorHelpers.getColorWithAlpha(ColorHelpers.getThemeColor(1), 40.800000000000004 * (double)this.getShowAnimation().getAnimationValue()));
            } else if (this.getDesign().getSelected("Standard")) {
                VisualHelpers.drawRoundedRect(matrixStack, x, y, width, height, 12.0f, back);
            }
            VisualHelpers.drawRoundedOutline(matrixStack, x, y, width, height, 12.0f, 2.0f, ColorHelpers.rgba(190, 190, 190, 15.299999999999999 * (double)this.getShowAnimation().getAnimationValue()));
            StencilHelpers.init();
            VisualHelpers.drawRoundedRect(matrixStack, x, y, width, height, 12.0f, -1);
            StencilHelpers.read(1);
            if (this.getDesign().getSelected("Standard")) {
                VisualHelpers.drawGlow(matrixStack, x, y, width, 192.0f, 40.0f, glow);
            }
            StencilHelpers.uninit();
            VisualHelpers.drawRoundedRect(matrixStack, x + width - 30.0f, y, 26.0f, 3.0f, new Vector4f(0.0f, 4.0f, 4.0f, 0.0f), indicator);
            VisualHelpers.drawRoundedHead(matrixStack, x + 12.0f, y + 12.0f, 38.0f, 38.0f, 8.0f, this.getShowAnimation().getAnimationValue(), (AbstractClientPlayerEntity)this.target);
            float xHp = x + 60.0f;
            float yHp = y + 30.0f;
            float widthHp = 102.0f;
            float heightHp = 4.0f;
            float hp = this.target.getHealth();
            float maxHp = this.target.getMaxHealth();
            Score score = TargetHud.mc.world.getScoreboard().getOrCreateScore(this.target.getScoreboardName(), TargetHud.mc.world.getScoreboard().getObjectiveInDisplaySlot(2));
            if (mc.getCurrentServerData() != null) {
                String serverIP = TargetHud.mc.getCurrentServerData().serverIP;
                if (((FixHP)Load.getInstance().getHooks().getModuleManagers().findClass(FixHP.class)).isToggled() && this.target instanceof PlayerEntity) {
                    hp = score.getScorePoints();
                    maxHp = 20.0f;
                }
            }
            float finalHp = Math.min(hp, this.target.getMaxHealth());
            this.healthBar = Animation.animate(this.healthBar, finalHp / maxHp);
            this.healthBar = MathHelper.clamp(this.healthBar, 0.0f, 1.0f);
            this.absortionBar = Animation.animate(this.absortionBar, this.target.getAbsorptionAmount() / this.target.getMaxHealth());
            float absPrgs = this.absortionBar = MathHelper.clamp(this.absortionBar, 0.0f, 1.0f);
            float absStartX = xHp + widthHp * (1.0f - absPrgs);
            float absEndX = xHp + widthHp;
            float absWidth = absEndX - absStartX;
            VisualHelpers.drawRoundedRect(matrixStack, xHp, yHp, widthHp, heightHp, new Vector4f(2.0f, 2.0f, 2.0f, 2.0f), ColorHelpers.rgba(190, 190, 190, (int)(15.299999f * this.getShowAnimation().getAnimationValue())));
            VisualHelpers.drawRoundedRect(matrixStack, xHp, yHp, widthHp * this.healthBar, heightHp, new Vector4f(2.0f, 2.0f, 2.0f, 2.0f), ColorHelpers.setAlpha(ColorHelpers.getThemeColor(1), (int)(255.0f * this.getShowAnimation().getAnimationValue())));
            VisualHelpers.drawRoundedRect(matrixStack, absStartX, yHp, absWidth, heightHp, new Vector4f(2.0f, 2.0f, 2.0f, 2.0f), ColorHelpers.setAlpha(ColorHelpers.rgb(255, 235, 0), (int)(255.0f * this.getShowAnimation().getAnimationValue())));
            String targetName = this.target.getName().getString();
            String substring = targetName.substring(0, Math.min(targetName.length(), 8));
            StencilHelpers.init();
            VisualHelpers.drawRoundedRect(matrixStack, x - 3.0f, y - 3.0f, width, height + 6.0f, 10.0f, ColorHelpers.rgba(0, 0, 0, (int)(120.0f * this.getShowAnimation().getAnimationValue())));
            StencilHelpers.read(1);
            suisse_intl.drawText(matrixStack, substring, x + 60.0f, y + 12.0f, ColorHelpers.getColorWithAlpha(-1, this.getShowAnimation().getAnimationValue() * 255.0f), 12.0f);
            StencilHelpers.uninit();
            String HP = String.valueOf(hp);
            String targetHP = (float)((int)hp) > 900.0f ? "???" : HP.substring(0, Math.min(HP.length(), hp < 10.0f ? 1 : 2)) + "hp";
            suisse_intl.drawText(matrixStack, targetHP, x + 132.0f, y + 12.0f, ColorHelpers.getColorWithAlpha(indicator, 255.0f * this.getShowAnimation().getAnimationValue()), 12.0f);
            this.drawItemStack(x + 60.0f, y + 38.0f, 16.0f, this.getShowAnimation().getAnimationValue());
            this.getDraggableOption().setWidth(width);
            this.getDraggableOption().setHeight(height);
        }
    }

    private void drawItemStack(float x, float y, float offset, float scaleValue) {
        ArrayList<ItemStack> stackList = new ArrayList<ItemStack>(Arrays.asList(this.target.getHeldItemMainhand(), this.target.getHeldItemOffhand()));
        stackList.addAll((Collection)this.target.getArmorInventoryList());
        AtomicReference<Float> posX = new AtomicReference<Float>(Float.valueOf(x));
        stackList.stream().filter(stack -> !stack.isEmpty()).forEach(stack -> this.drawItemStack((ItemStack)stack, posX.getAndAccumulate(Float.valueOf(offset), Float::sum).floatValue(), y, scaleValue));
    }

    private void drawItemStack(ItemStack stack, float x, float y, float scaleValue) {
        RenderSystem.pushMatrix();
        RenderSystem.translatef(x, y, 0.0f);
        GL11.glScaled(scaleValue, scaleValue, scaleValue);
        mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, 0, 0);
        mc.getItemRenderer().renderItemOverlays(TargetHud.mc.fontRenderer, stack, 0, 0);
        RenderSystem.popMatrix();
    }
}
