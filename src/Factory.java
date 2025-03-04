import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Factory {

    final Worker[] threads;
    final Floor[] floors;
    double[][] affinityMatrix;
    Floor[] bestFloors;

    // Syncronization Objects
    final Semaphore semaphore;
    CyclicBarrier semaphoreCoordinator;
    final Exchanger<ArrayList<Station>> exchanger;

    // UI components
    FactoryUI ui;
    Timer timer;

    final int num_threads;
    final int num_floors;
    final int dimension;

    // Simulation end parameters
    AtomicInteger generationCount = new AtomicInteger(0);
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

        // Semaphore to help coordinate which threads do crossover, reserves half the total floors for crossover
        semaphore = new Semaphore(num_floors / 2);
        semaphoreCoordinator = new CyclicBarrier(num_floors / 2, this::resetSemaphoreBarrier);
        exchanger = new Exchanger<>();

        // Initialize UI
        ui = new FactoryUI(this, Color.LIGHT_GRAY);
        timer = new Timer(500, updateDisplay());    // Update every 0.5 seconds
        timer.start();  // It might be inportant to actually start the timer if you want it to do anything lol

        this.stationSet = new StationSet(num_stations, nTypes);
        // Formula for calculating required dimensions of floors for all stations to fit, the constant is arbitrary and can be changed as long as it's >=1
        dimension = (int) Math.sqrt(stationSet.totalSpots) + 5;
        // Initializes the affinity matrix
        createAffinityMatrix(nTypes);

        threads = new Worker[num_threads];
        for (int i = 0; i < num_threads; i++) {
            threads[i] = new Worker(this, nGen, nFitness);
        }

        floors = new Floor[num_floors];
        for (int i = 0; i < num_floors; i++) {
            floors[i] = new Floor(stationSet, dimension, affinityMatrix, this);
        }

        // After creating all Floors/Subpops, initialize the bestFloors field and call method to populate
        bestFloors = new Floor[num_floors / 4]; // Set to save an eighth of the total floors as the best
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
        int length = num_floors / 4; // Length of bestFloors array
        for (Floor floor : floors) {
            Floor challengerFloor = floor;
            for (int i = length - 1; i >= 0; i--) {
                if (bestFloors[i] == null) { // If current index is unoccupied, fill in by default and break since there is no station to pass down
                    bestFloors[i] = challengerFloor;
                    break;
                }
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
     * @param floor
     */
    synchronized void compareFloor(Floor floor) {
        int length = bestFloors.length;
        // Loop from best to worst of bestFloors array

        Floor challengerFloor = floor; // Variable for storing the floor currently competing to become one of the best, initialize with the input floor and swap with replaces
        for (int i = length - 1; i >= 0; i--) {
            if (challengerFloor == bestFloors[i]) { // If floor finds itself in the bestFloors array...
                break; // Break to prevent copies
            }
            else if (challengerFloor.fitness > bestFloors[i].fitness) {
                Floor tempFloor = bestFloors[i];
                bestFloors[i] = challengerFloor;
                challengerFloor = tempFloor;
            }
        }
    }

    boolean isBestFloor(Floor floor) {
        for (Floor best : bestFloors) {
            if (best == floor) { return true; } // Found a match, you are a best floor
        }
        return false; // Did not find a match, not a best floor
    }

    /**
     * Simple method call to update the best fitness value achieved so far, call with each barrier call
     */
    void updateBestFitness() {
        fitnessCount = bestFloors[bestFloors.length - 1].fitness;
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
        System.out.println("Killing thread");
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
        updateBestFitness(); // Update the best fitness value at the end of each generation
    }

    void resetSemaphoreBarrier() {
        semaphoreCoordinator = new CyclicBarrier(num_threads / 2, this::resetSemaphoreBarrier);
    }

    ActionListener updateDisplay() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ui.updateGrid(bestFloors[bestFloors.length-1]); // Update the display with the best floor
            }
        };
    }

    int getGenerationCount() {
        return generationCount.get();
    }

    double getFitnessCount() {
        return fitnessCount;
    }

}
