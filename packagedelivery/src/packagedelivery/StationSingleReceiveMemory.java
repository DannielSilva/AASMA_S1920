package packagedelivery;

public class StationSingleReceiveMemory extends StationMode {
	// so ve os intermedios a partir da origem

	@Override
	public Route findNewRoute(Station destiny) {
		// ver a memoria
		Route r = station.findMemoryRoute(destiny);
		// se falhar ve random
		return r != null ? r : station.randomRoute();
	}

	@Override
	public void receivePackage(PackBox b, Route r) {
		Station source = b.getSource();
		station.addStationRoute(source, r);
	}

}
