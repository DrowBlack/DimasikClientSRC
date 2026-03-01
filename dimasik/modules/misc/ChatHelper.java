package dimasik.modules.misc;

import dimasik.Load;
import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.CheckboxOption;
import dimasik.utils.client.ChatUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.TextFormatting;

public class ChatHelper
extends Module {
    private final CheckboxOption notifyEffects = new CheckboxOption("\u041e\u043f\u043e\u0432\u0435\u0449\u0430\u0442\u044c \u043e \u044d\u0444\u0444\u0435\u043a\u0442\u0430\u0445", true);
    public final CheckboxOption notifyTotem = new CheckboxOption("\u041e\u043f\u043e\u0432\u0435\u0449\u0430\u0442\u044c \u043e \u043f\u043e\u0442\u0435\u0440\u0435 \u0442\u043e\u0442\u0435\u043c\u0430", true);
    private final Map<String, Map<Effect, Integer>> playerPotionEffects = new HashMap<String, Map<Effect, Integer>>();
    private static final Map<Effect, String> EFFECT_TRANSLATIONS = new HashMap<Effect, String>(){
        {
            this.put(Effects.SPEED, "Speed");
            this.put(Effects.SLOWNESS, "Slowness");
            this.put(Effects.STRENGTH, "Strength");
            this.put(Effects.INSTANT_HEALTH, "Instant Health");
            this.put(Effects.INSTANT_DAMAGE, "Instant Damage");
            this.put(Effects.JUMP_BOOST, "Jump Boost");
            this.put(Effects.NAUSEA, "Nausea");
            this.put(Effects.REGENERATION, "Regeneration");
            this.put(Effects.RESISTANCE, "Resistance");
            this.put(Effects.FIRE_RESISTANCE, "Fire Resistance");
            this.put(Effects.WATER_BREATHING, "Water Breathing");
            this.put(Effects.INVISIBILITY, "Invisibility");
            this.put(Effects.BLINDNESS, "Blindness");
            this.put(Effects.NIGHT_VISION, "Night Vision");
            this.put(Effects.HUNGER, "Hunger");
            this.put(Effects.WEAKNESS, "Weakness");
            this.put(Effects.POISON, "Poison");
            this.put(Effects.WITHER, "Wither");
            this.put(Effects.HEALTH_BOOST, "Health Boost");
            this.put(Effects.ABSORPTION, "Absorption");
            this.put(Effects.SATURATION, "Saturation");
            this.put(Effects.GLOWING, "Glowing");
            this.put(Effects.LEVITATION, "Levitation");
            this.put(Effects.LUCK, "Luck");
            this.put(Effects.UNLUCK, "Unluck");
            this.put(Effects.SLOW_FALLING, "Slow Falling");
            this.put(Effects.CONDUIT_POWER, "Conduit Power");
            this.put(Effects.DOLPHINS_GRACE, "Dolphin's Grace");
            this.put(Effects.BAD_OMEN, "Bad Omen");
            this.put(Effects.HERO_OF_THE_VILLAGE, "Hero of the Village");
        }
    };
    private final EventListener<EventUpdate> update = this::update;

    public ChatHelper() {
        super("ChatHelper", Category.MISC);
        this.settings(this.notifyEffects, this.notifyTotem);
    }

    public void update(EventUpdate event) {
        if (ChatHelper.mc.world == null || ChatHelper.mc.player == null) {
            return;
        }
        for (PlayerEntity playerEntity : ChatHelper.mc.world.getPlayers()) {
            String nameColor;
            if (playerEntity == ChatHelper.mc.player) continue;
            String name = playerEntity.getName().getString();
            boolean isFriend = Load.getInstance().getHooks().getFriendManagers().is(name);
            String string = nameColor = isFriend ? TextFormatting.YELLOW.toString() : TextFormatting.RED.toString();
            if (!((Boolean)this.notifyEffects.getValue()).booleanValue()) continue;
            Map prevEffects = this.playerPotionEffects.getOrDefault(name, new HashMap());
            HashMap<Effect, Integer> currentEffects = new HashMap<Effect, Integer>();
            ArrayList<String> newEffectStrings = new ArrayList<String>();
            for (EffectInstance effectInstance : playerEntity.getActivePotionEffects()) {
                Effect effect = effectInstance.getPotion();
                int level = effectInstance.getAmplifier() + 1;
                currentEffects.put(effect, level);
                if (prevEffects.containsKey(effect)) continue;
                String effectName = EFFECT_TRANSLATIONS.getOrDefault(effect, effect.getDisplayName().getString());
                String duration = this.formatDuration(effectInstance.getDuration());
                newEffectStrings.add(String.format("%s %d (%s)", effectName, level, duration));
            }
            if (!newEffectStrings.isEmpty()) {
                ChatUtils.addClientMessage(String.format("%s[%s] %s\u041f\u043e\u043b\u0443\u0447\u0438\u043b \u044d\u0444\u0444\u0435\u043a\u0442\u044b:", new Object[]{nameColor, name, TextFormatting.RED}));
                for (String effectText : newEffectStrings) {
                    ChatUtils.addClientMessage(String.valueOf((Object)TextFormatting.GRAY) + " - " + effectText);
                }
            }
            this.playerPotionEffects.put(name, currentEffects);
        }
    }

    private String formatDuration(int ticks) {
        int totalSeconds = ticks / 20;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return minutes > 0 ? String.format("%d\u043c %02d\u0441", minutes, seconds) : String.format("%d\u0441", seconds);
    }
}
