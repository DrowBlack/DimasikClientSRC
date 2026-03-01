package lombok.bytecode;

import com.zwitserloot.cmdreader.CmdReader;
import com.zwitserloot.cmdreader.Description;
import com.zwitserloot.cmdreader.InvalidCommandLineException;
import com.zwitserloot.cmdreader.Mandatory;
import com.zwitserloot.cmdreader.Sequential;
import com.zwitserloot.cmdreader.Shorthand;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.bytecode.ClassFileMetaData;
import lombok.bytecode.PostCompilerApp;
import lombok.core.LombokApp;

public class PoolConstantsApp
extends LombokApp {
    @Override
    public String getAppName() {
        return "Xprintpool";
    }

    @Override
    public String getAppDescription() {
        return "Prints the content of the constant pool to standard out.";
    }

    @Override
    public boolean isDebugTool() {
        return true;
    }

    @Override
    public int runApp(List<String> raw) throws Exception {
        CmdArgs args;
        CmdReader<CmdArgs> reader = CmdReader.of(CmdArgs.class);
        try {
            args = reader.make(raw.toArray(new String[0]));
            if (args.help) {
                System.out.println(reader.generateCommandLineHelp("java -jar lombok.jar -printpool"));
                return 0;
            }
        }
        catch (InvalidCommandLineException e) {
            System.err.println(e.getMessage());
            System.err.println(reader.generateCommandLineHelp("java -jar lombok.jar -printpool"));
            return 1;
        }
        List<File> filesToProcess = PostCompilerApp.cmdArgsToFiles(args.classFiles);
        int filesVisited = 0;
        boolean moreThanOne = filesToProcess.size() > 1;
        for (File file : filesToProcess) {
            if (!file.exists() || !file.isFile()) {
                System.out.printf("Cannot find file '%s'\n", file.getAbsolutePath());
                continue;
            }
            ++filesVisited;
            if (moreThanOne) {
                System.out.printf("Processing '%s'\n", file.getAbsolutePath());
            }
            System.out.println(new ClassFileMetaData(PostCompilerApp.readFile(file)).poolContent());
        }
        if (moreThanOne) {
            System.out.printf("Total files visited: %d\n", filesVisited);
        }
        return filesVisited == 0 ? 1 : 0;
    }

    public static class CmdArgs {
        @Sequential
        @Mandatory
        @Description(value="paths to class files to be printed. If a directory is named, all files (recursively) in that directory will be printed.")
        private List<String> classFiles = new ArrayList<String>();
        @Shorthand(value={"h", "?"})
        @Description(value="Shows this help text")
        boolean help = false;
    }
}
