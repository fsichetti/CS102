/*  ch5 - AccountSummary
 *
 * Should print
 *
 *     savings 1234
 *     balance 250.0
 */
public class AccountSummary {
    public static void main( String [] args ) {
        SavingsAccount a = new SavingsAccount(250);
        System.out.println(a);
        System.out.println("balance " + a.getBalance());
    }
}

class Account {
    protected String number;
    protected String kind;

    public Account( String number, String kind ) {
        this.number = number;
        this.kind = kind;
    }

    public Account( String number ) {
        this(number, "checking");
    }

    public String toString() {
        return kind + " " + number;
    }
}

class SavingsAccount extends Account {
    private double balance;

    public SavingsAccount( double b ) {
        balance = b;
    }

    public double getBalance() {
        return balance;
    }
}
