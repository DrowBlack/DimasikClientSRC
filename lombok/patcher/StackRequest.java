package lombok.patcher;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum StackRequest {
    RETURN_VALUE(-1),
    THIS(-1),
    PARAM1(0),
    PARAM2(1),
    PARAM3(2),
    PARAM4(3),
    PARAM5(4),
    PARAM6(5);

    private final int paramPos;
    public static final List<StackRequest> PARAMS_IN_ORDER;

    static {
        PARAMS_IN_ORDER = Collections.unmodifiableList(Arrays.asList(PARAM1, PARAM2, PARAM3, PARAM4, PARAM5, PARAM6));
    }

    private StackRequest(int paramPos) {
        this.paramPos = paramPos;
    }

    public int getParamPos() {
        return this.paramPos;
    }
}
