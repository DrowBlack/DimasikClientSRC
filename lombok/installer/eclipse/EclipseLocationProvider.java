package lombok.installer.eclipse;

import java.util.Collections;
import lombok.installer.eclipse.EclipseProductDescriptor;
import lombok.installer.eclipse.EclipseProductLocationProvider;
import lombok.installer.eclipse.StandardProductDescriptor;

public class EclipseLocationProvider
extends EclipseProductLocationProvider {
    private static final EclipseProductDescriptor ECLIPSE = new StandardProductDescriptor("Eclipse", "eclipse", "eclipse", EclipseLocationProvider.class.getResource("eclipse.png"), Collections.<String>emptySet());

    public EclipseLocationProvider() {
        super(ECLIPSE);
    }
}
