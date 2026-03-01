package net.minecraft.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.util.ResourceLocation;

public interface ICriterionTrigger<T extends ICriterionInstance> {
    public ResourceLocation getId();

    public void addListener(PlayerAdvancements var1, Listener<T> var2);

    public void removeListener(PlayerAdvancements var1, Listener<T> var2);

    public void removeAllListeners(PlayerAdvancements var1);

    public T deserialize(JsonObject var1, ConditionArrayParser var2);

    public static class Listener<T extends ICriterionInstance> {
        private final T criterionInstance;
        private final Advancement advancement;
        private final String criterionName;

        public Listener(T criterionInstanceIn, Advancement advancementIn, String criterionNameIn) {
            this.criterionInstance = criterionInstanceIn;
            this.advancement = advancementIn;
            this.criterionName = criterionNameIn;
        }

        public T getCriterionInstance() {
            return this.criterionInstance;
        }

        public void grantCriterion(PlayerAdvancements playerAdvancementsIn) {
            playerAdvancementsIn.grantCriterion(this.advancement, this.criterionName);
        }

        public boolean equals(Object p_equals_1_) {
            if (this == p_equals_1_) {
                return true;
            }
            if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
                Listener listener = (Listener)p_equals_1_;
                if (!this.criterionInstance.equals(listener.criterionInstance)) {
                    return false;
                }
                return !this.advancement.equals(listener.advancement) ? false : this.criterionName.equals(listener.criterionName);
            }
            return false;
        }

        public int hashCode() {
            int i = this.criterionInstance.hashCode();
            i = 31 * i + this.advancement.hashCode();
            return 31 * i + this.criterionName.hashCode();
        }
    }
}
