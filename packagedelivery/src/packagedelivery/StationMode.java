package packagedelivery;

public abstract class StationMode {
	protected Station station;

	public abstract Route findNewRoute(Station destiny);

	public abstract void receivePackage(PackBox b, Route r);

}