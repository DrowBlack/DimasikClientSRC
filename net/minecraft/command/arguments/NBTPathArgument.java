package net.minecraft.command.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.command.CommandSource;
import net.minecraft.nbt.CollectionNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class NBTPathArgument
implements ArgumentType<NBTPath> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo.bar", "foo[0]", "[0]", "[]", "{foo=bar}");
    public static final SimpleCommandExceptionType PATH_MALFORMED = new SimpleCommandExceptionType(new TranslationTextComponent("arguments.nbtpath.node.invalid"));
    public static final DynamicCommandExceptionType NOTHING_FOUND = new DynamicCommandExceptionType(p_208665_0_ -> new TranslationTextComponent("arguments.nbtpath.nothing_found", p_208665_0_));

    public static NBTPathArgument nbtPath() {
        return new NBTPathArgument();
    }

    public static NBTPath getNBTPath(CommandContext<CommandSource> context, String name) {
        return context.getArgument(name, NBTPath.class);
    }

    @Override
    public NBTPath parse(StringReader p_parse_1_) throws CommandSyntaxException {
        ArrayList<INode> list = Lists.newArrayList();
        int i = p_parse_1_.getCursor();
        Object2IntOpenHashMap<INode> object2intmap = new Object2IntOpenHashMap<INode>();
        boolean flag = true;
        while (p_parse_1_.canRead() && p_parse_1_.peek() != ' ') {
            char c0;
            INode nbtpathargument$inode = NBTPathArgument.func_218079_a(p_parse_1_, flag);
            list.add(nbtpathargument$inode);
            object2intmap.put(nbtpathargument$inode, p_parse_1_.getCursor() - i);
            flag = false;
            if (!p_parse_1_.canRead() || (c0 = p_parse_1_.peek()) == ' ' || c0 == '[' || c0 == '{') continue;
            p_parse_1_.expect('.');
        }
        return new NBTPath(p_parse_1_.getString().substring(i, p_parse_1_.getCursor()), list.toArray(new INode[0]), object2intmap);
    }

    private static INode func_218079_a(StringReader p_218079_0_, boolean p_218079_1_) throws CommandSyntaxException {
        switch (p_218079_0_.peek()) {
            case '\"': {
                String s = p_218079_0_.readString();
                return NBTPathArgument.func_218083_a(p_218079_0_, s);
            }
            case '[': {
                p_218079_0_.skip();
                char j = p_218079_0_.peek();
                if (j == '{') {
                    CompoundNBT compoundnbt1 = new JsonToNBT(p_218079_0_).readStruct();
                    p_218079_0_.expect(']');
                    return new ListNode(compoundnbt1);
                }
                if (j == ']') {
                    p_218079_0_.skip();
                    return EmptyListNode.field_218067_a;
                }
                int i = p_218079_0_.readInt();
                p_218079_0_.expect(']');
                return new CollectionNode(i);
            }
            case '{': {
                if (!p_218079_1_) {
                    throw PATH_MALFORMED.createWithContext(p_218079_0_);
                }
                CompoundNBT compoundnbt = new JsonToNBT(p_218079_0_).readStruct();
                return new CompoundNode(compoundnbt);
            }
        }
        String s1 = NBTPathArgument.readTagName(p_218079_0_);
        return NBTPathArgument.func_218083_a(p_218079_0_, s1);
    }

    private static INode func_218083_a(StringReader p_218083_0_, String p_218083_1_) throws CommandSyntaxException {
        if (p_218083_0_.canRead() && p_218083_0_.peek() == '{') {
            CompoundNBT compoundnbt = new JsonToNBT(p_218083_0_).readStruct();
            return new JsonNode(p_218083_1_, compoundnbt);
        }
        return new StringNode(p_218083_1_);
    }

    private static String readTagName(StringReader p_197151_0_) throws CommandSyntaxException {
        int i = p_197151_0_.getCursor();
        while (p_197151_0_.canRead() && NBTPathArgument.isSimpleNameChar(p_197151_0_.peek())) {
            p_197151_0_.skip();
        }
        if (p_197151_0_.getCursor() == i) {
            throw PATH_MALFORMED.createWithContext(p_197151_0_);
        }
        return p_197151_0_.getString().substring(i, p_197151_0_.getCursor());
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    private static boolean isSimpleNameChar(char ch) {
        return ch != ' ' && ch != '\"' && ch != '[' && ch != ']' && ch != '.' && ch != '{' && ch != '}';
    }

    private static Predicate<INBT> equalToCompoundPredicate(CompoundNBT p_218080_0_) {
        return p_218081_1_ -> NBTUtil.areNBTEquals(p_218080_0_, p_218081_1_, true);
    }

    public static class NBTPath {
        private final String rawText;
        private final Object2IntMap<INode> field_218078_b;
        private final INode[] nodes;

        public NBTPath(String p_i51148_1_, INode[] p_i51148_2_, Object2IntMap<INode> p_i51148_3_) {
            this.rawText = p_i51148_1_;
            this.nodes = p_i51148_2_;
            this.field_218078_b = p_i51148_3_;
        }

        public List<INBT> func_218071_a(INBT p_218071_1_) throws CommandSyntaxException {
            List<INBT> list = Collections.singletonList(p_218071_1_);
            for (INode nbtpathargument$inode : this.nodes) {
                if (!(list = nbtpathargument$inode.func_218056_a(list)).isEmpty()) continue;
                throw this.createNothingFoundException(nbtpathargument$inode);
            }
            return list;
        }

        public int func_218069_b(INBT p_218069_1_) {
            List<INBT> list = Collections.singletonList(p_218069_1_);
            for (INode nbtpathargument$inode : this.nodes) {
                if (!(list = nbtpathargument$inode.func_218056_a(list)).isEmpty()) continue;
                return 0;
            }
            return list.size();
        }

        private List<INBT> func_218072_d(INBT p_218072_1_) throws CommandSyntaxException {
            List<INBT> list = Collections.singletonList(p_218072_1_);
            for (int i = 0; i < this.nodes.length - 1; ++i) {
                INode nbtpathargument$inode = this.nodes[i];
                int j = i + 1;
                if (!(list = nbtpathargument$inode.func_218052_a(list, this.nodes[j]::createEmptyElement)).isEmpty()) continue;
                throw this.createNothingFoundException(nbtpathargument$inode);
            }
            return list;
        }

        public List<INBT> func_218073_a(INBT p_218073_1_, Supplier<INBT> p_218073_2_) throws CommandSyntaxException {
            List<INBT> list = this.func_218072_d(p_218073_1_);
            INode nbtpathargument$inode = this.nodes[this.nodes.length - 1];
            return nbtpathargument$inode.func_218052_a(list, p_218073_2_);
        }

        private static int reduceToInt(List<INBT> p_218075_0_, Function<INBT, Integer> p_218075_1_) {
            return p_218075_0_.stream().map(p_218075_1_).reduce(0, (p_218074_0_, p_218074_1_) -> p_218074_0_ + p_218074_1_);
        }

        public int func_218076_b(INBT p_218076_1_, Supplier<INBT> p_218076_2_) throws CommandSyntaxException {
            List<INBT> list = this.func_218072_d(p_218076_1_);
            INode nbtpathargument$inode = this.nodes[this.nodes.length - 1];
            return NBTPath.reduceToInt(list, p_218077_2_ -> nbtpathargument$inode.func_218051_a((INBT)p_218077_2_, p_218076_2_));
        }

        public int func_218068_c(INBT p_218068_1_) {
            List<INBT> list = Collections.singletonList(p_218068_1_);
            for (int i = 0; i < this.nodes.length - 1; ++i) {
                list = this.nodes[i].func_218056_a(list);
            }
            INode nbtpathargument$inode = this.nodes[this.nodes.length - 1];
            return NBTPath.reduceToInt(list, nbtpathargument$inode::func_218053_a);
        }

        private CommandSyntaxException createNothingFoundException(INode p_218070_1_) {
            int i = this.field_218078_b.getInt(p_218070_1_);
            return NOTHING_FOUND.create(this.rawText.substring(0, i));
        }

        public String toString() {
            return this.rawText;
        }
    }

    static interface INode {
        public void addMatchingElements(INBT var1, List<INBT> var2);

        public void func_218054_a(INBT var1, Supplier<INBT> var2, List<INBT> var3);

        public INBT createEmptyElement();

        public int func_218051_a(INBT var1, Supplier<INBT> var2);

        public int func_218053_a(INBT var1);

        default public List<INBT> func_218056_a(List<INBT> p_218056_1_) {
            return this.func_218057_a(p_218056_1_, this::addMatchingElements);
        }

        default public List<INBT> func_218052_a(List<INBT> p_218052_1_, Supplier<INBT> p_218052_2_) {
            return this.func_218057_a(p_218052_1_, (p_218055_2_, p_218055_3_) -> this.func_218054_a((INBT)p_218055_2_, p_218052_2_, (List<INBT>)p_218055_3_));
        }

        default public List<INBT> func_218057_a(List<INBT> p_218057_1_, BiConsumer<INBT, List<INBT>> p_218057_2_) {
            ArrayList<INBT> list = Lists.newArrayList();
            for (INBT inbt : p_218057_1_) {
                p_218057_2_.accept(inbt, list);
            }
            return list;
        }
    }

    static class ListNode
    implements INode {
        private final CompoundNBT compound;
        private final Predicate<INBT> equalToPredicate;

        public ListNode(CompoundNBT p_i51151_1_) {
            this.compound = p_i51151_1_;
            this.equalToPredicate = NBTPathArgument.equalToCompoundPredicate(p_i51151_1_);
        }

        @Override
        public void addMatchingElements(INBT p_218050_1_, List<INBT> p_218050_2_) {
            if (p_218050_1_ instanceof ListNBT) {
                ListNBT listnbt = (ListNBT)p_218050_1_;
                listnbt.stream().filter(this.equalToPredicate).forEach(p_218050_2_::add);
            }
        }

        @Override
        public void func_218054_a(INBT p_218054_1_, Supplier<INBT> p_218054_2_, List<INBT> p_218054_3_) {
            MutableBoolean mutableboolean = new MutableBoolean();
            if (p_218054_1_ instanceof ListNBT) {
                ListNBT listnbt = (ListNBT)p_218054_1_;
                listnbt.stream().filter(this.equalToPredicate).forEach(p_218060_2_ -> {
                    p_218054_3_.add((INBT)p_218060_2_);
                    mutableboolean.setTrue();
                });
                if (mutableboolean.isFalse()) {
                    CompoundNBT compoundnbt = this.compound.copy();
                    listnbt.add(compoundnbt);
                    p_218054_3_.add(compoundnbt);
                }
            }
        }

        @Override
        public INBT createEmptyElement() {
            return new ListNBT();
        }

        @Override
        public int func_218051_a(INBT p_218051_1_, Supplier<INBT> p_218051_2_) {
            int i = 0;
            if (p_218051_1_ instanceof ListNBT) {
                ListNBT listnbt = (ListNBT)p_218051_1_;
                int j = listnbt.size();
                if (j == 0) {
                    listnbt.add(p_218051_2_.get());
                    ++i;
                } else {
                    for (int k = 0; k < j; ++k) {
                        INBT inbt1;
                        INBT inbt = listnbt.get(k);
                        if (!this.equalToPredicate.test(inbt) || (inbt1 = p_218051_2_.get()).equals(inbt) || !listnbt.setNBTByIndex(k, inbt1)) continue;
                        ++i;
                    }
                }
            }
            return i;
        }

        @Override
        public int func_218053_a(INBT p_218053_1_) {
            int i = 0;
            if (p_218053_1_ instanceof ListNBT) {
                ListNBT listnbt = (ListNBT)p_218053_1_;
                for (int j = listnbt.size() - 1; j >= 0; --j) {
                    if (!this.equalToPredicate.test(listnbt.get(j))) continue;
                    listnbt.remove(j);
                    ++i;
                }
            }
            return i;
        }
    }

    static class EmptyListNode
    implements INode {
        public static final EmptyListNode field_218067_a = new EmptyListNode();

        private EmptyListNode() {
        }

        @Override
        public void addMatchingElements(INBT p_218050_1_, List<INBT> p_218050_2_) {
            if (p_218050_1_ instanceof CollectionNBT) {
                p_218050_2_.addAll((CollectionNBT)p_218050_1_);
            }
        }

        @Override
        public void func_218054_a(INBT p_218054_1_, Supplier<INBT> p_218054_2_, List<INBT> p_218054_3_) {
            if (p_218054_1_ instanceof CollectionNBT) {
                CollectionNBT collectionnbt = (CollectionNBT)p_218054_1_;
                if (collectionnbt.isEmpty()) {
                    INBT inbt = p_218054_2_.get();
                    if (collectionnbt.addNBTByIndex(0, inbt)) {
                        p_218054_3_.add(inbt);
                    }
                } else {
                    p_218054_3_.addAll(collectionnbt);
                }
            }
        }

        @Override
        public INBT createEmptyElement() {
            return new ListNBT();
        }

        @Override
        public int func_218051_a(INBT p_218051_1_, Supplier<INBT> p_218051_2_) {
            if (!(p_218051_1_ instanceof CollectionNBT)) {
                return 0;
            }
            CollectionNBT collectionnbt = (CollectionNBT)p_218051_1_;
            int i = collectionnbt.size();
            if (i == 0) {
                collectionnbt.addNBTByIndex(0, p_218051_2_.get());
                return 1;
            }
            INBT inbt = p_218051_2_.get();
            int j = i - (int)collectionnbt.stream().filter(inbt::equals).count();
            if (j == 0) {
                return 0;
            }
            collectionnbt.clear();
            if (!collectionnbt.addNBTByIndex(0, inbt)) {
                return 0;
            }
            for (int k = 1; k < i; ++k) {
                collectionnbt.addNBTByIndex(k, p_218051_2_.get());
            }
            return j;
        }

        @Override
        public int func_218053_a(INBT p_218053_1_) {
            CollectionNBT collectionnbt;
            int i;
            if (p_218053_1_ instanceof CollectionNBT && (i = (collectionnbt = (CollectionNBT)p_218053_1_).size()) > 0) {
                collectionnbt.clear();
                return i;
            }
            return 0;
        }
    }

    static class CollectionNode
    implements INode {
        private final int field_218059_a;

        public CollectionNode(int p_i51153_1_) {
            this.field_218059_a = p_i51153_1_;
        }

        @Override
        public void addMatchingElements(INBT p_218050_1_, List<INBT> p_218050_2_) {
            if (p_218050_1_ instanceof CollectionNBT) {
                int j;
                CollectionNBT collectionnbt = (CollectionNBT)p_218050_1_;
                int i = collectionnbt.size();
                int n = j = this.field_218059_a < 0 ? i + this.field_218059_a : this.field_218059_a;
                if (0 <= j && j < i) {
                    p_218050_2_.add((INBT)collectionnbt.get(j));
                }
            }
        }

        @Override
        public void func_218054_a(INBT p_218054_1_, Supplier<INBT> p_218054_2_, List<INBT> p_218054_3_) {
            this.addMatchingElements(p_218054_1_, p_218054_3_);
        }

        @Override
        public INBT createEmptyElement() {
            return new ListNBT();
        }

        @Override
        public int func_218051_a(INBT p_218051_1_, Supplier<INBT> p_218051_2_) {
            if (p_218051_1_ instanceof CollectionNBT) {
                int j;
                CollectionNBT collectionnbt = (CollectionNBT)p_218051_1_;
                int i = collectionnbt.size();
                int n = j = this.field_218059_a < 0 ? i + this.field_218059_a : this.field_218059_a;
                if (0 <= j && j < i) {
                    INBT inbt = (INBT)collectionnbt.get(j);
                    INBT inbt1 = p_218051_2_.get();
                    if (!inbt1.equals(inbt) && collectionnbt.setNBTByIndex(j, inbt1)) {
                        return 1;
                    }
                }
            }
            return 0;
        }

        @Override
        public int func_218053_a(INBT p_218053_1_) {
            if (p_218053_1_ instanceof CollectionNBT) {
                int j;
                CollectionNBT collectionnbt = (CollectionNBT)p_218053_1_;
                int i = collectionnbt.size();
                int n = j = this.field_218059_a < 0 ? i + this.field_218059_a : this.field_218059_a;
                if (0 <= j && j < i) {
                    collectionnbt.remove(j);
                    return 1;
                }
            }
            return 0;
        }
    }

    static class CompoundNode
    implements INode {
        private final Predicate<INBT> field_218066_a;

        public CompoundNode(CompoundNBT p_i51149_1_) {
            this.field_218066_a = NBTPathArgument.equalToCompoundPredicate(p_i51149_1_);
        }

        @Override
        public void addMatchingElements(INBT p_218050_1_, List<INBT> p_218050_2_) {
            if (p_218050_1_ instanceof CompoundNBT && this.field_218066_a.test(p_218050_1_)) {
                p_218050_2_.add(p_218050_1_);
            }
        }

        @Override
        public void func_218054_a(INBT p_218054_1_, Supplier<INBT> p_218054_2_, List<INBT> p_218054_3_) {
            this.addMatchingElements(p_218054_1_, p_218054_3_);
        }

        @Override
        public INBT createEmptyElement() {
            return new CompoundNBT();
        }

        @Override
        public int func_218051_a(INBT p_218051_1_, Supplier<INBT> p_218051_2_) {
            return 0;
        }

        @Override
        public int func_218053_a(INBT p_218053_1_) {
            return 0;
        }
    }

    static class JsonNode
    implements INode {
        private final String field_218063_a;
        private final CompoundNBT field_218064_b;
        private final Predicate<INBT> field_218065_c;

        public JsonNode(String p_i51150_1_, CompoundNBT p_i51150_2_) {
            this.field_218063_a = p_i51150_1_;
            this.field_218064_b = p_i51150_2_;
            this.field_218065_c = NBTPathArgument.equalToCompoundPredicate(p_i51150_2_);
        }

        @Override
        public void addMatchingElements(INBT p_218050_1_, List<INBT> p_218050_2_) {
            INBT inbt;
            if (p_218050_1_ instanceof CompoundNBT && this.field_218065_c.test(inbt = ((CompoundNBT)p_218050_1_).get(this.field_218063_a))) {
                p_218050_2_.add(inbt);
            }
        }

        @Override
        public void func_218054_a(INBT p_218054_1_, Supplier<INBT> p_218054_2_, List<INBT> p_218054_3_) {
            if (p_218054_1_ instanceof CompoundNBT) {
                CompoundNBT compoundnbt = (CompoundNBT)p_218054_1_;
                INBT inbt = compoundnbt.get(this.field_218063_a);
                if (inbt == null) {
                    CompoundNBT compoundnbt1 = this.field_218064_b.copy();
                    compoundnbt.put(this.field_218063_a, compoundnbt1);
                    p_218054_3_.add(compoundnbt1);
                } else if (this.field_218065_c.test(inbt)) {
                    p_218054_3_.add(inbt);
                }
            }
        }

        @Override
        public INBT createEmptyElement() {
            return new CompoundNBT();
        }

        @Override
        public int func_218051_a(INBT p_218051_1_, Supplier<INBT> p_218051_2_) {
            INBT inbt1;
            CompoundNBT compoundnbt;
            INBT inbt;
            if (p_218051_1_ instanceof CompoundNBT && this.field_218065_c.test(inbt = (compoundnbt = (CompoundNBT)p_218051_1_).get(this.field_218063_a)) && !(inbt1 = p_218051_2_.get()).equals(inbt)) {
                compoundnbt.put(this.field_218063_a, inbt1);
                return 1;
            }
            return 0;
        }

        @Override
        public int func_218053_a(INBT p_218053_1_) {
            CompoundNBT compoundnbt;
            INBT inbt;
            if (p_218053_1_ instanceof CompoundNBT && this.field_218065_c.test(inbt = (compoundnbt = (CompoundNBT)p_218053_1_).get(this.field_218063_a))) {
                compoundnbt.remove(this.field_218063_a);
                return 1;
            }
            return 0;
        }
    }

    static class StringNode
    implements INode {
        private final String field_218058_a;

        public StringNode(String p_i51154_1_) {
            this.field_218058_a = p_i51154_1_;
        }

        @Override
        public void addMatchingElements(INBT p_218050_1_, List<INBT> p_218050_2_) {
            INBT inbt;
            if (p_218050_1_ instanceof CompoundNBT && (inbt = ((CompoundNBT)p_218050_1_).get(this.field_218058_a)) != null) {
                p_218050_2_.add(inbt);
            }
        }

        @Override
        public void func_218054_a(INBT p_218054_1_, Supplier<INBT> p_218054_2_, List<INBT> p_218054_3_) {
            if (p_218054_1_ instanceof CompoundNBT) {
                INBT inbt;
                CompoundNBT compoundnbt = (CompoundNBT)p_218054_1_;
                if (compoundnbt.contains(this.field_218058_a)) {
                    inbt = compoundnbt.get(this.field_218058_a);
                } else {
                    inbt = p_218054_2_.get();
                    compoundnbt.put(this.field_218058_a, inbt);
                }
                p_218054_3_.add(inbt);
            }
        }

        @Override
        public INBT createEmptyElement() {
            return new CompoundNBT();
        }

        @Override
        public int func_218051_a(INBT p_218051_1_, Supplier<INBT> p_218051_2_) {
            if (p_218051_1_ instanceof CompoundNBT) {
                INBT inbt1;
                CompoundNBT compoundnbt = (CompoundNBT)p_218051_1_;
                INBT inbt = p_218051_2_.get();
                if (!inbt.equals(inbt1 = compoundnbt.put(this.field_218058_a, inbt))) {
                    return 1;
                }
            }
            return 0;
        }

        @Override
        public int func_218053_a(INBT p_218053_1_) {
            CompoundNBT compoundnbt;
            if (p_218053_1_ instanceof CompoundNBT && (compoundnbt = (CompoundNBT)p_218053_1_).contains(this.field_218058_a)) {
                compoundnbt.remove(this.field_218058_a);
                return 1;
            }
            return 0;
        }
    }
}
