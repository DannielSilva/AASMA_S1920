package packagedelivery;

public class StationSingleReceiveMemory extends StationMode {
	// so ve os intermedios a partir da origem

	@Override
	public Route findNewRoute(Station destiny) {
		// ver a memoria
		// se falhar ve random
		return station.getRouteFromMemory(destiny);
	}

	@Override
	public void receivePackage(PackBox b, Route r) {
		Station source = b.getSource();
		station.addStationRoute(source, r, b.getCost());
	}

	@Override
	public void sendPackage(PackBox pack, Vehicle vehicle) {
		pack.addTransportationCost(vehicle.getCost());

	}

}
