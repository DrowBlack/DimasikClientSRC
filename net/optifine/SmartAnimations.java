package net.optifine;

import java.util.BitSet;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.optifine.Config;
import net.optifine.shaders.Shaders;

public class SmartAnimations {
    private static boolean active;
    private static BitSet spritesRendered;
    private static BitSet texturesRendered;

    public static boolean isActive() {
        return active && !Shaders.isShadowPass;
    }

    public static void update() {
        active = Config.getGameSettings().ofSmartAnimations;
    }

    public static void spriteRendered(TextureAtlasSprite sprite) {
        int i;
        if (sprite.isTerrain() && (i = sprite.getAnimationIndex()) >= 0) {
            spritesRendered.set(i);
        }
    }

    public static void spritesRendered(BitSet animationIndexes) {
        if (animationIndexes != null) {
            spritesRendered.or(animationIndexes);
        }
    }

    public static boolean isSpriteRendered(TextureAtlasSprite sprite) {
        if (!sprite.isTerrain()) {
            return true;
        }
        int i = sprite.getAnimationIndex();
        return i < 0 ? false : spritesRendered.get(i);
    }

    public static void resetSpritesRendered(AtlasTexture atlasTexture) {
        if (atlasTexture.isTerrain()) {
            spritesRendered.clear();
        }
    }

    public static void textureRendered(int textureId) {
        if (textureId >= 0) {
            texturesRendered.set(textureId);
        }
    }

    public static boolean isTextureRendered(int texId) {
        return texId < 0 ? false : texturesRendered.get(texId);
    }

    public static void resetTexturesRendered() {
        texturesRendered.clear();
    }

    static {
        spritesRendered = new BitSet();
        texturesRendered = new BitSet();
    }
}
