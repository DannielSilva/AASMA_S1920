package packagedelivery;

public class StationComunication extends StationMode {

	public StationComunication(Station s) {
		super(s);
	}

	@Override
	public Route findNewRoute(Station destiny) {
		return station.findBestRoute(destiny);
	}

	@Override
	public void receivePackage(PackBox b, Route r) {
		// nada
	}

	@Override
	public void sendPackage(PackBox pack, Vehicle vehicle) {
		// nada
	}
}