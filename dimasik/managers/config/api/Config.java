package dimasik.managers.config.api;

import dimasik.helpers.interfaces.IFastAccess;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import lombok.Generated;
import net.minecraft.client.Minecraft;

public abstract class Config
implements IFastAccess {
    protected File path;
    protected final String suffix = ".sk";
    private String name;

    protected Config(String name, String path) {
        this.name = name;
        this.path = new File(Minecraft.getInstance().gameDir, path);
    }

    protected String read() {
        return Files.readString(new File(String.valueOf(this.path) + "/" + this.name + ".sk").toPath());
    }

    protected void write(String cfg) {
        Files.writeString(new File(String.valueOf(this.path) + "/" + this.name + ".sk").toPath(), (CharSequence)cfg, new OpenOption[0]);
    }

    protected abstract void save();

    protected abstract void load();

    public synchronized void fastLoad() {
        if (!this.path.exists()) {
            this.path.mkdirs();
            System.out.println("Created new folder");
        }
        if (new File(String.valueOf(this.path) + "/" + this.name + ".sk").exists()) {
            try {
                this.load();
                System.out.println("loaded");
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        } else {
            System.out.println("gg");
        }
    }

    public void fastSave() {
        if (!this.path.exists()) {
            this.path.mkdirs();
            System.out.println("Created new folder");
        }
        try {
            this.save();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Generated
    public File getPath() {
        return this.path;
    }

    @Generated
    public String getSuffix() {
        return this.suffix;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public void setPath(File path) {
        this.path = path;
    }

    @Generated
    public void setName(String name) {
        this.name = name;
    }
}
