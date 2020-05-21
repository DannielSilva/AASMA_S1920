package packagedelivery;

public class StationSingleReceiveMemoryHeuristic extends StationMode {
	// so ve os intermedios a partir da origem

	@Override
	public Route findNewRoute(Station destiny) {
		// ver a memoria
		// mandar para alguem do continente do destino
		// se falhar ve random
		return station.randomRoute();
	}

	@Override
	public void receivePackage(PackBox b, Route r) {
		// nada
	}
}
