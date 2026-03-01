package net.minecraft.client.tutorial;

import java.util.function.Function;
import net.minecraft.client.tutorial.CompletedTutorialStep;
import net.minecraft.client.tutorial.CraftPlanksStep;
import net.minecraft.client.tutorial.FindTreeStep;
import net.minecraft.client.tutorial.ITutorialStep;
import net.minecraft.client.tutorial.MovementStep;
import net.minecraft.client.tutorial.OpenInventoryStep;
import net.minecraft.client.tutorial.PunchTreeStep;
import net.minecraft.client.tutorial.Tutorial;

public enum TutorialSteps {
    MOVEMENT("movement", MovementStep::new),
    FIND_TREE("find_tree", FindTreeStep::new),
    PUNCH_TREE("punch_tree", PunchTreeStep::new),
    OPEN_INVENTORY("open_inventory", OpenInventoryStep::new),
    CRAFT_PLANKS("craft_planks", CraftPlanksStep::new),
    NONE("none", CompletedTutorialStep::new);

    private final String name;
    private final Function<Tutorial, ? extends ITutorialStep> tutorial;

    private <T extends ITutorialStep> TutorialSteps(String nameIn, Function<Tutorial, T> constructor) {
        this.name = nameIn;
        this.tutorial = constructor;
    }

    public ITutorialStep create(Tutorial tutorial) {
        return this.tutorial.apply(tutorial);
    }

    public String getName() {
        return this.name;
    }

    public static TutorialSteps byName(String name) {
        for (TutorialSteps tutorialsteps : TutorialSteps.values()) {
            if (!tutorialsteps.name.equals(name)) continue;
            return tutorialsteps;
        }
        return NONE;
    }
}
