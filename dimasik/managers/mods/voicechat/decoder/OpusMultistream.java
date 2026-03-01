package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.ChannelLayout;

class OpusMultistream {
    OpusMultistream() {
    }

    static int validate_layout(ChannelLayout layout) {
        int max_channel = layout.nb_streams + layout.nb_coupled_streams;
        if (max_channel > 255) {
            return 0;
        }
        for (int i = 0; i < layout.nb_channels; ++i) {
            if (layout.mapping[i] < max_channel || layout.mapping[i] == 255) continue;
            return 0;
        }
        return 1;
    }

    static int get_left_channel(ChannelLayout layout, int stream_id, int prev) {
        int i;
        int n = i = prev < 0 ? 0 : prev + 1;
        while (i < layout.nb_channels) {
            if (layout.mapping[i] == stream_id * 2) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    static int get_right_channel(ChannelLayout layout, int stream_id, int prev) {
        int i;
        int n = i = prev < 0 ? 0 : prev + 1;
        while (i < layout.nb_channels) {
            if (layout.mapping[i] == stream_id * 2 + 1) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    static int get_mono_channel(ChannelLayout layout, int stream_id, int prev) {
        int i;
        int n = i = prev < 0 ? 0 : prev + 1;
        while (i < layout.nb_channels) {
            if (layout.mapping[i] == stream_id + layout.nb_coupled_streams) {
                return i;
            }
            ++i;
        }
        return -1;
    }
}
