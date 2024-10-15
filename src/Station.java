import java.util.ArrayList;
import java.util.HashSet;

/**
 * Class for representing an individual station on a factory floor.
 */
public class Station {

    ArrayList<Coordinate> coordinates;
    int type;

    Station(int type) {
        this.type = type;
        coordinates = new ArrayList<>();
    }

    void addCoordinate(Coordinate c) {
        coordinates.add(c);
    }

    void remove(Coordinate c) {
        coordinates.remove(c);
    }

    int getType() {
        return type;
    }

    void setType() {

    }

}
