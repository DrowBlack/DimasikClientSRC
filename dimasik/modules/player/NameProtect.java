package dimasik.modules.player;

import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.CheckboxOption;
import dimasik.managers.module.option.main.SelectOption;
import dimasik.managers.module.option.main.SelectOptionValue;
import dimasik.managers.module.option.main.StringOption;
import lombok.Generated;
import net.minecraft.client.Minecraft;

public class NameProtect
extends Module {
    private final SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("Default"), new SelectOptionValue("Custom"));
    private final StringOption names = new StringOption("Name", "dimasikclient.ru").visible(() -> this.mode.getSelected("Custom"));
    private final CheckboxOption friends = new CheckboxOption("Friends", true);

    public NameProtect() {
        super("NameProtect", Category.PLAYER);
        this.settings(this.mode, this.names, this.friends);
    }

    public String patch(String text) {
        String out = text;
        if (this.isToggled()) {
            out = text.replaceAll(Minecraft.getInstance().session.getUsername(), this.mode.getSelected("Default") ? "dimasikclient.ru" : (String)this.names.getValue());
        }
        return out;
    }

    @Generated
    public SelectOption getMode() {
        return this.mode;
    }

    @Generated
    public StringOption getNames() {
        return this.names;
    }

    @Generated
    public CheckboxOption getFriends() {
        return this.friends;
    }
}
