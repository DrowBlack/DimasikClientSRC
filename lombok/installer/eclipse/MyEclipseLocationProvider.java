package lombok.installer.eclipse;

import java.util.Collections;
import lombok.installer.eclipse.EclipseProductDescriptor;
import lombok.installer.eclipse.EclipseProductLocationProvider;
import lombok.installer.eclipse.StandardProductDescriptor;

public class MyEclipseLocationProvider
extends EclipseProductLocationProvider {
    private static final EclipseProductDescriptor MY_ECLIPSE = new StandardProductDescriptor("MyEclipse", "myeclipse", "myeclipse", MyEclipseLocationProvider.class.getResource("myeclipse.png"), Collections.<String>emptySet());

    public MyEclipseLocationProvider() {
        super(MY_ECLIPSE);
    }
}
