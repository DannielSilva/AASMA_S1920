package packagedelivery;

public class StationMultiReceiveMemory extends StationMode {
	// ve os intermedios a partir de onde a package andou kakaka

	@Override
	public Route findNewRoute() {
		// ver a memoria
		// se falhar ve random
		return station.randomRoute();
	}

	@Override
	public void receivePackage(PackBox b, Route r) {
		// nada
	}
}
