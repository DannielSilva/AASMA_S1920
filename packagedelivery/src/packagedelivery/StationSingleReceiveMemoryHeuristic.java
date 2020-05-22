package packagedelivery;

public class StationSingleReceiveMemoryHeuristic extends StationMode {
	// so ve os intermedios a partir da origem

	public StationSingleReceiveMemoryHeuristic(Station s) {
		super(s);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Route findNewRoute(Station destiny) {
		// ver a memoria
		// se falhar mandar para alguem do continente do destino
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

	@Override
	public boolean satisfiesHeuristic(Route r, Station destiny) {
		return station.sameContinent(r, destiny);
	}

}
