/*  ch8 - ReceiptPrinter
 *
 * Should print
 *
 *     SHOP RECEIPT: total 24.5
 *
 * Two errors. Both of them are the compiler telling you something about what
 * an interface is allowed to contain, and what it demands of the classes that
 * implement it.
 */
public class ReceiptPrinter {
    public static void main( String [] args ) {
        Printable p = new Receipt("SHOP RECEIPT", 24.5);
        p.print();
    }
}

interface Printable {
    String header = "RECEIPT";

    void print();
}

class Receipt implements Printable {
    private double total;

    public Receipt( String h, double total ) {
        header = h;
        this.total = total;
    }

    void print() {
        System.out.println(header + ": total " + total);
    }
}
