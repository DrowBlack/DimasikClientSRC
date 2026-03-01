package net.optifine.shaders;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.config.ConnectedParser;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import net.optifine.shaders.IShaderPack;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.config.MacroProcessor;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.StrUtils;

public class ItemAliases {
    private static int[] itemAliases = null;
    private static boolean updateOnResourcesReloaded;

    public static int getItemAliasId(int itemId) {
        if (itemAliases == null) {
            return -1;
        }
        return itemId >= 0 && itemId < itemAliases.length ? itemAliases[itemId] : -1;
    }

    public static void resourcesReloaded() {
        if (updateOnResourcesReloaded) {
            updateOnResourcesReloaded = false;
            ItemAliases.update(Shaders.getShaderPack());
        }
    }

    public static void update(IShaderPack shaderPack) {
        ItemAliases.reset();
        if (shaderPack != null) {
            if (Reflector.Loader_getActiveModList.exists() && Minecraft.getInstance().getResourceManager() == null) {
                Config.dbg("[Shaders] Delayed loading of item mappings after resources are loaded");
                updateOnResourcesReloaded = true;
            } else {
                ArrayList<Integer> list = new ArrayList<Integer>();
                String s = "/shaders/item.properties";
                InputStream inputstream = shaderPack.getResourceAsStream(s);
                if (inputstream != null) {
                    ItemAliases.loadItemAliases(inputstream, s, list);
                }
                ItemAliases.loadModItemAliases(list);
                if (list.size() > 0) {
                    itemAliases = ItemAliases.toArray(list);
                }
            }
        }
    }

    private static void loadModItemAliases(List<Integer> listItemAliases) {
        String[] astring = ReflectorForge.getForgeModIds();
        for (int i = 0; i < astring.length; ++i) {
            String s = astring[i];
            try {
                ResourceLocation resourcelocation = new ResourceLocation(s, "shaders/item.properties");
                InputStream inputstream = Config.getResourceStream(resourcelocation);
                ItemAliases.loadItemAliases(inputstream, resourcelocation.toString(), listItemAliases);
                continue;
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    private static void loadItemAliases(InputStream in, String path, List<Integer> listItemAliases) {
        if (in != null) {
            try {
                in = MacroProcessor.process(in, path, true);
                PropertiesOrdered properties = new PropertiesOrdered();
                properties.load(in);
                in.close();
                Config.dbg("[Shaders] Parsing item mappings: " + path);
                ConnectedParser connectedparser = new ConnectedParser("Shaders");
                for (String string : ((Properties)properties).keySet()) {
                    String s1 = properties.getProperty(string);
                    String s2 = "item.";
                    if (!string.startsWith(s2)) {
                        Config.warn("[Shaders] Invalid item ID: " + string);
                        continue;
                    }
                    String s3 = StrUtils.removePrefix(string, s2);
                    int i = Config.parseInt(s3, -1);
                    if (i < 0) {
                        Config.warn("[Shaders] Invalid item alias ID: " + i);
                        continue;
                    }
                    int[] aint = connectedparser.parseItems(s1);
                    if (aint != null && aint.length >= 1) {
                        for (int j = 0; j < aint.length; ++j) {
                            int k = aint[j];
                            ItemAliases.addToList(listItemAliases, k, i);
                        }
                        continue;
                    }
                    Config.warn("[Shaders] Invalid item ID mapping: " + string + "=" + s1);
                }
            }
            catch (IOException ioexception) {
                Config.warn("[Shaders] Error reading: " + path);
            }
        }
    }

    private static void addToList(List<Integer> list, int index, int val) {
        while (list.size() <= index) {
            list.add(-1);
        }
        list.set(index, val);
    }

    private static int[] toArray(List<Integer> list) {
        int[] aint = new int[list.size()];
        for (int i = 0; i < aint.length; ++i) {
            aint[i] = list.get(i);
        }
        return aint;
    }

    public static void reset() {
        itemAliases = null;
    }
}
