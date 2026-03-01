package wtf.wither.annotations.vmprotect;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import wtf.wither.annotations.vmprotect.VMProtectType;

@Retention(value=RetentionPolicy.RUNTIME)
public @interface VMProtect {
    public VMProtectType type();
}
