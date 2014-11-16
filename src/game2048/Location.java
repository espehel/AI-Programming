package game2048;

import java.util.List;

/**
 * Created by espen on 07/11/14.
 */
public class Location {


    private final int x;
    private final int y;
    private static List<Location> locations;

    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Location offset(Direction direction) {
        return new Location(x + direction.getX(), y + direction.getY());
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Location{" + "x=" + x + ", y=" + y + '}';
    }

    public double getLayoutY(int CELL_SIZE) {
        if (y == 0) {
            return CELL_SIZE / 2;
        }
        return (y * CELL_SIZE) + CELL_SIZE / 2;
    }

    public double getLayoutX(int CELL_SIZE) {
        if (x == 0) {
            return CELL_SIZE / 2;
        }
        return (x * CELL_SIZE) + CELL_SIZE / 2;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Location other = (Location) obj;
        if (this.x != other.x) {
            return false;
        }
        return this.y == other.y;
    }
    //used in hashmap, to make the hashmap search in right location when using this as key.
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.x;
        hash = 97 * hash + this.y;
        return hash;
    }
    public static List<Location> getLocations() {
        return locations;
    }

    public static void setLocations(List<Location> locations) {
        Location.locations = locations;
    }
}
