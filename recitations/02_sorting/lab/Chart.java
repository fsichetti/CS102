import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

// Draws line charts straight to PNG files, using only the standard library.
// You don't need to read this file: Benchmark calls it for you.
public class Chart {

    private static final int PANEL_W = 820;
    private static final int LEGEND_W = 330;
    private static final int WIDTH = 2 * PANEL_W + LEGEND_W;
    private static final int HEIGHT = 900;
    private static final int MARGIN_LEFT = 150;
    private static final int MARGIN_RIGHT = 30;
    private static final int MARGIN_TOP = 110;
    private static final int MARGIN_BOTTOM = 100;

    // Colorblind-safe palette (Okabe-Ito), yellow dropped: invisible on a white background.
    private static final Color[] PALETTE = {
            new Color(0xE6, 0x9F, 0x00),
            new Color(0x56, 0xB4, 0xE9),
            new Color(0x00, 0x9E, 0x73),
            new Color(0x00, 0x72, 0xB2),
            new Color(0xD5, 0x5E, 0x00),
            new Color(0xCC, 0x79, 0xA7),
            new Color(0x33, 0x33, 0x33),
            new Color(0x7F, 0x3B, 0x08),
    };
    private static final Color GUIDE = new Color(160, 160, 160);

    static {
        System.setProperty("java.awt.headless", "true");
    }

