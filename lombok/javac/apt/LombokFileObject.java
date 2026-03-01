package lombok.javac.apt;

import java.nio.charset.CharsetDecoder;
import javax.tools.JavaFileObject;

interface LombokFileObject
extends JavaFileObject {
    public CharsetDecoder getDecoder(boolean var1);
}
