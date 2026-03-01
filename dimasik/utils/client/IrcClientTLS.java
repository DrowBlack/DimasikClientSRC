package dimasik.utils.client;

import java.net.URI;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class IrcClientTLS
extends WebSocketClient {
    private final String nickname;
    private boolean sentNick = false;

    public IrcClientTLS(String url, String nickname) {
        super(URI.create(url));
        this.nickname = nickname;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        this.send(this.nickname);
        this.sentNick = true;
    }

    @Override
    public void onMessage(String message) {
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        this.sentNick = false;
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public void sendIrcMessage(String text) {
        if (this.sentNick) {
            this.send(text);
        }
    }
}
