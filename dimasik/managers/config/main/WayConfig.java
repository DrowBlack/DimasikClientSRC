package dimasik.managers.config.main;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dimasik.Load;
import dimasik.managers.command.main.WayCommand;
import dimasik.managers.config.api.Config;
import net.minecraft.util.math.vector.Vector3i;

public class WayConfig
extends Config {
    public WayConfig() {
        super("gps", "dimasik/client");
    }

    @Override
    protected void save() {
        WayCommand WayCommand2 = (WayCommand)Load.getInstance().getHooks().getCommandManagers().findClass(WayCommand.class);
        JsonObject gps = new JsonObject();
        JsonObject point = new JsonObject();
        for (String name : WayCommand2.getPoints().keySet()) {
            Vector3i position = WayCommand2.getPoints().get(name);
            int var10002 = position.getX();
            point.addProperty(name, var10002 + ":" + position.getY() + ":" + position.getZ());
        }
        gps.add("Gps", point);
        String contentPrettyPrint = new GsonBuilder().setPrettyPrinting().create().toJson(gps);
        this.write(contentPrettyPrint);
    }

    @Override
    protected void load() {
        WayCommand WayCommand2 = (WayCommand)Load.getInstance().getHooks().getCommandManagers().findClass(WayCommand.class);
        WayCommand2.getPoints().clear();
        JsonParser parser = new JsonParser();
        JsonObject gps = (JsonObject)parser.parse(this.read());
        if (gps.has("Gps")) {
            String[] gp;
            JsonObject point = gps.getAsJsonObject("Gps");
            for (String s : gp = point.toString().split(",")) {
                String[] name = s.split(":");
                Vector3i value = new Vector3i(Integer.parseInt(name[1].replace("\"", "")), Integer.parseInt(name[2]), Integer.parseInt(name[3].replace("\"", "").replace("}", "")));
                WayCommand2.getPoints().put(name[0].replace("\"", "").replace("{", ""), value);
            }
        }
    }
}
