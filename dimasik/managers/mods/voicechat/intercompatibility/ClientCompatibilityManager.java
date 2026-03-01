package dimasik.managers.mods.voicechat.intercompatibility;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.managers.mods.voicechat.api.VoicechatClientApi;
import dimasik.managers.mods.voicechat.intercompatibility.MCPClientCompatibilityManager;
import dimasik.managers.mods.voicechat.plugins.impl.VoicechatClientApiImpl;
import dimasik.managers.mods.voicechat.voice.client.ClientVoicechatConnection;
import java.net.SocketAddress;
import java.util.function.Consumer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.Entity;
import net.minecraft.network.NetworkManager;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.text.ITextComponent;

public abstract class ClientCompatibilityManager {
    public static ClientCompatibilityManager INSTANCE = new MCPClientCompatibilityManager();

    public abstract void onRenderNamePlate(RenderNameplateEvent var1);

    public abstract void onRenderHUD(RenderHUDEvent var1);

    public abstract void onKeyboardEvent(KeyboardEvent var1);

    public abstract void onMouseEvent(MouseEvent var1);

    public abstract void onClientTick(Runnable var1);

    public abstract InputMappings.Input getBoundKeyOf(KeyBinding var1);

    public abstract void onHandleKeyBinds(Runnable var1);

    public abstract KeyBinding registerKeyBinding(KeyBinding var1);

    public abstract void emitVoiceChatConnectedEvent(ClientVoicechatConnection var1);

    public abstract void emitVoiceChatDisconnectedEvent();

    public abstract void onVoiceChatConnected(Consumer<ClientVoicechatConnection> var1);

    public abstract void onVoiceChatDisconnected(Runnable var1);

    public abstract void onDisconnect(Runnable var1);

    public abstract void onJoinWorld(Runnable var1);

    public abstract void onPublishServer(Consumer<Integer> var1);

    public abstract SocketAddress getSocketAddress(NetworkManager var1);

    public abstract void addResourcePackSource(ResourcePackList var1, IPackFinder var2);

    public VoicechatClientApi getClientApi() {
        return VoicechatClientApiImpl.INSTANCE;
    }

    public abstract void emitJoinWorldEvent();

    public abstract void emitUnJoinWorldEvent();

    public static interface MouseEvent {
        public void onMouseEvent(long var1, int var3, int var4, int var5);
    }

    public static interface KeyboardEvent {
        public void onKeyboardEvent(long var1, int var3, int var4);
    }

    public static interface RenderHUDEvent {
        public void render(MatrixStack var1, float var2);
    }

    public static interface RenderNameplateEvent {
        public void render(Entity var1, ITextComponent var2, MatrixStack var3, IRenderTypeBuffer var4, int var5);
    }
}
