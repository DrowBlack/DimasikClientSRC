package lombok.installer.eclipse;

import java.util.Arrays;
import java.util.Collections;
import lombok.installer.eclipse.EclipseProductDescriptor;
import lombok.installer.eclipse.EclipseProductLocationProvider;
import lombok.installer.eclipse.StandardProductDescriptor;

public class STS4LocationProvider
extends EclipseProductLocationProvider {
    private static final EclipseProductDescriptor STS4 = new StandardProductDescriptor("Spring Tools Suite 4", "SpringToolSuite4", "sts", STS4LocationProvider.class.getResource("STS.png"), Collections.unmodifiableList(Arrays.asList("springsource", "spring-tool-suite")));

    public STS4LocationProvider() {
        super(STS4);
    }
}
