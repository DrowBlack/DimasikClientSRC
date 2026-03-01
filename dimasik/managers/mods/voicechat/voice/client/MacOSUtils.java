package dimasik.managers.mods.voicechat.voice.client;

import dimasik.managers.mods.voicechat.Voicechat;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import javax.annotation.Nullable;
import org.apache.commons.io.FileUtils;

public class MacOSUtils {
    public static void checkPermissionInSeparateProcess() {
        try {
            Voicechat.LOGGER.info("Checking for microphone permission - This may take up to 5 seconds", new Object[0]);
            Path path = MacOSUtils.copyJar();
            int exitCode = MacOSUtils.execInProcess(path, "dimasik.managers.mods.voicechat.macos.Main", new String[0]);
            Voicechat.LOGGER.info("Running permission check process ({})", exitCode);
            if (exitCode != 0) {
                exitCode = MacOSUtils.execInProcess(path, "dimasik.managers.mods.voicechat.macos.Main", "gui");
                Voicechat.LOGGER.info("Running patch GUI process ({})", exitCode);
                if (exitCode == 0) {
                    Voicechat.LOGGER.error("Don't forget to restart your game!", new Object[0]);
                }
            }
        }
        catch (Exception e) {
            Voicechat.LOGGER.info("Failed permission check: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    @Nullable
    private static String getJavaExecutable() {
        String javaHome = System.getProperty("java.home");
        if (javaHome == null) {
            return null;
        }
        return javaHome + File.separator + "bin" + File.separator + "java";
    }

    private static Path copyJar() throws IOException {
        URL macJar = MacOSUtils.class.getClassLoader().getResource("macos.zip");
        if (macJar == null) {
            throw new IOException("Resource not found");
        }
        Path tempDir = Files.createTempDirectory("voicechat", new FileAttribute[0]);
        Path macJarPath = tempDir.resolve("macos.jar");
        macJarPath.toFile().deleteOnExit();
        tempDir.toFile().deleteOnExit();
        FileUtils.copyURLToFile(macJar, macJarPath.toFile());
        return macJarPath;
    }

    private static int execInProcess(Path jarFile, String className, String ... args) throws IOException, InterruptedException {
        String javaExecutable = MacOSUtils.getJavaExecutable();
        if (javaExecutable == null) {
            throw new IOException("Couldn't find Java executable");
        }
        Object classpath = System.getProperty("java.class.path");
        classpath = classpath == null ? jarFile.toFile().getAbsolutePath() : (String)classpath + ":" + jarFile.toFile().getAbsolutePath();
        ArrayList<String> command = new ArrayList<String>();
        command.add(javaExecutable);
        command.add("-cp");
        command.add((String)classpath);
        command.add(className);
        command.addAll(Arrays.asList(args));
        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.inheritIO().start();
        process.waitFor();
        return process.exitValue();
    }
}
