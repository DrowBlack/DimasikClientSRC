package ca.weblite.objc.mappers;

import ca.weblite.objc.TypeMapping;

public class ScalarMapping
implements TypeMapping {
    public Object cToJ(Object cVar, String signature, TypeMapping root) {
        char firstChar = signature.charAt(0);
        if (Long.class.isInstance(cVar) || Long.TYPE.isInstance(cVar)) {
            long cObj = (Long)cVar;
            switch (firstChar) {
                case 'I': 
                case 'S': 
                case 'i': 
                case 's': {
                    return new Long(cObj).intValue();
                }
                case 'c': {
                    return new Long(cObj).byteValue();
                }
                case 'B': {
                    return cObj > 0L;
                }
            }
        }
        return cVar;
    }

    public Object jToC(Object jVar, String signature, TypeMapping root) {
        return jVar;
    }
}
