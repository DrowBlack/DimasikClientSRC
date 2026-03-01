package com.jhlabs.math;

import com.jhlabs.math.Function1D;
import com.jhlabs.math.Function2D;
import com.jhlabs.math.Function3D;
import java.util.Random;

public class SCNoise
implements Function1D,
Function2D,
Function3D {
    private static Random randomGenerator = new Random();
    public short[] perm;
    private static final int TABSIZE = 256;
    private static final int TABMASK = 255;
    private static final int NIMPULSES = 3;
    private static float[] impulseTab;
    private static final int SAMPRATE = 100;
    private static final int NENTRIES = 401;
    private static float[] table;

    public SCNoise() {
        short[] sArray = new short[256];
        sArray[0] = 225;
        sArray[1] = 155;
        sArray[2] = 210;
        sArray[3] = 108;
        sArray[4] = 175;
        sArray[5] = 199;
        sArray[6] = 221;
        sArray[7] = 144;
        sArray[8] = 203;
        sArray[9] = 116;
        sArray[10] = 70;
        sArray[11] = 213;
        sArray[12] = 69;
        sArray[13] = 158;
        sArray[14] = 33;
        sArray[15] = 252;
        sArray[16] = 5;
        sArray[17] = 82;
        sArray[18] = 173;
        sArray[19] = 133;
        sArray[20] = 222;
        sArray[21] = 139;
        sArray[22] = 174;
        sArray[23] = 27;
        sArray[24] = 9;
        sArray[25] = 71;
        sArray[26] = 90;
        sArray[27] = 246;
        sArray[28] = 75;
        sArray[29] = 130;
        sArray[30] = 91;
        sArray[31] = 191;
        sArray[32] = 169;
        sArray[33] = 138;
        sArray[34] = 2;
        sArray[35] = 151;
        sArray[36] = 194;
        sArray[37] = 235;
        sArray[38] = 81;
        sArray[39] = 7;
        sArray[40] = 25;
        sArray[41] = 113;
        sArray[42] = 228;
        sArray[43] = 159;
        sArray[44] = 205;
        sArray[45] = 253;
        sArray[46] = 134;
        sArray[47] = 142;
        sArray[48] = 248;
        sArray[49] = 65;
        sArray[50] = 224;
        sArray[51] = 217;
        sArray[52] = 22;
        sArray[53] = 121;
        sArray[54] = 229;
        sArray[55] = 63;
        sArray[56] = 89;
        sArray[57] = 103;
        sArray[58] = 96;
        sArray[59] = 104;
        sArray[60] = 156;
        sArray[61] = 17;
        sArray[62] = 201;
        sArray[63] = 129;
        sArray[64] = 36;
        sArray[65] = 8;
        sArray[66] = 165;
        sArray[67] = 110;
        sArray[68] = 237;
        sArray[69] = 117;
        sArray[70] = 231;
        sArray[71] = 56;
        sArray[72] = 132;
        sArray[73] = 211;
        sArray[74] = 152;
        sArray[75] = 20;
        sArray[76] = 181;
        sArray[77] = 111;
        sArray[78] = 239;
        sArray[79] = 218;
        sArray[80] = 170;
        sArray[81] = 163;
        sArray[82] = 51;
        sArray[83] = 172;
        sArray[84] = 157;
        sArray[85] = 47;
        sArray[86] = 80;
        sArray[87] = 212;
        sArray[88] = 176;
        sArray[89] = 250;
        sArray[90] = 87;
        sArray[91] = 49;
        sArray[92] = 99;
        sArray[93] = 242;
        sArray[94] = 136;
        sArray[95] = 189;
        sArray[96] = 162;
        sArray[97] = 115;
        sArray[98] = 44;
        sArray[99] = 43;
        sArray[100] = 124;
        sArray[101] = 94;
        sArray[102] = 150;
        sArray[103] = 16;
        sArray[104] = 141;
        sArray[105] = 247;
        sArray[106] = 32;
        sArray[107] = 10;
        sArray[108] = 198;
        sArray[109] = 223;
        sArray[110] = 255;
        sArray[111] = 72;
        sArray[112] = 53;
        sArray[113] = 131;
        sArray[114] = 84;
        sArray[115] = 57;
        sArray[116] = 220;
        sArray[117] = 197;
        sArray[118] = 58;
        sArray[119] = 50;
        sArray[120] = 208;
        sArray[121] = 11;
        sArray[122] = 241;
        sArray[123] = 28;
        sArray[124] = 3;
        sArray[125] = 192;
        sArray[126] = 62;
        sArray[127] = 202;
        sArray[128] = 18;
        sArray[129] = 215;
        sArray[130] = 153;
        sArray[131] = 24;
        sArray[132] = 76;
        sArray[133] = 41;
        sArray[134] = 15;
        sArray[135] = 179;
        sArray[136] = 39;
        sArray[137] = 46;
        sArray[138] = 55;
        sArray[139] = 6;
        sArray[140] = 128;
        sArray[141] = 167;
        sArray[142] = 23;
        sArray[143] = 188;
        sArray[144] = 106;
        sArray[145] = 34;
        sArray[146] = 187;
        sArray[147] = 140;
        sArray[148] = 164;
        sArray[149] = 73;
        sArray[150] = 112;
        sArray[151] = 182;
        sArray[152] = 244;
        sArray[153] = 195;
        sArray[154] = 227;
        sArray[155] = 13;
        sArray[156] = 35;
        sArray[157] = 77;
        sArray[158] = 196;
        sArray[159] = 185;
        sArray[160] = 26;
        sArray[161] = 200;
        sArray[162] = 226;
        sArray[163] = 119;
        sArray[164] = 31;
        sArray[165] = 123;
        sArray[166] = 168;
        sArray[167] = 125;
        sArray[168] = 249;
        sArray[169] = 68;
        sArray[170] = 183;
        sArray[171] = 230;
        sArray[172] = 177;
        sArray[173] = 135;
        sArray[174] = 160;
        sArray[175] = 180;
        sArray[176] = 12;
        sArray[177] = 1;
        sArray[178] = 243;
        sArray[179] = 148;
        sArray[180] = 102;
        sArray[181] = 166;
        sArray[182] = 38;
        sArray[183] = 238;
        sArray[184] = 251;
        sArray[185] = 37;
        sArray[186] = 240;
        sArray[187] = 126;
        sArray[188] = 64;
        sArray[189] = 74;
        sArray[190] = 161;
        sArray[191] = 40;
        sArray[192] = 184;
        sArray[193] = 149;
        sArray[194] = 171;
        sArray[195] = 178;
        sArray[196] = 101;
        sArray[197] = 66;
        sArray[198] = 29;
        sArray[199] = 59;
        sArray[200] = 146;
        sArray[201] = 61;
        sArray[202] = 254;
        sArray[203] = 107;
        sArray[204] = 42;
        sArray[205] = 86;
        sArray[206] = 154;
        sArray[207] = 4;
        sArray[208] = 236;
        sArray[209] = 232;
        sArray[210] = 120;
        sArray[211] = 21;
        sArray[212] = 233;
        sArray[213] = 209;
        sArray[214] = 45;
        sArray[215] = 98;
        sArray[216] = 193;
        sArray[217] = 114;
        sArray[218] = 78;
        sArray[219] = 19;
        sArray[220] = 206;
        sArray[221] = 14;
        sArray[222] = 118;
        sArray[223] = 127;
        sArray[224] = 48;
        sArray[225] = 79;
        sArray[226] = 147;
        sArray[227] = 85;
        sArray[228] = 30;
        sArray[229] = 207;
        sArray[230] = 219;
        sArray[231] = 54;
        sArray[232] = 88;
        sArray[233] = 234;
        sArray[234] = 190;
        sArray[235] = 122;
        sArray[236] = 95;
        sArray[237] = 67;
        sArray[238] = 143;
        sArray[239] = 109;
        sArray[240] = 137;
        sArray[241] = 214;
        sArray[242] = 145;
        sArray[243] = 93;
        sArray[244] = 92;
        sArray[245] = 100;
        sArray[246] = 245;
        sArray[248] = 216;
        sArray[249] = 186;
        sArray[250] = 60;
        sArray[251] = 83;
        sArray[252] = 105;
        sArray[253] = 97;
        sArray[254] = 204;
        sArray[255] = 52;
        this.perm = sArray;
    }

    @Override
    public float evaluate(float x) {
        return this.evaluate(x, 0.1f);
    }

    @Override
    public float evaluate(float x, float y) {
        float sum = 0.0f;
        if (impulseTab == null) {
            impulseTab = SCNoise.impulseTabInit(665);
        }
        int ix = SCNoise.floor(x);
        float fx = x - (float)ix;
        int iy = SCNoise.floor(y);
        float fy = y - (float)iy;
        int m = 2;
        int i = -m;
        while (i <= m) {
            int j = -m;
            while (j <= m) {
                int h = this.perm[ix + i + this.perm[iy + j & 0xFF] & 0xFF];
                int n = 3;
                while (n > 0) {
                    int h4 = h * 4;
                    float dx = fx - ((float)i + impulseTab[h4++]);
                    float dy = fy - ((float)j + impulseTab[h4++]);
                    float distsq = dx * dx + dy * dy;
                    sum += this.catrom2(distsq) * impulseTab[h4];
                    --n;
                    h = h + 1 & 0xFF;
                }
                ++j;
            }
            ++i;
        }
        return sum / 3.0f;
    }

    @Override
    public float evaluate(float x, float y, float z) {
        float sum = 0.0f;
        if (impulseTab == null) {
            impulseTab = SCNoise.impulseTabInit(665);
        }
        int ix = SCNoise.floor(x);
        float fx = x - (float)ix;
        int iy = SCNoise.floor(y);
        float fy = y - (float)iy;
        int iz = SCNoise.floor(z);
        float fz = z - (float)iz;
        int m = 2;
        int i = -m;
        while (i <= m) {
            int j = -m;
            while (j <= m) {
                int k = -m;
                while (k <= m) {
                    int h = this.perm[ix + i + this.perm[iy + j + this.perm[iz + k & 0xFF] & 0xFF] & 0xFF];
                    int n = 3;
                    while (n > 0) {
                        int h4 = h * 4;
                        float dx = fx - ((float)i + impulseTab[h4++]);
                        float dy = fy - ((float)j + impulseTab[h4++]);
                        float dz = fz - ((float)k + impulseTab[h4++]);
                        float distsq = dx * dx + dy * dy + dz * dz;
                        sum += this.catrom2(distsq) * impulseTab[h4];
                        --n;
                        h = h + 1 & 0xFF;
                    }
                    ++k;
                }
                ++j;
            }
            ++i;
        }
        return sum / 3.0f;
    }

    public static int floor(float x) {
        int ix = (int)x;
        if (x < 0.0f && x != (float)ix) {
            return ix - 1;
        }
        return ix;
    }

    public float catrom2(float d) {
        int i;
        if (d >= 4.0f) {
            return 0.0f;
        }
        if (table == null) {
            table = new float[401];
            i = 0;
            while (i < 401) {
                float x = (float)i / 100.0f;
                SCNoise.table[i] = (x = (float)Math.sqrt(x)) < 1.0f ? 0.5f * (2.0f + x * x * (-5.0f + x * 3.0f)) : 0.5f * (4.0f + x * (-8.0f + x * (5.0f - x)));
                ++i;
            }
        }
        if ((i = SCNoise.floor(d = d * 100.0f + 0.5f)) >= 401) {
            return 0.0f;
        }
        return table[i];
    }

    static float[] impulseTabInit(int seed) {
        float[] impulseTab = new float[1024];
        randomGenerator = new Random(seed);
        int i = 0;
        while (i < 256) {
            impulseTab[i++] = randomGenerator.nextFloat();
            impulseTab[i++] = randomGenerator.nextFloat();
            impulseTab[i++] = randomGenerator.nextFloat();
            impulseTab[i++] = 1.0f - 2.0f * randomGenerator.nextFloat();
            ++i;
        }
        return impulseTab;
    }
}
