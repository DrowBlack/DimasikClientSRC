package dimasik.managers.mods.voicechat.debug;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.managers.mods.voicechat.debug.VoicechatUncaughtExceptionHandler;
import dimasik.managers.mods.voicechat.gui.GroupType;
import dimasik.managers.mods.voicechat.intercompatibility.ClientCompatibilityManager;
import dimasik.managers.mods.voicechat.intercompatibility.CommonCompatibilityManager;
import dimasik.managers.mods.voicechat.voice.client.AudioChannel;
import dimasik.managers.mods.voicechat.voice.client.ClientGroupManager;
import dimasik.managers.mods.voicechat.voice.client.ClientManager;
import dimasik.managers.mods.voicechat.voice.client.ClientPlayerStateManager;
import dimasik.managers.mods.voicechat.voice.client.ClientVoicechat;
import dimasik.managers.mods.voicechat.voice.client.speaker.ALSpeaker;
import dimasik.managers.mods.voicechat.voice.client.speaker.Speaker;
import dimasik.managers.mods.voicechat.voice.common.ClientGroup;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;

public class DebugOverlay {
    private static final Minecraft mc = Minecraft.getInstance();
    private final Map<UUID, AudioChannelInfo> audioChannelInfoMap;
    private boolean active;
    @Nullable
    private TimerThread timer;
    private List<String> rightText = new ArrayList<String>();
    public static final int MAX_AUDIO_CHANNELS = 4;
    private static final int LEFT_PADDING = 5;

    public DebugOverlay() {
        this.audioChannelInfoMap = new LinkedHashMap<UUID, AudioChannelInfo>();
        ClientCompatibilityManager.INSTANCE.onRenderHUD(this::render);
    }

    public void toggle() {
        boolean bl = this.active = !this.active;
        if (this.active) {
            this.audioChannelInfoMap.clear();
            this.timer = new TimerThread();
        } else {
            if (this.timer != null) {
                this.timer.close();
            }
            this.audioChannelInfoMap.clear();
        }
    }

    private void render(MatrixStack stack, float tickDelta) {
        if (!this.active) {
            return;
        }
        this.rightText.clear();
        this.rightText.add(String.format("%s %s debug overlay", CommonCompatibilityManager.INSTANCE.getModName(), CommonCompatibilityManager.INSTANCE.getModVersion()));
        this.rightText.add(String.format("Press ALT + %s to toggle", ClientCompatibilityManager.INSTANCE.getBoundKeyOf(Minecraft.getInstance().gameSettings.KEY_VOICE_CHAT).toString()));
        this.rightText.add(null);
        ClientVoicechat client = ClientManager.getClient();
        if (client == null) {
            this.rightText.add("Voice chat not running");
            this.drawRight(stack, this.rightText);
            return;
        }
        this.rightText.add(String.format("UUID: %s", ClientManager.getPlayerStateManager().getOwnID()));
        this.rightText.add(null);
        this.addStateStrings(this.rightText);
        this.rightText.add(null);
        this.addAudioChannelStrings(this.rightText);
        this.drawRight(stack, this.rightText);
    }

    private void addAudioChannelStrings(List<String> strings) {
        strings.add(String.format("Audio Channels: %s", this.audioChannelInfoMap.size()));
        ArrayList<Map.Entry<UUID, AudioChannelInfo>> entries = new ArrayList<Map.Entry<UUID, AudioChannelInfo>>(this.audioChannelInfoMap.entrySet());
        for (int i = 0; i < entries.size() && i < 4; ++i) {
            Map.Entry<UUID, AudioChannelInfo> entry = entries.get(i);
            AudioChannelInfo audioChannel = entry.getValue();
            if (audioChannel.audioBufferCount < 0) {
                strings.add(String.format("ID: %s Packets: %s Reordering: %S Lost: %s Queue: STOPPED", entry.getKey().toString().substring(24), audioChannel.bufferedPackets, audioChannel.packetReorderingBuffer, audioChannel.lostPackets));
                continue;
            }
            strings.add(String.format("ID: %s Packets: %s Reordering: %S Lost: %s Queue: %s/%s", entry.getKey().toString().substring(24), audioChannel.bufferedPackets, audioChannel.packetReorderingBuffer, audioChannel.lostPackets, audioChannel.audioBufferCount, audioChannel.audioBufferSize));
        }
        if (entries.size() > 4) {
            strings.add(String.format("%s more channels", entries.size() - 4));
        }
    }

