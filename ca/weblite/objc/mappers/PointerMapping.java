package ca.weblite.objc.mappers;

import ca.weblite.objc.TypeMapping;
import com.sun.jna.Pointer;
import java.util.HashMap;
import java.util.Map;

public class PointerMapping
implements TypeMapping {
    Map<String, TypeMapping> mappers = new HashMap<String, TypeMapping>();

    public Object cToJ(Object cVar, String signature, TypeMapping root) {
        if (Pointer.class.isInstance(cVar)) {
            return cVar;
        }
        return new Pointer((Long)cVar);
    }

    public Object jToC(Object jVar, String signature, TypeMapping root) {
        return jVar;
    }
}
