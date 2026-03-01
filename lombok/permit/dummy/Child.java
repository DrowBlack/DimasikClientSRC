package lombok.permit.dummy;

import lombok.permit.dummy.Parent;

public abstract class Child
extends Parent {
    private volatile transient boolean foo;
    private volatile transient Object[] bar;
    private volatile transient Object baz;
}
