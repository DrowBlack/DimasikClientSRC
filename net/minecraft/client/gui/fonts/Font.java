package net.minecraft.client.gui.fonts;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.fonts.DefaultGlyph;
import net.minecraft.client.gui.fonts.EmptyGlyph;
import net.minecraft.client.gui.fonts.FontTexture;
import net.minecraft.client.gui.fonts.IGlyph;
import net.minecraft.client.gui.fonts.IGlyphInfo;
import net.minecraft.client.gui.fonts.TexturedGlyph;
import net.minecraft.client.gui.fonts.WhiteGlyph;
import net.minecraft.client.gui.fonts.providers.IGlyphProvider;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class Font
implements AutoCloseable {
    private static final EmptyGlyph EMPTY_GLYPH = new EmptyGlyph();
    private static final IGlyph GLYPH_ADVANCE_SPACE = () -> 4.0f;
    private static final Random RANDOM = new Random();
    private final TextureManager textureManager;
    private final ResourceLocation id;
    private TexturedGlyph fallbackGlyph;
    private TexturedGlyph whiteGlyph;
    private final List<IGlyphProvider> glyphProviders = Lists.newArrayList();
    private final Int2ObjectMap<TexturedGlyph> mapTexturedGlyphs = new Int2ObjectOpenHashMap<TexturedGlyph>();
    private final Int2ObjectMap<IGlyph> glyphs = new Int2ObjectOpenHashMap<IGlyph>();
    private final Int2ObjectMap<IntList> glyphsByWidth = new Int2ObjectOpenHashMap<IntList>();
    private final List<FontTexture> textures = Lists.newArrayList();

    public Font(TextureManager textureManagerIn, ResourceLocation resourceLocationIn) {
        this.textureManager = textureManagerIn;
        this.id = resourceLocationIn;
    }

    public void setGlyphProviders(List<IGlyphProvider> glyphProvidersIn) {
        this.func_230154_b_();
        this.deleteTextures();
        this.mapTexturedGlyphs.clear();
        this.glyphs.clear();
        this.glyphsByWidth.clear();
        this.fallbackGlyph = this.createTexturedGlyph(DefaultGlyph.INSTANCE);
        this.whiteGlyph = this.createTexturedGlyph(WhiteGlyph.INSTANCE);
        IntOpenHashSet intset = new IntOpenHashSet();
        for (IGlyphProvider iglyphprovider : glyphProvidersIn) {
            intset.addAll(iglyphprovider.func_230428_a_());
        }
        HashSet set = Sets.newHashSet();
        intset.forEach(p_lambda$setGlyphProviders$2_3_ -> {
            for (IGlyphProvider iglyphprovider1 : glyphProvidersIn) {
                IGlyph iglyph = p_lambda$setGlyphProviders$2_3_ == 32 ? GLYPH_ADVANCE_SPACE : iglyphprovider1.getGlyphInfo(p_lambda$setGlyphProviders$2_3_);
                if (iglyph == null) continue;
                set.add(iglyphprovider1);
                if (iglyph == DefaultGlyph.INSTANCE) break;
                this.glyphsByWidth.computeIfAbsent(MathHelper.ceil(iglyph.getAdvance(false)), p_lambda$null$1_0_ -> new IntArrayList()).add(p_lambda$setGlyphProviders$2_3_);
                break;
            }
        });
        glyphProvidersIn.stream().filter(set::contains).forEach(this.glyphProviders::add);
    }

    @Override
    public void close() {
        this.func_230154_b_();
        this.deleteTextures();
    }

    private void func_230154_b_() {
        for (IGlyphProvider iglyphprovider : this.glyphProviders) {
            iglyphprovider.close();
        }
        this.glyphProviders.clear();
    }

    private void deleteTextures() {
        for (FontTexture fonttexture : this.textures) {
            fonttexture.close();
        }
        this.textures.clear();
    }

    public IGlyph func_238557_a_(int p_238557_1_) {
        IGlyph iglyph = (IGlyph)this.glyphs.get(p_238557_1_);
        if (iglyph == null) {
            iglyph = p_238557_1_ == 32 ? GLYPH_ADVANCE_SPACE : this.getGlyphInfo(p_238557_1_);
            this.glyphs.put(p_238557_1_, iglyph);
        }
        return iglyph;
    }

    private IGlyphInfo getGlyphInfo(int p_212455_1_) {
        for (IGlyphProvider iglyphprovider : this.glyphProviders) {
            IGlyphInfo iglyphinfo = iglyphprovider.getGlyphInfo(p_212455_1_);
            if (iglyphinfo == null) continue;
            return iglyphinfo;
        }
        return DefaultGlyph.INSTANCE;
    }

    public TexturedGlyph func_238559_b_(int p_238559_1_) {
        TexturedGlyph texturedglyph = (TexturedGlyph)this.mapTexturedGlyphs.get(p_238559_1_);
        if (texturedglyph == null) {
            texturedglyph = p_238559_1_ == 32 ? EMPTY_GLYPH : this.createTexturedGlyph(this.getGlyphInfo(p_238559_1_));
            this.mapTexturedGlyphs.put(p_238559_1_, texturedglyph);
        }
        return texturedglyph;
    }

    private TexturedGlyph createTexturedGlyph(IGlyphInfo glyphInfoIn) {
        for (FontTexture fonttexture : this.textures) {
            TexturedGlyph texturedglyph = fonttexture.createTexturedGlyph(glyphInfoIn);
            if (texturedglyph == null) continue;
            return texturedglyph;
        }
        FontTexture fonttexture1 = new FontTexture(new ResourceLocation(this.id.getNamespace(), this.id.getPath() + "/" + this.textures.size()), glyphInfoIn.isColored());
        this.textures.add(fonttexture1);
        this.textureManager.loadTexture(fonttexture1.getTextureLocation(), fonttexture1);
        TexturedGlyph texturedglyph1 = fonttexture1.createTexturedGlyph(glyphInfoIn);
        return texturedglyph1 == null ? this.fallbackGlyph : texturedglyph1;
    }

    public TexturedGlyph obfuscate(IGlyph glyph) {
        IntList intlist = (IntList)this.glyphsByWidth.get(MathHelper.ceil(glyph.getAdvance(false)));
        return intlist != null && !intlist.isEmpty() ? this.func_238559_b_(intlist.getInt(RANDOM.nextInt(intlist.size()))) : this.fallbackGlyph;
    }

    public TexturedGlyph getWhiteGlyph() {
        return this.whiteGlyph;
    }
}
