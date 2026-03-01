package net.minecraft.util.concurrent;

import com.google.common.collect.Queues;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;

public interface ITaskQueue<T, F> {
    @Nullable
    public F poll();

    public boolean enqueue(T var1);

    public boolean isEmpty();

    public static final class Single<T>
    implements ITaskQueue<T, T> {
        private final Queue<T> queue;

        public Single(Queue<T> queueIn) {
            this.queue = queueIn;
        }

        @Override
        @Nullable
        public T poll() {
            return this.queue.poll();
        }

        @Override
        public boolean enqueue(T value) {
            return this.queue.add(value);
        }

        @Override
        public boolean isEmpty() {
            return this.queue.isEmpty();
        }
    }

    public static final class RunnableWithPriority
    implements Runnable {
        private final int priority;
        private final Runnable runnable;

        public RunnableWithPriority(int priorityIn, Runnable runnableIn) {
            this.priority = priorityIn;
            this.runnable = runnableIn;
        }

        @Override
        public void run() {
            this.runnable.run();
        }

        public int getPriority() {
            return this.priority;
        }
    }

    public static final class Priority
    implements ITaskQueue<RunnableWithPriority, Runnable> {
        private final List<ConcurrentLinkedQueue<Runnable>> queues;

        public Priority(int queueCount) {
            this.queues = IntStream.range(0, queueCount).mapToObj(p_219948_0_ -> Queues.newConcurrentLinkedQueue()).collect(Collectors.toList());
        }

        @Override
        @Nullable
        public Runnable poll() {
            for (ConcurrentLinkedQueue<Runnable> queue : this.queues) {
                Runnable runnable = queue.poll();
                if (runnable == null) continue;
                return runnable;
            }
            return null;
        }

        @Override
        public boolean enqueue(RunnableWithPriority value) {
            int i = value.getPriority();
            this.queues.get(i).add(value);
            return true;
        }

        @Override
        public boolean isEmpty() {
            return this.queues.stream().allMatch(Collection::isEmpty);
        }
    }
}
