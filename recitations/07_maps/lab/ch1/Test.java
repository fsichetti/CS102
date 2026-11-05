// This class holds your test cases for expand. Compile and run it on its own:
//
//     javac Test.java
//     java Test
//
// Add one line to main for every case you want to check. The check method at
// the bottom runs your solution and reports what it got against what you said
// to expect; you do not need to change it.

public class Test {

    public static void main(String [] args) {
        // A full set of test cases, written out for you as an example.
        // All of them use the abbreviations listed in Main.abbreviations().

        check( "brb",           "be right back" );
        check( "ok brb",        "ok be right back" );
        check( "idk tbh",       "I don't know to be honest" );
        check( "see u later",   "see you later" );
        check( "brb brb",       "be right back be right back" );
        check( "",              "" );
        check( "hello there",   "hello there" );
        check( "BRB",           "BRB" );            // keys are case sensitive
        check( "brbx",          "brbx" );           // whole words only
        check( "u",             "you" );
    }

    public static void check(String message, String expected) {
        String actual = Main.expand(message, Main.abbreviations());
        String call = "expand(\"" + message + "\")";
        System.out.printf("%-4s  %-24s expected \"%s\", got \"%s\"\n",
                (expected.equals(actual) ? "PASS" : "FAIL"), call, expected, actual);
    }

}
