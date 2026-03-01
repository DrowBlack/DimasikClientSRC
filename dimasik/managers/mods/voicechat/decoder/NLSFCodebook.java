package dimasik.managers.mods.voicechat.decoder;

class NLSFCodebook {
    short nVectors = 0;
    short order = 0;
    short quantStepSize_Q16 = 0;
    short invQuantStepSize_Q6 = 0;
    short[] CB1_NLSF_Q8 = null;
    short[] CB1_iCDF = null;
    short[] pred_Q8 = null;
    short[] ec_sel = null;
    short[] ec_iCDF = null;
    short[] ec_Rates_Q5 = null;
    short[] deltaMin_Q15 = null;

    NLSFCodebook() {
    }

    void Reset() {
        this.nVectors = 0;
        this.order = 0;
        this.quantStepSize_Q16 = 0;
        this.invQuantStepSize_Q6 = 0;
        this.CB1_NLSF_Q8 = null;
        this.CB1_iCDF = null;
        this.pred_Q8 = null;
        this.ec_sel = null;
        this.ec_iCDF = null;
        this.ec_Rates_Q5 = null;
        this.deltaMin_Q15 = null;
    }
}
