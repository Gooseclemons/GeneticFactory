import java.util.ArrayList;
import java.util.HashSet;

/**
 * Class for representing an individual station on a factory floor.
 */
public class Station {

    ArrayList<Coordinate> coordinates;
    Coordinate root;
    int type;

    /**
     * General constructor
     * @param type
     */
    Station(int type) {
        this.type = type;
        coordinates = new ArrayList<>();
    }

    /**
     * Add a coordinate as a root coordinate as well as add the coordinate to the coordinates array
     * @param root
     */
    void setRoot(Coordinate root) {
        this.root = root;
        addCoordinate(root);
    }

    void addCoordinate(Coordinate c) {
        coordinates.add(c);
    }

    void removeCoordinate(Coordinate c) {
        coordinates.remove(c);
    }

    void removeAllCoordinates() {
        coordinates.clear();
    }

    int getType() {
        return type;
    }

    void setType() {

    }

}
