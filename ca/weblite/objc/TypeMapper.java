package ca.weblite.objc;

import ca.weblite.objc.TypeMapping;
import ca.weblite.objc.mappers.NSObjectMapping;
import ca.weblite.objc.mappers.PointerMapping;
import ca.weblite.objc.mappers.ScalarMapping;
import ca.weblite.objc.mappers.StringMapping;
import ca.weblite.objc.mappers.StructureMapping;
import java.util.HashMap;
import java.util.Map;

public class TypeMapper
implements TypeMapping {
    static TypeMapper instance;
    Map<String, TypeMapping> mappers = new HashMap<String, TypeMapping>();

    public static TypeMapper getInstance() {
        if (instance == null) {
            instance = new TypeMapper();
        }
        return instance;
    }

    public TypeMapper() {
        this.init();
    }

    private void init() {
        this.addMapping(new ScalarMapping(), "cCiIsSfdlLqQB[:b?#v".split(""));
        this.addMapping(new StringMapping(), "*".split(""));
        this.addMapping(new PointerMapping(), "^");
        this.addMapping(new NSObjectMapping(), "@");
        this.addMapping(new StructureMapping(), "{");
    }

    public TypeMapper addMapping(TypeMapping mapping, String ... signatures) {
        for (int i = 0; i < signatures.length; ++i) {
            this.mappers.put(signatures[i], mapping);
        }
        return this;
    }

    public Object cToJ(Object cVar, String signature, TypeMapping root) {
        String firstChar;
        TypeMapping mapping;
        String prefixes = "rnNoORV";
        int offset = 0;
        while (prefixes.indexOf(signature.charAt(offset)) != -1 && ++offset <= signature.length() - 1) {
        }
        if (offset > 0) {
            signature = signature.substring(offset);
        }
        if ((mapping = this.mappers.get(firstChar = signature.substring(0, 1))) == null) {
            throw new RuntimeException("No mapper registered for type " + firstChar);
        }
        return mapping.cToJ(cVar, signature, root);
    }

    public Object jToC(Object jVar, String signature, TypeMapping root) {
        String firstChar;
        TypeMapping mapping;
        String prefixes = "rnNoORV";
        int offset = 0;
        while (prefixes.indexOf(signature.charAt(offset)) != -1 && ++offset <= signature.length() - 1) {
        }
        if (offset > 0) {
            signature = signature.substring(offset);
        }
        if ((mapping = this.mappers.get(firstChar = signature.substring(0, 1))) == null) {
            throw new RuntimeException("No mapper registered for type " + firstChar);
        }
        return mapping.jToC(jVar, signature, root);
    }
}
