/*  ch4 - WordBox
 *
 * Should print
 *
 *     hello
 *     42
 */
public class WordBox {
    public static void main( String [] args ) {
        Box<String> word = new Box<String>();
        word.set("hello");
        System.out.println( word.get() );

        Box<int> number = new Box<int>();
        number.set(42);
        System.out.println( number.get() );
    }
}

class Box<T> {
    private T item;

    public void set ( T item ) { this.item = item; }
    public T get () { return item; }
}
