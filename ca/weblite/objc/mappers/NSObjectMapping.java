package ca.weblite.objc.mappers;

import ca.weblite.objc.Peerable;
import ca.weblite.objc.Proxy;
import ca.weblite.objc.Recipient;
import ca.weblite.objc.Runtime;
import ca.weblite.objc.RuntimeUtils;
import ca.weblite.objc.TypeMapping;
import com.sun.jna.Pointer;
import com.sun.jna.PointerTool;

public class NSObjectMapping
implements TypeMapping {
    public Object cToJ(Object cVar, String signature, TypeMapping root) {
        Pointer cObj = Pointer.NULL;
        if (Pointer.class.isInstance(cVar)) {
            cObj = (Pointer)cVar;
        } else if (Long.TYPE.isInstance(cVar) || Long.class.isInstance(cVar)) {
            cObj = new Pointer((Long)cVar);
        } else {
            return cVar;
        }
        if (Pointer.NULL == cObj || cVar == null || cObj == null || PointerTool.getPeer(cObj) == 0L) {
            return null;
        }
        String clsName = Runtime.INSTANCE.object_getClassName(cObj);
        boolean isString = false;
        if ("NSString".equals(clsName) || "__NSCFString".equals(clsName)) {
            isString = true;
        }
        if (isString) {
            return RuntimeUtils.str(cObj);
        }
        Recipient peer = RuntimeUtils.getJavaPeer(PointerTool.getPeer(cObj));
        if (peer == null) {
            return Proxy.load(cObj);
        }
        return peer;
    }

    public Object jToC(Object jVar, String signature, TypeMapping root) {
        if (jVar == null) {
            return Pointer.NULL;
        }
        if (String.class.isInstance(jVar)) {
            return RuntimeUtils.str((String)jVar);
        }
        if (Peerable.class.isInstance(jVar)) {
            return ((Peerable)jVar).getPeer();
        }
        return (Pointer)jVar;
    }
}
