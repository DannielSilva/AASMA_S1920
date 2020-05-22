package packagedelivery;

import java.util.List;

public class StationSingleReceiveMemory extends StationMode {
	// so ve os intermedios a partir da origem

	@Override
	public Route findNewRoute(Station destiny) {
		// ver a memoria
		Route r = station.getRouteFromMemory(destiny);
		// se falhar ve random
		return r;
	}

	@Override
	public void receivePackage(PackBox b, Route r) {
		Station source = b.getSource();
		station.addStationRoute(source, r, b.getCost());
	}

	@Override
	public void sendPackage(PackBox pack, Vehicle vehicle) {
		// TODO Auto-generated method stub
		pack.addTransportationCost(vehicle.getCost());

	}

}
