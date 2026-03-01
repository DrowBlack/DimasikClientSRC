package dimasik.managers.mods.voicechat.intercompatibility;

import dimasik.Load;
import dimasik.events.main.EventUpdate;
import dimasik.managers.mods.voicechat.events.ClientVoiceChatConnectedEvent;
import dimasik.managers.mods.voicechat.events.ClientVoiceChatDisconnectedEvent;
import dimasik.managers.mods.voicechat.intercompatibility.ClientCompatibilityManager;
import dimasik.managers.mods.voicechat.resourcepacks.IPackRepository;
import dimasik.managers.mods.voicechat.voice.client.ClientVoicechatConnection;
import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.network.NetworkManager;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.server.integrated.IntegratedServer;

public class MCPClientCompatibilityManager
extends ClientCompatibilityManager {
    private final List<ClientCompatibilityManager.MouseEvent> mouseEvents;
    private final List<ClientCompatibilityManager.KeyboardEvent> keyboardEvents;
    private final List<Runnable> clientTickEvents = new CopyOnWriteArrayList<Runnable>();
    private final List<Runnable> inputEvents;
    private final List<Runnable> disconnectEvents;
    private final List<Runnable> joinWorldEvents;
    private final List<Consumer<ClientVoicechatConnection>> voicechatConnectEvents;
    private final List<Runnable> voicechatDisconnectEvents;
    private final List<Consumer<Integer>> publishServerEvents;
    private boolean wasPublished;

    public MCPClientCompatibilityManager() {
        this.keyboardEvents = new CopyOnWriteArrayList<ClientCompatibilityManager.KeyboardEvent>();
        this.inputEvents = new CopyOnWriteArrayList<Runnable>();
        this.mouseEvents = new CopyOnWriteArrayList<ClientCompatibilityManager.MouseEvent>();
        this.disconnectEvents = new CopyOnWriteArrayList<Runnable>();
        this.joinWorldEvents = new CopyOnWriteArrayList<Runnable>();
        this.voicechatConnectEvents = new CopyOnWriteArrayList<Consumer<ClientVoicechatConnection>>();
        this.voicechatDisconnectEvents = new CopyOnWriteArrayList<Runnable>();
        this.publishServerEvents = new CopyOnWriteArrayList<Consumer<Integer>>();
    }

    public void sigma(EventUpdate eventUpdate) {
        this.clientTickEvents.forEach(Runnable::run);
        this.inputEvents.forEach(Runnable::run);
        IntegratedServer server = Minecraft.getInstance().getIntegratedServer();
        if (server == null) {
            return;
        }
        boolean published = server.isDedicatedServer();
        if (published && !this.wasPublished) {
            this.publishServerEvents.forEach(portConsumer -> portConsumer.accept(server.getServerPort()));
        }
        this.wasPublished = published;
    }

    @Override
    public void emitUnJoinWorldEvent() {
        this.disconnectEvents.forEach(Runnable::run);
    }

    @Override
    public void onRenderNamePlate(ClientCompatibilityManager.RenderNameplateEvent onRenderNamePlate) {
    }

    @Override
    public void onRenderHUD(ClientCompatibilityManager.RenderHUDEvent onRenderHUD) {
    }

    @Override
    public void onKeyboardEvent(ClientCompatibilityManager.KeyboardEvent onKeyboardEvent) {
        this.keyboardEvents.add(onKeyboardEvent);
    }

    @Override
    public void onMouseEvent(ClientCompatibilityManager.MouseEvent onMouseEvent) {
        this.mouseEvents.add(onMouseEvent);
    }

    @Override
    public void onClientTick(Runnable onClientTick) {
        this.clientTickEvents.add(onClientTick);
    }

    @Override
    public InputMappings.Input getBoundKeyOf(KeyBinding keyBinding) {
        InputMappings.Input key = keyBinding.keyCode;
        return key;
    }

    @Override
    public void onHandleKeyBinds(Runnable onHandleKeyBinds) {
        this.inputEvents.add(onHandleKeyBinds);
    }

    @Override
    public KeyBinding registerKeyBinding(KeyBinding keyBinding) {
        return keyBinding;
    }

    @Override
    public void emitVoiceChatConnectedEvent(ClientVoicechatConnection client) {
        this.voicechatConnectEvents.forEach(consumer -> consumer.accept(client));
        Load.getInstance().getEvents().call(new ClientVoiceChatConnectedEvent(client));
    }

    @Override
    public void emitVoiceChatDisconnectedEvent() {
        this.voicechatDisconnectEvents.forEach(Runnable::run);
        Load.getInstance().getEvents().call(new ClientVoiceChatDisconnectedEvent());
    }

    @Override
    public void onVoiceChatConnected(Consumer<ClientVoicechatConnection> onVoiceChatConnected) {
        this.voicechatConnectEvents.add(onVoiceChatConnected);
    }

    @Override
    public void onVoiceChatDisconnected(Runnable onVoiceChatDisconnected) {
        this.voicechatDisconnectEvents.add(onVoiceChatDisconnected);
    }

    @Override
    public void onDisconnect(Runnable onDisconnect) {
        this.disconnectEvents.add(onDisconnect);
    }

    @Override
    public void emitJoinWorldEvent() {
        this.joinWorldEvents.forEach(Runnable::run);
    }

    @Override
    public void onJoinWorld(Runnable onJoinWorld) {
        this.joinWorldEvents.add(onJoinWorld);
    }

    @Override
    public void onPublishServer(Consumer<Integer> onPublishServer) {
        this.publishServerEvents.add(onPublishServer);
    }

    @Override
    public SocketAddress getSocketAddress(NetworkManager connection) {
        return connection.getChannel().remoteAddress();
    }

    @Override
    public void addResourcePackSource(ResourcePackList packRepository, IPackFinder repositorySource) {
        IPackRepository repository = (IPackRepository)((Object)packRepository);
        repository.addSource(repositorySource);
    }
}
