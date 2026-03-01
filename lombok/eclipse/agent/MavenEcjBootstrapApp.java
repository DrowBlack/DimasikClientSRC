package lombok.eclipse.agent;

import com.zwitserloot.cmdreader.CmdReader;
import com.zwitserloot.cmdreader.Description;
import com.zwitserloot.cmdreader.InvalidCommandLineException;
import com.zwitserloot.cmdreader.Shorthand;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import lombok.core.LombokApp;

public class MavenEcjBootstrapApp
extends LombokApp {
    @Override
    public String getAppName() {
        return "createMavenECJBootstrap";
    }

    @Override
    public String getAppDescription() {
        return "Creates .mvn/jvm.config and .mvn/lombok-bootstrap.jar for\nuse with the ECJ compiler.";
    }

    @Override
    public int runApp(List<String> rawArgs) throws Exception {
        CmdArgs args;
        CmdReader<CmdArgs> reader = CmdReader.of(CmdArgs.class);
        try {
            args = reader.make(rawArgs.toArray(new String[0]));
        }
        catch (InvalidCommandLineException e) {
            this.printHelp(reader, e.getMessage(), System.err);
            return 1;
        }
        if (args.help) {
            this.printHelp(reader, null, System.out);
            return 0;
        }
        return this.createBootstrap(args.output, args.overwrite);
    }

    private int createBootstrap(String root, boolean overwrite) {
        File mvn = new File(root, ".mvn");
        int result = 0;
        if (result == 0) {
            result = this.makeMvn(mvn);
        }
        if (result == 0) {
            result = this.makeJvmConfig(mvn, overwrite);
        }
        if (result == 0) {
            result = this.makeJar(mvn, overwrite);
        }
        return result;
    }

    private int makeMvn(File mvn) {
        int result = 0;
        Exception err = null;
        try {
            if (!mvn.exists() && !mvn.mkdirs()) {
                result = 1;
            }
        }
        catch (Exception e) {
            result = 1;
            err = e;
        }
        if (result != 0) {
            System.err.println("Could not create " + mvn.getPath());
            if (err != null) {
                err.printStackTrace(System.err);
            }
        }
        return result;
    }

    private int makeJvmConfig(File mvn, boolean overwrite) {
        File jvmConfig = new File(mvn, "jvm.config");
        if (jvmConfig.exists() && !overwrite) {
            System.err.println(String.valueOf(MavenEcjBootstrapApp.canonical(jvmConfig)) + " exists but '-w' not specified.");
            return 1;
        }
        try {
            FileWriter writer = new FileWriter(jvmConfig);
            writer.write("-javaagent:.mvn/lombok-bootstrap.jar");
            writer.flush();
            writer.close();
            System.out.println("Successfully created: " + MavenEcjBootstrapApp.canonical(jvmConfig));
            return 0;
        }
        catch (Exception e) {
            System.err.println("Could not create: " + MavenEcjBootstrapApp.canonical(jvmConfig));
            e.printStackTrace(System.err);
            return 1;
        }
    }

    private int makeJar(File mvn, boolean overwrite) {
        File jar = new File(mvn, "lombok-bootstrap.jar");
        if (jar.exists() && !overwrite) {
            System.err.println(String.valueOf(MavenEcjBootstrapApp.canonical(jar)) + " but '-w' not specified.");
            return 1;
        }
        InputStream input = MavenEcjBootstrapApp.class.getResourceAsStream("/lombok/launch/mavenEcjBootstrapAgent.jar");
        FileOutputStream output = new FileOutputStream(jar);
        try {
            int length;
            byte[] buffer = new byte[4096];
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            output.flush();
            output.close();
            System.out.println("Successfully created: " + MavenEcjBootstrapApp.canonical(jar));
        }
        catch (Throwable throwable) {
            try {
                try {
                    output.close();
                }
                catch (Exception exception) {}
                throw throwable;
            }
            catch (Exception e) {
                System.err.println("Could not create: " + MavenEcjBootstrapApp.canonical(jar));
                e.printStackTrace(System.err);
                return 1;
            }
        }
        try {
            output.close();
        }
        catch (Exception exception) {}
        return 0;
    }

    private static String canonical(File out) {
        try {
            return out.getCanonicalPath();
        }
        catch (Exception exception) {
            return out.getAbsolutePath();
        }
    }

    private void printHelp(CmdReader<CmdArgs> reader, String message, PrintStream out) {
        if (message != null) {
            out.println(message);
            out.println("----------------------------");
        }
        out.println(reader.generateCommandLineHelp("java -jar lombok.jar createMavenECJBootstrap"));
    }

    private static class CmdArgs {
        @Shorthand(value={"w"})
        @Description(value="Overwrite existing files. Defaults to false.")
        boolean overwrite = false;
        @Shorthand(value={"o"})
        @Description(value="The root of a Maven project. Defaults to the current working directory.")
        String output;
        @Shorthand(value={"h", "?"})
        @Description(value="Shows this help text")
        boolean help;

        private CmdArgs() {
        }
    }
}
