package packagedelivery;

public class Route implements Comparable<Route> {
    public enum RouteType {
        LAND, SEA, AIR
    }

    private Station one;
    private Station two;
    private RouteType type;
    private int cost = 100;

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

    public Station getOther(Station s) {
        return s.getStationId() == one.getStationId() ? two : one;
    }

    public void sendVehicleFrom(Vehicle vehicle, Station from) {
        Station s = getOther(from);
        s.receiveVehicle(vehicle, this);
    }

    public void sendPackageFrom(PackBox pack, Station from) {
        Station s = getOther(from);
        s.receivePackage(pack, this);
    }

    @Override
    public String toString() {
        return "Route [connects " + one + "and " + two + ", type=" + type + "]";
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int cost() {
        return this.cost;
    }

    @Override
    public int compareTo(Route arg0) {
        String s1 = one.getStationId() + "-" + two.getStationId();
        String s2 = arg0.getStationOne().getStationId() + "-" + arg0.getStationTwo().getStationId();

        return s1.compareTo(s2);
    }
}
