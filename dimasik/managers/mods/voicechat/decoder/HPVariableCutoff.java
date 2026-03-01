package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Inlines;
import dimasik.managers.mods.voicechat.decoder.SilkChannelEncoder;

class HPVariableCutoff {
    HPVariableCutoff() {
    }

    static void silk_HP_variable_cutoff(SilkChannelEncoder[] state_Fxx) {
        SilkChannelEncoder psEncC1 = state_Fxx[0];
        if (psEncC1.prevSignalType == 2) {
            int pitch_freq_Hz_Q16 = Inlines.silk_DIV32_16(Inlines.silk_LSHIFT(Inlines.silk_MUL(psEncC1.fs_kHz, 1000), 16), psEncC1.prevLag);
            int pitch_freq_log_Q7 = Inlines.silk_lin2log(pitch_freq_Hz_Q16) - 2048;
            int quality_Q15 = psEncC1.input_quality_bands_Q15[0];
            int delta_freq_Q7 = (pitch_freq_log_Q7 = Inlines.silk_SMLAWB(pitch_freq_log_Q7, Inlines.silk_SMULWB(Inlines.silk_LSHIFT(-quality_Q15, 2), quality_Q15), pitch_freq_log_Q7 - (Inlines.silk_lin2log(0x3C0000) - 2048))) - Inlines.silk_RSHIFT(psEncC1.variable_HP_smth1_Q15, 8);
            if (delta_freq_Q7 < 0) {
                delta_freq_Q7 = Inlines.silk_MUL(delta_freq_Q7, 3);
            }
            delta_freq_Q7 = Inlines.silk_LIMIT_32(delta_freq_Q7, -51, 51);
            psEncC1.variable_HP_smth1_Q15 = Inlines.silk_SMLAWB(psEncC1.variable_HP_smth1_Q15, Inlines.silk_SMULBB(psEncC1.speech_activity_Q8, delta_freq_Q7), 6554);
            psEncC1.variable_HP_smth1_Q15 = Inlines.silk_LIMIT_32(psEncC1.variable_HP_smth1_Q15, Inlines.silk_LSHIFT(Inlines.silk_lin2log(60), 8), Inlines.silk_LSHIFT(Inlines.silk_lin2log(100), 8));
        }
    }
}
