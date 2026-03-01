package net.minecraft.client.gui;

import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.gui.IGuiEventListener;

public interface INestedGuiEventHandler
extends IGuiEventListener {
    public List<? extends IGuiEventListener> getEventListeners();

    default public Optional<IGuiEventListener> getEventListenerForPos(double mouseX, double mouseY) {
        for (IGuiEventListener iGuiEventListener : this.getEventListeners()) {
            if (!iGuiEventListener.isMouseOver(mouseX, mouseY)) continue;
            return Optional.of(iGuiEventListener);
        }
        return Optional.empty();
    }

    @Override
    default public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (IGuiEventListener iGuiEventListener : this.getEventListeners()) {
            if (!iGuiEventListener.mouseClicked(mouseX, mouseY, button)) continue;
            this.setListener(iGuiEventListener);
            if (button == 0) {
                this.setDragging(true);
            }
            return true;
        }
        return false;
    }

    @Override
    default public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.setDragging(false);
        return this.getEventListenerForPos(mouseX, mouseY).filter(listener -> listener.mouseReleased(mouseX, mouseY, button)).isPresent();
    }

    @Override
    default public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return this.getListener() != null && this.isDragging() && button == 0 ? this.getListener().mouseDragged(mouseX, mouseY, button, dragX, dragY) : false;
    }

    public boolean isDragging();

    public void setDragging(boolean var1);

    @Override
    default public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return this.getEventListenerForPos(mouseX, mouseY).filter(listener -> listener.mouseScrolled(mouseX, mouseY, delta)).isPresent();
    }

    @Override
    default public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.getListener() != null && this.getListener().keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    default public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return this.getListener() != null && this.getListener().keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    default public boolean charTyped(char codePoint, int modifiers) {
        return this.getListener() != null && this.getListener().charTyped(codePoint, modifiers);
    }

    @Nullable
    public IGuiEventListener getListener();

    public void setListener(@Nullable IGuiEventListener var1);

    default public void setFocusedDefault(@Nullable IGuiEventListener eventListener) {
        this.setListener(eventListener);
        eventListener.changeFocus(true);
    }

    default public void setListenerDefault(@Nullable IGuiEventListener eventListener) {
        this.setListener(eventListener);
    }

    @Override
    default public boolean changeFocus(boolean focus) {
        Supplier<IGuiEventListener> supplier;
        BooleanSupplier booleansupplier;
        boolean flag;
        IGuiEventListener iguieventlistener = this.getListener();
        boolean bl = flag = iguieventlistener != null;
        if (flag && iguieventlistener.changeFocus(focus)) {
            return true;
        }
        List<? extends IGuiEventListener> list = this.getEventListeners();
        int j = list.indexOf(iguieventlistener);
        int i = flag && j >= 0 ? j + (focus ? 1 : 0) : (focus ? 0 : list.size());
        ListIterator<? extends IGuiEventListener> listiterator = list.listIterator(i);
        BooleanSupplier booleanSupplier = focus ? listiterator::hasNext : (booleansupplier = listiterator::hasPrevious);
        Supplier<IGuiEventListener> supplier2 = focus ? listiterator::next : (supplier = listiterator::previous);
        while (booleansupplier.getAsBoolean()) {
            IGuiEventListener iguieventlistener1 = supplier.get();
            if (!iguieventlistener1.changeFocus(focus)) continue;
            this.setListener(iguieventlistener1);
            return true;
        }
        this.setListener(null);
        return false;
    }
}
