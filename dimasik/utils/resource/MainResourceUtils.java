package dimasik.utils.resource;

import dimasik.helpers.interfaces.IFastAccess;
import dimasik.utils.resource.MainPack;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;

public class MainResourceUtils
extends ResourceLocation
implements IFastAccess {
    private static final String NAMESPACE = "main";

    public MainResourceUtils(String pathIn) {
        super(NAMESPACE, pathIn);
    }

    public static void registerResources() {
        SimpleReloadableResourceManager resourceManager = (SimpleReloadableResourceManager)mc.getResourceManager();
        MainPack customResourcePack = new MainPack(NAMESPACE);
        MainPack customResourcePack2 = new MainPack("cpm");
        resourceManager.addResourcePack(customResourcePack);
        resourceManager.addResourcePack(customResourcePack2);
    }
}
