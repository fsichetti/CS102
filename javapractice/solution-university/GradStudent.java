public class GradStudent extends Student implements Paid {
    private Instructor advisor;
    public GradStudent ( String name, String id, int year, Instructor advisor ) {
        super(name, id, year);
        this.advisor = advisor;
    }
    @Override
    public String describe () {
        return name + " (" + id + "), graduate student in year " + year + " advised by " + advisor.getName();
    }
    public double monthlyPay () { return 2000; }
}
