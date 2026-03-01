package lombok.launch;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import lombok.launch.ClassFileMetaData;

class PackageShader {
    private final byte[][] froms;
    private final byte[][] tos;
    private static final byte CONSTANTPOOLTYPE_UTF8 = 1;

    public PackageShader(String ... shadeOps) {
        if (shadeOps.length % 2 != 0) {
            throw new IllegalArgumentException("Provide pairs: real package name to shaded package name (you provided an odd number of strings; even number required)");
        }
        Charset ascii = Charset.forName("US-ASCII");
        int len = shadeOps.length / 2;
        this.froms = new byte[len][];
        this.tos = new byte[len][];
        int i = 0;
        while (i < len) {
            String in = shadeOps[i << 1];
            String out = shadeOps[i << 1 | 1];
            if (in.contains(".") || out.contains(".")) {
                throw new IllegalArgumentException("Binary name prefixes are required (use slashes and dollars instead of dots to separate type name elements); they look like e.g. 'java/util/'. Violating entry: " + in + " -> " + out);
            }
            this.froms[i] = in.getBytes(ascii);
            this.tos[i] = out.getBytes(ascii);
            if (this.froms[i].length != this.tos[i].length) {
                throw new IllegalArgumentException("Pair [" + in + " -> " + out + "] is invalid: Both strings must be the same length");
            }
            ++i;
        }
    }

    public boolean apply(byte[] b) {
        ClassFileMetaData md = new ClassFileMetaData(b);
        boolean changes = false;
        int[] startPoints = new int[260];
        int[] nArray = md.getOffsets((byte)1);
        int n = nArray.length;
        int n2 = 0;
        while (n2 < n) {
            int offset = nArray[n2];
            int len = PackageShader.readValue(b, offset);
            startPoints[0] = offset += 2;
            int maxStartPoints = 1;
            int i = offset;
            int max = offset + len;
            while (i < max) {
                if (b[i] == 76) {
                    startPoints[maxStartPoints++] = i + 1;
                }
                ++i;
            }
            i = 0;
            while (i < this.froms.length) {
                int startPointIdx = 0;
                while (startPointIdx < maxStartPoints) {
                    block8: {
                        int indexIntoSignature = startPoints[startPointIdx];
                        if (len - (indexIntoSignature - offset) >= this.froms[i].length) {
                            int p = 0;
                            while (p < this.froms[i].length) {
                                if (b[indexIntoSignature + p] == this.froms[i][p]) {
                                    ++p;
                                    continue;
                                }
                                break block8;
                            }
                            System.arraycopy(this.tos[i], 0, b, indexIntoSignature, this.tos[i].length);
                            changes = true;
                        }
                    }
                    ++startPointIdx;
                }
                ++i;
            }
            ++n2;
        }
        return changes;
    }

    private static int readValue(byte[] b, int position) {
        return (b[position] & 0xFF) << 8 | b[position + 1] & 0xFF;
    }

    public String reverseResourceName(String name) {
        int i = 0;
        while (i < this.tos.length) {
            block6: {
                int len = this.tos[i].length;
                if (name.length() >= len) {
                    int p = 0;
                    while (p < len) {
                        if (name.charAt(p) == this.tos[i][p]) {
                            ++p;
                            continue;
                        }
                        break block6;
                    }
                    try {
                        String out = String.valueOf(new String(this.froms[i], 0, this.froms[i].length, "US-ASCII")) + name.substring(len);
                        return out;
                    }
                    catch (UnsupportedEncodingException unsupportedEncodingException) {
                        return name;
                    }
                }
            }
            ++i;
        }
        return name;
    }
}
