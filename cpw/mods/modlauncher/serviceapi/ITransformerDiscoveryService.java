package cpw.mods.modlauncher.serviceapi;

import java.nio.file.Path;
import java.util.List;

public interface ITransformerDiscoveryService {
    public List<Path> candidates(Path var1);
}
