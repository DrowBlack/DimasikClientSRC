package net.minecraft.command.arguments;

import net.minecraft.command.CommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

public interface ILocationArgument {
    public Vector3d getPosition(CommandSource var1);

    public Vector2f getRotation(CommandSource var1);

    default public BlockPos getBlockPos(CommandSource source) {
        return new BlockPos(this.getPosition(source));
    }

    public boolean isXRelative();

    public boolean isYRelative();

    public boolean isZRelative();
}
