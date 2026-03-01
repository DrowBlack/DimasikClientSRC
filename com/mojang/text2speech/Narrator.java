package com.mojang.text2speech;

import com.mojang.text2speech.NarratorDummy;
import com.mojang.text2speech.NarratorLinux;
import com.mojang.text2speech.NarratorOSX;
import com.mojang.text2speech.NarratorWindows;
import java.util.Locale;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface Narrator {
    public void say(String var1, boolean var2);

    public void clear();

    public boolean active();

    public void destroy();

    public static Narrator getNarrator() {
        Logger LOGGER = LogManager.getLogger();
        try {
            String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
            if (osName.contains("linux")) {
                Narrator.setJNAPath(":");
                return new NarratorLinux();
            }
            if (osName.contains("win")) {
                Narrator.setJNAPath(";");
                return new NarratorWindows();
            }
            if (osName.contains("mac")) {
                Narrator.setJNAPath(":");
                return new NarratorOSX();
            }
            return new NarratorDummy();
        }
        catch (Throwable e) {
            LOGGER.error(String.format("Error while loading the narrator : %s", e));
            return new NarratorDummy();
        }
    }

    public static void setJNAPath(String sep) {
        System.setProperty("jna.library.path", System.getProperty("jna.library.path") + sep + "./src/natives/resources/");
        System.setProperty("jna.library.path", System.getProperty("jna.library.path") + sep + System.getProperty("java.library.path"));
    }
}
