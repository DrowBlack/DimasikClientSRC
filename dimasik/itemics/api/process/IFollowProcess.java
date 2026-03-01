package dimasik.itemics.api.process;

import dimasik.itemics.api.process.IItemicsProcess;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;

public interface IFollowProcess
extends IItemicsProcess {
    public void follow(Predicate<Entity> var1);

    public List<Entity> following();

    public Predicate<Entity> currentFilter();

    default public void cancel() {
        this.onLostControl();
    }
}
