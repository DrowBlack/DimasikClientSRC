package net.minecraft.util.concurrent;

import com.mojang.datafixers.util.Either;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ITaskExecutor<Msg>
extends AutoCloseable {
    public String getName();

    public void enqueue(Msg var1);

    @Override
    default public void close() {
    }

    default public <Source> CompletableFuture<Source> func_213141_a(Function<? super ITaskExecutor<Source>, ? extends Msg> p_213141_1_) {
        CompletableFuture completablefuture = new CompletableFuture();
        Msg msg = p_213141_1_.apply(ITaskExecutor.inline("ask future procesor handle", completablefuture::complete));
        this.enqueue(msg);
        return completablefuture;
    }

    default public <Source> CompletableFuture<Source> func_233528_c_(Function<? super ITaskExecutor<Either<Source, Exception>>, ? extends Msg> p_233528_1_) {
        CompletableFuture completablefuture = new CompletableFuture();
        Msg msg = p_233528_1_.apply(ITaskExecutor.inline("ask future procesor handle", p_233527_1_ -> {
            p_233527_1_.ifLeft(completablefuture::complete);
            p_233527_1_.ifRight(completablefuture::completeExceptionally);
        }));
        this.enqueue(msg);
        return completablefuture;
    }

    public static <Msg> ITaskExecutor<Msg> inline(final String name, final Consumer<Msg> p_213140_1_) {
        return new ITaskExecutor<Msg>(){

            @Override
            public String getName() {
                return name;
            }

            @Override
            public void enqueue(Msg taskIn) {
                p_213140_1_.accept(taskIn);
            }

            public String toString() {
                return name;
            }
        };
    }
}
