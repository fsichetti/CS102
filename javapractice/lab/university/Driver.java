/*  Do not modify this file. It is the program that uses the classes you write.
 *
 *  It will not compile until those classes exist. That is expected: compile it
 *  now and read the error list as a to-do list.
 */
public class Driver {
    public static void main( String [] args ) {
        GradStudent cara   = new GradStudent("Cara Silva", "g01", 1, "Prof. Diallo");
        Instructor  diallo = new Instructor("Prof. Diallo", "i01", 9000);

        Roster<Student> students = new Roster<Student>();
        students.add( new Student("Alice Nguyen", "s01", 1) );
        students.add( new Student("Bob Okonkwo", "s02", 2) );
        students.add( cara );

        Roster<Instructor> staff = new Roster<Instructor>();
        staff.add( diallo );

        System.out.println("-- students --");
        for ( int i = 0; i < students.size(); i++ )
            System.out.println( students.get(i).describe() );

        System.out.println("-- staff --");
        for ( int i = 0; i < staff.size(); i++ )
            System.out.println( staff.get(i).describe() );

        Paid [] payroll = { cara, diallo };
        double total = 0;
        for ( int i = 0; i < payroll.length; i++ )
            total += payroll[i].monthlyPay();
        System.out.println("monthly payroll: " + total);
    }
}
