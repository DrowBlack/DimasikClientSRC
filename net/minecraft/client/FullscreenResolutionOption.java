package net.minecraft.client;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.GameSettings;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Monitor;
import net.minecraft.client.renderer.VideoMode;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class FullscreenResolutionOption
extends SliderPercentageOption {
    public FullscreenResolutionOption(MainWindow mainWindowIn) {
        this(mainWindowIn, mainWindowIn.getMonitor());
    }

    private FullscreenResolutionOption(MainWindow mainWindowIn, @Nullable Monitor monitorIn) {
        super("options.fullscreen.resolution", -1.0, monitorIn != null ? (double)(monitorIn.getVideoModeCount() - 1) : -1.0, 1.0f, (GameSettings p_225306_2_) -> {
            if (monitorIn == null) {
                return -1.0;
            }
            Optional<VideoMode> optional = mainWindowIn.getVideoMode();
            return optional.map(p_225304_1_ -> monitorIn.getVideoModeIndex((VideoMode)p_225304_1_)).orElse(-1.0);
        }, (GameSettings p_225303_2_, Double p_225303_3_) -> {
            if (monitorIn != null) {
                if (p_225303_3_ == -1.0) {
                    mainWindowIn.setVideoMode(Optional.empty());
                } else {
                    mainWindowIn.setVideoMode(Optional.of(monitorIn.getVideoModeFromIndex(p_225303_3_.intValue())));
                }
            }
        }, (GameSettings p_225305_1_, SliderPercentageOption p_225305_2_) -> {
            if (monitorIn == null) {
                return new TranslationTextComponent("options.fullscreen.unavailable");
            }
            double d0 = p_225305_2_.get((GameSettings)p_225305_1_);
            return d0 == -1.0 ? p_225305_2_.getGenericValueComponent(new TranslationTextComponent("options.fullscreen.current")) : p_225305_2_.getGenericValueComponent(new StringTextComponent(monitorIn.getVideoModeFromIndex((int)d0).toString()));
        });
    }
}
