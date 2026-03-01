package dimasik.managers.config.main;

import dimasik.Load;
import dimasik.managers.config.api.Config;
import dimasik.ui.alt.Account;

public class AltConfig
extends Config {
    public AltConfig() {
        super("alt", "dimasik/client");
    }

    @Override
    protected void save() {
        this.write(Load.getInstance().getAltScreen().get().toString().replace("[", "").replace("]", ""));
    }

    @Override
    protected void load() {
        String info = this.read();
        String[] accounts = info.split(", ");
        Load.getInstance().getAltScreen().getAccounts().clear();
        if (!info.isEmpty()) {
            for (String s : accounts) {
                Load.getInstance().getAltScreen().getAccounts().add(new Account(s));
            }
        }
    }
}
