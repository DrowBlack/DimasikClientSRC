package org.openjdk.nashorn.internal.runtime;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import org.openjdk.nashorn.internal.ir.FunctionNode;
import org.openjdk.nashorn.internal.runtime.options.Options;

final class AstSerializer {
    private static final int COMPRESSION_LEVEL = Options.getIntProperty("nashorn.serialize.compression", 4);

    AstSerializer() {
    }

    static byte[] serialize(FunctionNode fn) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Deflater deflater = new Deflater(COMPRESSION_LEVEL);
        try (ObjectOutputStream oout = new ObjectOutputStream(new DeflaterOutputStream((OutputStream)out, deflater));){
            oout.writeObject(fn);
        }
        catch (IOException e) {
            throw new AssertionError("Unexpected exception serializing function", e);
        }
        finally {
            deflater.end();
        }
        return out.toByteArray();
    }
}
