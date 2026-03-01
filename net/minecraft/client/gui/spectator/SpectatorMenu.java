package net.minecraft.client.gui.spectator;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.SpectatorGui;
import net.minecraft.client.gui.spectator.BaseSpectatorGroup;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuRecipient;
import net.minecraft.client.gui.spectator.ISpectatorMenuView;
import net.minecraft.client.gui.spectator.categories.SpectatorDetails;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class SpectatorMenu {
    private static final ISpectatorMenuObject CLOSE_ITEM = new EndSpectatorObject();
    private static final ISpectatorMenuObject SCROLL_LEFT = new MoveMenuObject(-1, true);
    private static final ISpectatorMenuObject SCROLL_RIGHT_ENABLED = new MoveMenuObject(1, true);
    private static final ISpectatorMenuObject SCROLL_RIGHT_DISABLED = new MoveMenuObject(1, false);
    private static final ITextComponent field_243477_f = new TranslationTextComponent("spectatorMenu.close");
    private static final ITextComponent field_243478_g = new TranslationTextComponent("spectatorMenu.previous_page");
    private static final ITextComponent field_243479_h = new TranslationTextComponent("spectatorMenu.next_page");
    public static final ISpectatorMenuObject EMPTY_SLOT = new ISpectatorMenuObject(){

        @Override
        public void selectItem(SpectatorMenu menu) {
        }

        @Override
        public ITextComponent getSpectatorName() {
            return StringTextComponent.EMPTY;
        }

        @Override
        public void func_230485_a_(MatrixStack p_230485_1_, float p_230485_2_, int p_230485_3_) {
        }

        @Override
        public boolean isEnabled() {
            return false;
        }
    };
    private final ISpectatorMenuRecipient listener;
    private ISpectatorMenuView category = new BaseSpectatorGroup();
    private int selectedSlot = -1;
    private int page;

    public SpectatorMenu(ISpectatorMenuRecipient menu) {
        this.listener = menu;
    }

    public ISpectatorMenuObject getItem(int index) {
        int i = index + this.page * 6;
        if (this.page > 0 && index == 0) {
            return SCROLL_LEFT;
        }
        if (index == 7) {
            return i < this.category.getItems().size() ? SCROLL_RIGHT_ENABLED : SCROLL_RIGHT_DISABLED;
        }
        if (index == 8) {
            return CLOSE_ITEM;
        }
        return i >= 0 && i < this.category.getItems().size() ? MoreObjects.firstNonNull(this.category.getItems().get(i), EMPTY_SLOT) : EMPTY_SLOT;
    }

    public List<ISpectatorMenuObject> getItems() {
        ArrayList<ISpectatorMenuObject> list = Lists.newArrayList();
        for (int i = 0; i <= 8; ++i) {
            list.add(this.getItem(i));
        }
        return list;
    }

    public ISpectatorMenuObject getSelectedItem() {
        return this.getItem(this.selectedSlot);
    }

    public ISpectatorMenuView getSelectedCategory() {
        return this.category;
    }

    public void selectSlot(int slotIn) {
        ISpectatorMenuObject ispectatormenuobject = this.getItem(slotIn);
        if (ispectatormenuobject != EMPTY_SLOT) {
            if (this.selectedSlot == slotIn && ispectatormenuobject.isEnabled()) {
                ispectatormenuobject.selectItem(this);
            } else {
                this.selectedSlot = slotIn;
            }
        }
    }

    public void exit() {
        this.listener.onSpectatorMenuClosed(this);
    }

    public int getSelectedSlot() {
        return this.selectedSlot;
    }

    public void selectCategory(ISpectatorMenuView menuView) {
        this.category = menuView;
        this.selectedSlot = -1;
        this.page = 0;
    }

    public SpectatorDetails getCurrentPage() {
        return new SpectatorDetails(this.category, this.getItems(), this.selectedSlot);
    }

    static class EndSpectatorObject
    implements ISpectatorMenuObject {
        private EndSpectatorObject() {
        }

        @Override
        public void selectItem(SpectatorMenu menu) {
            menu.exit();
        }

        @Override
        public ITextComponent getSpectatorName() {
            return field_243477_f;
        }

        @Override
        public void func_230485_a_(MatrixStack p_230485_1_, float p_230485_2_, int p_230485_3_) {
            Minecraft.getInstance().getTextureManager().bindTexture(SpectatorGui.SPECTATOR_WIDGETS);
            AbstractGui.blit(p_230485_1_, 0, 0, 128.0f, 0.0f, 16, 16, 256, 256);
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

    static class MoveMenuObject
    implements ISpectatorMenuObject {
        private final int direction;
        private final boolean enabled;

        public MoveMenuObject(int p_i45495_1_, boolean p_i45495_2_) {
            this.direction = p_i45495_1_;
            this.enabled = p_i45495_2_;
        }

        @Override
        public void selectItem(SpectatorMenu menu) {
            menu.page += this.direction;
        }

        @Override
        public ITextComponent getSpectatorName() {
            return this.direction < 0 ? field_243478_g : field_243479_h;
        }

        @Override
        public void func_230485_a_(MatrixStack p_230485_1_, float p_230485_2_, int p_230485_3_) {
            Minecraft.getInstance().getTextureManager().bindTexture(SpectatorGui.SPECTATOR_WIDGETS);
            if (this.direction < 0) {
                AbstractGui.blit(p_230485_1_, 0, 0, 144.0f, 0.0f, 16, 16, 256, 256);
            } else {
                AbstractGui.blit(p_230485_1_, 0, 0, 160.0f, 0.0f, 16, 16, 256, 256);
            }
        }

        @Override
        public boolean isEnabled() {
            return this.enabled;
        }
    }
}
