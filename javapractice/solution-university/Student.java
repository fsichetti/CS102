public class Student extends Person {
    protected int year;
    public Student ( String name, String id, int year ) {
        super(name, id);
        this.year = year;
    }
    @Override
    public String describe () { return name + " (" + id + "), undergraduate year " + year; }
}
