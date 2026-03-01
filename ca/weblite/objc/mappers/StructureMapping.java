package ca.weblite.objc.mappers;

import ca.weblite.objc.TypeMapping;

public class StructureMapping
implements TypeMapping {
    public Object cToJ(Object cVar, String signature, TypeMapping root) {
        return cVar;
    }

    public Object jToC(Object jVar, String signature, TypeMapping root) {
        return jVar;
    }
}
