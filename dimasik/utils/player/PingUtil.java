package dimasik.utils.player;

import dimasik.events.main.packet.EventReceivePacket;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.play.client.CConfirmTransactionPacket;
import net.minecraft.network.play.server.SConfirmTransactionPacket;

public class PingUtil {
    private static int MAX_HISTORY = 10;
    public long lastConfirmSentTime;
    public long lastConfirmReceivedTime;
    public boolean lagging;
    private final List<Long> pingHistory = new ArrayList<Long>();

    public long getAveragePing() {
        if (this.pingHistory.isEmpty()) {
            return 0L;
        }
        long sum = 0L;
        for (long ping : this.pingHistory) {
            sum += ping;
        }
        return sum / (long)this.pingHistory.size();
    }

    private void onUpdate() {
        if (System.currentTimeMillis() - this.lastConfirmSentTime > 1000L && this.lastConfirmReceivedTime < this.lastConfirmSentTime) {
            this.lagging = true;
        }
    }

    private void onReceivePacket(EventReceivePacket e) {
        if (e.getPacket() instanceof SConfirmTransactionPacket) {
            this.lastConfirmReceivedTime = System.currentTimeMillis();
            this.lagging = false;
            long ping = this.lastConfirmReceivedTime - this.lastConfirmSentTime;
            if (ping >= 0L && ping < 10000L) {
                this.pingHistory.add(ping);
                if (this.pingHistory.size() > MAX_HISTORY) {
                    this.pingHistory.remove(0);
                }
            }
        }
        if (e.getPacket() instanceof CConfirmTransactionPacket) {
            this.lastConfirmSentTime = System.currentTimeMillis();
        }
    }
}
