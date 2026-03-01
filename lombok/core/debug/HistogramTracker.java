package lombok.core.debug;

import java.io.PrintStream;
import java.util.GregorianCalendar;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicStampedReference;
import lombok.core.debug.ProblemReporter;

public class HistogramTracker {
    private static final long[] RANGES = new long[]{250001L, 500001L, 1000001L, 2000001L, 4000001L, 8000001L, 16000001L, 32000001L, 64000001L, 128000001L, 256000001L, 512000001L, 1024000001L, 2048000001L, 10000000001L};
    private static final long REPORT_WINDOW = 60000L;
    private final String category;
    private final AtomicStampedReference<long[]> bars = new AtomicStampedReference<long[]>(new long[RANGES.length + 2], 0);
    private final AtomicBoolean addedSysHook = new AtomicBoolean(false);
    private final PrintStream out;

    public HistogramTracker(String category) {
        this.category = category;
        this.out = null;
        this.printInit();
    }

    public HistogramTracker(String category, PrintStream out) {
        this.category = category;
        this.out = out;
        this.printInit();
    }

    private void printInit() {
        if (this.category == null) {
            if (this.out == null) {
                ProblemReporter.info("Initialized histogram", null);
            } else {
                this.out.println("Initialized histogram");
            }
        } else if (this.out == null) {
            ProblemReporter.info(String.format("Initialized histogram tracker for '%s'", this.category), null);
        } else {
            this.out.printf("Initialized histogram tracker for '%s'%n", this.category);
        }
    }

    public long start() {
        return System.nanoTime();
    }

    public void end(long startToken) {
        long[] newBars;
        if (!this.addedSysHook.getAndSet(true)) {
            Runtime.getRuntime().addShutdownHook(new Thread("Histogram Printer"){

                @Override
                public void run() {
                    int[] currentInterval = new int[1];
                    long[] b = (long[])HistogramTracker.this.bars.get(currentInterval);
                    HistogramTracker.this.printReport(currentInterval[0], b);
                }
            });
        }
        long end = System.nanoTime();
        long now = System.currentTimeMillis();
        long delta = end - startToken;
        if (delta < 0L) {
            delta = 0L;
        }
        int interval = (int)(now / 60000L);
        int[] currentInterval = new int[1];
        long[] bars = this.bars.get(currentInterval);
        if (currentInterval[0] != interval) {
            this.printReport(currentInterval[0], bars);
            newBars = new long[RANGES.length + 2];
            if (!this.bars.compareAndSet(bars, newBars, currentInterval[0], interval)) {
                newBars = this.bars.get(currentInterval);
            }
        } else {
            newBars = bars;
        }
        int n = RANGES.length + 1;
        newBars[n] = newBars[n] + delta;
        int i = 0;
        while (i < RANGES.length) {
            if (delta < RANGES[i]) {
                int n2 = i;
                newBars[n2] = newBars[n2] + 1L;
                return;
            }
            ++i;
        }
        int n3 = RANGES.length;
        newBars[n3] = newBars[n3] + 1L;
    }

    private void printReport(int interval, long[] bars) {
        StringBuilder sb = new StringBuilder();
        if (this.category != null) {
            sb.append(this.category).append(" ");
        }
        sb.append("[");
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis((long)interval * 60000L);
        int hour = gc.get(11);
        int minute = gc.get(12);
        if (hour < 10) {
            sb.append('0');
        }
        sb.append(hour).append(":");
        if (minute < 10) {
            sb.append('0');
        }
        sb.append(minute).append("] {");
        long sum = bars[RANGES.length];
        int count = 0;
        int lastZeroPos = sb.length();
        int i = 0;
        while (i < RANGES.length) {
            sum += bars[i];
            sb.append(bars[i]);
            if (bars[i] != 0L) {
                lastZeroPos = sb.length();
            }
            sb.append(" ");
            if (++count == 3) {
                sb.append("-- ");
            }
            if (count == 9) {
                sb.append("-- ");
            }
            ++i;
        }
        if (sum == 0L) {
            return;
        }
        sb.setLength(lastZeroPos);
        double millis = (double)bars[RANGES.length + 1] / 1000000.0;
        long over = bars[RANGES.length];
        if (over > 0L) {
            sb.append(" -- ").append(bars[RANGES.length]);
        }
        sb.append("} total calls: ").append(sum).append(" total time (millis): ").append((int)(millis + 0.5));
        if (this.out == null) {
            ProblemReporter.info(sb.toString(), null);
        } else {
            this.out.println(sb.toString());
        }
    }
}
