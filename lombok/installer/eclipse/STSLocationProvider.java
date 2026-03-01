package lombok.installer.eclipse;

import java.util.Collections;
import lombok.installer.eclipse.EclipseProductDescriptor;
import lombok.installer.eclipse.EclipseProductLocationProvider;
import lombok.installer.eclipse.StandardProductDescriptor;

public class STSLocationProvider
extends EclipseProductLocationProvider {
    private static final EclipseProductDescriptor STS = new StandardProductDescriptor("STS", "STS", "sts", STSLocationProvider.class.getResource("STS.png"), Collections.singleton("springsource"));

    public STSLocationProvider() {
        super(STS);
    }
}
