package distributions;

public class UniformDistribution extends Distribution {

    @Override
    public int[] generate(int size, int range) {
        int[] a = new int[size];
        for (int i = 0; i < size; i++) {
            a[i] = rng.nextInt(range);
        }
        return a;
    }

}
