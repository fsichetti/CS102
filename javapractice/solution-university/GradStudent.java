public class GradStudent extends Student implements Paid {
    private String advisor;
    public GradStudent ( String name, String id, int year, String advisor ) {
        super(name, id, year);
        this.advisor = advisor;
    }
    @Override
    public String describe () {
        return name + " (" + id + "), graduate student in year " + year + " advised by " + advisor;
    }
    public double monthlyPay () { return 2000; }
}
