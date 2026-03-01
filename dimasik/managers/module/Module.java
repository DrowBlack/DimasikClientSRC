package dimasik.managers.module;

import com.google.gson.JsonObject;
import dimasik.Load;
import dimasik.helpers.animation.Animation;
import dimasik.helpers.interfaces.IFastAccess;
import dimasik.managers.client.ClientManagers;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.api.Option;
import dimasik.managers.module.option.main.BindOption;
import dimasik.managers.module.option.main.CheckboxOption;
import dimasik.managers.module.option.main.ColorOption;
import dimasik.managers.module.option.main.DraggableOption;
import dimasik.managers.module.option.main.MultiOption;
import dimasik.managers.module.option.main.MultiOptionValue;
import dimasik.managers.module.option.main.SelectOption;
import dimasik.managers.module.option.main.SelectOptionValue;
import dimasik.managers.module.option.main.SliderOption;
import dimasik.managers.module.option.main.StringOption;
import dimasik.managers.notification.api.Notification;
import dimasik.managers.notification.api.Pattern;
import dimasik.utils.client.SoundUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Generated;
import net.minecraft.util.math.vector.Vector2f;

public class Module
implements IFastAccess {
    private final ArrayList<Option<?>> options = new ArrayList();
    private final String name;
    private final Animation animation = new Animation();
    private final Animation toggleFade = new Animation();
    private final Category category;
    private String description = "none";
    private boolean toggled;
    private boolean opened;
    private int currentKey = -1;

    public Module(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    public boolean hasBind() {
        return this.currentKey != -1;
    }

    public void onEnabled() {
    }

    public void onDisabled() {
    }

    public void eventRegister() {
        Load.getInstance().getEvents().register(this);
    }

    public void eventUnRegister() {
        Load.getInstance().getEvents().unregister(this);
    }

    public void toggle() {
        if (!ClientManagers.isUnHook()) {
            boolean bl = this.toggled = !this.toggled;
            if (this.toggled) {
                this.eventRegister();
                System.out.println("Enable " + this.name);
                SoundUtils.playSound("enable");
                Load.getInstance().getHooks().getNotificationManagers().register(new Notification("\u041c\u043e\u0434\u0443\u043b\u044c '" + this.name + "' \u0431\u044b\u043b \u0432\u043a\u043b\u044e\u0447\u0435\u043d", "Module Info", 1500L, this).setPattern(Pattern.ENABLE));
                this.onEnabled();
            } else {
                this.eventUnRegister();
                System.out.println("Disable " + this.name);
                SoundUtils.playSound("disable");
                Load.getInstance().getHooks().getNotificationManagers().register(new Notification("\u041c\u043e\u0434\u0443\u043b\u044c '" + this.name + "' \u0431\u044b\u043b \u0432\u044b\u043a\u043b\u044e\u0447\u0435\u043d", "Module Info", 1500L, this).setPattern(Pattern.DISABLE));
                this.onDisabled();
            }
        }
    }

    public List<Option<?>> getSettingList() {
        return this.getOptions();
    }

    public JsonObject save() {
        JsonObject object = new JsonObject();
        object.addProperty("state", this.isToggled());
        if (this.hasBind()) {
            object.addProperty("keyIndex", this.currentKey);
        }
        JsonObject propertiesObject = new JsonObject();
        JsonObject multiObject = new JsonObject();
        for (Option<?> option : this.getSettingList()) {
            if (this.getSettingList() != null) {
                if (option instanceof CheckboxOption) {
                    CheckboxOption checkboxOption = (CheckboxOption)option;
                    propertiesObject.addProperty(option.getSettingName(), (Boolean)checkboxOption.getValue());
                    if (checkboxOption.getKey() != -1) {
                        propertiesObject.addProperty(option.getSettingName() + "bind", checkboxOption.getKey());
                    }
                } else if (option instanceof SelectOption) {
                    SelectOption selectOption = (SelectOption)option;
                    propertiesObject.addProperty(option.getSettingName(), ((SelectOptionValue)selectOption.getValue()).getName());
                } else if (option instanceof BindOption) {
                    BindOption bindOption = (BindOption)option;
                    propertiesObject.addProperty(option.getSettingName(), bindOption.getKey());
                } else if (option instanceof SliderOption) {
                    SliderOption sliderOption = (SliderOption)option;
                    propertiesObject.addProperty(option.getSettingName(), (Number)sliderOption.getValue());
                } else if (option instanceof MultiOption) {
                    MultiOption multiOption = (MultiOption)option;
                    propertiesObject.addProperty(option.getSettingName(), multiOption.selected());
                    for (MultiOptionValue value : multiOption.getValues()) {
                        if (value.getKey() == -1) continue;
                        multiObject.addProperty(value.getName(), value.getKey());
                    }
                    propertiesObject.add("Binded", multiObject);
                } else if (option instanceof StringOption) {
                    StringOption stringOption = (StringOption)option;
                    propertiesObject.addProperty(option.getSettingName(), (String)stringOption.getValue());
                } else if (option instanceof ColorOption) {
                    ColorOption colorOption = (ColorOption)option;
                    propertiesObject.addProperty(option.getSettingName(), (Number)colorOption.getValue());
                } else if (option instanceof DraggableOption) {
                    DraggableOption draggableOption = (DraggableOption)option;
                    propertiesObject.addProperty(option.getSettingName(), ((Vector2f)draggableOption.getValue()).x + ":" + ((Vector2f)draggableOption.getValue()).y);
                }
            }
            object.add("Options", propertiesObject);
        }
        return object;
    }

    public void load(JsonObject object) {
        if (object != null) {
            if (object.has("state") && object.get("state").getAsBoolean()) {
                this.toggle();
            }
            this.currentKey = -1;
            if (object.has("keyIndex")) {
                this.currentKey = object.get("keyIndex").getAsInt();
            }
            for (Option<?> option : this.getOptions()) {
                JsonObject options = object.getAsJsonObject("Options");
                if (option == null || options == null || !options.has(option.getSettingName())) continue;
                if (option instanceof CheckboxOption) {
                    CheckboxOption checkboxOption = (CheckboxOption)option;
                    checkboxOption.setValue(options.get(checkboxOption.getSettingName()).getAsBoolean());
                    if (!options.has(checkboxOption.getSettingName() + "bind")) continue;
                    checkboxOption.setKey(options.get(checkboxOption.getSettingName() + "bind").getAsInt());
                    continue;
                }
                if (option instanceof SelectOption) {
                    SelectOption selectOption = (SelectOption)option;
                    for (SelectOptionValue value : selectOption.getValues()) {
                        if (!value.getName().equals(options.get(selectOption.getSettingName()).getAsString())) continue;
                        selectOption.setValue(value);
                    }
                    continue;
                }
                if (option instanceof BindOption) {
                    BindOption bindOption = (BindOption)option;
                    bindOption.setKey(options.get(bindOption.getSettingName()).getAsInt());
                    continue;
                }
                if (option instanceof SliderOption) {
                    SliderOption sliderOption = (SliderOption)option;
                    sliderOption.setValue(Float.valueOf(options.get(sliderOption.getSettingName()).getAsFloat()));
                    continue;
                }
                if (option instanceof MultiOption) {
                    MultiOption multiOption = (MultiOption)option;
                    String[] toggled = options.get(multiOption.getSettingName()).getAsString().split(", ");
                    for (MultiOptionValue value : multiOption.getValues()) {
                        value.setToggle(false);
                        for (String s : toggled) {
                            if (!value.getName().equals(s)) continue;
                            value.setToggle(true);
                        }
                    }
                    if (!options.has("Binded")) continue;
                    JsonObject bindElements = options.getAsJsonObject("Binded");
                    for (MultiOptionValue value : multiOption.getValues()) {
                        if (!bindElements.has(value.getName())) continue;
                        value.setKey(bindElements.get(value.getName()).getAsInt());
                    }
                    continue;
                }
                if (!(option instanceof StringOption)) continue;
                StringOption stringOption = (StringOption)option;
                stringOption.setValue(options.get(stringOption.getSettingName()).getAsString());
            }
        }
    }

    public void settings(Option<?> ... options) {
        this.options.addAll(Arrays.asList(options));
    }

    @Generated
    public ArrayList<Option<?>> getOptions() {
        return this.options;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public Animation getAnimation() {
        return this.animation;
    }

    @Generated
    public Animation getToggleFade() {
        return this.toggleFade;
    }

    @Generated
    public Category getCategory() {
        return this.category;
    }

    @Generated
    public String getDescription() {
        return this.description;
    }

    @Generated
    public boolean isToggled() {
        return this.toggled;
    }

    @Generated
    public boolean isOpened() {
        return this.opened;
    }

    @Generated
    public int getCurrentKey() {
        return this.currentKey;
    }

    @Generated
    public void setDescription(String description) {
        this.description = description;
    }

    @Generated
    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }

    @Generated
    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    @Generated
    public void setCurrentKey(int currentKey) {
        this.currentKey = currentKey;
    }
}
