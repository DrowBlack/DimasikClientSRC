package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.Biome;

public class CreateBuffetWorldScreen
extends Screen {
    private static final ITextComponent field_243277_a = new TranslationTextComponent("createWorld.customize.buffet.biome");
    private final Screen parent;
    private final Consumer<Biome> field_238592_b_;
    private final MutableRegistry<Biome> field_243278_p;
    private BiomeList biomeList;
    private Biome field_238593_p_;
    private Button field_205313_u;

    public CreateBuffetWorldScreen(Screen p_i242054_1_, DynamicRegistries p_i242054_2_, Consumer<Biome> p_i242054_3_, Biome p_i242054_4_) {
        super(new TranslationTextComponent("createWorld.customize.buffet.title"));
        this.parent = p_i242054_1_;
        this.field_238592_b_ = p_i242054_3_;
        this.field_238593_p_ = p_i242054_4_;
        this.field_243278_p = p_i242054_2_.getRegistry(Registry.BIOME_KEY);
    }

    @Override
    public void closeScreen() {
        this.minecraft.displayGuiScreen(this.parent);
    }

    @Override
    protected void init() {
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        this.biomeList = new BiomeList();
        this.children.add(this.biomeList);
        this.field_205313_u = this.addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, DialogTexts.GUI_DONE, p_241579_1_ -> {
            this.field_238592_b_.accept(this.field_238593_p_);
            this.minecraft.displayGuiScreen(this.parent);
        }));
        this.addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, DialogTexts.GUI_CANCEL, p_213015_1_ -> this.minecraft.displayGuiScreen(this.parent)));
        this.biomeList.setSelected(this.biomeList.getEventListeners().stream().filter(p_241578_1_ -> Objects.equals(p_241578_1_.field_238599_b_, this.field_238593_p_)).findFirst().orElse(null));
    }

    private void func_205306_h() {
        this.field_205313_u.active = this.biomeList.getSelected() != null;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderDirtBackground(0);
        this.biomeList.render(matrixStack, mouseX, mouseY, partialTicks);
        CreateBuffetWorldScreen.drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 8, 0xFFFFFF);
        CreateBuffetWorldScreen.drawCenteredString(matrixStack, this.font, field_243277_a, this.width / 2, 28, 0xA0A0A0);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    class BiomeList
    extends ExtendedList<BiomeEntry> {
        private BiomeList() {
            super(CreateBuffetWorldScreen.this.minecraft, CreateBuffetWorldScreen.this.width, CreateBuffetWorldScreen.this.height, 40, CreateBuffetWorldScreen.this.height - 37, 16);
            CreateBuffetWorldScreen.this.field_243278_p.getEntries().stream().sorted(Comparator.comparing(p_238598_0_ -> ((RegistryKey)p_238598_0_.getKey()).getLocation().toString())).forEach(p_238597_1_ -> this.addEntry(new BiomeEntry((Biome)p_238597_1_.getValue())));
        }

        @Override
        protected boolean isFocused() {
            return CreateBuffetWorldScreen.this.getListener() == this;
        }

        @Override
        public void setSelected(@Nullable BiomeEntry entry) {
            super.setSelected(entry);
            if (entry != null) {
                CreateBuffetWorldScreen.this.field_238593_p_ = entry.field_238599_b_;
                NarratorChatListener.INSTANCE.say(new TranslationTextComponent("narrator.select", CreateBuffetWorldScreen.this.field_243278_p.getKey(entry.field_238599_b_)).getString());
            }
            CreateBuffetWorldScreen.this.func_205306_h();
        }

        class BiomeEntry
        extends ExtendedList.AbstractListEntry<BiomeEntry> {
            private final Biome field_238599_b_;
            private final ITextComponent field_243282_c;

            public BiomeEntry(Biome p_i232272_2_) {
                this.field_238599_b_ = p_i232272_2_;
                ResourceLocation resourcelocation = CreateBuffetWorldScreen.this.field_243278_p.getKey(p_i232272_2_);
                String s = "biome." + resourcelocation.getNamespace() + "." + resourcelocation.getPath();
                this.field_243282_c = LanguageMap.getInstance().func_230506_b_(s) ? new TranslationTextComponent(s) : new StringTextComponent(resourcelocation.toString());
            }

            @Override
            public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
                AbstractGui.drawString(p_230432_1_, CreateBuffetWorldScreen.this.font, this.field_243282_c, p_230432_4_ + 5, p_230432_3_ + 2, 0xFFFFFF);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (button == 0) {
                    BiomeList.this.setSelected(this);
                    return true;
                }
                return false;
            }
        }
    }
}
