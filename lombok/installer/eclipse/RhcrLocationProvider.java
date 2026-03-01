package lombok.installer.eclipse;

import java.util.Collections;
import lombok.installer.eclipse.EclipseProductDescriptor;
import lombok.installer.eclipse.EclipseProductLocationProvider;
import lombok.installer.eclipse.StandardProductDescriptor;

public class RhcrLocationProvider
extends EclipseProductLocationProvider {
    private static final EclipseProductDescriptor RHCR = new StandardProductDescriptor("Red Hat CodeReady Studio", "codereadystudio", "studio", RhcrLocationProvider.class.getResource("rhds.png"), Collections.<String>emptySet());

    public RhcrLocationProvider() {
        super(RHCR);
    }
}
