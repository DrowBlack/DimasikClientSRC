package dimasik.helpers.interfaces;

import dimasik.helpers.translate.TranslateHelpers;

public interface ITranslate {
    default public String getTranslation(String text) {
        return new TranslateHelpers().has(text) ? new TranslateHelpers().get(text) : text;
    }
}
