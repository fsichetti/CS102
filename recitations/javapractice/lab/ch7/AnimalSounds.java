/*  ch7 - AnimalSounds
 *
 * Each animal reports the sound it makes.
 *
 * Both printed lines are wrong, for the same reason. The reason is not that
 * speak() is broken.
 */
public class AnimalSounds {
    public static void main( String [] args ) {
        Animal a = new Dog();
        System.out.println(a.sound);
        System.out.println(a.speak());
    }
}

class Animal {
    protected String sound = "nothing";

    public String speak() {
        return "I say " + sound;
    }
}

class Dog extends Animal {
    protected String sound = "woof";
}
