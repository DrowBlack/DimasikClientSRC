package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Filters;
import dimasik.managers.mods.voicechat.decoder.Inlines;

class SilkLPState {
    final int[] In_LP_State = new int[2];
    int transition_frame_no = 0;
    int mode = 0;

    SilkLPState() {
    }

    void Reset() {
        this.In_LP_State[0] = 0;
        this.In_LP_State[1] = 0;
        this.transition_frame_no = 0;
        this.mode = 0;
    }

    void silk_LP_variable_cutoff(short[] frame, int frame_ptr, int frame_length) {
        int[] B_Q28 = new int[3];
        int[] A_Q28 = new int[2];
        int fac_Q16 = 0;
        int ind = 0;
        Inlines.OpusAssert(this.transition_frame_no >= 0 && this.transition_frame_no <= 256);
        if (this.mode != 0) {
            fac_Q16 = Inlines.silk_LSHIFT(256 - this.transition_frame_no, 10);
            ind = Inlines.silk_RSHIFT(fac_Q16, 16);
            Inlines.OpusAssert(ind >= 0);
            Inlines.OpusAssert(ind < 5);
            Filters.silk_LP_interpolate_filter_taps(B_Q28, A_Q28, ind, fac_Q16 -= Inlines.silk_LSHIFT(ind, 16));
            this.transition_frame_no = Inlines.silk_LIMIT(this.transition_frame_no + this.mode, 0, 256);
            Inlines.OpusAssert(true);
            Filters.silk_biquad_alt(frame, frame_ptr, B_Q28, A_Q28, this.In_LP_State, frame, frame_ptr, frame_length, 1);
        }
    }
}
