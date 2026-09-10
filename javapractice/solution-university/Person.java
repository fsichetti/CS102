public abstract class Person {
    protected String name;
    protected String id;
    public Person ( String name, String id ) { this.name = name; this.id = id; }
    public abstract String describe ();
}
