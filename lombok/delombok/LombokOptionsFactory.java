package lombok.delombok;

import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Options;
import lombok.javac.Javac;
import lombok.javac.Javac6BasedLombokOptions;
import lombok.javac.Javac8BasedLombokOptions;
import lombok.javac.Javac9BasedLombokOptions;
import lombok.javac.LombokOptions;

public class LombokOptionsFactory {
    public static LombokOptions getDelombokOptions(Context context) {
        Options rawOptions = Options.instance(context);
        if (rawOptions instanceof LombokOptions) {
            return (LombokOptions)rawOptions;
        }
        LombokOptions options = Javac.getJavaCompilerVersion() < 8 ? LombokOptionCompilerVersion.JDK7_AND_LOWER.createAndRegisterOptions(context) : (Javac.getJavaCompilerVersion() == 8 ? LombokOptionCompilerVersion.JDK8.createAndRegisterOptions(context) : LombokOptionCompilerVersion.JDK9.createAndRegisterOptions(context));
        return options;
    }

    static enum LombokOptionCompilerVersion {
        JDK7_AND_LOWER{

            @Override
            LombokOptions createAndRegisterOptions(Context context) {
                return Javac6BasedLombokOptions.replaceWithDelombokOptions(context);
            }
        }
        ,
        JDK8{

            @Override
            LombokOptions createAndRegisterOptions(Context context) {
                return Javac8BasedLombokOptions.replaceWithDelombokOptions(context);
            }
        }
        ,
        JDK9{

            @Override
            LombokOptions createAndRegisterOptions(Context context) {
                return Javac9BasedLombokOptions.replaceWithDelombokOptions(context);
            }
        };


        abstract LombokOptions createAndRegisterOptions(Context var1);
    }
}
