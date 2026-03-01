package org.java_websocket.framing;

import org.java_websocket.enums.Opcode;
import org.java_websocket.framing.DataFrame;

public class ContinuousFrame
extends DataFrame {
    public ContinuousFrame() {
        super(Opcode.CONTINUOUS);
    }
}
