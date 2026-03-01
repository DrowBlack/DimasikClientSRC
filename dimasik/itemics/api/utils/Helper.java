package dimasik.itemics.api.utils;

import dimasik.itemics.api.ItemicsAPI;
import java.util.Arrays;
import java.util.Calendar;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public interface Helper {
    public static final Helper HELPER = new Helper(){};
    public static final Minecraft mc = Minecraft.getInstance();

    public static ITextComponent getPrefix() {
        boolean xd;
        Calendar now = Calendar.getInstance();
        boolean bl = xd = now.get(2) == 3 && now.get(5) <= 3;
        StringTextComponent itemics = new StringTextComponent(xd ? "Itemics" : ((Boolean)ItemicsAPI.getSettings().shortitemicsPrefix.value != false ? "B" : "itemics"));
        itemics.setStyle(itemics.getStyle().setFormatting(TextFormatting.LIGHT_PURPLE));
        StringTextComponent prefix = new StringTextComponent("");
        prefix.setStyle(itemics.getStyle().setFormatting(TextFormatting.DARK_PURPLE));
        prefix.appendString("[");
        prefix.append(itemics);
        prefix.appendString("]");
        return prefix;
    }

    default public void logToast(ITextComponent title, ITextComponent message) {
        mc.execute(() -> ((BiConsumer)ItemicsAPI.getSettings().toaster.value).accept(title, message));
    }

    default public void logToast(String title, String message) {
        this.logToast(new StringTextComponent(title), new StringTextComponent(message));
    }

    default public void logToast(String message) {
        this.logToast(Helper.getPrefix(), new StringTextComponent(message));
    }

    default public void logNotification(String message) {
        this.logNotification(message, false);
    }

    default public void logNotification(String message, boolean error) {
        if (((Boolean)ItemicsAPI.getSettings().desktopNotifications.value).booleanValue()) {
            this.logNotificationDirect(message, error);
        }
    }

    default public void logNotificationDirect(String message) {
        this.logNotificationDirect(message, false);
    }

    default public void logNotificationDirect(String message, boolean error) {
        mc.execute(() -> ((BiConsumer)ItemicsAPI.getSettings().notifier.value).accept(message, error));
    }

    default public void logDebug(String message) {
        if (!((Boolean)ItemicsAPI.getSettings().chatDebug.value).booleanValue()) {
            return;
        }
        this.logDirect(message, false);
    }

    default public void logDirect(boolean logAsToast, ITextComponent ... components) {
        StringTextComponent component = new StringTextComponent("");
        component.append(Helper.getPrefix());
        component.append(new StringTextComponent(" "));
        Arrays.asList(components).forEach(component::append);
        if (logAsToast) {
            this.logToast(Helper.getPrefix(), component);
        } else {
            mc.execute(() -> ((Consumer)ItemicsAPI.getSettings().logger.value).accept(component));
        }
    }

    default public void logDirect(ITextComponent ... components) {
        this.logDirect((Boolean)ItemicsAPI.getSettings().logAsToast.value, components);
    }

    default public void logDirect(String message, TextFormatting color, boolean logAsToast) {
        Stream.of(message.split("\n")).forEach(line -> {
            StringTextComponent component = new StringTextComponent(line.replace("\t", "    "));
            component.setStyle(component.getStyle().setFormatting(color));
            this.logDirect(logAsToast, component);
        });
    }

    default public void logDirect(String message, TextFormatting color) {
        this.logDirect(message, color, (Boolean)ItemicsAPI.getSettings().logAsToast.value);
    }

    default public void logDirect(String message, boolean logAsToast) {
        this.logDirect(message, TextFormatting.GRAY, logAsToast);
    }

    default public void logDirect(String message) {
        this.logDirect(message, (Boolean)ItemicsAPI.getSettings().logAsToast.value);
    }
}
