package lombok.core;

import lombok.core.DiagnosticsReceiver;

public interface PostCompilerTransformation {
    public byte[] applyTransformations(byte[] var1, String var2, DiagnosticsReceiver var3);
}
