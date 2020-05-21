package packagedelivery;

public class StationNaive extends StationMode {

	@Override
	public Route findNewRoute() {
		return station.randomRoute();
	}

	@Override
	public void receiveVehicle(Vehicle v, Route r) {

	}

	@Override
	public void receivePackage(PackBox b, Route r) {

	}
}
