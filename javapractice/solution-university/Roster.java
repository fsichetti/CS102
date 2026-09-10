import java.util.ArrayList;

public class Roster<T> {
    private ArrayList<T> members = new ArrayList<T>();

    public void add ( T member ) { members.add(member); }
    public T get ( int i ) { return members.get(i); }
    public int size () { return members.size(); }
}
