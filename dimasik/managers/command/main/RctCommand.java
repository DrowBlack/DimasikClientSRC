package dimasik.managers.command.main;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dimasik.Load;
import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.input.EventInput;
import dimasik.events.main.packet.EventSendPacket;
import dimasik.managers.client.ClientManagers;
import dimasik.managers.command.api.Command;
import dimasik.utils.client.ChatUtils;
import dimasik.utils.player.JoinerUtil;
import dimasik.utils.time.TimerUtils;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.text.TextFormatting;

public class RctCommand
extends Command {
    public TimerUtils timerUtil = new TimerUtils();
    public boolean start;
    public int grief;
    private final JoinerUtil joinerUtil = new JoinerUtil();
    private final EventListener<EventUpdate> update = this::update;
    private final EventListener<EventSendPacket> packet = this::packet;
    private final EventListener<EventInput> key = this::key;

    public RctCommand() {
        super("\u041f\u043e\u0437\u0432\u043e\u043b\u044f\u0435\u0442 \u0431\u044b\u0441\u0442\u0440\u043e \u043f\u0435\u0440\u0435\u043c\u0435\u0449\u0430\u0442\u044c\u0441\u044f \u043c\u0435\u0436\u0434\u0443 \u0441\u0435\u0440\u0432\u0435\u0440\u0430\u043c\u0438", "rct", "reconnect");
    }

    @Override
    public void run(String[] args) throws Exception {
        Load.getInstance().getEvents().register(this);
        if (ClientManagers.isConnectedToServer("playrw") || ClientManagers.isConnectedToServer("reallyworld")) {
            if (args.length == 2) {
                RctCommand.mc.player.sendChatMessage("/hub");
                this.joinerUtil.setSucsess(false);
                this.joinerUtil.setGrief(Integer.parseInt(args[1]));
            } else if (args.length == 1) {
                this.grief = this.getGrief("\u0413\u0420\u0418\u0424 #");
                if (this.grief != -1) {
                    RctCommand.mc.player.sendChatMessage("/hub");
                    this.joinerUtil.setGrief(this.grief);
                    this.joinerUtil.setSucsess(false);
                }
            } else {
                this.error();
            }
        }
    }

    public void update(EventUpdate eventUpdate) {
        this.joinerUtil.onUpd(eventUpdate);
    }

    public void packet(EventSendPacket eventPacket) {
        this.joinerUtil.packet(eventPacket);
    }

    public void key(EventInput eventInput) {
        this.joinerUtil.key(eventInput);
        this.joinerUtil.setSucsess(true);
    }

    @Override
    public void error() {
        ChatUtils.addMessage("\u041f\u0440\u0430\u0432\u0438\u043b\u044c\u043d\u043e\u0435 \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: ");
        ChatUtils.addMessage("");
        ChatUtils.addMessage(".rct ");
        ChatUtils.addMessage(".rct " + String.valueOf((Object)TextFormatting.GRAY) + "[" + String.valueOf((Object)TextFormatting.RED) + "\u043d\u043e\u043c\u0435\u0440 \u0441\u0435\u0440\u0432\u0435\u0440\u0430" + String.valueOf((Object)TextFormatting.GRAY) + "]");
        this.joinerUtil.setSucsess(true);
        Load.getInstance().getEvents().unregister(this);
    }

    @Override
    public void build(LiteralArgumentBuilder<ISuggestionProvider> builder) {
    }

    private int getGrief(String prefix) {
        String anca = "";
        int anarchy = -1;
        for (ScoreObjective team : RctCommand.mc.world.getScoreboard().getScoreObjectives()) {
            String an = team.getDisplayName().getString();
            if (!an.contains("\u0413\u0420\u0418\u0424 #")) continue;
            anca = an.split(prefix)[1];
            anarchy = Integer.parseInt(anca.replace(" ", ""));
            return anarchy;
        }
        return anarchy;
    }
}
