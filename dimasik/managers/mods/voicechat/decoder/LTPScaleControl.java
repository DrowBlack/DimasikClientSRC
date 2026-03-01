package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Inlines;
import dimasik.managers.mods.voicechat.decoder.SilkChannelEncoder;
import dimasik.managers.mods.voicechat.decoder.SilkEncoderControl;
import dimasik.managers.mods.voicechat.decoder.SilkTables;

class LTPScaleControl {
    LTPScaleControl() {
    }

    static void silk_LTP_scale_ctrl(SilkChannelEncoder psEnc, SilkEncoderControl psEncCtrl, int condCoding) {
        if (condCoding == 0) {
            int round_loss = psEnc.PacketLoss_perc + psEnc.nFramesPerPacket;
            psEnc.indices.LTP_scaleIndex = (byte)Inlines.silk_LIMIT(Inlines.silk_SMULWB(Inlines.silk_SMULBB(round_loss, psEncCtrl.LTPredCodGain_Q7), 51), 0, 2);
        } else {
            psEnc.indices.LTP_scaleIndex = 0;
        }
        psEncCtrl.LTP_scale_Q14 = SilkTables.silk_LTPScales_table_Q14[psEnc.indices.LTP_scaleIndex];
    }
}
