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
            placeStation(station);
        }
    }

    /**
     * Find a place on the floor for a station of any given type to fit and place while updating relevant fields
     * Search availableCoordinates from start to end for viable placement
     * @param station
     */
    void placeStation(Station station) {
        while (true) {
            Coordinate possibleRoot = availableCoordinates.get(ThreadLocalRandom.current().nextInt(availableCoordinates.size()));
            if (station.type == 0) { // 1X1...just place station
                availableCoordinates.addAll(station.coordinates);
                station.removeAllCoordinates();
                station.setRoot(possibleRoot);
                availableCoordinates.remove(possibleRoot);
                return;
            } else if (station.type == 1) { // 1X2...check for one adjacent available spot
                for (int x = -1; x <= 1; x += 2) { // Check for availability in the x direction
                    Coordinate pairCoordinate = new Coordinate(possibleRoot.x + x, possibleRoot.y);
                    if (availableCoordinates.contains(pairCoordinate)) {
                        availableCoordinates.addAll(station.coordinates);
                        station.removeAllCoordinates();
                        station.setRoot(possibleRoot);
                        station.addCoordinate(pairCoordinate);
                        availableCoordinates.remove(possibleRoot);
                        availableCoordinates.remove(pairCoordinate);
                        return;
                    }
                }
                for (int y = -1; y <= 1; y += 2) {
                    Coordinate pairCoordinate = new Coordinate(possibleRoot.x, possibleRoot.y + y);
                    if (availableCoordinates.contains(pairCoordinate)) {
                        availableCoordinates.addAll(station.coordinates);
                        station.removeAllCoordinates();
                        station.setRoot(possibleRoot);
                        station.addCoordinate(pairCoordinate);
                        availableCoordinates.remove(possibleRoot);
                        availableCoordinates.remove(pairCoordinate);
                        return;
                    }
                }
            } else if (station.type == 2) { // 2X2...check for square region

            }
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
                Coordinate root1 = station1.root;
                Coordinate root2 = station2.root;
                // Now use built in method from Coordinate class to get the distance between points
                double distance = root1.distance(root2);
                if (distance == 0) {
                    System.out.println();
                }
                double affinity = affinityMatrix[station1.type][station2.type]; // Types are zero based
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
            // if (number == 0) { shift; } if (number == 1) { relocate; }, if one method fails try the other
            placeStation(station);
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
