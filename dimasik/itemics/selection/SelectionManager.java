package dimasik.itemics.selection;

import dimasik.itemics.Itemics;
import dimasik.itemics.api.selection.ISelection;
import dimasik.itemics.api.selection.ISelectionManager;
import dimasik.itemics.api.utils.BetterBlockPos;
import dimasik.itemics.selection.Selection;
import dimasik.itemics.selection.SelectionRenderer;
import java.util.LinkedList;
import java.util.ListIterator;
import net.minecraft.util.Direction;

public class SelectionManager
implements ISelectionManager {
    private final LinkedList<ISelection> selections = new LinkedList();
    private ISelection[] selectionsArr = new ISelection[0];

    public SelectionManager(Itemics itemics) {
        new SelectionRenderer(itemics, this);
    }

    private void resetSelectionsArr() {
        this.selectionsArr = this.selections.toArray(new ISelection[0]);
    }

    @Override
    public synchronized ISelection addSelection(ISelection selection) {
        this.selections.add(selection);
        this.resetSelectionsArr();
        return selection;
    }

    @Override
    public ISelection addSelection(BetterBlockPos pos1, BetterBlockPos pos2) {
        return this.addSelection(new Selection(pos1, pos2));
    }

    @Override
    public synchronized ISelection removeSelection(ISelection selection) {
        this.selections.remove(selection);
        this.resetSelectionsArr();
        return selection;
    }

    @Override
    public synchronized ISelection[] removeAllSelections() {
        ISelection[] selectionsArr = this.getSelections();
        this.selections.clear();
        this.resetSelectionsArr();
        return selectionsArr;
    }

    @Override
    public ISelection[] getSelections() {
        return this.selectionsArr;
    }

    @Override
    public synchronized ISelection getOnlySelection() {
        if (this.selections.size() == 1) {
            return this.selections.peekFirst();
        }
        return null;
    }

    @Override
    public ISelection getLastSelection() {
        return this.selections.peekLast();
    }

    @Override
    public synchronized ISelection expand(ISelection selection, Direction direction, int blocks) {
        ListIterator<ISelection> it = this.selections.listIterator();
        while (it.hasNext()) {
            ISelection current = (ISelection)it.next();
            if (current != selection) continue;
            it.remove();
            it.add(current.expand(direction, blocks));
            this.resetSelectionsArr();
            return (ISelection)it.previous();
        }
        return null;
    }

    @Override
    public synchronized ISelection contract(ISelection selection, Direction direction, int blocks) {
        ListIterator<ISelection> it = this.selections.listIterator();
        while (it.hasNext()) {
            ISelection current = (ISelection)it.next();
            if (current != selection) continue;
            it.remove();
            it.add(current.contract(direction, blocks));
            this.resetSelectionsArr();
            return (ISelection)it.previous();
        }
        return null;
    }

    @Override
    public synchronized ISelection shift(ISelection selection, Direction direction, int blocks) {
        ListIterator<ISelection> it = this.selections.listIterator();
        while (it.hasNext()) {
            ISelection current = (ISelection)it.next();
            if (current != selection) continue;
            it.remove();
            it.add(current.shift(direction, blocks));
            this.resetSelectionsArr();
            return (ISelection)it.previous();
        }
        return null;
    }
}
