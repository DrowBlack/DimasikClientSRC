package net.minecraftforge.forgespi.language;

import java.util.List;
import java.util.Optional;

public interface IConfigurable {
    public <T> Optional<T> getConfigElement(String ... var1);

    public List<? extends IConfigurable> getConfigList(String ... var1);
}
