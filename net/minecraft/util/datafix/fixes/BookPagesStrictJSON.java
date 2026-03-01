package net.minecraft.util.datafix.fixes;

import com.google.gson.JsonParseException;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.datafix.fixes.SignStrictJSON;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.commons.lang3.StringUtils;

public class BookPagesStrictJSON
extends DataFix {
    public BookPagesStrictJSON(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    public Dynamic<?> fixTag(Dynamic<?> p_209633_1_) {
        return p_209633_1_.update("pages", p_212821_1_ -> DataFixUtils.orElse(p_212821_1_.asStreamOpt().map(p_209630_0_ -> p_209630_0_.map(p_209631_0_ -> {
            if (!p_209631_0_.asString().result().isPresent()) {
                return p_209631_0_;
            }
            String s = p_209631_0_.asString("");
            ITextComponent itextcomponent = null;
            if (!"null".equals(s) && !StringUtils.isEmpty(s)) {
                if (s.charAt(0) == '\"' && s.charAt(s.length() - 1) == '\"' || s.charAt(0) == '{' && s.charAt(s.length() - 1) == '}') {
                    try {
                        itextcomponent = JSONUtils.fromJson(SignStrictJSON.GSON, s, ITextComponent.class, true);
                        if (itextcomponent == null) {
                            itextcomponent = StringTextComponent.EMPTY;
                        }
                    }
                    catch (JsonParseException jsonParseException) {
                        // empty catch block
                    }
                    if (itextcomponent == null) {
                        try {
                            itextcomponent = ITextComponent.Serializer.getComponentFromJson(s);
                        }
                        catch (JsonParseException jsonParseException) {
                            // empty catch block
                        }
                    }
                    if (itextcomponent == null) {
                        try {
                            itextcomponent = ITextComponent.Serializer.getComponentFromJsonLenient(s);
                        }
                        catch (JsonParseException jsonParseException) {
                            // empty catch block
                        }
                    }
                    if (itextcomponent == null) {
                        itextcomponent = new StringTextComponent(s);
                    }
                } else {
                    itextcomponent = new StringTextComponent(s);
                }
            } else {
                itextcomponent = StringTextComponent.EMPTY;
            }
            return p_209631_0_.createString(ITextComponent.Serializer.toJson(itextcomponent));
        })).map(p_209633_1_::createList).result(), p_209633_1_.emptyList()));
    }

    @Override
    public TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
        OpticFinder<?> opticfinder = type.findField("tag");
        return this.fixTypeEverywhereTyped("ItemWrittenBookPagesStrictJsonFix", type, p_207415_2_ -> p_207415_2_.updateTyped(opticfinder, p_207417_1_ -> p_207417_1_.update(DSL.remainderFinder(), this::fixTag)));
    }
}
