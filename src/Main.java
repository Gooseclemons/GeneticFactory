public class Main {

    /** How many stations are present in the simulation */
    final int NUM_STATIONS = 9;

    /** How many types of station there will be, differing in dimensions and affinity */
    final int NUM_TYPES = 2;

    /** How many threads the problem space will be divided amongst */
    final int NUM_THREADS = 4;

    public static void main(String[] args) {
        for (int i = 0; i < 4; i++) {
            oneRun();
        }
    }

    static void oneRun() {
        Factory factory = new Factory(4, 4);
        factory.start();

    }

}