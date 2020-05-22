package packagedelivery;

public abstract class StationMode {
	protected Station station;

	public abstract Route findNewRoute(Station destiny);

	public abstract void receivePackage(PackBox b, Route r);

	public abstract void sendPackage(PackBox pack, Vehicle vehicle);

	public boolean satisfiesHeuristic(Route r, Station destiny) {
		return false;
	}

}