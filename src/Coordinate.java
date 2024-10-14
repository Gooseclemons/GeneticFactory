public class Coordinate {

    final int x,y;

    Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    boolean isSame(Coordinate c) {
        int x = c.x;
        int y = c.y;
        if ((this.x == x) && (this.y == y)) { return true; }
        else { return false; }
    }

}
