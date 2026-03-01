package lombok;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.SOURCE)
public @interface Locked {
    public String value() default "";

    @Target(value={ElementType.METHOD})
    @Retention(value=RetentionPolicy.SOURCE)
    public static @interface Read {
        public String value() default "";
    }

    @Target(value={ElementType.METHOD})
    @Retention(value=RetentionPolicy.SOURCE)
    public static @interface Write {
        public String value() default "";
    }
}
