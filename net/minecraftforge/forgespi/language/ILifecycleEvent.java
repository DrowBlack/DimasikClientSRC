package net.minecraftforge.forgespi.language;

public interface ILifecycleEvent<R extends ILifecycleEvent<?>> {
    default public R concrete() {
        return (R)this;
    }
}
