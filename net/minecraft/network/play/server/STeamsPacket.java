package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class STeamsPacket
implements IPacket<IClientPlayNetHandler> {
    private String name = "";
    private ITextComponent displayName = StringTextComponent.EMPTY;
    private ITextComponent prefix = StringTextComponent.EMPTY;
    private ITextComponent suffix = StringTextComponent.EMPTY;
    private String nameTagVisibility;
    private String collisionRule;
    private TextFormatting color;
    private final Collection<String> players;
    private int action;
    private int friendlyFlags;

    public STeamsPacket() {
        this.nameTagVisibility = Team.Visible.ALWAYS.internalName;
        this.collisionRule = Team.CollisionRule.ALWAYS.name;
        this.color = TextFormatting.RESET;
        this.players = Lists.newArrayList();
    }

    public STeamsPacket(ScorePlayerTeam teamIn, int actionIn) {
        this.nameTagVisibility = Team.Visible.ALWAYS.internalName;
        this.collisionRule = Team.CollisionRule.ALWAYS.name;
        this.color = TextFormatting.RESET;
        this.players = Lists.newArrayList();
        this.name = teamIn.getName();
        this.action = actionIn;
        if (actionIn == 0 || actionIn == 2) {
            this.displayName = teamIn.getDisplayName();
            this.friendlyFlags = teamIn.getFriendlyFlags();
            this.nameTagVisibility = teamIn.getNameTagVisibility().internalName;
            this.collisionRule = teamIn.getCollisionRule().name;
            this.color = teamIn.getColor();
            this.prefix = teamIn.getPrefix();
            this.suffix = teamIn.getSuffix();
        }
        if (actionIn == 0) {
            this.players.addAll(teamIn.getMembershipCollection());
        }
    }

    public STeamsPacket(ScorePlayerTeam teamIn, Collection<String> playersIn, int actionIn) {
        this.nameTagVisibility = Team.Visible.ALWAYS.internalName;
        this.collisionRule = Team.CollisionRule.ALWAYS.name;
        this.color = TextFormatting.RESET;
        this.players = Lists.newArrayList();
        if (actionIn != 3 && actionIn != 4) {
            throw new IllegalArgumentException("Method must be join or leave for player constructor");
        }
        if (playersIn == null || playersIn.isEmpty()) {
            throw new IllegalArgumentException("Players cannot be null/empty");
        }
        this.action = actionIn;
        this.name = teamIn.getName();
        this.players.addAll(playersIn);
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.name = buf.readString(16);
        this.action = buf.readByte();
        if (this.action == 0 || this.action == 2) {
            this.displayName = buf.readTextComponent();
            this.friendlyFlags = buf.readByte();
            this.nameTagVisibility = buf.readString(40);
            this.collisionRule = buf.readString(40);
            this.color = buf.readEnumValue(TextFormatting.class);
            this.prefix = buf.readTextComponent();
            this.suffix = buf.readTextComponent();
        }
        if (this.action == 0 || this.action == 3 || this.action == 4) {
            int i = buf.readVarInt();
            for (int j = 0; j < i; ++j) {
                this.players.add(buf.readString(40));
            }
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeString(this.name);
        buf.writeByte(this.action);
        if (this.action == 0 || this.action == 2) {
            buf.writeTextComponent(this.displayName);
            buf.writeByte(this.friendlyFlags);
            buf.writeString(this.nameTagVisibility);
            buf.writeString(this.collisionRule);
            buf.writeEnumValue(this.color);
            buf.writeTextComponent(this.prefix);
            buf.writeTextComponent(this.suffix);
        }
        if (this.action == 0 || this.action == 3 || this.action == 4) {
            buf.writeVarInt(this.players.size());
            for (String s : this.players) {
                buf.writeString(s);
            }
        }
    }

    @Override
    public void processPacket(IClientPlayNetHandler handler) {
        handler.handleTeams(this);
    }

    public String getName() {
        return this.name;
    }

    public ITextComponent getDisplayName() {
        return this.displayName;
    }

    public Collection<String> getPlayers() {
        return this.players;
    }

    public int getAction() {
        return this.action;
    }

    public int getFriendlyFlags() {
        return this.friendlyFlags;
    }

    public TextFormatting getColor() {
        return this.color;
    }

    public String getNameTagVisibility() {
        return this.nameTagVisibility;
    }

    public String getCollisionRule() {
        return this.collisionRule;
    }

    public ITextComponent getPrefix() {
        return this.prefix;
    }

    public ITextComponent getSuffix() {
        return this.suffix;
    }
}
