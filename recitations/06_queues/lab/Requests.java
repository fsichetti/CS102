/*  The requests to the web server of NASA's Kennedy Space Center on July 13, 1995,
 *  the day of the launch of the Space Shuttle mission STS-70 (09:41 in the morning).
 *      javac Requests.java
 *      java Requests              the rate limiter allows 20 requests per host in any 60 seconds
 *      java Requests 100 3600     100 requests in any 3600 seconds
 *
 *  Uses Tail (Part 1), HitCounter and RateLimiter (Part 2).
 *  One line per request: the host that sent it, the time, the page, ...
 *  Data: NASA-HTTP logs, The Internet Traffic Archive (data/nasa-1995-07-13.log).
 */
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import windows.HitCounter;
import windows.RateLimiter;
import windows.Tail;

public class Requests {
    static final Path LOG = Path.of("data", "nasa-1995-07-13.log");

    static final int LAST = 5;          // lines, for tail
    static final int MINUTE = 60;       // seconds
    static int limit = 20;              // requests per host, for the rate limiter
    static int window = 60;             // seconds, for the rate limiter

    static String[] hosts;              // hosts[i] sent request i
    static long[] times;                // at times[i], in seconds since midnight

    public static void main(String[] args) throws IOException {
        if (!Files.exists(LOG)) {
            System.out.println("cannot read " + LOG + ": run Requests from the lab folder");
            return;
        }
        if (args.length == 2) {
            limit = Integer.parseInt(args[0]);
            window = Integer.parseInt(args[1]);
        }
        load();
        System.out.println(times.length + " requests");

        section("Part 1: the last " + LAST + " lines", Requests::tail);
        section("Part 2: the busiest " + MINUTE + " seconds of each hour", Requests::busiest);
        section("Part 2: at most " + limit + " requests per host in any " + window + " seconds", Requests::rateLimit);
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

    // Part 1
    static void tail() {
        try (Scanner in = new Scanner(LOG)) {
            for (String line : Tail.tail(in, LAST)) System.out.println(line);
        } catch (IOException e) {
            System.out.println("cannot read " + LOG);
        }
    }

    // Part 2: after every request, the number of requests in the last MINUTE seconds
    static void busiest() {
        HitCounter counter = new HitCounter(MINUTE);
        int[] most = new int[24];       // most[h]: the largest count during hour h
        long[] when = new long[24];
        for (long t : times) {
            counter.hit(t);
            int c = counter.count(t);
            int h = (int) (t / 3600);
            if (c > most[h]) {
                most[h] = c;
                when[h] = t;
            }
        }
        int top = 0;
        for (int c : most) top = Math.max(top, c);
        System.out.printf("%-6s %8s   %-10s%n", "hour", "requests", "ending at");
        for (int h = 0; h < 24; h++)
            System.out.printf("%02d     %8d   %-10s %s%n", h, most[h], clock(when[h]), "#".repeat(most[h] * 40 / top));
    }

    // Part 2: one rate limiter per host
    static void rateLimit() {
        Map<String, RateLimiter> limiters = new HashMap<>();    // the limiter of each host
        Map<String, Integer> rejected = new HashMap<>();        // how many requests of each host were rejected
        int total = 0;
        for (int i = 0; i < times.length; i++) {
            RateLimiter r = limiters.computeIfAbsent(hosts[i], host -> new RateLimiter(limit, window));
            if (!r.allow(times[i])) {
                rejected.merge(hosts[i], 1, Integer::sum);
                total++;
            }
        }
        System.out.printf("%d requests rejected, from %d hosts out of %d%n%n", total, rejected.size(), limiters.size());
        List<String> worst = new ArrayList<>(rejected.keySet());
        worst.sort((a, b) -> rejected.get(b) - rejected.get(a));
        System.out.printf("%-32s %8s %8s%n", "host", "rejected", "sent");
        Map<String, Integer> sent = new HashMap<>();
        for (String h : hosts) sent.merge(h, 1, Integer::sum);
        for (String h : worst.subList(0, Math.min(10, worst.size())))
            System.out.printf("%-32s %8d %8d%n", h, rejected.get(h), sent.get(h));
    }

    static String clock(long t) { return String.format("%02d:%02d:%02d", t / 3600, t / 60 % 60, t % 60); }

    // a line: 199.72.81.55 - - [13/Jul/1995:09:41:55 -0400] "GET /shuttle/countdown/ HTTP/1.0" 200 3985
    static void load() throws IOException {
        List<String> lines = Files.readAllLines(LOG);
        hosts = new String[lines.size()];
        times = new long[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            hosts[i] = line.substring(0, line.indexOf(' '));
            String clock = line.substring(line.indexOf('[') + 13, line.indexOf('[') + 21);    // 09:41:55
            String[] hms = clock.split(":");
            times[i] = Long.parseLong(hms[0]) * 3600 + Long.parseLong(hms[1]) * 60 + Long.parseLong(hms[2]);
        }
    }
}
