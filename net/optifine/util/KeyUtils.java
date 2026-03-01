package net.optifine.util;

import java.util.Arrays;
import java.util.HashSet;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;

public class KeyUtils {
    public static void fixKeyConflicts(KeyBinding[] keys, KeyBinding[] keysPrio) {
        HashSet<String> set = new HashSet<String>();
        for (int i = 0; i < keysPrio.length; ++i) {
            KeyBinding keybinding = keysPrio[i];
            set.add(keybinding.getTranslationKey());
        }
        HashSet<KeyBinding> set1 = new HashSet<KeyBinding>(Arrays.asList(keys));
        set1.removeAll(Arrays.asList(keysPrio));
        for (KeyBinding keybinding1 : set1) {
            String s = keybinding1.getTranslationKey();
            if (!set.contains(s)) continue;
            keybinding1.bind(InputMappings.INPUT_INVALID);
        }
    }
}
