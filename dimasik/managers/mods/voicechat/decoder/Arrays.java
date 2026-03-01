package dimasik.managers.mods.voicechat.decoder;

class Arrays {
    Arrays() {
    }

    static int[][] InitTwoDimensionalArrayInt(int x, int y) {
        return new int[x][y];
    }

    static float[][] InitTwoDimensionalArrayFloat(int x, int y) {
        return new float[x][y];
    }

    static short[][] InitTwoDimensionalArrayShort(int x, int y) {
        return new short[x][y];
    }

    static byte[][] InitTwoDimensionalArrayByte(int x, int y) {
        return new byte[x][y];
    }

    static byte[][][] InitThreeDimensionalArrayByte(int x, int y, int z) {
        return new byte[x][y][z];
    }

    static void MemSet(byte[] array, byte value) {
        java.util.Arrays.fill(array, value);
    }

    static void MemSet(short[] array, short value) {
        java.util.Arrays.fill(array, value);
    }

    static void MemSet(int[] array, int value) {
        java.util.Arrays.fill(array, value);
    }

    static void MemSet(float[] array, float value) {
        java.util.Arrays.fill(array, value);
    }

    static void MemSet(byte[] array, byte value, int length) {
        java.util.Arrays.fill(array, 0, length, value);
    }

    static void MemSet(short[] array, short value, int length) {
        java.util.Arrays.fill(array, 0, length, value);
    }

    static void MemSet(int[] array, int value, int length) {
        java.util.Arrays.fill(array, 0, length, value);
    }

    static void MemSet(float[] array, float value, int length) {
        java.util.Arrays.fill(array, 0, length, value);
    }

    static void MemSetWithOffset(byte[] array, byte value, int offset, int length) {
        java.util.Arrays.fill(array, offset, offset + length, value);
    }

    static void MemSetWithOffset(short[] array, short value, int offset, int length) {
        java.util.Arrays.fill(array, offset, offset + length, value);
    }

    static void MemSetWithOffset(int[] array, int value, int offset, int length) {
        java.util.Arrays.fill(array, offset, offset + length, value);
    }

    static void MemMove(byte[] array, int src_idx, int dst_idx, int length) {
        System.arraycopy(array, src_idx, array, dst_idx, length);
    }

    static void MemMove(short[] array, int src_idx, int dst_idx, int length) {
        System.arraycopy(array, src_idx, array, dst_idx, length);
    }

    static void MemMove(int[] array, int src_idx, int dst_idx, int length) {
        System.arraycopy(array, src_idx, array, dst_idx, length);
    }
}
