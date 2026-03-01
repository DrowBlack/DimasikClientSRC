package net.minecraft.network.datasync;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;

public interface IDataSerializer<T> {
    public void write(PacketBuffer var1, T var2);

    public T read(PacketBuffer var1);

    default public DataParameter<T> createKey(int id) {
        return new DataParameter(id, this);
    }

    public T copyValue(T var1);
}
