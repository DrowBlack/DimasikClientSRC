package dimasik.helpers.interfaces;

import dimasik.helpers.interfaces.IFont;

public interface IMouse
extends IFont {
    default public boolean isHover(float mouseX, float mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    default public boolean isHover(double mouseX, double mouseY, double x, double y, double width, double height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    default public boolean isLClick(int button) {
        return button == 0;
    }

    default public boolean isRClick(int button) {
        return button == 1;
    }

    default public boolean isMClick(int button) {
        return button == 2;
    }
}
