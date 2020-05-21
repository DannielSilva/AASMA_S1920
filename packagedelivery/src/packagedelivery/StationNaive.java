package packagedelivery;

public class StationNaive extends StationMode {

	@Override
	public Route findNewRoute(Station destiny) {
		return station.randomRoute();
	}

	@Override
	public void receivePackage(PackBox b, Route r) {
		// nada
	}
}
