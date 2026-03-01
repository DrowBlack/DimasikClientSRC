package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Inlines;

class LinearAlgebra {
    LinearAlgebra() {
    }

    static void silk_solve_LDL(int[] A, int A_ptr, int M, int[] b, int[] x_Q16) {
        Inlines.OpusAssert(M <= 16);
        int[] L_Q16 = new int[M * M];
        int[] Y = new int[16];
        int[] inv_D = new int[32];
        LinearAlgebra.silk_LDL_factorize(A, A_ptr, M, L_Q16, inv_D);
        LinearAlgebra.silk_LS_SolveFirst(L_Q16, M, b, Y);
        LinearAlgebra.silk_LS_divide_Q16(Y, inv_D, M);
        LinearAlgebra.silk_LS_SolveLast(L_Q16, M, Y, x_Q16);
    }

    private static void silk_LDL_factorize(int[] A, int A_ptr, int M, int[] L_Q16, int[] inv_D) {
        int[] v_Q0 = new int[M];
        int[] D_Q0 = new int[M];
        Inlines.OpusAssert(M <= 16);
        boolean status = true;
        int diag_min_value = Inlines.silk_max_32(Inlines.silk_SMMUL(Inlines.silk_ADD_SAT32(A[A_ptr], A[A_ptr + Inlines.silk_SMULBB(M, M) - 1]), 21475), 512);
        block0: for (int loop_count = 0; loop_count < M && status; ++loop_count) {
            status = false;
            for (int j = 0; j < M; ++j) {
                int i;
                int[] scratch1 = L_Q16;
                int scratch1_ptr = Inlines.MatrixGetPointer(j, 0, M);
                int tmp_32 = 0;
                for (i = 0; i < j; ++i) {
                    v_Q0[i] = Inlines.silk_SMULWW(D_Q0[i], scratch1[scratch1_ptr + i]);
                    tmp_32 = Inlines.silk_SMLAWW(tmp_32, v_Q0[i], scratch1[scratch1_ptr + i]);
                }
                tmp_32 = Inlines.silk_SUB32(Inlines.MatrixGet(A, A_ptr, j, j, M), tmp_32);
                if (tmp_32 < diag_min_value) {
                    tmp_32 = Inlines.silk_SUB32(Inlines.silk_SMULBB(loop_count + 1, diag_min_value), tmp_32);
                    for (i = 0; i < M; ++i) {
                        Inlines.MatrixSet(A, A_ptr, i, i, M, Inlines.silk_ADD32(Inlines.MatrixGet(A, A_ptr, i, i, M), tmp_32));
                    }
                    status = true;
                    continue block0;
                }
                D_Q0[j] = tmp_32;
                int one_div_diag_Q36 = Inlines.silk_INVERSE32_varQ(tmp_32, 36);
                int one_div_diag_Q40 = Inlines.silk_LSHIFT(one_div_diag_Q36, 4);
                int err = Inlines.silk_SUB32(0x1000000, Inlines.silk_SMULWW(tmp_32, one_div_diag_Q40));
                int one_div_diag_Q48 = Inlines.silk_SMULWW(err, one_div_diag_Q40);
                inv_D[j * 2 + 0] = one_div_diag_Q36;
                inv_D[j * 2 + 1] = one_div_diag_Q48;
                Inlines.MatrixSet(L_Q16, j, j, M, 65536);
                scratch1 = A;
                scratch1_ptr = Inlines.MatrixGetPointer(j, 0, M) + A_ptr;
                int[] scratch2 = L_Q16;
                int scratch2_ptr = Inlines.MatrixGetPointer(j + 1, 0, M);
                for (i = j + 1; i < M; ++i) {
                    tmp_32 = 0;
                    for (int k = 0; k < j; ++k) {
                        tmp_32 = Inlines.silk_SMLAWW(tmp_32, v_Q0[k], scratch2[scratch2_ptr + k]);
                    }
                    tmp_32 = Inlines.silk_SUB32(scratch1[scratch1_ptr + i], tmp_32);
                    Inlines.MatrixSet(L_Q16, i, j, M, Inlines.silk_ADD32(Inlines.silk_SMMUL(tmp_32, one_div_diag_Q48), Inlines.silk_RSHIFT(Inlines.silk_SMULWW(tmp_32, one_div_diag_Q36), 4)));
                    scratch2_ptr += M;
                }
            }
        }
        Inlines.OpusAssert(!status);
    }

    private static void silk_LS_divide_Q16(int[] T, int[] inv_D, int M) {
        for (int i = 0; i < M; ++i) {
            int one_div_diag_Q36 = inv_D[i * 2 + 0];
            int one_div_diag_Q48 = inv_D[i * 2 + 1];
            int tmp_32 = T[i];
            T[i] = Inlines.silk_ADD32(Inlines.silk_SMMUL(tmp_32, one_div_diag_Q48), Inlines.silk_RSHIFT(Inlines.silk_SMULWW(tmp_32, one_div_diag_Q36), 4));
        }
    }

    private static void silk_LS_SolveFirst(int[] L_Q16, int M, int[] b, int[] x_Q16) {
        for (int i = 0; i < M; ++i) {
            int ptr32 = Inlines.MatrixGetPointer(i, 0, M);
            int tmp_32 = 0;
            for (int j = 0; j < i; ++j) {
                tmp_32 = Inlines.silk_SMLAWW(tmp_32, L_Q16[ptr32 + j], x_Q16[j]);
            }
            x_Q16[i] = Inlines.silk_SUB32(b[i], tmp_32);
        }
    }

    private static void silk_LS_SolveLast(int[] L_Q16, int M, int[] b, int[] x_Q16) {
        for (int i = M - 1; i >= 0; --i) {
            int ptr32 = Inlines.MatrixGetPointer(0, i, M);
            int tmp_32 = 0;
            for (int j = M - 1; j > i; --j) {
                tmp_32 = Inlines.silk_SMLAWW(tmp_32, L_Q16[ptr32 + Inlines.silk_SMULBB(j, M)], x_Q16[j]);
            }
            x_Q16[i] = Inlines.silk_SUB32(b[i], tmp_32);
        }
    }
}
