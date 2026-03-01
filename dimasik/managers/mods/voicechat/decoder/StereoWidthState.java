package dimasik.managers.mods.voicechat.decoder;

class StereoWidthState {
    int XX;
    int XY;
    int YY;
    int smoothed_width;
    int max_follower;

    StereoWidthState() {
    }

    void Reset() {
        this.XX = 0;
        this.XY = 0;
        this.YY = 0;
        this.smoothed_width = 0;
        this.max_follower = 0;
    }
}
