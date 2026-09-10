/*  Do not modify this file. It is the program that uses the classes you write.
 *
 *  It will not compile until those classes exist. That is expected: compile it
 *  now and read the error list as a to-do list.
 */
public class Driver {
    public static void main( String [] args ) {
        Instructor  prof        = new Instructor("Prof. Courant", "i01", 9000);
        GradStudent gradstudent = new GradStudent("Ada Lovelace", "g01", 1, prof);

        Roster<Student> students = new Roster<Student>();
        students.add( new Student("Emmy Noether", "s01", 1) );
        students.add( new Student("Alan Turing", "s02", 2) );
        students.add( new Student("Srinivasa Ramanujan", "s03", 1) );
        students.add( gradstudent );

        Roster<Instructor> staff = new Roster<Instructor>();
        staff.add( prof );

        System.out.println("-- students --");
        for ( int i = 0; i < students.size(); i++ )
            System.out.println( students.get(i).describe() );

        System.out.println("-- staff --");
        for ( int i = 0; i < staff.size(); i++ )
            System.out.println( staff.get(i).describe() );

        Paid [] payroll = { gradstudent, prof };
        double total = 0;
        for ( int i = 0; i < payroll.length; i++ )
            total += payroll[i].monthlyPay();
        System.out.println("monthly payroll: " + total);
    }
}
