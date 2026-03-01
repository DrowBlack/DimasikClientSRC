package dimasik.modules.render;

import dimasik.events.api.EventListener;
import dimasik.events.main.packet.EventReceivePacket;
import dimasik.helpers.render.ColorHelpers;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.CheckboxOption;
import java.util.HashMap;
import java.util.Map;
import lombok.Generated;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponent;

public class BetterChat
extends Module {
    private final CheckboxOption animationChat = new CheckboxOption("Animation", true);
    public static CheckboxOption antiSpam = new CheckboxOption("\u0423\u0431\u0440\u0430\u0442\u044c \u0441\u043f\u0430\u043c", false);
    public static CheckboxOption history = new CheckboxOption("\u0418\u0441\u0442\u043e\u0440\u0438\u044f \u0447\u0430\u0442\u0430", false);
    private final Map<String, Integer> messageCounts = new HashMap<String, Integer>();
    private final Map<String, Integer> messageLines = new HashMap<String, Integer>();
    private int line;
    private final EventListener<EventReceivePacket> receive = this::receive;

    public BetterChat() {
        super("BetterChat", Category.RENDER);
        this.settings(this.animationChat, antiSpam, history);
    }

    public void receive(EventReceivePacket event) {
        SChatPacket sPacketChat;
        IPacket<?> packet;
        if (((Boolean)antiSpam.getValue()).booleanValue() && (packet = event.getPacket()) instanceof SChatPacket && (sPacketChat = (SChatPacket)packet).getType() != ChatType.GAME_INFO) {
            TextComponent message = (TextComponent)sPacketChat.getChatComponent();
            String rawMessage = message.getString();
            NewChatGui chatGui = BetterChat.mc.ingameGUI.getChatGUI();
            if (this.messageCounts.containsKey(rawMessage)) {
                int count = this.messageCounts.get(rawMessage) + 1;
                this.messageCounts.put(rawMessage, count);
                int lineNumber = this.messageLines.getOrDefault(rawMessage, -1);
                if (lineNumber != -1) {
                    chatGui.deleteChatLine(lineNumber);
                }
                TextComponent newMessage = (TextComponent)message.deepCopy();
                newMessage.append(ColorHelpers.gradient(" (" + count + "x)", ColorHelpers.getThemeColor(1), ColorHelpers.getThemeColor(2)));
                int currentLine = this.line;
                chatGui.printChatMessageWithOptionalDeletion(newMessage, currentLine, false);
                this.messageLines.put(rawMessage, currentLine);
            } else {
                this.messageCounts.put(rawMessage, 1);
                int currentLine = this.line;
                chatGui.printChatMessageWithOptionalDeletion(message, currentLine, false);
                this.messageLines.put(rawMessage, currentLine);
            }
            ++this.line;
            event.setCancelled(true);
        }
    }

    @Generated
    public CheckboxOption getAnimationChat() {
        return this.animationChat;
    }
}
