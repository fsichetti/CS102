/*  ch6 - PayReport
 *
 * Should print the manager's real pay:
 *
 *     Dana Reyes is paid 6000.0
 *
 * This one compiles. There is an annotation you could have written that
 * would have turned this into a compile error instead of a wrong number.
 * add it once you have found the bug.
 */
public class PayReport {
    public static void main( String [] args ) {
        Employee e = new Manager("Dana Reyes", 5000, 1000);
        System.out.println(e);
    }
}

class Employee {
    protected String name;
    protected double base;

    public Employee( String name, double base ) {
        this.name = name;
        this.base = base;
    }

    public double pay() {
        return base;
    }

    public String toString() {
        return name + " is paid " + pay();
    }
}

class Manager extends Employee {
    private double bonus;

    public Manager( String name, double base, double bonus ) {
        super(name, base);
        this.bonus = bonus;
    }

    public double pay( double months ) {
        return (base + bonus) * months;
    }
}
