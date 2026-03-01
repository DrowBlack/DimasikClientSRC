package dimasik.managers.mods.voicechat.api.audio;

public interface AudioConverter {
    public short[] bytesToShorts(byte[] var1);

    public byte[] shortsToBytes(short[] var1);

    public short[] floatsToShorts(float[] var1);

    public float[] shortsToFloats(short[] var1);

    public byte[] floatsToBytes(float[] var1);

    public float[] bytesToFloats(byte[] var1);
}
