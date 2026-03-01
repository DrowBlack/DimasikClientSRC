package net.minecraft.util;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IWorldPosCallable {
    public static final IWorldPosCallable DUMMY = new IWorldPosCallable(){

        @Override
        public <T> Optional<T> apply(BiFunction<World, BlockPos, T> worldPosConsumer) {
            return Optional.empty();
        }
    };

    public static IWorldPosCallable of(final World world, final BlockPos pos) {
        return new IWorldPosCallable(){

            @Override
            public <T> Optional<T> apply(BiFunction<World, BlockPos, T> worldPosConsumer) {
                return Optional.of(worldPosConsumer.apply(world, pos));
            }
        };
    }

    public <T> Optional<T> apply(BiFunction<World, BlockPos, T> var1);

    default public <T> T applyOrElse(BiFunction<World, BlockPos, T> worldPosConsumer, T defaultValue) {
        return this.apply(worldPosConsumer).orElse(defaultValue);
    }

    default public void consume(BiConsumer<World, BlockPos> worldPosConsumer) {
        this.apply((world, pos) -> {
            worldPosConsumer.accept((World)world, (BlockPos)pos);
            return Optional.empty();
        });
    }
}
