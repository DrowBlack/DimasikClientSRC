package lombok.installer;

import java.util.List;
import java.util.regex.Pattern;
import lombok.installer.CorruptedIdeLocationException;
import lombok.installer.IdeLocation;

public interface IdeLocationProvider {
    public IdeLocation create(String var1) throws CorruptedIdeLocationException;

    public Pattern getLocationSelectors();

    public void findIdes(List<IdeLocation> var1, List<CorruptedIdeLocationException> var2);
}
