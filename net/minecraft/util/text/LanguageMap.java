package net.minecraft.util.text;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextProcessing;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class LanguageMap {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson field_240591_b_ = new Gson();
    private static final Pattern NUMERIC_VARIABLE_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d.]*[df]");
    private static volatile LanguageMap field_240592_d_ = LanguageMap.func_240595_c_();

    private static LanguageMap func_240595_c_() {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        BiConsumer<String, String> biconsumer = builder::put;
        try (InputStream inputstream = LanguageMap.class.getResourceAsStream("/assets/minecraft/lang/en_us.json");){
            LanguageMap.func_240593_a_(inputstream, biconsumer);
        }
        catch (JsonParseException | IOException ioexception) {
            LOGGER.error("Couldn't read strings from /assets/minecraft/lang/en_us.json", (Throwable)ioexception);
        }
        final ImmutableMap map = builder.build();
        return new LanguageMap(){

            @Override
            public String func_230503_a_(String p_230503_1_) {
                return map.getOrDefault(p_230503_1_, p_230503_1_);
            }

            @Override
            public boolean func_230506_b_(String p_230506_1_) {
                return map.containsKey(p_230506_1_);
            }

            @Override
            public boolean func_230505_b_() {
                return false;
            }

            @Override
            public IReorderingProcessor func_241870_a(ITextProperties p_241870_1_) {
                return p_244262_1_ -> p_241870_1_.getComponentWithStyle((p_244261_1_, p_244261_2_) -> TextProcessing.func_238346_c_(p_244261_2_, p_244261_1_, p_244262_1_) ? Optional.empty() : ITextProperties.field_240650_b_, Style.EMPTY).isPresent();
            }
        };
    }

    public static void func_240593_a_(InputStream p_240593_0_, BiConsumer<String, String> p_240593_1_) {
        JsonObject jsonobject = field_240591_b_.fromJson((Reader)new InputStreamReader(p_240593_0_, StandardCharsets.UTF_8), JsonObject.class);
        for (Map.Entry<String, JsonElement> entry : jsonobject.entrySet()) {
            String s = NUMERIC_VARIABLE_PATTERN.matcher(JSONUtils.getString(entry.getValue(), entry.getKey())).replaceAll("%$1s");
            p_240593_1_.accept(entry.getKey(), s);
        }
    }

    public static LanguageMap getInstance() {
        return field_240592_d_;
    }

    public static void func_240594_a_(LanguageMap p_240594_0_) {
        field_240592_d_ = p_240594_0_;
    }

    public abstract String func_230503_a_(String var1);

    public abstract boolean func_230506_b_(String var1);

    public abstract boolean func_230505_b_();

    public abstract IReorderingProcessor func_241870_a(ITextProperties var1);

    public List<IReorderingProcessor> func_244260_a(List<ITextProperties> p_244260_1_) {
        return p_244260_1_.stream().map(LanguageMap.getInstance()::func_241870_a).collect(ImmutableList.toImmutableList());
    }
}
