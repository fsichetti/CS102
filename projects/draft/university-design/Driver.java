/*  Do not modify this file.
 *  It is the program that will use the classes you design.
 *  Your job is to make it compile and produce sensible output.
 */
public class Driver {
    public static void main( String [] args ) {
        University u = new University("New York University");

        u.addCourse("CS101", "Intro to Programming");
        u.addCourse("CS102", "Data Structures");

        u.addStudent("s01", "Alice Nguyen", 1);
        u.addStudent("s02", "Bob Okonkwo", 2);
        u.addGradStudent("g01", "Cara Silva", "Prof. Diallo");
        u.addInstructor("i01", "Prof. Diallo", 9000);

        u.enroll("s01", "CS101");
        u.enroll("s02", "CS101");
        u.enroll("g01", "CS102");

        u.assignInstructor("i01", "CS101");
        u.makeTA("g01", "CS101");

        System.out.println("--- descriptions ---");
        System.out.println(u.describe("s01"));
        System.out.println(u.describe("g01"));
        System.out.println(u.describe("i01"));

        System.out.println("--- CS101 ---");
        System.out.println("enrolled: " + u.rosterOf("CS101"));
        System.out.println("taught by: " + u.teachingStaffOf("CS101"));

        System.out.println("--- CS102 ---");
        System.out.println("enrolled: " + u.rosterOf("CS102"));

        System.out.printf("monthly payroll: %.2f%n", u.monthlyPayroll());
    }
}
