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
}
