package packagedelivery;

public class PreProcessedPackBox implements Comparable<PreProcessedPackBox> {
	private PackBox pack;
	private int rate = 100;
	private Route route;
	private Vehicle vehicle;

	public PreProcessedPackBox(PackBox packBox) {
		this.pack = packBox;
	}

	@Override
	public int compareTo(PreProcessedPackBox other) {
		return ((Integer) rate).compareTo((Integer) other.getRate());
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public PackBox getPack() {
		return pack;
	}

	public void setPack(PackBox pack) {
		this.pack = pack;
	}

}