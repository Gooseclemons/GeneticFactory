import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

public class Floor {

    ArrayList<Coordinate> availableCoordinates;
    StationSet stationSet;
    final int dimension;
    final double[][] affinityMatrix;
    double fitness;
    AtomicBoolean busy; // Used to limit thread access to only one thread

    Floor(StationSet stationSet, int dimension, double[][] affinityMatrix) {
        this.stationSet = new StationSet(stationSet);
        this.dimension = dimension;
        this.affinityMatrix = affinityMatrix;
        this.busy = new AtomicBoolean(false); // False if not claimed by thread, true if claimed

        availableCoordinates = new ArrayList<>();
        // Fills availableCoordinates set according to the given dimensions from (0,0) to (dimension - 1, dimension - 1)
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                availableCoordinates.add(new Coordinate(i,j));
            }
        }
        initializeFloor(); // Initializes the stations in the floor
        calcFitness(); // Automatically calculates the fitness for the initial solution
    }

    /**
     * For each station in StationSet, add a coordinate to each station to "place" them into the floor and remove
     * the coordinate(s) from available coordinates
     */
    private void initializeFloor() {
        for (Station station : stationSet.stations) {
            int random = ThreadLocalRandom.current().nextInt(0, availableCoordinates.size()); // Gets a random coordinate from availableCoordinates
            station.addCoordinate(availableCoordinates.get(random));
            availableCoordinates.remove(random);
        }
    }

    void calcFitness() {
        fitness = 0; // Default fitness to 0 before starting calculations
        for (int i = 0; i < stationSet.size() - 1; i++) {
            for (int j = i + 1; j < stationSet.size(); j++) {
                // Gets associated stations from StationSet arraylist
                Station station1 = stationSet.get(i);
                Station station2 = stationSet.get(j);
                // Gets the root coordinate for given stations, when multiple types are introduced add a root coordinate to station class so the below code can be simplified
                Coordinate root1 = station1.coordinates.getFirst();
                Coordinate root2 = station2.coordinates.getFirst();
                // Now use built in method from Coordinate class to get the distance between points
                double distance = root1.distance(root2);
                double affinity = affinityMatrix[station1.type - 1][station2.type - 1]; // Might need to minus 1 the types, check if issues
                fitness += (1 / distance) * affinity; // Taking the reciprocal of distance punishes further distances while rewarding shorter distances
            }
        }
    }

    void doMutation() {
        int numMutated = ThreadLocalRandom.current().nextInt(1, (stationSet.size() / 4)); // Gets a random number from 1 to a quarter of the total number of stations
        StationSet mutationSet = new StationSet(numMutated, stationSet); // Creates a temporary stationSet to hold the stations we want to mutate
        for (int i = 0; i < numMutated; i++) {
            mutationSet.add(stationSet.get(i));
        }

        for (Station station : mutationSet.stations) { // Now iterate through mutationSet while deciding which operation to perform based off of a random number perhaps
            // Random number
            // if (number == 0) { shift; } if (number == 1) { relocate; }, if one method fails try the other
            // Get a random coordinate from available coordinate
            Coordinate newCoordinate = availableCoordinates.get(ThreadLocalRandom.current().nextInt(0, availableCoordinates.size())); // Need to change this to an arraylist when multiple types added
            if (newCoordinate == null) { return; } // If there are no available coordinates
            ArrayList<Coordinate> oldCoordinates = (ArrayList<Coordinate>) station.coordinates.clone();
            for (Coordinate coordinate : oldCoordinates) {
                station.removeCoordinate(coordinate);   // Removes all the coordinates from stations old position...
            }
            station.addCoordinate(newCoordinate); // ...and adds the new coordinate
        }
    }

    /**
     * For debugging individual floors
     */
    void printFloor() {
        String megaString = "";
        for (Coordinate coordinate : availableCoordinates) {
            megaString = megaString.concat(coordinate.toString() + " ");
        }
        System.out.println(megaString);
    }

}
