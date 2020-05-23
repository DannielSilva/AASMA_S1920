package packagedelivery;

import java.util.List;

public class StationMultiReceiveMemory extends StationMode {
	// ve os intermedios a partir de onde a package andou kakaka

	public StationMultiReceiveMemory(Station s) {
		super(s);
	}

	@Override
	public Route findNewRoute(Station destiny) {
		// ver a memoria
		// se falhar ve random
		return station.getRouteFromMemory(destiny);
	}

	@Override
	public void receivePackage(PackBox b, Route r) {
		List<Station> path = b.getPath();

		for (int i = 0; i < path.size(); i++) {
			Station s = path.get(i);
			int cost = (b.getCost() * (i + 1)) / path.size();
			station.addStationRoute(s, r, cost);
		}
	}

	@Override
	public void sendPackage(PackBox pack, Vehicle vehicle) {
		pack.addTransportationCost(vehicle.getCost());
	}

}
