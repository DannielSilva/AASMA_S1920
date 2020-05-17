package packagedelivery;

import java.util.*;

public class StationReactive extends StationBehaviour {

	@Override
	public void agentDecision() {
		SortedSet<PreProcessedPackBox> orderedPackages = station.chooseBox();

		for (PreProcessedPackBox prep : orderedPackages) {
			PackBox pack = prep.getPack();

			Station destiny = station.readPackageDestiny(pack);
			// somos a estacao?
			if (destiny.getStationId() == station.getStationId()) {
				Station source = pack.getSource();
				source.increaseDelivered();
				source.increasePointsBy(pack.getReward());
			}

			Route route = prep.getRoute();
			if (route != null) {

				Vehicle vehicle = prep.getVehicle();
				if (vehicle != null) {
					station.send(destiny, vehicle, pack);
					break;
				}

			} else {
				// experimenta um vizinho
				Route guess = station.randomRoute();
				Vehicle vehicle = station.findVehicle(guess);
				if (vehicle != null) {
					station.send(guess.getOther(station), vehicle, pack);
				}
				break;
			}

		}
	}

}