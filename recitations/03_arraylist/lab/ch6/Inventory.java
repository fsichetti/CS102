/*  ch6 - Inventory
 *
 * Keeps the list of items in stock and checks whether the item a customer
 * asks for is available.
 *
 * Read the documentation of contains: how does it decide that an element
 * of the list "is" the request?
 *
 * You may edit: the class Item (you may add methods).
 * Do not edit:  main.
 */
import java.util.ArrayList;

class Item {
    String name;
    String size;

    Item( String name, String size ) {
        this.name = name;
        this.size = size;
    }
}

public class Inventory {
    public static void main( String [] args ) {
        ArrayList<Item> stock = new ArrayList<>();
        stock.add(new Item("shirt", "M"));
        stock.add(new Item("shirt", "L"));
        stock.add(new Item("jacket", "S"));

        Item request = new Item("shirt", "L");

        if ( stock.contains(request) )
            System.out.println("in stock");
        else
            System.out.println("not in stock");
    }
}
