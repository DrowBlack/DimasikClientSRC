package net.minecraft.inventory;

import javax.annotation.Nullable;

public interface IClearable {
    public void clear();

    public static void clearObj(@Nullable Object object) {
        if (object instanceof IClearable) {
            ((IClearable)object).clear();
        }
    }
}
