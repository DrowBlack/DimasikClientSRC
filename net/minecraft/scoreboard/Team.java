package net.minecraft.scoreboard;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public abstract class Team {
    public boolean isSameTeam(@Nullable Team other) {
        if (other == null) {
            return false;
        }
        return this == other;
    }

    public abstract String getName();

    public abstract IFormattableTextComponent func_230427_d_(ITextComponent var1);

    public abstract boolean getSeeFriendlyInvisiblesEnabled();

    public abstract boolean getAllowFriendlyFire();

    public abstract Visible getNameTagVisibility();

    public abstract TextFormatting getColor();

    public abstract Collection<String> getMembershipCollection();

    public abstract Visible getDeathMessageVisibility();

    public abstract CollisionRule getCollisionRule();

    public static enum Visible {
        ALWAYS("always", 0),
        NEVER("never", 1),
        HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2),
        HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);

        private static final Map<String, Visible> nameMap;
        public final String internalName;
        public final int id;

        @Nullable
        public static Visible getByName(String nameIn) {
            return nameMap.get(nameIn);
        }

        private Visible(String nameIn, int idIn) {
            this.internalName = nameIn;
            this.id = idIn;
        }

        public ITextComponent getDisplayName() {
            return new TranslationTextComponent("team.visibility." + this.internalName);
        }

        static {
            nameMap = Arrays.stream(Visible.values()).collect(Collectors.toMap(p_199873_0_ -> p_199873_0_.internalName, p_199872_0_ -> p_199872_0_));
        }
    }

    public static enum CollisionRule {
        ALWAYS("always", 0),
        NEVER("never", 1),
        PUSH_OTHER_TEAMS("pushOtherTeams", 2),
        PUSH_OWN_TEAM("pushOwnTeam", 3);

        private static final Map<String, CollisionRule> nameMap;
        public final String name;
        public final int id;

        @Nullable
        public static CollisionRule getByName(String nameIn) {
            return nameMap.get(nameIn);
        }

        private CollisionRule(String nameIn, int idIn) {
            this.name = nameIn;
            this.id = idIn;
        }

        public ITextComponent getDisplayName() {
            return new TranslationTextComponent("team.collision." + this.name);
        }

        static {
            nameMap = Arrays.stream(CollisionRule.values()).collect(Collectors.toMap(p_199871_0_ -> p_199871_0_.name, p_199870_0_ -> p_199870_0_));
        }
    }
}
