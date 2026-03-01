package org.openjdk.nashorn.internal.runtime;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.AccessController;
import java.util.zip.InflaterInputStream;
import org.openjdk.nashorn.internal.ir.FunctionNode;

final class AstDeserializer {
    AstDeserializer() {
    }

    static FunctionNode deserialize(byte[] serializedAst) {
        return AccessController.doPrivileged(() -> {
            try {
                return (FunctionNode)new ObjectInputStream(new InflaterInputStream(new ByteArrayInputStream(serializedAst))).readObject();
            }
            catch (IOException | ClassNotFoundException e) {
                throw new AssertionError("Unexpected exception deserializing function", e);
            }
        });
    }
}
