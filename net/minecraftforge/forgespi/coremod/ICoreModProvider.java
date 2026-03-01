package net.minecraftforge.forgespi.coremod;

import cpw.mods.modlauncher.api.ITransformer;
import java.util.List;
import net.minecraftforge.forgespi.coremod.ICoreModFile;

public interface ICoreModProvider {
    public void addCoreMod(ICoreModFile var1);

    public List<ITransformer<?>> getCoreModTransformers();
}
