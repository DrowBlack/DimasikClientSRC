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
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector4f;

public class Cooldowns
extends Component {
    private float width = 0.0f;
    private float time = 0.0f;
    private final CheckboxOption hide = new CheckboxOption("Hide", true);
    private final Map<Item, Float> lastFraction = new HashMap<Item, Float>();
    private final Map<Item, Long> lastTimeMs = new HashMap<Item, Long>();
    private final Map<Item, Float> estimatedTotalSeconds = new HashMap<Item, Float>();
    private final Map<Item, Float> slopeEma = new HashMap<Item, Float>();
    private final Map<Item, Float> lastSecondsLeft = new HashMap<Item, Float>();
    private final Map<Item, Integer> lastCreateTicks = new HashMap<Item, Integer>();
    private final Map<Item, Integer> lastExpireTicks = new HashMap<Item, Integer>();

    public Cooldowns() {
        super("Cooldowns", new Vector2f(170.0f, 326.0f), 156.0f, 66.0f);
        this.getDraggableOption().settings(this.getDesign(), this.getCompression(), this.hide);
    }

    @Override
    public void update(EventUpdate event) {
        for (EffectInstance effect : Cooldowns.mc.player.getActivePotionEffects()) {
            effect.getAnimation().update(effect.getDuration() > 10);
        }
        boolean show = Cooldowns.mc.currentScreen instanceof ChatScreen && Load.getInstance().getHooks().getModuleManagers().getInterfaces().getElements().getSelected("Cooldowns") || (Boolean)this.hide.getValue() == false;
        for (int i = 0; i < Cooldowns.mc.player.inventory.getSizeInventory(); ++i) {
            ItemStack itemStack = Cooldowns.mc.player.inventory.getStackInSlot(i);
            if (itemStack.isEmpty() || !Cooldowns.mc.player.getCooldownTracker().hasCooldown(itemStack.getItem())) continue;
            show = Load.getInstance().getHooks().getModuleManagers().getInterfaces().getElements().getSelected("Cooldowns");
        }
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
        HashSet<Item> activeCooldowns = new HashSet<Item>();
        for (int i = 0; i < Cooldowns.mc.player.inventory.getSizeInventory(); ++i) {
            ItemStack itemStack = Cooldowns.mc.player.inventory.getStackInSlot(i);
            if (itemStack.isEmpty() || !Cooldowns.mc.player.getCooldownTracker().hasCooldown(itemStack.getItem()) || activeCooldowns.contains(itemStack.getItem())) continue;
            String itemName = itemStack.getItem().getName().getString();
            float cooldown = this.formtime(itemStack.getItem());
            height += 18.0f;
            staticWidth = Math.max(staticWidth, sf_medium.getWidth(itemName, 14.0f) + sf_medium.getWidth(cooldown + "s", 14.0f) + 30.0f);
            staticTime = Math.max(staticTime, sf_medium.getWidth(cooldown + "s", 14.0f));
        }
        this.width = Animation.animate(this.width, staticWidth);
        this.time = Animation.animate(this.time, staticTime);
        this.getShowAnimation().animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, Cooldowns.mc.getTimer().renderPartialTicks);
        int back = ColorHelpers.rgba(15, 15, 15, 255.0f * this.getShowAnimation().getAnimationValue());
        int glow = ColorHelpers.setAlpha(ColorHelpers.getThemeColor(1), (int)(30.599999999999998 * (double)this.getShowAnimation().getAnimationValue()));
        int back2 = ColorHelpers.rgba(15, 15, 15, 30.599999999999998 * (double)this.getShowAnimation().getAnimationValue());
        int indicator = ColorHelpers.setAlpha(ColorHelpers.getThemeColor(1), (int)(255.0f * this.getShowAnimation().getAnimationValue()));
        if ((double)this.getShowAnimation().getAnimationValue() > 0.1) {
            if (this.getDesign().getSelected("Transparent")) {
                float finalHeight = height;
                BLUR_RUNNABLES.add(() -> VisualHelpers.drawRoundedRect(matrixStack, x, y, this.width, finalHeight, 12.0f, back));
                this.blurSetting(Cooldowns.mc.getTimer().renderPartialTicks, 10.0f, ((Float)this.getCompression().getValue()).floatValue());
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
            suisse_intl.drawText(matrixStack, "Cooldowns", x + 12.0f, y + 15.0f, ColorHelpers.rgba(255, 255, 255, (int)(255.0f * this.getShowAnimation().getAnimationValue())), 14.0f);
            nursultan.drawText(matrixStack, "F", x + this.width - 26.0f, y + 14.0f, ColorHelpers.setAlpha(ColorHelpers.getThemeColor(1), (int)(255.0f * this.getShowAnimation().getAnimationValue())), 12.0f);
            float i2 = 32.0f;
            for (int i = 0; i < Cooldowns.mc.player.inventory.getSizeInventory(); ++i) {
                ItemStack itemStack = Cooldowns.mc.player.inventory.getStackInSlot(i);
                if (itemStack.isEmpty() || !Cooldowns.mc.player.getCooldownTracker().hasCooldown(itemStack.getItem()) || activeCooldowns.contains(itemStack.getItem())) continue;
                itemStack.getAnimation().animate(0.0f, 1.0f, 0.2f, EasingList.NONE, Cooldowns.mc.getTimer().renderPartialTicks);
                String itemName = itemStack.getItem().getName().getString();
                float cooldown = this.formtime(itemStack.getItem());
                activeCooldowns.add(itemStack.getItem());
                this.drawItemStack(itemStack, x + 8.0f, y + i2 + 8.0f, 16.0, 16.0);
                suisse_intl.drawText(matrixStack, itemName, x + 29.0f, y + i2 + 10.0f, ColorHelpers.rgba(255, 255, 255, (int)(255.0f * this.getShowAnimation().getAnimationValue())), 12.0f);
                suisse_intl.drawText(matrixStack, cooldown + "s", x + this.width - sf_medium.getWidth(cooldown + "s", 14.0f) - 12.0f, y + i2 + 10.0f, indicator, 12.0f);
                i2 += 18.0f;
            }
            StencilHelpers.uninit();
            this.getDraggableOption().setWidth(this.width);
            this.getDraggableOption().setHeight(height);
        }
    }

    public void drawItemStack(ItemStack stack, double x, double y, double width, double height) {
        RenderSystem.translated(x, y, 0.0);
        double scaleX = width / 16.0;
        double scaleY = height / 16.0;
        RenderSystem.scaled(scaleX, scaleY, 1.0);
        mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, 0, 0);
        RenderSystem.scaled(1.0 / scaleX, 1.0 / scaleY, 1.0);
        RenderSystem.translated(-x, -y, 0.0);
    }

    private float formtime(Item item) {
        Minecraft mc = Minecraft.getInstance();
        try {
            CooldownTracker tracker = mc.player.getCooldownTracker();
            Class<?> clazz = tracker.getClass();
            Field fCooldowns = clazz.getDeclaredField("cooldowns");
            fCooldowns.setAccessible(true);
            Map map = (Map)fCooldowns.get(tracker);
            Object entry = map.get(item);
            if (entry == null) {
                this.lastFraction.remove(item);
                this.lastTimeMs.remove(item);
                this.estimatedTotalSeconds.remove(item);
                this.slopeEma.remove(item);
                this.lastSecondsLeft.remove(item);
                this.lastCreateTicks.remove(item);
                this.lastExpireTicks.remove(item);
                return 0.0f;
            }
            Field fExpire = entry.getClass().getDeclaredField("expireTicks");
            Field fCreate = entry.getClass().getDeclaredField("createTicks");
            fExpire.setAccessible(true);
            fCreate.setAccessible(true);
            int expire = (Integer)fExpire.get(entry);
            int create = (Integer)fCreate.get(entry);
            Field fTicks = clazz.getDeclaredField("ticks");
            fTicks.setAccessible(true);
            int ticks = (Integer)fTicks.get(tracker);
            float totalTicks = Math.max(1.0f, (float)(expire - create));
            float remainingTicks = Math.max(0.0f, (float)expire - ((float)ticks + mc.getTimer().renderPartialTicks));
            float secondsLeft = remainingTicks / 20.0f;
            float frac = mc.player.getCooldownTracker().getCooldown(item, mc.getTimer().renderPartialTicks);
            if (secondsLeft < 0.05f && frac > 0.0f) {
                secondsLeft = Math.max(secondsLeft, frac * totalTicks / 20.0f);
            }
            boolean isNewCooldown = !this.lastExpireTicks.containsKey(item) || !this.lastCreateTicks.containsKey(item) || !this.lastExpireTicks.get(item).equals(expire) || !this.lastCreateTicks.get(item).equals(create);
            Float prevLeft = this.lastSecondsLeft.get(item);
            if (isNewCooldown) {
                prevLeft = null;
            }
            if (prevLeft != null) {
                long now = System.currentTimeMillis();
                long prev = this.lastTimeMs.getOrDefault(item, now);
                float dt = Math.max(0.0f, (float)(now - prev) / 1000.0f);
                secondsLeft = Math.min(secondsLeft, Math.max(0.0f, prevLeft.floatValue() - dt));
            }
            this.lastSecondsLeft.put(item, Float.valueOf(secondsLeft));
            this.lastTimeMs.put(item, System.currentTimeMillis());
            this.lastCreateTicks.put(item, create);
            this.lastExpireTicks.put(item, expire);
            return Math.max(0.0f, (float)Math.round(secondsLeft * 10.0f) / 10.0f);
        }
        catch (Throwable t) {
            float rc = mc.player.getCooldownTracker().getCooldown(item, mc.getTimer().renderPartialTicks);
            if (rc <= 0.0f) {
                return 0.0f;
            }
            Float prevLeft = this.lastSecondsLeft.get(item);
            if (prevLeft == null) {
                prevLeft = Float.valueOf(rc * 5.0f);
            }
            long now = System.currentTimeMillis();
            long prev = this.lastTimeMs.getOrDefault(item, now);
            float dt = Math.max(0.0f, (float)(now - prev) / 1000.0f);
            float secondsLeft = Math.max(0.0f, prevLeft.floatValue() - dt);
            this.lastSecondsLeft.put(item, Float.valueOf(secondsLeft));
            this.lastTimeMs.put(item, now);
            return Math.max(0.0f, (float)Math.round(secondsLeft * 10.0f) / 10.0f);
        }
    }
}
