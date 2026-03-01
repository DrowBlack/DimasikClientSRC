package dimasik.managers.config.main;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dimasik.Load;
import dimasik.managers.config.api.Config;
import dimasik.managers.draggable.api.Component;
import net.minecraft.util.math.vector.Vector2f;

public class DraggableConfig
extends Config {
    public DraggableConfig() {
        super("draggable", "dimasik/client");
    }

    @Override
    protected void save() {
        JsonObject drags = new JsonObject();
        JsonObject dragss = new JsonObject();
        JsonObject dragsOptions = new JsonObject();
        for (Component component : Load.getInstance().getHooks().getDraggableManagers()) {
            dragsOptions.addProperty(component.getDraggableOption().getSettingName(), ((Vector2f)component.getDraggableOption().getValue()).x + ":" + ((Vector2f)component.getDraggableOption().getValue()).y);
            dragss.add(component.getDraggableOption().getSettingName(), component.save());
        }
        drags.add("Drag", dragsOptions);
        drags.add("Next", dragss);
        String contentPrettyPrint = new GsonBuilder().setPrettyPrinting().create().toJson(drags);
        this.write(contentPrettyPrint);
    }

    @Override
    protected void load() {
        JsonParser parser = new JsonParser();
        JsonObject drags = (JsonObject)parser.parse(this.read());
        if (drags.has("Drag")) {
            JsonObject drag = drags.getAsJsonObject("Drag");
            JsonObject dragss = drags.getAsJsonObject("Next");
            for (Component component : Load.getInstance().getHooks().getDraggableManagers()) {
                String[] coords = drag.get(component.getDraggableOption().getSettingName()).getAsString().split(":");
                Vector2f value = new Vector2f(Float.parseFloat(coords[0]), Float.parseFloat(coords[1]));
                component.getDraggableOption().setValue(value);
                component.getDraggableOption().setStartX(value.x);
                component.getDraggableOption().setStartY(value.y);
                component.load(dragss.getAsJsonObject(component.getDraggableOption().getSettingName()));
            }
        }
    }
}
