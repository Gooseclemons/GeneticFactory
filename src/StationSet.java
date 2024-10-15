import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * StationSet is a class used for record keeping as a set of all stations used in the simulations.
 * An instance of StationSet must be kept in the Factory class to represent a coordinate-less StationSet that is then
 * passed down unto subpop Floor instances for them to set unique coordinates for each station in their instance.
 */
public class StationSet {

    ArrayList<Station> stations;
    final int num_of_stations;
    final int num_of_types;
    /** Represents the total spots required to place all stations in StationSet, helps with creating floors */
    int totalSpots;

    StationSet(int num_of_stations, int num_of_types) {
        this.num_of_stations = num_of_stations;
        this.num_of_types = num_of_types;
        stations = new ArrayList<>();
        initialize();
    }

    StationSet(StationSet other) {
        this(other.num_of_stations, other.num_of_types);
    }

    void initialize() {
        for (int i = 0; i < num_of_stations; i++) {
            stations.add(new Station(ThreadLocalRandom.current().nextInt(1, num_of_types + 1))); // Assigns type, worry about more than one type later
            totalSpots += 1; // Assumes stations of 1X1 proportion, add mre type functionality later
        }
    }

    int size() {
        return stations.size();
    }

    Station get(int idx) {
        return stations.get(idx);
    }

    // Debugging
    void printStations() {
        for (Station s : stations) {
            System.out.println(s.type);
        }
    }

}
