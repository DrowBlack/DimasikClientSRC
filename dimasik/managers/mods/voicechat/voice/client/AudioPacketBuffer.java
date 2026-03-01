package dimasik.managers.mods.voicechat.voice.client;

import dimasik.managers.mods.voicechat.voice.common.SoundPacket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

public class AudioPacketBuffer {
    private final int packetThreshold;
    @Nullable
    private List<SoundPacket<?>> packetBuffer;
    private long lastSequenceNumber = -1L;
    private boolean isFlushingBuffer;

    public AudioPacketBuffer(int packetThreshold) {
        this.packetThreshold = packetThreshold;
        if (packetThreshold > 0) {
            this.packetBuffer = new ArrayList();
        }
    }

    @Nullable
    public SoundPacket<?> poll(BlockingQueue<SoundPacket<?>> queue) throws InterruptedException {
        if (this.packetThreshold <= 0) {
            return queue.poll(10L, TimeUnit.MILLISECONDS);
        }
        SoundPacket<?> packet = this.getNext();
        if (packet != null) {
            return packet;
        }
        packet = queue.poll(5L, TimeUnit.MILLISECONDS);
        if (packet == null) {
            return null;
        }
        if (packet.getSequenceNumber() == this.lastSequenceNumber + 1L || this.lastSequenceNumber < 0L) {
            this.lastSequenceNumber = packet.getSequenceNumber();
            return packet;
        }
        this.addSorted(packet);
        return null;
    }

    private void addSorted(SoundPacket<?> packet) {
        if (packet.getData().length <= 0) {
            this.isFlushingBuffer = true;
        }
        this.packetBuffer.add(packet);
        this.packetBuffer.sort(Comparator.comparingLong(SoundPacket::getSequenceNumber));
    }

    @Nullable
    private SoundPacket<?> getNext() {
        if (this.isFlushingBuffer) {
            if (this.packetBuffer.isEmpty()) {
                this.isFlushingBuffer = false;
                return null;
            }
            return this.getFirstPacket();
        }
        if (this.packetBuffer.size() > this.packetThreshold) {
            return this.getFirstPacket();
        }
        if (!this.packetBuffer.isEmpty()) {
            SoundPacket<?> packet = this.packetBuffer.get(0);
            if (packet.getSequenceNumber() == this.lastSequenceNumber + 1L || this.lastSequenceNumber < 0L) {
                return this.getFirstPacket();
            }
            return null;
        }
        return null;
    }

    private SoundPacket<?> getFirstPacket() {
        SoundPacket<?> packet = this.packetBuffer.remove(0);
        this.lastSequenceNumber = packet.getSequenceNumber();
        return packet;
    }

    public void clear() {
        if (this.packetBuffer != null) {
            this.packetBuffer.clear();
        }
        this.lastSequenceNumber = -1L;
        this.isFlushingBuffer = false;
    }

    public int getSize() {
        if (this.packetBuffer == null) {
            return 0;
        }
        return this.packetBuffer.size();
    }
}
