import java.util.Set;

public class Floor {

    Set<Coordinate> availableCoordinates;
    StationSet stationSet = new StationSet(48);
    int dimension;

    Floor(int dimension) {
        this.dimension = dimension;
        int x,y = 0;
        // Fills availableCoordinates set according to the given dimensions
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                availableCoordinates.add(new Coordinate(i,j));
            }
        }
    }

    boolean spotAvailable(Coordinate c) {
        if (availableCoordinates.contains(c)) { return false; }
        else { return true; }
    }

    boolean placeStation(Station s) {
        return true;
    }


}
