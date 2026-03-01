package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.client.gui.screen.FlatPresetsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.FlatLayerInfo;

public class CreateFlatWorldScreen
extends Screen {
    protected final CreateWorldScreen createWorldGui;
    private final Consumer<FlatGenerationSettings> field_238601_b_;
    private FlatGenerationSettings generatorInfo;
    private ITextComponent materialText;
    private ITextComponent heightText;
    private DetailsList createFlatWorldListSlotGui;
    private Button removeLayerButton;

    public CreateFlatWorldScreen(CreateWorldScreen p_i242055_1_, Consumer<FlatGenerationSettings> p_i242055_2_, FlatGenerationSettings p_i242055_3_) {
        super(new TranslationTextComponent("createWorld.customize.flat.title"));
        this.createWorldGui = p_i242055_1_;
        this.field_238601_b_ = p_i242055_2_;
        this.generatorInfo = p_i242055_3_;
    }

    public FlatGenerationSettings func_238603_g_() {
        return this.generatorInfo;
    }

    public void func_238602_a_(FlatGenerationSettings p_238602_1_) {
        this.generatorInfo = p_238602_1_;
    }

    @Override
    protected void init() {
        this.materialText = new TranslationTextComponent("createWorld.customize.flat.tile");
        this.heightText = new TranslationTextComponent("createWorld.customize.flat.height");
        this.createFlatWorldListSlotGui = new DetailsList();
        this.children.add(this.createFlatWorldListSlotGui);
        this.removeLayerButton = this.addButton(new Button(this.width / 2 - 155, this.height - 52, 150, 20, new TranslationTextComponent("createWorld.customize.flat.removeLayer"), p_213007_1_ -> {
            if (this.hasSelectedLayer()) {
                List<FlatLayerInfo> list = this.generatorInfo.getFlatLayers();
                int i = this.createFlatWorldListSlotGui.getEventListeners().indexOf(this.createFlatWorldListSlotGui.getSelected());
                int j = list.size() - i - 1;
                list.remove(j);
                this.createFlatWorldListSlotGui.setSelected(list.isEmpty() ? null : (DetailsList.LayerEntry)this.createFlatWorldListSlotGui.getEventListeners().get(Math.min(i, list.size() - 1)));
                this.generatorInfo.updateLayers();
                this.createFlatWorldListSlotGui.func_214345_a();
                this.onLayersChanged();
            }
        }));
        this.addButton(new Button(this.width / 2 + 5, this.height - 52, 150, 20, new TranslationTextComponent("createWorld.customize.presets"), p_213011_1_ -> {
            this.minecraft.displayGuiScreen(new FlatPresetsScreen(this));
            this.generatorInfo.updateLayers();
            this.onLayersChanged();
        }));
        this.addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, DialogTexts.GUI_DONE, p_213010_1_ -> {
            this.field_238601_b_.accept(this.generatorInfo);
            this.minecraft.displayGuiScreen(this.createWorldGui);
            this.generatorInfo.updateLayers();
        }));
        this.addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, DialogTexts.GUI_CANCEL, p_213009_1_ -> {
            this.minecraft.displayGuiScreen(this.createWorldGui);
            this.generatorInfo.updateLayers();
        }));
        this.generatorInfo.updateLayers();
        this.onLayersChanged();
    }

    private void onLayersChanged() {
        this.removeLayerButton.active = this.hasSelectedLayer();
    }

    private boolean hasSelectedLayer() {
        return this.createFlatWorldListSlotGui.getSelected() != null;
    }

    @Override
    public void closeScreen() {
        this.minecraft.displayGuiScreen(this.createWorldGui);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        this.createFlatWorldListSlotGui.render(matrixStack, mouseX, mouseY, partialTicks);
        CreateFlatWorldScreen.drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 8, 0xFFFFFF);
        int i = this.width / 2 - 92 - 16;
        CreateFlatWorldScreen.drawString(matrixStack, this.font, this.materialText, i, 32, 0xFFFFFF);
        CreateFlatWorldScreen.drawString(matrixStack, this.font, this.heightText, i + 2 + 213 - this.font.getStringPropertyWidth(this.heightText), 32, 0xFFFFFF);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    class DetailsList
    extends ExtendedList<LayerEntry> {
        public DetailsList() {
            super(CreateFlatWorldScreen.this.minecraft, CreateFlatWorldScreen.this.width, CreateFlatWorldScreen.this.height, 43, CreateFlatWorldScreen.this.height - 60, 24);
            for (int i = 0; i < CreateFlatWorldScreen.this.generatorInfo.getFlatLayers().size(); ++i) {
                this.addEntry(new LayerEntry());
            }
        }

        @Override
        public void setSelected(@Nullable LayerEntry entry) {
            FlatLayerInfo flatlayerinfo;
            Item item;
            super.setSelected(entry);
            if (entry != null && (item = (flatlayerinfo = CreateFlatWorldScreen.this.generatorInfo.getFlatLayers().get(CreateFlatWorldScreen.this.generatorInfo.getFlatLayers().size() - this.getEventListeners().indexOf(entry) - 1)).getLayerMaterial().getBlock().asItem()) != Items.AIR) {
                NarratorChatListener.INSTANCE.say(new TranslationTextComponent("narrator.select", item.getDisplayName(new ItemStack(item))).getString());
            }
            CreateFlatWorldScreen.this.onLayersChanged();
        }

        @Override
        protected boolean isFocused() {
            return CreateFlatWorldScreen.this.getListener() == this;
        }

        @Override
        protected int getScrollbarPosition() {
            return this.width - 70;
        }

        public void func_214345_a() {
            int i = this.getEventListeners().indexOf(this.getSelected());
            this.clearEntries();
            for (int j = 0; j < CreateFlatWorldScreen.this.generatorInfo.getFlatLayers().size(); ++j) {
                this.addEntry(new LayerEntry());
            }
            List list = this.getEventListeners();
            if (i >= 0 && i < list.size()) {
                this.setSelected((LayerEntry)list.get(i));
            }
        }

        class LayerEntry
        extends ExtendedList.AbstractListEntry<LayerEntry> {
            private LayerEntry() {
            }

            @Override
            public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
                FlatLayerInfo flatlayerinfo = CreateFlatWorldScreen.this.generatorInfo.getFlatLayers().get(CreateFlatWorldScreen.this.generatorInfo.getFlatLayers().size() - p_230432_2_ - 1);
                BlockState blockstate = flatlayerinfo.getLayerMaterial();
                Item item = blockstate.getBlock().asItem();
                if (item == Items.AIR) {
                    if (blockstate.isIn(Blocks.WATER)) {
                        item = Items.WATER_BUCKET;
                    } else if (blockstate.isIn(Blocks.LAVA)) {
                        item = Items.LAVA_BUCKET;
                    }
                }
                ItemStack itemstack = new ItemStack(item);
                this.func_238605_a_(p_230432_1_, p_230432_4_, p_230432_3_, itemstack);
                CreateFlatWorldScreen.this.font.func_243248_b(p_230432_1_, item.getDisplayName(itemstack), p_230432_4_ + 18 + 5, p_230432_3_ + 3, 0xFFFFFF);
                String s = p_230432_2_ == 0 ? I18n.format("createWorld.customize.flat.layer.top", flatlayerinfo.getLayerCount()) : (p_230432_2_ == CreateFlatWorldScreen.this.generatorInfo.getFlatLayers().size() - 1 ? I18n.format("createWorld.customize.flat.layer.bottom", flatlayerinfo.getLayerCount()) : I18n.format("createWorld.customize.flat.layer", flatlayerinfo.getLayerCount()));
                CreateFlatWorldScreen.this.font.drawString(p_230432_1_, s, p_230432_4_ + 2 + 213 - CreateFlatWorldScreen.this.font.getStringWidth(s), p_230432_3_ + 3, 0xFFFFFF);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (button == 0) {
                    DetailsList.this.setSelected(this);
                    return true;
                }
                return false;
            }

            private void func_238605_a_(MatrixStack p_238605_1_, int p_238605_2_, int p_238605_3_, ItemStack p_238605_4_) {
                this.func_238604_a_(p_238605_1_, p_238605_2_ + 1, p_238605_3_ + 1);
                RenderSystem.enableRescaleNormal();
                if (!p_238605_4_.isEmpty()) {
                    CreateFlatWorldScreen.this.itemRenderer.renderItemIntoGUI(p_238605_4_, p_238605_2_ + 2, p_238605_3_ + 2);
                }
                RenderSystem.disableRescaleNormal();
            }

            private void func_238604_a_(MatrixStack p_238604_1_, int p_238604_2_, int p_238604_3_) {
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                DetailsList.this.minecraft.getTextureManager().bindTexture(AbstractGui.STATS_ICON_LOCATION);
                AbstractGui.blit(p_238604_1_, p_238604_2_, p_238604_3_, CreateFlatWorldScreen.this.getBlitOffset(), 0.0f, 0.0f, 18, 18, 128, 128);
            }
        }
    }
}
