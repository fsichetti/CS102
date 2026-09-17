public class Instructor extends Person implements Paid {
    private double salary;
    public Instructor ( String name, String id, double salary ) {
        super(name, id);
        this.salary = salary;
    }
    @Override
    public String describe () { return name + " (" + id + "), instructor"; }
    public double monthlyPay () { return salary; }
}
