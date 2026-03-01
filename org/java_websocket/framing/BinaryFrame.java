package org.java_websocket.framing;

import org.java_websocket.enums.Opcode;
import org.java_websocket.framing.DataFrame;

public class BinaryFrame
extends DataFrame {
    public BinaryFrame() {
        super(Opcode.BINARY);
    }
}
