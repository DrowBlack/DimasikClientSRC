package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.HorseModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.util.ResourceLocation;

public class UndeadHorseRenderer
extends AbstractHorseRenderer<AbstractHorseEntity, HorseModel<AbstractHorseEntity>> {
    private static final Map<EntityType<?>, ResourceLocation> UNDEAD_HORSE_TEXTURES = Maps.newHashMap(ImmutableMap.of(EntityType.ZOMBIE_HORSE, new ResourceLocation("textures/entity/horse/horse_zombie.png"), EntityType.SKELETON_HORSE, new ResourceLocation("textures/entity/horse/horse_skeleton.png")));

    public UndeadHorseRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new HorseModel(0.0f), 1.0f);
    }

    @Override
    public ResourceLocation getEntityTexture(AbstractHorseEntity entity) {
        return UNDEAD_HORSE_TEXTURES.get(entity.getType());
    }
}
