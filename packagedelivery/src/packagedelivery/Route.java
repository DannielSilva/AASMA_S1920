package packagedelivery;

public class Route {
    public enum RouteType {
        LAND, SEA, AIR
    }

    private Station one;
    private Station two;
    private RouteType type;

    public Route(Station one, Station two, RouteType type) {
        this.one = one;
        this.two = two;
        this.type = type;
    }

    public Station getStationOne() {
        return one;
    }

    public Station getStationTwo() {
        return two;
    }

    public RouteType getType() {
        return type;
    }

    public boolean containsStation(Station s) {
        return s.getId() == one.getId() || s.getId() == two.getId();
    }
}
