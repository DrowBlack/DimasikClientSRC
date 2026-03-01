package dimasik.managers.mods.voicechat.gui.widgets;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.list.AbstractOptionList;

public abstract class ListScreenEntryBase<T extends AbstractOptionList.Entry<T>>
extends AbstractOptionList.Entry<T> {
    protected final List<IGuiEventListener> children = Lists.newArrayList();

    public List<? extends IGuiEventListener> children() {
        return this.children;
    }
}
