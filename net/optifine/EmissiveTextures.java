package net.optifine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.render.RenderUtils;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.TextureUtils;

public class EmissiveTextures {
    private static String suffixEmissive = null;
    private static String suffixEmissivePng = null;
    private static boolean active = false;
    private static boolean render = false;
    private static boolean hasEmissive = false;
    private static boolean renderEmissive = false;
    private static final String SUFFIX_PNG = ".png";
    private static final ResourceLocation LOCATION_TEXTURE_EMPTY = TextureUtils.LOCATION_TEXTURE_EMPTY;
    private static final ResourceLocation LOCATION_SPRITE_EMPTY = TextureUtils.LOCATION_SPRITE_EMPTY;
    private static TextureManager textureManager;
    private static int countRecursive;

    public static boolean isActive() {
        return active;
    }

    public static String getSuffixEmissive() {
        return suffixEmissive;
    }

    public static void beginRender() {
        if (render) {
            ++countRecursive;
        } else {
            render = true;
            hasEmissive = false;
        }
    }

    public static ResourceLocation getEmissiveTexture(ResourceLocation locationIn) {
        if (!render) {
            return locationIn;
        }
        Texture texture = textureManager.getTexture(locationIn);
        if (texture instanceof AtlasTexture) {
            return locationIn;
        }
        ResourceLocation resourcelocation = null;
        if (texture instanceof SimpleTexture) {
            resourcelocation = ((SimpleTexture)texture).locationEmissive;
        }
        if (!renderEmissive) {
            if (resourcelocation != null) {
                hasEmissive = true;
            }
            return locationIn;
        }
        if (resourcelocation == null) {
            resourcelocation = LOCATION_TEXTURE_EMPTY;
        }
        return resourcelocation;
    }

    public static TextureAtlasSprite getEmissiveSprite(TextureAtlasSprite sprite) {
        if (!render) {
            return sprite;
        }
        TextureAtlasSprite textureatlassprite = sprite.spriteEmissive;
        if (!renderEmissive) {
            if (textureatlassprite != null) {
                hasEmissive = true;
            }
            return sprite;
        }
        if (textureatlassprite == null) {
            textureatlassprite = sprite.getAtlasTexture().getSprite(LOCATION_SPRITE_EMPTY);
        }
        return textureatlassprite;
    }

    public static BakedQuad getEmissiveQuad(BakedQuad quad) {
        if (!render) {
            return quad;
        }
        BakedQuad bakedquad = quad.getQuadEmissive();
        if (!renderEmissive) {
            if (bakedquad != null) {
                hasEmissive = true;
            }
            return quad;
        }
        return bakedquad;
    }

    public static boolean hasEmissive() {
        return countRecursive > 0 ? false : hasEmissive;
    }

    public static void beginRenderEmissive() {
        renderEmissive = true;
    }

    public static boolean isRenderEmissive() {
        return renderEmissive;
    }

    public static void endRenderEmissive() {
        RenderUtils.flushRenderBuffers();
        renderEmissive = false;
    }

    public static void endRender() {
        if (countRecursive > 0) {
            --countRecursive;
        } else {
            render = false;
            hasEmissive = false;
        }
    }

    public static void update() {
        textureManager = Minecraft.getInstance().getTextureManager();
        active = false;
        suffixEmissive = null;
        suffixEmissivePng = null;
        if (Config.isEmissiveTextures()) {
            try {
                String s = "optifine/emissive.properties";
                ResourceLocation resourcelocation = new ResourceLocation(s);
                InputStream inputstream = Config.getResourceStream(resourcelocation);
                if (inputstream == null) {
                    return;
                }
                EmissiveTextures.dbg("Loading " + s);
                PropertiesOrdered properties = new PropertiesOrdered();
                properties.load(inputstream);
                inputstream.close();
                suffixEmissive = properties.getProperty("suffix.emissive");
                if (suffixEmissive != null) {
                    suffixEmissivePng = suffixEmissive + SUFFIX_PNG;
                }
                active = suffixEmissive != null;
            }
            catch (FileNotFoundException filenotfoundexception) {
                return;
            }
            catch (IOException ioexception) {
                ioexception.printStackTrace();
            }
        }
    }

    public static void updateIcons(AtlasTexture textureMap, Set<ResourceLocation> locations) {
        if (active) {
            for (ResourceLocation resourcelocation : locations) {
                EmissiveTextures.checkEmissive(textureMap, resourcelocation);
            }
        }
    }

    private static void checkEmissive(AtlasTexture textureMap, ResourceLocation locSprite) {
        ResourceLocation resourcelocation;
        ResourceLocation resourcelocation1;
        String s = EmissiveTextures.getSuffixEmissive();
        if (s != null && !locSprite.getPath().endsWith(s) && Config.hasResource(resourcelocation1 = textureMap.getSpritePath(resourcelocation = new ResourceLocation(locSprite.getNamespace(), locSprite.getPath() + s)))) {
            TextureAtlasSprite textureatlassprite = textureMap.registerSprite(locSprite);
            TextureAtlasSprite textureatlassprite1 = textureMap.registerSprite(resourcelocation);
            textureatlassprite1.isSpriteEmissive = true;
            textureatlassprite.spriteEmissive = textureatlassprite1;
            textureMap.registerSprite(LOCATION_SPRITE_EMPTY);
        }
    }

    public static void refreshIcons(AtlasTexture textureMap) {
        for (TextureAtlasSprite textureatlassprite : textureMap.getRegisteredSprites()) {
            EmissiveTextures.refreshIcon(textureatlassprite, textureMap);
        }
    }

    private static void refreshIcon(TextureAtlasSprite sprite, AtlasTexture textureMap) {
        TextureAtlasSprite textureatlassprite1;
        TextureAtlasSprite textureatlassprite;
        if (sprite.spriteEmissive != null && (textureatlassprite = textureMap.getUploadedSprite(sprite.getName())) != null && (textureatlassprite1 = textureMap.getUploadedSprite(sprite.spriteEmissive.getName())) != null) {
            textureatlassprite1.isSpriteEmissive = true;
            textureatlassprite.spriteEmissive = textureatlassprite1;
        }
    }

    private static void dbg(String str) {
        Config.dbg("EmissiveTextures: " + str);
    }

    private static void warn(String str) {
        Config.warn("EmissiveTextures: " + str);
    }

    public static boolean isEmissive(ResourceLocation loc) {
        return suffixEmissivePng == null ? false : loc.getPath().endsWith(suffixEmissivePng);
    }

    public static void loadTexture(ResourceLocation loc, SimpleTexture tex) {
        if (loc != null && tex != null) {
            String s;
            tex.isEmissive = false;
            tex.locationEmissive = null;
            if (suffixEmissivePng != null && (s = loc.getPath()).endsWith(SUFFIX_PNG)) {
                if (s.endsWith(suffixEmissivePng)) {
                    tex.isEmissive = true;
                } else {
                    String s1 = s.substring(0, s.length() - SUFFIX_PNG.length()) + suffixEmissivePng;
                    ResourceLocation resourcelocation = new ResourceLocation(loc.getNamespace(), s1);
                    if (Config.hasResource(resourcelocation)) {
                        tex.locationEmissive = resourcelocation;
                    }
                }
            }
        }
    }

    static {
        countRecursive = 0;
    }
}
