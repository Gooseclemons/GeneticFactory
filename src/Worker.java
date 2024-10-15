public class Worker extends Thread {

    Factory pop;
    // Variables for defining the max number of generations and fitness before terminating
    int nGen;
    double nFitness;
    // Variables for defining the populations current generation and highest fitness score
    int generationCount;
    double fitnessCount;

    Worker(Factory pop, int nGen, double nFitness) {
        this.pop = pop;
        this.nGen = nGen;
        this.nFitness = nFitness;
    }

    /**
     * Overrides the run() method for threads allowing custom behavior to be specified.
     * Thread (Worker) should run until either nGen or nFitness is met while every Worker should synchronize at some
     * point between generations
     */
    @Override
    public void run() {
        // While the max generation or fitness count haven't been met, checks before loop execution to prevent overrunning by one generation
        while ((pop.getGenerationCount() < nGen) && (pop.getFitnessCount() < nFitness)) {
            // get a subpop
            // compare subpop to current best subpops
            // if you contain a best solution, switch current subpop into the Factory class best subpops

        }
    }

}
