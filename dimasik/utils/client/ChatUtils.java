package dimasik.utils.client;

import dimasik.helpers.interfaces.IFastAccess;
import dimasik.helpers.render.ColorHelpers;
import lombok.Generated;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public final class ChatUtils
implements IFastAccess {
    public static void addMessage(String text) {
        ChatUtils.mc.player.sendMessage(ITextComponent.getTextComponentOrEmpty(text), Util.DUMMY_UUID);
    }

    public static void addClientMessage(String text) {
        ChatUtils.mc.player.sendMessage(ColorHelpers.gradient("\u2726 Dimasik", ColorHelpers.getThemeColor(1), ColorHelpers.getThemeColor(2)).append(new StringTextComponent(String.valueOf((Object)TextFormatting.GRAY) + " \u2192 " + String.valueOf((Object)TextFormatting.RESET) + text)), Util.DUMMY_UUID);
    }

    @Generated
    private ChatUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
