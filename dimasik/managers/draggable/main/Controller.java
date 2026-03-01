package dimasik.managers.draggable.main;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.Load;
import dimasik.helpers.interfaces.IFastAccess;
import dimasik.managers.draggable.api.Component;
import dimasik.managers.module.option.api.Option;
import dimasik.managers.module.option.main.DraggableOption;

public class Controller
implements IFastAccess {
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialsTicks) {
        for (Component module : Load.getInstance().getHooks().getDraggableManagers()) {
            for (Option<?> option : module.getSettingList()) {
                DraggableOption draggableOption;
                if (!(option instanceof DraggableOption) || !(draggableOption = (DraggableOption)option).getVisible().getAsBoolean()) continue;
                draggableOption.draw(matrixStack, mouseX, mouseY, partialsTicks);
                if (!(draggableOption.getClickAnimation().getAnimationValue() > 0.1f)) continue;
                module.renderSettings(matrixStack, mouseX, mouseY, partialsTicks);
            }
        }
    }

    public void tick() {
        for (Component module : Load.getInstance().getHooks().getDraggableManagers()) {
            for (Option<?> option : module.getSettingList()) {
                DraggableOption draggableOption;
                if (!(option instanceof DraggableOption) || !((draggableOption = (DraggableOption)option).getClickAnimation().getAnimationValue() > 0.1f)) continue;
                module.updateSettings();
            }
        }
    }

    public void click(int mouseX, int mouseY, int button) {
        for (Component module : Load.getInstance().getHooks().getDraggableManagers()) {
            for (Option<?> option : module.getSettingList()) {
                if (!(option instanceof DraggableOption)) continue;
                DraggableOption draggableOption = (DraggableOption)option;
                if (draggableOption.getVisible().getAsBoolean() && button == 0) {
                    draggableOption.leftClick(mouseX, mouseY);
                }
                if (draggableOption.getVisible().getAsBoolean() && button == 1) {
                    draggableOption.rightClick(mouseX, mouseY);
                }
                module.clickSettings(mouseX, mouseY, button);
            }
        }
    }

    public void release(int mouseX, int mouseY, int button) {
        for (Component module : Load.getInstance().getHooks().getDraggableManagers()) {
            for (Option<?> option : module.getSettingList()) {
                if (!(option instanceof DraggableOption)) continue;
                DraggableOption draggableOption = (DraggableOption)option;
                module.releaseSettings(mouseX, mouseY, button);
            }
        }
    }

    public void release() {
        for (Component module : Load.getInstance().getHooks().getDraggableManagers()) {
            for (Option<?> option : module.getSettingList()) {
                DraggableOption draggableOption;
                if (!(option instanceof DraggableOption) || !(draggableOption = (DraggableOption)option).getVisible().getAsBoolean()) continue;
                draggableOption.setDrag(false);
            }
        }
    }

    public void translate() {
        for (Component module : Load.getInstance().getHooks().getDraggableManagers()) {
            for (Option<?> option : module.getSettingList()) {
                if (!(option instanceof DraggableOption)) continue;
                DraggableOption draggableOption = (DraggableOption)option;
                module.translate();
            }
        }
    }
}
