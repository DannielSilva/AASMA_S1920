package packagedelivery;

public abstract class StationMode {
	protected Station station;

	public abstract Route findNewRoute();

	public abstract void receiveVehicle(Vehicle v, Route r);

	public abstract void receivePackage(PackBox b, Route r);

}