    private void addStateStrings(List<String> strings) {
        ClientGroupManager groupManager = ClientManager.getGroupManager();
        Collection<ClientGroup> groups = groupManager.getGroups();
        ClientPlayerStateManager stateManager = ClientManager.getPlayerStateManager();
        ClientGroup group = stateManager.getGroup();
        strings.add(String.format("Groups: %s", groups.size()));
        strings.add(this.clientGroupToString(group));
        strings.add(String.format("States: %s Disconnected: %s Disabled: %s Muted: %s", stateManager.getPlayerStates(true).size(), stateManager.isDisconnected(), stateManager.isDisabled(), stateManager.isMuted()));
    }

    private String clientGroupToString(ClientGroup group) {
        if (group == null) {
            return "Group: N/A";
        }
        return String.format("Group: %s Name: %s Password: %s Persistent: %s Type: %s", group.getId().toString().substring(24), group.getName(), group.hasPassword(), group.isPersistent(), GroupType.fromType(group.getType()).name());
    }

    private void updateCache() {
        ClientVoicechat client = ClientManager.getClient();
        if (client == null) {
            this.audioChannelInfoMap.clear();
            return;
        }
        Map<UUID, AudioChannel> audioChannels = client.getAudioChannels();
        this.audioChannelInfoMap.values().removeIf(audioChannelInfo -> !audioChannels.containsKey(audioChannelInfo.id));
        for (Map.Entry<UUID, AudioChannel> entry : audioChannels.entrySet()) {
            AudioChannel audioChannel = entry.getValue();
            AudioChannelInfo info = this.audioChannelInfoMap.computeIfAbsent(entry.getKey(), uuid -> new AudioChannelInfo((UUID)entry.getKey()));
            info.update(audioChannel);
        }
    }

    private void drawRight(MatrixStack stack, List<String> strings) {
        for (int i = 0; i < strings.size(); ++i) {
            String text = strings.get(i);
            if (text == null || text.isEmpty()) continue;
            stack.push();
            int width = DebugOverlay.mc.fontRenderer.getStringWidth(text);
            double d = mc.getMainWindow().getScaledWidth() - width - 5;
            float f = i;
            Objects.requireNonNull(DebugOverlay.mc.fontRenderer);
            stack.translate(d, 25.0f + f * (9.0f + 1.0f), 0.0);
            AbstractGui.fill(stack, -1, -1, width, DebugOverlay.mc.fontRenderer.FONT_HEIGHT, -1873784752);
            DebugOverlay.mc.fontRenderer.drawString(stack, text, 0.0f, 0.0f, 0xFFFFFF);
            stack.pop();
        }
    }

    private class TimerThread
    extends Thread {
        private boolean stopped;

        private TimerThread() {
            this.setName("Voicechat Debug Overlay Thread");
            this.setDaemon(true);
            this.setUncaughtExceptionHandler(new VoicechatUncaughtExceptionHandler());
            this.start();
        }

        @Override
        public void run() {
            while (!this.stopped) {
                DebugOverlay.this.updateCache();
                try {
                    Thread.sleep(20L);
                }
                catch (InterruptedException interruptedException) {}
            }
        }

        public void close() {
            this.stopped = true;
            this.interrupt();
        }
    }

    private static class AudioChannelInfo {
        private final UUID id;
        private int audioBufferSize;
        private int audioBufferCount;
        private int bufferedPackets;
        private int packetReorderingBuffer;
        private long lostPackets;

        public AudioChannelInfo(UUID id) {
            this.id = id;
        }

        public AudioChannelInfo update(AudioChannel audioChannel) {
            this.audioBufferSize = 32;
            this.audioBufferCount = -1;
            this.bufferedPackets = audioChannel.getQueue().size();
            this.packetReorderingBuffer = audioChannel.getPacketBuffer().getSize();
            this.lostPackets = audioChannel.getLostPackets();
            Speaker speaker = audioChannel.getSpeaker();
            if (speaker instanceof ALSpeaker) {
                ((ALSpeaker)speaker).fetchQueuedBuffersAsync(bufferCount -> {
                    this.audioBufferCount = bufferCount;
                });
            }
            return this;
        }
    }
}
