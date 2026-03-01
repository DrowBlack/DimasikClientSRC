package lombok.installer.eclipse;

import java.util.Collections;
import lombok.installer.eclipse.EclipseProductDescriptor;
import lombok.installer.eclipse.EclipseProductLocationProvider;
import lombok.installer.eclipse.StandardProductDescriptor;

public class AngularIDELocationProvider
extends EclipseProductLocationProvider {
    private static final EclipseProductDescriptor ANGULAR = new StandardProductDescriptor("Angular IDE", "angularide", "angular", AngularIDELocationProvider.class.getResource("angular.png"), Collections.<String>emptySet());

    public AngularIDELocationProvider() {
        super(ANGULAR);
    }
}
