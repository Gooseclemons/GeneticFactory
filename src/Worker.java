import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Worker extends Thread {

    Factory pop;
    // Variables for defining the max number of generations and fitness before terminating
    int nGen;
    double nFitness;
    // Variables for defining the populations current generation and highest fitness score
    AtomicInteger generationCount;
    double fitnessCount;

    Worker(Factory pop, int nGen, double nFitness) {
        this.pop = pop;
        this.nGen = nGen;
        this.nFitness = nFitness;

        generationCount = pop.generationCount;
        fitnessCount = pop.getFitnessCount();
    }

    /**
     * Overrides the run() method for threads allowing custom behavior to be specified.
     * Thread (Worker) should run until either nGen or nFitness is met while every Worker should synchronize at some
     * point between generations
     */
    @Override
    public void run() {
        int length = pop.num_floors;
        int pos = ThreadLocalRandom.current().nextInt(0, length); // Random value from 0 - length-1
        while (fitnessCount < nFitness && generationCount.get() < nGen) { // While the simulation parameters haven't been exceeded...
            int expectedGeneration = pop.getGenerationCount(); // Get the current generation count for CAS later

            Floor floor = pop.floors[pos];
            AtomicBoolean busy = floor.busy; // AtomicBoolean for blocking access to a subpop
            if (!busy.get() && busy.compareAndSet(false, true)) { // If the subpop isn't claimed by a thread already claim it and do stuff
                pop.compareFloor(floor); // Compares floor in possession to bestFloors to replace if better
                try {
                    pop.cyclicBarrierWait(); // Waits to sync with other threads
                    if (!pop.isBestFloor(floor)) { // If not doing selection aka nothing
                        floor.doCrossover(); // Crossover
                        floor.doMutation();
                    }
                    floor.calcFitness(); // Recalculate the fitness of the floor after the operation was performed
                    busy.set(false); // Now that the operation has been done, release the subpop so the next iteration can get one
                    pop.cyclicBarrierWait(); // Wait again at the cyclic barrier for everyone to free their subpop and do their operation
                    pos = ThreadLocalRandom.current().nextInt(0, length); // Gives a new random index for the next iteration to use

                    // Simulation termination field changes
                    pop.generationCount.compareAndSet(expectedGeneration, expectedGeneration + 1);
                    fitnessCount = pop.getFitnessCount();

                } catch (BrokenBarrierException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else if (++pos >= length) { // This case continues to increment position until the thread finds a subpop to claim
                pos = 0;
            }
        }
        // Thread done call here when nGen and nFitness are implemented
        try {
            pop.threadDone();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
