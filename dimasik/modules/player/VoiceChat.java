package dimasik.modules.player;

import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.player.EventPlayerDisconnect;
import dimasik.events.main.player.EventPlayersJoin;
import dimasik.events.main.render.EventRender2D;
import dimasik.managers.mods.voicechat.intercompatibility.MCPClientCompatibilityManager;
import dimasik.managers.mods.voicechat.intercompatibility.MCPCommonCompatibilityManager;
import dimasik.managers.mods.voicechat.voice.client.ClientManager;
import dimasik.managers.mods.voicechat.voice.client.RenderEvents;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;

public class VoiceChat
extends Module {
    private final EventListener<EventRender2D.Post> render2d = this::lox;
    private final EventListener<EventUpdate> update = this::onupde;
    private final EventListener<EventPlayersJoin> join = this::lox;
    private final EventListener<EventPlayerDisconnect> disconnect = this::govno;

    public VoiceChat() {
        super("VoiceChat", Category.PLAYER);
    }

    public void lox(EventRender2D.Post eventRender2D) {
        new RenderEvents().onRenderHUD(eventRender2D.getMatrixStack(), eventRender2D.getPartialTicks());
    }

    public void onupde(EventUpdate eventUpdate) {
        ClientManager.getKeyEvents().handleKeybinds();
        new MCPClientCompatibilityManager().sigma(eventUpdate);
    }

    public void lox(EventPlayersJoin eventPlayersJoin) {
        new MCPCommonCompatibilityManager().event(eventPlayersJoin);
    }

    public void govno(EventPlayerDisconnect eventPlayerDisconnect) {
        new MCPCommonCompatibilityManager().lo1(eventPlayerDisconnect);
    }

    @Override
    public void onDisabled() {
    }
}
