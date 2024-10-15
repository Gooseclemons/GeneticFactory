import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Floor {

    ArrayList<Coordinate> availableCoordinates;
    StationSet stationSet;
    final int dimension;
    final double[][] affinityMatrix;
    double fitness;

    Floor(StationSet stationSet, int dimension, double[][] affinityMatrix) {
        this.stationSet = new StationSet(stationSet);
        this.dimension = dimension;
        this.affinityMatrix = affinityMatrix;
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
        for (int i = 0; i < stationSet.size(); i++) {
            for (int j = i + 1; j < stationSet.size(); j++) {
                // Gets associated stations from StationSet arraylist
                Station station1 = stationSet.get(i);
                Station station2 = stationSet.get(j);
                // Gets the root coordinate for given stations, when multiple types are introduced add a root coordinate to station class so the below code can be simplified
                Coordinate root1 = station1.coordinates.get(0);
                Coordinate root2 = station2.coordinates.get(0);
                // Now use built in method from Coordinate class to get the distance between points
                double distance = root1.distance(root2);
                double affinity = affinityMatrix[station1.type - 1][station2.type - 1]; // Might need to minus 1 the types, check if issues
                fitness += (1 / distance) * affinity; // Taking the reciprocal of distance punishes further distances while rewarding shorter distances
            }
        }
    }

    /**
     * For debugging individual floors
     */
    void printFloor() {
        for (Coordinate coordinate : availableCoordinates) {
            System.out.print(coordinate.toString() + " ");
        }
        System.out.println();
    }

}
