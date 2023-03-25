public class RNG {
    private long seed;

    public RNG(long seed) {
        this.seed = seed;
    }

    public double next() {
        long x = seed;
        x ^= (x << 21);
        x ^= (x >>> 35);
        x ^= (x << 4);
        seed = x;
        return Math.abs((double) x / Long.MAX_VALUE);
    }
}