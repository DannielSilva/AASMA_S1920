package packagedelivery;

public class StationNaive extends StationMode {

	public StationNaive(Station s) {
		super(s);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Route findNewRoute(Station destiny) {
		return station.randomRoute();
	}

	@Override
	public void receivePackage(PackBox b, Route r) {
		// nada
	}

	@Override
	public void sendPackage(PackBox pack, Vehicle vehicle) {
		pack.addToPath(station);
	}
}