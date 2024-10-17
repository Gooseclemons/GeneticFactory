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

    /**
     * Constructor for use by Factory, Floor
     * @param num_of_stations
     * @param num_of_types
     */
    StationSet(int num_of_stations, int num_of_types) {
        this.num_of_stations = num_of_stations;
        this.num_of_types = num_of_types;
        stations = new ArrayList<>();
        initialize();
    }

    /**
     * Constructor for creating a copy of the stationSet
     * @param other
     */
    StationSet(StationSet other) {
        this(other.num_of_stations, other.num_of_types);
    }

    /**
     * Constructor for creating an empty stationSet that can then have values added to it
     * @param num_of_stations
     * @param other
     */
    StationSet(int num_of_stations, StationSet other) {
        this.num_of_stations = num_of_stations;
        num_of_types = other.num_of_types;
        stations = new ArrayList<>();
    }

    void initialize() {
        for (int i = 0; i < num_of_stations; i++) {
            int stationType = ThreadLocalRandom.current().nextInt(0, num_of_types);
            stations.add(new Station(ThreadLocalRandom.current().nextInt(0, num_of_types), i)); // Assigns type, worry about more than one type later
            if (stationType == 0) { totalSpots++; }         //1X1 case
            else if (stationType == 1) { totalSpots += 2; } //1X2 case
            else if (stationType == 2) { totalSpots += 4; } //2X2 case
        }
    }

    void incrementSpots(Station s) {
        if (s.type == 0) { totalSpots++; }         //1X1 case
        else if (s.type == 1) { totalSpots += 2; } //1X2 case
        else if (s.type == 2) { totalSpots += 4; } //2X2 case
    }

    int size() {
        return stations.size();
    }

    Station get(int idx) {
        return stations.get(idx);
    }

    void add(Station station) {
        stations.add(station);
    }

    // Debugging
    void printStations() {
        for (Station s : stations) {
            System.out.println(s.type);
        }
    }

}
