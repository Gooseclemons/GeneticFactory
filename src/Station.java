import java.util.HashSet;

/**
 * Class for representing an individual station on a factory floor.
 */
public class Station {

    HashSet<Coordinate> coordinates;
    int type;

    Station(int type) {
        this.type = type;
    }

    void add(Coordinate c) {
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
