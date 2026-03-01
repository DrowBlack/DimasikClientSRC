package dimasik.managers.config.main;

import dimasik.Load;
import dimasik.managers.config.api.Config;
import dimasik.managers.macro.api.Macro;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.OpenOption;

public class MacroConfig
extends Config {
    public MacroConfig() {
        super("macro", "dimasik/client");
    }

    @Override
    protected void save() {
        try {
            StringBuilder builder = new StringBuilder();
            Load.getInstance().getHooks().getMacroManagers().forEach(macro -> builder.append(macro.getMessage()).append(":").append(String.valueOf(macro.getKey()).toUpperCase()).append("\n"));
            Files.write(new File(String.valueOf(this.getPath()) + "/" + this.getName() + this.getSuffix()).toPath(), builder.toString().getBytes(), new OpenOption[0]);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void load() {
        Load.getInstance().getHooks().getMacroManagers().clear();
        try {
            String line;
            FileInputStream fileInputStream = new FileInputStream(new File(String.valueOf(this.getPath()) + "/" + this.getName() + this.getSuffix()).getAbsolutePath());
            BufferedReader reader = new BufferedReader(new InputStreamReader(new DataInputStream(fileInputStream)));
            while ((line = reader.readLine()) != null) {
                String curLine = line.trim();
                String command = curLine.split(":")[0];
                String key = curLine.split(":")[1];
                Load.getInstance().getHooks().getMacroManagers().add(new Macro(command, Integer.parseInt(key)));
            }
            reader.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
