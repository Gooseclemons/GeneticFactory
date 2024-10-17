public class GeneticFactory {

    /** How many stations are present in the simulation */
    static final int NUM_STATIONS = 48;

    /** How many types of station there will be, differing in dimensions, limited to 3 max and at least 1 */
    static final int NUM_TYPES = 2;

    /** How many threads the problem space will be divided amongst */
    static final int NUM_THREADS = 32;

    /** User defined desired number of generations to run for the simulation */
    static final int nGen = 1000;

    /** User defined desired fitness score for the simulation */
    static final double nFitness = 500;

    public static void main(String[] args) {

        Factory factory = new Factory(NUM_THREADS, NUM_STATIONS, nGen, nFitness, NUM_TYPES);
        factory.start();

    }

}