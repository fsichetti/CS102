/*  Central Park, New York: the high and low temperature of every day since 1870, in °F.
 *      javac Weather.java
 *      java Weather
 *
 *  Uses MovingAverage (Part 1), Windows.min and Windows.max (Part 3), Windows.longestStable (optional).
 *  Data: NOAA, Global Historical Climatology Network daily, station USW00094728 (data/centralpark.csv).
 */
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import windows.MovingAverage;
import windows.Windows;

public class Weather {
    static LocalDate[] dates;
    static double[] highs;
    static double[] lows;

    static final int MONTH = 30;    // days, for the moving average
    static final int WEEK = 7;      // days, for heat waves and cold spells
    static final double BAND = 10;  // °F, for the stable stretches

    public static void main(String[] args) throws IOException {
        load(Path.of("data", "centralpark.csv"));
        System.out.println(dates.length + " days, " + dates[0] + " to " + dates[dates.length - 1]);

        section("Part 1: the coldest and warmest " + MONTH + " days of each decade", Weather::months);
        section("Part 3: the hottest and coldest week of each decade", Weather::weeks);
        section("Optional: the longest stretch of each decade with highs within " + BAND + " °F", Weather::stable);
    }

    // runs one part; if it is not written yet, says so and moves on
    static void section(String title, Runnable r) {
        System.out.println("\n== " + title);
        try {
            r.run();
        } catch (UnsupportedOperationException e) {
            System.out.println("not written yet: " + e.getMessage());
        }
    }

    // Part 1: the average of (high + low) / 2 over the last MONTH days, for every day
    static void months() {
        MovingAverage m = new MovingAverage(MONTH);
        double[] averages = new double[dates.length];     // averages[j]: of the MONTH days ending on day j
        for (int j = 0; j < dates.length; j++) averages[j] = m.addNext((highs[j] + lows[j]) / 2);

        System.out.printf("averages of (high + low) / 2 over %d days, by the decade of the last day%n%n", MONTH);
        System.out.printf("%-8s %-24s %-24s%n", "decade", "coldest", "warmest");
        int decade = -1, coldest = -1, warmest = -1;
        double coldestAvg = 0, warmestAvg = 0;
        for (int j = MONTH - 1; j < dates.length; j++) {     // before MONTH - 1, fewer than MONTH days
            double avg = averages[j];
            if (decade(j) != decade) {
                if (decade != -1) printMonths(decade, coldest, coldestAvg, warmest, warmestAvg);
                decade = decade(j);
                coldest = warmest = j;
                coldestAvg = warmestAvg = avg;
            }
            if (avg < coldestAvg) { coldest = j; coldestAvg = avg; }
            if (avg > warmestAvg) { warmest = j; warmestAvg = avg; }
        }
        printMonths(decade, coldest, coldestAvg, warmest, warmestAvg);
    }

    static void printMonths(int decade, int coldest, double coldestAvg, int warmest, double warmestAvg) {
        System.out.printf("%-8s %-24s %-24s%n", decade + "s",
            String.format("%5.1f  to %s", coldestAvg, dates[coldest]),
            String.format("%5.1f  to %s", warmestAvg, dates[warmest]));
    }

    // Part 3: hottest week = the WEEK days whose lowest high is the highest; coldest week the opposite
    static void weeks() {
        double[] coolestHigh = Windows.min(highs, WEEK);    // window i: days i to i + WEEK - 1
        double[] warmestHigh = Windows.max(highs, WEEK);
        System.out.printf("%d days in a row with every high at least (hottest) or at most (coldest) the value%n%n", WEEK);
        System.out.printf("%-8s %-24s %-24s%n", "decade", "hottest", "coldest");
        int i = 0;
        while (i < coolestHigh.length) {
            int decade = decade(i);
            int hottest = i, coldest = i;
            for (; i < coolestHigh.length && decade(i) == decade; i++) {
                if (coolestHigh[i] > coolestHigh[hottest]) hottest = i;
                if (warmestHigh[i] < warmestHigh[coldest]) coldest = i;
            }
            System.out.printf("%-8s %-24s %-24s%n", decade + "s",
                String.format(">= %3.0f  from %s", coolestHigh[hottest], dates[hottest]),
                String.format("<= %3.0f  from %s", warmestHigh[coldest], dates[coldest]));
        }
    }

    // optional: the longest stretch of each decade whose highs are all within BAND degrees
    static void stable() {
        System.out.printf("%-8s %6s   %-24s %s%n", "decade", "days", "from", "highs");
        int start = 0;
        while (start < dates.length) {
            int end = start;
            while (end < dates.length && decade(end) == decade(start)) end++;
            double[] decadeHighs = java.util.Arrays.copyOfRange(highs, start, end);
            int[] best = Windows.longestStable(decadeHighs, BAND);
            int i = start + best[0], j = start + best[1];
            double lo = highs[i], hi = highs[i];
            for (int d = i; d <= j; d++) {
                lo = Math.min(lo, highs[d]);
                hi = Math.max(hi, highs[d]);
            }
            System.out.printf("%-8s %6d   %-24s %.0f to %.0f%n", decade(start) + "s", j - i + 1,
                dates[i] + " to " + dates[j], lo, hi);
            start = end;
        }
    }

    static int decade(int day) { return dates[day].getYear() / 10 * 10; }

    // reads the file: a header, then one line per day, "1870-01-01,43,34" (date, high, low)
    static void load(Path file) throws IOException {
        List<String> lines;
        try {
            lines = Files.readAllLines(file);
        } catch (IOException e) {
            throw new IOException("cannot read " + file + ": run Weather from the lab folder", e);
        }
        int n = lines.size() - 1;
        dates = new LocalDate[n];
        highs = new double[n];
        lows = new double[n];
        for (int i = 0; i < n; i++) {
            String[] f = lines.get(i + 1).split(",");
            dates[i] = LocalDate.parse(f[0]);
            highs[i] = Double.parseDouble(f[1]);
            lows[i] = Double.parseDouble(f[2]);
        }
    }
}
