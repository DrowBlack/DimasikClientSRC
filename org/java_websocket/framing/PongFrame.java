package org.java_websocket.framing;

import org.java_websocket.enums.Opcode;
import org.java_websocket.framing.ControlFrame;
import org.java_websocket.framing.PingFrame;

public class PongFrame
extends ControlFrame {
    public PongFrame() {
        super(Opcode.PONG);
    }

    public PongFrame(PingFrame pingFrame) {
        super(Opcode.PONG);
        this.setPayload(pingFrame.getPayloadData());
    }
}
