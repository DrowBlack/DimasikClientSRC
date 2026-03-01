package dimasik.managers.config.main;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dimasik.managers.client.ClientManagers;
import dimasik.managers.config.api.Config;

public class ClientConfig
extends Config {
    public ClientConfig() {
        super("client", "dimasik/client");
    }

    @Override
    protected void save() {
        JsonObject global = new JsonObject();
        JsonObject information = new JsonObject();
        information.addProperty("isUnhooked", ClientManagers.isUnHook());
        information.addProperty("Value", ClientManagers.getRandom());
        information.addProperty("Language", ClientManagers.getLanguage());
        global.add("Global", information);
        String contentPrettyPrint = new GsonBuilder().setPrettyPrinting().create().toJson(global);
        this.write(contentPrettyPrint);
    }

    @Override
    protected void load() {
        JsonParser parser = new JsonParser();
        JsonObject global = (JsonObject)parser.parse(this.read());
        if (global.has("Global")) {
            JsonObject information = global.getAsJsonObject("Global");
            String lang = information.get("Language").getAsString();
            ClientManagers.changeLanguage(lang);
            boolean bool = information.get("isUnhooked").getAsBoolean();
            String value = information.get("Value").getAsString();
            ClientManagers.setUnHook(bool);
            ClientManagers.setRandom(value);
        }
    }
}
