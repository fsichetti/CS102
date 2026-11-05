import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main (String [] args) {
        Map<String, String> abbreviations = abbreviations();
        System.out.println("Known abbreviations: " + abbreviations.keySet());
        System.out.println("Enter a message (words separated by single spaces): ");
        Scanner in = new Scanner(System.in);
        String message = in.nextLine();
        System.out.printf("\nexpanded:\n %s\n\n", expand(message, abbreviations));
        in.close();

        // To try many inputs at once instead of typing them in one at a time,
        // edit Test.java in this folder and run it:  javac Test.java
        //                                            java Test
    }

    public static String expand(String message, Map<String, String> abbreviations) {
        //TODO: implement this method
        //      you will need to remove or modify the return statement below
        return "";
    }

    // Provided for you: the abbreviations used by main and by Test.
    public static Map<String, String> abbreviations() {
        Map<String, String> abbr = new HashMap<>();
        abbr.put("brb", "be right back");
        abbr.put("idk", "I don't know");
        abbr.put("imo", "in my opinion");
        abbr.put("tbh", "to be honest");
        abbr.put("ttyl", "talk to you later");
        abbr.put("u", "you");
        return abbr;
    }

}
