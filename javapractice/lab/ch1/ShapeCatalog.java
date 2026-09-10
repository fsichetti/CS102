/*  ch1 - ShapeCatalog
 *
 * Should print
 *
 *     Square with area 9.0
 *
 * There are two separate errors. One of them is fixed by deleting a line,
 * and you should be able to say why that line could never have worked.
 */
public class ShapeCatalog {
    public static void main( String [] args ) {
        Shape blob = new Shape("blob");
        System.out.println(blob);

        Shape s = new Square(3);
        System.out.println(s);
    }
}

abstract class Shape {
    protected String name;

    public Shape( String name ) {
        this.name = name;
    }

    public abstract double area();

    public String toString() {
        return name + " with area " + area();
    }
}

class Square extends Shape {
    private double side;

    public Square( double s ) {
        super("Square");
        side = s;
    }

    public double perimeter() {
        return 4 * side;
    }
}
