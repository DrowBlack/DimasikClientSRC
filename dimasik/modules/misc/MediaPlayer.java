package dimasik.modules.misc;

import dimasik.events.api.EventListener;
import dimasik.events.main.input.EventInput;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.BindOption;
import dimasik.utils.client.MediaTracker;

public class MediaPlayer
extends Module {
    private final BindOption pause = new BindOption("\u041f\u0430\u0443\u0437\u0430", -1);
    private final BindOption start = new BindOption("\u0421\u0442\u0430\u0440", -1);
    private final BindOption skip = new BindOption("\u041f\u0440\u043e\u043f\u0443\u0441\u0442\u0438\u0442\u044c", -1);
    private final BindOption back = new BindOption("\u0412\u0435\u0440\u043d\u0443\u0442\u044c\u0441\u044f", -1);
    private final BindOption mute = new BindOption("\u0417\u0430\u0433\u043b\u0443\u0448\u0438\u0442\u044c", -1);
    private final BindOption vol_up = new BindOption("\u0413\u0440\u043e\u043c\u0447\u0435", -1);
    private final BindOption vol_down = new BindOption("\u0422\u0438\u0448\u0435", -1);
    private final EventListener<EventInput> input = this::input;
    private boolean isSetPause = false;
    private boolean isSetStart = false;
    private boolean isSetSkip = false;
    private boolean isSetBack = false;
    private boolean isSetMute = false;
    private boolean isSetVol_up = false;
    private boolean isSetVol_down = false;

    public MediaPlayer() {
        super("MediaPlayer", Category.MISC);
        this.settings(this.pause);
    }

    public void input(EventInput eventInput) {
        if (this.pause.getKey() == eventInput.getKey()) {
            MediaTracker.MediaInfo info = MediaTracker.getSystemMediaInfo();
            if (info.isPlaying()) {
                System.out.println("\u25b6\ufe0f  \u0421\u0435\u0439\u0447\u0430\u0441 \u0438\u0433\u0440\u0430\u0435\u0442: " + info.getArtist() + " - " + info.getTitle());
            } else if (info.isPaused()) {
                System.out.println("\u23f8\ufe0f  \u041d\u0430 \u043f\u0430\u0443\u0437\u0435: " + info.getArtist() + " - " + info.getTitle());
            } else {
                System.out.println("\ud83d\udd07 " + info.getMessage());
            }
        }
    }
}
