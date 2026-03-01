package lombok.installer.eclipse;

import java.util.Collections;
import lombok.installer.eclipse.EclipseProductDescriptor;
import lombok.installer.eclipse.EclipseProductLocationProvider;
import lombok.installer.eclipse.StandardProductDescriptor;

public class RhdsLocationProvider
extends EclipseProductLocationProvider {
    private static final EclipseProductDescriptor RHDS = new StandardProductDescriptor("Red Hat JBoss Developer Studio", "devstudio", "studio", RhdsLocationProvider.class.getResource("rhds.png"), Collections.<String>emptySet());

    public RhdsLocationProvider() {
        super(RHDS);
    }
}
