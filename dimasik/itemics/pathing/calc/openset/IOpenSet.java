package dimasik.itemics.pathing.calc.openset;

import dimasik.itemics.pathing.calc.PathNode;

public interface IOpenSet {
    public void insert(PathNode var1);

    public boolean isEmpty();

    public PathNode removeLowest();

    public void update(PathNode var1);
}
