package net.minecraft.client.resources;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.resources.Language;
import net.minecraft.client.util.BidiReorderer;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.LanguageMap;
import net.optifine.Lang;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientLanguageMap
extends LanguageMap {
    private static final Logger field_239493_a_ = LogManager.getLogger();
    private final Map<String, String> field_239495_c_;
    private final boolean field_239496_d_;

    private ClientLanguageMap(Map<String, String> p_i232487_1_, boolean p_i232487_2_) {
        this.field_239495_c_ = p_i232487_1_;
        this.field_239496_d_ = p_i232487_2_;
    }

    public static ClientLanguageMap func_239497_a_(IResourceManager p_239497_0_, List<Language> p_239497_1_) {
        HashMap<String, String> map = Maps.newHashMap();
        boolean flag = false;
        for (Language language : p_239497_1_) {
            flag |= language.isBidirectional();
            String s = String.format("lang/%s.json", language.getCode());
            for (String s1 : p_239497_0_.getResourceNamespaces()) {
                try {
                    ResourceLocation resourcelocation = new ResourceLocation(s1, s);
                    ClientLanguageMap.func_239498_a_(p_239497_0_.getAllResources(resourcelocation), map);
                    Lang.loadResources(p_239497_0_, language.getCode(), map);
                }
                catch (FileNotFoundException resourcelocation) {
                }
                catch (Exception exception1) {
                    field_239493_a_.warn("Skipped language file: {}:{} ({})", (Object)s1, (Object)s, (Object)exception1.toString());
                }
            }
        }
        return new ClientLanguageMap(ImmutableMap.copyOf(map), flag);
    }

    private static void func_239498_a_(List<IResource> p_239498_0_, Map<String, String> p_239498_1_) {
        for (IResource iresource : p_239498_0_) {
            try {
                InputStream inputstream = iresource.getInputStream();
                try {
                    LanguageMap.func_240593_a_(inputstream, p_239498_1_::put);
                }
                finally {
                    if (inputstream == null) continue;
                    inputstream.close();
                }
            }
            catch (IOException ioexception1) {
                field_239493_a_.warn("Failed to load translations from {}", (Object)iresource, (Object)ioexception1);
            }
        }
    }

    @Override
    public String func_230503_a_(String p_230503_1_) {
        return this.field_239495_c_.getOrDefault(p_230503_1_, p_230503_1_);
    }

    @Override
    public boolean func_230506_b_(String p_230506_1_) {
        return this.field_239495_c_.containsKey(p_230506_1_);
    }

    @Override
    public boolean func_230505_b_() {
        return this.field_239496_d_;
    }

    @Override
    public IReorderingProcessor func_241870_a(ITextProperties p_241870_1_) {
        return BidiReorderer.func_243508_a(p_241870_1_, this.field_239496_d_);
    }

    public Map<String, String> getLanguageData() {
        return this.field_239495_c_;
    }
}
