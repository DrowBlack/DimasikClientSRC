package ca.weblite.objc.mappers;

import ca.weblite.objc.TypeMapping;
import com.sun.jna.Pointer;

public class StringMapping
implements TypeMapping {
    public Object cToJ(Object cVar, String signature, TypeMapping root) {
        System.out.println("Mapping string from cVar");
        return new Pointer((Long)cVar).getString(0L);
    }

    public Object jToC(Object jVar, String signature, TypeMapping root) {
        return jVar;
    }
}
