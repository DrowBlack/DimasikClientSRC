package net.minecraft.util.text;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITargetedTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.HoverEvent;

public class TextComponentUtils {
    public static IFormattableTextComponent func_240648_a_(IFormattableTextComponent p_240648_0_, Style p_240648_1_) {
        if (p_240648_1_.isEmpty()) {
            return p_240648_0_;
        }
        Style style = p_240648_0_.getStyle();
        if (style.isEmpty()) {
            return p_240648_0_.setStyle(p_240648_1_);
        }
        return style.equals(p_240648_1_) ? p_240648_0_ : p_240648_0_.setStyle(style.mergeStyle(p_240648_1_));
    }

    public static IFormattableTextComponent func_240645_a_(@Nullable CommandSource p_240645_0_, ITextComponent p_240645_1_, @Nullable Entity p_240645_2_, int p_240645_3_) throws CommandSyntaxException {
        if (p_240645_3_ > 100) {
            return p_240645_1_.deepCopy();
        }
        IFormattableTextComponent iformattabletextcomponent = p_240645_1_ instanceof ITargetedTextComponent ? ((ITargetedTextComponent)((Object)p_240645_1_)).func_230535_a_(p_240645_0_, p_240645_2_, p_240645_3_ + 1) : p_240645_1_.copyRaw();
        for (ITextComponent itextcomponent : p_240645_1_.getSiblings()) {
            iformattabletextcomponent.append(TextComponentUtils.func_240645_a_(p_240645_0_, itextcomponent, p_240645_2_, p_240645_3_ + 1));
        }
        return iformattabletextcomponent.mergeStyle(TextComponentUtils.func_240646_a_(p_240645_0_, p_240645_1_.getStyle(), p_240645_2_, p_240645_3_));
    }

    private static Style func_240646_a_(@Nullable CommandSource p_240646_0_, Style p_240646_1_, @Nullable Entity p_240646_2_, int p_240646_3_) throws CommandSyntaxException {
        ITextComponent itextcomponent;
        HoverEvent hoverevent = p_240646_1_.getHoverEvent();
        if (hoverevent != null && (itextcomponent = hoverevent.getParameter(HoverEvent.Action.SHOW_TEXT)) != null) {
            HoverEvent hoverevent1 = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponentUtils.func_240645_a_(p_240646_0_, itextcomponent, p_240646_2_, p_240646_3_ + 1));
            return p_240646_1_.setHoverEvent(hoverevent1);
        }
        return p_240646_1_;
    }

    public static ITextComponent getDisplayName(GameProfile profile) {
        if (profile.getName() != null) {
            return new StringTextComponent(profile.getName());
        }
        return profile.getId() != null ? new StringTextComponent(profile.getId().toString()) : new StringTextComponent("(unknown)");
    }

    public static ITextComponent makeGreenSortedList(Collection<String> collection) {
        return TextComponentUtils.makeSortedList(collection, p_197681_0_ -> new StringTextComponent((String)p_197681_0_).mergeStyle(TextFormatting.GREEN));
    }

    public static <T extends Comparable<T>> ITextComponent makeSortedList(Collection<T> collection, Function<T, ITextComponent> toTextComponent) {
        if (collection.isEmpty()) {
            return StringTextComponent.EMPTY;
        }
        if (collection.size() == 1) {
            return toTextComponent.apply((Comparable)collection.iterator().next());
        }
        ArrayList<T> list = Lists.newArrayList(collection);
        list.sort(Comparable::compareTo);
        return TextComponentUtils.func_240649_b_(list, toTextComponent);
    }

    public static <T> IFormattableTextComponent func_240649_b_(Collection<T> p_240649_0_, Function<T, ITextComponent> p_240649_1_) {
        if (p_240649_0_.isEmpty()) {
            return new StringTextComponent("");
        }
        if (p_240649_0_.size() == 1) {
            return p_240649_1_.apply(p_240649_0_.iterator().next()).deepCopy();
        }
        StringTextComponent iformattabletextcomponent = new StringTextComponent("");
        boolean flag = true;
        for (T t : p_240649_0_) {
            if (!flag) {
                iformattabletextcomponent.append(new StringTextComponent(", ").mergeStyle(TextFormatting.GRAY));
            }
            iformattabletextcomponent.append(p_240649_1_.apply(t));
            flag = false;
        }
        return iformattabletextcomponent;
    }

    public static IFormattableTextComponent wrapWithSquareBrackets(ITextComponent toWrap) {
        return new TranslationTextComponent("chat.square_brackets", toWrap);
    }

    public static ITextComponent toTextComponent(Message message) {
        return message instanceof ITextComponent ? (ITextComponent)message : new StringTextComponent(message.getString());
    }
}
