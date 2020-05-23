package packagedelivery;

import packagedelivery.Route.RouteType;;

public class Vehicle extends Entity {
    private int cost;
    private RouteType rType;

    public Vehicle(int cost, RouteType rType) {
        super();
        this.cost = cost;
        this.rType = rType;
    }

    public boolean canGoThrough(Route r) {
        return r.getType().equals(this.rType);
    }

    public int getCost() {
        return cost;
    }

    public RouteType getrType() {
        return rType;
    }

    @Override
    public String toString() {
        return "[cost=" + cost + ", rType=" + rType + "]";
    }

}
