package net.minecraft.advancements.criterion;

import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public abstract class EntityTypePredicate {
    public static final EntityTypePredicate ANY = new EntityTypePredicate(){

        @Override
        public boolean test(EntityType<?> type) {
            return true;
        }

        @Override
        public JsonElement serialize() {
            return JsonNull.INSTANCE;
        }
    };
    private static final Joiner JOINER = Joiner.on(", ");

    public abstract boolean test(EntityType<?> var1);

    public abstract JsonElement serialize();

    public static EntityTypePredicate deserialize(@Nullable JsonElement element) {
        if (element != null && !element.isJsonNull()) {
            String s = JSONUtils.getString(element, "type");
            if (s.startsWith("#")) {
                ResourceLocation resourcelocation1 = new ResourceLocation(s.substring(1));
                return new TagPredicate(TagCollectionManager.getManager().getEntityTypeTags().getTagByID(resourcelocation1));
            }
            ResourceLocation resourcelocation = new ResourceLocation(s);
            EntityType<?> entitytype = Registry.ENTITY_TYPE.getOptional(resourcelocation).orElseThrow(() -> new JsonSyntaxException("Unknown entity type '" + String.valueOf(resourcelocation) + "', valid types are: " + JOINER.join(Registry.ENTITY_TYPE.keySet())));
            return new TypePredicate(entitytype);
        }
        return ANY;
    }

    public static EntityTypePredicate fromType(EntityType<?> type) {
        return new TypePredicate(type);
    }

    public static EntityTypePredicate fromTag(ITag<EntityType<?>> tag) {
        return new TagPredicate(tag);
    }

    static class TagPredicate
    extends EntityTypePredicate {
        private final ITag<EntityType<?>> tag;

        public TagPredicate(ITag<EntityType<?>> tag) {
            this.tag = tag;
        }

        @Override
        public boolean test(EntityType<?> type) {
            return this.tag.contains(type);
        }

        @Override
        public JsonElement serialize() {
            return new JsonPrimitive("#" + String.valueOf(TagCollectionManager.getManager().getEntityTypeTags().getValidatedIdFromTag(this.tag)));
        }
    }

    static class TypePredicate
    extends EntityTypePredicate {
        private final EntityType<?> type;

        public TypePredicate(EntityType<?> type) {
            this.type = type;
        }

        @Override
        public boolean test(EntityType<?> type) {
            return this.type == type;
        }

        @Override
        public JsonElement serialize() {
            return new JsonPrimitive(Registry.ENTITY_TYPE.getKey(this.type).toString());
        }
    }
}
