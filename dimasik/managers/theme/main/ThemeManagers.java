package dimasik.managers.theme.main;

import dimasik.helpers.render.ColorHelpers;
import dimasik.managers.theme.api.Theme;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Generated;
import ru.dreamix.class2native.CompileNativeCalls;
import ru.dreamix.class2native.experimental.AdditionalReferenceProtection;
import ru.dreamix.class2native.experimental.AdditionalReferenceProtectionType;

@AdditionalReferenceProtection(value=AdditionalReferenceProtectionType.GENERATIVE_CONFUSION)
public class ThemeManagers {
    public List<Theme> themes = new ArrayList<Theme>();
    private Theme currentTheme = null;

    public ThemeManagers() {
        this.init();
    }

    @CompileNativeCalls
    public void init() {
        this.themes.addAll(Arrays.asList(new Theme("Client", ColorHelpers.rgba(117, 93, 154, 255), ColorHelpers.rgba(40, 31, 52, 255)), new Theme("Dimasik", ColorHelpers.rgba(48, 207, 151, 255), ColorHelpers.rgba(24, 103, 75, 255)), new Theme("Custom", ColorHelpers.rgba(255, 255, 255, 255), ColorHelpers.rgba(255, 255, 255, 255))));
        this.currentTheme = this.themes.get(0);
    }

    @Generated
    public Theme getCurrentTheme() {
        return this.currentTheme;
    }

    @Generated
    public void setCurrentTheme(Theme currentTheme) {
        this.currentTheme = currentTheme;
    }
}
