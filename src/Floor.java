import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

public class Floor {

    ArrayList<Coordinate> availableCoordinates;
    StationSet stationSet;
    final int dimension;
    final double[][] affinityMatrix;
    double fitness;

    // Syncronization objects
    AtomicBoolean busy; // Used to limit thread access to only one thread
    final Exchanger<ArrayList<Station>> exchanger;

    /**
     * General floor constructor for general use
     * @param stationSet
     * @param dimension
     * @param affinityMatrix
     * @param pop
     */
    Floor(StationSet stationSet, int dimension, double[][] affinityMatrix, Factory pop) {
        this.stationSet = new StationSet(stationSet);
        this.dimension = dimension;
        this.affinityMatrix = affinityMatrix;

        this.busy = new AtomicBoolean(false); // False if not claimed by thread, true if claimed
        this.exchanger = pop.exchanger;

        availableCoordinates = createAvailableCoordinates();
        initializeFloor(); // Initializes the stations in the floor
        calcFitness(); // Automatically calculates the fitness for the initial solution
    }

    /**
     * Empty floor constructor for use in doCrossover
     * exchanger can be null since no need for exchanging, template will be discarded
     */
    Floor(StationSet stationSet, int num_stations, int dimension, double[][] affinityMatrix) {
        this.stationSet = new StationSet(num_stations, stationSet);
        this.dimension = dimension;
        this.affinityMatrix = affinityMatrix;

        this.busy = new AtomicBoolean(false);
        this.exchanger = null;

        availableCoordinates = createAvailableCoordinates(); // Initialize full available coordinates
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
     * More accurately should be used strictly for relocating stations
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
                    System.out.println("Fat nuts");
                }
                double affinity = affinityMatrix[station1.type][station2.type]; // Types are zero based
                fitness += (1 / distance) * affinity; // Taking the reciprocal of distance punishes further distances while rewarding shorter distances
            }
        }
    }

    void doMutation() {
        int numMutated = ThreadLocalRandom.current().nextInt(1, stationSet.size()); // Gets a random number from 1 to a quarter of the total number of stations
//        StationSet mutationSet = new StationSet(numMutated, stationSet); // Creates a temporary stationSet to hold the stations we want to mutate
//        for (int i = 0; i < numMutated; i++) {
//            mutationSet.add(stationSet.get(i));
//        }
//
//        for (Station station : mutationSet.stations) { // Now iterate through mutationSet while deciding which operation to perform based off of a random number perhaps
//            // if (number == 0) { shift; } if (number == 1) { relocate; }, if one method fails try the other
//            placeStation(station);
//        }
        Station mutatedStation = stationSet.get(numMutated);
        Coordinate root = mutatedStation.root;
        //Try shifting
        if (mutatedStation.type == 0) {
            for (int x = -1; x <= 1; x += 2) {
                if (availableCoordinates.contains(new Coordinate(root.x + x, root.y))) {
                    availableCoordinates.addAll(mutatedStation.coordinates);
                    mutatedStation.removeAllCoordinates();
                    mutatedStation.setRoot(root);
                    availableCoordinates.remove(root);
                    break;
                }
            }
            for (int y = -1; y <= 1; y += 2) {
                if (availableCoordinates.contains(new Coordinate(root.x, root.y + y))) {
                    availableCoordinates.addAll(mutatedStation.coordinates);
                    mutatedStation.removeAllCoordinates();
                    mutatedStation.setRoot(root);
                    availableCoordinates.remove(root);
                    break;
                }
            }
        }
        else if (mutatedStation.type == 1) {
            placeStation(mutatedStation);
        }
    }

    /**
     * Selects a cluster of stations to offer to an exchanger, meets an exchanger and swaps station clusters,
     * creates a new custom stationSet, pastes the received cluster into the stationset and tries to fill in the
     * missing stations from the floor calling this method.
     * Keep track of which stations have been tracked by adding a station id?
     */
    void doCrossover() throws InterruptedException {
        // Choose stations that will be passed to another thread
        ArrayList<Station> crossoverStations = new ArrayList<>();
        int startIdx = ThreadLocalRandom.current().nextInt(0, stationSet.size() - 2); // -2 prevents indexes from acting weird
        for (int i = startIdx; i < stationSet.size(); i++) {
            crossoverStations.add(stationSet.get(i));
        }

        // Offer chosen stations to the exchanger
        exchanger.exchange(crossoverStations);

        // Create a container floor to act as the new floor template
        Floor childFloor = new Floor(stationSet, stationSet.num_of_stations, dimension, affinityMatrix);

        // Populate the childFloor
        for (Station received : crossoverStations) { // Add all the received floors first to guarantee crossover of data
            childFloor.availableCoordinates.removeAll(received.coordinates);
            childFloor.stationSet.add(received);
        }
        for (Station current : stationSet.stations) { // Add all of the current/parent stations that don't share an id with the received
            if ((!containsID(crossoverStations, current.id) && childFloor.coordinatesAvailable(current))) { // Checks for ids not matching and that space is available
                childFloor.availableCoordinates.removeAll(current.coordinates);
                childFloor.stationSet.add(current);
            } else if (!containsID(crossoverStations, current.id)) { // Places station randomly while overriding station data
                current.removeAllCoordinates();
                while (true) {
                    System.out.println("Waiting in the while");
                    Coordinate possibleRoot = childFloor.availableCoordinates.get(ThreadLocalRandom.current().nextInt(childFloor.availableCoordinates.size()));
                    if (current.type == 0) {
                        current.setRoot(possibleRoot);
                        childFloor.availableCoordinates.remove(possibleRoot);
                        childFloor.stationSet.add(current);
                        break;
                    }
                    else if (current.type == 1) {
                        boolean placed = false;
                        current.removeAllCoordinates();
                        for (int x = -1; x <= 1; x += 2) {
                            Coordinate pairCoordinate = new Coordinate(possibleRoot.x + x, possibleRoot.y);
                            if (childFloor.availableCoordinates.contains(pairCoordinate)) {
                                current.setRoot(possibleRoot);
                                current.addCoordinate(pairCoordinate);
                                childFloor.availableCoordinates.remove(pairCoordinate);
                                childFloor.availableCoordinates.remove(possibleRoot);
                                childFloor.stationSet.add(current);
                                placed = true;
                                break;
                            }
                        }
                        for (int y = -1; y <= 1; y += 2) {
                            Coordinate pairCoordinate = new Coordinate(possibleRoot.x, possibleRoot.y + y);
                            if (childFloor.availableCoordinates.contains(pairCoordinate)) {
                                current.setRoot(possibleRoot);
                                current.addCoordinate(pairCoordinate);
                                childFloor.availableCoordinates.remove(pairCoordinate);
                                childFloor.availableCoordinates.remove(possibleRoot);
                                childFloor.stationSet.add(current);
                                placed = true;
                                break;
                            }
                        }
                        if (placed) { break; }
                    }
                }
            }
        }

        // Set current floors stationSet and availableCoordinates to the template floors coords
        stationSet = childFloor.stationSet;
        availableCoordinates = childFloor.availableCoordinates;

    }

    /**
     * Helper method for the doCrossover() method.
     * Takes an arraylist of stations and determines if a station with the given id is contained within
     * @param stations
     * @param id
     * @return
     */
    boolean containsID(ArrayList<Station> stations, int id) {
        for (Station station : stations) {
            if (station.id == id) { return true; }
        }
        return false;
    }

    ArrayList<Coordinate> createAvailableCoordinates() {
        // Fills availableCoordinates set according to the given dimensions from (0,0) to (dimension - 1, dimension - 1)
        ArrayList<Coordinate> tempCoordinates = new ArrayList<>();
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                tempCoordinates.add(new Coordinate(i,j));
            }
        }
        return tempCoordinates;
    }

    /**
     * Check that all the coordinates present in station.coordinates are in available coordinates.
     * Best used for checking before placing
     * @param s
     * @return
     */
    boolean coordinatesAvailable(Station s) {
        for (Coordinate coordinate : s.coordinates) {
            if (!availableCoordinates.contains(coordinate)) { return false; }
        }
        return true;
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
