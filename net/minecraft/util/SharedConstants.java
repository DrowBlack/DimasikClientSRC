package net.minecraft.util;

import com.mojang.bridge.game.GameVersion;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.netty.util.ResourceLeakDetector;
import java.time.Duration;
import net.minecraft.command.TranslatableExceptionProvider;
import net.minecraft.util.MinecraftVersion;

public class SharedConstants {
    public static final ResourceLeakDetector.Level NETTY_LEAK_DETECTION = ResourceLeakDetector.Level.DISABLED;
    public static final long field_240855_b_ = Duration.ofMillis(300L).toNanos();
    public static boolean useDatafixers = true;
    public static boolean developmentMode;
    public static final char[] ILLEGAL_FILE_CHARACTERS;
    private static GameVersion version;

    public static boolean isAllowedCharacter(char character) {
        return character != '\u00a7' && character >= ' ' && character != '\u007f';
    }

    public static int getProtocolVersion() {
        return 754;
    }

    public static String filterAllowedCharacters(String input) {
        StringBuilder stringbuilder = new StringBuilder();
        for (char c0 : input.toCharArray()) {
            if (!SharedConstants.isAllowedCharacter(c0)) continue;
            stringbuilder.append(c0);
        }
        return stringbuilder.toString();
    }

    public static GameVersion getVersion() {
        if (version == null) {
            version = MinecraftVersion.load();
        }
        return version;
    }

    static {
        ILLEGAL_FILE_CHARACTERS = new char[]{'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};
        ResourceLeakDetector.setLevel(NETTY_LEAK_DETECTION);
        CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES = false;
        CommandSyntaxException.BUILT_IN_EXCEPTIONS = new TranslatableExceptionProvider();
    }
}