    // One series per (names[i], xs[i], ys[i]); all values must be positive.
    // Left panel: both axes logarithmic. Right panel: both axes linear. Same data on both.
    public static void draw(String title, String xLabel, String yLabel,
                            String[] names, double[][] xs, double[][] ys,
                            String pngPath) throws IOException {
        double[] range = dataRange(xs, ys);   // xMin, xMax, yMin, yMax

        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 28));
        FontMetrics titleFm = g.getFontMetrics();
        g.drawString(title, (2 * PANEL_W - titleFm.stringWidth(title)) / 2, 45);

        drawPanel(g, 0, true, xLabel, yLabel, names, xs, ys, range);
        double[] zoomed = range.clone();
        zoomed[3] = subQuadraticMax(ys, range[3]);
        drawPanel(g, PANEL_W, false, xLabel, yLabel, names, xs, ys, zoomed);
        drawLegend(g, names);

        g.dispose();
        new File(pngPath).getParentFile().mkdirs();
        ImageIO.write(img, "png", new File(pngPath));
    }

    private static void drawPanel(Graphics2D g, int offsetX, boolean log, String xLabel, String yLabel,
                                  String[] names, double[][] xs, double[][] ys, double[] range) {
        int plotX = offsetX + MARGIN_LEFT;
        int plotY = MARGIN_TOP;
        int plotW = PANEL_W - MARGIN_LEFT - MARGIN_RIGHT;
        int plotH = HEIGHT - MARGIN_TOP - MARGIN_BOTTOM;

        // axis ranges, in axis coordinates (log10 of the value on a log panel)
        double[] tx = axisRange(range[0], range[1], log);
        double[] ty = axisRange(range[2], range[3], log);
        Axes a = new Axes(log, tx[0], tx[1], ty[0], ty[1], plotX, plotY, plotW, plotH);

        double[] xTicks = log ? logTicks(range[0], range[1]) : linearTicks(0, tx[1]);
        double[] yTicks = log ? logTicks(range[2], range[3]) : linearTicks(0, ty[1]);

        // gridlines + tick labels
        g.setStroke(new BasicStroke(1f));
        g.setFont(new Font("SansSerif", Font.PLAIN, 16));
        FontMetrics fm = g.getFontMetrics();
        for (double v : xTicks) {
            int px = a.px(v);
            if (px < plotX || px > plotX + plotW) continue;
            g.setColor(new Color(225, 225, 225));
            g.drawLine(px, plotY, px, plotY + plotH);
            g.setColor(Color.BLACK);
            String label = tickLabel(v);
            g.drawString(label, px - fm.stringWidth(label) / 2, plotY + plotH + fm.getHeight() + 6);
        }
        for (double v : yTicks) {
            int py = a.py(v);
            if (py < plotY || py > plotY + plotH) continue;
            g.setColor(new Color(225, 225, 225));
            g.drawLine(plotX, py, plotX + plotW, py);
            g.setColor(Color.BLACK);
            String label = tickLabel(v);
            g.drawString(label, plotX - fm.stringWidth(label) - 8, py + fm.getAscent() / 2 - 2);
        }

        g.setStroke(new BasicStroke(2f));
        g.drawLine(plotX, plotY + plotH, plotX + plotW, plotY + plotH);
        g.drawLine(plotX, plotY, plotX, plotY + plotH);

        // panel subtitle and axis labels
        g.setFont(new Font("SansSerif", Font.ITALIC, 20));
        String sub = log ? "log-log scale" : "linear scale";
        g.drawString(sub, plotX + (plotW - g.getFontMetrics().stringWidth(sub)) / 2, MARGIN_TOP - 20);
        g.setFont(new Font("SansSerif", Font.PLAIN, 20));
        FontMetrics axisFm = g.getFontMetrics();
        g.drawString(xLabel, plotX + (plotW - axisFm.stringWidth(xLabel)) / 2, HEIGHT - 30);
        AffineTransform saved = g.getTransform();
        int ylX = offsetX + 28, ylY = plotY + plotH / 2 + axisFm.stringWidth(yLabel) / 2;
        g.rotate(-Math.PI / 2, ylX, ylY);
        g.drawString(yLabel, ylX, ylY);
        g.setTransform(saved);

        // reference curves, through the bottom-left data point (smallest N, smallest value)
        g.setClip(plotX, plotY, plotW + 1, plotH + 1);
        ArrayList<Integer> labelYs = new ArrayList<>();
        drawGuide(g, a, range, 0, "N", labelYs);
        drawGuide(g, a, range, 1, "N log N", labelYs);
        drawGuide(g, a, range, 2, "N^2", labelYs);

        // series: solid lines, told apart by color and marker
        for (int s = 0; s < names.length; s++) {
            Color color = PALETTE[s % PALETTE.length];
            g.setColor(color);
            g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Path2D.Double line = new Path2D.Double();
            for (int i = 0; i < xs[s].length; i++) {
                if (i == 0) line.moveTo(a.px(xs[s][i]), a.py(ys[s][i]));
                else line.lineTo(a.px(xs[s][i]), a.py(ys[s][i]));
            }
            g.draw(line);
            for (int i = 0; i < xs[s].length; i++) drawMarker(g, s, color, a.px(xs[s][i]), a.py(ys[s][i]));
        }
        g.setClip(null);
    }

    // kind 0: N, 1: N log2 N, 2: N^2
    private static double guide(int kind, double n) {
        if (kind == 0) return n;
        if (kind == 1) return n * Math.log(n) / Math.log(2);
        return n * n;
    }

    private static void drawGuide(Graphics2D g, Axes a, double[] range, int kind, String label,
                                  ArrayList<Integer> labelYs) {
        double n0 = Math.max(range[0], 2);                    // log2 N must stay positive
        double scale = range[2] / guide(kind, n0);
        double nEnd = a.log ? Math.pow(10, a.txMax) : a.txMax;
        g.setColor(GUIDE);
        g.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                0, new float[] {6f, 6f}, 0));
        Path2D.Double path = new Path2D.Double();
        int lastX = a.px(n0), lastY = a.py(range[2]);
        path.moveTo(lastX, lastY);
        for (int step = 1; step <= 300; step++) {
            double n = n0 + (nEnd - n0) * step / 300;
            if (a.log) n = n0 * Math.pow(nEnd / n0, step / 300.0);
            double v = scale * guide(kind, n);
            int x = a.px(n), y = a.py(v);
            path.lineTo(x, y);
            if (y < a.plotY) break;                           // left the plot through the top
            lastX = x;
            lastY = y;
        }
        g.draw(path);                                         // one path, so the dashes are continuous
        // label just inside the last visible point, moved if it would cover another label
        g.setFont(new Font("SansSerif", Font.ITALIC, 16));
        int labelW = g.getFontMetrics().stringWidth(label);
        int labelY = Math.max(lastY - 8, a.plotY + 16);
        for (int used : labelYs) {
            if (Math.abs(labelY - used) < 18) labelY = (used - 18 >= a.plotY + 16) ? used - 18 : used + 18;
        }
        labelYs.add(labelY);
        g.drawString(label, Math.max(lastX - labelW - 4, a.plotX + 6), labelY);
    }

    private static void drawLegend(Graphics2D g, String[] names) {
        g.setFont(new Font("SansSerif", Font.PLAIN, 18));
        FontMetrics fm = g.getFontMetrics();
        int x = 2 * PANEL_W + 10;
        int y = MARGIN_TOP + 10;
        for (int s = 0; s < names.length; s++) {
            Color color = PALETTE[s % PALETTE.length];
            g.setColor(color);
            g.setStroke(new BasicStroke(3f));
            g.drawLine(x, y, x + 36, y);
            drawMarker(g, s, color, x + 18, y);
            g.setColor(Color.BLACK);
            g.drawString(names[s], x + 46, y + fm.getAscent() / 2 - 2);
            y += fm.getHeight() + 12;
        }
    }

    // 4 shapes, filled for the first 4 series and hollow for the next 4
    private static void drawMarker(Graphics2D g, int s, Color color, int cx, int cy) {
        int r = 7;
        Shape shape;
        switch (s % 4) {
            case 0: shape = new Ellipse2D.Double(cx - r, cy - r, 2 * r, 2 * r); break;
            case 1: shape = new Rectangle2D.Double(cx - r + 1, cy - r + 1, 2 * r - 2, 2 * r - 2); break;
            case 2: shape = polygon(new double[] {cx, cx - r, cx + r}, new double[] {cy - r, cy + r - 1, cy + r - 1}); break;
            default: shape = polygon(new double[] {cx, cx + r, cx, cx - r}, new double[] {cy - r, cy, cy + r, cy}); break;
        }
        if ((s / 4) % 2 == 0) {
            g.setColor(color);
            g.fill(shape);
        } else {
            g.setColor(Color.WHITE);
            g.fill(shape);
            g.setColor(color);
            g.setStroke(new BasicStroke(2.5f));
            g.draw(shape);
        }
    }

    private static Shape polygon(double[] xp, double[] yp) {
        Path2D.Double p = new Path2D.Double();
        p.moveTo(xp[0], yp[0]);
        for (int i = 1; i < xp.length; i++) p.lineTo(xp[i], yp[i]);
        p.closePath();
        return p;
    }

    // ---- coordinates ----

    private static class Axes {
        final boolean log;
        final double txMin, txMax, tyMin, tyMax;
        final int plotX, plotY, plotW, plotH;

        Axes(boolean log, double txMin, double txMax, double tyMin, double tyMax,
             int plotX, int plotY, int plotW, int plotH) {
            this.log = log;
            this.txMin = txMin; this.txMax = txMax; this.tyMin = tyMin; this.tyMax = tyMax;
            this.plotX = plotX; this.plotY = plotY; this.plotW = plotW; this.plotH = plotH;
        }

        int px(double v) {
            double t = log ? Math.log10(v) : v;
            return plotX + (int) Math.round((t - txMin) / (txMax - txMin) * plotW);
        }

        int py(double v) {
            double t = log ? Math.log10(v) : v;
            return plotY + plotH - (int) Math.round((t - tyMin) / (tyMax - tyMin) * plotH);
        }
    }

    // Top of the linear panel: the largest value among the series that grow slower than
    // quadratically (last ratio under 3), so N and N log N stay distinguishable; steeper
    // series leave through the top. If every series is quadratic, the full range.
    private static double subQuadraticMax(double[][] ys, double fullMax) {
        double max = 0;
        for (double[] y : ys) {
            int last = y.length - 1;
            if (last >= 1 && y[last] < 3 * y[last - 1]) {
                for (double v : y) max = Math.max(max, v);
            }
        }
        return max > 0 ? max * 1.1 : fullMax;
    }

    // xMin, xMax, yMin, yMax over all series
    private static double[] dataRange(double[][] xs, double[][] ys) {
        double[] r = {Double.POSITIVE_INFINITY, 0, Double.POSITIVE_INFINITY, 0};
        for (int s = 0; s < xs.length; s++) {
            for (int i = 0; i < xs[s].length; i++) {
                r[0] = Math.min(r[0], xs[s][i]);
                r[1] = Math.max(r[1], xs[s][i]);
                r[2] = Math.min(r[2], ys[s][i]);
                r[3] = Math.max(r[3], ys[s][i]);
            }
        }
        if (r[1] <= r[0]) { r[0] = 1; r[1] = 10; }
        if (r[3] <= r[2]) { r[2] = Math.max(r[2], 1e-4) / 2; r[3] = Math.max(r[3], 1e-4) * 2; }
        return r;
    }

    // a log axis is padded by 5% on both sides; a linear axis starts at 0
    private static double[] axisRange(double min, double max, boolean log) {
        if (!log) return new double[] {0, max * 1.05};
        double lo = Math.log10(min), hi = Math.log10(max), pad = (hi - lo) * 0.05;
        return new double[] {lo - pad, hi + pad};
    }

    // ---- ticks ----

    private static double[] linearTicks(double min, double max) {
        double raw = (max - min) / 5;
        double mag = Math.pow(10, Math.floor(Math.log10(raw)));
        double norm = raw / mag;
        double step = (norm <= 1 ? 1 : norm <= 2 ? 2 : norm <= 5 ? 5 : 10) * mag;
        ArrayList<Double> ticks = new ArrayList<>();
        for (double v = Math.ceil(min / step) * step; v <= max + step * 1e-9; v += step) ticks.add(v);
        return toArray(ticks);
    }

    private static double[] logTicks(double min, double max) {
        int lo = (int) Math.floor(Math.log10(min));
        int hi = (int) Math.ceil(Math.log10(max));
        boolean fewDecades = (hi - lo) < 3;
        ArrayList<Double> ticks = new ArrayList<>();
        for (int e = lo; e <= hi; e++) {
            double base = Math.pow(10, e);
            ticks.add(base);
            if (fewDecades) {
                ticks.add(base * 2);
                ticks.add(base * 5);
            }
        }
        return toArray(ticks);
    }

    private static double[] toArray(ArrayList<Double> list) {
        double[] result = new double[list.size()];
        for (int i = 0; i < result.length; i++) result[i] = list.get(i);
        return result;
    }

    private static String tickLabel(double v) {
        if (v == 0) return "0";
        if (Math.abs(v) >= 1) return String.format("%,.0f", v);
        return String.format("%.3g", v);
    }
}
