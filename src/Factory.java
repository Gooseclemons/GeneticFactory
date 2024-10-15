import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadLocalRandom;

public class Factory {

    final Worker[] threads;
    final Floor[] floors;
    double[][] affinityMatrix;
    Floor[] bestFloors;

    final int num_threads;
    final int num_floors;
    final int dimension;

    int generationCount = 0;
    double fitnessCount = 0;

    // Barriers for running
    final CountDownLatch done; // Used for end of thread lifecycle
    CyclicBarrier barrier; // Barrier used for syncing between generations

    StationSet stationSet;

    Factory(int num_threads, int num_stations, int nGen, double nFitness, int nTypes) {
        // The number of floors (Subpops) is the same as the number of threads
        this.num_threads = num_threads;
        this.num_floors = num_threads;
        // Initialize barriers
        done = new CountDownLatch(num_threads);
        barrier = new CyclicBarrier(num_threads, this::resetBarrier); // Supply action to cyclic barrier to reset itself upon continuation?

        this.stationSet = new StationSet(num_stations, nTypes);
        // Formula for calculating required dimensions of floors for all stations to fit
        dimension = (int) Math.sqrt(stationSet.totalSpots) + 1;
        // Initializes the affinity matrix
        createAffinityMatrix(nTypes);

        threads = new Worker[num_threads];
        for (int i = 0; i < num_threads; i++) {
            threads[i] = new Worker(this, nGen, nFitness);
        }

        floors = new Floor[num_floors];
        for (int i = 0; i < num_floors; i++) {
            floors[i] = new Floor(stationSet, dimension, affinityMatrix);
        }

        // After creating all Floors/Subpops, initialize the bestFloors field and call method to populate
        bestFloors = new Floor[num_floors / 8]; // Set to save an eighth of the total floors as the best
        initialBestFloors();

    }

    void createAffinityMatrix(int nTypes) {
        affinityMatrix = new double[nTypes][nTypes]; // Creates an array based off of the number of types present
        for (int i = 0; i < nTypes; i++) {
            for (int j = 0; j < nTypes; j++) {
                if (i == j) { affinityMatrix[i][j] = 1; } // Same types have a default affinity of 1 with each other
                else {
                    affinityMatrix[i][j] = ThreadLocalRandom.current().nextDouble(1) + 1;
                    affinityMatrix[j][i] = affinityMatrix[i][j]; // Affinity matrix is symmetrical via this assignment
                }
            }
        }
    }

    /**
     * Populates the bestFloors array upon class initialization.
     * Will automatically sort the bestFloors array with the higher the index the better the Floor
     * Same insertion sort algorithm acan be used for the runtime version of checkBestFloors()
     */
    void initialBestFloors() {
        int length = num_floors / 8; // Length of bestFloors array
        for (Floor floor : floors) {
            Floor challengerFloor = floor;
            for (int i = length - 1; i >= 0; i--) {
                if (bestFloors[i] == null) { bestFloors[i] = challengerFloor; } // If current index is unoccupied, fill in by default
                else if ((bestFloors[i] != null) && (challengerFloor.fitness > bestFloors[i].fitness)) { // If tempFloor has a better fitness value than current spot holder, replace
                    Floor tempFloor = bestFloors[i];
                    bestFloors[i] = challengerFloor;
                    challengerFloor = tempFloor;
                }
            }
        }
    }

    /**
     * Synchronized method so only one thread can edit bestFloors array at one time
     * Assumes the bestFloors array is full since initializeBestFloors() populates the bestFloors array on Factory init
     * @param challengerFloor
     */
    synchronized void compareFloor(Floor challengerFloor) {
        int length = bestFloors.length;
        // Loop from best to worst of bestFloors array
        for (int i = length - 1; i >= 0; i--) {
            if (challengerFloor.fitness > bestFloors[i].fitness) {
                Floor tempFloor = bestFloors[i];
                bestFloors[i] = challengerFloor;
                challengerFloor = tempFloor;
            }
        }
    }

    void start() {
        for (int i = 0; i < num_threads; i++) {
            threads[i].start();
        }
    }

    void shutdown() {
        for (int i = 0; i < num_threads; i++) {
            threads[i].interrupt();
        }
    }

    void threadDone() throws InterruptedException {
        done.countDown();
    }

    void awaitDone() throws InterruptedException {
        done.await();
    }

    void cyclicBarrierWait() throws BrokenBarrierException, InterruptedException {
        barrier.await();
    }

    void resetBarrier() {
        barrier = new CyclicBarrier(num_threads, this::resetBarrier);
    }

    int getGenerationCount() {
        return generationCount;
    }

    double getFitnessCount() {
        return fitnessCount;
    }

}
