package net.minecraft.client.gui.fonts.providers;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.annotation.Nullable;
import net.minecraft.client.gui.fonts.IGlyphInfo;
import net.minecraft.client.gui.fonts.providers.IGlyphProvider;
import net.minecraft.client.gui.fonts.providers.IGlyphProviderFactory;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.optifine.util.FontUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextureGlyphProvider
implements IGlyphProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private final NativeImage texture;
    private final Int2ObjectMap<GlyphInfo> glyphInfos;
    private boolean blend = false;
    private float widthSpace = -1.0f;

    private TextureGlyphProvider(NativeImage p_i232266_1_, Int2ObjectMap<GlyphInfo> p_i232266_2_) {
        this.texture = p_i232266_1_;
        this.glyphInfos = p_i232266_2_;
    }

    @Override
    public void close() {
        this.texture.close();
    }

    @Override
    @Nullable
    public IGlyphInfo getGlyphInfo(int character) {
        return (IGlyphInfo)this.glyphInfos.get(character);
    }

    @Override
    public IntSet func_230428_a_() {
        return IntSets.unmodifiable(this.glyphInfos.keySet());
    }

    public boolean isBlend() {
        return this.blend;
    }

    public float getWidthSpace() {
        return this.widthSpace;
    }

    static final class GlyphInfo
    implements IGlyphInfo {
        private final float scale;
        private final NativeImage texture;
        private final int unpackSkipPixels;
        private final int unpackSkipRows;
        private final int width;
        private final int height;
        private final int advanceWidth;
        private final int ascent;
        private float offsetBold = 1.0f;

        private GlyphInfo(float p_i49748_1_, NativeImage p_i49748_2_, int p_i49748_3_, int p_i49748_4_, int p_i49748_5_, int p_i49748_6_, int p_i49748_7_, int p_i49748_8_) {
            this.scale = p_i49748_1_;
            this.texture = p_i49748_2_;
            this.unpackSkipPixels = p_i49748_3_;
            this.unpackSkipRows = p_i49748_4_;
            this.width = p_i49748_5_;
            this.height = p_i49748_6_;
            this.advanceWidth = p_i49748_7_;
            this.ascent = p_i49748_8_;
        }

        @Override
        public float getOversample() {
            return 1.0f / this.scale;
        }

        @Override
        public int getWidth() {
            return this.width;
        }

        @Override
        public int getHeight() {
            return this.height;
        }

        @Override
        public float getAdvance() {
            return this.advanceWidth;
        }

        @Override
        public float getBearingY() {
            return IGlyphInfo.super.getBearingY() + 7.0f - (float)this.ascent;
        }

        @Override
        public void uploadGlyph(int xOffset, int yOffset) {
            this.texture.uploadTextureSub(0, xOffset, yOffset, this.unpackSkipPixels, this.unpackSkipRows, this.width, this.height, false, false);
        }

        @Override
        public boolean isColored() {
            return this.texture.getFormat().getPixelSize() > 1;
        }

        @Override
        public float getBoldOffset() {
            return this.offsetBold;
        }
    }

    public static class Factory
    implements IGlyphProviderFactory {
        private ResourceLocation file;
        private final List<int[]> chars;
        private final int height;
        private final int ascent;

        public Factory(ResourceLocation textureLocationIn, int heightIn, int ascentIn, List<int[]> listCharRowsIn) {
            this.file = new ResourceLocation(textureLocationIn.getNamespace(), "textures/" + textureLocationIn.getPath());
            this.file = FontUtils.getHdFontLocation(this.file);
            this.chars = listCharRowsIn;
            this.height = heightIn;
            this.ascent = ascentIn;
        }

        public static Factory deserialize(JsonObject jsonIn) {
            int i = JSONUtils.getInt(jsonIn, "height", 8);
            int j = JSONUtils.getInt(jsonIn, "ascent");
            if (j > i) {
                throw new JsonParseException("Ascent " + j + " higher than height " + i);
            }
            ArrayList<int[]> list = Lists.newArrayList();
            JsonArray jsonarray = JSONUtils.getJsonArray(jsonIn, "chars");
            for (int k = 0; k < jsonarray.size(); ++k) {
                int l;
                String s = JSONUtils.getString(jsonarray.get(k), "chars[" + k + "]");
                int[] aint = s.codePoints().toArray();
                if (k > 0 && aint.length != (l = ((int[])list.get(0)).length)) {
                    throw new JsonParseException("Elements of chars have to be the same length (found: " + aint.length + ", expected: " + l + "), pad with space or \\u0000");
                }
                list.add(aint);
            }
            if (!list.isEmpty() && ((int[])list.get(0)).length != 0) {
                return new Factory(new ResourceLocation(JSONUtils.getString(jsonIn, "file")), i, j, list);
            }
            throw new JsonParseException("Expected to find data in chars, found none.");
        }

        @Override
        @Nullable
        public IGlyphProvider create(IResourceManager resourceManagerIn) {
            TextureGlyphProvider textureGlyphProvider;
            block14: {
                IResource iresource = resourceManagerIn.getResource(this.file);
                try {
                    NativeImage nativeimage = NativeImage.read(NativeImage.PixelFormat.RGBA, iresource.getInputStream());
                    int i = nativeimage.getWidth();
                    int j = nativeimage.getHeight();
                    int k = i / this.chars.get(0).length;
                    int l = j / this.chars.size();
                    float f = (float)this.height / (float)l;
                    Int2ObjectOpenHashMap<GlyphInfo> int2objectmap = new Int2ObjectOpenHashMap<GlyphInfo>();
                    Properties properties = FontUtils.readFontProperties(this.file);
                    Int2ObjectMap<Float> int2objectmap1 = FontUtils.readCustomCharWidths(properties);
                    Float f1 = (Float)int2objectmap1.get(32);
                    boolean flag = FontUtils.readBoolean(properties, "blend", false);
                    float f2 = FontUtils.readFloat(properties, "offsetBold", -1.0f);
                    if (f2 < 0.0f) {
                        f2 = k > 8 ? 0.5f : 1.0f;
                    }
                    for (int i1 = 0; i1 < this.chars.size(); ++i1) {
                        int j1 = 0;
                        for (int k1 : this.chars.get(i1)) {
                            GlyphInfo textureglyphprovider$glyphinfo;
                            int l1 = j1++;
                            if (k1 == 0 || k1 == 32) continue;
                            float f3 = this.getCharacterWidth(nativeimage, k, l, l1, i1);
                            Float f4 = (Float)int2objectmap1.get(k1);
                            if (f4 != null) {
                                f3 = f4.floatValue() * ((float)k / 8.0f);
                            }
                            if ((textureglyphprovider$glyphinfo = int2objectmap.put(k1, new GlyphInfo(f, nativeimage, l1 * k, i1 * l, k, l, (int)(0.5 + (double)(f3 * f)) + 1, this.ascent))) != null) {
                                LOGGER.warn("Codepoint '{}' declared multiple times in {}", (Object)Integer.toHexString(k1), (Object)this.file);
                            }
                            GlyphInfo textureglyphprovider$glyphinfo1 = (GlyphInfo)int2objectmap.get(k1);
                            textureglyphprovider$glyphinfo1.offsetBold = f2;
                        }
                    }
                    TextureGlyphProvider textureglyphprovider = new TextureGlyphProvider(nativeimage, int2objectmap);
                    textureglyphprovider.blend = flag;
                    if (f1 != null) {
                        textureglyphprovider.widthSpace = f1.floatValue();
                    }
                    textureGlyphProvider = textureglyphprovider;
                    if (iresource == null) break block14;
                }
                catch (Throwable throwable) {
                    try {
                        if (iresource != null) {
                            try {
                                iresource.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    catch (IOException ioexception1) {
                        throw new RuntimeException(ioexception1.getMessage());
                    }
                }
                iresource.close();
            }
            return textureGlyphProvider;
        }

        private int getCharacterWidth(NativeImage nativeImageIn, int charWidthIn, int charHeightInsp, int columnIn, int rowIn) {
            int i;
            for (i = charWidthIn - 1; i >= 0; --i) {
                int j = columnIn * charWidthIn + i;
                for (int k = 0; k < charHeightInsp; ++k) {
                    int l = rowIn * charHeightInsp + k;
                    if ((nativeImageIn.getPixelLuminanceOrAlpha(j, l) & 0xFF) <= 16) continue;
                    return i + 1;
                }
            }
            return i + 1;
        }
    }
}
