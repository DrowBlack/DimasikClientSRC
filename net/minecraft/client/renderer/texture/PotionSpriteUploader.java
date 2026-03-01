package net.minecraft.client.renderer.texture;

import java.util.stream.Stream;
import net.minecraft.client.renderer.texture.SpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class PotionSpriteUploader
extends SpriteUploader {
    public PotionSpriteUploader(TextureManager textureManagerIn) {
        super(textureManagerIn, new ResourceLocation("textures/atlas/mob_effects.png"), "mob_effect");
    }

    @Override
    protected Stream<ResourceLocation> getResourceLocations() {
        return Registry.EFFECTS.keySet().stream();
    }

    public TextureAtlasSprite getSprite(Effect effectIn) {
        return this.getSprite(Registry.EFFECTS.getKey(effectIn));
    }
}
