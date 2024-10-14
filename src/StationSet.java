import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * StationSet is a class used for record keeping as a set of all stations used in the simulations.
 * An instance of StationSet must be kept in the Factory class to represent a coordinateless StationSet that is then
 * passed down unto subpop Floor instances for them to set unique coordinates for each station in their instance.
 */
public class StationSet {

    Set<Station> stations;
    int num_of_stations = 0;
    /*
     * Starts at zero, as stations are added to StationSet increment count based on number of spots the station occupies
     */
    int totalSpots;

    StationSet(int num_of_stations) {
        this.num_of_stations = num_of_stations;
        stations = new HashSet<>();
        initialize();
    }

    void initialize() {
        for (int i = 0; i < num_of_stations; i++) {
            stations.add(new Station(ThreadLocalRandom.current().nextInt(0, 3)));
        }
    }

    void printStations() {
        for (Station s : stations) {
            System.out.println(s.type);
        }
    }

}
