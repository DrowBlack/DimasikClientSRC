package dimasik.managers.theme.api;

import dimasik.helpers.render.ColorHelpers;

public class Theme {
    public String name;
    public int[] colors;

    public Theme(String name, int ... colors) {
        this.name = name;
        this.colors = colors;
    }

    public int getColor(int index) {
        return ColorHelpers.gradient(5, index, this.colors);
    }
}
