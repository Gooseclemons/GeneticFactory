public class Coordinate {

    final int x,y;

    Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Overrides equals method for Coordinate so calling .contains() from an ArrayList of Coordinates will return
     * our desired output if a coordinate that is the same as the input coordinate is contained within the ArrayList
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        Coordinate c = (Coordinate) obj;
        return x == c.x && y == c.y;
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    double distance(Coordinate c) {
        return Math.sqrt(Math.pow(x - c.x, 2) + Math.pow(y - c.y, 2));
    }

}
