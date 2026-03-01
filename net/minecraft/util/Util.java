package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.MoreExecutors;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.Hash;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.util.ICharacterPredicate;
import net.minecraft.crash.ReportedException;
import net.minecraft.state.Property;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Bootstrap;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Util {
    private static final AtomicInteger NEXT_SERVER_WORKER_ID = new AtomicInteger(1);
    private static final ExecutorService BOOTSTRAP_SERVICE = Util.createNamedService("Bootstrap");
    private static final ExecutorService SERVER_EXECUTOR = Util.createNamedService("Main");
    private static final ExecutorService RENDERING_SERVICE = Util.startThreadedService();
    public static LongSupplier nanoTimeSupplier = System::nanoTime;
    public static final UUID DUMMY_UUID = new UUID(0L, 0L);
    private static final Logger LOGGER = LogManager.getLogger();
    private static Exception exceptionOpenUrl;
    private static final ExecutorService CAPE_EXECUTOR;

    public static <K, V> Collector<Map.Entry<? extends K, ? extends V>, ?, Map<K, V>> toMapCollector() {
        return Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue);
    }

    public static <T extends Comparable<T>> String getValueName(Property<T> property, Object value) {
        return property.getName((Comparable)value);
    }

    public static String makeTranslationKey(String type, @Nullable ResourceLocation id) {
        return id == null ? type + ".unregistered_sadface" : type + "." + id.getNamespace() + "." + id.getPath().replace('/', '.');
    }

    public static long milliTime() {
        return Util.nanoTime() / 1000000L;
    }

    public static long nanoTime() {
        return nanoTimeSupplier.getAsLong();
    }

    public static long millisecondsSinceEpoch() {
        return Instant.now().toEpochMilli();
    }

    private static ExecutorService createNamedService(String serviceName) {
        int i = MathHelper.clamp(Runtime.getRuntime().availableProcessors() - 1, 1, 7);
        ExecutorService executorservice = i <= 0 ? MoreExecutors.newDirectExecutorService() : new ForkJoinPool(i, p_lambda$createExecutor$0_1_ -> {
            ForkJoinWorkerThread forkjoinworkerthread = new ForkJoinWorkerThread(p_lambda$createExecutor$0_1_){

                @Override
                protected void onTermination(Throwable p_onTermination_1_) {
                    if (p_onTermination_1_ != null) {
                        LOGGER.warn("{} died", (Object)this.getName(), (Object)p_onTermination_1_);
                    } else {
                        LOGGER.debug("{} shutdown", (Object)this.getName());
                    }
                    super.onTermination(p_onTermination_1_);
                }
            };
            forkjoinworkerthread.setName("Worker-" + serviceName + "-" + NEXT_SERVER_WORKER_ID.getAndIncrement());
            return forkjoinworkerthread;
        }, Util::printException, true);
        return executorservice;
    }

    public static Executor getBootstrapService() {
        return BOOTSTRAP_SERVICE;
    }

    public static Executor getServerExecutor() {
        return SERVER_EXECUTOR;
    }

    public static Executor getRenderingService() {
        return RENDERING_SERVICE;
    }

    public static void shutdown() {
        Util.shutdownService(SERVER_EXECUTOR);
        Util.shutdownService(RENDERING_SERVICE);
        Util.shutdownService(CAPE_EXECUTOR);
    }

    private static void shutdownService(ExecutorService p_240985_0_) {
        boolean flag;
        p_240985_0_.shutdown();
        try {
            flag = p_240985_0_.awaitTermination(3L, TimeUnit.SECONDS);
        }
        catch (InterruptedException interruptedexception) {
            flag = false;
        }
        if (!flag) {
            p_240985_0_.shutdownNow();
        }
    }

    private static ExecutorService startThreadedService() {
        return Executors.newCachedThreadPool(p_lambda$createIoExecutor$1_0_ -> {
            Thread thread = new Thread(p_lambda$createIoExecutor$1_0_);
            thread.setName("IO-Worker-" + NEXT_SERVER_WORKER_ID.getAndIncrement());
            thread.setUncaughtExceptionHandler(Util::printException);
            return thread;
        });
    }

    public static <T> CompletableFuture<T> completedExceptionallyFuture(Throwable throwableIn) {
        CompletableFuture completablefuture = new CompletableFuture();
        completablefuture.completeExceptionally(throwableIn);
        return completablefuture;
    }

    public static void toRuntimeException(Throwable throwableIn) {
        throw throwableIn instanceof RuntimeException ? (RuntimeException)throwableIn : new RuntimeException(throwableIn);
    }

    private static void printException(Thread thread, Throwable throwable) {
        Util.pauseDevMode(throwable);
        if (throwable instanceof CompletionException) {
            throwable = throwable.getCause();
        }
        if (throwable instanceof ReportedException) {
            Bootstrap.printToSYSOUT(((ReportedException)throwable).getCrashReport().getCompleteReport());
            System.exit(-1);
        }
        LOGGER.error(String.format("Caught exception in thread %s", thread), throwable);
    }

    @Nullable
    public static Type<?> attemptDataFix(DSL.TypeReference type, String choiceName) {
        return !SharedConstants.useDatafixers ? null : Util.attemptDataFixInternal(type, choiceName);
    }

    @Nullable
    private static Type<?> attemptDataFixInternal(DSL.TypeReference typeIn, String choiceName) {
        Type<?> type;
        block2: {
            type = null;
            try {
                type = DataFixesManager.getDataFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getVersion().getWorldVersion())).getChoiceType(typeIn, choiceName);
            }
            catch (IllegalArgumentException illegalargumentexception) {
                LOGGER.debug("No data fixer registered for {}", (Object)choiceName);
                if (!SharedConstants.developmentMode) break block2;
                throw illegalargumentexception;
            }
        }
        return type;
    }

    public static OS getOSType() {
        String s = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        if (s.contains("win")) {
            return OS.WINDOWS;
        }
        if (s.contains("mac")) {
            return OS.OSX;
        }
        if (s.contains("solaris")) {
            return OS.SOLARIS;
        }
        if (s.contains("sunos")) {
            return OS.SOLARIS;
        }
        if (s.contains("linux")) {
            return OS.LINUX;
        }
        return s.contains("unix") ? OS.LINUX : OS.UNKNOWN;
    }

    public static Stream<String> getJvmFlags() {
        RuntimeMXBean runtimemxbean = ManagementFactory.getRuntimeMXBean();
        return runtimemxbean.getInputArguments().stream().filter(p_lambda$getJvmFlags$2_0_ -> p_lambda$getJvmFlags$2_0_.startsWith("-X"));
    }

    public static <T> T getLast(List<T> listIn) {
        return listIn.get(listIn.size() - 1);
    }

    public static <T> T getElementAfter(Iterable<T> iterable, @Nullable T element) {
        Iterator<T> iterator = iterable.iterator();
        T t = iterator.next();
        if (element != null) {
            T t1 = t;
            while (t1 != element) {
                if (!iterator.hasNext()) continue;
                t1 = iterator.next();
            }
            if (iterator.hasNext()) {
                return iterator.next();
            }
        }
        return t;
    }

    public static <T> T getElementBefore(Iterable<T> iterable, @Nullable T current) {
        Iterator<T> iterator = iterable.iterator();
        T t = null;
        while (iterator.hasNext()) {
            T t1 = iterator.next();
            if (t1 == current) {
                if (t != null) break;
                t = iterator.hasNext() ? Iterators.getLast(iterator) : current;
                break;
            }
            t = t1;
        }
        return t;
    }

    public static <T> T make(Supplier<T> supplier) {
        return supplier.get();
    }

    public static <T> T make(T object, Consumer<T> consumer) {
        consumer.accept(object);
        return object;
    }

    public static <K> Hash.Strategy<K> identityHashStrategy() {
        return IdentityStrategy.INSTANCE;
    }

    public static <V> CompletableFuture<List<V>> gather(List<? extends CompletableFuture<? extends V>> futuresIn) {
        ArrayList list = Lists.newArrayListWithCapacity(futuresIn.size());
        CompletableFuture[] completablefuture = new CompletableFuture[futuresIn.size()];
        CompletableFuture completablefuture1 = new CompletableFuture();
        futuresIn.forEach(p_lambda$gather$4_3_ -> {
            int i = list.size();
            list.add(null);
            completablefuture[i] = p_lambda$gather$4_3_.whenComplete((p_lambda$null$3_3_, p_lambda$null$3_4_) -> {
                if (p_lambda$null$3_4_ != null) {
                    completablefuture1.completeExceptionally((Throwable)p_lambda$null$3_4_);
                } else {
                    list.set(i, p_lambda$null$3_3_);
                }
            });
        });
        return CompletableFuture.allOf(completablefuture).applyToEither((CompletionStage)completablefuture1, p_lambda$gather$5_1_ -> list);
    }

    public static <T> Stream<T> streamOptional(Optional<? extends T> optionalIn) {
        return optionalIn.isPresent() ? Stream.of(optionalIn.get()) : Stream.empty();
    }

    public static Exception getExceptionOpenUrl() {
        return exceptionOpenUrl;
    }

    public static void setExceptionOpenUrl(Exception p_setExceptionOpenUrl_0_) {
        exceptionOpenUrl = p_setExceptionOpenUrl_0_;
    }

    public static ExecutorService getCapeExecutor() {
        return CAPE_EXECUTOR;
    }

    public static <T> Optional<T> acceptOrElse(Optional<T> opt, Consumer<T> consumer, Runnable orElse) {
        if (opt.isPresent()) {
            consumer.accept(opt.get());
        } else {
            orElse.run();
        }
        return opt;
    }

    public static Runnable namedRunnable(Runnable runnableIn, Supplier<String> supplierIn) {
        return runnableIn;
    }

    public static <T extends Throwable> T pauseDevMode(T throwableIn) {
        if (SharedConstants.developmentMode) {
            LOGGER.error("Trying to throw a fatal exception, pausing in IDE", throwableIn);
            try {
                while (true) {
                    Thread.sleep(1000L);
                    LOGGER.error("paused");
                }
            }
            catch (InterruptedException interruptedexception) {
                return throwableIn;
            }
        }
        return throwableIn;
    }

    public static String getMessage(Throwable throwableIn) {
        if (throwableIn.getCause() != null) {
            return Util.getMessage(throwableIn.getCause());
        }
        return throwableIn.getMessage() != null ? throwableIn.getMessage() : throwableIn.toString();
    }

    public static <T> T getRandomObject(T[] selections, Random rand) {
        return selections[rand.nextInt(selections.length)];
    }

    public static int getRandomInt(int[] selections, Random rand) {
        return selections[rand.nextInt(selections.length)];
    }

    private static BooleanSupplier func_244363_a(final Path p_244363_0_, final Path p_244363_1_) {
        return new BooleanSupplier(){

            @Override
            public boolean getAsBoolean() {
                try {
                    Files.move(p_244363_0_, p_244363_1_, new CopyOption[0]);
                    return true;
                }
                catch (IOException ioexception) {
                    LOGGER.error("Failed to rename", (Throwable)ioexception);
                    return false;
                }
            }

            public String toString() {
                return "rename " + String.valueOf(p_244363_0_) + " to " + String.valueOf(p_244363_1_);
            }
        };
    }

    private static BooleanSupplier func_244362_a(final Path p_244362_0_) {
        return new BooleanSupplier(){

            @Override
            public boolean getAsBoolean() {
                try {
                    Files.deleteIfExists(p_244362_0_);
                    return true;
                }
                catch (IOException ioexception) {
                    LOGGER.warn("Failed to delete", (Throwable)ioexception);
                    return false;
                }
            }

            public String toString() {
                return "delete old " + String.valueOf(p_244362_0_);
            }
        };
    }

    private static BooleanSupplier func_244366_b(final Path p_244366_0_) {
        return new BooleanSupplier(){

            @Override
            public boolean getAsBoolean() {
                return !Files.exists(p_244366_0_, new LinkOption[0]);
            }

            public String toString() {
                return "verify that " + String.valueOf(p_244366_0_) + " is deleted";
            }
        };
    }

    private static BooleanSupplier func_244367_c(final Path p_244367_0_) {
        return new BooleanSupplier(){

            @Override
            public boolean getAsBoolean() {
                return Files.isRegularFile(p_244367_0_, new LinkOption[0]);
            }

            public String toString() {
                return "verify that " + String.valueOf(p_244367_0_) + " is present";
            }
        };
    }

    private static boolean func_244365_a(BooleanSupplier ... p_244365_0_) {
        for (BooleanSupplier booleansupplier : p_244365_0_) {
            if (booleansupplier.getAsBoolean()) continue;
            LOGGER.warn("Failed to execute {}", (Object)booleansupplier);
            return false;
        }
        return true;
    }

    private static boolean func_244359_a(int p_244359_0_, String p_244359_1_, BooleanSupplier ... p_244359_2_) {
        for (int i = 0; i < p_244359_0_; ++i) {
            if (Util.func_244365_a(p_244359_2_)) {
                return true;
            }
            LOGGER.error("Failed to {}, retrying {}/{}", (Object)p_244359_1_, (Object)i, (Object)p_244359_0_);
        }
        LOGGER.error("Failed to {}, aborting, progress might be lost", (Object)p_244359_1_);
        return false;
    }

    public static void backupThenUpdate(File current, File latest, File oldBackup) {
        Util.func_244364_a(current.toPath(), latest.toPath(), oldBackup.toPath());
    }

    public static void func_244364_a(Path p_244364_0_, Path p_244364_1_, Path p_244364_2_) {
        int i = 10;
        if ((!Files.exists(p_244364_0_, new LinkOption[0]) || Util.func_244359_a(10, "create backup " + String.valueOf(p_244364_2_), Util.func_244362_a(p_244364_2_), Util.func_244363_a(p_244364_0_, p_244364_2_), Util.func_244367_c(p_244364_2_))) && Util.func_244359_a(10, "remove old " + String.valueOf(p_244364_0_), Util.func_244362_a(p_244364_0_), Util.func_244366_b(p_244364_0_)) && !Util.func_244359_a(10, "replace " + String.valueOf(p_244364_0_) + " with " + String.valueOf(p_244364_1_), Util.func_244363_a(p_244364_1_, p_244364_0_), Util.func_244367_c(p_244364_0_))) {
            Util.func_244359_a(10, "restore " + String.valueOf(p_244364_0_) + " from " + String.valueOf(p_244364_2_), Util.func_244363_a(p_244364_2_, p_244364_0_), Util.func_244367_c(p_244364_0_));
        }
    }

    public static int func_240980_a_(String p_240980_0_, int p_240980_1_, int p_240980_2_) {
        int i = p_240980_0_.length();
        if (p_240980_2_ >= 0) {
            for (int j = 0; p_240980_1_ < i && j < p_240980_2_; ++j) {
                if (!Character.isHighSurrogate(p_240980_0_.charAt(p_240980_1_++)) || p_240980_1_ >= i || !Character.isLowSurrogate(p_240980_0_.charAt(p_240980_1_))) continue;
                ++p_240980_1_;
            }
        } else {
            for (int k = p_240980_2_; p_240980_1_ > 0 && k < 0; ++k) {
                if (!Character.isLowSurrogate(p_240980_0_.charAt(--p_240980_1_)) || p_240980_1_ <= 0 || !Character.isHighSurrogate(p_240980_0_.charAt(p_240980_1_ - 1))) continue;
                --p_240980_1_;
            }
        }
        return p_240980_1_;
    }

    public static Consumer<String> func_240982_a_(String prefix, Consumer<String> p_240982_1_) {
        return p_lambda$func_240982_a_$6_2_ -> p_240982_1_.accept(prefix + p_lambda$func_240982_a_$6_2_);
    }

    public static DataResult<int[]> validateIntStreamSize(IntStream stream, int size) {
        int[] aint = stream.limit(size + 1).toArray();
        if (aint.length != size) {
            String s = "Input is not a list of " + size + " ints";
            return aint.length >= size ? DataResult.error(s, Arrays.copyOf(aint, size)) : DataResult.error(s);
        }
        return DataResult.success(aint);
    }

    public static void func_240994_l_() {
        Thread thread = new Thread("Timer hack thread"){

            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(Integer.MAX_VALUE);
                    }
                }
                catch (InterruptedException interruptedexception) {
                    LOGGER.warn("Timer hack thread interrupted, that really should not happen");
                    return;
                }
            }
        };
        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
        thread.start();
    }

    public static void func_240984_a_(Path p_240984_0_, Path p_240984_1_, Path p_240984_2_) throws IOException {
        Path path = p_240984_0_.relativize(p_240984_2_);
        Path path1 = p_240984_1_.resolve(path);
        Files.copy(p_240984_2_, path1, new CopyOption[0]);
    }

    public static String func_244361_a(String p_244361_0_, ICharacterPredicate p_244361_1_) {
        return p_244361_0_.toLowerCase(Locale.ROOT).chars().mapToObj(p_lambda$func_244361_a$7_1_ -> p_244361_1_.test((char)p_lambda$func_244361_a$7_1_) ? Character.toString((char)p_lambda$func_244361_a$7_1_) : "_").collect(Collectors.joining());
    }

    static {
        CAPE_EXECUTOR = Util.createNamedService("Cape");
    }

    /*
     * Uses 'sealed' constructs - enablewith --sealed true
     */
    public static enum OS {
        LINUX,
        SOLARIS,
        WINDOWS{

            @Override
            protected String[] getOpenCommandLine(URL url) {
                return new String[]{"rundll32", "url.dll,FileProtocolHandler", url.toString()};
            }
        }
        ,
        OSX{

            @Override
            protected String[] getOpenCommandLine(URL url) {
                return new String[]{"open", url.toString()};
            }
        }
        ,
        UNKNOWN;


        public void openURL(URL url) {
            try {
                Process process = AccessController.doPrivileged(() -> Runtime.getRuntime().exec(this.getOpenCommandLine(url)));
                for (String s : IOUtils.readLines(process.getErrorStream())) {
                    LOGGER.error(s);
                }
                process.getInputStream().close();
                process.getErrorStream().close();
                process.getOutputStream().close();
            }
            catch (IOException | PrivilegedActionException ioexception) {
                LOGGER.error("Couldn't open url '{}'", (Object)url, (Object)ioexception);
                exceptionOpenUrl = ioexception;
            }
        }

        public void openURI(URI uri) {
            try {
                this.openURL(uri.toURL());
            }
            catch (MalformedURLException malformedurlexception) {
                LOGGER.error("Couldn't open uri '{}'", (Object)uri, (Object)malformedurlexception);
            }
        }

        public void openFile(File fileIn) {
            try {
                this.openURL(fileIn.toURI().toURL());
            }
            catch (MalformedURLException malformedurlexception) {
                LOGGER.error("Couldn't open file '{}'", (Object)fileIn, (Object)malformedurlexception);
            }
        }

        protected String[] getOpenCommandLine(URL url) {
            String s = url.toString();
            if ("file".equals(url.getProtocol())) {
                s = s.replace("file:", "file://");
            }
            return new String[]{"xdg-open", s};
        }

        public void openURI(String uri) {
            try {
                this.openURL(new URI(uri).toURL());
            }
            catch (IllegalArgumentException | MalformedURLException | URISyntaxException malformedurlexception) {
                LOGGER.error("Couldn't open uri '{}'", (Object)uri, (Object)malformedurlexception);
            }
        }
    }

    static enum IdentityStrategy implements Hash.Strategy<Object>
    {
        INSTANCE;


        @Override
        public int hashCode(Object p_hashCode_1_) {
            return System.identityHashCode(p_hashCode_1_);
        }

        @Override
        public boolean equals(Object p_equals_1_, Object p_equals_2_) {
            return p_equals_1_ == p_equals_2_;
        }
    }
}
