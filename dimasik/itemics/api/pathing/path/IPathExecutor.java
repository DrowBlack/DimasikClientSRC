package dimasik.itemics.api.pathing.path;

import dimasik.itemics.api.pathing.calc.IPath;

public interface IPathExecutor {
    public IPath getPath();

    public int getPosition();
}
